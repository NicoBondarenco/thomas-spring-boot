package com.thomas.spring.boot.configuration

import com.thomas.core.context.SessionContextHolder
import com.thomas.spring.boot.extension.TRACE_HEADER_TOKEN
import com.thomas.spring.boot.properties.WebClientProperties
import io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import java.time.Duration
import java.util.concurrent.TimeUnit.MILLISECONDS
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.http.codec.autoconfigure.CodecsAutoConfiguration
import org.springframework.boot.webclient.WebClientCustomizer
import org.springframework.boot.webclient.autoconfigure.WebClientAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.FormHttpMessageWriter
import org.springframework.http.codec.json.JacksonJsonDecoder
import org.springframework.http.codec.json.JacksonJsonEncoder
import org.springframework.http.codec.multipart.MultipartHttpMessageWriter
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import tools.jackson.databind.json.JsonMapper

@AutoConfiguration(before = [WebClientAutoConfiguration::class, CodecsAutoConfiguration::class])
@ConditionalOnClass(WebClient::class)
@EnableConfigurationProperties(WebClientProperties::class)
class WebClientCoroutinesAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean
    fun connectionProvider(
        properties: WebClientProperties
    ): ConnectionProvider = ConnectionProvider.builder(properties.connectionProviderName)
        .maxConnections(properties.providerMaxConnections)
        .pendingAcquireMaxCount(properties.pendingAcquireCount)
        .pendingAcquireTimeout(Duration.ofMillis(properties.pendingAcquireTimeout))
        .maxIdleTime(Duration.ofMillis(properties.maxIdleTime))
        .build()

    @Bean
    @Primary
    @ConditionalOnMissingBean
    fun clientHttpConnector(
        properties: WebClientProperties,
        connectionProvider: ConnectionProvider
    ): ClientHttpConnector = HttpClient.create(connectionProvider)
        .option(CONNECT_TIMEOUT_MILLIS, properties.clientConnectionTimeout.toInt())
        .responseTimeout(Duration.ofMillis(properties.clientResponseTimeout))
        .wiretap(properties.clientWiretapEnabled)
        .doOnConnected { conn ->
            conn.addHandlerLast(ReadTimeoutHandler(properties.clientReadTimeout, MILLISECONDS))
            conn.addHandlerLast(WriteTimeoutHandler(properties.clientWriteTimeout, MILLISECONDS))
        }.let { ReactorClientHttpConnector(it) }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    fun exchangeStrategies(
        jsonMapper: JsonMapper,
        properties: WebClientProperties
    ): ExchangeStrategies = ExchangeStrategies.builder().codecs { configurer ->
        configurer.defaultCodecs().jacksonJsonEncoder(JacksonJsonEncoder(jsonMapper))
        configurer.defaultCodecs().jacksonJsonDecoder(JacksonJsonDecoder(jsonMapper))
        configurer.customCodecs().register(FormHttpMessageWriter())
        configurer.customCodecs().register(MultipartHttpMessageWriter())
        configurer.defaultCodecs().maxInMemorySize(properties.clientPayloadSize)
    }.build()

    @Bean
    @Primary
    @ConditionalOnMissingBean
    fun webClientCustomizer(
        httpClient: ClientHttpConnector,
        exchangeStrategies: ExchangeStrategies,
    ): WebClientCustomizer = WebClientCustomizer { builder ->
        builder.clientConnector(httpClient)
        builder.exchangeStrategies(exchangeStrategies)
        builder.defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
    }

}
