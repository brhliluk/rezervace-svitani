package cz.svitaninymburk.projects.rezervace.routing

import cz.svitaninymburk.projects.rezervace.service.AuthService
import cz.svitaninymburk.projects.rezervace.auth.GoogleLoginRequest
import cz.svitaninymburk.projects.rezervace.auth.LoginRequest
import cz.svitaninymburk.projects.rezervace.auth.RefreshTokenRequest
import cz.svitaninymburk.projects.rezervace.auth.RegisterRequest
import cz.svitaninymburk.projects.rezervace.error.AuthError
import cz.svitaninymburk.projects.rezervace.error.localizedMessage
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.authRoutes(authService: AuthService) {

    post("/google") {
        val request = call.receive<GoogleLoginRequest>()

        authService.loginWithGoogle(request.idToken)
            .onLeft { error -> when (error) {
                is AuthError.InvalidGoogleToken -> call.respond(HttpStatusCode.Unauthorized, error.localizedMessage)
            } }
            .onRight { response -> call.respond(HttpStatusCode.OK, response) }
    }

    post("/register") {
        val request = call.receive<RegisterRequest>()

        authService.register(request)
            .onLeft { error -> when (error) {
                is AuthError.UserAlreadyExists -> call.respond(HttpStatusCode.Forbidden, error.localizedMessage)
            } }
            .onRight { response -> call.respond(HttpStatusCode.OK, response) }
    }

    post("/login") {
        val request = call.receive<LoginRequest>()

        authService.login(request)
            .onLeft { error -> when (error) {
                is AuthError.InvalidCredentials -> call.respond(HttpStatusCode.Unauthorized, error.localizedMessage)
                is AuthError.LoggedInWithAnotherProvider -> call.respond(HttpStatusCode.BadRequest, error.localizedMessage)
            } }
            .onRight { response -> call.respond(HttpStatusCode.OK, response) }
    }

    post("/auth/refresh") {
        val request = call.receive<RefreshTokenRequest>()

        authService.refreshToken(request.refreshToken)
            .onLeft { call.respond(HttpStatusCode.Unauthorized) }
            .onRight { newAccessToken -> call.respond(HttpStatusCode.OK, mapOf("accessToken" to newAccessToken)) }
    }
}
