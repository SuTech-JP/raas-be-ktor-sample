package com.example

import com.example.plugins.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import jp.co.sutech.raas.RaasConnectionConfig

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    var config = RaasConnectionConfig(
        application = environment.config.propertyOrNull("raas.application")?.getString() ?: "",
        landscape = environment.config.propertyOrNull("raas.landscape")?.getString() ?: "",
        token = environment.config.propertyOrNull("raas.token")?.getString() ?: ""
    )
    
    println("Loaded configuration:")
    println("Application: ${config.application}")
    println("Landscape: ${config.landscape}")
    println("Token length: ${config.token.length}")

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
    }

    configureSerialization()
    configureRouting(config)
}
