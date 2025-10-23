package com.mengo.orchestrator.infrastructure.events.mapper

import com.mengo.payload.booking.BookingCreatedPayload
import com.mengo.payload.booking.BookingProduct
import com.mengo.orchestrator.domain.model.Product
import com.mengo.orchestrator.domain.model.command.OrchestratorCommand
import com.mengo.orchestrator.domain.model.command.SagaCommand
import com.mengo.payload.orchestrator.OrchestratorCancelBookingPayload
import com.mengo.payload.orchestrator.OrchestratorConfirmBookingPayload
import java.util.UUID

fun SagaCommand.ConfirmBooking.toAvro(): OrchestratorConfirmBookingPayload =
    OrchestratorConfirmBookingPayload(
        bookingId.toString(),
    )

fun SagaCommand.CancelBooking.toAvro(): OrchestratorCancelBookingPayload =
    OrchestratorCancelBookingPayload(
        bookingId.toString(),
    )

fun BookingCreatedPayload.toDomain(): OrchestratorCommand.BookingCreated =
    OrchestratorCommand.BookingCreated(
        bookingId = UUID.fromString(bookingId),
        products = products.map { it.toDomain() }.toSet(),
    )

fun BookingProduct.toDomain(): Product =
    Product(
        productId = UUID.fromString(productId),
        quantity = quantity,
    )
