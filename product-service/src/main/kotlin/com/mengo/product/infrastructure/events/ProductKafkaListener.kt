package com.mengo.product.infrastructure.events

import com.mengo.orchestrator.payload.OrchestratorRequestStockPayload
import com.mengo.product.domain.service.ProductService
import com.mengo.product.infrastructure.events.KafkaTopics.KAFKA_SAGA_REQUEST_STOCK
import com.mengo.product.infrastructure.events.mappers.toDomain
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ProductKafkaListener(
    private val productService: ProductService,
) {
    @KafkaListener(topics = [KAFKA_SAGA_REQUEST_STOCK], groupId = "product-service-group")
    fun consumeBookingCreatedEvent(payload: OrchestratorRequestStockPayload) {
        val bookingDomain = payload.toDomain()
        productService.onBookingCreated(bookingDomain)
    }
}
