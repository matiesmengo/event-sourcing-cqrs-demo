package com.mengo.payment.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_REQUEST_PAYMENT
import com.mengo.kafka.test.KafkaTestContainerBase
import com.mengo.payment.application.PaymentServiceCommand
import com.mengo.payment.fixtures.PayloadTestData.buildOrchestratorRequestPaymentPayload
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import com.mengo.payment.fixtures.PaymentConstants.PRODUCT_PRICE
import org.awaitility.Awaitility
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
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
            verify(paymentServiceCommand).onRequestPayment(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertEquals(PRODUCT_PRICE, it.totalPrice)
                },
            )
        }
    }
}
