package com.mengo.booking.infrastructure.events.mappers

import com.mengo.booking.domain.model.command.BookingCommand
import com.mengo.payload.orchestrator.OrchestratorCancelBookingPayload
import com.mengo.payload.orchestrator.OrchestratorConfirmBookingPayload
import java.util.UUID

fun OrchestratorConfirmBookingPayload.toDomain(): BookingCommand.BookingConfirmed =
    BookingCommand.BookingConfirmed(bookingId = UUID.fromString(bookingId))

fun OrchestratorCancelBookingPayload.toDomain(): BookingCommand.BookingFailed =
    BookingCommand.BookingFailed(bookingId = UUID.fromString(bookingId))
