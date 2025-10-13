package com.mengo.booking.infrastructure.events

import com.mengo.booking.domain.service.BookingService
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.booking.infrastructure.events.mappers.toDomain
import com.mengo.payment.payload.PaymentCompletedPayload
import com.mengo.payment.payload.PaymentFailedPayload
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class PaymentKafkaListener(
    private val bookingService: BookingService,
) {
    @KafkaListener(topics = [KAFKA_PAYMENT_COMPLETED], groupId = "booking-service-group")
    fun consumePaymentCompletedEvent(payload: PaymentCompletedPayload) {
        val successPaymentDomain = payload.toDomain()
        bookingService.onPaymentCompleted(successPaymentDomain)
    }

    @KafkaListener(topics = [KAFKA_PAYMENT_FAILED], groupId = "booking-service-group")
    fun consumePaymentFailedEvent(payload: PaymentFailedPayload) {
        val failedPaymentDomain = payload.toDomain()
        bookingService.onPaymentFailed(failedPaymentDomain)
    }
}
