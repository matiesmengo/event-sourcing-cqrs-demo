package com.mengo.booking.infrastructure.events

import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingTestData.buildBookingCreatedEvent
import com.mengo.booking.infrastructure.events.mappers.toAvro
import org.apache.avro.specific.SpecificRecord
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.kafka.core.KafkaTemplate

class BookingKafkaPublisherTest {
    private lateinit var kafkaTemplate: KafkaTemplate<String, SpecificRecord>
    private lateinit var publisher: BookingKafkaPublisher

    @BeforeEach
    fun setUp() {
        kafkaTemplate = mock()
        publisher = BookingKafkaPublisher(kafkaTemplate)
    }

    @Test
    fun `should publish BookingCreated event`() {
        // given
        val booking = buildBookingCreatedEvent()
        val avroBooking = booking.toAvro()

        // when
        publisher.publishBookingCreated(booking)

        // then
        verify(kafkaTemplate)
            .send(eq("booking.created"), eq(BOOKING_ID.toString()), eq(avroBooking))
    }
}
