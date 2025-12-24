package com.mengo.booking.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_CANCEL_BOOKING
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_CONFIRM_BOOKING
import com.mengo.architecture.inbox.InboxRepository
import com.mengo.architecture.observability.ObservabilityStep
import com.mengo.booking.domain.service.BookingService
import com.mengo.booking.infrastructure.events.mappers.toDomain
import com.mengo.payload.orchestrator.OrchestratorCancelBookingPayload
import com.mengo.payload.orchestrator.OrchestratorConfirmBookingPayload
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class BookingKafkaListener(
    private val bookingService: BookingService,
    private val inboxRepository: InboxRepository,
) {
    @Transactional
    @KafkaListener(topics = [KAFKA_SAGA_CONFIRM_BOOKING], groupId = "booking-command-group")
    @ObservabilityStep(name = "booking_confirm_booking")
    open fun onPaymentCompletedEvent(payload: OrchestratorConfirmBookingPayload) {
        if (!inboxRepository.validateIdempotencyEvent()) return

        bookingService.onPaymentCompleted(payload.toDomain())
    }

    @Transactional
    @KafkaListener(topics = [KAFKA_SAGA_CANCEL_BOOKING], groupId = "booking-command-group")
    @ObservabilityStep(name = "booking_cancel_booking")
    open fun onPaymentFailedEvent(payload: OrchestratorCancelBookingPayload) {
        if (!inboxRepository.validateIdempotencyEvent()) return

        bookingService.onPaymentFailed(payload.toDomain())
    }
}
