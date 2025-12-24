package com.mengo.booking.domain.service

import com.mengo.booking.domain.model.BookingQueryEvent

interface UpdateService {
    fun handleCreated(event: BookingQueryEvent.Created)

    fun handleProductReserved(event: BookingQueryEvent.ProductReserved)

    fun handlePaymentCompleted(event: BookingQueryEvent.PaymentProcessed)

    fun handleStatusChange(event: BookingQueryEvent.StatusChanged)
}
