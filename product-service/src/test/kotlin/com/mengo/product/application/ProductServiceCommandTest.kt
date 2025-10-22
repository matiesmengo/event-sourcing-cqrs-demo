package com.mengo.product.application

import com.mengo.product.domain.model.command.SagaCommand
import com.mengo.product.domain.model.eventstore.ProductAggregate
import com.mengo.product.domain.model.eventstore.ProductReleasedEvent
import com.mengo.product.domain.model.eventstore.ProductReservedEvent
import com.mengo.product.domain.service.ProductEventPublisher
import com.mengo.product.domain.service.ProductEventStoreRepository
import com.mengo.product.fixtures.ProductConstants.BOOKING_ID
import com.mengo.product.fixtures.ProductConstants.PRODUCT_ID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import kotlin.test.assertTrue

class ProductServiceCommandTest {
    private val eventStoreRepository: ProductEventStoreRepository = mock()
    private val eventPublisher: ProductEventPublisher = mock()
    private val service = ProductServiceCommand(eventStoreRepository, eventPublisher)

    private lateinit var aggregate: ProductAggregate

    @BeforeEach
    fun setup() {
        clearInvocations(eventStoreRepository, eventPublisher)

        aggregate =
            ProductAggregate
                .create(PRODUCT_ID, stockTotal = 10, price = BigDecimal(100))
                .let { ProductAggregate.rehydrate(listOf(it)) }
    }

    @Test
    fun `onReserveProduct should publish failed when not enough stock`() {
        // given
        whenever(eventStoreRepository.load(PRODUCT_ID))
            .thenReturn(aggregate.copy(stockTotal = 5, reserved = 5))
        val command = SagaCommand.ReserveProduct(BOOKING_ID, PRODUCT_ID, quantity = 1)

        // then
        service.onReserveProduct(command)

        // when
        verify(eventPublisher).publishProductReservedFailed(any())
        verify(eventStoreRepository, never()).append(any())
    }

    @Test
    fun `onReserveProduct should reserve product and publish success`() {
        // given
        whenever(eventStoreRepository.load(PRODUCT_ID)).thenReturn(aggregate)
        val command = SagaCommand.ReserveProduct(BOOKING_ID, PRODUCT_ID, quantity = 3)

        // then
        service.onReserveProduct(command)

        // when
        verify(eventStoreRepository).append(argThat { this is ProductReservedEvent && quantity == 3 })
        verify(eventPublisher).publishProductReserved(
            argThat {
                bookingId == BOOKING_ID && productId == PRODUCT_ID && quantity == 3
            },
        )
    }

    @Test
    fun `onReleaseProduct should release product`() {
        // given
        whenever(eventStoreRepository.load(PRODUCT_ID)).thenReturn(aggregate)
        val command = SagaCommand.ReleaseProduct(BOOKING_ID, PRODUCT_ID, quantity = 2)

        // then
        service.onReleaseProduct(command)

        verify(eventStoreRepository).append(argThat { this is ProductReleasedEvent && quantity == 2 })
    }

    @Test
    fun `onReserveProduct should throw if product doesn't exist`() {
        // given
        whenever(eventStoreRepository.load(PRODUCT_ID)).thenReturn(null)
        val command = SagaCommand.ReserveProduct(BOOKING_ID, PRODUCT_ID, quantity = 1)

        // then + when
        val ex =
            assertThrows<IllegalStateException> {
                service.onReserveProduct(command)
            }

        assertTrue(ex.message!!.contains("doesn't exist"))
    }

    @Test
    fun `onReleaseProduct should throw if product doesn't exist`() {
        // given
        whenever(eventStoreRepository.load(PRODUCT_ID)).thenReturn(null)
        val command = SagaCommand.ReleaseProduct(BOOKING_ID, PRODUCT_ID, quantity = 1)

        // then + when
        val ex =
            assertThrows<IllegalStateException> {
                service.onReleaseProduct(command)
            }

        assertTrue(ex.message!!.contains("doesn't exist"))
    }
}
