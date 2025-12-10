package com.mengo.payment.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_REQUEST_PAYMENT
import com.mengo.architecture.test.ContainerBase
import com.mengo.payment.application.PaymentServiceCommand
import com.mengo.payment.fixtures.PayloadTestData.buildOrchestratorRequestPaymentPayload
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import com.mengo.payment.fixtures.PaymentConstants.PRODUCT_PRICE
import org.awaitility.Awaitility
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration
import java.util.UUID

class PaymentKafkaListenerIntegrationTest : ContainerBase() {
    @MockitoBean
    lateinit var paymentServiceCommand: PaymentServiceCommand

    @Test
    fun `should consume OrchestratorRequestPaymentPayload from onRequestPayment and call PaymentServiceCommand`() {
        val event = buildOrchestratorRequestPaymentPayload()
        val messageId = UUID.randomUUID()

        repeat(5) {
            kafkaTemplate.send(
                buildProducerRecord(
                    topic = KAFKA_SAGA_REQUEST_PAYMENT,
                    key = event.bookingId,
                    payload = event,
                    messageId = messageId,
                ),
            )
        }
        kafkaTemplate.flush()
        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(paymentServiceCommand, times(1)).onRequestPayment(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertEquals(PRODUCT_PRICE, it.totalPrice)
                },
            )
        }
    }
}
