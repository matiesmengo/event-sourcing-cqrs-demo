package com.mengo.payment.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_INITIATED
import com.mengo.kafka.test.KafkaTestContainerBase
import com.mengo.payload.payment.PaymentCompletedPayload
import com.mengo.payload.payment.PaymentFailedPayload
import com.mengo.payload.payment.PaymentInitiatedPayload
import com.mengo.payment.domain.model.PaymentCompletedEvent
import com.mengo.payment.domain.model.PaymentFailedEvent
import com.mengo.payment.domain.model.PaymentInitiatedEvent
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_ID
import com.mengo.payment.infrastructure.events.mappers.toAvro
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals

class PaymentKafkaPublisherIntegrationTest : KafkaTestContainerBase() {
    @Autowired
    private lateinit var publisher: PaymentKafkaPublisher

    @Test
    fun `should publish payment initiated event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_PAYMENT_INITIATED))

        val initiatedPayment =
            PaymentInitiatedEvent(
                paymentId = PAYMENT_ID,
                bookingId = BOOKING_ID,
                totalPrice = 123.45.toBigDecimal(),
                aggregateVersion = 1,
            )
        val avroPayment = initiatedPayment.toAvro()

        // when
        publisher.publishPaymentInitiated(initiatedPayment)

        // then
        val records = kafkaConsumer.poll(Duration.ofSeconds(10))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroPayment.bookingId.toString(), record.key())
        assertEquals(avroPayment.paymentId, (record.value() as PaymentInitiatedPayload).get("paymentId"))
    }

    @Test
    fun `should publish payment completed event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_PAYMENT_COMPLETED))

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
        val records = kafkaConsumer.poll(Duration.ofSeconds(10))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroPayment.bookingId.toString(), record.key())
        assertEquals(avroPayment.paymentId, (record.value() as PaymentCompletedPayload).get("paymentId"))
    }

    @Test
    fun `should publish payment failed event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_PAYMENT_FAILED))

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
        val records = kafkaConsumer.poll(Duration.ofSeconds(10))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroPayment.bookingId.toString(), record.key())
        assertEquals(avroPayment.paymentId, (record.value() as PaymentFailedPayload).get("paymentId"))
    }
}
