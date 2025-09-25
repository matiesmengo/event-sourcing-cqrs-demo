package com.mengo.payment.infrastructure.events

import com.mengo.payment.domain.model.CompletedPayment
import com.mengo.payment.domain.model.FailedPayment
import com.mengo.payment.domain.service.PaymentEventPublisher
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.payment.infrastructure.events.mappers.toAvro
import org.apache.avro.specific.SpecificRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class PaymentKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, SpecificRecord>,
) : PaymentEventPublisher {
    override fun publishPaymentCompleted(payment: CompletedPayment) {
        val avroPayment = payment.toAvro()
        kafkaTemplate.send(KAFKA_PAYMENT_COMPLETED, avroPayment.paymentId.toString(), avroPayment)
    }

    override fun publishPaymentFailed(payment: FailedPayment) {
        val avroPayment = payment.toAvro()
        kafkaTemplate.send(KAFKA_PAYMENT_FAILED, avroPayment.paymentId.toString(), avroPayment)
    }
}
