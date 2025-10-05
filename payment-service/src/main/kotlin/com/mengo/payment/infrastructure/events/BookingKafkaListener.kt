package com.mengo.payment.infrastructure.events

import com.mengo.booking.events.BookingCreatedEvent
import com.mengo.payment.domain.service.PaymentService
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.payment.infrastructure.events.mappers.toDomain
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class BookingKafkaListener(
    private val paymentService: PaymentService,
) {
    @KafkaListener(topics = [KAFKA_BOOKING_CREATED], groupId = "payment-service-group")
    fun consumeBookingCreatedEvent(payload: BookingCreatedEvent) {
        val bookingPaymentDomain = payload.toDomain()
        paymentService.onBookingCreated(bookingPaymentDomain)
    }
}
