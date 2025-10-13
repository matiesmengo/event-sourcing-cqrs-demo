package com.mengo.product.infrastructure.events

object KafkaTopics {
    const val KAFKA_PAYMENT_INITIATED = "payment.initiated"
    const val KAFKA_PAYMENT_COMPLETED = "payment.completed"
    const val KAFKA_PAYMENT_FAILED = "payment.failed"

    const val KAFKA_BOOKING_CREATED = "booking.created"
    const val KAFKA_BOOKING_COMPLETED = "booking.completed"
    const val KAFKA_BOOKING_FAILED = "booking.failed"

    const val KAFKA_PRODUCT_RESERVED = "product.reserved"
    const val KAFKA_PRODUCT_RELEASED = "product.released"
}
