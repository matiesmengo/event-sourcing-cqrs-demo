package com.mengo.product.infrastructure.events

import com.mengo.product.application.ProductServiceCommand
import com.mengo.product.fixtures.PayloadTestData.buildOrchestratorRequestStockPayload
import com.mengo.product.fixtures.ProductConstants.BOOKING_ID
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

class ProductKafkaListenerTest {
    private val serviceCommand: ProductServiceCommand = mock()
    private val listener = ProductKafkaListener(serviceCommand)

    @Test
    fun `should call bookingService on consumePaymentCompletedEvent`() {
        // given
        val payload = buildOrchestratorRequestStockPayload()

        // when
        listener.consumeBookingCreatedEvent(payload)

        // then
        verify(serviceCommand).onBookingCreated(
            check { assertEquals(BOOKING_ID, it.bookingId) },
        )
    }
}
