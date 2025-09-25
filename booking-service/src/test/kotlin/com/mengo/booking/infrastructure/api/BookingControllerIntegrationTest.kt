package com.mengo.booking.infrastructure.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.booking.application.BookingService
import com.mengo.booking.fixtures.BookingTestData.BOOKING_ID
import com.mengo.booking.fixtures.BookingTestData.RESOURCE_ID
import com.mengo.booking.fixtures.BookingTestData.USER_ID
import com.mengo.booking.fixtures.BookingTestData.buildBookingDomain
import com.mengo.booking.fixtures.minimalBookingApiRequestJson
import com.mengo.booking.model.BookingResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var bookingService: BookingService

    @Test
    fun `bookingsPost should return 200 OK and call service`() {
        // given
        val bookingDomain = buildBookingDomain()
        whenever(bookingService.createBooking(any())).thenReturn(bookingDomain)

        // when
        val mvcResult =
            mockMvc
                .perform(
                    post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(minimalBookingApiRequestJson.asString()),
                ).andExpect(status().isOk)
                .andReturn()

        // then
        val responseBody = mvcResult.response.contentAsString
        val response = objectMapper.readValue(responseBody, BookingResponse::class.java)

        // from request body to domain mapper
        verify(bookingService).createBooking(
            check {
                assertEquals(USER_ID, it.userId)
                assertEquals(RESOURCE_ID, it.resourceId)
            },
        )

        // from domain to api mapper
        assertEquals(BOOKING_ID, response.bookingId)
        assertEquals(USER_ID, response.userId)
        assertEquals(RESOURCE_ID, response.resourceId)
        assertEquals(BookingResponse.StatusEnum.CREATED, response.status)
    }
}
