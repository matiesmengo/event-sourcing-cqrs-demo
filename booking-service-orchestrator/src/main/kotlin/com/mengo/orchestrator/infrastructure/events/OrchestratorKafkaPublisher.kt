package com.mengo.orchestrator.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_CANCEL_BOOKING
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_CONFIRM_BOOKING
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_RELEASE_STOCK
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_REQUEST_PAYMENT
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_REQUEST_STOCK
import com.mengo.orchestrator.domain.model.command.SagaCommand
import com.mengo.orchestrator.domain.service.OrchestratorEventPublisher
import com.mengo.orchestrator.infrastructure.SagaMetrics
import com.mengo.orchestrator.infrastructure.events.mapper.toAvro
import org.apache.avro.specific.SpecificRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class OrchestratorKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, SpecificRecord>,
    private val sagaMetrics: SagaMetrics,
) : OrchestratorEventPublisher {
    override fun publishRequestStock(requestStock: SagaCommand.RequestStock) {
        val avroBooking = requestStock.toAvro()
        kafkaTemplate.send(KAFKA_SAGA_REQUEST_STOCK, avroBooking.bookingId.toString(), avroBooking)
    }

    override fun publishRequestPayment(requestPayment: SagaCommand.RequestPayment) {
        val avroBooking = requestPayment.toAvro()
        kafkaTemplate.send(KAFKA_SAGA_REQUEST_PAYMENT, avroBooking.bookingId.toString(), avroBooking)
    }

    override fun publishReleaseStock(releaseStock: SagaCommand.ReleaseStock) {
        val avroBooking = releaseStock.toAvro()
        kafkaTemplate.send(KAFKA_SAGA_RELEASE_STOCK, avroBooking.bookingId.toString(), avroBooking)
    }

    override fun publishConfirmBooking(bookingCompleted: SagaCommand.ConfirmBooking) {
        val avroBooking = bookingCompleted.toAvro()
        kafkaTemplate.send(KAFKA_SAGA_CONFIRM_BOOKING, avroBooking.bookingId.toString(), avroBooking)
        sagaMetrics.incrementCompleted("booking_saga")
    }

    override fun publishCancelBooking(cancelBooking: SagaCommand.CancelBooking) {
        val avroBooking = cancelBooking.toAvro()
        kafkaTemplate.send(KAFKA_SAGA_CANCEL_BOOKING, avroBooking.bookingId.toString(), avroBooking)
        sagaMetrics.incrementFailed("booking_saga")
    }
}
