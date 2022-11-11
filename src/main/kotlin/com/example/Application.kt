package com.example

import com.example.plugins.configureHTTP
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 3000, host = "0.0.0.0", watchPaths = listOf("classes")) {
        configureSecurity()
        configureHTTP()
        configureRouting()
    }.start(wait = true)
}
