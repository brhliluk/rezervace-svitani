package cz.svitaninymburk.projects.rezervace.routing

import cz.svitaninymburk.projects.rezervace.error.UserError
import cz.svitaninymburk.projects.rezervace.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.put


fun Route.userRoutes(userService: UserService) {
    put("/users/{id}/name/{name}") {
        val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "chybějící ID")
        val name = call.parameters["name"] ?: return@put call.respond(HttpStatusCode.BadRequest, "chybějící jméno")
        val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString()
        if (id != userId) return@put call.respond(HttpStatusCode.BadRequest, "Bez oprávnění upravovat cizí uživatele ID")

        userService.changeName(id, name)
            .onLeft { error -> when (error)  {
                is UserError.UserNotFound -> call.respond(HttpStatusCode.BadRequest, "Uživatel nenalezen")
            } }
            .onRight { call.respond(HttpStatusCode.OK, "změněno") }
    }

    put("/users/{id}/surname/{surname}") {
        val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "chybějící ID")
        val surname = call.parameters["surname"] ?: return@put call.respond(HttpStatusCode.BadRequest, "chybějící příjmení")
        val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString()
        if (id != userId) return@put call.respond(HttpStatusCode.BadRequest, "Bez oprávnění upravovat cizí uživatele ID")

        userService.changeSurname(id, surname)
            .onLeft { error -> when (error)  {
                is UserError.UserNotFound -> call.respond(HttpStatusCode.BadRequest, "Uživatel nenalezen")
            } }
            .onRight { call.respond(HttpStatusCode.OK, "změněno") }
    }

    put("/users/{id}/email/{email}") {
        val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "chybějící ID")
        val email = call.parameters["email"] ?: return@put call.respond(HttpStatusCode.BadRequest, "chybějící email")
        val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString()
        if (id != userId) return@put call.respond(HttpStatusCode.BadRequest, "Bez oprávnění upravovat cizí uživatele ID")

        userService.changeEmail(id, email)
            .onLeft { error -> when (error)  {
                is UserError.UserNotFound -> call.respond(HttpStatusCode.BadRequest, "Uživatel nenalezen")
            } }
            .onRight { call.respond(HttpStatusCode.OK, "změněno") }
    }
}