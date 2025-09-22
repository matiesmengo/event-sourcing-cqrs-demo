package com.mengo.booking.domain.service

import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.model.CreateBooking
import java.util.UUID

interface BookingRepository {
    fun save(createBooking: CreateBooking): Booking

    fun findById(bookingId: UUID): Booking
}
