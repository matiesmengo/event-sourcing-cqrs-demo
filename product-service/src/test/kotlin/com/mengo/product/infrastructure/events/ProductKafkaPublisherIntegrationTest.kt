package com.mengo.product.infrastructure.events

import com.mengo.kafka.test.KafkaTestContainerBase
import com.mengo.product.fixtures.CommandTestData.buildBookingCommandReserved
import com.mengo.product.fixtures.CommandTestData.buildBookingCommandReservedFailed
import com.mengo.product.infrastructure.events.KafkaTopics.KAFKA_PRODUCT_RESERVATION_FAILED
import com.mengo.product.infrastructure.events.KafkaTopics.KAFKA_PRODUCT_RESERVED
import com.mengo.product.infrastructure.events.mappers.toAvro
import com.mengo.product.payload.ProductReservationFailedPayload
import com.mengo.product.payload.ProductReservedPayload
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals

class ProductKafkaPublisherIntegrationTest : KafkaTestContainerBase() {
    @Autowired
    private lateinit var publisher: ProductKafkaPublisher

    @Test
    fun `should publish product reserved event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_PRODUCT_RESERVED))
        val booking = buildBookingCommandReserved()
        val avroBooking = booking.toAvro()

        // when
        publisher.publishProductReserved(booking)

        // then
        val records = kafkaConsumer.poll(Duration.ofSeconds(5))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroBooking.bookingId.toString(), record.key())
        assertEquals(avroBooking.bookingId, (record.value() as ProductReservedPayload).bookingId)
    }

    @Test
    fun `should publish product reserved failed event to Kafka`() {
        // given
        kafkaConsumer.subscribe(listOf(KAFKA_PRODUCT_RESERVATION_FAILED))
        val booking = buildBookingCommandReservedFailed()
        val avroBooking = booking.toAvro()

        // when
        publisher.publishProductReservedFailed(booking)

        // then
        val records = kafkaConsumer.poll(Duration.ofSeconds(5))
        assertEquals(1, records.count())
        val record = records.first()
        assertEquals(avroBooking.bookingId.toString(), record.key())
        assertEquals(avroBooking.bookingId, (record.value() as ProductReservationFailedPayload).bookingId)
    }
}
