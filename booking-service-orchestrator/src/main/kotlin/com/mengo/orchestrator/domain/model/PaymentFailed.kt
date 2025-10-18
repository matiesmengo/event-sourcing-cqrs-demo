package com.mengo.orchestrator.domain.model

import java.util.UUID

data class PaymentFailed(
    val bookingId: UUID,
    val paymentId: UUID,
    val reason: String? = null,
)
