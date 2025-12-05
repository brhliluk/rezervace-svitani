package cz.svitaninymburk.projects.rezervace

import cz.svitaninymburk.projects.rezervace.auth.AuthService
import cz.svitaninymburk.projects.rezervace.auth.BCryptHashingService
import cz.svitaninymburk.projects.rezervace.auth.GoogleAuthService
import cz.svitaninymburk.projects.rezervace.auth.HashingService
import cz.svitaninymburk.projects.rezervace.auth.JwtTokenService
import cz.svitaninymburk.projects.rezervace.repository.event.EventInstanceRepository
import cz.svitaninymburk.projects.rezervace.repository.event.EventDefinitionRepository
import cz.svitaninymburk.projects.rezervace.repository.event.InMemoryEventInstanceRepository
import cz.svitaninymburk.projects.rezervace.repository.event.InMemoryEventDefinitionRepository
import cz.svitaninymburk.projects.rezervace.repository.reservation.InMemoryReservationRepository
import cz.svitaninymburk.projects.rezervace.repository.reservation.ReservationRepository
import cz.svitaninymburk.projects.rezervace.repository.user.InMemoryUserRepository
import cz.svitaninymburk.projects.rezervace.repository.user.UserRepository
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

    single { AuthService(get(), get(), get(), get()) }
    single { ReservationService(get(), get()) }
}
