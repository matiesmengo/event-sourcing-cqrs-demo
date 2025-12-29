package com.mengo.product.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_RELEASE_STOCK
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_REQUEST_STOCK
import com.mengo.architecture.inbox.InboxRepository
import com.mengo.architecture.observability.ObservabilityStep
import com.mengo.payload.orchestrator.OrchestratorReleaseStockPayload
import com.mengo.payload.orchestrator.OrchestratorRequestStockPayload
import com.mengo.product.domain.service.ProductService
import com.mengo.product.infrastructure.events.mappers.toDomain
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
open class ProductKafkaListener(
    private val productService: ProductService,
    private val inboxRepository: InboxRepository,
) {
    @Transactional
    @KafkaListener(topics = [KAFKA_SAGA_REQUEST_STOCK], groupId = "product-service-group", concurrency = "6")
    @ObservabilityStep(name = "product_request_stock")
    open fun onReserveProduct(payload: OrchestratorRequestStockPayload) {
        if (!inboxRepository.validateIdempotencyEvent()) return

        val bookingDomain = payload.toDomain()
        productService.onReserveProduct(bookingDomain)
    }

    @Transactional
    @KafkaListener(topics = [KAFKA_SAGA_RELEASE_STOCK], groupId = "product-service-group", concurrency = "6")
    @ObservabilityStep(name = "product_release_stock")
    open fun onReleaseProduct(payload: OrchestratorReleaseStockPayload) {
        if (!inboxRepository.validateIdempotencyEvent()) return

        val bookingDomain = payload.toDomain()
        productService.onReleaseProduct(bookingDomain)
    }
}
