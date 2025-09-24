package com.mengo.payment.infrastructure.events

object KafkaTopics {
    const val KAFKA_PAYMENT_COMPLETED = "payment.completed"
    const val KAFKA_PAYMENT_FAILED = "payment.failed"

    const val KAFKA_BOOKING_CREATED = "booking.created"
}
