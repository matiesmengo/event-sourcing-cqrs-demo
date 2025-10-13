package com.mengo.payment.domain.service

import com.mengo.payment.domain.model.BookingPayment

fun interface PaymentService {
    fun onBookingReserved(bookingPayment: BookingPayment)
}
