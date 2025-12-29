package com.mengo.architecture.config

import com.mengo.architecture.metadata.MetadataHttpInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
open class WebMvcConfiguration(
    private val metadataHttpInterceptor: MetadataHttpInterceptor,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(metadataHttpInterceptor)
            .addPathPatterns("/**")
            .order(Ordered.HIGHEST_PRECEDENCE)
    }
}
