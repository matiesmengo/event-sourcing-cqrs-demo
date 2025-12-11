package com.mengo.payment.application

import com.mengo.payment.domain.model.command.PaymentCommand
import com.mengo.payment.domain.model.command.SagaCommand
import com.mengo.payment.domain.model.events.PaymentAggregate
import com.mengo.payment.domain.service.PaymentEventPublisher
import com.mengo.payment.domain.service.PaymentEventStoreRepository
import com.mengo.payment.domain.service.PaymentProcessor
import com.mengo.payment.domain.service.PaymentProcessorResult
import com.mengo.payment.domain.service.PaymentService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
open class PaymentServiceCommand(
    private val processor: PaymentProcessor,
    private val eventStoreRepository: PaymentEventStoreRepository,
    private val eventPublisher: PaymentEventPublisher,
) : PaymentService {
    @Transactional(propagation = Propagation.REQUIRED)
    override fun onRequestPayment(command: PaymentCommand.BookingPayment) {
        val newPayment =
            PaymentAggregate.createPaymentEvent(
                bookingId = command.bookingId,
                totalPrice = command.totalPrice,
            )

        eventStoreRepository.append(newPayment)
        eventPublisher.publishPaymentInitiated(
            PaymentCommand.PaymentInitiated(
                paymentId = newPayment.paymentId,
                bookingId = command.bookingId,
                totalPrice = command.totalPrice,
            ),
        )
    }

    @Transactional(propagation = Propagation.REQUIRED)
    override fun onPaymentInitiated(command: PaymentCommand.PaymentInitiated) {
        val aggregate =
            eventStoreRepository.load(command.paymentId)
                ?: error("onPaymentInitiated not found for paymentId ${command.paymentId} ")

        when (val result = processor.executePayment(command.paymentId)) {
            is PaymentProcessorResult.Success -> {
                val completedEvent = aggregate.confirmPayment(result.reference)

                eventStoreRepository.append(completedEvent)
                eventPublisher.publishPaymentCompleted(
                    SagaCommand.PaymentCompleted(
                        paymentId = completedEvent.paymentId,
                        bookingId = completedEvent.bookingId,
                        reference = completedEvent.reference,
                    ),
                )
            }

            is PaymentProcessorResult.Failure -> {
                val failedEvent = aggregate.failPayment(result.reason)

                eventStoreRepository.append(failedEvent)
                eventPublisher.publishPaymentFailed(
                    SagaCommand.PaymentFailed(
                        paymentId = failedEvent.paymentId,
                        bookingId = failedEvent.bookingId,
                        reason = failedEvent.reason,
                    ),
                )
            }
        }
    }
}
