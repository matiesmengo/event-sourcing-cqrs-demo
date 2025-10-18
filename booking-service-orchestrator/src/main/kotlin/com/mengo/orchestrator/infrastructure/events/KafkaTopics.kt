package com.mengo.orchestrator.infrastructure.events

object KafkaTopics {
    const val KAFKA_BOOKING_CREATED = "booking.created"
    const val KAFKA_SAGA_REQUEST_STOCK = "saga.request_stock"
    const val KAFKA_PRODUCT_RESERVED = "product.reserved"
    const val KAFKA_PRODUCT_RESERVATION_FAILED = "product.reservation_failed"
    const val KAFKA_SAGA_REQUEST_PAYMENT = "saga.request_payment"
    const val KAFKA_PAYMENT_COMPLETED = "payment.completed"
    const val KAFKA_PAYMENT_FAILED = "payment.failed"
    const val KAFKA_SAGA_CONFIRM_BOOKING = "saga.confirm_booking"
    const val KAFKA_SAGA_CANCEL_BOOKING = "saga.cancel_booking"
    const val KAFKA_SAGA_RELEASE_STOCK = "saga.release_stock"
}
