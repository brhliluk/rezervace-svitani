package cz.svitaninymburk.projects.rezervace.routing

import cz.svitaninymburk.projects.rezervace.error.ReservationError
import cz.svitaninymburk.projects.rezervace.error.localizedMessage
import cz.svitaninymburk.projects.rezervace.reservation.CreateReservationRequest
import cz.svitaninymburk.projects.rezervace.reservation.GetReservationsRequest
import cz.svitaninymburk.projects.rezervace.service.ReservationService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post


fun Route.reservationRoutes(reservationService: ReservationService) {
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

        reservationService.cancelReservation(id)
            .onLeft { error -> when (error) {
                is ReservationError.EventAlreadyFinished -> call.respond(HttpStatusCode.BadRequest, error.localizedMessage)
                is ReservationError.EventNotFound -> call.respond(HttpStatusCode.NotFound, error.localizedMessage)
                // TODO: error/success reason
                is ReservationError.FailedToSendCancellationEmail -> call.respond(HttpStatusCode.OK, "Rezervace zrušena, ale odeslání emailu se nezdařilo")
            } }
            .onRight { call.respond(HttpStatusCode.OK, "Rezervace zrušena") }
    }
}

fun Route.authenticatedReservationRoutes(reservationService: ReservationService) {
    get("/reservations") {
        val userId = call.principal<JWTPrincipal>()?.payload?.id
        if (userId == null) {
            call.respond(HttpStatusCode.Unauthorized, "Nepřihlášený uživatel")
            return@get
        }
        val req = call.receive<GetReservationsRequest>()

        reservationService.getReservations(req.userId)
            .onLeft { error -> when (error) {
                ReservationError.FailedToGetAllReservations -> call.respond(HttpStatusCode.InternalServerError, error.localizedMessage)
            } }
            .onRight { reservations -> call.respond(HttpStatusCode.OK, reservations) }
    }
}
