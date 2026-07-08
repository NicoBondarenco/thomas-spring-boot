package com.thomas.spring.boot.configuration

import com.thomas.spring.boot.model.accessor.SessionContextAccessor
import io.micrometer.context.ContextRegistry
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass

@AutoConfiguration
@ConditionalOnClass(ContextRegistry::class)
class SessionContextAutoConfiguration {

    @PostConstruct
    fun registerContextPropagation() {
        ContextRegistry.getInstance().registerThreadLocalAccessor(SessionContextAccessor())
    }

}
