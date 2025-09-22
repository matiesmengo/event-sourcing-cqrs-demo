package com.mengo.booking.application

import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.model.CreateBooking
import com.mengo.booking.domain.service.BookingEventPublisher
import com.mengo.booking.domain.service.BookingRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class BookingService(
    private val repository: BookingRepository,
    private val eventPublisher: BookingEventPublisher,
) {
    @Transactional
    fun execute(createBooking: CreateBooking): Booking {
        val saved = repository.save(createBooking)
        eventPublisher.publishBookingCreated(saved)
        return saved
    }
}
