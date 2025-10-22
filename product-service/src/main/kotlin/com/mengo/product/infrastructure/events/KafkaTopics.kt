package com.mengo.product.infrastructure.events

object KafkaTopics {
    const val KAFKA_SAGA_REQUEST_STOCK = "saga.request_stock"
    const val KAFKA_SAGA_RELEASE_STOCK = "saga.release_stock"

    const val KAFKA_PRODUCT_RESERVED = "product.reserved"
    const val KAFKA_PRODUCT_RESERVATION_FAILED = "product.reservation_failed"
}
