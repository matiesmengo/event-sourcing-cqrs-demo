package com.mengo.booking.infrastructure.api

import com.mengo.booking.domain.model.BookingCommand
import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.model.BookingStatus
import com.mengo.booking.domain.service.BookingProjectionRepository
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.PRODUCT_ID
import com.mengo.booking.fixtures.BookingConstants.PRODUCT_PRICE
import com.mengo.booking.fixtures.BookingConstants.PRODUCT_QUANTITY
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc
class BookingQueryControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repository: BookingProjectionRepository

    @Test
    fun `should return booking when it exists in mongodb`() {
        // given
        val product = BookingItem(PRODUCT_ID, PRODUCT_QUANTITY, PRODUCT_PRICE)
        val reservation =
            BookingCommand.Create(
                bookingId = BOOKING_ID,
                userId = USER_ID,
                items = mutableListOf(product),
                timestamp = Instant.now(),
            )
        repository.save(reservation)

        // when then
        mockMvc
            .perform(
                get("/bookings/$BOOKING_ID")
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.bookingId").value(BOOKING_ID.toString()))
            .andExpect(jsonPath("$.userId").value(USER_ID.toString()))
            .andExpect(jsonPath("$.status").value(BookingStatus.CREATED.toString()))
            .andExpect(jsonPath("$.totalPrice").value(PRODUCT_QUANTITY.toBigDecimal() * PRODUCT_PRICE))
            .andExpect(jsonPath("$.products[0].quantity").value(PRODUCT_QUANTITY))
    }
}
