package com.mengo.architecture.config

import com.mengo.architecture.metadata.MetadataHttpInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
open class WebMvcConfiguration : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(MetadataHttpInterceptor())
            .addPathPatterns("/**")
    }
}
