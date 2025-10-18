package com.mengo.orchestrator.infrastructure.events

import com.mengo.orchestrator.fixtures.CommandTestData.buildSagaCommandCancelBooking
import com.mengo.orchestrator.fixtures.CommandTestData.buildSagaCommandConfirmBooking
import com.mengo.orchestrator.fixtures.CommandTestData.buildSagaCommandReleaseStock
import com.mengo.orchestrator.fixtures.CommandTestData.buildSagaCommandRequestPayment
import com.mengo.orchestrator.fixtures.CommandTestData.buildSagaCommandRequestStock
import com.mengo.orchestrator.fixtures.OrchestratorConstants.BOOKING_ID
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_SAGA_CANCEL_BOOKING
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_SAGA_CONFIRM_BOOKING
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_SAGA_RELEASE_STOCK
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_SAGA_REQUEST_PAYMENT
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_SAGA_REQUEST_STOCK
import com.mengo.orchestrator.infrastructure.events.mapper.toAvro
import org.apache.avro.specific.SpecificRecord
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.kafka.core.KafkaTemplate

class OrchestratorKafkaPublisherTest {
    private lateinit var kafkaTemplate: KafkaTemplate<String, SpecificRecord>
    private lateinit var publisher: OrchestratorKafkaPublisher

    @BeforeEach
    fun setUp() {
        kafkaTemplate = mock()
        publisher = OrchestratorKafkaPublisher(kafkaTemplate)
    }

    @Test
    fun `should publish RequestStock event`() {
        // given
        val command = buildSagaCommandRequestStock()
        val avroBooking = command.toAvro()

        // when
        publisher.publishRequestStock(command)

        // then
        verify(kafkaTemplate)
            .send(eq(KAFKA_SAGA_REQUEST_STOCK), eq(BOOKING_ID.toString()), eq(avroBooking))
    }

    @Test
    fun `should publish RequestPayment event`() {
        // given
        val command = buildSagaCommandRequestPayment()
        val avroCommand = command.toAvro()

        // when
        publisher.publishRequestPayment(command)

        // then
        verify(kafkaTemplate)
            .send(eq(KAFKA_SAGA_REQUEST_PAYMENT), eq(BOOKING_ID.toString()), eq(avroCommand))
    }

    @Test
    fun `should publish ReleaseStock event`() {
        // given
        val command = buildSagaCommandReleaseStock()
        val avroCommand = command.toAvro()

        // when
        publisher.publishReleaseStock(command)

        // then
        verify(kafkaTemplate)
            .send(eq(KAFKA_SAGA_RELEASE_STOCK), eq(BOOKING_ID.toString()), eq(avroCommand))
    }

    @Test
    fun `should publish ConfirmBooking event`() {
        // given
        val command = buildSagaCommandConfirmBooking()
        val avroCommand = command.toAvro()

        // when
        publisher.publishConfirmBooking(command)

        // then
        verify(kafkaTemplate)
            .send(eq(KAFKA_SAGA_CONFIRM_BOOKING), eq(BOOKING_ID.toString()), eq(avroCommand))
    }

    @Test
    fun `should publish CancelBooking event`() {
        // given
        val command = buildSagaCommandCancelBooking()
        val avroCommand = command.toAvro()

        // when
        publisher.publishCancelBooking(command)

        // then
        verify(kafkaTemplate)
            .send(eq(KAFKA_SAGA_CANCEL_BOOKING), eq(BOOKING_ID.toString()), eq(avroCommand))
    }
}
