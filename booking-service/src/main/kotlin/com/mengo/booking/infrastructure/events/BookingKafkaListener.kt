package com.mengo.booking.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_CANCEL_BOOKING
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_CONFIRM_BOOKING
import com.mengo.booking.domain.service.BookingService
import com.mengo.booking.infrastructure.events.mappers.toDomain
import com.mengo.payload.orchestrator.OrchestratorCancelBookingPayload
import com.mengo.payload.orchestrator.OrchestratorConfirmBookingPayload
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class BookingKafkaListener(
    private val bookingService: BookingService,
) {
    @KafkaListener(topics = [KAFKA_SAGA_CONFIRM_BOOKING], groupId = "booking-service-group")
    fun onPaymentCompletedEvent(payload: OrchestratorConfirmBookingPayload) {
        bookingService.onPaymentCompleted(payload.toDomain())
    }

    @KafkaListener(topics = [KAFKA_SAGA_CANCEL_BOOKING], groupId = "booking-service-group")
    fun onPaymentFailedEvent(payload: OrchestratorCancelBookingPayload) {
        bookingService.onPaymentFailed(payload.toDomain())
    }
}
