package com.mengo.booking.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_CANCEL_BOOKING
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_CONFIRM_BOOKING
import com.mengo.architecture.test.ContainerBase
import com.mengo.booking.application.BookingServiceCommand
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.PayloadTestData.buildPaymentCompletedPayload
import com.mengo.booking.fixtures.PayloadTestData.buildPaymentFailedPayload
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration
import java.util.UUID
import kotlin.test.assertEquals

class BookingKafkaListenerIntegrationTest : ContainerBase() {
    @MockitoBean
    lateinit var bookingServiceCommand: BookingServiceCommand

    @Test
    fun `should consume OrchestratorConfirmBookingPayload from onPaymentCompletedEvent and call BookingServiceCommand`() {
        val payload = buildPaymentCompletedPayload()
        val messageId = UUID.randomUUID()

        repeat(5) {
            kafkaTemplate.send(
                buildProducerRecord(
                    topic = KAFKA_SAGA_CONFIRM_BOOKING,
                    key = payload.bookingId,
                    payload = payload,
                    messageId = messageId,
                ),
            )
        }
        kafkaTemplate.flush()

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(bookingServiceCommand, times(1)).onPaymentCompleted(
                check { assertEquals(BOOKING_ID, it.bookingId) },
            )
        }
    }

    @Test
    fun `should consume OrchestratorCancelBookingPayload from onPaymentFailedEvent and call BookingServiceCommand`() {
        val payload = buildPaymentFailedPayload()
        val messageId = UUID.randomUUID()

        repeat(5) {
            kafkaTemplate.send(
                buildProducerRecord(
                    topic = KAFKA_SAGA_CANCEL_BOOKING,
                    key = payload.bookingId,
                    payload = payload,
                    messageId = messageId,
                ),
            )
        }
        kafkaTemplate.flush()

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(bookingServiceCommand, times(1)).onPaymentFailed(check { assertEquals(BOOKING_ID, it.bookingId) })
        }
    }
}
