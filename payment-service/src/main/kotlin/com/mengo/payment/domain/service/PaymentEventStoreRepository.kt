package com.mengo.payment.domain.service

import com.mengo.payment.domain.model.events.PaymentAggregate
import com.mengo.payment.domain.model.events.PaymentEvent
import java.util.UUID

interface PaymentEventStoreRepository {
    fun load(paymentId: UUID): PaymentAggregate?

    fun append(event: PaymentEvent)
}
