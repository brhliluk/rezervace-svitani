package cz.svitaninymburk.projects.rezervace.routing

import cz.svitaninymburk.projects.rezervace.plugins.auth.AdminAuthorization
import cz.svitaninymburk.projects.rezervace.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post


fun Route.adminRoutes(userService: UserService) {
    install(AdminAuthorization)

    post("users/{id}/admin") {
        val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "chybějící ID")

        userService.raiseToAdmin(id)
        call.respond(HttpStatusCode.OK, "Uživatel povýšen na administrátora")
    }
}