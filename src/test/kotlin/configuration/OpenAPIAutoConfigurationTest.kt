package configuration

import com.thomas.core.util.StringUtils.randomString
import com.thomas.spring.boot.configuration.OpenAPIAutoConfiguration
import com.thomas.spring.boot.properties.OpenAPIContactProperties
import com.thomas.spring.boot.properties.OpenAPIGroupProperties
import com.thomas.spring.boot.properties.OpenAPIInfoProperties
import com.thomas.spring.boot.properties.OpenAPISecurityProperties
import io.swagger.v3.oas.models.security.SecurityScheme
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class OpenAPIAutoConfigurationTest {

    private val configuration = OpenAPIAutoConfiguration()
    private lateinit var infoProperties: OpenAPIInfoProperties
    private lateinit var contactProperties: OpenAPIContactProperties
    private lateinit var securityProperties: OpenAPISecurityProperties
    private lateinit var groupProperties: OpenAPIGroupProperties

    @BeforeEach
    fun setUp() {
        infoProperties = OpenAPIInfoProperties(
            title = randomString(),
            version = randomString(),
            description = randomString(),
        )
        contactProperties = OpenAPIContactProperties(
            name = randomString(),
            email = randomString(),
            url = randomString(),
        )
        securityProperties = OpenAPISecurityProperties(
            name = randomString(),
            type = SecurityScheme.Type.entries.random(),
            scheme = randomString(),
            format = randomString(),
            description = randomString(),
        )
        groupProperties = OpenAPIGroupProperties(
            secured = randomString(),
            open = randomString(),
        )
    }

    @Test
    fun `API configuration should reflect the specified properties`() {
        val specification = configuration.openAPI(infoProperties, contactProperties, securityProperties)
        assertEquals(infoProperties.title, specification.info.title)
        assertEquals(infoProperties.version, specification.info.version)
        assertEquals(infoProperties.description, specification.info.description)
        assertEquals(contactProperties.name, specification.info.contact.name)
        assertEquals(contactProperties.email, specification.info.contact.email)
        assertEquals(contactProperties.url, specification.info.contact.url)
        assertTrue(specification.components.securitySchemes.isNotEmpty())
        val scheme = specification.components.securitySchemes[securityProperties.name]!!
        assertEquals(securityProperties.name, scheme.name)
        assertEquals(securityProperties.type, scheme.type)
        assertEquals(securityProperties.scheme, scheme.scheme)
        assertEquals(securityProperties.format, scheme.bearerFormat)
        assertEquals(securityProperties.description, scheme.description)
    }

    @Test
    fun `Private APIs should reflect the specified properties and have security configuration`() {
        val api = configuration.openAPI(infoProperties, contactProperties, securityProperties)
        val specification = configuration.privateApiGroup(securityProperties, groupProperties)
        assertEquals(groupProperties.secured, specification.group)
        assertDoesNotThrow { specification.openApiCustomizers.first().customise(api) }
    }

    @Test
    fun `Public APIs should reflect the specified properties`() {
        val specification = configuration.publicApiGroup(groupProperties)
        assertEquals(groupProperties.open, specification.group)
        assertTrue(specification.openApiCustomizers.isEmpty())
    }

}
