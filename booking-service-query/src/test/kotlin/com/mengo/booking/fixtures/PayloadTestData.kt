package com.mengo.booking.fixtures

import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.PAYMENT_ID
import com.mengo.booking.fixtures.BookingConstants.PAYMENT_REASON
import com.mengo.booking.fixtures.BookingConstants.PAYMENT_REFERENCE
import com.mengo.booking.fixtures.BookingConstants.PRODUCT_ID
import com.mengo.booking.fixtures.BookingConstants.PRODUCT_PRICE
import com.mengo.booking.fixtures.BookingConstants.PRODUCT_QUANTITY
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import com.mengo.payload.booking.BookingCancelledPayload
import com.mengo.payload.booking.BookingConfirmedPayload
import com.mengo.payload.booking.BookingCreatedPayload
import com.mengo.payload.booking.BookingProduct
import com.mengo.payload.payment.PaymentCompletedPayload
import com.mengo.payload.payment.PaymentFailedPayload
import com.mengo.payload.product.ProductReservedPayload

object PayloadTestData {
    fun buildBookingCreatedPayload() =
        BookingCreatedPayload(
            BOOKING_ID.toString(),
            USER_ID.toString(),
            listOf(buildBookingProduct()),
        )

    fun buildBookingProduct() =
        BookingProduct(
            PRODUCT_ID.toString(),
            PRODUCT_QUANTITY,
        )

    fun buildProductReservedPayload() =
        ProductReservedPayload(
            PRODUCT_ID.toString(),
            BOOKING_ID.toString(),
            PRODUCT_QUANTITY,
            PRODUCT_PRICE,
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

    fun buildBookingCancelledPayload() = BookingCancelledPayload(BOOKING_ID.toString(), PAYMENT_REASON)

    fun buildBookingConfirmedPayload() = BookingConfirmedPayload(BOOKING_ID.toString())
}
