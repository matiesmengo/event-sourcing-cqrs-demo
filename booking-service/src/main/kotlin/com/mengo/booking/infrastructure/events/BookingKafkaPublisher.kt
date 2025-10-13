package com.mengo.booking.infrastructure.events

import com.mengo.booking.domain.model.BookingCreatedEvent
import com.mengo.booking.domain.model.BookingPaymentConfirmedEvent
import com.mengo.booking.domain.model.BookingPaymentFailedEvent
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
    override fun publishBookingCreated(bookingCreated: BookingCreatedEvent) {
        val avroBooking = bookingCreated.toAvro()
        kafkaTemplate.send(KAFKA_BOOKING_CREATED, avroBooking.bookingId.toString(), avroBooking)
    }

    override fun publishBookingCompleted(completedBooking: BookingPaymentConfirmedEvent) {
        val avroBooking = completedBooking.toAvro()
        kafkaTemplate.send(KAFKA_BOOKING_COMPLETED, avroBooking.bookingId.toString(), avroBooking)
    }

    override fun publishBookingFailed(failedBooking: BookingPaymentFailedEvent) {
        val avroBooking = failedBooking.toAvro()
        kafkaTemplate.send(KAFKA_BOOKING_FAILED, avroBooking.bookingId.toString(), avroBooking)
    }
}
