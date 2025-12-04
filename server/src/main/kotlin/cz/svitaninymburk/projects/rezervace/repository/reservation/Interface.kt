package cz.svitaninymburk.projects.rezervace.repository.reservation

import cz.svitaninymburk.projects.rezervace.reservation.Reservation

interface ReservationRepository {
    suspend fun save(reservation: Reservation): Reservation
    suspend fun findById(id: String): Reservation?
    suspend fun countSeats(eventId: String): Int
}