package com.thomas.spring.boot.security

import com.thomas.core.context.SessionContextHolder.currentUnity
import com.thomas.core.extension.randomUUIDv7
import com.thomas.core.generator.SecurityUserGenerator.generateSecurityUser
import com.thomas.core.model.security.SecurityRole
import com.thomas.core.model.security.SecurityUnity
import com.thomas.core.util.StringUtils.randomString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SecurityUserAuthenticationTest {

    @Test
    fun `SecurityUserAuthentication should reflect SecurityUser correctly`() {
        val unityId = randomUUIDv7()
        currentUnity = unityId
        val user = generateSecurityUser(
            organizationRoles = setOf(SecurityRole(randomString()), SecurityRole(randomString())),
            securityUnities = setOf(
                SecurityUnity(
                    unityId = unityId,
                    unityRoles = setOf(SecurityRole(randomString()), SecurityRole(randomString())),
                )
            )
        )
        val token = randomString()
        val authentication = SecurityUserAuthentication(user, token, true)

        assertEquals(user.userId.toString(), authentication.name)
        assertEquals(token, authentication.credentials)
        assertEquals(user, authentication.details)
        assertEquals(user, authentication.principal)
        assertTrue(authentication.isAuthenticated)
        assertEquals(user.currentRoles.size, authentication.authorities.size)
        user.currentRoles.forEach { role ->
            assertTrue(authentication.authorities.any { it.authority == role.role })
        }
        authentication.isAuthenticated = false
        assertFalse(authentication.isAuthenticated)
    }

}
