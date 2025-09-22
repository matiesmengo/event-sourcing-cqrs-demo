package com.mengo.booking.infrastructure.persist

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BookingJpaRepository : JpaRepository<BookingEntity, UUID>
