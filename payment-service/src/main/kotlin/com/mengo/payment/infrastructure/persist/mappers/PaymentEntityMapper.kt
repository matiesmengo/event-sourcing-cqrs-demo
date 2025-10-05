package com.mengo.payment.infrastructure.persist.mappers

import com.mengo.payment.domain.model.CompletedPayment
import com.mengo.payment.domain.model.FailedPayment
import com.mengo.payment.domain.model.Payment
import com.mengo.payment.domain.model.PaymentStatus
import com.mengo.payment.domain.model.PendingPayment
import com.mengo.payment.infrastructure.persist.PaymentEntity

fun Payment.toEntity(): PaymentEntity =
    when (this) {
        is PendingPayment -> this.toEntity()
        is CompletedPayment -> this.toEntity()
        is FailedPayment -> this.toEntity()
    }

fun PendingPayment.toEntity(): PaymentEntity =
    PaymentEntity(
        paymentId = this.paymentId,
        bookingId = this.bookingId,
        paymentStatus = PaymentStatus.PENDING,
        reference = null,
        reason = null,
        createdAt = this.createdAt,
    )

fun CompletedPayment.toEntity(): PaymentEntity =
    PaymentEntity(
        paymentId = this.paymentId,
        bookingId = this.bookingId,
        paymentStatus = PaymentStatus.COMPLETED,
        reference = this.reference,
        reason = null,
        createdAt = this.createdAt,
    )

fun FailedPayment.toEntity(): PaymentEntity =
    PaymentEntity(
        paymentId = this.paymentId,
        bookingId = this.bookingId,
        paymentStatus = PaymentStatus.FAILED,
        reference = null,
        reason = this.reason,
        createdAt = this.createdAt,
    )

fun PaymentEntity.toDomain(): Payment =
    when (this.paymentStatus) {
        PaymentStatus.PENDING ->
            PendingPayment(
                paymentId = this.paymentId,
                bookingId = this.bookingId,
                createdAt = this.createdAt,
            )

        PaymentStatus.COMPLETED ->
            CompletedPayment(
                paymentId = this.paymentId,
                bookingId = this.bookingId,
                reference = this.reference ?: throw IllegalArgumentException("CompletedPayment: ${this::paymentId} has not reference"),
                createdAt = this.createdAt,
            )

        PaymentStatus.FAILED ->
            FailedPayment(
                paymentId = this.paymentId,
                bookingId = this.bookingId,
                reason = this.reason ?: throw IllegalArgumentException("FailedPayment: ${this::paymentId} has not reason"),
                createdAt = this.createdAt,
            )
    }
