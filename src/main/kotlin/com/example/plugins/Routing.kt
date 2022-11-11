package com.example.plugins

import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import java.io.File

private val folder = File("files").apply { mkdir() }

fun Application.configureRouting() {
    routing {
        post("/files") {
            val list = folder.list() ?: return@post
            val sb = StringBuilder()
            list.forEachIndexed { index, name ->
                if (index + 1 == list.size) {
                    sb.append(name)
                } else {
                    sb.append("$name ")
                }
            }

            val s = sb.toString()
            this@configureRouting.log.info("send $s")
            call.respondText(s)
        }

        post("/isExists/{name}") {
            val name = call.parameters["name"] ?: return@post
            val b = folder.list()?.contains(name) ?: false
            call.respondText(b.toString())
        }

        post("/upload") {
            val multipart = call.receiveMultipart()
            multipart.forEachPart {
                if (it is PartData.FileItem) {
                    val file = File(folder, it.originalFileName?.replace(" ", "_") ?: return@forEachPart)

                    if (file.exists()) {
                        call.respondText("file exists")
                        return@forEachPart
                    }

                    file.outputStream().use { output ->
                        it.streamProvider().copyTo(output)
                    }

                    call.respondText("done")
                }
            }
        }

        post("/delete/{name}") {
            val name = call.parameters["name"] ?: return@post
            val file = folder.listFiles()?.firstOrNull { it.name == name }

            if (file != null) {
                file.delete()
                call.respond("done")
            } else {
                call.respond("file not found")
            }
        }

        static("/download") {
            staticRootFolder = folder
            files(".")
        }
    }
}
