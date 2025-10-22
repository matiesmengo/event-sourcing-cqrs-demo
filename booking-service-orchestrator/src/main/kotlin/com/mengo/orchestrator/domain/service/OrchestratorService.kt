package com.mengo.orchestrator.domain.service

import com.mengo.orchestrator.domain.model.command.OrchestratorCommand

interface OrchestratorService {
    fun onBookingCreated(command: OrchestratorCommand.BookingCreated)

    fun onProductReserved(command: OrchestratorCommand.ProductReserved)

    fun onProductReservationFailed(command: OrchestratorCommand.ProductReservationFailed)

    fun onPaymentCompleted(command: OrchestratorCommand.PaymentCompleted)

    fun onPaymentFailed(command: OrchestratorCommand.PaymentFailed)
}
