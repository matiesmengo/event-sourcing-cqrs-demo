package com.mengo.orchestrator.infrastructure.events.mapper

import com.mengo.orchestrator.domain.model.command.OrchestratorCommand
import com.mengo.orchestrator.domain.model.command.SagaCommand
import com.mengo.payload.orchestrator.OrchestratorReleaseStockPayload
import com.mengo.payload.orchestrator.OrchestratorRequestStockPayload
import com.mengo.payload.product.ProductReservationFailedPayload
import com.mengo.payload.product.ProductReservedPayload
import org.apache.avro.specific.SpecificRecord
import java.util.UUID

// TODO: refactor mappers
fun SagaCommand.toAvro(): SpecificRecord =
    when (this) {
        is SagaCommand.RequestStock -> this.toAvro()
        is SagaCommand.ReleaseStock -> this.toAvro()
        is SagaCommand.CancelBooking -> this.toAvro()
        is SagaCommand.ConfirmBooking -> this.toAvro()
        is SagaCommand.RequestPayment -> this.toAvro()
    }

fun SagaCommand.RequestStock.toAvro(): OrchestratorRequestStockPayload =
    OrchestratorRequestStockPayload(
        bookingId.toString(),
        productId.toString(),
        quantity,
    )

fun SagaCommand.ReleaseStock.toAvro(): OrchestratorReleaseStockPayload =
    OrchestratorReleaseStockPayload(
        bookingId.toString(),
        productId.toString(),
        quantity,
    )

fun ProductReservedPayload.toDomain(): OrchestratorCommand.ProductReserved =
    OrchestratorCommand.ProductReserved(
        bookingId = UUID.fromString(bookingId),
        productId = UUID.fromString(productId),
        quantity = quantity,
        price = price,
    )

fun ProductReservationFailedPayload.toDomain(): OrchestratorCommand.ProductReservationFailed =
    OrchestratorCommand.ProductReservationFailed(
        bookingId = UUID.fromString(bookingId),
        productId = UUID.fromString(productId),
    )
