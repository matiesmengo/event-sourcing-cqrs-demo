package com.mengo.payment.infrastructure.persist

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "payments")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "payment_type", discriminatorType = DiscriminatorType.STRING)
abstract class PaymentEntity(
    @Id
    @Column(name = "payment_id", nullable = false)
    open val paymentId: UUID = UUID.randomUUID(),
    @Column(name = "booking_id", nullable = false)
    open val bookingId: UUID,
    @Column(name = "created_at", nullable = false)
    open val createdAt: Instant = Instant.now(),
)

@Entity
@DiscriminatorValue("PENDING")
data class PendingPaymentEntity(
    override val paymentId: UUID = UUID.randomUUID(),
    override val bookingId: UUID,
    override val createdAt: Instant = Instant.now(),
) : PaymentEntity(paymentId, bookingId, createdAt)

@Entity
@DiscriminatorValue("COMPLETED")
data class CompletedPaymentEntity(
    override val paymentId: UUID,
    override val bookingId: UUID,
    @Column(name = "reference", nullable = false)
    val reference: String,
    override val createdAt: Instant = Instant.now(),
) : PaymentEntity(paymentId, bookingId, createdAt)

@Entity
@DiscriminatorValue("FAILED")
data class FailedPaymentEntity(
    override val paymentId: UUID,
    override val bookingId: UUID,
    @Column(name = "reason", nullable = false)
    val reason: String,
    override val createdAt: Instant = Instant.now(),
) : PaymentEntity(paymentId, bookingId, createdAt)
