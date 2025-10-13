package com.mengo.payment.domain.model

import java.math.BigDecimal
import java.util.UUID

data class BookingPayment(
    val bookingId: UUID,
    val productId: UUID,
    val price: BigDecimal,
)
