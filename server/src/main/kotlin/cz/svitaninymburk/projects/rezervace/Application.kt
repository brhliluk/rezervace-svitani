package cz.svitaninymburk.projects.rezervace

import cz.svitaninymburk.projects.rezervace.plugins.routing.configureRouting
import cz.svitaninymburk.projects.rezervace.plugins.security.configureSecurity
import cz.svitaninymburk.projects.rezervace.plugins.serialization.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.sse.SSE
import org.koin.ktor.plugin.Koin


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Koin) {
        modules(appModule)
    }

    install(SSE)

    configureSerialization()
    configureSecurity()

    configureRouting()
}