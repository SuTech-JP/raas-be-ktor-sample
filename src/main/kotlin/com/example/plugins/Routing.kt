package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import jp.co.sutech.raas.be.*

fun Application.configureRouting() {
    routing {
        route("/raas") {
            raasRouting()
        }
    }
}
