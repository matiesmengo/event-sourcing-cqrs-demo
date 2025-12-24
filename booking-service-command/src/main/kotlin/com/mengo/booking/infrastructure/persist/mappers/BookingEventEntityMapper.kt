package com.mengo.booking.infrastructure.persist.mappers

import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.booking.domain.model.eventstore.BookingConfirmedEvent
import com.mengo.booking.domain.model.eventstore.BookingCreatedEvent
import com.mengo.booking.domain.model.eventstore.BookingEvent
import com.mengo.booking.domain.model.eventstore.BookingFailedEvent
import com.mengo.booking.infrastructure.persist.BookingEventEntity
import org.springframework.stereotype.Component

@Component
class BookingEventEntityMapper(
    private val objectMapper: ObjectMapper,
) {
    fun toEntity(event: BookingEvent): BookingEventEntity =
        when (event) {
            is BookingCreatedEvent -> event.toEntity()
            is BookingConfirmedEvent -> event.toEntity()
            is BookingFailedEvent -> event.toEntity()
        }

    fun BookingCreatedEvent.toEntity(): BookingEventEntity =
        BookingEventEntity(
            bookingId = this.bookingId,
            eventType = "BookingCreatedEvent",
            eventData = objectMapper.writeValueAsString(this),
            aggregateVersion = this.aggregateVersion,
        )

    fun BookingConfirmedEvent.toEntity(): BookingEventEntity =
        BookingEventEntity(
            bookingId = this.bookingId,
            eventType = "BookingConfirmedEvent",
            eventData = objectMapper.writeValueAsString(this),
            aggregateVersion = this.aggregateVersion,
        )

    fun BookingFailedEvent.toEntity(): BookingEventEntity =
        BookingEventEntity(
            bookingId = this.bookingId,
            eventType = "BookingFailedEvent",
            eventData = objectMapper.writeValueAsString(this),
            aggregateVersion = this.aggregateVersion,
        )

    fun toDomain(event: BookingEventEntity): BookingEvent =
        when (event.eventType) {
            "BookingCreatedEvent" -> objectMapper.readValue(event.eventData, BookingCreatedEvent::class.java)
            "BookingConfirmedEvent" -> objectMapper.readValue(event.eventData, BookingConfirmedEvent::class.java)
            "BookingFailedEvent" -> objectMapper.readValue(event.eventData, BookingFailedEvent::class.java)
            else -> throw IllegalArgumentException("Unknown PaymentEvent type: ${event.eventType}")
        }
}
