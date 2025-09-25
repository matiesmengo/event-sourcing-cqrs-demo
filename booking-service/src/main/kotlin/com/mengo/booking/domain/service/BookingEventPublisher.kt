package com.mengo.booking.domain.service

import com.mengo.booking.domain.model.Booking

fun interface BookingEventPublisher {
    fun publishBookingCreated(booking: Booking)
}
