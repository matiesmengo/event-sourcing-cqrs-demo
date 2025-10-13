package com.mengo.payment.domain.service

import com.mengo.payment.domain.model.PaymentCompletedEvent
import com.mengo.payment.domain.model.PaymentFailedEvent
import com.mengo.payment.domain.model.PaymentInitiatedEvent

interface PaymentEventPublisher {
    fun publishPaymentInitiated(payment: PaymentInitiatedEvent)

    fun publishPaymentCompleted(payment: PaymentCompletedEvent)

    fun publishPaymentFailed(payment: PaymentFailedEvent)
}
