package com.thomas.spring.boot.configuration

import com.thomas.spring.boot.filter.AuthenticationFilter
import com.thomas.spring.boot.filter.LinkIdentifierFilter
import com.thomas.spring.boot.filter.SessionContextLifecycleFilter
import com.thomas.spring.boot.filter.UnityFilter
import com.thomas.spring.boot.token.TokenDecrypter
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@AutoConfiguration
class FilterAutoConfiguration {

    @Bean
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    fun securityFilterChain(
        http: HttpSecurity,
        tokenDecrypter: TokenDecrypter
    ): SecurityFilterChain {
        val authenticationFilter = AuthenticationFilter(tokenDecrypter)
        val unityFilter = UnityFilter()
        val linkFilter = LinkIdentifierFilter()

        http.cors {
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
            authenticationFilter,
            UsernamePasswordAuthenticationFilter::class.java
        ).addFilterBefore(
            linkFilter,
            AuthenticationFilter::class.java
        ).addFilterBefore(
            unityFilter,
            UsernamePasswordAuthenticationFilter::class.java
        )

        return http.build()
    }

    @Bean
    fun sessionCleanupFilterRegistration(): FilterRegistrationBean<SessionContextLifecycleFilter> {
        val filter = SessionContextLifecycleFilter()
        val registrationBean = FilterRegistrationBean(filter)
        registrationBean.addUrlPatterns("/*")
        registrationBean.order = HIGHEST_PRECEDENCE
        return registrationBean
    }

}
