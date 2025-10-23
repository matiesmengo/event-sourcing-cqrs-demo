package com.mengo.product.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_RELEASE_STOCK
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_REQUEST_STOCK
import com.mengo.payload.orchestrator.OrchestratorReleaseStockPayload
import com.mengo.payload.orchestrator.OrchestratorRequestStockPayload
import com.mengo.product.domain.service.ProductService
import com.mengo.product.infrastructure.events.mappers.toDomain
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ProductKafkaListener(
    private val productService: ProductService,
) {
    @KafkaListener(topics = [KAFKA_SAGA_REQUEST_STOCK], groupId = "product-service-group")
    fun onReserveProduct(payload: OrchestratorRequestStockPayload) {
        val bookingDomain = payload.toDomain()
        productService.onReserveProduct(bookingDomain)
    }

    @KafkaListener(topics = [KAFKA_SAGA_RELEASE_STOCK], groupId = "product-service-group")
    fun onReleaseProduct(payload: OrchestratorReleaseStockPayload) {
        val bookingDomain = payload.toDomain()
        productService.onReleaseProduct(bookingDomain)
    }
}
