package com.thomas.spring.boot.configuration

import com.thomas.core.authorization.UnauthorizedUserException
import com.thomas.spring.boot.extension.toExceptionResponse
import com.thomas.spring.boot.filter.AuthenticationFilter
import com.thomas.spring.boot.filter.LinkIdentifierFilter
import com.thomas.spring.boot.filter.SessionContextLifecycleFilter
import com.thomas.spring.boot.filter.UnityFilter
import com.thomas.spring.boot.token.TokenDecrypter
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import tools.jackson.databind.json.JsonMapper

@AutoConfiguration
@EnableWebSecurity
@EnableMethodSecurity
class FilterAutoConfiguration {

    @Bean
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    fun restAuthenticationEntryPoint(
        jsonMapper: JsonMapper
    ): AuthenticationEntryPoint = AuthenticationEntryPoint { request, response, _ ->
        response.contentType = APPLICATION_JSON_VALUE
        response.status = UNAUTHORIZED.value()
        response.writer.write(jsonMapper.writeValueAsString(UnauthorizedUserException().toExceptionResponse(request.requestURI)))
    }

    @Bean
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    fun securityFilterChain(
        http: HttpSecurity,
        tokenDecrypter: TokenDecrypter,
        authenticationEntryPoint: AuthenticationEntryPoint,
    ): SecurityFilterChain = http.cors {
        it.disable()
    }.sessionManagement {
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
    }.authorizeHttpRequests {
        it.requestMatchers("/public/**").permitAll()
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers("/v3/api-docs/**").permitAll()
            .requestMatchers("/configuration/**").permitAll()
            .requestMatchers("/swagger-resources/**").permitAll()
            .requestMatchers("/swagger-resources").permitAll()
            .requestMatchers("/swagger-ui/**").permitAll()
            .requestMatchers("/swagger-ui.html").permitAll()
            .requestMatchers("/webjars/**").permitAll()
            .anyRequest().authenticated()
    }.addFilterBefore(
        AuthenticationFilter(tokenDecrypter),
        UsernamePasswordAuthenticationFilter::class.java
    ).addFilterBefore(
        LinkIdentifierFilter(),
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
