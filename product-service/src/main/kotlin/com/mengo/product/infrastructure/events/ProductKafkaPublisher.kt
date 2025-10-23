package com.mengo.product.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVATION_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVED
import com.mengo.product.domain.model.command.BookingCommand
import com.mengo.product.domain.service.ProductEventPublisher
import com.mengo.product.infrastructure.events.mappers.toAvro
import org.apache.avro.specific.SpecificRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ProductKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, SpecificRecord>,
) : ProductEventPublisher {
    override fun publishProductReserved(reserved: BookingCommand.Reserved) {
        val avroPayment = reserved.toAvro()
        kafkaTemplate.send(KAFKA_PRODUCT_RESERVED, avroPayment.bookingId.toString(), avroPayment)
    }

    override fun publishProductReservedFailed(reservedFailed: BookingCommand.ReservedFailed) {
        val avroPayment = reservedFailed.toAvro()
        kafkaTemplate.send(KAFKA_PRODUCT_RESERVATION_FAILED, avroPayment.bookingId.toString(), avroPayment)
    }
}
