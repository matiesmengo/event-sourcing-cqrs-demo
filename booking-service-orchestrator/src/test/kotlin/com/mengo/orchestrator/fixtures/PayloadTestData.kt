package com.mengo.orchestrator.fixtures

import com.mengo.booking.payload.BookingCreatedPayload
import com.mengo.booking.payload.BookingProduct
import com.mengo.orchestrator.fixtures.OrchestratorConstants.BOOKING_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PAYMENT_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PAYMENT_REASON
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PAYMENT_REFERENCE
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_PRICE
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_QUANTITY
import com.mengo.orchestrator.fixtures.OrchestratorConstants.USER_ID
import com.mengo.payment.payload.PaymentCompletedPayload
import com.mengo.payment.payload.PaymentFailedPayload
import com.mengo.product.payload.ProductReservationFailedPayload
import com.mengo.product.payload.ProductReservedPayload
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.ByteBuffer

object PayloadTestData {
    fun buildBookingProduct() =
        BookingProduct(
            PRODUCT_ID.toString(),
            PRODUCT_QUANTITY,
        )

    fun buildBookingCreatedPayload() =
        BookingCreatedPayload(
            BOOKING_ID.toString(),
            USER_ID.toString(),
            listOf(buildBookingProduct()),
        )

    fun buildProductReservedPayload() =
        ProductReservedPayload(
            PRODUCT_ID.toString(),
            BOOKING_ID.toString(),
            PRODUCT_QUANTITY,
            PRODUCT_PRICE.toAvroDecimal(),
        )

    fun buildProductReservationFailedPayload() =
        ProductReservationFailedPayload(
            PRODUCT_ID.toString(),
            BOOKING_ID.toString(),
        )

    fun buildPaymentCompletedPayload() =
        PaymentCompletedPayload(
            PAYMENT_ID.toString(),
            BOOKING_ID.toString(),
            PAYMENT_REFERENCE,
        )

    fun buildPaymentFailedPayload() =
        PaymentFailedPayload(
            PAYMENT_ID.toString(),
            BOOKING_ID.toString(),
            PAYMENT_REASON,
        )

    // TODO: migrate
    fun BigDecimal.toAvroDecimal(scale: Int = 2): ByteBuffer {
        val scaled = this.setScale(scale, RoundingMode.HALF_UP)
        val unscaled = scaled.unscaledValue().toByteArray()
        return ByteBuffer.wrap(unscaled)
    }
}
