package com.mengo.orchestrator.application

import OrchestratorEvent
import com.mengo.orchestrator.domain.model.Product
import com.mengo.orchestrator.domain.model.command.OrchestratorCommand
import com.mengo.orchestrator.domain.model.command.SagaCommand
import com.mengo.orchestrator.domain.model.events.OrchestratorAggregate
import com.mengo.orchestrator.domain.model.events.OrchestratorState
import com.mengo.orchestrator.domain.service.OrchestratorEventPublisher
import com.mengo.orchestrator.domain.service.OrchestratorEventStoreRepository
import com.mengo.orchestrator.domain.service.OrchestratorService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
open class OrchestratorServiceCommand(
    private val eventStoreRepository: OrchestratorEventStoreRepository,
    private val eventPublisher: OrchestratorEventPublisher,
) : OrchestratorService {
    @Transactional
    override fun onBookingCreated(command: OrchestratorCommand.BookingCreated) {
        if (eventStoreRepository.load(command.bookingId) != null) {
            error("onBookingCreated this booking already exists")
        }

        eventStoreRepository.append(
            OrchestratorAggregate.createBookingEvent(
                bookingId = command.bookingId,
                expectedProducts = command.products,
            ),
        )

        command.products.forEach { product ->
            eventPublisher.publishRequestStock(
                SagaCommand.RequestStock(
                    bookingId = command.bookingId,
                    productId = product.productId,
                    quantity = product.quantity,
                ),
            )
        }
    }

    @Transactional
    override fun onProductReserved(command: OrchestratorCommand.ProductReserved) {
        val aggregate =
            eventStoreRepository.load(command.bookingId)
                ?: error("onProductReserved not found for booking ${command.bookingId} ")

        val event = aggregate.reserveProduct(Product(command.productId, command.quantity, command.price))
        eventStoreRepository.append(event)
        val aggregateUpdated = aggregate.applyEventSafely(event)

        if (event is OrchestratorEvent.ProductReserved && aggregateUpdated.state == OrchestratorState.WAITING_PAYMENT) {
            eventPublisher.publishRequestPayment(
                SagaCommand.RequestPayment(
                    bookingId = command.bookingId,
                    totalPrice =
                        aggregateUpdated.expectedProducts.sumOf {
                            it.price.multiply(BigDecimal(it.quantity)) ?: BigDecimal.ZERO
                        },
                ),
            )
        } else if (event is OrchestratorEvent.CompensatedProduct) {
            eventPublisher.publishReleaseStock(
                SagaCommand.ReleaseStock(command.bookingId, command.productId, command.quantity),
            )
        }
    }

    @Transactional
    override fun onProductReservationFailed(command: OrchestratorCommand.ProductReservationFailed) {
        val aggregate =
            eventStoreRepository.load(command.bookingId)
                ?: error("onProductReservationFailed not found for booking ${command.bookingId} ")

        val failEvent = aggregate.failProductReservation(command.productId)
        eventStoreRepository.append(failEvent)
        val aggregateUpdated = aggregate.applyEventSafely(failEvent)

        aggregateUpdated.reservedProducts.forEach { product ->
            val compensatedEvent =
                OrchestratorEvent.CompensatedProduct(command.bookingId, product, aggregateUpdated.lastEventVersion + 1)
            eventStoreRepository.append(compensatedEvent)
            eventPublisher.publishReleaseStock(
                SagaCommand.ReleaseStock(command.bookingId, product.productId, product.quantity),
            )
        }

        eventPublisher.publishCancelBooking(SagaCommand.CancelBooking(command.bookingId))
    }

    @Transactional
    override fun onPaymentCompleted(command: OrchestratorCommand.PaymentCompleted) {
        val currentAggregate =
            eventStoreRepository.load(command.bookingId)
                ?: error("onPaymentCompleted not found for booking ${command.bookingId} ")

        eventStoreRepository.append(currentAggregate.completePayment())
        eventPublisher.publishConfirmBooking(SagaCommand.ConfirmBooking(command.bookingId))
    }

    @Transactional
    override fun onPaymentFailed(command: OrchestratorCommand.PaymentFailed) {
        val currentAggregate =
            eventStoreRepository.load(command.bookingId)
                ?: error("handlePaymentFailed not found for booking ${command.bookingId} ")

        currentAggregate.reservedProducts.forEach { product ->
            eventPublisher.publishReleaseStock(
                SagaCommand.ReleaseStock(
                    bookingId = command.bookingId,
                    productId = product.productId,
                    quantity = product.quantity,
                ),
            )
        }

        eventStoreRepository.append(currentAggregate.failPayment())
        eventPublisher.publishCancelBooking(SagaCommand.CancelBooking(command.bookingId))
    }
}
