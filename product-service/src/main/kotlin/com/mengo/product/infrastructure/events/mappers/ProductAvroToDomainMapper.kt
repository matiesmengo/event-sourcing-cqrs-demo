package com.mengo.product.infrastructure.events.mappers

import com.mengo.orchestrator.payload.OrchestratorRequestStockPayload
import com.mengo.product.domain.model.BookingProduct
import java.util.UUID

fun OrchestratorRequestStockPayload.toDomain(): BookingProduct =
    BookingProduct(
        bookingId = UUID.fromString(bookingId),
        productId = UUID.fromString(productId),
        quantity = quantity,
    )
