package com.mengo.booking.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_COMPLETED
import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_FAILED
import com.mengo.architecture.outbox.OutboxRepository
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.CommandTestData.buildSagaCommandBookingConfirmed
import com.mengo.booking.fixtures.CommandTestData.buildSagaCommandBookingCreated
import com.mengo.booking.fixtures.CommandTestData.buildSagaCommandBookingFailed
import com.mengo.booking.infrastructure.events.mappers.toAvro
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class BookingKafkaPublisherTest {
    private lateinit var outboxRepository: OutboxRepository
    private lateinit var publisher: BookingKafkaPublisher

    @BeforeEach
    fun setUp() {
        outboxRepository = mock()
        publisher = BookingKafkaPublisher(outboxRepository)
    }

    @Test
    fun `should publish BookingCreated event`() {
        // given
        val booking = buildSagaCommandBookingCreated()
        val avroBooking = booking.toAvro()

        // when
        publisher.publishBookingCreated(booking)

        // then
        verify(outboxRepository)
            .persistOutboxEvent(eq(KAFKA_BOOKING_CREATED), eq(BOOKING_ID.toString()), eq(avroBooking))
    }

    @Test
    fun `should publish BookingConfirmedEvent event`() {
        // given
        val booking = buildSagaCommandBookingConfirmed()
        val avroBooking = booking.toAvro()

        // when
        publisher.publishBookingCompleted(booking)

        // then
        verify(outboxRepository)
            .persistOutboxEvent(eq(KAFKA_BOOKING_COMPLETED), eq(BOOKING_ID.toString()), eq(avroBooking))
    }

    @Test
    fun `should publish BookingFailedEvent event`() {
        // given
        val booking = buildSagaCommandBookingFailed()
        val avroBooking = booking.toAvro()

        // when
        publisher.publishBookingFailed(booking)

        // then
        verify(outboxRepository)
            .persistOutboxEvent(eq(KAFKA_BOOKING_FAILED), eq(BOOKING_ID.toString()), eq(avroBooking))
    }
}
