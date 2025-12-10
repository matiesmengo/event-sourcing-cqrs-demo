package com.mengo.architecture.outbox

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "pending_messages")
data class OutboxEntity(
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID(),
    @Column(name = "topic", nullable = false)
    val topic: String,
    @Column(name = "key")
    val key: String? = null,
    @Column(name = "payload_type", nullable = false)
    val payloadType: String,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    val payload: String,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "headers", columnDefinition = "jsonb")
    val headers: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: OutboxStatus = OutboxStatus.PENDING,
    @Column(name = "retries", nullable = false)
    var retries: Int = 0,
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
    @Column(name = "sent_at")
    var sentAt: Instant? = null,
) {
    constructor() : this(
        topic = "",
        payload = "{}",
        payloadType = "",
    )

    override fun equals(other: Any?) = other is OutboxEntity && id == other.id

    override fun hashCode() = id.hashCode()
}

enum class OutboxStatus {
    PENDING,
    SENT,
    FAILED,
}
