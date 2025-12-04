package cz.svitaninymburk.projects.rezervace.event

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.ExperimentalTime

@Serializable
data class EventDefinition @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val title: String,
    val description: String,
    val defaultPrice: Double,
    val defaultCapacity: Int,
    val defaultDuration: Duration,
    val recurrenceType: RecurrenceType = RecurrenceType.NONE,
    val recurrenceEndDate: Instant? = null,
)

@Serializable
data class EventInstance(
    val id: String,
    val definitionId: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val price: Double,
    val capacity: Int,
    val occupiedSpots: Int = 0,
    val isCancelled: Boolean = false,
) {
    val isFull: Boolean
        get() = occupiedSpots >= capacity
}

@Serializable
enum class RecurrenceType {
    NONE, DAILY, WEEKLY, MONTHLY
}