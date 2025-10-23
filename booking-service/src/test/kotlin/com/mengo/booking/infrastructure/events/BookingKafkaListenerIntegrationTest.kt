package com.mengo.booking.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_CANCEL_BOOKING
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_CONFIRM_BOOKING
import com.mengo.booking.application.BookingServiceCommand
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.PayloadTestData.buildPaymentCompletedPayload
import com.mengo.booking.fixtures.PayloadTestData.buildPaymentFailedPayload
import com.mengo.kafka.test.KafkaTestContainerBase
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.verify
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration
import kotlin.test.assertEquals

class BookingKafkaListenerIntegrationTest : KafkaTestContainerBase() {
    @MockitoBean
    lateinit var bookingServiceCommand: BookingServiceCommand

    @Test
    fun `should consume PaymentCompletedEvent from onPaymentCompletedEvent and call bookingService`() {
        val event = buildPaymentCompletedPayload()

        kafkaTemplate.send(KAFKA_SAGA_CONFIRM_BOOKING, event.bookingId, event)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(bookingServiceCommand).onPaymentCompleted(
                check { assertEquals(BOOKING_ID, it.bookingId) },
            )
        }
    }

    @Test
    fun `should consume PaymentFailedEvent and call bookingService`() {
        val event = buildPaymentFailedPayload()

        kafkaTemplate.send(KAFKA_SAGA_CANCEL_BOOKING, event.bookingId, event)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(bookingServiceCommand).onPaymentFailed(check { assertEquals(BOOKING_ID, it.bookingId) })
        }
    }
}
