package com.mengo.booking.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_COMPLETED
import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_FAILED
import com.mengo.booking.fixtures.CommandTestData.buildSagaCommandBookingConfirmed
import com.mengo.booking.fixtures.CommandTestData.buildSagaCommandBookingCreated
import com.mengo.booking.fixtures.CommandTestData.buildSagaCommandBookingFailed
import com.mengo.booking.infrastructure.events.mappers.toAvro
import com.mengo.kafka.test.KafkaTestContainerBase
import com.mengo.payload.booking.BookingCancelledPayload
import com.mengo.payload.booking.BookingConfirmedPayload
import com.mengo.payload.booking.BookingCreatedPayload
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals

class BookingKafkaPublisherIntegrationTest : KafkaTestContainerBase() {
    @Autowired
    private lateinit var publisher: BookingKafkaPublisher

    @Test
    fun `should publish booking created event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_BOOKING_CREATED))
        val booking = buildSagaCommandBookingCreated()
        val avroBooking = booking.toAvro()

        // when
        publisher.publishBookingCreated(booking)

        // then
        val records = kafkaConsumer.poll(Duration.ofSeconds(5))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroBooking.bookingId.toString(), record.key())
        assertEquals(avroBooking.bookingId, (record.value() as BookingCreatedPayload).bookingId)
    }

    @Test
    fun `should publish booking completed event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_BOOKING_COMPLETED))
        val booking = buildSagaCommandBookingConfirmed()
        val avroBooking = booking.toAvro()

        // when
        publisher.publishBookingCompleted(booking)

        // then
        val records = kafkaConsumer.poll(Duration.ofSeconds(5))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroBooking.bookingId.toString(), record.key())
        assertEquals(avroBooking.bookingId, (record.value() as BookingConfirmedPayload).bookingId)
    }

    @Test
    fun `should publish booking failed event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_BOOKING_FAILED))
        val booking = buildSagaCommandBookingFailed()
        val avroBooking = booking.toAvro()

        // when
        publisher.publishBookingFailed(booking)

        // then
        val records = kafkaConsumer.poll(Duration.ofSeconds(5))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroBooking.bookingId.toString(), record.key())
        assertEquals(avroBooking.bookingId, (record.value() as BookingCancelledPayload).bookingId)
    }
}
