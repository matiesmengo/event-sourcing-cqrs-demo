package com.mengo.payment.domain.service

import com.mengo.payment.domain.model.command.PaymentCommand
import com.mengo.payment.domain.model.command.SagaCommand

interface PaymentEventPublisher {
    fun publishPaymentInitiated(payment: PaymentCommand.PaymentInitiated)

    fun publishPaymentCompleted(payment: SagaCommand.PaymentCompleted)

    fun publishPaymentFailed(payment: SagaCommand.PaymentFailed)
}
