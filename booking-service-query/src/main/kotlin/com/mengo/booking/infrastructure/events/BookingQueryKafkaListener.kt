package com.mengo.booking.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_COMPLETED
import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_CREATED
import com.mengo.architecture.KafkaTopics.KAFKA_BOOKING_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_COMPLETED
import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVED
import com.mengo.architecture.observability.ObservabilityStep
import com.mengo.booking.domain.service.UpdateService
import com.mengo.booking.infrastructure.events.mappers.toDomain
import com.mengo.payload.booking.BookingCancelledPayload
import com.mengo.payload.booking.BookingConfirmedPayload
import com.mengo.payload.booking.BookingCreatedPayload
import com.mengo.payload.payment.PaymentCompletedPayload
import com.mengo.payload.payment.PaymentFailedPayload
import com.mengo.payload.product.ProductReservedPayload
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Service

@Service
class BookingQueryKafkaListener(
    private val service: UpdateService,
) {
    @KafkaListener(topics = [KAFKA_BOOKING_CREATED], groupId = "booking-query-group")
    @ObservabilityStep(name = "query_booking_created")
    fun onBookingCreated(
        payload: BookingCreatedPayload,
        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) ts: Long,
    ) {
        service.handleCreated(payload.toDomain(ts))
    }

    @KafkaListener(topics = [KAFKA_PRODUCT_RESERVED], groupId = "booking-query-group")
    @ObservabilityStep(name = "query_product_reserved")
    fun onProductReserved(
        payload: ProductReservedPayload,
        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) ts: Long,
    ) {
        service.handleProductReserved(payload.toDomain(ts))
    }

    @KafkaListener(topics = [KAFKA_PAYMENT_COMPLETED], groupId = "booking-query-group")
    @ObservabilityStep(name = "query_payment_completed")
    fun onPaymentCompleted(
        payload: PaymentCompletedPayload,
        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) ts: Long,
    ) {
        service.handlePaymentCompleted(payload.toDomain(ts))
    }

    @KafkaListener(topics = [KAFKA_PAYMENT_FAILED], groupId = "booking-query-group")
    @ObservabilityStep(name = "query_payment_failed")
    fun onPaymentFailed(
        payload: PaymentFailedPayload,
        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) ts: Long,
    ) {
        service.handleStatusChange(payload.toDomain(ts))
    }

    @KafkaListener(topics = [KAFKA_BOOKING_FAILED], groupId = "booking-query-group")
    @ObservabilityStep(name = "query_booking_failed")
    fun onBookingCancelled(
        payload: BookingCancelledPayload,
        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) ts: Long,
    ) {
        service.handleStatusChange(payload.toDomain(ts))
    }

    @KafkaListener(topics = [KAFKA_BOOKING_COMPLETED], groupId = "booking-query-group")
    @ObservabilityStep(name = "query_booking_completed")
    fun onBookingConfirmed(
        payload: BookingConfirmedPayload,
        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) ts: Long,
    ) {
        service.handleStatusChange(payload.toDomain(ts))
    }
}
