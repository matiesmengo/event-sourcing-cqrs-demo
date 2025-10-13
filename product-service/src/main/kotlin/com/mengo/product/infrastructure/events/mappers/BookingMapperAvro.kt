package com.mengo.product.infrastructure.events.mappers

import com.mengo.booking.payload.BookingCreatedPayload
import com.mengo.product.domain.model.Booking
import com.mengo.product.domain.model.BookingProduct
import java.util.UUID
import com.mengo.booking.payload.BookingProduct as AvroBookingProduct

fun BookingCreatedPayload.toDomain(): Booking =
    Booking(
        bookingId = UUID.fromString(bookingId),
        products = products.map { it.toDomain() },
    )

fun AvroBookingProduct.toDomain(): BookingProduct =
    BookingProduct(
        productId = UUID.fromString(productId),
        quantity = quantity,
    )
