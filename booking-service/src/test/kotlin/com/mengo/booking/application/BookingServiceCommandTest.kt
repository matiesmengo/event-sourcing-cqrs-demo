package com.mengo.booking.application

import com.mengo.booking.domain.model.BookingConfirmedEvent
import com.mengo.booking.domain.model.BookingCreatedEvent
import com.mengo.booking.domain.model.BookingFailedEvent
import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.service.BookingEventPublisher
import com.mengo.booking.domain.service.BookingEventStoreRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.UUID
import kotlin.test.assertEquals

class BookingServiceCommandTest {
    private val eventStoreRepository: BookingEventStoreRepository = mock()
    private val eventPublisher: BookingEventPublisher = mock()

    private val service = BookingServiceCommand(eventStoreRepository, eventPublisher)

    private val bookingId = UUID.randomUUID()
    private val userId = UUID.randomUUID()

    @Test
    fun `createBooking should persist and publish BookingCreatedEvent`() {
        // given
        val products = listOf(BookingItem(UUID.randomUUID(), 2))
        val event =
            BookingCreatedEvent(
                bookingId = bookingId,
                userId = userId,
                products = products,
                aggregateVersion = 1,
            )

        // when
        service.createBooking(event)

        // then
        verify(eventStoreRepository).save(
            argThat { e ->
                val ev = e as BookingCreatedEvent
                assertEquals(bookingId, ev.bookingId)
                assertEquals(userId, ev.userId)
                assertEquals(products, ev.products)
                assertEquals(1, ev.aggregateVersion)
                true
            },
        )
        verify(eventPublisher).publishBookingCreated(any())
    }

    @Test
    fun `onPaymentCompleted should persist and publish BookingPaymentConfirmedEvent`() {
        // given
        val event =
            BookingConfirmedEvent(
                bookingId = bookingId,
                aggregateVersion = 2,
            )

        // when
        service.onPaymentCompleted(event)

        // then
        verify(eventStoreRepository).save(
            argThat { e ->
                val ev = e as BookingConfirmedEvent
                assertEquals(bookingId, ev.bookingId)
                assertEquals(2, ev.aggregateVersion)
                true
            },
        )
        verify(eventPublisher).publishBookingCompleted(any())
    }

    @Test
    fun `onPaymentFailed should persist and publish BookingPaymentFailedEvent`() {
        // given
        val event =
            BookingFailedEvent(
                bookingId = bookingId,
                aggregateVersion = 2,
            )

        // when
        service.onPaymentFailed(event)

        // then
        verify(eventStoreRepository).save(
            argThat { e ->
                val ev = e as BookingFailedEvent
                assertEquals(bookingId, ev.bookingId)
                assertEquals(2, ev.aggregateVersion)
                true
            },
        )
        verify(eventPublisher).publishBookingFailed(any())
    }
}
