package cz.svitaninymburk.projects.rezervace.repository.reservation

import cz.svitaninymburk.projects.rezervace.reservation.Reservation
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.ExperimentalTime


class InMemoryReservationRepository : ReservationRepository {

    private val reservations = ConcurrentHashMap<String, Reservation>()

    @OptIn(ExperimentalTime::class)
    override suspend fun save(reservation: Reservation): Reservation {
        val id = reservation.id.ifBlank { UUID.randomUUID().toString() }
        val newRes = reservation.copy(id = id)
        reservations[id] = newRes
        return newRes
    }

    override suspend fun findById(id: String): Reservation? = reservations[id]

    override suspend fun countSeats(eventId: String): Int {
        return reservations.values
            .filter { it.eventInstanceId == eventId }
            // Počítáme jen aktivní rezervace (ne zrušené)
            .filter { it.status != Reservation.Status.CANCELLED && it.status != Reservation.Status.REJECTED }
            .sumOf { it.seatCount }
    }

    override suspend fun getAll(userId: String): List<Reservation> {
        return reservations.values.filter { it.userId == userId }
    }
}