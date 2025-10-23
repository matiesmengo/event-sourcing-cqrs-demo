package com.mengo.orchestrator.fixtures

import com.mengo.orchestrator.fixtures.OrchestratorConstants.BOOKING_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PAYMENT_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PAYMENT_REASON
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PAYMENT_REFERENCE
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_PRICE
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_QUANTITY
import com.mengo.orchestrator.fixtures.OrchestratorConstants.USER_ID
import com.mengo.payload.booking.BookingCreatedPayload
import com.mengo.payload.booking.BookingProduct
import com.mengo.payload.payment.PaymentCompletedPayload
import com.mengo.payload.payment.PaymentFailedPayload
import com.mengo.payload.product.ProductReservationFailedPayload
import com.mengo.payload.product.ProductReservedPayload

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
            PRODUCT_PRICE,
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
}
