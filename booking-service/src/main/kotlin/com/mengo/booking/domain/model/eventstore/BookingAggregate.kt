package com.mengo.booking.domain.model.eventstore

import com.mengo.booking.domain.model.BookingItem
import java.util.UUID

enum class BookingAggregateStatus { CREATED, CONFIRMED, FAILED }

data class BookingAggregate(
    val bookingId: UUID,
    val userId: UUID,
    val products: List<BookingItem>,
    val status: BookingAggregateStatus,
    val lastEventVersion: Int,
) {
    companion object {
        fun create(
            bookingId: UUID,
            userId: UUID,
            products: List<BookingItem>,
        ): BookingCreatedEvent =
            BookingCreatedEvent(
                bookingId = bookingId,
                userId = userId,
                products = products,
                aggregateVersion = 0,
            )

        fun rehydrate(events: List<BookingEvent>): BookingAggregate {
            require(events.isNotEmpty()) { "No events provided for rehydration" }

            return events
                .sortedBy { it.aggregateVersion }
                .fold(null as BookingAggregate?) { current, event -> current?.applyEvent(event) ?: fromEvent(event) }
                ?: throw IllegalStateException("Failed to rehydrate BookingAggregate")
        }

        private fun fromEvent(event: BookingEvent): BookingAggregate =
            when (event) {
                is BookingCreatedEvent ->
                    BookingAggregate(
                        bookingId = event.bookingId,
                        userId = event.userId,
                        products = event.products,
                        status = BookingAggregateStatus.CREATED,
                        lastEventVersion = event.aggregateVersion,
                    )
                else -> error("Unsupported initial event type: ${event::class.simpleName}")
            }
    }

    private fun applyEvent(event: BookingEvent): BookingAggregate =
        when (event) {
            is BookingCreatedEvent -> fromEvent(event)

            is BookingConfirmedEvent -> {
                copy(status = BookingAggregateStatus.CONFIRMED, lastEventVersion = event.aggregateVersion)
            }

            is BookingFailedEvent -> {
                copy(status = BookingAggregateStatus.FAILED, lastEventVersion = event.aggregateVersion)
            }
        }

    fun confirmedBooking(): BookingConfirmedEvent {
        if (status == BookingAggregateStatus.CREATED) {
            return BookingConfirmedEvent(
                bookingId = bookingId,
                aggregateVersion = lastEventVersion + 1,
            )
        }
        throw IllegalStateException("Cannot mark as confirmed a booking in state $status")
    }

    fun failedBooking(): BookingFailedEvent {
        if (status == BookingAggregateStatus.CREATED) {
            return BookingFailedEvent(
                bookingId = bookingId,
                aggregateVersion = lastEventVersion + 1,
            )
        }
        throw IllegalStateException("Cannot mark as failed a booking in state $status")
    }
}
