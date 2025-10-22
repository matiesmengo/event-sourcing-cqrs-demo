package com.mengo.product.infrastructure.events.mappers

import com.mengo.product.domain.model.command.BookingCommand
import com.mengo.product.payload.ProductReservationFailedPayload
import com.mengo.product.payload.ProductReservedPayload
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.ByteBuffer

fun BookingCommand.Reserved.toAvro(): ProductReservedPayload =
    ProductReservedPayload(
        productId.toString(),
        bookingId.toString(),
        quantity,
        price.toAvroDecimal(),
    )

fun BookingCommand.ReservedFailed.toAvro(): ProductReservationFailedPayload =
    ProductReservationFailedPayload(
        productId.toString(),
        bookingId.toString(),
    )

// TODO: common toAvroDecimal mapper
fun BigDecimal.toAvroDecimal(scale: Int = 2): ByteBuffer {
    val scaled = this.setScale(scale, RoundingMode.HALF_UP)
    val unscaled = scaled.unscaledValue().toByteArray()
    return ByteBuffer.wrap(unscaled)
}
