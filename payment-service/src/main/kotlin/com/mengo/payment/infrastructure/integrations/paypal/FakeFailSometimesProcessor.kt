package com.mengo.payment.infrastructure.integrations.paypal

import com.mengo.payment.domain.model.PaymentEvent
import com.mengo.payment.domain.service.PaymentProcessor
import com.mengo.payment.domain.service.PaymentProcessorResult
import org.springframework.stereotype.Component
import java.util.concurrent.ThreadLocalRandom

@Component
class FakeFailSometimesProcessor : PaymentProcessor {
    override fun executePayment(payment: PaymentEvent): PaymentProcessorResult {
        val r = ThreadLocalRandom.current().nextInt(0, 10)
        return if (r < 11) {
            PaymentProcessorResult.Success("Random success occurred - ${payment.paymentId}")
        } else {
            PaymentProcessorResult.Failure("Random failure occurred")
        }
    }
}
