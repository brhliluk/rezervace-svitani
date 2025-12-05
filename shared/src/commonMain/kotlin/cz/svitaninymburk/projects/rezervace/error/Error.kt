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
    sealed interface CreateReservation : ReservationError
    sealed interface CancelReservation : ReservationError
    sealed interface GetAll : ReservationError

    data object EventNotFound : CreateReservation, CancelReservation
    data object EventAlreadyFinished : CreateReservation, CancelReservation
    data object EventCancelled : CreateReservation
    data object CapacityExceeded : CreateReservation
    data object FailedToGetAllReservations : GetAll
}

val ReservationError.localizedMessage: String get() = when (this) {
    ReservationError.EventNotFound -> "Událost nebyla nalezena"
    ReservationError.CapacityExceeded -> "Kapacita události překročena"
    ReservationError.EventAlreadyFinished -> "Událost již skončila"
    ReservationError.EventCancelled -> "Událost byla zrušena"
    ReservationError.FailedToGetAllReservations -> "Nelze získat seznam rezervací"
}