package cz.svitaninymburk.projects.rezervace.service

import arrow.core.Either
import arrow.core.raise.context.ensureNotNull
import arrow.core.raise.either
import arrow.core.raise.ensure
import cz.svitaninymburk.projects.rezervace.error.ReservationError
import cz.svitaninymburk.projects.rezervace.repository.event.EventInstanceRepository
import cz.svitaninymburk.projects.rezervace.repository.reservation.ReservationRepository
import cz.svitaninymburk.projects.rezervace.reservation.CreateReservationRequest
import cz.svitaninymburk.projects.rezervace.reservation.Reservation
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock


class ReservationService(
    private val eventRepo: EventInstanceRepository,
    private val reservationRepo: ReservationRepository,
) {
    
    suspend fun createReservation(
        request: CreateReservationRequest,
        userId: String?
    ): Either<ReservationError.CreateReservation, Reservation> = either {

        val instance = ensureNotNull(eventRepo.findById(request.eventInstanceId)) { ReservationError.EventNotFound }

        ensure(!instance.isCancelled) { ReservationError.EventCancelled }
        ensure(instance.startDateTime > Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())) { ReservationError.EventAlreadyFinished }

        val isReserved = eventRepo.attemptToReserveSpots(
            instanceId = instance.id,
            amount = request.seatCount,
            limit = instance.capacity
        )

        ensure(isReserved) { ReservationError.CapacityExceeded }

        val reservation = reservationRepo.save(
            Reservation(
                eventInstanceId = request.eventInstanceId,
                registeredUserId = userId,
                seatCount = request.seatCount,
                contactName = request.contactName,
                contactEmail = request.contactEmail,
                contactPhone = request.contactPhone,
                paymentType = request.paymentType,
                totalPrice = instance.price * request.seatCount, // Cena z instance!
                status = Reservation.Status.PENDING_PAYMENT,
                createdAt = Clock.System.now(),
                id = ""
            )
        )

        eventRepo.incrementOccupiedSpots(instance.id, request.seatCount)

        reservation
    }

    
    suspend fun cancelReservation(
        reservationId: String,
        userId: String?,
    ): Either<ReservationError.CancelReservation, Boolean> = either {
        val reservation = ensureNotNull(reservationRepo.findById(reservationId)) { ReservationError.EventNotFound }

        ensure(Clock.System.now() < reservation.createdAt) { ReservationError.EventAlreadyFinished }

        val cancelledReservation = reservation.copy(status = Reservation.Status.CANCELLED)
        reservationRepo.save(cancelledReservation)

        eventRepo.decrementOccupiedSpots(reservation.eventInstanceId, reservation.seatCount)

        // TODO: notify user

        true
    }

    
    suspend fun getReservations(userId: String): Either<ReservationError.GetAll, List<Reservation>> = either {
        val reservations = reservationRepo.getAll(userId)
        if (reservations.isEmpty()) return@either emptyList()

        val eventIds = reservations.map { it.eventInstanceId }.distinct()

        val events = eventRepo.findByIds(eventIds).associateBy { it.id }

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        reservations.filter { reservation ->
            val event = events[reservation.eventInstanceId]
            event != null && event.endDateTime > now && !event.isCancelled
        }
    }
}