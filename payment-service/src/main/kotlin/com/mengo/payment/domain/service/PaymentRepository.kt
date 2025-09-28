package com.mengo.payment.domain.service

import com.mengo.payment.domain.model.Payment
import java.util.UUID

interface PaymentRepository {
    fun save(payment: Payment): Payment

    fun findById(paymentId: UUID): Payment?

    fun deleteById(paymentId: UUID)
}
