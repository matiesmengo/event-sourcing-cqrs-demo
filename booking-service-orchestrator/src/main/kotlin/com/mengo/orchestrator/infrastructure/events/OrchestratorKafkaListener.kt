package com.mengo.orchestrator.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVATION_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVED
import com.mengo.architecture.inbox.InboxRepository
import com.mengo.architecture.observability.ObservabilityStep
import com.mengo.orchestrator.application.OrchestratorServiceCommand
import com.mengo.orchestrator.infrastructure.events.mapper.toDomain
import com.mengo.payload.booking.BookingCreatedPayload
import com.mengo.payload.payment.PaymentCompletedPayload
import com.mengo.payload.payment.PaymentFailedPayload
import com.mengo.payload.product.ProductReservationFailedPayload
import com.mengo.payload.product.ProductReservedPayload
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
open class OrchestratorKafkaListener(
    private val inboxRepository: InboxRepository,
    private val orchestratorServiceCommand: OrchestratorServiceCommand,
) {
    @Transactional
    @KafkaListener(topics = [KAFKA_BOOKING_CREATED], groupId = "booking-saga-orchestrator", concurrency = "6")
    @ObservabilityStep(name = "orchestrator_booking_created")
    open fun onBookingCreated(payload: BookingCreatedPayload) {
        if (!inboxRepository.validateIdempotencyEvent()) return

        orchestratorServiceCommand.onBookingCreated(payload.toDomain())
    }

    @Transactional
    @KafkaListener(topics = [KAFKA_PRODUCT_RESERVED], groupId = "booking-saga-orchestrator", concurrency = "6")
    @ObservabilityStep(name = "orchestrator_product_reserved")
    open fun onProductReserved(payload: ProductReservedPayload) {
        if (!inboxRepository.validateIdempotencyEvent()) return

        orchestratorServiceCommand.onProductReserved(payload.toDomain())
    }

    @Transactional
    @KafkaListener(topics = [KAFKA_PRODUCT_RESERVATION_FAILED], groupId = "booking-saga-orchestrator", concurrency = "6")
    @ObservabilityStep(name = "orchestrator_product_reserved_failed")
    open fun onProductReservationFailed(payload: ProductReservationFailedPayload) {
        if (!inboxRepository.validateIdempotencyEvent()) return

        orchestratorServiceCommand.onProductReservationFailed(payload.toDomain())
    }

    @Transactional
    @KafkaListener(topics = [KAFKA_PAYMENT_COMPLETED], groupId = "booking-saga-orchestrator", concurrency = "6")
    @ObservabilityStep(name = "orchestrator_payment_completed")
    open fun onPaymentCompleted(payload: PaymentCompletedPayload) {
        if (!inboxRepository.validateIdempotencyEvent()) return

        orchestratorServiceCommand.onPaymentCompleted(payload.toDomain())
    }

    @Transactional
    @KafkaListener(topics = [KAFKA_PAYMENT_FAILED], groupId = "booking-saga-orchestrator", concurrency = "6")
    @ObservabilityStep(name = "orchestrator_payment_failed")
    open fun onPaymentFailed(payload: PaymentFailedPayload) {
        if (!inboxRepository.validateIdempotencyEvent()) return

        orchestratorServiceCommand.onPaymentFailed(payload.toDomain())
    }
}
