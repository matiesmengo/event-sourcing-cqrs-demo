package com.mengo.booking.infrastructure.persist.mappers

import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.booking.domain.model.BookingConfirmedEvent
import com.mengo.booking.domain.model.BookingCreatedEvent
import com.mengo.booking.domain.model.BookingEvent
import com.mengo.booking.domain.model.BookingFailedEvent
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
            eventType = "BookingPaymentConfirmedEvent",
            eventData = objectMapper.writeValueAsString(this),
            aggregateVersion = this.aggregateVersion,
        )

    fun BookingFailedEvent.toEntity(): BookingEventEntity =
        BookingEventEntity(
            bookingId = this.bookingId,
            eventType = "BookingPaymentFailedEvent",
            eventData = objectMapper.writeValueAsString(this),
            aggregateVersion = this.aggregateVersion,
        )

    fun toDomain(event: BookingEventEntity): BookingEvent =
        when (event.eventType) {
            "BookingCreatedEvent" -> objectMapper.readValue(event.eventData, BookingCreatedEvent::class.java)
            "BookingPaymentConfirmedEvent" -> objectMapper.readValue(event.eventData, BookingConfirmedEvent::class.java)
            "BookingPaymentFailedEvent" -> objectMapper.readValue(event.eventData, BookingFailedEvent::class.java)
            else -> throw IllegalArgumentException("Unknown PaymentEvent type: ${event.eventType}")
        }
}
