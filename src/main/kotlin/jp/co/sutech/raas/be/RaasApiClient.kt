package jp.co.sutech.raas.be

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class CreateExternalSessionResponse(val application: String,val url: String,val newUrl: String)
@Serializable
data class CreateExternalSessionRequest(val backUrl: String,val subUrl: String,val tenant: String, val sub: String)


val SYSTEM_TOKEN = "please ask sutech"
val baseUrl = "https://sutech.dev.functions.asaservice.inc"

suspend inline fun <reified R,reified B> proxy(body : B , m : HttpMethod , url : String , token : String ) : R {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }
    val response: HttpResponse = client.request("${baseUrl}${url}"){
        method = m
        headers {
            append(HttpHeaders.Authorization, "Bearer $token")
        }
        contentType(ContentType.Application.Json)
        setBody(body)
    }
    val ret: R = response.body<R>()
    client.close()
    return ret

}

suspend fun createExternalSession( msa:String,backUrl:String, subUrl:String, tenant:String , sub:String):CreateExternalSessionResponse {
    return proxy<CreateExternalSessionResponse,CreateExternalSessionRequest>(CreateExternalSessionRequest(backUrl , subUrl , tenant, sub) , HttpMethod.Post , "/${msa}/external/session" , SYSTEM_TOKEN)
}
