package com.mengo.booking.domain.service

import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.model.CreateBooking
import com.mengo.booking.domain.model.FailedPayment
import com.mengo.booking.domain.model.SuccessPayment

interface BookingService {
    fun createBooking(createBooking: CreateBooking): Booking

    fun onPaymentCompleted(payment: SuccessPayment)

    fun onPaymentFailed(payment: FailedPayment)
}
