package com.mengo.architecture.inbox

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "processed_events",
    uniqueConstraints = [UniqueConstraint(columnNames = ["causation_id"])],
)
data class InboxEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "causation_id")
    val causationId: UUID,
    @Column(name = "aggregate_id", nullable = false)
    val aggregateId: UUID,
    @Column(name = "processed_at", nullable = false)
    val processedAt: Instant = Instant.now(),
) {
    constructor() : this(
        id = 0,
        causationId = UUID.randomUUID(),
        aggregateId = UUID.randomUUID(),
        processedAt = Instant.now(),
    )
}
