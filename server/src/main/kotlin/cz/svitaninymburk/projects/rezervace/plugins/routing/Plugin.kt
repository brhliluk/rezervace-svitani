package cz.svitaninymburk.projects.rezervace.plugins.routing

import cz.svitaninymburk.projects.rezervace.auth.AuthService
import cz.svitaninymburk.projects.rezervace.plugins.auth.AdminAuthorization
import cz.svitaninymburk.projects.rezervace.service.ReservationService
import cz.svitaninymburk.projects.rezervace.routing.authRoutes
import cz.svitaninymburk.projects.rezervace.routing.authenticatedReservationRoutes
import cz.svitaninymburk.projects.rezervace.routing.eventRoutes
import cz.svitaninymburk.projects.rezervace.routing.reservationRoutes
import cz.svitaninymburk.projects.rezervace.service.EventService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Application.configureRouting() {
    val authService by inject<AuthService>()
    val reservationService by inject<ReservationService>()
    val eventService by inject<EventService>()


    routing {
        route("/auth") {
            authRoutes(authService)
        }

        authenticate("auth-jwt") {

            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("id")?.asString()
                val email = principal?.payload?.getClaim("email")?.asString()

                call.respond(mapOf("id" to userId, "email" to email))
            }
            authenticatedReservationRoutes(reservationService)

            route("/event") {
                install(AdminAuthorization)
                eventRoutes(eventService)
            }
        }

        authenticate("auth-jwt", optional = true) {
            reservationRoutes(reservationService)
        }
    }
}
