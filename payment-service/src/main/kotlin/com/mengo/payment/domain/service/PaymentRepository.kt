package com.mengo.payment.domain.service

import com.mengo.payment.domain.model.Payment
import com.mengo.payment.domain.model.PendingPayment
import java.util.UUID

interface PaymentRepository {
    fun findById(paymentId: UUID): Payment?

    fun save(payment: PendingPayment): Payment

    fun update(payment: Payment)

    fun deleteById(paymentId: UUID)
}
