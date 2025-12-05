package cz.svitaninymburk.projects.rezervace.routing

import cz.svitaninymburk.projects.rezervace.error.ReservationError
import cz.svitaninymburk.projects.rezervace.error.localizedMessage
import cz.svitaninymburk.projects.rezervace.reservation.CreateReservationRequest
import cz.svitaninymburk.projects.rezervace.reservation.ReservationService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post

// TODO: require user
fun Route.reservationRoutes(reservationService: ReservationService) {
    //TODO: get(/reservations)

    post("/reservations") {
        val req = call.receive<CreateReservationRequest>()

        reservationService.createReservation(req, call.principal<JWTPrincipal>()?.payload?.id)
            .onLeft { error ->
                val status = when(error) {
                    ReservationError.EventNotFound -> HttpStatusCode.NotFound
                    ReservationError.CapacityExceeded -> HttpStatusCode.Conflict
                    ReservationError.EventAlreadyFinished -> HttpStatusCode.BadRequest
                    ReservationError.EventCancelled -> HttpStatusCode.NotAcceptable
                }
                call.respond(status, error.localizedMessage)
            }
            .onRight { reservation -> call.respond(HttpStatusCode.Created, reservation) }
    }

    delete("/reservations/{id}") {
        val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "chybějící ID")

        reservationService.cancelReservation(id, call.principal<JWTPrincipal>()?.payload?.id)
            .onLeft { error -> when (error) {
                ReservationError.EventAlreadyFinished -> call.respond(HttpStatusCode.BadRequest, error.localizedMessage)
                ReservationError.EventNotFound -> call.respond(HttpStatusCode.NotFound, error.localizedMessage)
            } }
            .onRight { call.respond(HttpStatusCode.OK, "Rezervace zrušena") }
    }
}