package com.mengo.booking.infrastructure.persist

import com.mengo.booking.domain.model.BookingStatus
import com.mengo.booking.domain.model.CreateBooking
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingTestData.buildBooking
import com.mengo.postgres.test.PostgresTestContainerBase
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class BookingRepositoryServiceIntegrationTest : PostgresTestContainerBase() {
    @Autowired
    private lateinit var bookingRepositoryService: BookingRepositoryService

    @Test
    fun `save and findById should persist and return booking`() {
       // given
        val booking = buildBooking()

        // when
        val saved =
            bookingRepositoryService.save(
                CreateBooking(
                    userId = booking.userId,
                    resourceId = booking.resourceId,
                ),
            )

        val fetched = bookingRepositoryService.findById(saved.bookingId)

        // then
        assertEquals(saved.bookingId, fetched.bookingId)
        assertEquals(saved.userId, fetched.userId)
        assertEquals(saved.resourceId, fetched.resourceId)
        assertEquals(saved.bookingStatus, BookingStatus.CREATED)
    }

    @Test
    fun `update should modify existing booking`() {
        val booking = buildBooking()
        val saved =
            bookingRepositoryService.save(
                CreateBooking(
                    userId = booking.userId,
                    resourceId = booking.resourceId,
                ),
            )

        val updatedBooking = saved.copy(bookingStatus = BookingStatus.CANCELLED)
        val updated = bookingRepositoryService.update(updatedBooking)
        val fetched = bookingRepositoryService.findById(updated.bookingId)

        assertEquals(BookingStatus.CANCELLED, fetched.bookingStatus)
    }

    @Test
    fun `findById should throw when booking does not exist`() {
        val exception =
            assertThrows<RuntimeException> {
                bookingRepositoryService.findById(BOOKING_ID)
            }
        assertTrue(exception.message!!.contains("Booking with id $BOOKING_ID not found"))
    }
}
