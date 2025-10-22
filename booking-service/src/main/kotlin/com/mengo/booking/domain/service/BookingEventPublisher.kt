package com.mengo.booking.domain.service

import com.mengo.booking.domain.model.command.SagaCommand

interface BookingEventPublisher {
    fun publishBookingCreated(bookingCreated: SagaCommand.BookingCreated)

    fun publishBookingCompleted(completedBooking: SagaCommand.BookingConfirmed)

    fun publishBookingFailed(failedBooking: SagaCommand.BookingFailed)
}
