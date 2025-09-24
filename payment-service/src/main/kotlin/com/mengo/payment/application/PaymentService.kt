package com.mengo.payment.application

import com.mengo.payment.domain.model.BookingPayment
import com.mengo.payment.domain.model.CompletedPayment
import com.mengo.payment.domain.model.FailedPayment
import com.mengo.payment.domain.model.Payment
import com.mengo.payment.domain.model.PendingPayment
import com.mengo.payment.domain.service.PaymentEventPublisher
import com.mengo.payment.domain.service.PaymentProcessor
import com.mengo.payment.domain.service.PaymentProcessorResult
import com.mengo.payment.domain.service.PaymentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val repository: PaymentRepository,
    private val processor: PaymentProcessor,
    private val eventPublisher: PaymentEventPublisher,
) {
    @Transactional
    fun onBookingCreated(bookingPayment: BookingPayment) {
        val payment = resolvePayment(bookingPayment)
        processPayment(payment)
    }

    private fun resolvePayment(bookingPayment: BookingPayment): Payment =
        when (val existing = repository.findById(bookingPayment.bookingId)) {
            null -> PendingPayment(bookingId = bookingPayment.bookingId)
            is PendingPayment -> existing
            is FailedPayment -> throw IllegalStateException(
                "Payment for bookingId=${bookingPayment.bookingId} is already FAILED",
            )

            is CompletedPayment -> throw IllegalStateException(
                "Payment for bookingId=${bookingPayment.bookingId} is already COMPLETED",
            )
        }

    private fun processPayment(payment: Payment) {
        val saved = repository.save(payment)

        when (val result = processor.executePayment(saved)) {
            is PaymentProcessorResult.Success -> {
                val completedPayment =
                    CompletedPayment(
                        paymentId = payment.paymentId,
                        bookingId = payment.bookingId,
                        reference = result.reference,
                    )
                repository.save(completedPayment)
                eventPublisher.publishPaymentCompleted(completedPayment)
            }

            is PaymentProcessorResult.Failure -> {
                val failedPayment =
                    FailedPayment(
                        paymentId = payment.paymentId,
                        bookingId = payment.bookingId,
                        reason = result.reason,
                    )
                repository.save(failedPayment)
                eventPublisher.publishPaymentFailed(failedPayment)
            }
        }
    }
}
