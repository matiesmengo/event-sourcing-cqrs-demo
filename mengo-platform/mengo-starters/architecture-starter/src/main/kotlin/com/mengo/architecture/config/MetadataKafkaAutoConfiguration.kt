package com.mengo.architecture.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.architecture.metadata.MetadataRecordInterceptor
import io.micrometer.tracing.Tracer
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory

@Configuration
@EnableKafka
open class MetadataKafkaAutoConfiguration(
    private val objectMapper: ObjectMapper,
    private val tracer: Tracer,
) {
    @Bean
    open fun metadataRecordInterceptor(): MetadataRecordInterceptor = MetadataRecordInterceptor(objectMapper, tracer)

    @Bean
    open fun kafkaListenerContainerFactory(
        configurer: ConcurrentKafkaListenerContainerFactoryConfigurer,
        consumerFactory: ConsumerFactory<Any, Any>,
        metadataInterceptor: MetadataRecordInterceptor,
    ): ConcurrentKafkaListenerContainerFactory<Any, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<Any, Any>()
        configurer.configure(factory, consumerFactory)
        factory.setRecordInterceptor(metadataInterceptor)
        return factory
    }
}
