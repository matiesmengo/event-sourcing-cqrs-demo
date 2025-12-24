package com.mengo.booking.domain.service

import com.mengo.booking.domain.model.eventstore.BookingAggregate
import com.mengo.booking.domain.model.eventstore.BookingEvent
import java.util.UUID

interface BookingEventStoreRepository {
    fun load(bookingId: UUID): BookingAggregate?

    fun append(event: BookingEvent)
}
