package com.mengo.booking.fixtures

import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.model.command.SagaCommand
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.PRODUCT_ID
import com.mengo.booking.fixtures.BookingConstants.PRODUCT_QUANTITY
import com.mengo.booking.fixtures.BookingConstants.USER_ID

object CommandTestData {
    fun buildSagaCommandBookingCreated() =
        SagaCommand.BookingCreated(
            bookingId = BOOKING_ID,
            userId = USER_ID,
            products =
                listOf(
                    BookingItem(
                        productId = PRODUCT_ID,
                        quantity = PRODUCT_QUANTITY,
                    ),
                ),
        )

    fun buildSagaCommandBookingConfirmed() =
        SagaCommand.BookingConfirmed(
            bookingId = BOOKING_ID,
        )

    fun buildSagaCommandBookingFailed() =
        SagaCommand.BookingFailed(
            bookingId = BOOKING_ID,
        )
}
