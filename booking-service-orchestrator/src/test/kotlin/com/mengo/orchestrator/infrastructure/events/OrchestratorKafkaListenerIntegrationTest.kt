package com.mengo.orchestrator.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVATION_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVED
import com.mengo.kafka.test.KafkaTestContainerBase
import com.mengo.orchestrator.application.OrchestratorServiceCommand
import com.mengo.orchestrator.fixtures.OrchestratorConstants.BOOKING_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PAYMENT_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PAYMENT_REASON
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PAYMENT_REFERENCE
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_PRICE
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_QUANTITY
import com.mengo.orchestrator.fixtures.PayloadTestData.buildBookingCreatedPayload
import com.mengo.orchestrator.fixtures.PayloadTestData.buildPaymentCompletedPayload
import com.mengo.orchestrator.fixtures.PayloadTestData.buildPaymentFailedPayload
import com.mengo.orchestrator.fixtures.PayloadTestData.buildProductReservationFailedPayload
import com.mengo.orchestrator.fixtures.PayloadTestData.buildProductReservedPayload
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.verify
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrchestratorKafkaListenerIntegrationTest : KafkaTestContainerBase() {
    @MockitoBean
    lateinit var serviceCommand: OrchestratorServiceCommand

    @Test
    fun `should consume BookingCreatedPayload from onBookingCreated and call paymentService`() {
        val payload = buildBookingCreatedPayload()

        kafkaTemplate.send(KAFKA_BOOKING_CREATED, payload.bookingId, payload)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand).onBookingCreated(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertTrue(it.products.any { product -> product.productId == PRODUCT_ID })
                    assertTrue(it.products.any { product -> product.quantity == PRODUCT_QUANTITY })
                },
            )
        }
    }

    @Test
    fun `should consume ProductReservedPayload from onProductReserved and call paymentService`() {
        val payload = buildProductReservedPayload()

        kafkaTemplate.send(KAFKA_PRODUCT_RESERVED, payload.bookingId, payload)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand).onProductReserved(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertEquals(PRODUCT_ID, it.productId)
                    assertEquals(PRODUCT_QUANTITY, it.quantity)
                    assertEquals(PRODUCT_PRICE, it.price)
                },
            )
        }
    }

    @Test
    fun `should consume ProductReservationFailedPayload from onProductReservationFailed and call paymentService`() {
        val payload = buildProductReservationFailedPayload()

        kafkaTemplate.send(KAFKA_PRODUCT_RESERVATION_FAILED, payload.bookingId, payload)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand).onProductReservationFailed(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertEquals(PRODUCT_ID, it.productId)
                },
            )
        }
    }

    @Test
    fun `should consume PaymentCompletedPayload from onPaymentCompleted and call paymentService`() {
        val payload = buildPaymentCompletedPayload()

        kafkaTemplate.send(KAFKA_PAYMENT_COMPLETED, payload.bookingId, payload)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand).onPaymentCompleted(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertEquals(PAYMENT_ID, it.paymentId)
                    assertEquals(PAYMENT_REFERENCE, it.reference)
                },
            )
        }
    }

    @Test
    fun `should consume PaymentFailedPayload from onPaymentFailed and call paymentService`() {
        val payload = buildPaymentFailedPayload()

        kafkaTemplate.send(KAFKA_PAYMENT_FAILED, payload.bookingId, payload)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand).onPaymentFailed(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertEquals(PAYMENT_ID, it.paymentId)
                    assertEquals(PAYMENT_REASON, it.reason)
                },
            )
        }
    }
}
