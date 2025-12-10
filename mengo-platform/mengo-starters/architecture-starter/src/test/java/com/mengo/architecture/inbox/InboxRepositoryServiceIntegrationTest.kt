package com.mengo.architecture.inbox

import com.mengo.architecture.metadata.Metadata
import com.mengo.architecture.metadata.MetadataContextHolder
import com.mengo.architecture.test.ContainerBase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InboxRepositoryServiceIntegrationTest
    @Autowired
    constructor(
        private val inboxJpaRepository: InboxJpaRepository,
        private val inboxRepositoryService: InboxRepositoryService,
        transactionManager: PlatformTransactionManager,
    ) : ContainerBase() {
        private val transactionTemplate: TransactionTemplate = TransactionTemplate(transactionManager)

        companion object {
            private val CORRELATION_ID: UUID = UUID.randomUUID()
            private val CAUSATION_ID: UUID = UUID.randomUUID()
        }

        @BeforeEach
        fun cleanup() {
            inboxJpaRepository.deleteAll()

            MetadataContextHolder.set(
                Metadata(
                    correlationId = CORRELATION_ID,
                    causationId = CAUSATION_ID,
                ),
            )
        }

        @Test
        fun `registerNew should return true and persist the entry for a new causationId`() {
            // when
            transactionTemplate.execute {
                val result = inboxRepositoryService.validateIdempotencyEvent()

                // then
                assertTrue(result)
                assertEquals(1, inboxJpaRepository.countByCausationId(CAUSATION_ID))
            }
        }

        @Test
        fun `registerNew should return false for a duplicate causationId within the same transaction`() {
            // given
            transactionTemplate.execute {
                val firstAttempt = inboxRepositoryService.validateIdempotencyEvent()
                assertTrue(firstAttempt)
            }

            // when
            transactionTemplate.execute {
                val secondAttempt = inboxRepositoryService.validateIdempotencyEvent()
                assertFalse(secondAttempt)
            }

            // then
            val countInTransaction = inboxJpaRepository.countByCausationId(CAUSATION_ID)
            assertEquals(1, countInTransaction)
        }

        @Test
        fun `registration should commit the entry even if the outer saga logic fails later`() {
            // when
            assertThrows<RuntimeException> {
                transactionTemplate.execute {
                    val isNew = inboxRepositoryService.validateIdempotencyEvent()
                    assertTrue(isNew)
                    throw RuntimeException("Simulated ES Concurrency Conflict or Domain Error")
                }
            }

            // then
            assertEquals(1, inboxJpaRepository.countByCausationId(CAUSATION_ID))
            transactionTemplate.execute {
                val retryResult = inboxRepositoryService.validateIdempotencyEvent()
                assertFalse(retryResult)
            }
        }
    }
