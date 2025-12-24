package com.mengo.booking.infrastructure.persist

import org.springframework.data.mongodb.repository.MongoRepository

interface BookingProjectionMongoRepository : MongoRepository<BookingProjectionEntity, String>
