package com.mengo.booking.infrastructure.events

import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.service.BookingEventPublisher
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.booking.infrastructure.events.mappers.toAvro
import org.apache.avro.specific.SpecificRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaBookingEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, SpecificRecord>,
) : BookingEventPublisher {
    override fun publishBookingCreated(booking: Booking) {
        val avroBooking = booking.toAvro()
        kafkaTemplate.send(KAFKA_BOOKING_CREATED, avroBooking.bookingId.toString(), avroBooking)
    }
}
