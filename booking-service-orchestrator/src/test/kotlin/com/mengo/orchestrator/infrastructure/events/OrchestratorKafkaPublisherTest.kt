package com.mengo.orchestrator.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_CANCEL_BOOKING
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_CONFIRM_BOOKING
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_RELEASE_STOCK
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_REQUEST_PAYMENT
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_REQUEST_STOCK
import com.mengo.architecture.outbox.OutboxRepository
import com.mengo.orchestrator.fixtures.CommandTestData.buildSagaCommandCancelBooking
import com.mengo.orchestrator.fixtures.CommandTestData.buildSagaCommandConfirmBooking
import com.mengo.orchestrator.fixtures.CommandTestData.buildSagaCommandReleaseStock
import com.mengo.orchestrator.fixtures.CommandTestData.buildSagaCommandRequestPayment
import com.mengo.orchestrator.fixtures.CommandTestData.buildSagaCommandRequestStock
import com.mengo.orchestrator.fixtures.OrchestratorConstants.BOOKING_ID
import com.mengo.orchestrator.infrastructure.events.mapper.toAvro
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class OrchestratorKafkaPublisherTest {
    private lateinit var outboxRepository: OutboxRepository
    private lateinit var publisher: OrchestratorKafkaPublisher

    @BeforeEach
    fun setUp() {
        outboxRepository = mock()
        publisher = OrchestratorKafkaPublisher(outboxRepository)
    }

    @Test
    fun `should publish RequestStock event`() {
        // given
        val command = buildSagaCommandRequestStock()
        val avroCommand = command.toAvro()

        // when
        publisher.publishRequestStock(command)

        // then
        verify(outboxRepository)
            .persistOutboxEvent(eq(KAFKA_SAGA_REQUEST_STOCK), eq(BOOKING_ID.toString()), eq(avroCommand))
    }

    @Test
    fun `should publish RequestPayment event`() {
        // given
        val command = buildSagaCommandRequestPayment()
        val avroCommand = command.toAvro()

        // when
        publisher.publishRequestPayment(command)

        // then
        verify(outboxRepository)
            .persistOutboxEvent(eq(KAFKA_SAGA_REQUEST_PAYMENT), eq(BOOKING_ID.toString()), eq(avroCommand))
    }

    @Test
    fun `should publish ReleaseStock event`() {
        // given
        val command = buildSagaCommandReleaseStock()
        val avroCommand = command.toAvro()

        // when
        publisher.publishReleaseStock(command)

        // then
        verify(outboxRepository)
            .persistOutboxEvent(eq(KAFKA_SAGA_RELEASE_STOCK), eq(BOOKING_ID.toString()), eq(avroCommand))
    }

    @Test
    fun `should publish ConfirmBooking event`() {
        // given
        val command = buildSagaCommandConfirmBooking()
        val avroCommand = command.toAvro()

        // when
        publisher.publishConfirmBooking(command)

        // then
        verify(outboxRepository)
            .persistOutboxEvent(eq(KAFKA_SAGA_CONFIRM_BOOKING), eq(BOOKING_ID.toString()), eq(avroCommand))
    }

    @Test
    fun `should publish CancelBooking event`() {
        // given
        val command = buildSagaCommandCancelBooking()
        val avroCommand = command.toAvro()

        // when
        publisher.publishCancelBooking(command)

        // then
        verify(outboxRepository)
            .persistOutboxEvent(eq(KAFKA_SAGA_CANCEL_BOOKING), eq(BOOKING_ID.toString()), eq(avroCommand))
    }
}
