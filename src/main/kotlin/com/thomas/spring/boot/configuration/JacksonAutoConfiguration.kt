package com.thomas.spring.boot.configuration

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY
import com.fasterxml.jackson.annotation.PropertyAccessor.ALL
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.json.ProblemDetailJacksonMixin
import org.springframework.http.support.JacksonHandlerInstantiator
import tools.jackson.core.StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN
import tools.jackson.core.json.JsonFactory
import tools.jackson.databind.DeserializationFeature.FAIL_ON_INVALID_SUBTYPE
import tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import tools.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import tools.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS
import tools.jackson.databind.cfg.DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS
import tools.jackson.databind.json.JsonMapper
import tools.jackson.datatype.javax.money.JavaxMoneyModule
import tools.jackson.module.blackbird.BlackbirdModule
import tools.jackson.module.kotlin.KotlinFeature.NullIsSameAsDefault
import tools.jackson.module.kotlin.KotlinFeature.NullToEmptyCollection
import tools.jackson.module.kotlin.KotlinFeature.NullToEmptyMap
import tools.jackson.module.kotlin.KotlinFeature.SingletonSupport
import tools.jackson.module.kotlin.KotlinFeature.StrictNullChecks
import tools.jackson.module.kotlin.KotlinModule
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration as SpringJacksonAutoConfiguration

@AutoConfiguration(before = [SpringJacksonAutoConfiguration::class])
class JacksonAutoConfiguration {

    companion object {
        private const val REFLECTION_CACHE_SIZE = 512
    }

    private val kotlinModule = KotlinModule.Builder()
        .withReflectionCacheSize(REFLECTION_CACHE_SIZE)
        .configure(NullToEmptyCollection, false)
        .configure(NullToEmptyMap, false)
        .configure(NullIsSameAsDefault, false)
        .configure(SingletonSupport, false)
        .configure(StrictNullChecks, false)
        .build()

    private val blackbirdModule: BlackbirdModule = BlackbirdModule()

    private val moneyModule = JavaxMoneyModule()

    @Bean
    @Primary
    @ConditionalOnMissingBean
    fun jacksonJsonMapper(
        customizers: ObjectProvider<JsonMapperBuilderCustomizer>,
        beanFactory: AutowireCapableBeanFactory,
        jsonFactoryProvider: ObjectProvider<JsonFactory>
    ): JsonMapper = (jsonFactoryProvider.getIfAvailable()?.let { JsonMapper.builder(it) } ?: JsonMapper.builder())
        .addModule(kotlinModule)
        .addModule(blackbirdModule)
        .addModule(moneyModule)
        .propertyNamingStrategy(SNAKE_CASE)
        .changeDefaultVisibility { it.withVisibility(ALL, ANY) }
        .enable(WRITE_BIGDECIMAL_AS_PLAIN)
        .disable(FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(FAIL_ON_INVALID_SUBTYPE)
        .disable(WRITE_DATES_AS_TIMESTAMPS)
        .disable(FAIL_ON_EMPTY_BEANS)
        .handlerInstantiator(JacksonHandlerInstantiator(beanFactory))
        .addMixIn(ProblemDetail::class.java, ProblemDetailJacksonMixin::class.java)
        .apply {
            customizers.orderedStream().forEach { it.customize(this) }
        }
        .build()

}
