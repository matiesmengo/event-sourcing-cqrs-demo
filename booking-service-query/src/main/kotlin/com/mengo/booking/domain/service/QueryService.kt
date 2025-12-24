package com.mengo.booking.domain.service

import com.mengo.booking.domain.model.BookingReadModel
import java.util.UUID

fun interface QueryService {
    fun findBookingById(bookingId: UUID): BookingReadModel
}
