package com.mengo.payment.infrastructure.events

import com.mengo.payment.domain.model.PaymentCompletedEvent
import com.mengo.payment.domain.model.PaymentFailedEvent
import com.mengo.payment.domain.model.PaymentInitiatedEvent
import com.mengo.payment.domain.service.PaymentEventPublisher
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_INITIATED
import com.mengo.payment.infrastructure.events.mappers.toAvro
import org.apache.avro.specific.SpecificRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class PaymentKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, SpecificRecord>,
) : PaymentEventPublisher {
    override fun publishPaymentInitiated(payment: PaymentInitiatedEvent) {
        val avroPayment = payment.toAvro()
        kafkaTemplate.send(KAFKA_PAYMENT_INITIATED, avroPayment.bookingId.toString(), avroPayment)
    }

    override fun publishPaymentCompleted(payment: PaymentCompletedEvent) {
        val avroPayment = payment.toAvro()
        kafkaTemplate.send(KAFKA_PAYMENT_COMPLETED, avroPayment.bookingId.toString(), avroPayment)
    }

    override fun publishPaymentFailed(payment: PaymentFailedEvent) {
        val avroPayment = payment.toAvro()
        kafkaTemplate.send(KAFKA_PAYMENT_FAILED, avroPayment.bookingId.toString(), avroPayment)
    }
}
