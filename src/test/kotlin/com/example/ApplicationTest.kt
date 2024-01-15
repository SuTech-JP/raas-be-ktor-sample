package com.example

import com.example.plugins.*
import io.ktor.server.testing.*
import jp.co.sutech.raas.RaasConnectionConfig
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            var config = RaasConnectionConfig(
                application = environment.config.propertyOrNull("raas.application")?.getString() ?: "",
                landscape = environment.config.propertyOrNull("raas.landscape")?.getString() ?: "",
                token = environment.config.propertyOrNull("raas.token")?.getString() ?: ""
            )
            configureRouting(config)
        }
    }
}
