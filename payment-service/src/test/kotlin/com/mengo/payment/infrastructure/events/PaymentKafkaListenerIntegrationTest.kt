package com.mengo.payment.infrastructure.events

import com.mengo.kafka.test.KafkaTestContainerBase
import com.mengo.payment.application.PaymentServiceCommand
import com.mengo.payment.fixtures.PayloadTestData.buildOrchestratorRequestPaymentPayload
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_SAGA_REQUEST_PAYMENT
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration

class PaymentKafkaListenerIntegrationTest : KafkaTestContainerBase() {
    @MockitoBean
    lateinit var paymentServiceCommand: PaymentServiceCommand

    @Test
    fun `should consume OrchestratorRequestPaymentPayload and call paymentService`() {
        val event = buildOrchestratorRequestPaymentPayload()

        kafkaTemplate.send(KAFKA_SAGA_REQUEST_PAYMENT, event.bookingId, event)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(paymentServiceCommand).onRequestPayment(any())
        }
    }
}
