package com.mengo.payment.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_PAYMENT_INITIATED
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_REQUEST_PAYMENT
import com.mengo.architecture.inbox.InboxRepository
import com.mengo.architecture.observability.ObservabilityStep
import com.mengo.payload.orchestrator.OrchestratorRequestPaymentPayload
import com.mengo.payload.payment.PaymentInitiatedPayload
import com.mengo.payment.domain.service.PaymentService
import com.mengo.payment.infrastructure.events.mappers.toDomain
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class PaymentKafkaListener(
    private val paymentService: PaymentService,
    private val inboxRepository: InboxRepository,
) {
    @Transactional
    @KafkaListener(topics = [KAFKA_SAGA_REQUEST_PAYMENT], groupId = "payment-service-group", concurrency = "6")
    @ObservabilityStep(name = "payment_request_payment")
    open fun onRequestPayment(payload: OrchestratorRequestPaymentPayload) {
        if (!inboxRepository.validateIdempotencyEvent()) return

        paymentService.onRequestPayment(payload.toDomain())
    }

    @Transactional
    @KafkaListener(topics = [KAFKA_PAYMENT_INITIATED], groupId = "payment-service-group", concurrency = "6")
    @ObservabilityStep(name = "payment_request_payment")
    open fun onPaymentInitiated(payload: PaymentInitiatedPayload) {
        if (!inboxRepository.validateIdempotencyEvent()) return

        paymentService.onPaymentInitiated(payload.toDomain())
    }
}
