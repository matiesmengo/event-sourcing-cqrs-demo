package com.mengo.booking.domain.model

import java.util.UUID

data class CreateBooking(
    val userId: UUID,
    val resourceId: UUID,
)
