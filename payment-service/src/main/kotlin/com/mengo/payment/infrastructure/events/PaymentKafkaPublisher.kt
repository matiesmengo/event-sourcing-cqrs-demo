package com.mengo.payment.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_INITIATED
import com.mengo.architecture.outbox.OutboxRepository
import com.mengo.payment.domain.model.PaymentCompletedEvent
import com.mengo.payment.domain.model.PaymentFailedEvent
import com.mengo.payment.domain.model.PaymentInitiatedEvent
import com.mengo.payment.domain.service.PaymentEventPublisher
import com.mengo.payment.infrastructure.events.mappers.toAvro
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
open class PaymentKafkaPublisher(
    private val outboxRepository: OutboxRepository,
) : PaymentEventPublisher {
    @Transactional(propagation = Propagation.REQUIRED)
    override fun publishPaymentInitiated(payment: PaymentInitiatedEvent) {
        val avroPayment = payment.toAvro()

        outboxRepository.persistOutboxEvent(
            topic = KAFKA_PAYMENT_INITIATED,
            key = avroPayment.bookingId.toString(),
            payloadJson = avroPayment,
        )
    }

    @Transactional(propagation = Propagation.REQUIRED)
    override fun publishPaymentCompleted(payment: PaymentCompletedEvent) {
        val avroPayment = payment.toAvro()

        outboxRepository.persistOutboxEvent(
            topic = KAFKA_PAYMENT_COMPLETED,
            key = avroPayment.bookingId.toString(),
            payloadJson = avroPayment,
        )
    }

    @Transactional(propagation = Propagation.REQUIRED)
    override fun publishPaymentFailed(payment: PaymentFailedEvent) {
        val avroPayment = payment.toAvro()

        outboxRepository.persistOutboxEvent(
            topic = KAFKA_PAYMENT_FAILED,
            key = avroPayment.bookingId.toString(),
            payloadJson = avroPayment,
        )
    }
}
