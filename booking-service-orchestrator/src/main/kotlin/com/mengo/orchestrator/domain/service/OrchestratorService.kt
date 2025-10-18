package com.mengo.orchestrator.domain.service

import com.mengo.orchestrator.domain.model.BookingCreated
import com.mengo.orchestrator.domain.model.PaymentCompleted
import com.mengo.orchestrator.domain.model.PaymentFailed
import com.mengo.orchestrator.domain.model.ProductReservationFailed
import com.mengo.orchestrator.domain.model.ProductReserved

interface OrchestratorService {
    fun handleBookingCreated(domain: BookingCreated)

    fun handleProductReserved(domain: ProductReserved)

    fun handleProductReservationFailed(domain: ProductReservationFailed)

    fun handlePaymentCompleted(domain: PaymentCompleted)

    fun handlePaymentFailed(domain: PaymentFailed)
}
