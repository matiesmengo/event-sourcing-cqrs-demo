package com.mengo.product.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVATION_FAILED
import com.mengo.architecture.KafkaTopics.KAFKA_PRODUCT_RESERVED
import com.mengo.architecture.outbox.OutboxRepository
import com.mengo.product.fixtures.CommandTestData.buildBookingCommandReserved
import com.mengo.product.fixtures.CommandTestData.buildBookingCommandReservedFailed
import com.mengo.product.fixtures.ProductConstants.BOOKING_ID
import com.mengo.product.infrastructure.events.mappers.toAvro
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ProductKafkaPublisherTest {
    private lateinit var outboxRepository: OutboxRepository
    private lateinit var publisher: ProductKafkaPublisher

    @BeforeEach
    fun setUp() {
        outboxRepository = mock()
        publisher = ProductKafkaPublisher(outboxRepository)
    }

    @Test
    fun `should publish reserved event`() {
        // given
        val command = buildBookingCommandReserved()
        val avroCommand = command.toAvro()

        // when
        publisher.publishProductReserved(command)

        // then
        verify(outboxRepository)
            .persistOutboxEvent(eq(KAFKA_PRODUCT_RESERVED), eq(BOOKING_ID.toString()), eq(avroCommand))
    }

    @Test
    fun `should publish reserved failed event`() {
        // given
        val command = buildBookingCommandReservedFailed()
        val avroCommand = command.toAvro()

        // when
        publisher.publishProductReservedFailed(command)

        // then
        verify(outboxRepository)
            .persistOutboxEvent(eq(KAFKA_PRODUCT_RESERVATION_FAILED), eq(BOOKING_ID.toString()), eq(avroCommand))
    }
}
