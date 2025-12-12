package cz.svitaninymburk.projects.rezervace

import arrow.core.right
import cz.svitaninymburk.projects.rezervace.error.ReservationError
import cz.svitaninymburk.projects.rezervace.event.EventInstance
import cz.svitaninymburk.projects.rezervace.repository.event.EventInstanceRepository
import cz.svitaninymburk.projects.rezervace.repository.reservation.ReservationRepository
import cz.svitaninymburk.projects.rezervace.reservation.CreateReservationRequest
import cz.svitaninymburk.projects.rezervace.reservation.PaymentInfo
import cz.svitaninymburk.projects.rezervace.service.EmailService
import cz.svitaninymburk.projects.rezervace.service.PaymentTrigger
import cz.svitaninymburk.projects.rezervace.service.QrCodeService
import cz.svitaninymburk.projects.rezervace.service.ReservationService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


class ReservationServiceTest {
    private val eventRepository = mockk<EventInstanceRepository>()
    private val reservationRepository = mockk<ReservationRepository>()
    private val emailService = mockk<EmailService>()
    private val qrCodeService = mockk<QrCodeService>()
    private val paymentTrigger = mockk<PaymentTrigger>()


    private val service = ReservationService(eventRepository, reservationRepository, emailService, qrCodeService, paymentTrigger)

    @Test
    fun `createReservation should fail when capacity is exceeded`() = runTest {
        val fullEvent = getEvent(capacity = 30, occupiedSpots = 30)

        coEvery { eventRepository.attemptToReserveSpots("1", 1) } returns false

        val result = service.createReservation(
            request = getReservationRequest(),
            userId = null,
        )

        assertTrue(result.isLeft())
        assertEquals(ReservationError.CapacityExceeded, result.leftOrNull())
    }

    @Test
    fun `createReservation should fail when event is already finished`() = runTest {
        val pastEvent = getEvent(startDateTime = LocalDateTime(2000, 1, 1, 1, 0), endDateTime = LocalDateTime(2000, 1, 1, 1, 0))

        val result = service.createReservation(
            request = getReservationRequest(),
            userId = null,
        )

        assertTrue(result.isLeft())
        assertEquals(ReservationError.EventAlreadyFinished, result.leftOrNull())
    }

    @Test
    fun `createReservation should fail when event already started`() = runTest {
        val longEvent = getEvent(startDateTime = LocalDateTime(2000, 1, 1, 1, 0), endDateTime = LocalDateTime(2040, 1, 1, 1, 0))

        val result = service.createReservation(
            request = getReservationRequest(),
            userId = null,
        )

        assertTrue(result.isLeft())
        assertEquals(ReservationError.EventAlreadyStarted, result.leftOrNull())
    }

    @Test
    fun `createReservation should fail when event is cancelled`() = runTest {
        val cancelledEvent = getEvent(cancelled = true)

        val result = service.createReservation(
            request = getReservationRequest(),
            userId = null,
        )

        assertTrue(result.isLeft())
        assertEquals(ReservationError.EventCancelled, result.leftOrNull())
    }

    @Test
    fun `createReservation should fail when event is not found`() = runTest {
        coEvery { eventRepository.get("1") } returns null
        val result = service.createReservation(
            request = getReservationRequest(),
            userId = null,
        )

        assertTrue(result.isLeft())
        assertEquals(ReservationError.EventNotFound, result.leftOrNull())
    }

    @Test
    fun `createReservation should succeed`() = runTest {
        val event = getEvent()

        coEvery { eventRepository.get("1") } returns event
        coEvery { eventRepository.attemptToReserveSpots("1", 1) } returns true
        coEvery { eventRepository.incrementOccupiedSpots("1", 1) } returns 1
        coEvery { reservationRepository.save(any()) } returnsArgument 0
        coEvery { qrCodeService.accountNumber } returns "123456789"
        coEvery { qrCodeService.generateQrPaymentImage(any()) } returns ByteArray(0)
        coEvery { emailService.sendReservationConfirmation(any(), any(), any(), any()) } returns Unit.right()
        coEvery { paymentTrigger.notifyNewReservation() } returns Unit

        val result = service.createReservation(
            request = getReservationRequest(),
            userId = null,
        )

        assertTrue(result.isRight())
        assertEquals(event.id, result.getOrNull()?.eventInstanceId)
    }

    private fun getReservationRequest() = CreateReservationRequest(
        eventInstanceId = "1",
        seatCount = 1,
        contactName = "Petr Pavel",
        contactEmail = "william.henry.moody@my-own-personal-domain.com",
        contactPhone = "123456789",
        paymentType = PaymentInfo.Type.BANK_TRANSFER,
        customValues = emptyList()
    )

    private fun getEvent(
        capacity: Int = 30,
        occupiedSpots: Int = 0,
        startDateTime: LocalDateTime = LocalDateTime(2040, 1, 1, 10, 0),
        endDateTime: LocalDateTime = LocalDateTime(2040, 1, 1, 20, 0),
        cancelled: Boolean = false,
    ): EventInstance {
        val instance = EventInstance(
            id = "1",
            definitionId = "1",
            title = "Test Event",
            description = "Test Description",
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            price = 100.0,
            capacity = capacity,
            occupiedSpots = occupiedSpots,
            isCancelled = cancelled,
        )
        coEvery { eventRepository.get("1") } returns instance
        return instance
    }
}