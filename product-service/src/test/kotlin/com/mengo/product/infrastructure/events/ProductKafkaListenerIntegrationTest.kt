package com.mengo.product.infrastructure.events

import com.mengo.kafka.test.KafkaTestContainerBase
import com.mengo.product.application.ProductServiceCommand
import com.mengo.product.fixtures.PayloadTestData.buildOrchestratorRequestStockPayload
import com.mengo.product.infrastructure.events.KafkaTopics.KAFKA_SAGA_REQUEST_STOCK
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration

class ProductKafkaListenerIntegrationTest : KafkaTestContainerBase() {
    @MockitoBean
    lateinit var serviceCommand: ProductServiceCommand

    @Test
    fun `should consume PaymentCompletedEvent and call bookingService`() {
        val event = buildOrchestratorRequestStockPayload()

        kafkaTemplate.send(KAFKA_SAGA_REQUEST_STOCK, event.bookingId, event)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand).onBookingCreated(any())
        }
    }
}
