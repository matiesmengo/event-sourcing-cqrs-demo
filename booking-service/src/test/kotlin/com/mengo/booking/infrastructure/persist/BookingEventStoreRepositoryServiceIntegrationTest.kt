package com.mengo.booking.infrastructure.persist

import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.model.eventstore.BookingAggregateStatus
import com.mengo.booking.domain.model.eventstore.BookingConfirmedEvent
import com.mengo.booking.domain.model.eventstore.BookingCreatedEvent
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import com.mengo.booking.infrastructure.persist.mappers.BookingEventEntityMapper
import com.mengo.postgres.test.PostgresTestContainerBase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.InvalidDataAccessApiUsageException
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BookingEventStoreRepositoryServiceIntegrationTest
    @Autowired
    constructor(
        private val jpaRepository: BookingEventStoreJpaRepository,
        private val mapper: BookingEventEntityMapper,
        private val repository: BookingEventStoreRepositoryService,
    ) : PostgresTestContainerBase() {
        @BeforeEach
        fun cleanup() {
            jpaRepository.deleteAll()
        }

        @Test
        fun `load should return null when no events exist`() {
            val result = repository.load(BOOKING_ID)
            assertNull(result)
        }

        @Test
        fun `load should rehydrate BookingAggregate from stored events`() {
            // given
            val products = listOf<BookingItem>()
            val createdEvent = BookingCreatedEvent(BOOKING_ID, USER_ID, products, 0)
            val confirmedEvent = BookingConfirmedEvent(BOOKING_ID, 1)

            jpaRepository.saveAll(
                listOf(
                    mapper.toEntity(createdEvent),
                    mapper.toEntity(confirmedEvent),
                ),
            )

            // when
            val aggregate = repository.load(BOOKING_ID)

            // then
            assertNotNull(aggregate)
            assertEquals(BOOKING_ID, aggregate.bookingId)
            assertEquals(USER_ID, aggregate.userId)
            assertEquals(BookingAggregateStatus.CONFIRMED, aggregate.status)
            assertEquals(1, aggregate.lastEventVersion)
        }

        @Test
        fun `append should persist event when version is correct`() {
            // given
            val products = listOf<BookingItem>()
            val createdEvent = BookingCreatedEvent(BOOKING_ID, USER_ID, products, 0)

            // when
            repository.append(createdEvent)

            // then
            val stored = jpaRepository.findByBookingIdOrderByAggregateVersionAsc(BOOKING_ID)
            assertEquals(1, stored.size)
            assertEquals(0, stored.first().aggregateVersion)
        }

        @Test
        fun `append should throw on concurrency conflict`() {
            val product = BookingItem(UUID.randomUUID(), 1)
            val firstEvent = BookingCreatedEvent(BOOKING_ID, USER_ID, listOf(product), 0)
            val secondEvent = BookingConfirmedEvent(BOOKING_ID, 5)

            // when
            jpaRepository.save(mapper.toEntity(firstEvent))

            // when + then
            val ex = assertFailsWith<InvalidDataAccessApiUsageException> { repository.append(secondEvent) }
            assert(ex.message!!.contains("Concurrency conflict"))
        }
    }
