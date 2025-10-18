package com.mengo.product.domain.service

import com.mengo.product.domain.model.BookingCommand

interface ProductEventPublisher {
    fun publishProductReserved(reserved: BookingCommand.Reserved)

    fun publishProductReservedFailed(reservedFailed: BookingCommand.ReservedFailed)
}
