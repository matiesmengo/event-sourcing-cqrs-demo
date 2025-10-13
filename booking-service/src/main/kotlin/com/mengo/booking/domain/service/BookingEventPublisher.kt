package com.mengo.booking.domain.service

import com.mengo.booking.domain.model.BookingCreatedEvent
import com.mengo.booking.domain.model.BookingPaymentConfirmedEvent
import com.mengo.booking.domain.model.BookingPaymentFailedEvent

interface BookingEventPublisher {
    fun publishBookingCreated(bookingCreated: BookingCreatedEvent)

    fun publishBookingCompleted(completedBooking: BookingPaymentConfirmedEvent)

    fun publishBookingFailed(failedBooking: BookingPaymentFailedEvent)
}
