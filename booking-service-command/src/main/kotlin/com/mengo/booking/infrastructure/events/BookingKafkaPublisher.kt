package com.mengo.booking.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_COMPLETED
import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_FAILED
import com.mengo.architecture.observability.Telemetry
import com.mengo.architecture.outbox.OutboxRepository
import com.mengo.booking.domain.model.command.SagaCommand
import com.mengo.booking.domain.service.BookingEventPublisher
import com.mengo.booking.infrastructure.events.mappers.toAvro
import com.mengo.payload.booking.BookingCancelledPayload
import com.mengo.payload.booking.BookingConfirmedPayload
import com.mengo.payload.booking.BookingCreatedPayload
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
open class BookingKafkaPublisher(
    private val outboxRepository: OutboxRepository,
    private val telemetry: Telemetry,
) : BookingEventPublisher {
    @Transactional(propagation = Propagation.REQUIRED)
    override fun publishBookingCreated(bookingCreated: SagaCommand.BookingCreated) {
        val avroPayload: BookingCreatedPayload = bookingCreated.toAvro()

        outboxRepository.persistOutboxEvent(
            topic = KAFKA_BOOKING_CREATED,
            key = avroPayload.bookingId.toString(),
            payloadJson = avroPayload,
        )

        telemetry.recordSagaStarted("booking_saga")
    }

    @Transactional(propagation = Propagation.REQUIRED)
    override fun publishBookingCompleted(completedBooking: SagaCommand.BookingConfirmed) {
        val avroPayload: BookingConfirmedPayload = completedBooking.toAvro()

        outboxRepository.persistOutboxEvent(
            topic = KAFKA_BOOKING_COMPLETED,
            key = avroPayload.bookingId.toString(),
            payloadJson = avroPayload,
        )

        telemetry.recordSagaCompleted("booking_saga")
    }

    @Transactional(propagation = Propagation.REQUIRED)
    override fun publishBookingFailed(failedBooking: SagaCommand.BookingFailed) {
        val avroPayload: BookingCancelledPayload = failedBooking.toAvro()

        outboxRepository.persistOutboxEvent(
            topic = KAFKA_BOOKING_FAILED,
            key = avroPayload.bookingId.toString(),
            payloadJson = avroPayload,
        )

        telemetry.recordSagaCompensated("booking_saga")
    }
}
