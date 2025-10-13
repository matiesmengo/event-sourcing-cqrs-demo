package com.mengo.product.infrastructure.persist.eventstore.mappers

import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.product.domain.model.ProductCreatedEvent
import com.mengo.product.domain.model.ProductEvent
import com.mengo.product.domain.model.ProductReleasedEvent
import com.mengo.product.domain.model.ProductReservedEvent
import com.mengo.product.infrastructure.persist.eventstore.ProductEventEntity
import org.springframework.stereotype.Component

@Component
class ProductEventEntityMapper(
    private val objectMapper: ObjectMapper,
) {
    fun toEntity(event: ProductEvent): ProductEventEntity =
        when (event) {
            is ProductCreatedEvent -> event.toEntity()
            is ProductReservedEvent -> event.toEntity()
            is ProductReleasedEvent -> event.toEntity()
        }

    fun ProductCreatedEvent.toEntity(): ProductEventEntity =
        ProductEventEntity(
            productId = productId,
            eventType = "ProductCreatedEvent",
            eventData = objectMapper.writeValueAsString(this),
            aggregateVersion = aggregateVersion,
            createdAt = createdAt,
        )

    fun ProductReservedEvent.toEntity(): ProductEventEntity =
        ProductEventEntity(
            productId = productId,
            eventType = "ProductReservedEvent",
            eventData = objectMapper.writeValueAsString(this),
            aggregateVersion = aggregateVersion,
            createdAt = createdAt,
        )

    fun ProductReleasedEvent.toEntity(): ProductEventEntity =
        ProductEventEntity(
            productId = productId,
            eventType = "ProductReleasedEvent",
            eventData = objectMapper.writeValueAsString(this),
            aggregateVersion = aggregateVersion,
            createdAt = createdAt,
        )

    fun toDomain(event: ProductEventEntity): ProductEvent =
        when (event.eventType) {
            "ProductCreatedEvent" -> objectMapper.readValue(event.eventData, ProductCreatedEvent::class.java)
            "ProductReservedEvent" -> objectMapper.readValue(event.eventData, ProductReservedEvent::class.java)
            "ProductReleasedEvent" -> objectMapper.readValue(event.eventData, ProductReleasedEvent::class.java)
            else -> throw IllegalArgumentException("Unknown PaymentEvent type: ${event.eventType}")
        }
}
