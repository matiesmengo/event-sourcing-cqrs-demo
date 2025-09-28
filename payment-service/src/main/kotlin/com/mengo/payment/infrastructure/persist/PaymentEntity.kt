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
abstract class PaymentEntity protected constructor() { // Hibernate
    @Id
    @Column(name = "payment_id", nullable = false, updatable = false)
    lateinit var paymentId: UUID

    @Column(name = "booking_id", nullable = false)
    lateinit var bookingId: UUID

    @Column(name = "created_at", nullable = false)
    lateinit var createdAt: Instant
}

@Entity
@DiscriminatorValue("PENDING")
class PendingPaymentEntity private constructor() : PaymentEntity() { // Hibernate
    companion object {
        fun create(paymentId: UUID, bookingId: UUID, createdAt: Instant): PendingPaymentEntity {
            val p = PendingPaymentEntity()
            p.paymentId = paymentId
            p.bookingId = bookingId
            p.createdAt = createdAt
            return p
        }
    }
}

@Entity
@DiscriminatorValue("COMPLETED")
class CompletedPaymentEntity private constructor() : PaymentEntity() { // Hibernate
    @Column(name = "reference")
    lateinit var reference: String

    companion object {
        fun create(paymentId: UUID, bookingId: UUID, reference: String, createdAt: Instant): CompletedPaymentEntity {
            val p = CompletedPaymentEntity()
            p.paymentId = paymentId
            p.bookingId = bookingId
            p.reference = reference
            p.createdAt = createdAt
            return p
        }
    }
}

@Entity
@DiscriminatorValue("FAILED")
class FailedPaymentEntity private constructor() : PaymentEntity() { // Hibernate
    @Column(name = "reason")
    lateinit var reason: String

    companion object {
        fun create(paymentId: UUID, bookingId: UUID, reason: String,  createdAt: Instant): FailedPaymentEntity {
            val p = FailedPaymentEntity()
            p.paymentId = paymentId
            p.bookingId = bookingId
            p.reason = reason
            p.createdAt = createdAt
            return p
        }
    }
}