package com.mengo.payment.infrastructure.integrations.paypal

import com.mengo.architecture.metadata.MetadataContextHolder
import com.mengo.payment.domain.service.PaymentProcessor
import com.mengo.payment.domain.service.PaymentProcessorResult
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

@Component
class FakeFailSometimesProcessor : PaymentProcessor {
    override fun executePayment(paymentId: UUID): PaymentProcessorResult {
        val metadata = MetadataContextHolder.get()
        val forcedOutcome = metadata?.attributes?.get("forced-payment-outcome")?.uppercase()

        return when (forcedOutcome) {
            "SUCCESS" -> PaymentProcessorResult.Success("Forced success for $paymentId")
            "FAILURE" -> PaymentProcessorResult.Failure("Forced failure for $paymentId")
            else -> simulateRandom(paymentId)
        }
    }

    private fun simulateRandom(paymentId: UUID): PaymentProcessorResult {
        val r = ThreadLocalRandom.current().nextInt(0, 10)
        return if (r < 6) {
            PaymentProcessorResult.Success("Random success for $paymentId")
        } else {
            PaymentProcessorResult.Failure("Random failure for $paymentId")
        }
    }
}
