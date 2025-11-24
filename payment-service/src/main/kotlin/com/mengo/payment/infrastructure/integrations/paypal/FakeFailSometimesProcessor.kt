package com.mengo.payment.infrastructure.integrations.paypal

import com.mengo.architecture.metadata.MetadataContextHolder
import com.mengo.payment.domain.model.PaymentInitiatedEvent
import com.mengo.payment.domain.service.PaymentProcessor
import com.mengo.payment.domain.service.PaymentProcessorResult
import org.springframework.stereotype.Component
import java.util.concurrent.ThreadLocalRandom

@Component
class FakeFailSometimesProcessor : PaymentProcessor {
    override fun executePayment(payment: PaymentInitiatedEvent): PaymentProcessorResult {
        val metadata = MetadataContextHolder.get()
        val forcedOutcome = metadata?.attributes?.get("forced-payment-outcome")?.uppercase()

        return when (forcedOutcome) {
            "SUCCESS" -> PaymentProcessorResult.Success("Forced success for ${payment.paymentId}")
            "FAILURE" -> PaymentProcessorResult.Failure("Forced failure for ${payment.paymentId}")
            else -> simulateRandom(payment)
        }
    }

    private fun simulateRandom(payment: PaymentInitiatedEvent): PaymentProcessorResult {
        val r = ThreadLocalRandom.current().nextInt(0, 10)
        return if (r < 6) {
            PaymentProcessorResult.Success("Random success for ${payment.paymentId}")
        } else {
            PaymentProcessorResult.Failure("Random failure for ${payment.paymentId}")
        }
    }
}
