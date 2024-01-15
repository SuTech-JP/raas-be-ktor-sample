package com.example

import com.example.plugins.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.CORS
import jp.co.sutech.raas.RaasConnectionConfig

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

fun Application.module() {
    var config = RaasConnectionConfig(
        application = environment.config.propertyOrNull("raas.application")?.getString() ?: "",
        landscape = environment.config.propertyOrNull("raas.landscape")?.getString() ?: "",
        token = environment.config.propertyOrNull("raas.token")?.getString() ?: ""
    )

    install(CORS){
        allowHost("*")
        allowHeader(HttpHeaders.ContentType)
    }

    configureSerialization()
    configureRouting(config)
}
