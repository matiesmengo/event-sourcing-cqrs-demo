package com.mengo.product.fixtures

import com.mengo.product.domain.model.BookingCommand
import com.mengo.product.fixtures.ProductConstants.BOOKING_ID
import com.mengo.product.fixtures.ProductConstants.PRODUCT_ID
import com.mengo.product.fixtures.ProductConstants.PRODUCT_PRICE
import com.mengo.product.fixtures.ProductConstants.PRODUCT_QUANTITY
import java.math.BigDecimal
import java.util.UUID

object CommandTestData {
    fun buildBookingCommandReserved(
        productId: UUID = PRODUCT_ID,
        bookingId: UUID = BOOKING_ID,
        quantity: Int = PRODUCT_QUANTITY,
        price: BigDecimal = PRODUCT_PRICE,
    ): BookingCommand.Reserved =
        BookingCommand.Reserved(
            productId = productId,
            bookingId = bookingId,
            quantity = quantity,
            price = price,
        )

    fun buildBookingCommandReservedFailed(
        productId: UUID = PRODUCT_ID,
        bookingId: UUID = BOOKING_ID,
    ): BookingCommand.ReservedFailed =
        BookingCommand.ReservedFailed(
            productId = productId,
            bookingId = bookingId,
        )
}
