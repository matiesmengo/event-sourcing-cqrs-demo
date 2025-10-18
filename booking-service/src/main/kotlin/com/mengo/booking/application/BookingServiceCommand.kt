package com.mengo.booking.application

import com.mengo.booking.domain.model.BookingConfirmedEvent
import com.mengo.booking.domain.model.BookingCreatedEvent
import com.mengo.booking.domain.model.BookingFailedEvent
import com.mengo.booking.domain.service.BookingEventPublisher
import com.mengo.booking.domain.service.BookingEventStoreRepository
import com.mengo.booking.domain.service.BookingService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
open class BookingServiceCommand(
    private val eventStoreRepository: BookingEventStoreRepository,
    private val eventPublisher: BookingEventPublisher,
) : BookingService {
    // TODO: refactor domain with command class
    // TODO: update projection after publish each topic

    @Transactional
    override fun createBooking(createBooking: BookingCreatedEvent) {
        eventStoreRepository.save(createBooking)
        eventPublisher.publishBookingCreated(createBooking)
    }

    @Transactional
    override fun onPaymentCompleted(completedBooking: BookingConfirmedEvent) {
        eventStoreRepository.save(completedBooking)
        eventPublisher.publishBookingCompleted(completedBooking)
    }

    @Transactional
    override fun onPaymentFailed(failedBooking: BookingFailedEvent) {
        eventStoreRepository.save(failedBooking)
        eventPublisher.publishBookingFailed(failedBooking)
    }
}
