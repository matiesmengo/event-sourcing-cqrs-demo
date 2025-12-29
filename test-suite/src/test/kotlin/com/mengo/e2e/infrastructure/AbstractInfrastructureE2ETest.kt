package com.mengo.e2e.infrastructure

import com.mengo.architecture.test.infrastructure.KafkaTestClient
import com.mengo.architecture.test.infrastructure.TestContainerFactory
import org.testcontainers.containers.Network
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class AbstractInfrastructureE2ETest {
    companion object {
        val network = Network.newNetwork()

        @Container
        val kafka = TestContainerFactory.createKafka(network)

        @Container
        val schemaRegistry = TestContainerFactory.createSchemaRegistry(network, kafka)

        @Container
        val bookingPostgres = TestContainerFactory.createPostgres(network, "booking", "booking-postgres")

        @Container
        val orchestratorPostgres = TestContainerFactory.createPostgres(network, "orchestrator", "orchestrator-postgres")

        @Container
        val productPostgres = TestContainerFactory.createPostgres(network, "product", "product-postgres")

        @Container
        val paymentPostgres = TestContainerFactory.createPostgres(network, "payment", "payment-postgres")

        fun createKafkaClient(): KafkaTestClient {
            val bootstrap = kafka.bootstrapServers
            val registryUrl = "http://${schemaRegistry.host}:${schemaRegistry.getMappedPort(8081)}"
            return KafkaTestClient(bootstrap, registryUrl)
        }
    }
}
