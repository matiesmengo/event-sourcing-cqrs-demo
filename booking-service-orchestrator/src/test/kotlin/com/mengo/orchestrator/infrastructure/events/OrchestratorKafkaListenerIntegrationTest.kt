package com.mengo.orchestrator.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVATION_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVED
import com.mengo.architecture.test.infrastructure.AbstractIntegrationTest
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
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrchestratorKafkaListenerIntegrationTest : AbstractIntegrationTest() {
    @MockitoBean
    lateinit var serviceCommand: OrchestratorServiceCommand

    @Test
    fun `should consume BookingCreatedPayload from onBookingCreated and call OrchestratorServiceCommand`() {
        val payload = buildBookingCreatedPayload()
        val messageId = UUID.randomUUID()

        repeat(5) {
            kafkaTemplate.send(
                buildProducerRecord(
                    topic = KAFKA_BOOKING_CREATED,
                    key = payload.bookingId,
                    payload = payload,
                    messageId = messageId,
                ),
            )
        }
        kafkaTemplate.flush()

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand, times(1)).onBookingCreated(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertTrue(it.products.any { product -> product.productId == PRODUCT_ID })
                    assertTrue(it.products.any { product -> product.quantity == PRODUCT_QUANTITY })
                },
            )
        }
    }

    @Test
    fun `should consume ProductReservedPayload from onProductReserved and call OrchestratorServiceCommand`() {
        val payload = buildProductReservedPayload()
        val messageId = UUID.randomUUID()

        repeat(5) {
            kafkaTemplate.send(
                buildProducerRecord(
                    topic = KAFKA_PRODUCT_RESERVED,
                    key = payload.bookingId,
                    payload = payload,
                    messageId = messageId,
                ),
            )
        }
        kafkaTemplate.flush()

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand, times(1)).onProductReserved(
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
    fun `should consume ProductReservationFailedPayload from onProductReservationFailed and call OrchestratorServiceCommand`() {
        val payload = buildProductReservationFailedPayload()
        val messageId = UUID.randomUUID()

        repeat(5) {
            kafkaTemplate.send(
                buildProducerRecord(
                    topic = KAFKA_PRODUCT_RESERVATION_FAILED,
                    key = payload.bookingId,
                    payload = payload,
                    messageId = messageId,
                ),
            )
        }
        kafkaTemplate.flush()

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand, times(1)).onProductReservationFailed(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertEquals(PRODUCT_ID, it.productId)
                },
            )
        }
    }

    @Test
    fun `should consume PaymentCompletedPayload from onPaymentCompleted and call OrchestratorServiceCommand`() {
        val payload = buildPaymentCompletedPayload()
        val messageId = UUID.randomUUID()

        repeat(5) {
            kafkaTemplate.send(
                buildProducerRecord(
                    topic = KAFKA_PAYMENT_COMPLETED,
                    key = payload.bookingId,
                    payload = payload,
                    messageId = messageId,
                ),
            )
        }
        kafkaTemplate.flush()

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand, times(1)).onPaymentCompleted(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertEquals(PAYMENT_ID, it.paymentId)
                    assertEquals(PAYMENT_REFERENCE, it.reference)
                },
            )
        }
    }

    @Test
    fun `should consume PaymentFailedPayload from onPaymentFailed and call OrchestratorServiceCommand`() {
        val payload = buildPaymentFailedPayload()
        val messageId = UUID.randomUUID()

        repeat(5) {
            kafkaTemplate.send(
                buildProducerRecord(
                    topic = KAFKA_PAYMENT_FAILED,
                    key = payload.bookingId,
                    payload = payload,
                    messageId = messageId,
                ),
            )
        }
        kafkaTemplate.flush()

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand, times(1)).onPaymentFailed(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertEquals(PAYMENT_ID, it.paymentId)
                    assertEquals(PAYMENT_REASON, it.reason)
                },
            )
        }
    }
}
