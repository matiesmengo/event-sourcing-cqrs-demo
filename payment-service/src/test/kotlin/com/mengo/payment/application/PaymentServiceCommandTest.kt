package com.mengo.payment.application

import com.mengo.payment.domain.model.BookingPayment
import com.mengo.payment.domain.model.PaymentCompletedEvent
import com.mengo.payment.domain.model.PaymentEvent
import com.mengo.payment.domain.model.PaymentFailedEvent
import com.mengo.payment.domain.model.PaymentInitiatedEvent
import com.mengo.payment.domain.service.PaymentEventPublisher
import com.mengo.payment.domain.service.PaymentEventStoreRepository
import com.mengo.payment.domain.service.PaymentProcessor
import com.mengo.payment.domain.service.PaymentProcessorResult
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PaymentServiceCommandTest {
    private val processor: PaymentProcessor = mock()
    private val eventStoreRepository: PaymentEventStoreRepository = mock()
    private val eventPublisher: PaymentEventPublisher = mock()

    private lateinit var service: PaymentServiceCommand

    private val bookingId = UUID.randomUUID()
    private val bookingPayment = BookingPayment(bookingId, BigDecimal("123.45"))

    @BeforeEach
    fun setUp() {
        service = PaymentServiceCommand(processor, eventStoreRepository, eventPublisher)
    }

    @Test
    fun `should process new payment successfully`() {
        // given
        whenever(processor.executePayment(any()))
            .thenReturn(PaymentProcessorResult.Success("ref-123"))

        // when
        service.onRequestPayment(bookingPayment)

        // then
        val captor = argumentCaptor<PaymentEvent>()
        verify(eventStoreRepository, times(2)).save(captor.capture())

        val events = captor.allValues
        val initiated = events[0]
        val completed = events[1]

        // PaymentInitiatedEvent
        assertTrue(initiated is PaymentInitiatedEvent)
        assertEquals(bookingId, initiated.bookingId)
        assertEquals(bookingPayment.totalPrice, initiated.totalPrice)
        assertEquals(1, initiated.aggregateVersion)

        verify(eventPublisher).publishPaymentInitiated(initiated)

        // PaymentCompletedEvent
        assertTrue(completed is PaymentCompletedEvent)
        assertEquals(bookingId, completed.bookingId)
        assertEquals("ref-123", completed.reference)
        assertEquals(2, completed.aggregateVersion)

        verify(eventPublisher).publishPaymentCompleted(completed)
    }

    @Test
    fun `should process new payment with failure`() {
        // given
        whenever(processor.executePayment(any()))
            .thenReturn(PaymentProcessorResult.Failure("insufficient funds"))

        // when
        service.onRequestPayment(bookingPayment)

        // then
        val captor = argumentCaptor<PaymentEvent>()
        verify(eventStoreRepository, times(2)).save(captor.capture())

        val events = captor.allValues
        val initiated = events[0]
        val failed = events[1]

        // PaymentInitiatedEvent
        assertTrue(initiated is PaymentInitiatedEvent)
        assertEquals(bookingId, initiated.bookingId)
        assertEquals(bookingPayment.totalPrice, initiated.totalPrice)
        assertEquals(1, initiated.aggregateVersion)

        verify(eventPublisher).publishPaymentInitiated(initiated)

        // PaymentFailedEvent
        assertTrue(failed is PaymentFailedEvent)
        assertEquals(bookingId, failed.bookingId)
        assertEquals("insufficient funds", failed.reason)
        assertEquals(2, failed.aggregateVersion)

        verify(eventPublisher).publishPaymentFailed(failed)
    }
}
