package com.mengo.payment.infrastructure.persist

import com.mengo.payment.domain.model.PaymentStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "payments")
class PaymentEntity(
    @Id
    @Column(name = "payment_id", nullable = false, updatable = false)
    val paymentId: UUID,
    @Column(name = "booking_id", nullable = false)
    val bookingId: UUID,
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    val paymentStatus: PaymentStatus,
    @Column(name = "reference", nullable = true)
    val reference: String?,
    @Column(name = "reason", nullable = true)
    val reason: String?,
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,
) {
    private constructor() : this(
        paymentId = UUID.randomUUID(),
        bookingId = UUID.randomUUID(),
        paymentStatus = PaymentStatus.PENDING,
        reference = null,
        reason = null,
        createdAt = Instant.now(),
    )
}
