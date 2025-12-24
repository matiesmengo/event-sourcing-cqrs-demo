package com.mengo.booking.infrastructure.api.mappers

import com.mengo.api.booking.model.BookingRetrievedResponse
import com.mengo.api.booking.model.Product
import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.model.BookingReadModel

fun BookingReadModel.toApi(): BookingRetrievedResponse =
    BookingRetrievedResponse()
        .bookingId(bookingId)
        .userId(userId)
        .products(items.map { it.toApi() })
        .status(status.toString())
        .totalPrice(totalPrice)

private fun BookingItem.toApi(): Product =
    Product()
        .productId(productId)
        .quantity(quantity)
        .price(price)
