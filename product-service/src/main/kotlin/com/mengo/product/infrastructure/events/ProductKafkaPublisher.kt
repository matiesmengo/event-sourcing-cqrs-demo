package com.mengo.product.infrastructure.events

import com.mengo.product.domain.model.ProductReservedEvent
import com.mengo.product.domain.service.ProductEventPublisher
import com.mengo.product.infrastructure.events.KafkaTopics.KAFKA_PRODUCT_RESERVED
import com.mengo.product.infrastructure.events.mappers.toAvro
import org.apache.avro.specific.SpecificRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ProductKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, SpecificRecord>,
) : ProductEventPublisher {
    override fun publishProductReserved(productReservedEvent: ProductReservedEvent) {
        val avroPayment = productReservedEvent.toAvro()
        kafkaTemplate.send(KAFKA_PRODUCT_RESERVED, avroPayment.bookingId.toString(), avroPayment)
    }
}
