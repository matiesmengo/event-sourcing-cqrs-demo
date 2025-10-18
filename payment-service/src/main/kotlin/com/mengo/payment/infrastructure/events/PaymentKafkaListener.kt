package com.mengo.payment.infrastructure.events

import com.mengo.orchestrator.payload.OrchestratorRequestPaymentPayload
import com.mengo.payment.domain.service.PaymentService
import com.mengo.payment.infrastructure.events.KafkaTopics.KAFKA_SAGA_REQUEST_PAYMENT
import com.mengo.payment.infrastructure.events.mappers.toDomain
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class PaymentKafkaListener(
    private val paymentService: PaymentService,
) {
    @KafkaListener(topics = [KAFKA_SAGA_REQUEST_PAYMENT], groupId = "payment-service-group")
    fun onRequestPayment(event: OrchestratorRequestPaymentPayload) {
        paymentService.onRequestPayment(event.toDomain())
    }
}
