package com.mengo.orchestrator.application

import com.mengo.orchestrator.domain.model.BookingCreated
import com.mengo.orchestrator.domain.model.PaymentCompleted
import com.mengo.orchestrator.domain.model.PaymentFailed
import com.mengo.orchestrator.domain.model.Product
import com.mengo.orchestrator.domain.model.ProductReservationFailed
import com.mengo.orchestrator.domain.model.ProductReserved
import com.mengo.orchestrator.domain.model.events.OrchestratorEvent
import com.mengo.orchestrator.domain.model.events.OrchestratorEvent.WaitingStock
import com.mengo.orchestrator.domain.model.events.SagaCommand
import com.mengo.orchestrator.domain.service.OrchestratorEventPublisher
import com.mengo.orchestrator.domain.service.OrchestratorEventStoreRepository
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrchestratorServiceCommandTest {
    private val eventStoreRepository: OrchestratorEventStoreRepository = mock()
    private val eventPublisher: OrchestratorEventPublisher = mock()

    private val service = OrchestratorServiceCommand(eventStoreRepository, eventPublisher)

    private val bookingId = UUID.randomUUID()
    private val paymentId = UUID.randomUUID()
    private val product1 = Product(UUID.randomUUID(), 2, BigDecimal("10.50"))
    private val product2 = Product(UUID.randomUUID(), 3, BigDecimal("33.22"))

    // TODO: Test assertThrows<IllegalStateException>
    @Test
    fun `handleBookingCreated publishes stock requests`() {
        // given
        val booking = BookingCreated(bookingId, setOf(product1))

        // when
        service.handleBookingCreated(booking)

        // then
        verify(eventStoreRepository).save(
            argThat { e ->
                val ev = e as WaitingStock
                assertEquals(bookingId, bookingId)
                assertContains(ev.expectedProducts, product1)
                assertTrue(ev.reservedProducts.isEmpty())
                true
            },
        )
        verify(eventPublisher).publishRequestStock(
            SagaCommand.RequestStock(bookingId, product1.productId, product1.quantity),
        )
    }

    @Test
    fun `handleProductReserved should publish payment request when updated is WaitingPayment`() {
        // given
        val current =
            WaitingStock(
                bookingId = bookingId,
                expectedProducts = setOf(product1, product2),
                reservedProducts = mutableSetOf(product1),
            )
        whenever(eventStoreRepository.findByBookingId(bookingId)).thenReturn(current)
        val reserved = ProductReserved(bookingId, product2.productId, product2.quantity, product2.price!!)

        // when
        service.handleProductReserved(reserved)

        // then
        verify(eventStoreRepository).save(
            argThat { e ->
                val ev = e as OrchestratorEvent.WaitingPayment
                assertEquals(bookingId, ev.bookingId)
                assertContains(ev.reservedProducts, product2)
                true
            },
        )
        verify(eventPublisher).publishRequestPayment(
            argThat { cmd ->
                assertEquals(bookingId, cmd.bookingId)
                assertTrue(cmd.totalPrice.compareTo(product1.price?.add(product2.price)) == 0)
                true
            },
        )
    }

    @Test
    fun `handleProductReserved should NOT publish payment request when updated is not WaitingPayment`() {
        // given
        val current =
            WaitingStock(
                bookingId = bookingId,
                expectedProducts = setOf(product1, product2),
                reservedProducts = mutableSetOf(),
            )
        whenever(eventStoreRepository.findByBookingId(bookingId)).thenReturn(current)
        val reserved = ProductReserved(bookingId, product1.productId, product1.quantity, product1.price!!)

        // when
        service.handleProductReserved(reserved)

        // then
        verify(eventStoreRepository).save(
            argThat { e ->
                val ev = e as WaitingStock
                assertEquals(bookingId, ev.bookingId)
                assertContains(ev.reservedProducts, product1)
                true
            },
        )

        verify(eventPublisher, never()).publishRequestPayment(any())
    }

    @Test
    fun `handleProductReservationFailed should create Compensating event and publish release and cancel commands`() {
        // given
        val current =
            WaitingStock(
                bookingId = bookingId,
                expectedProducts = setOf(product1, product2),
                reservedProducts = mutableSetOf(),
            )
        whenever(eventStoreRepository.findByBookingId(bookingId)).thenReturn(current)

        val domain = ProductReservationFailed(bookingId, product1.productId)

        // when
        service.handleProductReservationFailed(domain)

        // then
        verify(eventStoreRepository).save(
            argThat { ev ->
                assertTrue(ev is OrchestratorEvent.Compensating)
                assertEquals(bookingId, ev.bookingId)
                assertEquals(current.expectedProducts, ev.expectedProducts)
                true
            },
        )
        verify(eventPublisher, times(2)).publishReleaseStock(
            argThat { ev ->
                assertEquals(bookingId, ev.bookingId)
                assertTrue(ev.productId in setOf(product1.productId, product2.productId))
                assertTrue(ev.quantity in setOf(product1.quantity, product2.quantity))
                true
            },
        )
        verify(eventPublisher).publishCancelBooking(
            argThat { ev ->
                assertEquals(bookingId, ev.bookingId)
                true
            },
        )
    }

    @Test
    fun `handlePaymentCompleted should complete payment and publish confirm booking`() {
        // given
        val current =
            OrchestratorEvent.WaitingPayment(
                bookingId = bookingId,
                expectedProducts = setOf(product1, product2),
                reservedProducts = setOf(product1, product2),
            )
        whenever(eventStoreRepository.findByBookingId(bookingId)).thenReturn(current)

        val domain = PaymentCompleted(bookingId = bookingId, paymentId = paymentId, reference = "ref-1234")

        // when
        service.handlePaymentCompleted(domain)

        // then
        verify(eventStoreRepository).save(
            argThat { ev ->
                assertTrue(ev is OrchestratorEvent.Completed)
                assertEquals(bookingId, ev.bookingId)
                true
            },
        )

        verify(eventPublisher).publishConfirmBooking(
            argThat { ev ->
                assertEquals(bookingId, ev.bookingId)
                true
            },
        )
    }

    @Test
    fun `should transition to compensating and publish release and cancel`() {
        // given
        val current =
            OrchestratorEvent.WaitingPayment(
                bookingId = bookingId,
                expectedProducts = setOf(product1, product2),
                reservedProducts = setOf(product1, product2),
            )
        whenever(eventStoreRepository.findByBookingId(bookingId)).thenReturn(current)

        val domain = PaymentFailed(bookingId = bookingId, paymentId = paymentId, reason = "error")

        // when
        service.handlePaymentFailed(domain)

        // then
        verify(eventStoreRepository).save(
            argThat { ev ->
                assertTrue(ev is OrchestratorEvent.Compensating)
                assertEquals(bookingId, ev.bookingId)
                assertEquals(current.expectedProducts, ev.expectedProducts)
                true
            },
        )

        verify(eventPublisher, times(2)).publishReleaseStock(
            argThat { cmd ->
                assertEquals(bookingId, cmd.bookingId)
                assertTrue(cmd.productId in setOf(product1.productId, product2.productId))
                true
            },
        )

        verify(eventPublisher).publishCancelBooking(
            argThat { cmd ->
                assertEquals(bookingId, cmd.bookingId)
                true
            },
        )
    }
}
