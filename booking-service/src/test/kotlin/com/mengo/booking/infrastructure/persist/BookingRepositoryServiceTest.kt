package com.mengo.booking.infrastructure.persist

import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.model.BookingStatus
import com.mengo.booking.domain.model.CreateBooking
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.RESOURCE_ID
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import com.mengo.booking.infrastructure.persist.mappers.toDomain
import com.mengo.booking.infrastructure.persist.mappers.toEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BookingRepositoryServiceTest {
    private lateinit var bookingRepository: BookingJpaRepository
    private lateinit var service: BookingRepositoryService

    @BeforeEach
    fun setUp() {
        bookingRepository = mock()
        service = BookingRepositoryService(bookingRepository)
    }

    @Test
    fun `save should delegate to repository and return domain Booking`() {
        val createBooking = CreateBooking(userId = USER_ID, resourceId = RESOURCE_ID)
        val entity: BookingEntity = createBooking.toEntity()

        whenever(bookingRepository.save(any<BookingEntity>())).thenReturn(entity)

        service.save(createBooking)

        verify(bookingRepository).save(any<BookingEntity>())
    }

    @Test
    fun `update should delegate to repository and return domain Booking`() {
        val booking =
            Booking(
                userId = USER_ID,
                resourceId = RESOURCE_ID,
                bookingId = BOOKING_ID,
                bookingStatus = BookingStatus.PAID,
                createdAt = OffsetDateTime.now(),
                updatedAt = OffsetDateTime.now(),
            )
        val entity: BookingEntity = booking.toEntity()

        whenever(bookingRepository.save(any<BookingEntity>())).thenReturn(entity)

        service.update(booking)

        verify(bookingRepository).save(any<BookingEntity>())
    }

    @Test
    fun `findById should return Booking when repository finds it`() {
        val entity =
            BookingEntity(
                bookingId = BOOKING_ID,
                userId = USER_ID,
                resourceId = RESOURCE_ID,
                bookingStatus = BookingStatus.CREATED,
                createdAt = OffsetDateTime.now(),
            )
        val expectedBooking = entity.toDomain()

        whenever(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(entity))

        val result = service.findById(BOOKING_ID)

        assertEquals(expectedBooking, result)
        verify(bookingRepository).findById(BOOKING_ID)
    }

    @Test
    fun `findById should throw exception when booking not found`() {
        val bookingId = UUID.randomUUID()
        whenever(bookingRepository.findById(bookingId)).thenReturn(Optional.empty())

        val exception =
            assertFailsWith<RuntimeException> {
                service.findById(bookingId)
            }

        assertEquals("Booking with id $bookingId not found", exception.message)
        verify(bookingRepository).findById(bookingId)
    }
}
