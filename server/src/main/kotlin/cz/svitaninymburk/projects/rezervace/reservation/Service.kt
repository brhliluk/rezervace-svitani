package cz.svitaninymburk.projects.rezervace.reservation

import arrow.core.Either
import arrow.core.raise.context.ensure
import arrow.core.raise.context.ensureNotNull
import arrow.core.raise.either
import arrow.core.raise.ensure
import cz.svitaninymburk.projects.rezervace.error.ReservationError
import cz.svitaninymburk.projects.rezervace.repository.event.EventInstanceRepository
import cz.svitaninymburk.projects.rezervace.repository.reservation.ReservationRepository
import io.ktor.client.request.request
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class ReservationService(
    private val eventRepo: EventInstanceRepository,
    private val reservationRepo: ReservationRepository,
) {
    @OptIn(ExperimentalTime::class)
    suspend fun createReservation(
        request: CreateReservationRequest,
        userId: String?
    ): Either<ReservationError, Reservation> = either {

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

    @OptIn(ExperimentalTime::class)
    suspend fun cancelReservation(
        reservationId: String,
        userId: String?,
    ): Either<ReservationError, Boolean> = either {
        val reservation = ensureNotNull(reservationRepo.findById(reservationId)) { ReservationError.EventNotFound }

        ensure(Clock.System.now() < reservation.createdAt) { ReservationError.EventAlreadyFinished }

        val cancelledReservation = reservation.copy(status = Reservation.Status.CANCELLED)
        reservationRepo.save(cancelledReservation)

        eventRepo.decrementOccupiedSpots(reservation.eventInstanceId, reservation.seatCount)

        // TODO: notify user

        true
    }
}