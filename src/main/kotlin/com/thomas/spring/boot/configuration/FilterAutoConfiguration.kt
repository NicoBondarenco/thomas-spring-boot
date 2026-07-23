package com.thomas.spring.boot.configuration

import com.thomas.core.authorization.UnauthorizedUserException
import com.thomas.spring.boot.extension.QUERY
import com.thomas.spring.boot.extension.toProblemDetail
import com.thomas.spring.boot.filter.AuthenticationFilter
import com.thomas.spring.boot.filter.LocaleFilter
import com.thomas.spring.boot.filter.SessionContextLifecycleFilter
import com.thomas.spring.boot.filter.TraceIdentifierFilter
import com.thomas.spring.boot.filter.UnityFilter
import com.thomas.spring.boot.handler.SpringBootExceptionHandler
import com.thomas.spring.boot.model.accessor.SessionContextAccessor
import com.thomas.spring.boot.token.TokenDecrypter
import io.micrometer.context.ContextRegistry
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.HEAD
import org.springframework.http.HttpMethod.OPTIONS
import org.springframework.http.HttpMethod.PATCH
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION
import org.springframework.security.config.web.server.SecurityWebFiltersOrder.FIRST
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.firewall.ServerWebExchangeFirewall
import org.springframework.security.web.server.firewall.StrictServerWebExchangeFirewall
import org.springframework.web.server.WebFilter
import reactor.core.publisher.Hooks
import reactor.core.publisher.Mono
import tools.jackson.databind.json.JsonMapper

@AutoConfiguration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Import(SpringBootExceptionHandler::class)
class FilterAutoConfiguration {

    companion object {
        private val ALLOWED_HTTP_METHODS = mutableSetOf(
            DELETE,
            GET,
            HEAD,
            OPTIONS,
            PATCH,
            POST,
            PUT,
            QUERY,
        )
    }

    @PostConstruct
    fun enableContextPropagation() {
        Hooks.enableAutomaticContextPropagation()
    }

    @Bean
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    fun restAuthenticationEntryPoint(
        jsonMapper: JsonMapper
    ): ServerAuthenticationEntryPoint = ServerAuthenticationEntryPoint { exchange, _ ->
        val body = UnauthorizedUserException().toProblemDetail(exchange.request.uri, UNAUTHORIZED)
        val json = jsonMapper.writeValueAsBytes(body)
        exchange.response.statusCode = UNAUTHORIZED
        exchange.response.headers.contentType = APPLICATION_JSON
        exchange.response.writeWith(Mono.just(exchange.response.bufferFactory().wrap(json)))
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    fun serverWebExchangeFirewall(): ServerWebExchangeFirewall = StrictServerWebExchangeFirewall().apply {
        setAllowedHttpMethods(ALLOWED_HTTP_METHODS)
    }

    @Bean
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        tokenDecrypter: TokenDecrypter,
        authenticationEntryPoint: ServerAuthenticationEntryPoint,
    ): SecurityWebFilterChain = http.cors {
        it.disable()
    }.csrf {
        it.disable()
    }.formLogin {
        it.disable()
    }.httpBasic {
        it.disable()
    }.anonymous {
        it.disable()
    }.exceptionHandling {
        it.authenticationEntryPoint(authenticationEntryPoint)
    }.authorizeExchange {
        it.pathMatchers("/public/**").permitAll()
            .pathMatchers("/actuator/**").permitAll()
            .pathMatchers("/v3/api-docs/**").permitAll()
            .pathMatchers("/configuration/**").permitAll()
            .pathMatchers("/swagger-resources/**").permitAll()
            .pathMatchers("/swagger-resources").permitAll()
            .pathMatchers("/swagger-ui/**").permitAll()
            .pathMatchers("/swagger-ui.html").permitAll()
            .pathMatchers("/webjars/**").permitAll()
            .anyExchange().authenticated()
    }.addFilterAt(TraceIdentifierFilter(), FIRST)
        .addFilterAfter(LocaleFilter(), FIRST)
        .addFilterBefore(AuthenticationFilter(tokenDecrypter), AUTHENTICATION)
        .addFilterBefore(UnityFilter(), AUTHENTICATION)
        .build()

    @Bean
    @Order(HIGHEST_PRECEDENCE)
    fun sessionContextLifecycleFilter(): WebFilter = SessionContextLifecycleFilter()

}
