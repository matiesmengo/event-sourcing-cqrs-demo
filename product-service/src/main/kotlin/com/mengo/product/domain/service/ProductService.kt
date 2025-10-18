package com.mengo.product.domain.service

import com.mengo.product.domain.model.BookingProduct

fun interface ProductService {
    fun onBookingCreated(product: BookingProduct)
}
