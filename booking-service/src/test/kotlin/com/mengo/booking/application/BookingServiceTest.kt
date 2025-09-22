package com.mengo.booking.application

import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.model.BookingStatus
import com.mengo.booking.domain.model.CreateBooking
import com.mengo.booking.domain.service.BookingEventPublisher
import com.mengo.booking.domain.service.BookingRepository
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.RESOURCE_ID
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime

class BookingServiceTest {
    private val repository: BookingRepository = mock()
    private val eventPublisher: BookingEventPublisher = mock()
    private val service = BookingService(repository, eventPublisher)

    @Test
    fun `when booking is created it is saved and event is published`() {
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
        val result = service.execute(createBooking)

        // then
        assertEquals(booking, result)
        verify(repository).save(createBooking)
        verify(eventPublisher).publishBookingCreated(booking)
    }
}
