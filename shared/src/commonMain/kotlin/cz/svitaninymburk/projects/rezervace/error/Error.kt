package cz.svitaninymburk.projects.rezervace.error

import cz.svitaninymburk.projects.rezervace.user.User
import kotlin.reflect.KClass


sealed interface AppError

sealed interface AuthError : AppError {
    sealed interface LoginWithGoogle : AuthError
    sealed interface Register : AuthError
    sealed interface LoginWithEmail : AuthError

    data object InvalidGoogleToken : LoginWithGoogle
    data class LoggedInWithAnotherProvider(val userClass: KClass<User>) : LoginWithEmail
    data object InvalidCredentials : LoginWithEmail
    data object UserAlreadyExists : Register
}

val AuthError.localizedMessage: String get() = when (this) {
    is AuthError.InvalidCredentials -> "Neplatné přihlašovací údaje"
    is AuthError.LoggedInWithAnotherProvider -> "Přihlášení jinou metodou: ${userClass.simpleName}"
    is AuthError.UserAlreadyExists -> "Účet již existuje"
    is AuthError.InvalidGoogleToken -> "Neplatný Google Token"
}

// TODO: Localized messages, separate by action
sealed interface ReservationError : AppError {
    data object EventNotFound : ReservationError
    data object EventCancelled : ReservationError
    data object CapacityExceeded : ReservationError
    data object EventAlreadyFinished : ReservationError
}