package com.mengo.booking.application

import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.model.BookingReadModel
import com.mengo.booking.domain.service.BookingProjectionRepository
import com.mengo.booking.domain.service.QueryService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class BookingServiceQuery(
    private val repository: BookingProjectionRepository,
) : QueryService {
    override fun findBookingById(bookingId: UUID): BookingReadModel {
        val entity =
            repository.findById(bookingId)
                ?: throw NoSuchElementException("Booking with ID $bookingId not found")

        return entity.apply {
            totalPrice = entity.items.calculateTotal()
        }
    }

    private fun List<BookingItem>.calculateTotal(): BigDecimal =
        this
            .mapNotNull { it.price?.multiply(BigDecimal(it.quantity)) }
            .fold(BigDecimal.ZERO, BigDecimal::add)
}
