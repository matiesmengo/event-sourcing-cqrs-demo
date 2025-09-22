package com.mengo.booking.infrastructure.persist.mappers

import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.model.BookingStatus
import com.mengo.booking.domain.model.CreateBooking
import com.mengo.booking.infrastructure.persist.BookingEntity
import java.time.OffsetDateTime
import java.util.UUID

fun CreateBooking.toEntity() =
    BookingEntity(
        bookingId = UUID.randomUUID(),
        userId = userId,
        resourceId = resourceId,
        bookingStatus = BookingStatus.CREATED,
        createdAt = OffsetDateTime.now(),
    )

fun BookingEntity.toDomain() =
    Booking(
        bookingId = bookingId,
        userId = userId,
        resourceId = resourceId,
        bookingStatus = bookingStatus,
        createdAt = createdAt,
    )
