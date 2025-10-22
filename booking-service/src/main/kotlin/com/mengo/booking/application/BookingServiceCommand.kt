package com.mengo.booking.application

import com.mengo.booking.domain.model.command.BookingCommand
import com.mengo.booking.domain.model.command.SagaCommand
import com.mengo.booking.domain.model.eventstore.BookingAggregate
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
    // TODO: update projection after publish each topic
    // TODO: handle custom errors

    @Transactional
    override fun onCreateBooking(command: BookingCommand.CreateBooking) {
        if (eventStoreRepository.load(command.bookingId) != null) error("This booking already exists")

        eventStoreRepository.append(
            BookingAggregate.create(
                bookingId = command.bookingId,
                userId = command.userId,
                products = command.products,
            ),
        )

        eventPublisher.publishBookingCreated(
            SagaCommand.BookingCreated(
                bookingId = command.bookingId,
                userId = command.userId,
                products = command.products,
            ),
        )
    }

    @Transactional
    override fun onPaymentCompleted(command: BookingCommand.BookingConfirmed) {
        val aggregate = eventStoreRepository.load(command.bookingId) ?: error("This booking doesn't exist")

        eventStoreRepository.append(aggregate.confirmedBooking())
        eventPublisher.publishBookingCompleted(SagaCommand.BookingConfirmed(command.bookingId))
    }

    @Transactional
    override fun onPaymentFailed(command: BookingCommand.BookingFailed) {
        val aggregate = eventStoreRepository.load(command.bookingId) ?: error("This booking doesn't exist")

        eventStoreRepository.append(aggregate.failedBooking())
        eventPublisher.publishBookingFailed(SagaCommand.BookingFailed(command.bookingId))
    }
}
