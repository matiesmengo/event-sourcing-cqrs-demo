package com.mengo.orchestrator.fixtures

import com.mengo.orchestrator.domain.model.Product
import com.mengo.orchestrator.domain.model.events.OrchestratorEvent
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_PRICE
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_QUANTITY
import java.util.UUID

object DomainTestData {
    // TODO: Random products
    fun buildProduct(): Product =
        Product(
            productId = PRODUCT_ID,
            quantity = PRODUCT_QUANTITY,
            price = PRODUCT_PRICE,
        )

    fun randomProducts(count: Int = 3): Set<Product> = (1..count).map { buildProduct() }.toSet()

    fun buildCreated(): OrchestratorEvent.Created {
        val bookingId = UUID.randomUUID()
        val products = randomProducts()
        return OrchestratorEvent.Created(
            bookingId = bookingId,
            expectedProducts = products,
        )
    }

    fun buildWaitingStock(): OrchestratorEvent.WaitingStock {
        val created = buildCreated()
        return created.startStockReservation()
    }

    fun buildWaitingPayment(): OrchestratorEvent.WaitingPayment {
        val waitingStock = buildWaitingStock()
        val reserved = waitingStock.expectedProducts
        return OrchestratorEvent.WaitingPayment(
            bookingId = waitingStock.bookingId,
            expectedProducts = waitingStock.expectedProducts,
            reservedProducts = reserved,
        )
    }

    fun buildCompleted(): OrchestratorEvent.Completed {
        val payment = buildWaitingPayment()
        return payment.completePayment()
    }

    fun buildCompensating(): OrchestratorEvent.Compensating {
        val created = buildCompleted()
        return OrchestratorEvent.Compensating(
            bookingId = created.bookingId,
            expectedProducts = created.expectedProducts,
        )
    }
}
