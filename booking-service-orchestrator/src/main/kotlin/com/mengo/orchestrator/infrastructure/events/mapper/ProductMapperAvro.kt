package com.mengo.orchestrator.infrastructure.events.mapper

import com.mengo.orchestrator.domain.model.command.OrchestratorCommand
import com.mengo.orchestrator.domain.model.command.SagaCommand
import com.mengo.orchestrator.payload.OrchestratorReleaseStockPayload
import com.mengo.orchestrator.payload.OrchestratorRequestStockPayload
import com.mengo.product.payload.ProductReservationFailedPayload
import com.mengo.product.payload.ProductReservedPayload
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer
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
        price = price.toBigDecimal(),
    )

fun ProductReservationFailedPayload.toDomain(): OrchestratorCommand.ProductReservationFailed =
    OrchestratorCommand.ProductReservationFailed(
        bookingId = UUID.fromString(bookingId),
        productId = UUID.fromString(productId),
    )

fun ByteBuffer.toBigDecimal(scale: Int = 2): BigDecimal {
    val bytes = ByteArray(this.remaining())
    this.get(bytes)
    return BigDecimal(BigInteger(bytes), scale)
}
