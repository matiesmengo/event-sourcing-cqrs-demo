package com.mengo.booking.infrastructure.persist

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BookingEventStoreJpaRepository : JpaRepository<BookingEventEntity, UUID> {
    fun findByBookingId(paymentId: UUID): List<BookingEventEntity>
}
