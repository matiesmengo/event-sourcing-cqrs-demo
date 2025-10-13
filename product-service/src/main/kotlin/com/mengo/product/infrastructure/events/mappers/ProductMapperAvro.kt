package com.mengo.product.infrastructure.events.mappers

import com.mengo.product.domain.model.ProductReservedEvent
import com.mengo.product.payload.ProductReservedPayload

fun ProductReservedEvent.toAvro(): ProductReservedPayload =
    ProductReservedPayload(
        productId.toString(),
        bookingId.toString(),
        quantity,
        createdAt.toString(),
    )
