package com.mengo.product.fixtures

import com.mengo.payload.orchestrator.OrchestratorReleaseStockPayload
import com.mengo.payload.orchestrator.OrchestratorRequestStockPayload
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

    fun buildOrchestratorReleaseStockPayload() =
        OrchestratorReleaseStockPayload(
            BOOKING_ID.toString(),
            PRODUCT_ID.toString(),
            PRODUCT_QUANTITY,
        )
}
