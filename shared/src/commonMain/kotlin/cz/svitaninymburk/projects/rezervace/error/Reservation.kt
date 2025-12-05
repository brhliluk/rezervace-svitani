package cz.svitaninymburk.projects.rezervace.error


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
