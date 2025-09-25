package com.mengo.payment.infrastructure.persist.mappers

import com.mengo.payment.domain.model.CompletedPayment
import com.mengo.payment.domain.model.FailedPayment
import com.mengo.payment.domain.model.Payment
import com.mengo.payment.domain.model.PendingPayment
import com.mengo.payment.infrastructure.persist.CompletedPaymentEntity
import com.mengo.payment.infrastructure.persist.FailedPaymentEntity
import com.mengo.payment.infrastructure.persist.PaymentEntity
import com.mengo.payment.infrastructure.persist.PendingPaymentEntity

fun Payment.toEntity(): PaymentEntity =
    when (this) {
        is PendingPayment ->
            PendingPaymentEntity(
                paymentId = this.paymentId,
                bookingId = this.bookingId,
                createdAt = this.createdAt,
            )
        is CompletedPayment ->
            CompletedPaymentEntity(
                paymentId = this.paymentId,
                bookingId = this.bookingId,
                reference = this.reference,
                createdAt = this.createdAt,
            )
        is FailedPayment ->
            FailedPaymentEntity(
                paymentId = this.paymentId,
                bookingId = this.bookingId,
                reason = this.reason,
                createdAt = this.createdAt,
            )
    }

fun PaymentEntity.toDomain(): Payment =
    when (this) {
        is PendingPaymentEntity ->
            PendingPayment(
                paymentId = this.paymentId,
                bookingId = this.bookingId,
                createdAt = this.createdAt,
            )
        is CompletedPaymentEntity ->
            CompletedPayment(
                paymentId = this.paymentId,
                bookingId = this.bookingId,
                reference = this.reference,
                createdAt = this.createdAt,
            )
        is FailedPaymentEntity ->
            FailedPayment(
                paymentId = this.paymentId,
                bookingId = this.bookingId,
                reason = this.reason,
                createdAt = this.createdAt,
            )

        else -> throw IllegalArgumentException("Unknown PaymentEntity subtype: ${this::class}")
    }
