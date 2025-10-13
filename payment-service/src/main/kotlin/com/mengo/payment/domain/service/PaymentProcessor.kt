package com.mengo.payment.domain.service

import com.mengo.payment.domain.model.PaymentEvent

/**
 * Port that defines an external payment engine.
 */
fun interface PaymentProcessor {
    /**
     * Try to authorize the payment and return a reference or fill in an exception in case of error
     * The implementation must be idempotent.
     */
    fun executePayment(payment: PaymentEvent): PaymentProcessorResult
}

sealed class PaymentProcessorResult {
    data class Success(
        val reference: String,
    ) : PaymentProcessorResult()

    data class Failure(
        val reason: String,
    ) : PaymentProcessorResult()
}
