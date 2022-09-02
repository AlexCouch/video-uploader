@file:OptIn(KtorExperimentalLocationsAPI::class)

package org.example.application

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.locations.*
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import org.litote.kmongo.json
import java.io.File

fun HTML.index() {
    head {
        title("Hello from Ktor!")
    }
    body {
        div {
            id = "root"
        }
        script(src = "/static/video-uploader.js") {}
    }
}

fun HTML.error(code: String){
    head{
        title("Uh-oh!")
    }
    body{
        div{
            id = "root"
        }
        p{
            +"Error: $code"
        }
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        install(ContentNegotiation){
            json()
        }
        install(CORS){
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Delete)
            anyHost()
        }
        routing {
            post(Routes.UPLOAD){
                val multipart = call.receiveMultipart()
                multipart.forEachPart { part ->
                    if(part is PartData.FileItem){
                        val name = part.originalFileName!!
                        val file = File("uploads/$name")
                        println(file.extension)
                        if(file.extension !in listOf("mp4", "MP4")){
                            call.respondRedirect{
                                path(Routes.ERROR)
                                parametersOf("code", listOf(HttpStatusCode.UnsupportedMediaType.description))
                            }
                            part.dispose()
                            return@forEachPart
                        }

                        if(!file.exists()){
                            file.parentFile.mkdirs()
                            if(!file.createNewFile()){
                                call.respond(HttpStatusCode.InternalServerError)
                                part.dispose()
                                return@forEachPart
                            }
                        }

                        part.streamProvider().use{ its ->
                            file.outputStream().buffered().use{
                                its.copyTo(it)
                            }
                        }
                        call.respond(HttpStatusCode.OK)
                    }
                    part.dispose()
                }
            }
            get(Routes.ERROR){
                val parameters = call.receiveParameters()
                call.respondHtml(HttpStatusCode.OK){
                    parameters.forEach { key, values ->
                        when(key){
                            "code" -> {
                                val code = values[0]
                                error(code)
                            }
                        }
                    }
                }
            }
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }
            static("/static") {
                resources()
            }
        }
    }.start(wait = true)
}