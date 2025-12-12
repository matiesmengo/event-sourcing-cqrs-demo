package com.mengo.architecture.test.infrastructure

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

object TestContainerFactory {
    private const val POSTGRES_IMAGE = "postgres:16"
    private const val KAFKA_IMAGE = "confluentinc/cp-kafka:7.5.1"
    private const val SCHEMA_REGISTRY_IMAGE = "confluentinc/cp-schema-registry:7.5.1"

    fun createPostgres(
        network: Network,
        dbName: String,
        alias: String,
    ): PostgreSQLContainer<*> =
        PostgreSQLContainer(DockerImageName.parse(POSTGRES_IMAGE))
            .withDatabaseName(dbName)
            .withUsername("user")
            .withPassword("pass")
            .withNetwork(network)
            .withNetworkAliases(alias)

    fun createKafka(network: Network): KafkaContainer =
        KafkaContainer(DockerImageName.parse(KAFKA_IMAGE))
            .withNetwork(network)
            .withNetworkAliases("kafka")

    fun createSchemaRegistry(
        network: Network,
        kafka: KafkaContainer,
    ): GenericContainer<*> =
        GenericContainer(DockerImageName.parse(SCHEMA_REGISTRY_IMAGE))
            .withExposedPorts(8081)
            .withNetwork(network)
            .withNetworkAliases("schema-registry")
            .dependsOn(kafka)
            .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://kafka:9092")
            .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
            .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
            .waitingFor(Wait.forHttp("/subjects").forStatusCode(200))

    fun createMicroservice(
        imageName: String,
        port: Int,
        network: Network,
        dbAlias: String,
        dbName: String,
        logWait: String,
    ): GenericContainer<*> =
        GenericContainer(DockerImageName.parse(imageName))
            .withExposedPorts(port)
            .withNetwork(network)
            .withEnv("SPRING_DATASOURCE_URL", "jdbc:postgresql://$dbAlias:5432/$dbName")
            .withEnv("SPRING_DATASOURCE_USERNAME", "user")
            .withEnv("SPRING_DATASOURCE_PASSWORD", "pass")
            .withEnv("SPRING_KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")
            .withEnv("SCHEMA_REGISTRY_URL", "http://schema-registry:8081")
            .withEnv("SPRING_KAFKA_CONSUMER_PROPERTIES_SCHEMA_REGISTRY_URL", "http://schema-registry:8081")
            .withEnv("SPRING_KAFKA_PRODUCER_PROPERTIES_SCHEMA_REGISTRY_URL", "http://schema-registry:8081")
            .waitingFor(Wait.forLogMessage(logWait, 1))
}
