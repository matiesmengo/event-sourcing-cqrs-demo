package com.mengo.booking.infrastructure.events

import com.mengo.booking.domain.service.BookingService
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_SAGA_CANCEL_BOOKING
import com.mengo.booking.infrastructure.events.KafkaTopics.KAFKA_SAGA_CONFIRM_BOOKING
import com.mengo.booking.infrastructure.events.mappers.toDomain
import com.mengo.orchestrator.payload.OrchestratorCancelBookingPayload
import com.mengo.orchestrator.payload.OrchestratorConfirmBookingPayload
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class BookingKafkaListener(
    private val bookingService: BookingService,
) {
    @KafkaListener(topics = [KAFKA_SAGA_CONFIRM_BOOKING], groupId = "booking-service-group")
    fun consumePaymentCompletedEvent(payload: OrchestratorConfirmBookingPayload) {
        val successPaymentDomain = payload.toDomain()
        bookingService.onPaymentCompleted(successPaymentDomain)
    }

    @KafkaListener(topics = [KAFKA_SAGA_CANCEL_BOOKING], groupId = "booking-service-group")
    fun consumePaymentFailedEvent(payload: OrchestratorCancelBookingPayload) {
        val failedPaymentDomain = payload.toDomain()
        bookingService.onPaymentFailed(failedPaymentDomain)
    }
}
