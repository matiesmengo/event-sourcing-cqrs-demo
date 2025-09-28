package com.mengo.payment.infrastructure.events

import com.mengo.kafka.test.KafkaTestContainerBase
import com.mengo.payment.domain.model.CompletedPayment
import com.mengo.payment.domain.model.FailedPayment
import com.mengo.payment.events.PaymentCompletedEvent
import com.mengo.payment.events.PaymentFailedEvent
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_ID
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.payment.infrastructure.events.mappers.toAvro
import java.time.Duration
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class PaymentKafkaPublisherIntegrationTest : KafkaTestContainerBase() {
    @Autowired
    private lateinit var publisher: PaymentKafkaPublisher

    @Test
    fun `should publish payment completed event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_PAYMENT_COMPLETED))
        val booking = CompletedPayment(
            bookingId = BOOKING_ID,
            paymentId = PAYMENT_ID,
            reference = "ref-123",
        )
        val avroBooking = booking.toAvro()

        // when
        publisher.publishPaymentCompleted(booking)

        // then
        val records = kafkaConsumer.poll(Duration.ofSeconds(10))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroBooking.paymentId.toString(), record.key())
        assertEquals(avroBooking.paymentId, (record.value() as PaymentCompletedEvent).paymentId)
    }

    @Test
    fun `should publish payment failed event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_PAYMENT_FAILED))

        val failedPayment = FailedPayment(
            bookingId = BOOKING_ID,
            paymentId = PAYMENT_ID,
            reason = "reason failed payment",
        )
        val avroBooking = failedPayment.toAvro()

        // when
        publisher.publishPaymentFailed(failedPayment)

        // then
        val records = kafkaConsumer.poll(Duration.ofSeconds(10))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroBooking.paymentId.toString(), record.key())
        assertEquals(avroBooking.paymentId, (record.value() as PaymentFailedEvent).paymentId)
    }

}