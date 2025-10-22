package com.mengo.product.infrastructure.events

import com.mengo.product.application.ProductServiceCommand
import com.mengo.product.fixtures.PayloadTestData.buildOrchestratorReleaseStockPayload
import com.mengo.product.fixtures.PayloadTestData.buildOrchestratorRequestStockPayload
import com.mengo.product.fixtures.ProductConstants.BOOKING_ID
import com.mengo.product.fixtures.ProductConstants.PRODUCT_ID
import com.mengo.product.fixtures.ProductConstants.PRODUCT_QUANTITY
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

class ProductKafkaListenerTest {
    private val serviceCommand: ProductServiceCommand = mock()
    private val listener = ProductKafkaListener(serviceCommand)

    @Test
    fun `consumeReserveProduct should call ProductService`() {
        // given
        val payload = buildOrchestratorRequestStockPayload()

        // when
        listener.consumeReserveProduct(payload)

        // then
        verify(serviceCommand).onReserveProduct(
            check {
                assertEquals(BOOKING_ID, it.bookingId)
                assertEquals(PRODUCT_ID, it.productId)
                assertEquals(PRODUCT_QUANTITY, it.quantity)
            },
        )
    }

    @Test
    fun `consumeReleaseProduct should call ProductService`() {
        // given
        val payload = buildOrchestratorReleaseStockPayload()

        // when
        listener.consumeReleaseProduct(payload)

        // then
        verify(serviceCommand).onReleaseProduct(
            check {
                assertEquals(BOOKING_ID, it.bookingId)
                assertEquals(PRODUCT_ID, it.productId)
                assertEquals(PRODUCT_QUANTITY, it.quantity)
            },
        )
    }
}
