package com.mengo.booking.infrastructure.persist

import com.mengo.booking.domain.model.BookingStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "bookings")
class BookingEntity(
    @Id
    @Column(name = "booking_id", nullable = false, updatable = false)
    val bookingId: UUID = UUID.randomUUID(),
    @Column(name = "user_id", nullable = false)
    val userId: UUID,
    @Column(name = "resource_id", nullable = false)
    val resourceId: UUID,
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    var bookingStatus: BookingStatus,
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false)
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
) {
    private constructor() : this(
        bookingId = UUID.randomUUID(),
        userId = UUID.randomUUID(),
        resourceId = UUID.randomUUID(),
        bookingStatus = BookingStatus.CANCELLED,
        createdAt = OffsetDateTime.now(),
        updatedAt = OffsetDateTime.now(),
    )
}
