package com.mengo.postgres.test

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
@ActiveProfiles("integration-postgresql")
abstract class PostgresTestContainerBase {

    companion object {
        @Container
        val postgresContainer =
            PostgreSQLContainer<Nothing>("postgres:15.3").apply {
                withDatabaseName("testdb")
                withUsername("test")
                withPassword("test")
            }

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgresContainer.jdbcUrl }
            registry.add("spring.datasource.username") { postgresContainer.username }
            registry.add("spring.datasource.password") { postgresContainer.password }
            registry.add("spring.datasource.driver-class-name") { postgresContainer.driverClassName }
        }
    }
}
