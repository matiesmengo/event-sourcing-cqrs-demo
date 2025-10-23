package com.mengo.product.infrastructure.events

import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_RELEASE_STOCK
import com.mengo.architecture.KafkaTopics.KAFKA_SAGA_REQUEST_STOCK
import com.mengo.kafka.test.KafkaTestContainerBase
import com.mengo.product.application.ProductServiceCommand
import com.mengo.product.fixtures.PayloadTestData.buildOrchestratorReleaseStockPayload
import com.mengo.product.fixtures.PayloadTestData.buildOrchestratorRequestStockPayload
import com.mengo.product.fixtures.ProductConstants.BOOKING_ID
import com.mengo.product.fixtures.ProductConstants.PRODUCT_ID
import com.mengo.product.fixtures.ProductConstants.PRODUCT_QUANTITY
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.verify
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration
import kotlin.test.assertEquals

class ProductKafkaListenerIntegrationTest : KafkaTestContainerBase() {
    @MockitoBean
    lateinit var serviceCommand: ProductServiceCommand

    @Test
    fun `should consume saga request stock from onReserveProduct and call bookingService`() {
        val event = buildOrchestratorRequestStockPayload()

        kafkaTemplate.send(KAFKA_SAGA_REQUEST_STOCK, event.bookingId, event)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand).onReserveProduct(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertEquals(PRODUCT_ID, it.productId)
                    assertEquals(PRODUCT_QUANTITY, it.quantity)
                },
            )
        }
    }

    @Test
    fun `should consume PaymentCompletedEvent from onReleaseProduct and call bookingService`() {
        val event = buildOrchestratorReleaseStockPayload()

        kafkaTemplate.send(KAFKA_SAGA_RELEASE_STOCK, event.bookingId, event)

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(serviceCommand).onReleaseProduct(
                check {
                    assertEquals(BOOKING_ID, it.bookingId)
                    assertEquals(PRODUCT_ID, it.productId)
                    assertEquals(PRODUCT_QUANTITY, it.quantity)
                },
            )
        }
    }
}
