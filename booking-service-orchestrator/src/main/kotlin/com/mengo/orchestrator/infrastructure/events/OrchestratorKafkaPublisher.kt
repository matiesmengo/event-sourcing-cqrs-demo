package com.mengo.orchestrator.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_CANCEL_BOOKING
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_CONFIRM_BOOKING
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_RELEASE_STOCK
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_REQUEST_PAYMENT
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_REQUEST_STOCK
import com.mengo.architecture.outbox.OutboxRepository
import com.mengo.orchestrator.domain.model.command.SagaCommand
import com.mengo.orchestrator.domain.service.OrchestratorEventPublisher
import com.mengo.orchestrator.infrastructure.SagaMetrics
import com.mengo.orchestrator.infrastructure.events.mapper.toAvro
import com.mengo.payload.orchestrator.OrchestratorCancelBookingPayload
import com.mengo.payload.orchestrator.OrchestratorConfirmBookingPayload
import com.mengo.payload.orchestrator.OrchestratorReleaseStockPayload
import com.mengo.payload.orchestrator.OrchestratorRequestPaymentPayload
import com.mengo.payload.orchestrator.OrchestratorRequestStockPayload
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
open class OrchestratorKafkaPublisher(
    private val outboxRepository: OutboxRepository,
    private val sagaMetrics: SagaMetrics,
) : OrchestratorEventPublisher {
    @Transactional(propagation = Propagation.REQUIRED)
    override fun publishRequestStock(requestStock: SagaCommand.RequestStock) {
        val avroPayload: OrchestratorRequestStockPayload = requestStock.toAvro()

        outboxRepository.persistOutboxEvent(
            topic = KAFKA_SAGA_REQUEST_STOCK,
            key = avroPayload.bookingId.toString(),
            payloadJson = avroPayload,
        )
    }

    @Transactional(propagation = Propagation.REQUIRED)
    override fun publishRequestPayment(requestPayment: SagaCommand.RequestPayment) {
        val avroPayload: OrchestratorRequestPaymentPayload = requestPayment.toAvro()

        outboxRepository.persistOutboxEvent(
            topic = KAFKA_SAGA_REQUEST_PAYMENT,
            key = avroPayload.bookingId.toString(),
            payloadJson = avroPayload,
        )
    }

    @Transactional(propagation = Propagation.REQUIRED)
    override fun publishReleaseStock(releaseStock: SagaCommand.ReleaseStock) {
        val avroPayload: OrchestratorReleaseStockPayload = releaseStock.toAvro()

        outboxRepository.persistOutboxEvent(
            topic = KAFKA_SAGA_RELEASE_STOCK,
            key = avroPayload.bookingId.toString(),
            payloadJson = avroPayload,
        )
    }

    @Transactional(propagation = Propagation.REQUIRED)
    override fun publishConfirmBooking(bookingCompleted: SagaCommand.ConfirmBooking) {
        val avroPayload: OrchestratorConfirmBookingPayload = bookingCompleted.toAvro()
        sagaMetrics.incrementCompleted("booking_saga")

        outboxRepository.persistOutboxEvent(
            topic = KAFKA_SAGA_CONFIRM_BOOKING,
            key = avroPayload.bookingId.toString(),
            payloadJson = avroPayload,
        )
    }

    @Transactional(propagation = Propagation.REQUIRED)
    override fun publishCancelBooking(cancelBooking: SagaCommand.CancelBooking) {
        val avroPayload: OrchestratorCancelBookingPayload = cancelBooking.toAvro()
        sagaMetrics.incrementFailed("booking_saga")

        outboxRepository.persistOutboxEvent(
            topic = KAFKA_SAGA_CANCEL_BOOKING,
            key = avroPayload.bookingId.toString(),
            payloadJson = avroPayload,
        )
    }
}
