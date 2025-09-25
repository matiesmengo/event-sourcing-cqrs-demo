package com.mengo.booking.infrastructure.events

import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.model.BookingStatus
import com.mengo.booking.fixtures.BookingTestData.BOOKING_ID
import com.mengo.booking.fixtures.BookingTestData.RESOURCE_ID
import com.mengo.booking.fixtures.BookingTestData.USER_ID
import com.mengo.booking.infrastructure.events.mappers.toAvro
import org.apache.avro.specific.SpecificRecord
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.kafka.core.KafkaTemplate
import java.time.OffsetDateTime

class KafkaBookingEventPublisherTest {
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
        val booking =
            Booking(
                bookingId = BOOKING_ID,
                userId = USER_ID,
                resourceId = RESOURCE_ID,
                bookingStatus = BookingStatus.CREATED,
                createdAt = OffsetDateTime.now(),
            )
        val avroBooking = booking.toAvro()

        // when
        publisher.publishBookingCreated(booking)

        // then
        verify(kafkaTemplate)
            .send(eq("booking.created"), eq(BOOKING_ID.toString()), eq(avroBooking))
    }
}
