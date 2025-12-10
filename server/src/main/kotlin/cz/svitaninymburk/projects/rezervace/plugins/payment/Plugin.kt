package cz.svitaninymburk.projects.rezervace.plugins.payment

import cz.svitaninymburk.projects.rezervace.repository.reservation.ReservationRepository
import cz.svitaninymburk.projects.rezervace.service.PaymentPairingService
import cz.svitaninymburk.projects.rezervace.service.PaymentTrigger
import io.ktor.server.application.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource


fun Application.startPaymentCheck() {
    val reservationRepository: ReservationRepository by inject()
    val paymentPairingService: PaymentPairingService by inject()
    val paymentTrigger: PaymentTrigger by inject()

    launch(Dispatchers.IO) {
        delay(5.seconds)

        val minInterval = 40.seconds
        var lastCheckTime = TimeSource.Monotonic.markNow()

        while (isActive) {
            val now = TimeSource.Monotonic.markNow()
            if (now - lastCheckTime > minInterval) {
                paymentPairingService.checkAndPairPayments()
                    .onLeft { e -> println(e) }
                    .onRight { lastCheckTime = TimeSource.Monotonic.markNow() }
            }

            val hasPending = reservationRepository.hasPendingReservations()

            val sleepDuration =
                if (hasPending) 5.minutes
                else 1.hours

            println("💤 Jdu spát na $sleepDuration (nebo dokud nezazvoní trigger)")

            withTimeoutOrNull(sleepDuration) {
                paymentTrigger.waitForSignal()
            }
        }
    }
}