package com.mengo.orchestrator.infrastructure.events

import com.mengo.booking.payload.BookingCreatedPayload
import com.mengo.orchestrator.application.OrchestratorServiceCommand
import com.mengo.orchestrator.infrastructure.events.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.orchestrator.infrastructure.events.mapper.toDomain
import com.mengo.payment.payload.PaymentCompletedPayload
import com.mengo.payment.payload.PaymentFailedPayload
import com.mengo.product.payload.ProductReservationFailedPayload
import com.mengo.product.payload.ProductReservedPayload
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrchestratorKafkaListener(
    private val orchestratorServiceCommand: OrchestratorServiceCommand,
) {
    @KafkaListener(topics = [KAFKA_BOOKING_CREATED], groupId = "booking-saga-orchestrator")
    fun onBookingCreated(payload: BookingCreatedPayload) {
        orchestratorServiceCommand.handleBookingCreated(payload.toDomain())
    }

    @KafkaListener(topics = [KafkaTopics.KAFKA_PRODUCT_RESERVED], groupId = "booking-saga-orchestrator")
    fun onProductReserved(payload: ProductReservedPayload) {
        orchestratorServiceCommand.handleProductReserved(payload.toDomain())
    }

    @KafkaListener(topics = [KafkaTopics.KAFKA_PRODUCT_RESERVATION_FAILED], groupId = "booking-saga-orchestrator")
    fun onProductReservationFailed(payload: ProductReservationFailedPayload) {
        orchestratorServiceCommand.handleProductReservationFailed(payload.toDomain())
    }

    @KafkaListener(topics = [KafkaTopics.KAFKA_PAYMENT_COMPLETED], groupId = "booking-saga-orchestrator")
    fun onPaymentCompleted(payload: PaymentCompletedPayload) {
        orchestratorServiceCommand.handlePaymentCompleted(payload.toDomain())
    }

    @KafkaListener(topics = [KafkaTopics.KAFKA_PAYMENT_FAILED], groupId = "booking-saga-orchestrator")
    fun onPaymentFailed(payload: PaymentFailedPayload) {
        orchestratorServiceCommand.handlePaymentFailed(payload.toDomain())
    }
}
