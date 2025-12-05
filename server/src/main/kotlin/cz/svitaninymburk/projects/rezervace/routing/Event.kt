package cz.svitaninymburk.projects.rezervace.routing

import cz.svitaninymburk.projects.rezervace.error.EventError
import cz.svitaninymburk.projects.rezervace.error.localizedMessage
import cz.svitaninymburk.projects.rezervace.event.CreateEventDefinitionRequest
import cz.svitaninymburk.projects.rezervace.event.CreateEventInstanceRequest
import cz.svitaninymburk.projects.rezervace.event.EventDefinition
import cz.svitaninymburk.projects.rezervace.event.EventInstance
import cz.svitaninymburk.projects.rezervace.service.EventService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post


fun Route.eventRoutes(eventService: EventService) {

    post("/definition") {
        val req = call.receive<CreateEventDefinitionRequest>()

        eventService.createEventDefinition(req)
            .onRight { call.respond(HttpStatusCode.Created, "Úspěšně vytvořeno") }
    }

    post("/definition/{id}") {
        val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "chybějící ID")
        val req = call.receive<EventDefinition>()
        if (id != req.id) return@post call.respond(HttpStatusCode.BadRequest, "Nesouhlasející ID")

        eventService.updateEventDefinition(req)
            .onLeft { error -> when (error) {
                is EventError.EventDefinitionNotFound -> call.respond(HttpStatusCode.NotFound, error.localizedMessage)
            } }
            .onRight { call.respond(HttpStatusCode.OK, "Úspěšně aktualizováno") }
    }

    post("/definition/{id}") {
        val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "chybějící ID")

        eventService.deleteEventDefinition(id)
            .onRight { call.respond(HttpStatusCode.OK, "Úspěšně smazáno") }
    }

    post("/instance") {
        val req = call.receive<CreateEventInstanceRequest>()

        eventService.createEventInstance(req)
            .onLeft { error -> when (error) {
                is EventError.EventDefinitionNotFound -> call.respond(HttpStatusCode.NotFound, error.localizedMessage)
            } }
            .onRight { call.respond(HttpStatusCode.Created, "Úspěšně vytvořeno") }
    }

    post("/instance/{id}") {
        val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "chybějící ID")
        val req = call.receive<EventInstance>()
        if (id != req.id) return@post call.respond(HttpStatusCode.BadRequest, "Nesouhlasející ID")

        eventService.updateEventInstance(req)
            .onLeft { error -> when (error) {
                is EventError.EventInstanceNotFound -> call.respond(HttpStatusCode.NotFound, error.localizedMessage)
            } }
            .onRight { call.respond(HttpStatusCode.OK, "Úspěšně aktualizováno") }
    }

    delete("/instance/{id}") {
        val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "chybějící ID")
        eventService.deleteEventInstance(id)
            .onRight { call.respond(HttpStatusCode.OK, "Úspěšně smazáno") }
    }
}