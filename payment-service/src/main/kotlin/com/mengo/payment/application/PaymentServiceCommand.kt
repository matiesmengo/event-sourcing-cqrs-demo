package com.mengo.payment.application

import com.mengo.payment.domain.model.BookingPayment
import com.mengo.payment.domain.model.PaymentCompletedEvent
import com.mengo.payment.domain.model.PaymentFailedEvent
import com.mengo.payment.domain.model.PaymentInitiatedEvent
import com.mengo.payment.domain.service.PaymentEventPublisher
import com.mengo.payment.domain.service.PaymentEventStoreRepository
import com.mengo.payment.domain.service.PaymentProcessor
import com.mengo.payment.domain.service.PaymentProcessorResult
import com.mengo.payment.domain.service.PaymentService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class PaymentServiceCommand(
    private val processor: PaymentProcessor,
    private val eventStoreRepository: PaymentEventStoreRepository,
    private val eventPublisher: PaymentEventPublisher,
) : PaymentService {
    // TODO: command class

    @Transactional
    override fun onRequestPayment(bookingPayment: BookingPayment) {
        val createdEvent =
            PaymentInitiatedEvent(
                bookingId = bookingPayment.bookingId,
                totalPrice = bookingPayment.totalPrice,
                aggregateVersion = 1,
            )
        eventStoreRepository.save(createdEvent)
        eventPublisher.publishPaymentInitiated(createdEvent)

        when (val result = processor.executePayment(createdEvent)) {
            is PaymentProcessorResult.Success -> {
                val completedEvent =
                    PaymentCompletedEvent(
                        paymentId = createdEvent.paymentId,
                        bookingId = bookingPayment.bookingId,
                        reference = result.reference,
                        aggregateVersion = 2,
                    )
                eventStoreRepository.save(completedEvent)
                eventPublisher.publishPaymentCompleted(completedEvent)
            }

            is PaymentProcessorResult.Failure -> {
                val failedEvent =
                    PaymentFailedEvent(
                        paymentId = createdEvent.paymentId,
                        bookingId = bookingPayment.bookingId,
                        reason = result.reason,
                        aggregateVersion = 2,
                    )
                eventStoreRepository.save(failedEvent)
                eventPublisher.publishPaymentFailed(failedEvent)
            }
        }
    }
}
