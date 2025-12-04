package cz.svitaninymburk.projects.rezervace.plugins.routing

import cz.svitaninymburk.projects.rezervace.auth.AuthService
import cz.svitaninymburk.projects.rezervace.reservation.ReservationService
import cz.svitaninymburk.projects.rezervace.routing.authRoutes
import cz.svitaninymburk.projects.rezervace.routing.reservationRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Application.configureRouting() {
    val authService by inject<AuthService>()
    val reservationService by inject<ReservationService>()

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
        }

        authenticate("auth-jwt", optional = true) {
            reservationRoutes(reservationService)
        }
    }
}
