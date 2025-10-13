package com.mengo.payment.infrastructure.events

import com.mengo.payment.domain.service.PaymentService
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_PRODUCT_RESERVED
import com.mengo.payment.infrastructure.events.mappers.toDomain
import com.mengo.product.payload.ProductReservedPayload
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class ProductKafkaListener(
    private val paymentService: PaymentService,
) {
    // TODO: Implement SAGA and allow multiples products
    // TODO: Add unit test and integration test

    @KafkaListener(topics = [KAFKA_PRODUCT_RESERVED], groupId = "payment-service-group")
    fun onBookingFullyReserved(event: ProductReservedPayload) {
        paymentService.onBookingReserved(event.toDomain())
    }
}
