package com.mengo.booking.infrastructure.events.mappers

import com.mengo.booking.domain.model.BookingConfirmedEvent
import com.mengo.booking.domain.model.BookingFailedEvent
import com.mengo.orchestrator.payload.OrchestratorCancelBookingPayload
import com.mengo.orchestrator.payload.OrchestratorConfirmBookingPayload
import java.util.UUID

fun OrchestratorConfirmBookingPayload.toDomain(): BookingConfirmedEvent =
    BookingConfirmedEvent(
        bookingId = UUID.fromString(bookingId),
    )

fun OrchestratorCancelBookingPayload.toDomain(): BookingFailedEvent =
    BookingFailedEvent(
        bookingId = UUID.fromString(bookingId),
    )
