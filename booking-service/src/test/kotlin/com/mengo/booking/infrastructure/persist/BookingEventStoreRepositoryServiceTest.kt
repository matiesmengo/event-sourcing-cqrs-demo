import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mengo.booking.domain.model.BookingCreatedEvent
import com.mengo.booking.domain.model.BookingPaymentConfirmedEvent
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingTestData.buildBookingCreatedEvent
import com.mengo.booking.fixtures.BookingTestData.buildBookingPaymentConfirmedEvent
import com.mengo.booking.fixtures.BookingTestData.buildBookingPaymentFailedEvent
import com.mengo.booking.infrastructure.persist.BookingEventStoreJpaRepository
import com.mengo.booking.infrastructure.persist.BookingEventStoreRepositoryService
import com.mengo.booking.infrastructure.persist.mappers.BookingEventEntityMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BookingEventStoreRepositoryServiceTest {
    private val bookingRepository: BookingEventStoreJpaRepository = mock()
    private val bookingEventMapper =
        BookingEventEntityMapper(
            ObjectMapper().apply {
                registerModule(KotlinModule.Builder().build())
                registerModule(JavaTimeModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            },
        )
    private val repository = BookingEventStoreRepositoryService(bookingRepository, bookingEventMapper)

    @Test
    fun `save should persist BookingCreatedEvent`() {
        val event = buildBookingCreatedEvent()

        repository.save(event)

        verify(bookingRepository).save(
            check { entity ->
                assert(entity.bookingId == event.bookingId)
                assert(entity.aggregateVersion == 1)
            },
        )
    }

    @Test
    fun `save should persist BookingPaymentConfirmedEvent`() {
        val event = buildBookingPaymentConfirmedEvent()

        repository.save(event)

        verify(bookingRepository).save(
            check { entity ->
                assert(entity.bookingId == event.bookingId)
                assert(entity.aggregateVersion == 2)
            },
        )
    }

    @Test
    fun `save should persist BookingPaymentFailedEvent`() {
        val event = buildBookingPaymentFailedEvent()

        repository.save(event)

        verify(bookingRepository).save(
            check { entity ->
                assert(entity.bookingId == event.bookingId)
                assert(entity.aggregateVersion == 2)
            },
        )
    }

    @Test
    fun `findById should return mapped domain when entity exists`() {
        val event = buildBookingCreatedEvent()

        val entity = bookingEventMapper.toEntity(event)
        whenever(bookingRepository.findByBookingId(event.bookingId)).thenReturn(listOf(entity))

        val result = repository.findById(event.bookingId)

        verify(bookingRepository).findByBookingId(event.bookingId)
        assertEquals(event.bookingId, result?.bookingId)
        assertEquals(event.userId, (result as BookingCreatedEvent).userId)
    }

    @Test
    fun `findById should return null when entity does not exist`() {
        whenever(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(emptyList())

        val result = repository.findById(BOOKING_ID)

        verify(bookingRepository).findByBookingId(BOOKING_ID)
        assertNull(result)
    }

    @Test
    fun `findById should return latest BookingEvent when multiple events exist`() {
        // arrange
        val createdEvent = buildBookingCreatedEvent()
        val confirmedEvent = buildBookingPaymentConfirmedEvent()

        val entities = listOf(bookingEventMapper.toEntity(createdEvent), bookingEventMapper.toEntity(confirmedEvent))
        whenever(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(entities)

        // act
        val result = repository.findById(BOOKING_ID)

        // assert
        assertNotNull(result)
        assertEquals(confirmedEvent.aggregateVersion, result.aggregateVersion)
        assertTrue(result is BookingPaymentConfirmedEvent)
    }

    @Test
    fun `findById should return BookingCreatedEvent when only one event exists`() {
        val createdEvent = buildBookingCreatedEvent()

        whenever(bookingRepository.findByBookingId(BOOKING_ID))
            .thenReturn(listOf(bookingEventMapper.toEntity(createdEvent)))

        val result = repository.findById(BOOKING_ID)

        assertNotNull(result)
        assertEquals(createdEvent.aggregateVersion, result?.aggregateVersion)
        assertTrue(result is BookingCreatedEvent)
    }

    @Test
    fun `findById should return null when no events exist`() {
        whenever(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(emptyList())

        val result = repository.findById(BOOKING_ID)

        assertNull(result)
    }
}
