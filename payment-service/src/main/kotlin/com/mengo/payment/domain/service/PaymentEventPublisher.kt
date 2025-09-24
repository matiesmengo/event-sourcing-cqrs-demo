package com.mengo.payment.domain.service

import com.mengo.payment.domain.model.CompletedPayment
import com.mengo.payment.domain.model.FailedPayment

interface PaymentEventPublisher {
    fun publishPaymentCompleted(payment: CompletedPayment)

    fun publishPaymentFailed(payment: FailedPayment)
}
