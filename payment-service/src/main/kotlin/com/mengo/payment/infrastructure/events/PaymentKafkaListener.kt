package com.mengo.payment.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_REQUEST_PAYMENT
import com.mengo.architecture.observability.ObservabilityStep
import com.mengo.payload.orchestrator.OrchestratorRequestPaymentPayload
import com.mengo.payment.domain.service.PaymentService
import com.mengo.payment.infrastructure.events.mappers.toDomain
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
open class PaymentKafkaListener(
    private val paymentService: PaymentService,
) {
    @KafkaListener(topics = [KAFKA_SAGA_REQUEST_PAYMENT], groupId = "payment-service-group")
    @ObservabilityStep(name = "payment_request_payment")
    open fun onRequestPayment(event: OrchestratorRequestPaymentPayload) {
        paymentService.onRequestPayment(event.toDomain())
    }
}
