package com.mengo.orchestrator.infrastructure.events.mapper

import com.mengo.orchestrator.domain.model.command.OrchestratorCommand
import com.mengo.orchestrator.domain.model.command.SagaCommand
import com.mengo.payload.orchestrator.OrchestratorReleaseStockPayload
import com.mengo.payload.orchestrator.OrchestratorRequestStockPayload
import com.mengo.payload.product.ProductReservationFailedPayload
import com.mengo.payload.product.ProductReservedPayload
import java.util.UUID

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
