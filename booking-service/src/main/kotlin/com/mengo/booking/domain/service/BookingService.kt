package com.mengo.booking.domain.service

import com.mengo.booking.domain.model.BookingConfirmedEvent
import com.mengo.booking.domain.model.BookingCreatedEvent
import com.mengo.booking.domain.model.BookingFailedEvent

interface BookingService {
    fun createBooking(createBooking: BookingCreatedEvent)

    fun onPaymentCompleted(completedBooking: BookingConfirmedEvent)

    fun onPaymentFailed(failedBooking: BookingFailedEvent)
}
