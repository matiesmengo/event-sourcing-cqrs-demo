package com.mengo.booking.infrastructure.events

import com.mengo.booking.application.BookingService
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.booking.infrastructure.events.mappers.toDomain
import com.mengo.payment.events.PaymentCompletedEvent
import com.mengo.payment.events.PaymentFailedEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class PaymentKafkaListener(
    private val bookingService: BookingService,
) {
    @KafkaListener(topics = [KAFKA_PAYMENT_COMPLETED], groupId = "booking-service-group")
    fun consumePaymentCompletedEvent(payload: PaymentCompletedEvent) {
        val successPaymentDomain = payload.toDomain()
        bookingService.onPaymentCompleted(successPaymentDomain)
    }

    @KafkaListener(topics = [KAFKA_PAYMENT_FAILED], groupId = "booking-service-group")
    fun consumePaymentFailedEvent(payload: PaymentFailedEvent) {
        val failedPaymentDomain = payload.toDomain()
        bookingService.onPaymentFailed(failedPaymentDomain)
    }
}
