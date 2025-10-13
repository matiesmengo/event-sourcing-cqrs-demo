package com.mengo.booking.domain.service

import com.mengo.booking.domain.model.BookingCreatedEvent
import com.mengo.booking.domain.model.BookingPaymentConfirmedEvent
import com.mengo.booking.domain.model.BookingPaymentFailedEvent

interface BookingService {
    fun createBooking(createBooking: BookingCreatedEvent)

    fun onPaymentCompleted(completedBooking: BookingPaymentConfirmedEvent)

    fun onPaymentFailed(failedBooking: BookingPaymentFailedEvent)
}
