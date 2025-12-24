package com.mengo.booking.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_COMPLETED
import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVED
import com.mengo.architecture.test.infrastructure.AbstractIntegrationTest
import com.mengo.booking.application.BookingUpdateService
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import com.mengo.booking.fixtures.PayloadTestData.buildBookingCancelledPayload
import com.mengo.booking.fixtures.PayloadTestData.buildBookingConfirmedPayload
import com.mengo.booking.fixtures.PayloadTestData.buildBookingCreatedPayload
import com.mengo.booking.fixtures.PayloadTestData.buildPaymentCompletedPayload
import com.mengo.booking.fixtures.PayloadTestData.buildPaymentFailedPayload
import com.mengo.booking.fixtures.PayloadTestData.buildProductReservedPayload
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.verify
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration
import java.util.UUID
import kotlin.test.assertEquals

class BookingQueryKafkaListenerIntegrationTest : AbstractIntegrationTest() {
    @MockitoBean
    lateinit var service: BookingUpdateService

    @Test
    fun `should consume BookingCreatedPayload from onBookingCreated and call BookingUpdateService`() {
        val payload = buildBookingCreatedPayload()
        val messageId = UUID.randomUUID()

        kafkaTemplate.send(
            buildProducerRecord(
                topic = KAFKA_BOOKING_CREATED,
                key = payload.bookingId,
                payload = payload,
                messageId = messageId,
            ),
        )
        kafkaTemplate.flush()

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(service).handleCreated(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertEquals(USER_ID, it.userId)
                },
            )
        }
    }

    @Test
    fun `should consume ProductReservedPayload from onProductReserved and call BookingUpdateService`() {
        val payload = buildProductReservedPayload()
        val messageId = UUID.randomUUID()

        kafkaTemplate.send(
            buildProducerRecord(
                topic = KAFKA_PRODUCT_RESERVED,
                key = payload.bookingId,
                payload = payload,
                messageId = messageId,
            ),
        )
        kafkaTemplate.flush()

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(service).handleProductReserved(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                },
            )
        }
    }

    @Test
    fun `should consume PaymentCompletedPayload from onPaymentCompleted and call BookingUpdateService`() {
        val payload = buildPaymentCompletedPayload()
        val messageId = UUID.randomUUID()

        kafkaTemplate.send(
            buildProducerRecord(
                topic = KAFKA_PAYMENT_COMPLETED,
                key = payload.bookingId,
                payload = payload,
                messageId = messageId,
            ),
        )
        kafkaTemplate.flush()

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(service).handlePaymentCompleted(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                },
            )
        }
    }

    @Test
    fun `should consume PaymentFailedPayload from onPaymentFailed and call BookingUpdateService`() {
        val payload = buildPaymentFailedPayload()
        val messageId = UUID.randomUUID()

        kafkaTemplate.send(
            buildProducerRecord(
                topic = KAFKA_PAYMENT_FAILED,
                key = payload.bookingId,
                payload = payload,
                messageId = messageId,
            ),
        )
        kafkaTemplate.flush()

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(service).handleStatusChange(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                },
            )
        }
    }

    @Test
    fun `should consume BookingCancelledPayload from onBookingCancelled and call BookingUpdateService`() {
        val payload = buildBookingCancelledPayload()
        val messageId = UUID.randomUUID()

        kafkaTemplate.send(
            buildProducerRecord(
                topic = KAFKA_BOOKING_FAILED,
                key = payload.bookingId,
                payload = payload,
                messageId = messageId,
            ),
        )
        kafkaTemplate.flush()

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(service).handleStatusChange(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                },
            )
        }
    }

    @Test
    fun `should consume BookingConfirmedPayload from onBookingConfirmed and call BookingUpdateService`() {
        val payload = buildBookingConfirmedPayload()
        val messageId = UUID.randomUUID()

        kafkaTemplate.send(
            buildProducerRecord(
                topic = KAFKA_BOOKING_COMPLETED,
                key = payload.bookingId,
                payload = payload,
                messageId = messageId,
            ),
        )
        kafkaTemplate.flush()

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(service).handleStatusChange(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                },
            )
        }
    }
}
