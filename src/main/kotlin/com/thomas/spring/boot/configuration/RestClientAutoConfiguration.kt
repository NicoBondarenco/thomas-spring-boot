package com.thomas.spring.boot.configuration

import com.thomas.spring.boot.properties.RestClientProperties
import java.util.concurrent.TimeUnit.MILLISECONDS
import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.core5.util.TimeValue
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.restclient.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
import org.springframework.web.client.RestClient
import org.springframework.boot.restclient.autoconfigure.RestClientAutoConfiguration as SpringRestClientAutoConfiguration

@AutoConfiguration(before = [SpringRestClientAutoConfiguration::class, HttpMessageConvertersAutoConfiguration::class])
@ConditionalOnClass(RestClient::class, PoolingHttpClientConnectionManager::class)
@EnableConfigurationProperties(RestClientProperties::class)
class RestClientAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean
    fun connectionConfig(
        properties: RestClientProperties
    ): ConnectionConfig = ConnectionConfig.custom()
        .setConnectTimeout(properties.clientConnectionTimeout, MILLISECONDS)
        .setSocketTimeout(properties.clientReadTimeout, MILLISECONDS)
        .setIdleTimeout(properties.connectionIdleTimeout, MILLISECONDS)
        .setTimeToLive(properties.timeToLive, MILLISECONDS)
        .build()

    @Bean
    @Primary
    @ConditionalOnMissingBean
    fun poolingConnectionManager(
        properties: RestClientProperties,
        connectionConfig: ConnectionConfig
    ): PoolingHttpClientConnectionManager = PoolingHttpClientConnectionManager().also { pool ->
        pool.maxTotal = properties.maxPoolConnections
        pool.defaultMaxPerRoute = properties.maxRouteConnections
        pool.setDefaultConnectionConfig(connectionConfig)
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    fun requestConfig(
        properties: RestClientProperties
    ): RequestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout(properties.requestConnectionTimeout, MILLISECONDS)
        .setResponseTimeout(properties.requestResponseTimeout, MILLISECONDS)
        .build()

    @Bean
    @Primary
    @ConditionalOnMissingBean
    fun httpClients(
        properties: RestClientProperties,
        connectionManager: PoolingHttpClientConnectionManager,
        requestConfig: RequestConfig,
    ): CloseableHttpClient = HttpClients.custom()
        .setConnectionManager(connectionManager)
        .setDefaultRequestConfig(requestConfig)
        .evictExpiredConnections()
        .evictIdleConnections(TimeValue.ofMilliseconds(properties.connectionIdleTimeout))
        .build()

    @Bean
    @Primary
    @ConditionalOnMissingBean
    fun clientHttpRequestFactory(
        httpClient: CloseableHttpClient
    ): HttpComponentsClientHttpRequestFactory = HttpComponentsClientHttpRequestFactory(httpClient)

    @Bean
    @Primary
    @ConditionalOnMissingBean
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    fun restTemplateCustomizer(
        requestFactory: HttpComponentsClientHttpRequestFactory,
        jacksonConverter: JacksonJsonHttpMessageConverter
    ): RestClientCustomizer = RestClientCustomizer { builder ->
        builder.requestFactory(requestFactory)
        builder.configureMessageConverters { converters ->
            converters.registerDefaults()
            converters.withJsonConverter(jacksonConverter)
        }
        builder.requestInterceptors { interceptors ->
            interceptors.add { request, body, execution ->
                request.headers[ACCEPT] = APPLICATION_JSON_VALUE
                execution.execute(request, body)
            }
        }
    }

}
