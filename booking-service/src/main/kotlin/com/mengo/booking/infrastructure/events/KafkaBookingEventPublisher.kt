package com.mengo.booking.infrastructure.events

import com.example.booking.events.BookingCreated
import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.service.BookingEventPublisher
import com.mengo.booking.infrastructure.events.mappers.toAvro
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaBookingEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, BookingCreated>,
) : BookingEventPublisher {
    override fun publishBookingCreated(booking: Booking) {
        val avroBooking = booking.toAvro()
        kafkaTemplate.send("booking.created", avroBooking.bookingId.toString(), avroBooking)
    }
}
