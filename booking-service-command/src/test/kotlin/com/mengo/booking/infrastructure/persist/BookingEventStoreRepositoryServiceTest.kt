import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.model.eventstore.BookingAggregateStatus
import com.mengo.booking.domain.model.eventstore.BookingConfirmedEvent
import com.mengo.booking.domain.model.eventstore.BookingCreatedEvent
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import com.mengo.booking.infrastructure.persist.BookingEventStoreJpaRepository
import com.mengo.booking.infrastructure.persist.BookingEventStoreRepositoryService
import com.mengo.booking.infrastructure.persist.mappers.BookingEventEntityMapper
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BookingEventStoreRepositoryServiceTest {
    private val query: jakarta.persistence.Query = mock()
    private val entityManager: EntityManager = mock()
    private val bookingRepository: BookingEventStoreJpaRepository = mock()
    private val bookingEventMapper =
        BookingEventEntityMapper(
            ObjectMapper().apply {
                registerModule(KotlinModule.Builder().build())
                registerModule(JavaTimeModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            },
        )
    private val repository = BookingEventStoreRepositoryService(entityManager, bookingRepository, bookingEventMapper)

    @Test
    fun `load should return null when no events exist`() {
        // given
        whenever(entityManager.createNativeQuery(any())).thenReturn(query)
        whenever(query.setParameter(any<String>(), any())).thenReturn(query)
        whenever(query.singleResult).thenReturn(1)
        whenever(bookingRepository.findByBookingIdOrderByAggregateVersionAsc(BOOKING_ID)) doReturn emptyList()

        // when
        val result = repository.load(BOOKING_ID)

        // then
        assertNull(result)
        verify(bookingRepository).findByBookingIdOrderByAggregateVersionAsc(BOOKING_ID)
    }

    @Test
    fun `load should rehydrate BookingAggregate from stored events`() {
        // given
        whenever(entityManager.createNativeQuery(any())).thenReturn(query)
        whenever(query.setParameter(any<String>(), any())).thenReturn(query)
        whenever(query.singleResult).thenReturn(1)

        val products = listOf<BookingItem>()

        val createdEvent = BookingCreatedEvent(BOOKING_ID, USER_ID, products, 0)
        val confirmedEvent = BookingConfirmedEvent(BOOKING_ID, 1)

        val createdEntity = bookingEventMapper.toEntity(createdEvent)
        val confirmedEntity = bookingEventMapper.toEntity(confirmedEvent)

        whenever(bookingRepository.findByBookingIdOrderByAggregateVersionAsc(BOOKING_ID))
            .thenReturn(listOf(createdEntity, confirmedEntity))

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
        val createdEntity = bookingEventMapper.toEntity(createdEvent)

        whenever(bookingRepository.save(any())).thenReturn(createdEntity)

        // when
        repository.append(createdEvent)

        // then
        verify(bookingRepository).save(any())
    }
}
