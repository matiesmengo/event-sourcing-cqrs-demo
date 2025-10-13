package com.mengo.booking.fixtures

import com.mengo.booking.fixtures.BookingConstants.PRODUCT_ID
import com.mengo.booking.fixtures.BookingConstants.USER_ID

val minimalBookingApiRequestJson =
    """
{
  "userId": "$USER_ID",
  "products": [
        {
            "productId": "$PRODUCT_ID",
            "quantity": 2
        }
        
    ]
}
    """.trimIndent().asJson()
