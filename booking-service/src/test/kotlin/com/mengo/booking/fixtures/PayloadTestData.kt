package com.mengo.booking.fixtures

import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.orchestrator.payload.OrchestratorCancelBookingPayload
import com.mengo.orchestrator.payload.OrchestratorConfirmBookingPayload

object PayloadTestData {
    fun buildPaymentCompletedPayload() = OrchestratorConfirmBookingPayload(BOOKING_ID.toString())

    fun buildPaymentFailedPayload() = OrchestratorCancelBookingPayload(BOOKING_ID.toString())
}
