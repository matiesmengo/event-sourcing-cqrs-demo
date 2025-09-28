package com.mengo.booking.application

import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.model.BookingStatus
import com.mengo.booking.domain.model.CreateBooking
import com.mengo.booking.domain.model.FailedPayment
import com.mengo.booking.domain.model.SuccessPayment
import com.mengo.booking.domain.service.BookingEventPublisher
import com.mengo.booking.domain.service.BookingRepository
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.PAYMENT_ID
import com.mengo.booking.fixtures.BookingConstants.RESOURCE_ID
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class BookingServiceTest {
    private val repository: BookingRepository = mock()
    private val eventPublisher: BookingEventPublisher = mock()
    private val service = BookingService(repository, eventPublisher)

    @Test
    fun `createBooking should persisted and event is published`() {
        // given
        val createBooking = CreateBooking(USER_ID, RESOURCE_ID)
        val booking =
            Booking(
                BOOKING_ID,
                USER_ID,
                RESOURCE_ID,
                bookingStatus = BookingStatus.CREATED,
                createdAt = OffsetDateTime.now(),
            )

        whenever(repository.save(createBooking)).thenReturn(booking)

        // when
        val result = service.createBooking(createBooking)

        // then
        assertEquals(booking, result)
        verify(repository).save(createBooking)
        verify(eventPublisher).publishBookingCreated(booking)
    }

    @Test
    fun `onPaymentCompleted should update booking as paid`() {
        // given
        val booking = mock<Booking>()
        val confirmedBooking = mock<Booking>()

        whenever(repository.findById(BOOKING_ID)).thenReturn(booking)
        whenever(booking.confirm()).thenReturn(confirmedBooking)

        val payment = SuccessPayment(bookingId = BOOKING_ID, paymentId = PAYMENT_ID, reference = "ref-123")

        // when
        service.onPaymentCompleted(payment)

        // then
        verify(repository).findById(BOOKING_ID)
        verify(booking).confirm()
        verify(repository).update(confirmedBooking)
    }

    @Test
    fun `onPaymentFailed should update booking as canceled`() {
        // given
        val booking = mock<Booking>()
        val cancelledBooking = mock<Booking>()

        whenever(repository.findById(BOOKING_ID)).thenReturn(booking)
        whenever(booking.cancel()).thenReturn(cancelledBooking)

        val payment = FailedPayment(bookingId = BOOKING_ID, paymentId = PAYMENT_ID, reason = "Insufficient funds")

        // when
        service.onPaymentFailed(payment)

        // then
        verify(repository).findById(BOOKING_ID)
        verify(booking).cancel()
        verify(repository).update(cancelledBooking)
    }
}
