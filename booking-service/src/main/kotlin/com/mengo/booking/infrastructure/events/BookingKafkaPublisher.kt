package com.mengo.booking.infrastructure.events

import com.mengo.booking.domain.model.command.SagaCommand
import com.mengo.booking.domain.service.BookingEventPublisher
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_BOOKING_COMPLETED
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_BOOKING_FAILED
import com.mengo.booking.infrastructure.events.mappers.toAvro
import org.apache.avro.specific.SpecificRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class BookingKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, SpecificRecord>,
) : BookingEventPublisher {
    override fun publishBookingCreated(bookingCreated: SagaCommand.BookingCreated) {
        kafkaTemplate.send(KAFKA_BOOKING_CREATED, bookingCreated.bookingId.toString(), bookingCreated.toAvro())
    }

    override fun publishBookingCompleted(completedBooking: SagaCommand.BookingConfirmed) {
        kafkaTemplate.send(KAFKA_BOOKING_COMPLETED, completedBooking.bookingId.toString(), completedBooking.toAvro())
    }

    override fun publishBookingFailed(failedBooking: SagaCommand.BookingFailed) {
        kafkaTemplate.send(KAFKA_BOOKING_FAILED, failedBooking.bookingId.toString(), failedBooking.toAvro())
    }
}
