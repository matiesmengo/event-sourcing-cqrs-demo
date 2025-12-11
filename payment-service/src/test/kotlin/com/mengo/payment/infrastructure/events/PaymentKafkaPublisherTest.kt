package com.mengo.payment.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_INITIATED
import com.mengo.architecture.outbox.OutboxRepository
import com.mengo.payment.domain.model.command.PaymentCommand
import com.mengo.payment.domain.model.command.SagaCommand
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_ID
import com.mengo.payment.infrastructure.events.mappers.toAvro
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class PaymentKafkaPublisherTest {
    private lateinit var outboxRepository: OutboxRepository
    private lateinit var publisher: PaymentKafkaPublisher

    @BeforeEach
    fun setUp() {
        outboxRepository = mock()
        publisher = PaymentKafkaPublisher(outboxRepository)
    }

    @Test
    fun `should publish Payment Initiated event`() {
        // given
        val initiatedPayment =
            PaymentCommand.PaymentInitiated(
                paymentId = PAYMENT_ID,
                bookingId = BOOKING_ID,
                totalPrice = 123.45.toBigDecimal(),
            )
        val avroPayment = initiatedPayment.toAvro()

        // when
        publisher.publishPaymentInitiated(initiatedPayment)

        // then
        verify(outboxRepository)
            .persistOutboxEvent(eq(KAFKA_PAYMENT_INITIATED), eq(BOOKING_ID.toString()), eq(avroPayment))
    }

    @Test
    fun `should publish Payment Completed event`() {
        // given
        val completedPayment =
            SagaCommand.PaymentCompleted(
                paymentId = PAYMENT_ID,
                bookingId = BOOKING_ID,
                reference = "ref-123",
            )
        val avroPayment = completedPayment.toAvro()

        // when
        publisher.publishPaymentCompleted(completedPayment)

        // then
        verify(outboxRepository)
            .persistOutboxEvent(eq(KAFKA_PAYMENT_COMPLETED), eq(BOOKING_ID.toString()), eq(avroPayment))
    }

    @Test
    fun `should publish Payment Failed event`() {
        // given
        val failedPayment =
            SagaCommand.PaymentFailed(
                paymentId = PAYMENT_ID,
                bookingId = BOOKING_ID,
                reason = "reason failed payment",
            )
        val avroPayment = failedPayment.toAvro()

        // when
        publisher.publishPaymentFailed(failedPayment)

        // then
        verify(outboxRepository)
            .persistOutboxEvent(eq(KAFKA_PAYMENT_FAILED), eq(BOOKING_ID.toString()), eq(avroPayment))
    }
}
