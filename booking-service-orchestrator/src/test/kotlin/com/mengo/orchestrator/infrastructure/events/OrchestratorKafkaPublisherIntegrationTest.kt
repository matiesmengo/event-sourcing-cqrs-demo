package com.mengo.orchestrator.infrastructure.events

import com.mengo.kafka.test.KafkaTestContainerBase
import com.mengo.orchestrator.fixtures.CommandTestData.buildSagaCommandCancelBooking
import com.mengo.orchestrator.fixtures.CommandTestData.buildSagaCommandConfirmBooking
import com.mengo.orchestrator.fixtures.CommandTestData.buildSagaCommandReleaseStock
import com.mengo.orchestrator.fixtures.CommandTestData.buildSagaCommandRequestPayment
import com.mengo.orchestrator.fixtures.CommandTestData.buildSagaCommandRequestStock
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_SAGA_CANCEL_BOOKING
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_SAGA_CONFIRM_BOOKING
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_SAGA_RELEASE_STOCK
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_SAGA_REQUEST_PAYMENT
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_SAGA_REQUEST_STOCK
import com.mengo.orchestrator.infrastructure.events.mapper.toAvro
import com.mengo.orchestrator.payload.OrchestratorCancelBookingPayload
import com.mengo.orchestrator.payload.OrchestratorConfirmBookingPayload
import com.mengo.orchestrator.payload.OrchestratorReleaseStockPayload
import com.mengo.orchestrator.payload.OrchestratorRequestPaymentPayload
import com.mengo.orchestrator.payload.OrchestratorRequestStockPayload
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals

class OrchestratorKafkaPublisherIntegrationTest : KafkaTestContainerBase() {
    @Autowired
    private lateinit var publisher: OrchestratorKafkaPublisher

    @Test
    fun `should publish request stock event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_SAGA_REQUEST_STOCK))
        val command = buildSagaCommandRequestStock()
        val avroCommand = command.toAvro()

        // when
        publisher.publishRequestStock(command)

        // then
        val records = kafkaConsumer.poll(Duration.ofSeconds(5))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroCommand.bookingId.toString(), record.key())
        assertEquals(avroCommand.bookingId, (record.value() as OrchestratorRequestStockPayload).bookingId)
    }

    @Test
    fun `should publish request payment event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_SAGA_REQUEST_PAYMENT))
        val command = buildSagaCommandRequestPayment()
        val avroCommand = command.toAvro()

        // when
        publisher.publishRequestPayment(command)

        // then
        val records = kafkaConsumer.poll(Duration.ofSeconds(5))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroCommand.bookingId.toString(), record.key())
        assertEquals(avroCommand.bookingId, (record.value() as OrchestratorRequestPaymentPayload).bookingId)
    }

    @Test
    fun `should publish release stock event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_SAGA_RELEASE_STOCK))
        val command = buildSagaCommandReleaseStock()
        val avroCommand = command.toAvro()

        // when
        publisher.publishReleaseStock(command)

        // then
        val records = kafkaConsumer.poll(Duration.ofSeconds(5))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroCommand.bookingId.toString(), record.key())
        assertEquals(avroCommand.bookingId, (record.value() as OrchestratorReleaseStockPayload).bookingId)
    }

    @Test
    fun `should publish confirm booking event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_SAGA_CONFIRM_BOOKING))
        val command = buildSagaCommandConfirmBooking()
        val avroCommand = command.toAvro()

        // when
        publisher.publishConfirmBooking(command)

        // then
        val records = kafkaConsumer.poll(Duration.ofSeconds(5))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroCommand.bookingId.toString(), record.key())
        assertEquals(avroCommand.bookingId, (record.value() as OrchestratorConfirmBookingPayload).bookingId)
    }

    @Test
    fun `should publish cancel booking event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_SAGA_CANCEL_BOOKING))
        val command = buildSagaCommandCancelBooking()
        val avroCommand = command.toAvro()

        // when
        publisher.publishCancelBooking(command)

        // then
        val records = kafkaConsumer.poll(Duration.ofSeconds(5))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroCommand.bookingId.toString(), record.key())
        assertEquals(avroCommand.bookingId, (record.value() as OrchestratorCancelBookingPayload).bookingId)
    }
}
