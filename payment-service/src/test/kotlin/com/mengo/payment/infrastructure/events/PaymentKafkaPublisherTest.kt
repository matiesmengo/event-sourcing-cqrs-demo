package com.mengo.payment.infrastructure.events

import com.mengo.payment.domain.model.CompletedPayment
import com.mengo.payment.domain.model.FailedPayment
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_ID
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_FAILED
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
    fun `should publish Payment Completed event`() {
        // given
        val completedPayment =
            CompletedPayment(
                bookingId = BOOKING_ID,
                paymentId = PAYMENT_ID,
                reference = "ref-123",
            )
        val avroBooking = completedPayment.toAvro()

        // when
        publisher.publishPaymentCompleted(completedPayment)

        // then
        verify(kafkaTemplate)
            .send(eq(KAFKA_PAYMENT_COMPLETED), eq(PAYMENT_ID.toString()), eq(avroBooking))
    }

    @Test
    fun `should publish Payment Failed event`() {
        // given
        val failedPayment =
            FailedPayment(
                bookingId = BOOKING_ID,
                paymentId = PAYMENT_ID,
                reason = "reason failed payment",
            )
        val avroBooking = failedPayment.toAvro()

        // when
        publisher.publishPaymentFailed(failedPayment)

        // then
        verify(kafkaTemplate)
            .send(eq(KAFKA_PAYMENT_FAILED), eq(PAYMENT_ID.toString()), eq(avroBooking))
    }
}
