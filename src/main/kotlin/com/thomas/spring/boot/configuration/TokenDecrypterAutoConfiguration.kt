package com.thomas.spring.boot.configuration

import com.thomas.spring.boot.token.TokenDecrypter
import com.thomas.spring.boot.token.TokenDecrypterProperties
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@AutoConfiguration
@EnableConfigurationProperties(TokenDecrypterProperties::class)
class TokenDecrypterAutoConfiguration {

    @Bean
    fun tokenDecrypter(properties: TokenDecrypterProperties): TokenDecrypter {
        return TokenDecrypter(properties)
    }

}
