package com.mengo.booking.infrastructure.persist

import com.mengo.booking.domain.model.eventstore.BookingAggregate
import com.mengo.booking.domain.model.eventstore.BookingEvent
import com.mengo.booking.domain.service.BookingEventStoreRepository
import com.mengo.booking.infrastructure.persist.mappers.BookingEventEntityMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
open class BookingEventStoreRepositoryService(
    private val entityManager: EntityManager,
    private val bookingRepository: BookingEventStoreJpaRepository,
    private val bookingEventMapper: BookingEventEntityMapper,
) : BookingEventStoreRepository {
    @Transactional(propagation = Propagation.MANDATORY)
    override fun load(bookingId: UUID): BookingAggregate? {
        val lockId = bookingId.mostSignificantBits
        entityManager
            .createNativeQuery("SELECT pg_advisory_xact_lock(:lockId)")
            .setParameter("lockId", lockId)
            .singleResult

        val entities = bookingRepository.findByBookingIdOrderByAggregateVersionAsc(bookingId)
        if (entities.isEmpty()) return null

        val events = entities.map(bookingEventMapper::toDomain)
        return BookingAggregate.rehydrate(events)
    }

    @Transactional(propagation = Propagation.MANDATORY)
    override fun append(event: BookingEvent) {
        bookingRepository.save(bookingEventMapper.toEntity(event))
    }
}
