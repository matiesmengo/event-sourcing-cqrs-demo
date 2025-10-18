package com.mengo.orchestrator.domain.service

import com.mengo.orchestrator.domain.model.events.SagaCommand

interface OrchestratorEventPublisher {
    fun publishRequestStock(requestStock: SagaCommand.RequestStock)

    fun publishRequestPayment(requestPayment: SagaCommand.RequestPayment)

    fun publishReleaseStock(releaseStock: SagaCommand.ReleaseStock)

    fun publishConfirmBooking(bookingCompleted: SagaCommand.ConfirmBooking)

    fun publishCancelBooking(cancelBooking: SagaCommand.CancelBooking)
}
