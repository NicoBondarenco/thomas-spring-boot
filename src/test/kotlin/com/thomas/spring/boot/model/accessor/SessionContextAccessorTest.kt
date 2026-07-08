package com.thomas.spring.boot.model.accessor

import com.thomas.core.context.SessionContext
import com.thomas.core.context.SessionContextHolder
import com.thomas.core.context.SessionContextHolder.clearContext
import com.thomas.core.generator.SecurityUserGenerator.generateSecurityUser
import com.thomas.core.util.LocaleUtils.randomLocale
import com.thomas.core.util.SessionContextUtils.randomSessionProperties
import com.thomas.core.util.StringUtils.randomString
import io.micrometer.context.ThreadLocalAccessor
import kotlin.reflect.KParameter.Kind
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SessionContextAccessorTest {

    private lateinit var sessionContextAccessor: SessionContextAccessor

    @BeforeEach
    fun setUp() {
        clearContext()
        sessionContextAccessor = SessionContextAccessor()
    }

    @Test
    fun `SessionContextAccessor key should return SessionContext class name`() {
        val function = accessorFunction("key")
        assertEquals(SessionContext::class.java.simpleName, function.call(sessionContextAccessor))
    }

    @Test
    fun `SessionContextAccessor should return current context`() {
        val context = SessionContext.create(
            user = generateSecurityUser(),
            token = randomString(),
            locale = randomLocale(),
            properties = randomSessionProperties()
        )

        val setFunction = accessorFunction("setValue", 1)
        val params = setFunction.parameters
        val valueParam = params.first { it.kind == Kind.VALUE }
        val instanceParam = params.first { it.kind == Kind.INSTANCE }

        setFunction.callBy(
            mapOf(
                instanceParam to sessionContextAccessor,
                valueParam to context
            )
        )

        val getFunction = accessorFunction("getValue")
        assertEquals(context, getFunction.call(sessionContextAccessor))
    }

    @Test
    fun `SessionContextAccessor restore should set current context`() {
        val context = SessionContext.create(
            user = generateSecurityUser(),
            token = randomString(),
            locale = randomLocale(),
            properties = randomSessionProperties()
        )

        sessionContextAccessor.restore(context)

        val getFunction = accessorFunction("getValue")
        assertEquals(context, getFunction.call(sessionContextAccessor))
    }

    @Test
    fun `SessionContextAccessor setValue without arguments should clear context`() {
        val context = SessionContext.create(
            user = generateSecurityUser(),
            token = randomString(),
        )
        SessionContextHolder.context = context
        sessionContextAccessor.setValue()
        assertNull(SessionContextHolder.context.currentUser)
        assertNull(SessionContextHolder.context.currentToken)
    }

    @Test
    fun `SessionContextAccessor restore without arguments should clear context`() {
        val context = SessionContext.create(
            user = generateSecurityUser(),
            token = randomString(),
        )
        SessionContextHolder.context = context
        sessionContextAccessor.restore()
        assertNull(SessionContextHolder.context.currentUser)
        assertNull(SessionContextHolder.context.currentToken)
    }

    private fun accessorFunction(name: String, params: Int = 0) = ThreadLocalAccessor::class.memberFunctions.first { function ->
        function.name == name && function.parameters.filterNot {
            it.kind == Kind.INSTANCE
        }.size == params
    }.apply {
        isAccessible = true
    }

}
