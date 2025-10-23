package com.mengo.product.infrastructure.events.mappers

import com.mengo.payload.orchestrator.OrchestratorReleaseStockPayload
import com.mengo.payload.orchestrator.OrchestratorRequestStockPayload
import com.mengo.product.domain.model.command.SagaCommand
import java.util.UUID

fun OrchestratorRequestStockPayload.toDomain(): SagaCommand.ReserveProduct =
    SagaCommand.ReserveProduct(
        bookingId = UUID.fromString(bookingId),
        productId = UUID.fromString(productId),
        quantity = quantity,
    )

fun OrchestratorReleaseStockPayload.toDomain(): SagaCommand.ReleaseProduct =
    SagaCommand.ReleaseProduct(
        bookingId = UUID.fromString(bookingId),
        productId = UUID.fromString(productId),
        quantity = quantity,
    )
