package com.mengo.booking.infrastructure.events

import com.example.booking.events.BookingCreated
import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.model.BookingStatus
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.RESOURCE_ID
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import com.mengo.booking.infrastructure.events.mappers.toAvro
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.kafka.core.KafkaTemplate
import java.time.OffsetDateTime

class KafkaBookingEventPublisherTest {
    private lateinit var kafkaTemplate: KafkaTemplate<String, BookingCreated>
    private lateinit var publisher: KafkaBookingEventPublisher

    @BeforeEach
    fun setUp() {
        kafkaTemplate = mock()
        publisher = KafkaBookingEventPublisher(kafkaTemplate)
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
