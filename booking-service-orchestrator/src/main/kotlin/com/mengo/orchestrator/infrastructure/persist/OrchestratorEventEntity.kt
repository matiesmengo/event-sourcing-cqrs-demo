package com.mengo.orchestrator.infrastructure.persist

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "orchestrator_events")
data class OrchestratorEventEntity(
    @Id
    @Column(name = "event_id", nullable = false)
    val eventId: UUID = UUID.randomUUID(),
    @Column(name = "aggregate_id", nullable = false)
    val bookingId: UUID,
    @Column(name = "event_type", nullable = false)
    val eventType: String,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_data", nullable = false, columnDefinition = "jsonb")
    val eventData: String,
    @Column(name = "aggregate_version", nullable = false)
    val aggregateVersion: Int,
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
) {
    private constructor() : this(
        eventId = UUID.randomUUID(),
        bookingId = UUID.randomUUID(),
        eventType = "",
        eventData = "{}",
        aggregateVersion = 0,
        createdAt = Instant.now(),
    )
}
