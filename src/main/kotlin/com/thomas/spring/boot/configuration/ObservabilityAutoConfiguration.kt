package com.thomas.spring.boot.configuration

import com.thomas.spring.boot.properties.ObservabilityProperties
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean

@AutoConfiguration
@EnableConfigurationProperties(ObservabilityProperties::class)
class ObservabilityAutoConfiguration {

    companion object {
        private const val APPLICATION_TAG_NAME = "application"
        private const val INSTANCE_TAG_NAME = "app-instance"
    }

    @Bean
    fun meterRegistryCustomizer(
        properties: ObservabilityProperties,
    ): MeterRegistryCustomizer<MeterRegistry> = MeterRegistryCustomizer { registry ->
        registry.config().commonTags(APPLICATION_TAG_NAME, properties.application)
        registry.config().commonTags(INSTANCE_TAG_NAME, properties.instance)
    }

}
