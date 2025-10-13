package com.mengo.payment.infrastructure.events

import com.mengo.payment.domain.model.PaymentCompletedEvent
import com.mengo.payment.domain.model.PaymentFailedEvent
import com.mengo.payment.domain.model.PaymentInitiatedEvent
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_ID
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_INITIATED
import com.mengo.payment.infrastructure.events.mappers.toAvro
import org.apache.avro.specific.SpecificRecord
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.kafka.core.KafkaTemplate

class PaymentKafkaPublisherTest {
    private lateinit var kafkaTemplate: KafkaTemplate<String, SpecificRecord>
    private lateinit var publisher: PaymentKafkaPublisher

    @BeforeEach
    fun setUp() {
        kafkaTemplate = mock()
        publisher = PaymentKafkaPublisher(kafkaTemplate)
    }

    @Test
    fun `should publish Payment Initiated event`() {
        // given
        val initiatedPayment =
            PaymentInitiatedEvent(
                paymentId = PAYMENT_ID,
                bookingId = BOOKING_ID,
                totalAmount = 123.45.toBigDecimal(),
                aggregateVersion = 1,
            )
        val avroPayment = initiatedPayment.toAvro()

        // when
        publisher.publishPaymentInitiated(initiatedPayment)

        // then
        verify(kafkaTemplate)
            .send(eq(KAFKA_PAYMENT_INITIATED), eq(BOOKING_ID.toString()), eq(avroPayment))
    }

    @Test
    fun `should publish Payment Completed event`() {
        // given
        val completedPayment =
            PaymentCompletedEvent(
                paymentId = PAYMENT_ID,
                bookingId = BOOKING_ID,
                reference = "ref-123",
                aggregateVersion = 2,
            )
        val avroPayment = completedPayment.toAvro()

        // when
        publisher.publishPaymentCompleted(completedPayment)

        // then
        verify(kafkaTemplate)
            .send(eq(KAFKA_PAYMENT_COMPLETED), eq(BOOKING_ID.toString()), eq(avroPayment))
    }

    @Test
    fun `should publish Payment Failed event`() {
        // given
        val failedPayment =
            PaymentFailedEvent(
                paymentId = PAYMENT_ID,
                bookingId = BOOKING_ID,
                reason = "reason failed payment",
                aggregateVersion = 2,
            )
        val avroPayment = failedPayment.toAvro()

        // when
        publisher.publishPaymentFailed(failedPayment)

        // then
        verify(kafkaTemplate)
            .send(eq(KAFKA_PAYMENT_FAILED), eq(BOOKING_ID.toString()), eq(avroPayment))
    }
}
