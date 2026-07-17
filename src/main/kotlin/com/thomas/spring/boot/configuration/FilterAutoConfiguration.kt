package com.thomas.spring.boot.configuration

import com.thomas.core.authorization.UnauthorizedUserException
import com.thomas.spring.boot.extension.toProblemDetail
import com.thomas.spring.boot.filter.AuthenticationFilter
import com.thomas.spring.boot.filter.TraceIdentifierFilter
import com.thomas.spring.boot.filter.SessionContextLifecycleFilter
import com.thomas.spring.boot.filter.UnityFilter
import com.thomas.spring.boot.handler.SpringBootExceptionHandler
import com.thomas.spring.boot.token.TokenDecrypter
import java.net.URI
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import reactor.core.publisher.Mono
import tools.jackson.databind.json.JsonMapper

@AutoConfiguration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Import(SpringBootExceptionHandler::class)
class FilterAutoConfiguration {

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
    }.addFilterBefore(
        AuthenticationFilter(tokenDecrypter),
        UsernamePasswordAuthenticationFilter::class.java
    ).addFilterBefore(
        TraceIdentifierFilter(),
        AuthenticationFilter::class.java
    ).addFilterBefore(
        UnityFilter(),
        UsernamePasswordAuthenticationFilter::class.java
    ).build()

    @Bean
    fun sessionCleanupFilterRegistration(): FilterRegistrationBean<SessionContextLifecycleFilter> = FilterRegistrationBean(
        SessionContextLifecycleFilter()
    ).apply {
        addUrlPatterns("/*")
        order = HIGHEST_PRECEDENCE
    }

}
