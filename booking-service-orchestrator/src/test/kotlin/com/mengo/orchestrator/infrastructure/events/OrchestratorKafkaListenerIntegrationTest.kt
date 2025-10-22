package com.mengo.orchestrator.infrastructure.events

import com.mengo.kafka.test.KafkaTestContainerBase
import com.mengo.orchestrator.application.OrchestratorServiceCommand
import com.mengo.orchestrator.fixtures.PayloadTestData.buildBookingCreatedPayload
import com.mengo.orchestrator.fixtures.PayloadTestData.buildPaymentCompletedPayload
import com.mengo.orchestrator.fixtures.PayloadTestData.buildPaymentFailedPayload
import com.mengo.orchestrator.fixtures.PayloadTestData.buildProductReservationFailedPayload
import com.mengo.orchestrator.fixtures.PayloadTestData.buildProductReservedPayload
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_PRODUCT_RESERVATION_FAILED
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_PRODUCT_RESERVED
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration

class OrchestratorKafkaListenerIntegrationTest : KafkaTestContainerBase() {
    @MockitoBean
    lateinit var serviceCommand: OrchestratorServiceCommand

    @Test
    fun `should consume BookingCreatedPayload and call paymentService`() {
        val payload = buildBookingCreatedPayload()

        kafkaTemplate.send(KAFKA_BOOKING_CREATED, payload.bookingId, payload)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand).onBookingCreated(any())
        }
    }

    @Test
    fun `should consume ProductReservedPayload and call paymentService`() {
        val payload = buildProductReservedPayload()

        kafkaTemplate.send(KAFKA_PRODUCT_RESERVED, payload.bookingId, payload)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand).onProductReserved(any())
        }
    }

    @Test
    fun `should consume ProductReservationFailedPayload and call paymentService`() {
        val payload = buildProductReservationFailedPayload()

        kafkaTemplate.send(KAFKA_PRODUCT_RESERVATION_FAILED, payload.bookingId, payload)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand).onProductReservationFailed(any())
        }
    }

    @Test
    fun `should consume PaymentCompletedPayload and call paymentService`() {
        val payload = buildPaymentCompletedPayload()

        kafkaTemplate.send(KAFKA_PAYMENT_COMPLETED, payload.bookingId, payload)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand).onPaymentCompleted(any())
        }
    }

    @Test
    fun `should consume PaymentFailedPayload and call paymentService`() {
        val payload = buildPaymentFailedPayload()

        kafkaTemplate.send(KAFKA_PAYMENT_FAILED, payload.bookingId, payload)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand).onPaymentFailed(any())
        }
    }
}
