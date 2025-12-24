package com.mengo.booking.domain.model.command

import com.mengo.booking.domain.model.BookingItem
import java.util.UUID

sealed class BookingCommand {
    data class CreateBooking(
        val bookingId: UUID,
        val userId: UUID,
        val products: List<BookingItem>,
    ) : BookingCommand() {
        init {
            require(products.isNotEmpty()) {
                "Booking must contain at least one product."
            }
        }
    }

    data class BookingConfirmed(
        val bookingId: UUID,
    ) : BookingCommand()

    data class BookingFailed(
        val bookingId: UUID,
    ) : BookingCommand()
}
