package com.mengo.booking.infrastructure.persist

import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.model.CreateBooking
import com.mengo.booking.domain.service.BookingRepository
import com.mengo.booking.infrastructure.persist.mappers.toDomain
import com.mengo.booking.infrastructure.persist.mappers.toEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class BookingRepositoryService(
    private val bookingRepository: BookingJpaRepository,
) : BookingRepository {
    override fun save(createBooking: CreateBooking): Booking = bookingRepository.save(createBooking.toEntity()).toDomain()

    override fun findById(bookingId: UUID): Booking =
        bookingRepository
            .findById(bookingId)
            .map { it.toDomain() }
            .orElseThrow { RuntimeException("Booking with id $bookingId not found") }
}
