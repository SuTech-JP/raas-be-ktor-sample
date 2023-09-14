package jp.co.sutech.raas.be

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class CreateExternalSessionHttpRequest(val backUrl: String,val subUrl: String)

fun Route.raasRouting() {
    post("/session") {
        val body = call.receive<CreateExternalSessionHttpRequest>()
        call.respond(createExternalSession("report" , body.backUrl , body.subUrl , "test" , "test"))
    }
}
