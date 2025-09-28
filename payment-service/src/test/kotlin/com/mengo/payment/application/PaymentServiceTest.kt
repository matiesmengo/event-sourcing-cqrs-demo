package com.mengo.payment.application

import com.mengo.payment.domain.model.BookingPayment
import com.mengo.payment.domain.model.CompletedPayment
import com.mengo.payment.domain.model.FailedPayment
import com.mengo.payment.domain.model.PendingPayment
import com.mengo.payment.domain.service.PaymentEventPublisher
import com.mengo.payment.domain.service.PaymentProcessor
import com.mengo.payment.domain.service.PaymentProcessorResult
import com.mengo.payment.domain.service.PaymentRepository
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

class PaymentServiceTest {
    private val repository: PaymentRepository = mock()
    private val processor: PaymentProcessor = mock()
    private val eventPublisher: PaymentEventPublisher = mock()

    private val service = PaymentService(repository, processor, eventPublisher)

    private val bookingId = UUID.randomUUID()
    private val bookingPayment = BookingPayment(bookingId)

    @Test
    fun `should process new pending payment successfully`() {
        whenever(repository.findById(bookingId)).thenReturn(null)
        whenever(repository.save(any())).thenAnswer { it.arguments[0] }
        whenever(processor.executePayment(any())).thenReturn(PaymentProcessorResult.Success("ref-123"))

        service.onBookingCreated(bookingPayment)

        verify(repository, times(2)).save(any())
        verify(eventPublisher).publishPaymentCompleted(
            check {
                assertEquals("ref-123", it.reference)
            },
        )
    }

    @Test
    fun `should process new pending payment with failure`() {
        whenever(repository.findById(bookingId)).thenReturn(null)
        whenever(repository.save(any())).thenAnswer { it.arguments[0] }
        whenever(processor.executePayment(any())).thenReturn(PaymentProcessorResult.Failure("network error"))

        service.onBookingCreated(bookingPayment)

        verify(repository, times(2)).save(any()) // initial + failed
        verify(eventPublisher).publishPaymentFailed(
            check {
                assertEquals("network error", it.reason)
            },
        )
    }

    @Test
    fun `should continue processing if existing payment is PENDING`() {
        val existing = PendingPayment(paymentId = UUID.randomUUID(), bookingId = bookingId)
        whenever(repository.findById(bookingId)).thenReturn(existing)
        whenever(repository.save(any())).thenAnswer { it.arguments[0] }
        whenever(processor.executePayment(any())).thenReturn(PaymentProcessorResult.Success("ref-456"))

        service.onBookingCreated(bookingPayment)

        verify(repository, times(2)).save(any())
        verify(eventPublisher).publishPaymentCompleted(
            check {
                assertEquals("ref-456", it.reference)
            },
        )
    }

    @Test
    fun `should throw if existing payment is FAILED`() {
        val existing = FailedPayment(paymentId = UUID.randomUUID(), bookingId = bookingId, reason = "previous failure")
        whenever(repository.findById(bookingId)).thenReturn(existing)

        assertThrows<IllegalStateException> {
            service.onBookingCreated(bookingPayment)
        }

        verifyNoInteractions(processor, eventPublisher)
    }

    @Test
    fun `should throw if existing payment is COMPLETED`() {
        val existing = CompletedPayment(paymentId = UUID.randomUUID(), bookingId = bookingId, reference = "ref-999")
        whenever(repository.findById(bookingId)).thenReturn(existing)

        assertThrows<IllegalStateException> {
            service.onBookingCreated(bookingPayment)
        }

        verifyNoInteractions(processor, eventPublisher)
    }
}
