package com.mengo.payment.infrastructure.events.mappers

import com.mengo.payload.orchestrator.OrchestratorRequestPaymentPayload
import com.mengo.payment.domain.model.BookingPayment
import java.util.UUID

fun OrchestratorRequestPaymentPayload.toDomain(): BookingPayment =
    BookingPayment(
        UUID.fromString(bookingId),
        totalPrice,
    )
