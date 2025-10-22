package com.mengo.booking.domain.model.command

import com.mengo.booking.domain.model.BookingItem
import java.util.UUID

sealed class SagaCommand {
    data class BookingCreated(
        val bookingId: UUID,
        val userId: UUID,
        val products: List<BookingItem>,
    ) : SagaCommand()

    data class BookingConfirmed(
        val bookingId: UUID,
    ) : SagaCommand()

    data class BookingFailed(
        val bookingId: UUID,
    ) : SagaCommand()
}
