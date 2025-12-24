package com.mengo.booking.application

import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.model.BookingReadModel
import com.mengo.booking.domain.model.BookingStatus
import com.mengo.booking.domain.service.BookingProjectionRepository
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BookingServiceQueryTest {
    private val repository: BookingProjectionRepository = mock()
    private val service = BookingServiceQuery(repository)

    @Test
    fun `should return booking with calculated total price when found`() {
        // given
        val items =
            mutableListOf(
                BookingItem(productId = UUID.randomUUID(), quantity = 2, price = BigDecimal("10.00")),
                BookingItem(productId = UUID.randomUUID(), quantity = 1, price = BigDecimal("25.50")),
                BookingItem(productId = UUID.randomUUID(), quantity = 3, price = null),
            )

        val booking =
            BookingReadModel(
                bookingId = BOOKING_ID,
                status = BookingStatus.CREATED,
                items = items,
            )

        whenever(repository.findById(BOOKING_ID)).thenReturn(booking)

        // when
        val result = service.findBookingById(BOOKING_ID)

        // then
        assertNotNull(result)
        assertEquals(BOOKING_ID, result.bookingId)
        // (2 * 10.00) + (1 * 25.50) = 45.50
        assertEquals(0, BigDecimal("45.50").compareTo(result.totalPrice))
    }

    @Test
    fun `should throw no such element exception when booking does not exist`() {
        // given
        whenever(repository.findById(BOOKING_ID)).thenReturn(null)

        // when then
        assertThrows<NoSuchElementException> {
            service.findBookingById(BOOKING_ID)
        }
    }

    @Test
    fun `should return zero total price when items list is empty`() {
        // given
        val booking =
            BookingReadModel(
                bookingId = BOOKING_ID,
                status = BookingStatus.CREATED,
                items = mutableListOf(),
            )
        whenever(repository.findById(BOOKING_ID)).thenReturn(booking)

        // when
        val result = service.findBookingById(BOOKING_ID)

        // then
        assertEquals(BigDecimal.ZERO, result.totalPrice)
    }
}
