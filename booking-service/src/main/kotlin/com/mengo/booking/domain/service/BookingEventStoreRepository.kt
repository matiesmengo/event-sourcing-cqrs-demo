package com.mengo.booking.domain.service

import com.mengo.booking.domain.model.BookingEvent
import java.util.UUID

interface BookingEventStoreRepository {
    fun save(bookingEvent: BookingEvent)

    fun findById(bookingId: UUID): BookingEvent?
}
