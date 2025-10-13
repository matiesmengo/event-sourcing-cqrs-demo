package com.mengo.payment.infrastructure.events.mappers

import com.mengo.payment.domain.model.BookingPayment
import com.mengo.product.payload.ProductReservedPayload
import java.math.BigDecimal
import java.util.UUID

fun ProductReservedPayload.toDomain(): BookingPayment =
    BookingPayment(
        UUID.fromString(bookingId),
        UUID.fromString(productId),
        BigDecimal.TEN,
    )
