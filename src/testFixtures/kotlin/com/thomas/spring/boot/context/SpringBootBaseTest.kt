package com.thomas.spring.boot.context

import com.github.tomakehurst.wiremock.junit5.WireMockTest
import com.thomas.spring.boot.context.SpringBootBaseTest.Companion.WIREMOCK_PORT
import com.thomas.spring.boot.context.mock.stubOpentelemetry
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@SpringBootTest(
    classes = [ContextSpringBootApplication::class],
    webEnvironment = RANDOM_PORT,
)
@ActiveProfiles("test")
@WireMockTest(httpPort = WIREMOCK_PORT)
class SpringBootBaseTest {

    companion object {
        private const val WIREMOCK_PORT = 9999

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            val url = "http://localhost:$WIREMOCK_PORT/v1/traces"
            registry.add("management.otlp.tracing.endpoint") { url }
        }
    }

    @BeforeEach
    fun setup() {
        stubOpentelemetry()
    }

}
