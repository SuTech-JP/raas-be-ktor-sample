package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import jp.co.sutech.raas.createExternalSession
import jp.co.sutech.raas.RaasConnectionConfig
import jp.co.sutech.raas.RaasUserContext
import jp.co.sutech.raas.CreateExternalSessionHttpRequest
import jp.co.sutech.raas.get
import kotlinx.serialization.Serializable

@Serializable
data class DataImportLogQue(val id: String, val tenant: String, val sub: String)

@Serializable
data class DataImportLog(val id: String, val status: String)

@Serializable
data class DataImportLogDetail(val dataId: String, val pdfUrl: String, val csvFiles: List<DataImportLogDetailCsvFile> , val entity : InvoiceSample)

//スキーマに合わせた業務データを定義
@Serializable
data class InvoiceSample(val title: String?, val message: String?, )

@Serializable
data class DataImportLogDetailCsvFile(val name: String, val url: String)

@Serializable
data class DataImportLogForFe(val id: String, val status: String, val details: List<DataImportLogDetail>)

@Serializable
data class RaasLayout(val id: Int, val name: String, val description: String)

fun Application.configureRouting(cfg: RaasConnectionConfig) {
    val config = cfg
    routing {
        get("/raas/report/result/{targetId}") {
            val targetDataImportLogId = call.parameters["targetId"]
            if (targetDataImportLogId != null) {
                val que = DataImportLogQue(id = targetDataImportLogId, tenant = "test", sub = "test")   //DBから復元
                val userCtx = RaasUserContext(tenant = que.tenant, sub = que.sub)
                var dataImportLog =
                    get<DataImportLog>(config = config, user = userCtx, "/datatraveler/import/logs/${que.id}")
                if (dataImportLog.status == "FINISH") {
                    var dataImportLogDetails = get<List<DataImportLogDetail>>(
                        config = config,
                        user = userCtx,
                        "/datatraveler/import/logs/${que.id}/data"
                    )
                    println(dataImportLogDetails)

                    //PDFとJSONの取得を行い業務的に必要な処理（PDFをコピーしてJSONを元にDBにレコードを作る）を行う
                    call.respond(
                        DataImportLogForFe(
                            id = dataImportLog.id,
                            status = dataImportLog.status,
                            details = dataImportLogDetails
                        )
                    )
                } else {
                    call.respond(dataImportLog)
                }
            }
        }
        get("/raas/report/layout/{application}/{schema}") {
            val application = call.parameters["application"]
            val schema = call.parameters["schema"]
            val userCtx = RaasUserContext(tenant = "test", sub = "test")
            var layouts =
                get<List<RaasLayout>>(config = config, user = userCtx, "/report/layouts/${application}/${schema}")
            call.respond(layouts)
        }
        post("/raas/{msa}/session") {
            val msa = call.parameters["msa"]
            if (msa === null) throw Exception("path is strange")
            val body = call.receive<CreateExternalSessionHttpRequest>()
            var session = createExternalSession(
                config,
                RaasUserContext(tenant = "test", sub = "test"),
                msa,
                body.backUrl,
                body.subUrl
            )
            call.respond(session)
        }
    }
}
