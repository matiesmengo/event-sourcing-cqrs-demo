package com.mengo.booking.application

import com.mengo.booking.domain.model.command.BookingCommand
import com.mengo.booking.domain.model.command.SagaCommand
import com.mengo.booking.domain.model.eventstore.BookingAggregate
import com.mengo.booking.domain.service.BookingEventPublisher
import com.mengo.booking.domain.service.BookingEventStoreRepository
import com.mengo.booking.domain.service.BookingService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
open class BookingServiceCommand(
    private val eventStoreRepository: BookingEventStoreRepository,
    private val eventPublisher: BookingEventPublisher,
) : BookingService {
    // TODO: update projection after publish each topic
    // TODO: handle custom errors
    // TODO: Transactional required for rest clients?

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

    @Transactional(propagation = Propagation.REQUIRED)
    override fun onPaymentCompleted(command: BookingCommand.BookingConfirmed) {
        val aggregate = eventStoreRepository.load(command.bookingId) ?: error("This booking doesn't exist")

        eventStoreRepository.append(aggregate.confirmedBooking())
        eventPublisher.publishBookingCompleted(SagaCommand.BookingConfirmed(command.bookingId))
    }

    @Transactional(propagation = Propagation.REQUIRED)
    override fun onPaymentFailed(command: BookingCommand.BookingFailed) {
        val aggregate = eventStoreRepository.load(command.bookingId) ?: error("This booking doesn't exist")

        eventStoreRepository.append(aggregate.failedBooking())
        eventPublisher.publishBookingFailed(SagaCommand.BookingFailed(command.bookingId))
    }
}
