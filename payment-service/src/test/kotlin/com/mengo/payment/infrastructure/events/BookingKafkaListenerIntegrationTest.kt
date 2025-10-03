package com.mengo.payment.infrastructure.events

import com.mengo.booking.events.BookingCreatedEvent
import com.mengo.kafka.test.KafkaTestContainerBase
import com.mengo.payment.application.PaymentService
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import com.mengo.payment.fixtures.PaymentConstants.RESOURCE_ID
import com.mengo.payment.fixtures.PaymentConstants.USER_ID
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.payment.infrastructure.events.mappers.toDomain
import java.time.Duration
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.springframework.boot.test.mock.mockito.MockBean

class BookingKafkaListenerIntegrationTest : KafkaTestContainerBase() {
    @MockBean
    private lateinit var paymentService: PaymentService

    @Test
    fun `should consume PaymentCompletedEvent and call bookingService`() {
        val event = BookingCreatedEvent(BOOKING_ID.toString(), USER_ID.toString(), RESOURCE_ID.toString() )

        kafkaTemplate.send(KAFKA_BOOKING_CREATED, event.bookingId, event)

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted {
            verify(paymentService).onBookingCreated(event.toDomain())
        }
    }
}
