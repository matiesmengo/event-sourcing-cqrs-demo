package com.mengo.booking.infrastructure.events

import com.mengo.booking.events.BookingCreatedEvent
import com.mengo.booking.fixtures.BookingTestData.buildBooking
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.booking.infrastructure.events.mappers.toAvro
import com.mengo.kafka.test.KafkaTestContainerBase
import java.time.Duration
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class BookingKafkaPublisherIntegrationTest : KafkaTestContainerBase() {
    @Autowired
    private lateinit var publisher: BookingKafkaPublisher

    @Test
    fun `should publish booking created event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_BOOKING_CREATED))
        val booking = buildBooking()
        val avroBooking = booking.toAvro()

        // when
        publisher.publishBookingCreated(booking)

        // then
        val records = kafkaConsumer.poll(Duration.ofSeconds(5))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroBooking.bookingId.toString(), record.key())
        assertEquals(avroBooking.bookingId, (record.value() as BookingCreatedEvent).bookingId)
    }
}
