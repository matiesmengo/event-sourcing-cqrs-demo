package com.mengo.booking.domain.service

import com.mengo.booking.domain.model.command.BookingCommand

interface BookingService {
    fun onCreateBooking(command: BookingCommand.CreateBooking)

    fun onPaymentCompleted(command: BookingCommand.BookingConfirmed)

    fun onPaymentFailed(command: BookingCommand.BookingFailed)
}
