package com.mengo.booking.domain.service

import com.mengo.booking.domain.model.BookingConfirmedEvent
import com.mengo.booking.domain.model.BookingCreatedEvent
import com.mengo.booking.domain.model.BookingFailedEvent

interface BookingEventPublisher {
    fun publishBookingCreated(bookingCreated: BookingCreatedEvent)

    fun publishBookingCompleted(completedBooking: BookingConfirmedEvent)

    fun publishBookingFailed(failedBooking: BookingFailedEvent)
}
