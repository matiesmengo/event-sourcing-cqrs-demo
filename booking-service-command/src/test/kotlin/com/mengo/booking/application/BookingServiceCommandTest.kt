package com.mengo.booking.application

import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.model.command.BookingCommand
import com.mengo.booking.domain.model.command.SagaCommand
import com.mengo.booking.domain.model.eventstore.BookingAggregate
import com.mengo.booking.domain.model.eventstore.BookingAggregateStatus
import com.mengo.booking.domain.model.eventstore.BookingConfirmedEvent
import com.mengo.booking.domain.model.eventstore.BookingCreatedEvent
import com.mengo.booking.domain.model.eventstore.BookingEvent
import com.mengo.booking.domain.model.eventstore.BookingFailedEvent
import com.mengo.booking.domain.service.BookingEventPublisher
import com.mengo.booking.domain.service.BookingEventStoreRepository
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID
import kotlin.test.assertEquals

class BookingServiceCommandTest {
    private val eventStoreRepository: BookingEventStoreRepository = mock()
    private val eventPublisher: BookingEventPublisher = mock()

    private val service = BookingServiceCommand(eventStoreRepository, eventPublisher)

    val products = listOf(BookingItem(UUID.randomUUID(), 2))

    @Test
    fun `onCreateBooking should append and publish when booking does not exist`() {
        // given
        whenever(eventStoreRepository.load(BOOKING_ID)).thenReturn(null)

        // when
        service.onCreateBooking(BookingCommand.CreateBooking(BOOKING_ID, USER_ID, products))

        // then
        argumentCaptor<BookingEvent>().apply {
            verify(eventStoreRepository).append(capture())
            val event = firstValue as BookingCreatedEvent
            assertEquals(BOOKING_ID, event.bookingId)
            assertEquals(USER_ID, event.userId)
            assertEquals(0, event.aggregateVersion)
        }

        argumentCaptor<SagaCommand.BookingCreated>().apply {
            verify(eventPublisher).publishBookingCreated(capture())
            val command = firstValue
            assertEquals(BOOKING_ID, command.bookingId)
            assertEquals(USER_ID, command.userId)
        }
    }

    @Test
    fun `onCreateBooking should throw when booking already exists`() {
        // given
        whenever(eventStoreRepository.load(BOOKING_ID)).thenReturn(
            BookingAggregate(BOOKING_ID, USER_ID, products, BookingAggregateStatus.CREATED, 0),
        )

        // when + then
        val ex =
            assertThrows<IllegalStateException> {
                service.onCreateBooking(BookingCommand.CreateBooking(BOOKING_ID, USER_ID, products))
            }

        assertTrue(ex.message!!.contains("already exists"))
        verify(eventStoreRepository, never()).append(any())
        verify(eventPublisher, never()).publishBookingCreated(any())
    }

    @Test
    fun `onPaymentCompleted should append confirmed event and publish`() {
        // given
        val aggregate = BookingAggregate(BOOKING_ID, USER_ID, products, BookingAggregateStatus.CREATED, 0)
        whenever(eventStoreRepository.load(BOOKING_ID)).thenReturn(aggregate)

        // when
        service.onPaymentCompleted(BookingCommand.BookingConfirmed(BOOKING_ID))

        // then
        argumentCaptor<BookingEvent>().apply {
            verify(eventStoreRepository).append(capture())
            val event = firstValue as BookingConfirmedEvent
            assertEquals(BOOKING_ID, event.bookingId)
            assertEquals(1, event.aggregateVersion)
        }

        verify(eventPublisher).publishBookingCompleted(SagaCommand.BookingConfirmed(BOOKING_ID))
    }

    @Test
    fun `onPaymentCompleted should throw when booking not found`() {
        // given
        whenever(eventStoreRepository.load(BOOKING_ID)).thenReturn(null)

        // when + then
        val ex =
            assertThrows<IllegalStateException> {
                service.onPaymentCompleted(BookingCommand.BookingConfirmed(BOOKING_ID))
            }

        assertTrue(ex.message!!.contains("doesn't exist"))
        verify(eventStoreRepository, never()).append(any())
        verify(eventPublisher, never()).publishBookingCompleted(any())
    }

    @Test
    fun `onPaymentFailed should append failed event and publish`() {
        // given
        val aggregate = BookingAggregate(BOOKING_ID, USER_ID, products, BookingAggregateStatus.CREATED, 0)
        whenever(eventStoreRepository.load(BOOKING_ID)).thenReturn(aggregate)

        // when
        service.onPaymentFailed(BookingCommand.BookingFailed(BOOKING_ID))

        // then
        argumentCaptor<BookingEvent>().apply {
            verify(eventStoreRepository).append(capture())
            val event = firstValue as BookingFailedEvent
            assertEquals(BOOKING_ID, event.bookingId)
            assertEquals(1, event.aggregateVersion)
        }

        verify(eventPublisher).publishBookingFailed(SagaCommand.BookingFailed(BOOKING_ID))
    }

    @Test
    fun `onPaymentFailed should throw when booking not found`() {
        // given
        whenever(eventStoreRepository.load(BOOKING_ID)).thenReturn(null)

        // when + then
        val ex =
            assertThrows<IllegalStateException> {
                service.onPaymentFailed(BookingCommand.BookingFailed(BOOKING_ID))
            }

        assertTrue(ex.message!!.contains("doesn't exist"))
        verify(eventStoreRepository, never()).append(any())
        verify(eventPublisher, never()).publishBookingFailed(any())
    }
}
