package com.mengo.booking.infrastructure.events.mappers

import com.mengo.booking.domain.model.BookingEventItem
import com.mengo.booking.domain.model.BookingQueryEvent
import com.mengo.booking.domain.model.BookingStatus
import com.mengo.payload.booking.BookingCancelledPayload
import com.mengo.payload.booking.BookingConfirmedPayload
import com.mengo.payload.booking.BookingCreatedPayload
import com.mengo.payload.booking.BookingProduct
import com.mengo.payload.payment.PaymentCompletedPayload
import com.mengo.payload.payment.PaymentFailedPayload
import com.mengo.payload.product.ProductReservedPayload
import java.time.Instant
import java.util.UUID

fun BookingCreatedPayload.toDomain(ts: Long): BookingQueryEvent.Created =
    BookingQueryEvent.Created(
        bookingId = UUID.fromString(bookingId),
        userId = UUID.fromString(userId),
        items = products.map { item -> item.toDomain() },
        timestamp = Instant.ofEpochMilli(ts),
    )

fun ProductReservedPayload.toDomain(ts: Long): BookingQueryEvent.ProductReserved =
    BookingQueryEvent.ProductReserved(
        bookingId = UUID.fromString(bookingId),
        productId = UUID.fromString(productId),
        price = price,
        timestamp = Instant.ofEpochMilli(ts),
    )

fun PaymentCompletedPayload.toDomain(ts: Long): BookingQueryEvent.PaymentProcessed =
    BookingQueryEvent.PaymentProcessed(
        bookingId = UUID.fromString(bookingId),
        reference = reference,
        timestamp = Instant.ofEpochMilli(ts),
    )

fun BookingCancelledPayload.toDomain(ts: Long): BookingQueryEvent.StatusChanged =
    BookingQueryEvent.StatusChanged(
        bookingId = UUID.fromString(bookingId),
        status = BookingStatus.CANCELLED,
        reason = reason,
        timestamp = Instant.ofEpochMilli(ts),
    )

fun PaymentFailedPayload.toDomain(ts: Long): BookingQueryEvent.StatusChanged =
    BookingQueryEvent.StatusChanged(
        bookingId = UUID.fromString(bookingId),
        status = BookingStatus.PAYMENT_FAILED,
        reason = reason,
        timestamp = Instant.ofEpochMilli(ts),
    )

fun BookingConfirmedPayload.toDomain(ts: Long): BookingQueryEvent.StatusChanged =
    BookingQueryEvent.StatusChanged(
        bookingId = UUID.fromString(bookingId),
        status = BookingStatus.CONFIRMED,
        timestamp = Instant.ofEpochMilli(ts),
    )

private fun BookingProduct.toDomain(): BookingEventItem =
    BookingEventItem(
        productId = UUID.fromString(productId),
        quantity = quantity,
    )
