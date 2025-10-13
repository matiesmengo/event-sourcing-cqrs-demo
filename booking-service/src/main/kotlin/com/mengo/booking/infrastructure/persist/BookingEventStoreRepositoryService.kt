package com.mengo.booking.infrastructure.persist

import com.mengo.booking.domain.model.BookingEvent
import com.mengo.booking.domain.service.BookingEventStoreRepository
import com.mengo.booking.infrastructure.persist.mappers.BookingEventEntityMapper
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class BookingEventStoreRepositoryService(
    private val bookingRepository: BookingEventStoreJpaRepository,
    private val bookingEventMapper: BookingEventEntityMapper,
) : BookingEventStoreRepository {
    override fun save(bookingEvent: BookingEvent) {
        val entity = bookingEventMapper.toEntity(bookingEvent)
        bookingRepository.save(entity)
    }

    override fun findById(bookingId: UUID): BookingEvent? {
        val entities = bookingRepository.findByBookingId(bookingId)
        if (entities.isEmpty()) return null

        val latest = entities.maxBy { it.aggregateVersion }
        return bookingEventMapper.toDomain(latest)
    }
}
