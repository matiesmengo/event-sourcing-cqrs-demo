package com.mengo.orchestrator.fixtures

import com.mengo.orchestrator.domain.model.command.SagaCommand
import com.mengo.orchestrator.fixtures.OrchestratorConstants.BOOKING_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_ID
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_PRICE
import com.mengo.orchestrator.fixtures.OrchestratorConstants.PRODUCT_QUANTITY

object CommandTestData {
    fun buildSagaCommandRequestStock() =
        SagaCommand.RequestStock(
            bookingId = BOOKING_ID,
            productId = PRODUCT_ID,
            quantity = PRODUCT_QUANTITY,
        )

    fun buildSagaCommandReleaseStock() =
        SagaCommand.ReleaseStock(
            bookingId = BOOKING_ID,
            productId = PRODUCT_ID,
            quantity = PRODUCT_QUANTITY,
        )

    fun buildSagaCommandRequestPayment() =
        SagaCommand.RequestPayment(
            bookingId = BOOKING_ID,
            totalPrice = PRODUCT_PRICE,
        )

    fun buildSagaCommandConfirmBooking() =
        SagaCommand.ConfirmBooking(
            bookingId = BOOKING_ID,
        )

    fun buildSagaCommandCancelBooking() =
        SagaCommand.CancelBooking(
            bookingId = BOOKING_ID,
        )
}
