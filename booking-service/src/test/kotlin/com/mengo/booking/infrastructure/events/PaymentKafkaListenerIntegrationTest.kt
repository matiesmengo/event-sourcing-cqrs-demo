package com.mengo.booking.infrastructure.events

import com.mengo.booking.application.BookingServiceCommand
import com.mengo.booking.fixtures.PayloadTestData.buildPaymentCompletedPayload
import com.mengo.booking.fixtures.PayloadTestData.buildPaymentFailedPayload
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.booking.infrastructure.events.mappers.toDomain
import com.mengo.kafka.test.KafkaTestContainerBase
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration

class PaymentKafkaListenerIntegrationTest : KafkaTestContainerBase() {
    @MockitoBean
    lateinit var bookingServiceCommand: BookingServiceCommand

    @Test
    fun `should consume PaymentCompletedEvent and call bookingService`() {
        val event = buildPaymentCompletedPayload()

        kafkaTemplate.send(KAFKA_PAYMENT_COMPLETED, event.paymentId, event)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(bookingServiceCommand).onPaymentCompleted(any())
        }
    }

    @Test
    fun `should consume PaymentFailedEvent and call bookingService`() {
        val event = buildPaymentFailedPayload()

        kafkaTemplate.send(KAFKA_PAYMENT_FAILED, event.paymentId, event)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(bookingServiceCommand).onPaymentFailed(event.toDomain())
        }
    }
}
