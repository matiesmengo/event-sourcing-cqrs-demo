package com.mengo.booking.application

import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.model.CreateBooking
import com.mengo.booking.domain.model.FailedPayment
import com.mengo.booking.domain.model.SuccessPayment
import com.mengo.booking.domain.service.BookingEventPublisher
import com.mengo.booking.domain.service.BookingRepository
import com.mengo.booking.domain.service.BookingService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
open class BookingServiceAdapter(
    private val repository: BookingRepository,
    private val eventPublisher: BookingEventPublisher,
) : BookingService {
    @Transactional
    override fun createBooking(createBooking: CreateBooking): Booking {
        val saved = repository.save(createBooking)
        eventPublisher.publishBookingCreated(saved)
        return saved
    }

    @Transactional
    override fun onPaymentCompleted(payment: SuccessPayment) {
        val booking = repository.findById(payment.bookingId)
        val completedBooking = booking.confirm()
        repository.update(completedBooking)
    }

    @Transactional
    override fun onPaymentFailed(payment: FailedPayment) {
        val booking = repository.findById(payment.bookingId)
        val cancelledBooking = booking.cancel()
        repository.update(cancelledBooking)
    }
}
