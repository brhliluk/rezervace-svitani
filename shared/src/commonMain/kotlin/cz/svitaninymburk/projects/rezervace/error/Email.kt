package cz.svitaninymburk.projects.rezervace.error


sealed interface EmailError : AppError {
    sealed interface SendConfirmation: EmailError
    sealed interface SendCancellation: EmailError

    data class SendConfirmationFailed(val message: String) : SendConfirmation
    data class SendCancellationFailed(val message: String) : SendCancellation
}

val EmailError.localizedMessage: String get() = when (this) {
    is EmailError.SendConfirmationFailed -> message
    is EmailError.SendCancellationFailed -> message
}