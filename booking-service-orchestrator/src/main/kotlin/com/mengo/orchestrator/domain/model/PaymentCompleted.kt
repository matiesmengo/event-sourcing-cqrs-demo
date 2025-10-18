package com.mengo.orchestrator.domain.model

import java.util.UUID

data class PaymentCompleted(
    val bookingId: UUID,
    val paymentId: UUID,
    val reference: String,
)
