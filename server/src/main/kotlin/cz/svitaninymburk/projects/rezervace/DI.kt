package cz.svitaninymburk.projects.rezervace

import cz.svitaninymburk.projects.rezervace.service.AuthService
import cz.svitaninymburk.projects.rezervace.auth.BCryptHashingService
import cz.svitaninymburk.projects.rezervace.auth.GoogleAuthService
import cz.svitaninymburk.projects.rezervace.auth.HashingService
import cz.svitaninymburk.projects.rezervace.auth.JwtTokenService
import cz.svitaninymburk.projects.rezervace.repository.auth.InMemoryRefreshTokenRepository
import cz.svitaninymburk.projects.rezervace.repository.auth.RefreshTokenRepository
import cz.svitaninymburk.projects.rezervace.repository.event.EventInstanceRepository
import cz.svitaninymburk.projects.rezervace.repository.event.EventDefinitionRepository
import cz.svitaninymburk.projects.rezervace.repository.event.InMemoryEventInstanceRepository
import cz.svitaninymburk.projects.rezervace.repository.event.InMemoryEventDefinitionRepository
import cz.svitaninymburk.projects.rezervace.repository.reservation.InMemoryReservationRepository
import cz.svitaninymburk.projects.rezervace.repository.reservation.ReservationRepository
import cz.svitaninymburk.projects.rezervace.repository.user.InMemoryUserRepository
import cz.svitaninymburk.projects.rezervace.repository.user.UserRepository
import cz.svitaninymburk.projects.rezervace.service.EventService
import cz.svitaninymburk.projects.rezervace.service.GmailEmailService
import cz.svitaninymburk.projects.rezervace.service.QrCodeService
import cz.svitaninymburk.projects.rezervace.service.RefreshTokenService
import cz.svitaninymburk.projects.rezervace.service.ReservationService
import org.koin.dsl.module

val appModule = module {
    // 1. Konfigurace (V reálu načítat z ENV)
    single {
        JwtTokenService.Companion.JwtConfig(
            secret = "tajne-heslo-pro-vyvoj-123456", // Musí být dostatečně dlouhé pro HMAC256
            issuer = "http://0.0.0.0:8080/",
            audience = "moje-rezervace-app",
            realm = "Access to Reservation System"
        )
    }

    single { GoogleAuthService(clientId = "vas-google-client-id") }

    single { JwtTokenService(get()) }
    single<HashingService> { BCryptHashingService() }

    single<UserRepository> { InMemoryUserRepository() }
    single<EventDefinitionRepository> { InMemoryEventDefinitionRepository() }
    single<EventInstanceRepository> { InMemoryEventInstanceRepository() }
    single<ReservationRepository> { InMemoryReservationRepository() }
    single<RefreshTokenRepository> { InMemoryRefreshTokenRepository() }

    single { AuthService(get(), get(), get(),  get(), get(), get()) }
    single { RefreshTokenService(get(), get()) }
    single { EventService(get(), get()) }
    single { GmailEmailService(get(), get(), get()) }
    single { QrCodeService(accountNumber = System.getenv("BANK_ACCOUNT_NUMBER") ?: "123456-123456789/0100") }
    single { ReservationService(get(), get(), get(), get()) }
}
