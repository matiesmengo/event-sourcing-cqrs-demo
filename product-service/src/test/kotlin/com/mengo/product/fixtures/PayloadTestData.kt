package com.mengo.product.fixtures

import com.mengo.orchestrator.payload.OrchestratorRequestStockPayload
import com.mengo.product.fixtures.ProductConstants.BOOKING_ID
import com.mengo.product.fixtures.ProductConstants.PRODUCT_ID
import com.mengo.product.fixtures.ProductConstants.PRODUCT_QUANTITY

object PayloadTestData {
    fun buildOrchestratorRequestStockPayload() =
        OrchestratorRequestStockPayload(
            BOOKING_ID.toString(),
            PRODUCT_ID.toString(),
            PRODUCT_QUANTITY,
        )
}
