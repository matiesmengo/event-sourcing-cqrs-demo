package com.mengo.booking.infrastructure.events

import com.mengo.booking.fixtures.BookingTestData.buildBookingCreatedEvent
import com.mengo.booking.fixtures.BookingTestData.buildBookingPaymentConfirmedEvent
import com.mengo.booking.fixtures.BookingTestData.buildBookingPaymentFailedEvent
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_BOOKING_COMPLETED
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_BOOKING_FAILED
import com.mengo.booking.infrastructure.events.mappers.toAvro
import com.mengo.booking.payload.BookingCancelledPayload
import com.mengo.booking.payload.BookingConfirmedPayload
import com.mengo.booking.payload.BookingCreatedPayload
import com.mengo.kafka.test.KafkaTestContainerBase
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
        val booking = buildBookingCreatedEvent()
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
        val booking = buildBookingPaymentConfirmedEvent()
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
        val booking = buildBookingPaymentFailedEvent()
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
