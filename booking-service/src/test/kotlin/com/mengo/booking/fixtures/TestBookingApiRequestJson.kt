package com.mengo.booking.fixtures

import com.mengo.booking.fixtures.BookingConstants.RESOURCE_ID
import com.mengo.booking.fixtures.BookingConstants.USER_ID

val minimalBookingApiRequestJson =
    """
{
  "userId": "$USER_ID",
  "resourceId": "$RESOURCE_ID"
}
    """.trimIndent().asJson()
