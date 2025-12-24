package com.mengo.booking.infrastructure.persist.mappers

import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.model.BookingReadModel
import com.mengo.booking.domain.model.BookingStatus
import com.mengo.booking.infrastructure.persist.BookingProjectionEntity
import com.mengo.booking.infrastructure.persist.ItemProjectionEntity
import java.util.UUID

fun BookingProjectionEntity.toDomain(): BookingReadModel =
    BookingReadModel(
        bookingId = UUID.fromString(this.id),
        userId = userId?.let { UUID.fromString(it) } ?: UUID(0, 0),
        status = statusToDomainBookingStatus(status),
        items = items.map { it.toDomain() }.toMutableList(),
        paymentReference = paymentReference,
        cancelReason = cancelReason,
        updatedAt = updatedAt,
    )

fun ItemProjectionEntity.toDomain(): BookingItem =
    BookingItem(
        productId = UUID.fromString(productId),
        quantity = quantity,
        price = unitPrice,
    )

fun statusToDomainBookingStatus(value: String?): BookingStatus =
    when (value) {
        "CREATED" -> BookingStatus.CREATED
        "CONFIRMED" -> BookingStatus.CONFIRMED
        "PAID" -> BookingStatus.PAID
        "COMPLETED" -> BookingStatus.COMPLETED
        "CANCELLED" -> BookingStatus.CANCELLED
        "PAYMENT_FAILED" -> BookingStatus.PAYMENT_FAILED
        else -> BookingStatus.CREATED
    }
