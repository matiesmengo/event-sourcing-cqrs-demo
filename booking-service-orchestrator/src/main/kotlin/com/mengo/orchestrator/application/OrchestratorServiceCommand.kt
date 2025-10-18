package com.mengo.orchestrator.application

import com.mengo.orchestrator.domain.model.BookingCreated
import com.mengo.orchestrator.domain.model.PaymentCompleted
import com.mengo.orchestrator.domain.model.PaymentFailed
import com.mengo.orchestrator.domain.model.Product
import com.mengo.orchestrator.domain.model.ProductReservationFailed
import com.mengo.orchestrator.domain.model.ProductReserved
import com.mengo.orchestrator.domain.model.events.OrchestratorEvent
import com.mengo.orchestrator.domain.model.events.SagaCommand
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
    override fun handleBookingCreated(domain: BookingCreated) {
        // Created → WaitingStock
        val orchestratorEvent =
            OrchestratorEvent
                .Created(bookingId = domain.bookingId, expectedProducts = domain.products)
                .startStockReservation()

        eventStoreRepository.save(orchestratorEvent)
        orchestratorEvent.expectedProducts.forEach { product ->
            eventPublisher.publishRequestStock(
                SagaCommand.RequestStock(
                    bookingId = orchestratorEvent.bookingId,
                    productId = product.productId,
                    quantity = product.quantity,
                ),
            )
        }
    }

    @Transactional
    override fun handleProductReserved(domain: ProductReserved) {
        // Retrieve status
        val current =
            eventStoreRepository.findByBookingId(domain.bookingId)
                ?: throw IllegalStateException("Saga not found for booking ${domain.bookingId}")
        if (current !is OrchestratorEvent.WaitingStock) {
            throw IllegalStateException("Invalid saga state: expected WAITING_STOCK, got ${current::class.simpleName}")
        }

        // Reserve a product
        val updated = current.markProductReserved(Product(domain.productId, domain.quantity, domain.price))
        eventStoreRepository.save(updated)

        // WaitingStock → WaitingPayment. In the case of all products are reserved successfully
        if (updated is OrchestratorEvent.WaitingPayment) {
            eventPublisher.publishRequestPayment(
                SagaCommand.RequestPayment(
                    bookingId = updated.bookingId,
                    totalPrice =
                        updated.reservedProducts
                            .map { it.price ?: BigDecimal.ZERO }
                            .fold(BigDecimal.ZERO) { acc, amount -> acc + amount },
                ),
            )
        }
    }

    @Transactional
    override fun handleProductReservationFailed(domain: ProductReservationFailed) {
        // Retrieve status
        val current =
            eventStoreRepository.findByBookingId(domain.bookingId)
                ?: throw IllegalStateException("Saga not found for booking ${domain.bookingId}")

        // WaitingStock → Compensating
        val compensating =
            OrchestratorEvent.Compensating(
                bookingId = current.bookingId,
                expectedProducts = current.expectedProducts,
            )
        eventStoreRepository.save(compensating)

        // Compensating all products reserved
        compensating.expectedProducts.forEach { product ->
            eventPublisher.publishReleaseStock(
                SagaCommand.ReleaseStock(
                    bookingId = compensating.bookingId,
                    productId = product.productId,
                    quantity = product.quantity,
                ),
            )
        }
        // Cancel booking
        eventPublisher.publishCancelBooking(SagaCommand.CancelBooking(bookingId = compensating.bookingId))
    }

    @Transactional
    override fun handlePaymentCompleted(domain: PaymentCompleted) {
        val current =
            eventStoreRepository.findByBookingId(domain.bookingId)
                ?: throw IllegalStateException("Saga not found for booking ${domain.bookingId}")
        if (current !is OrchestratorEvent.WaitingPayment) {
            throw IllegalStateException("Invalid saga state: expected WAITING_PAYMENT, got ${current::class.simpleName}")
        }

        val completed = current.completePayment()
        eventStoreRepository.save(completed)

        eventPublisher.publishConfirmBooking(
            SagaCommand.ConfirmBooking(bookingId = completed.bookingId),
        )
    }

    @Transactional
    override fun handlePaymentFailed(domain: PaymentFailed) {
        val current =
            eventStoreRepository.findByBookingId(domain.bookingId)
                ?: throw IllegalStateException("Saga not found for booking ${domain.bookingId}")

        val compensating =
            OrchestratorEvent.Compensating(bookingId = current.bookingId, expectedProducts = current.expectedProducts)
        eventStoreRepository.save(compensating)

        compensating.expectedProducts.forEach { product ->
            eventPublisher.publishReleaseStock(
                SagaCommand.ReleaseStock(
                    bookingId = compensating.bookingId,
                    productId = product.productId,
                    quantity = product.quantity,
                ),
            )
        }
        eventPublisher.publishCancelBooking(SagaCommand.CancelBooking(bookingId = compensating.bookingId))
    }
}
