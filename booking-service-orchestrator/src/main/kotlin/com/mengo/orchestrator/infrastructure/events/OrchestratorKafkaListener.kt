package com.mengo.orchestrator.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVATION_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVED
import com.mengo.orchestrator.application.OrchestratorServiceCommand
import com.mengo.orchestrator.infrastructure.events.mapper.toDomain
import com.mengo.payload.booking.BookingCreatedPayload
import com.mengo.payload.payment.PaymentCompletedPayload
import com.mengo.payload.payment.PaymentFailedPayload
import com.mengo.payload.product.ProductReservationFailedPayload
import com.mengo.payload.product.ProductReservedPayload
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrchestratorKafkaListener(
    private val orchestratorServiceCommand: OrchestratorServiceCommand,
) {
    @KafkaListener(topics = [KAFKA_BOOKING_CREATED], groupId = "booking-saga-orchestrator")
    fun onBookingCreated(payload: BookingCreatedPayload) {
        orchestratorServiceCommand.onBookingCreated(payload.toDomain())
    }

    @KafkaListener(topics = [KAFKA_PRODUCT_RESERVED], groupId = "booking-saga-orchestrator")
    fun onProductReserved(payload: ProductReservedPayload) {
        orchestratorServiceCommand.onProductReserved(payload.toDomain())
    }

    @KafkaListener(topics = [KAFKA_PRODUCT_RESERVATION_FAILED], groupId = "booking-saga-orchestrator")
    fun onProductReservationFailed(payload: ProductReservationFailedPayload) {
        orchestratorServiceCommand.onProductReservationFailed(payload.toDomain())
    }

    @KafkaListener(topics = [KAFKA_PAYMENT_COMPLETED], groupId = "booking-saga-orchestrator")
    fun onPaymentCompleted(payload: PaymentCompletedPayload) {
        orchestratorServiceCommand.onPaymentCompleted(payload.toDomain())
    }

    @KafkaListener(topics = [KAFKA_PAYMENT_FAILED], groupId = "booking-saga-orchestrator")
    fun onPaymentFailed(payload: PaymentFailedPayload) {
        orchestratorServiceCommand.onPaymentFailed(payload.toDomain())
    }
}
