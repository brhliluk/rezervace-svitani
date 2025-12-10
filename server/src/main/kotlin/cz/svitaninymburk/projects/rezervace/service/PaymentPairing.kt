package cz.svitaninymburk.projects.rezervace.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import cz.svitaninymburk.projects.rezervace.bank.BankTransaction
import cz.svitaninymburk.projects.rezervace.bank.FioResponse
import cz.svitaninymburk.projects.rezervace.bank.parseFioTransactions
import cz.svitaninymburk.projects.rezervace.error.PaymentPairingError
import cz.svitaninymburk.projects.rezervace.repository.reservation.ReservationRepository
import cz.svitaninymburk.projects.rezervace.reservation.Reservation
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.server.util.url


class PaymentPairingService(
    private val httpClient: HttpClient,
    private val reservationRepo: ReservationRepository,
    private val emailService: EmailService,
    private val qrCodeService: QrCodeService,
    private val fioToken: String
) {
    suspend fun checkAndPairPayments(): Either<PaymentPairingError.CheckAndPairPayments, Unit> = either {
            println("🔄 Spouštím kontrolu plateb Fio banky...")

            val response = try {
                httpClient.get(url {
                    protocol = URLProtocol.HTTPS
                    host = "fio.cz"
                    path("ib_api/rest/last/$fioToken/transactions.json")
                })
            } catch (e: Exception) {
                raise(PaymentPairingError.Upstream(e, e.message ?: "Unknown error"))
            }

            ensure(response.status.isSuccess()) { PaymentPairingError.Failed(response.status.toString())  }

            val transactions = parseFioTransactions(response.body<FioResponse>())

            println("📥 Staženo ${transactions.size} nových transakcí.")

            transactions.forEach { processTransaction(it) }
        }

    private suspend fun processTransaction(transaction: BankTransaction) {
        val vs = transaction.variableSymbol
        if (vs.isNullOrBlank()) {
            println("⚠️ Transakce ${transaction.remoteId} nemá VS, nelze spárovat.")
            return
        }

        val reservation = reservationRepo.findAwaitingPayment(vs) ?: run {
            println("❓ Platba s VS $vs nenašla žádnou čekající rezervaci.")
            return
        }

        if ((transaction.amount < reservation.totalPrice) || (transaction.amount != reservation.unpaidAmount)) {
            println("⚠️ Nedoplatek! VS $vs: Očekávaná čáska: ${reservation.unpaidAmount}, přišlo ${transaction.amount}.")
            emailService.sendPaymentNotPaidInFull(reservation, transaction, fioToken, qrCodeService.generateQrPaymentImage(reservation.copy(totalPrice = reservation.unpaidAmount - transaction.amount)))
            return
        }

        val paidReservation = reservation.copy(
            status = Reservation.Status.CONFIRMED,
            paidAmount = transaction.amount,
            paymentPairingToken = transaction.remoteId,
        )
        reservationRepo.save(paidReservation)

        emailService.sendPaymentReceivedConfirmation(paidReservation)

        println("✅ Rezervace ${reservation.id} (VS $vs) úspěšně ZAPLACENA.")
    }
}