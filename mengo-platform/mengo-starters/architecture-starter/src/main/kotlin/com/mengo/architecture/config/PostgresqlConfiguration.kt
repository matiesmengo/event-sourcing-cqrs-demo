package com.mengo.architecture.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.architecture.inbox.InboxRepository
import com.mengo.architecture.inbox.InboxRepositoryService
import com.mengo.architecture.outbox.OutboxDispatcher
import com.mengo.architecture.outbox.OutboxJpaRepository
import com.mengo.architecture.outbox.OutboxRepository
import com.mengo.architecture.outbox.OutboxRepositoryService
import io.micrometer.core.instrument.MeterRegistry
import jakarta.persistence.EntityManager
import org.apache.avro.specific.SpecificRecord
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.kafka.core.KafkaTemplate

@Configuration
@ConditionalOnClass(JpaRepository::class)
class PostgresqlConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun inboxRepository(
        entityManager: EntityManager,
        @Value("\${spring.jpa.properties.hibernate.default_schema:public}") schema: String,
    ): InboxRepository = InboxRepositoryService(entityManager, schema)

    @Bean
    @ConditionalOnMissingBean
    fun outboxRepository(
        outboxJpaRepository: OutboxJpaRepository,
        objectMapper: ObjectMapper,
    ): OutboxRepository = OutboxRepositoryService(outboxJpaRepository, objectMapper)

    @Bean
    @ConditionalOnMissingBean
    fun outboxDispatcher(
        outboxJpaRepository: OutboxJpaRepository,
        kafkaTemplate: KafkaTemplate<String, SpecificRecord>,
        objectMapper: ObjectMapper,
        meterRegistry: MeterRegistry,
        @Value("\${outbox.dispatcher.max-retries:5}") maxRetries: Int,
    ): OutboxDispatcher =
        OutboxDispatcher(
            outboxJpaRepository,
            kafkaTemplate,
            objectMapper,
            meterRegistry,
            maxRetries,
        )
}
