package com.mengo.booking.application

import com.mengo.booking.domain.model.BookingCommand
import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.model.BookingQueryEvent
import com.mengo.booking.domain.model.BookingStatus
import com.mengo.booking.domain.service.BookingProjectionRepository
import com.mengo.booking.domain.service.UpdateService
import org.springframework.stereotype.Service

@Service
class BookingUpdateService(
    private val repository: BookingProjectionRepository,
) : UpdateService {
    override fun handleCreated(event: BookingQueryEvent.Created) {
        repository.save(
            BookingCommand.Create(
                bookingId = event.bookingId,
                userId = event.userId,
                items =
                    event.items.map { BookingItem(productId = it.productId, quantity = it.quantity) }.toMutableList(),
                timestamp = event.timestamp,
            ),
        )
    }

    override fun handleProductReserved(event: BookingQueryEvent.ProductReserved) {
        repository.updateProductPrice(
            BookingCommand.Price(
                bookingId = event.bookingId,
                productId = event.productId,
                price = event.price,
                timestamp = event.timestamp,
            ),
        )
    }

    override fun handlePaymentCompleted(event: BookingQueryEvent.PaymentProcessed) {
        repository.updatePayment(
            BookingCommand.Payment(
                bookingId = event.bookingId,
                reference = event.reference,
                status = BookingStatus.PAID,
                timestamp = event.timestamp,
            ),
        )
    }

    override fun handleStatusChange(event: BookingQueryEvent.StatusChanged) {
        repository.updateStatus(
            BookingCommand.Status(
                bookingId = event.bookingId,
                status = event.status,
                reason = event.reason,
                timestamp = event.timestamp,
            ),
        )
    }
}
