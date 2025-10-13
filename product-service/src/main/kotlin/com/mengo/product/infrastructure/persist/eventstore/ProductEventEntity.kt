package com.mengo.product.infrastructure.persist.eventstore

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "product_events")
data class ProductEventEntity(
    @Id
    @Column(name = "event_id", nullable = false)
    val eventId: UUID = UUID.randomUUID(),
    @Column(name = "aggregate_id", nullable = false)
    val productId: UUID,
    @Column(name = "event_type", nullable = false)
    val eventType: String,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_data", columnDefinition = "jsonb", nullable = false)
    val eventData: String,
    @Column(name = "aggregate_version", nullable = false)
    val aggregateVersion: Int,
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
) {
    private constructor() : this(
        eventId = UUID.randomUUID(),
        productId = UUID.randomUUID(),
        eventType = "",
        eventData = "{}",
        aggregateVersion = 0,
        createdAt = Instant.now(),
    )
}
