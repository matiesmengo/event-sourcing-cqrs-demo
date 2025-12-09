package com.mengo.orchestrator.application

import com.mengo.orchestrator.domain.model.Product
import com.mengo.orchestrator.domain.model.command.OrchestratorCommand
import com.mengo.orchestrator.domain.model.command.SagaCommand
import com.mengo.orchestrator.domain.model.events.OrchestratorAggregate
import com.mengo.orchestrator.domain.model.events.OrchestratorEvent
import com.mengo.orchestrator.domain.service.InboxRepository
import com.mengo.orchestrator.domain.service.OrchestratorEventPublisher
import com.mengo.orchestrator.domain.service.OrchestratorEventStoreRepository
import com.mengo.orchestrator.fixtures.OrchestratorConstants.BOOKING_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.CAUSATION_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PAYMENT_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_PRICE
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_QUANTITY
import org.junit.jupiter.api.Assertions.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.check
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrchestratorServiceCommandTest {
    private val eventStoreRepository: OrchestratorEventStoreRepository = mock()
    private val inboxRepository: InboxRepository = mock()
    private val eventPublisher: OrchestratorEventPublisher = mock()
    private val service = OrchestratorServiceCommand(eventStoreRepository, inboxRepository, eventPublisher)

    val product1 = Product(UUID.randomUUID(), 1, BigDecimal("10.10"))
    val product2 = Product(UUID.randomUUID(), 2, BigDecimal("5.05"))
    val products = setOf(product1, product2)

    @Test
    fun `onBookingCreated should create booking and publish RequestStock events`() {
        val products =
            setOf(
                Product(UUID.randomUUID(), 2, BigDecimal("12.5")),
                Product(UUID.randomUUID(), 1, BigDecimal("8.0")),
            )
        val command = OrchestratorCommand.BookingCreated(BOOKING_ID, products)

        whenever(eventStoreRepository.load(BOOKING_ID)) doReturn null

        service.onBookingCreated(command)

        verify(eventStoreRepository).append(
            check {
                assertTrue(it is OrchestratorEvent.Created)
                assertEquals(BOOKING_ID, it.bookingId)
                assertEquals(products, it.expectedProducts)
            },
        )
        verify(eventPublisher, times(2)).publishRequestStock(any())
    }

    @Test
    fun `onBookingCreated should fail if booking already exists`() {
        val command = OrchestratorCommand.BookingCreated(BOOKING_ID, emptySet())
        val existing =
            OrchestratorAggregate.rehydrate(
                listOf(OrchestratorAggregate.createBookingEvent(BOOKING_ID, emptySet())),
            )
        whenever(eventStoreRepository.load(BOOKING_ID)) doReturn existing

        val ex =
            assertThrows(IllegalStateException::class.java) {
                service.onBookingCreated(command)
            }

        assertTrue(ex.message!!.contains("already exists"))
        verify(eventStoreRepository, never()).append(any())
        verify(eventPublisher, never()).publishRequestStock(any())
    }

    @Test
    fun `onProductReserved should append event and publish payment when state is WAITING_PAYMENT`() {
        val createdEvent = OrchestratorEvent.Created(BOOKING_ID, products, 0)
        val productReserved = OrchestratorEvent.ProductReserved(BOOKING_ID, product1, 1)
        val aggregate = OrchestratorAggregate.rehydrate(listOf(createdEvent, productReserved))

        whenever(eventStoreRepository.load(BOOKING_ID)) doReturn aggregate

        val command =
            OrchestratorCommand.ProductReserved(BOOKING_ID, product2.productId, product2.quantity, product2.price)

        service.onProductReserved(command)

        verify(eventStoreRepository).append(any())
        verify(eventPublisher).publishRequestPayment(
            check<SagaCommand.RequestPayment> {
                assertEquals(BOOKING_ID, it.bookingId)
                assertEquals(BigDecimal("20.20"), it.totalPrice)
            },
        )
    }

    @Test
    fun `onProductReserved should publish release stock when CompensatedProduct`() {
        val createdEvent = OrchestratorEvent.Created(BOOKING_ID, products, 0)
        val productReservedFailed = OrchestratorEvent.ProductReservationFailed(BOOKING_ID, product1.productId, 1)
        val aggregate = OrchestratorAggregate.rehydrate(listOf(createdEvent, productReservedFailed))

        whenever(eventStoreRepository.load(BOOKING_ID)) doReturn aggregate

        val command =
            OrchestratorCommand.ProductReserved(BOOKING_ID, product2.productId, product2.quantity, product2.price)

        service.onProductReserved(command)

        verify(eventStoreRepository).append(any())
        verify(eventPublisher).publishReleaseStock(
            check<SagaCommand.ReleaseStock> {
                assertEquals(BOOKING_ID, it.bookingId)
                assertEquals(product2.productId, it.productId)
                assertEquals(product2.quantity, it.quantity)
            },
        )
    }

    @Test
    fun `onProductReserved should fail if booking not found`() {
        val command = OrchestratorCommand.ProductReserved(BOOKING_ID, PRODUCT_ID, PRODUCT_QUANTITY, PRODUCT_PRICE)
        whenever(eventStoreRepository.load(BOOKING_ID)) doReturn null

        val ex = assertThrows(IllegalStateException::class.java) { service.onProductReserved(command) }

        assertTrue(ex.message!!.contains("not found for booking"))
        verify(eventStoreRepository, never()).append(any())
        verify(eventPublisher, never()).publishRequestPayment(any())
    }

    @Test
    fun `onProductReservationFailed should compensate and cancel booking`() {
        val createdEvent = OrchestratorEvent.Created(BOOKING_ID, products, 0)
        val productReserved = OrchestratorEvent.ProductReserved(BOOKING_ID, product1, 1)
        val aggregate = OrchestratorAggregate.rehydrate(listOf(createdEvent, productReserved))
        whenever(eventStoreRepository.load(BOOKING_ID)) doReturn aggregate

        val command = OrchestratorCommand.ProductReservationFailed(BOOKING_ID, product2.productId)
        service.onProductReservationFailed(command)

        verify(eventStoreRepository, atLeastOnce()).append(any())
        verify(eventPublisher).publishReleaseStock(any())
        verify(eventPublisher).publishCancelBooking(
            check { assertEquals(BOOKING_ID, it.bookingId) },
        )
    }

    @Test
    fun `onPaymentCompleted should append and publish confirm booking`() {
        val createdEvent = OrchestratorEvent.Created(BOOKING_ID, setOf(product1), 0)
        val productReserved = OrchestratorEvent.ProductReserved(BOOKING_ID, product1, 1)
        val aggregate = OrchestratorAggregate.rehydrate(listOf(createdEvent, productReserved))
        whenever(eventStoreRepository.load(BOOKING_ID)) doReturn aggregate

        val command = OrchestratorCommand.PaymentCompleted(BOOKING_ID, PAYMENT_ID, "ref")
        service.onPaymentCompleted(command)

        verify(eventStoreRepository).append(
            check<OrchestratorEvent.PaymentCompleted> {
                assertEquals(BOOKING_ID, it.bookingId)
            },
        )
        verify(eventPublisher).publishConfirmBooking(
            check { assertEquals(BOOKING_ID, it.bookingId) },
        )
    }

    @Test
    fun `onPaymentFailed should release stock, append fail event, and cancel booking`() {
        val product = Product(UUID.randomUUID(), 3, BigDecimal("4.00"))
        val created = OrchestratorAggregate.createBookingEvent(BOOKING_ID, setOf(product))
        val reserved = OrchestratorEvent.ProductReserved(BOOKING_ID, product, 1)
        val aggregate = OrchestratorAggregate.rehydrate(listOf(created, reserved))
        whenever(eventStoreRepository.load(BOOKING_ID)) doReturn aggregate

        val command = OrchestratorCommand.PaymentFailed(BOOKING_ID, PAYMENT_ID, "reason")
        service.onPaymentFailed(command)

        verify(eventPublisher).publishReleaseStock(
            check<SagaCommand.ReleaseStock> {
                assertEquals(product.productId, it.productId)
                assertEquals(product.quantity, it.quantity)
            },
        )
        verify(eventStoreRepository).append(
            check<OrchestratorEvent.PaymentFailed> {
                assertEquals(BOOKING_ID, it.bookingId)
            },
        )
        verify(eventPublisher).publishCancelBooking(
            check { assertEquals(BOOKING_ID, it.bookingId) },
        )
    }
}
