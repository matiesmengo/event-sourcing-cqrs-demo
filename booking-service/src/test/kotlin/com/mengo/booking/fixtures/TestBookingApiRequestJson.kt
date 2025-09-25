package com.mengo.booking.fixtures

import com.mengo.booking.fixtures.BookingTestData.RESOURCE_ID
import com.mengo.booking.fixtures.BookingTestData.USER_ID

val minimalBookingApiRequestJson =
    """
{
  "userId": "$USER_ID",
  "resourceId": "$RESOURCE_ID"
}
    """.trimIndent().asJson()
