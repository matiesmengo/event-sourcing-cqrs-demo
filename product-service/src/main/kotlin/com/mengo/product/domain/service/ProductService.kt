package com.mengo.product.domain.service

import com.mengo.product.domain.model.Booking

fun interface ProductService {
    fun onBookingCreated(booking: Booking)
}
