package com.mengo.product.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVATION_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVED
import com.mengo.architecture.outbox.OutboxRepository
import com.mengo.product.domain.model.command.BookingCommand
import com.mengo.product.domain.service.ProductEventPublisher
import com.mengo.product.infrastructure.events.mappers.toAvro
import org.springframework.stereotype.Component

@Component
open class ProductKafkaPublisher(
    private val outboxRepository: OutboxRepository,
) : ProductEventPublisher {
    override fun publishProductReserved(reserved: BookingCommand.Reserved) {
        val avroPayment = reserved.toAvro()

        outboxRepository.persistOutboxEvent(
            topic = KAFKA_PRODUCT_RESERVED,
            key = avroPayment.bookingId.toString(),
            payloadJson = avroPayment,
        )
    }

    override fun publishProductReservedFailed(reservedFailed: BookingCommand.ReservedFailed) {
        val avroPayment = reservedFailed.toAvro()

        outboxRepository.persistOutboxEvent(
            topic = KAFKA_PRODUCT_RESERVATION_FAILED,
            key = avroPayment.bookingId.toString(),
            payloadJson = avroPayment,
        )
    }
}
