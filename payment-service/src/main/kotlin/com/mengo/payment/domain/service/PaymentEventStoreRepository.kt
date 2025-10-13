package com.mengo.payment.domain.service

import com.mengo.payment.domain.model.PaymentEvent
import java.util.UUID

interface PaymentEventStoreRepository {
    fun findById(paymentId: UUID): PaymentEvent?

    fun save(paymentEvent: PaymentEvent)
}
