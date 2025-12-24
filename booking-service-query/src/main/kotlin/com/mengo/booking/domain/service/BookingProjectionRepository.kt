package com.mengo.booking.domain.service

import com.mengo.booking.domain.model.BookingCommand
import com.mengo.booking.domain.model.BookingReadModel
import java.util.UUID

interface BookingProjectionRepository {
    fun findById(id: UUID): BookingReadModel?

    fun save(data: BookingCommand.Create)

    fun updateProductPrice(data: BookingCommand.Price)

    fun updatePayment(data: BookingCommand.Payment)

    fun updateStatus(data: BookingCommand.Status)
}
