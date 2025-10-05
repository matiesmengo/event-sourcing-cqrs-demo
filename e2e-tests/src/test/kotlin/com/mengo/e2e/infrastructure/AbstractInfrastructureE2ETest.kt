package com.mengo.e2e.infrastructure

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.sql.DriverManager

@Testcontainers
abstract class AbstractInfrastructureE2ETest {
    companion object {
        val network: Network = Network.newNetwork()

        @Container
        val kafka =
            KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.1"))
                .withNetwork(network)
                .withNetworkAliases("kafka")

        @Container
        val schemaRegistry =
            GenericContainer("confluentinc/cp-schema-registry:8.0.0")
                .withExposedPorts(8081)
                .withNetwork(network)
                .withNetworkAliases("schema-registry")
                .dependsOn(kafka)
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://kafka:9092")
                .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
                .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
                .waitingFor(Wait.forHttp("/subjects").forStatusCode(200))

        @Container
        val bookingPostgres =
            PostgreSQLContainer("postgres:16")
                .withDatabaseName("booking")
                .withUsername("user")
                .withPassword("pass")
                .withNetwork(network)
                .withNetworkAliases("booking-postgres")

        @Container
        val paymentPostgres =
            PostgreSQLContainer("postgres:16")
                .withDatabaseName("payment")
                .withUsername("user")
                .withPassword("pass")
                .withNetwork(network)
                .withNetworkAliases("payment-postgres")
    }

    fun queryStatus(
        jdbcUrl: String,
        schema: String,
        username: String,
        password: String,
        query: String,
        parameters: List<Any?> = emptyList(),
    ): Boolean {
        val urlWithSchema = if (jdbcUrl.contains("?")) "$jdbcUrl&currentSchema=$schema" else "$jdbcUrl?currentSchema=$schema"

        DriverManager.getConnection(urlWithSchema, username, password).use { conn ->
            conn.prepareStatement(query).use { stmt ->
                parameters.forEachIndexed { index, param ->
                    stmt.setObject(index + 1, param)
                }

                stmt.executeQuery().use { rs ->
                    return rs.next()
                }
            }
        }
    }
}
