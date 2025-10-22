package com.mengo.booking.infrastructure.persist

import com.mengo.booking.domain.model.eventstore.BookingAggregate
import com.mengo.booking.domain.model.eventstore.BookingEvent
import com.mengo.booking.domain.service.BookingEventStoreRepository
import com.mengo.booking.infrastructure.persist.mappers.BookingEventEntityMapper
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class BookingEventStoreRepositoryService(
    private val bookingRepository: BookingEventStoreJpaRepository,
    private val bookingEventMapper: BookingEventEntityMapper,
) : BookingEventStoreRepository {
    override fun load(bookingId: UUID): BookingAggregate? {
        val entities = bookingRepository.findByBookingIdOrderByAggregateVersionAsc(bookingId)
        if (entities.isEmpty()) return null

        val events = entities.map(bookingEventMapper::toDomain)
        return BookingAggregate.rehydrate(events)
    }

    override fun append(event: BookingEvent) {
        val currentVersion =
            bookingRepository
                .findFirstByBookingIdOrderByAggregateVersionDesc(event.bookingId)
                ?.aggregateVersion
                ?: -1

        require(event.aggregateVersion == currentVersion + 1) {
            "Concurrency conflict: expected=${event.aggregateVersion}, actual=$currentVersion"
        }

        bookingRepository.save(bookingEventMapper.toEntity(event))
    }
}
