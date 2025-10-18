package com.mengo.payment.infrastructure.integrations.paypal

import com.mengo.payment.domain.model.PaymentInitiatedEvent
import com.mengo.payment.domain.service.PaymentProcessor
import com.mengo.payment.domain.service.PaymentProcessorResult
import org.springframework.stereotype.Component
import java.util.concurrent.ThreadLocalRandom

@Component
class FakeFailSometimesProcessor : PaymentProcessor {
    override fun executePayment(payment: PaymentInitiatedEvent): PaymentProcessorResult {
        val r = ThreadLocalRandom.current().nextInt(0, 10)
        return if (r < 11) {
            PaymentProcessorResult.Success("The payment Id: ${payment.paymentId}, of ${payment.totalPrice} euros was made successfully.")
        } else {
            PaymentProcessorResult.Failure("The payment Id: ${payment.paymentId}, of ${payment.totalPrice} euros has failed.")
        }
    }
}
