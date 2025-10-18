package com.mengo.booking.infrastructure.events

object KafkaTopics {
    const val KAFKA_SAGA_CONFIRM_BOOKING = "saga.confirm_booking"
    const val KAFKA_SAGA_CANCEL_BOOKING = "saga.cancel_booking"

    const val KAFKA_BOOKING_COMPLETED = "booking.completed"
    const val KAFKA_BOOKING_FAILED = "booking.failed"
    const val KAFKA_BOOKING_CREATED = "booking.created"
}
