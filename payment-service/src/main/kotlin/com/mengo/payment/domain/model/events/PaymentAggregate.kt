package com.mengo.payment.domain.model.events

import java.math.BigDecimal
import java.util.UUID

enum class PaymentState {
    INITIATED,
    COMPLETED,
    FAILED,
}

data class PaymentAggregate(
    val paymentId: UUID,
    val bookingId: UUID,
    val status: PaymentState = PaymentState.INITIATED,
    val lastEventVersion: Int,
) {
    companion object {
        fun createPaymentEvent(
            bookingId: UUID,
            totalPrice: BigDecimal,
        ): PaymentEvent =
            PaymentEvent.Initiated(
                paymentId = UUID.randomUUID(),
                bookingId = bookingId,
                totalPrice = totalPrice,
                aggregateVersion = 0,
            )

        fun rehydrate(events: List<PaymentEvent>): PaymentAggregate {
            require(events.isNotEmpty()) { "No events provided for rehydration" }

            return events
                .sortedBy { it.aggregateVersion }
                .fold(null as PaymentAggregate?) { current, event ->
                    current?.applyEvent(event) ?: fromEvent(event)
                }
                ?: throw IllegalStateException("Failed to rehydrate PaymentEvent")
        }

        private fun fromEvent(event: PaymentEvent): PaymentAggregate =
            when (event) {
                is PaymentEvent.Initiated -> {
                    PaymentAggregate(
                        paymentId = event.paymentId,
                        bookingId = event.bookingId,
                        status = PaymentState.INITIATED,
                        lastEventVersion = event.aggregateVersion,
                    )
                }

                else -> {
                    error("Unsupported initial event type: ${event::class.simpleName}")
                }
            }
    }

    private fun applyEvent(event: PaymentEvent): PaymentAggregate =
        when (event) {
            is PaymentEvent.Initiated -> {
                fromEvent(event)
            }

            is PaymentEvent.Completed -> {
                copy(
                    status = PaymentState.COMPLETED,
                    lastEventVersion = event.aggregateVersion,
                )
            }

            is PaymentEvent.Failed -> {
                copy(
                    status = PaymentState.FAILED,
                    lastEventVersion = event.aggregateVersion,
                )
            }
        }

    fun confirmPayment(reference: String): PaymentEvent.Completed {
        if (status == PaymentState.INITIATED) {
            return PaymentEvent.Completed(
                paymentId = paymentId,
                bookingId = bookingId,
                reference = reference,
                aggregateVersion = lastEventVersion + 1,
            )
        }
        throw IllegalStateException("Cannot mark as confirmed a payment in state $status")
    }

    fun failPayment(reason: String): PaymentEvent.Failed {
        if (status == PaymentState.INITIATED) {
            return PaymentEvent.Failed(
                paymentId = paymentId,
                bookingId = bookingId,
                aggregateVersion = lastEventVersion + 1,
                reason = reason,
            )
        }
        throw IllegalStateException("Cannot mark as failed a payment in state $status")
    }
}
