package com.mengo.booking.fixtures

import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.payload.orchestrator.OrchestratorCancelBookingPayload
import com.mengo.payload.orchestrator.OrchestratorConfirmBookingPayload

object PayloadTestData {
    fun buildPaymentCompletedPayload() = OrchestratorConfirmBookingPayload(BOOKING_ID.toString())

    fun buildPaymentFailedPayload() = OrchestratorCancelBookingPayload(BOOKING_ID.toString())
}
