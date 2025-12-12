package com.mengo.product.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_RELEASE_STOCK
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_REQUEST_STOCK
import com.mengo.architecture.test.infrastructure.AbstractIntegrationTest
import com.mengo.product.application.ProductServiceCommand
import com.mengo.product.fixtures.PayloadTestData.buildOrchestratorReleaseStockPayload
import com.mengo.product.fixtures.PayloadTestData.buildOrchestratorRequestStockPayload
import com.mengo.product.fixtures.ProductConstants.BOOKING_ID
import com.mengo.product.fixtures.ProductConstants.PRODUCT_ID
import com.mengo.product.fixtures.ProductConstants.PRODUCT_QUANTITY
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration
import java.util.UUID
import kotlin.test.assertEquals

class ProductKafkaListenerIntegrationTest : AbstractIntegrationTest() {
    @MockitoBean
    lateinit var serviceCommand: ProductServiceCommand

    @Test
    fun `should consume OrchestratorRequestStockPayload from onReserveProduct and call ProductServiceCommand`() {
        val payload = buildOrchestratorRequestStockPayload()
        val messageId = UUID.randomUUID()

        repeat(5) {
            kafkaTemplate.send(
                buildProducerRecord(
                    topic = KAFKA_SAGA_REQUEST_STOCK,
                    key = payload.bookingId,
                    payload = payload,
                    messageId = messageId,
                ),
            )
        }
        kafkaTemplate.flush()

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand, times(1)).onReserveProduct(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertEquals(PRODUCT_ID, it.productId)
                    assertEquals(PRODUCT_QUANTITY, it.quantity)
                },
            )
        }
    }

    @Test
    fun `should consume OrchestratorReleaseStockPayload from onReleaseProduct and call ProductServiceCommand`() {
        val payload = buildOrchestratorReleaseStockPayload()
        val messageId = UUID.randomUUID()

        repeat(5) {
            kafkaTemplate.send(
                buildProducerRecord(
                    topic = KAFKA_SAGA_RELEASE_STOCK,
                    key = payload.bookingId,
                    payload = payload,
                    messageId = messageId,
                ),
            )
        }
        kafkaTemplate.flush()
        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand, times(1)).onReleaseProduct(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertEquals(PRODUCT_ID, it.productId)
                    assertEquals(PRODUCT_QUANTITY, it.quantity)
                },
            )
        }
    }
}
