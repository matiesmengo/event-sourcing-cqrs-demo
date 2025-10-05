package com.mengo.booking.infrastructure.events

import com.mengo.booking.application.BookingServiceAdapter
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.PAYMENT_ID
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.booking.infrastructure.events.mappers.toDomain
import com.mengo.kafka.test.KafkaTestContainerBase
import com.mengo.payment.events.PaymentCompletedEvent
import com.mengo.payment.events.PaymentFailedEvent
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.Duration

class PaymentKafkaListenerIntegrationTest : KafkaTestContainerBase() {
    @MockBean
    private lateinit var bookingServiceAdapter: BookingServiceAdapter

    @Test
    fun `should consume PaymentCompletedEvent and call bookingService`() {
        val event = PaymentCompletedEvent(PAYMENT_ID.toString(), BOOKING_ID.toString(), "reference-147")

        kafkaTemplate.send(KAFKA_PAYMENT_COMPLETED, event.paymentId, event)

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted {
            verify(bookingServiceAdapter).onPaymentCompleted(any())
        }
    }

    @Test
    fun `should consume PaymentFailedEvent and call bookingService`() {
        val event = PaymentFailedEvent(PAYMENT_ID.toString(), BOOKING_ID.toString(), "Card declined")

        kafkaTemplate.send(KAFKA_PAYMENT_FAILED, event.paymentId, event)

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted {
            verify(bookingServiceAdapter).onPaymentFailed(event.toDomain())
        }
    }
}
