package com.mengo.orchestrator.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVATION_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVED
import com.mengo.architecture.observability.ObservabilityStep
import com.mengo.orchestrator.application.OrchestratorServiceCommand
import com.mengo.orchestrator.infrastructure.SagaMetrics
import com.mengo.orchestrator.infrastructure.events.mapper.toDomain
import com.mengo.payload.booking.BookingCreatedPayload
import com.mengo.payload.payment.PaymentCompletedPayload
import com.mengo.payload.payment.PaymentFailedPayload
import com.mengo.payload.product.ProductReservationFailedPayload
import com.mengo.payload.product.ProductReservedPayload
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
open class OrchestratorKafkaListener(
    private val sagaMetrics: SagaMetrics,
    private val orchestratorServiceCommand: OrchestratorServiceCommand,
) {
    @KafkaListener(topics = [KAFKA_BOOKING_CREATED], groupId = "booking-saga-orchestrator")
    @ObservabilityStep(name = "orchestrator_booking_created")
    open fun onBookingCreated(payload: BookingCreatedPayload) {
        sagaMetrics.incrementStarted("booking_saga")
        orchestratorServiceCommand.onBookingCreated(payload.toDomain())
    }

    @KafkaListener(topics = [KAFKA_PRODUCT_RESERVED], groupId = "booking-saga-orchestrator")
    @ObservabilityStep(name = "orchestrator_product_reserved")
    open fun onProductReserved(payload: ProductReservedPayload) {
        orchestratorServiceCommand.onProductReserved(payload.toDomain())
    }

    @KafkaListener(topics = [KAFKA_PRODUCT_RESERVATION_FAILED], groupId = "booking-saga-orchestrator")
    @ObservabilityStep(name = "orchestrator_product_reserved_failed")
    open fun onProductReservationFailed(payload: ProductReservationFailedPayload) {
        orchestratorServiceCommand.onProductReservationFailed(payload.toDomain())
    }

    @KafkaListener(topics = [KAFKA_PAYMENT_COMPLETED], groupId = "booking-saga-orchestrator")
    @ObservabilityStep(name = "orchestrator_payment_completed")
    open fun onPaymentCompleted(payload: PaymentCompletedPayload) {
        orchestratorServiceCommand.onPaymentCompleted(payload.toDomain())
    }

    @KafkaListener(topics = [KAFKA_PAYMENT_FAILED], groupId = "booking-saga-orchestrator")
    @ObservabilityStep(name = "orchestrator_payment_failed")
    open fun onPaymentFailed(payload: PaymentFailedPayload) {
        orchestratorServiceCommand.onPaymentFailed(payload.toDomain())
    }
}
