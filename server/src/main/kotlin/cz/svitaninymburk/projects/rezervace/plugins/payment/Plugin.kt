package cz.svitaninymburk.projects.rezervace.plugins.payment

import cz.svitaninymburk.projects.rezervace.error.localizedMessage
import cz.svitaninymburk.projects.rezervace.repository.reservation.ReservationRepository
import cz.svitaninymburk.projects.rezervace.service.PaymentPairingService
import io.ktor.server.application.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


fun Application.startPaymentCheck() {
    val reservationRepository: ReservationRepository by inject()
    val paymentPairingService: PaymentPairingService by inject()

    launch(Dispatchers.IO) {
        delay(10.seconds)

        while (isActive) {
            val isWaitingForPayment = reservationRepository.hasPendingReservations()

            if (isWaitingForPayment) {
                paymentPairingService.checkAndPairPayments()
                    .onLeft { println("❌ Chyba při kontrole plateb: ${it.localizedMessage}") }
                delay(3.minutes)
            } else {
                paymentPairingService.checkAndPairPayments()
                    .onLeft { println("❌ Chyba při kontrole plateb: ${it.localizedMessage}") }
                delay(1.hours)
            }
        }
    }
}