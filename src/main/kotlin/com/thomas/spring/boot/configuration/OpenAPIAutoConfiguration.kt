package com.thomas.spring.boot.configuration

import com.thomas.spring.boot.properties.OpenAPIContactProperties
import com.thomas.spring.boot.properties.OpenAPIGroupProperties
import com.thomas.spring.boot.properties.OpenAPIInfoProperties
import com.thomas.spring.boot.properties.OpenAPISecurityProperties
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@AutoConfiguration
@EnableConfigurationProperties(
    OpenAPIInfoProperties::class,
    OpenAPIContactProperties::class,
    OpenAPISecurityProperties::class,
    OpenAPIGroupProperties::class
)
class OpenAPIAutoConfiguration {

    @Bean
    fun openAPI(
        infoProperties: OpenAPIInfoProperties,
        contactProperties: OpenAPIContactProperties,
        securityProperties: OpenAPISecurityProperties,
    ): OpenAPI = OpenAPI().also { api ->
        api.info = Info().also { info ->
            info.title = infoProperties.title
            info.version = infoProperties.version
            info.description = infoProperties.description
            info.contact = Contact().also { contact ->
                contact.name = contactProperties.name
                contact.email = contactProperties.email
                contact.url = contactProperties.url
            }
        }
        api.components(
            Components()
                .addSecuritySchemes(
                    securityProperties.name,
                    SecurityScheme()
                        .name(securityProperties.name)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme(securityProperties.scheme)
                        .bearerFormat(securityProperties.format)
                        .description(securityProperties.description)
                )
        )
    }

    @Bean
    fun privateApiGroup(
        securityProperties: OpenAPISecurityProperties,
        groupProperties: OpenAPIGroupProperties,
    ): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group(groupProperties.secured)
            .pathsToMatch("/{version:v[0-9]{1,2}}/**")
            .pathsToExclude("/public/**")
            .addOpenApiCustomizer { openApi ->
                openApi.addSecurityItem(SecurityRequirement().addList(securityProperties.name))
            }
            .build()
    }


    @Bean
    fun publicApiGroup(
        groupProperties: OpenAPIGroupProperties,
    ): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group(groupProperties.open)
            .pathsToMatch("/public/{version:v[0-9]{1,2}}/**")
            .build()
    }

}
