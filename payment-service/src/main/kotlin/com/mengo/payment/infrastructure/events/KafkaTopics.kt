package com.mengo.payment.infrastructure.events

object KafkaTopics {
    const val KAFKA_SAGA_REQUEST_PAYMENT = "saga.request_payment"

    const val KAFKA_PAYMENT_INITIATED = "payment.initiated"
    const val KAFKA_PAYMENT_COMPLETED = "payment.completed"
    const val KAFKA_PAYMENT_FAILED = "payment.failed"
}
