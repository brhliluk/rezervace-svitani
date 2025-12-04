package cz.svitaninymburk.projects.rezervace.repository.event

import cz.svitaninymburk.projects.rezervace.event.EventDefinition
import cz.svitaninymburk.projects.rezervace.event.EventInstance
import kotlinx.datetime.LocalDateTime

interface EventRepository {
    suspend fun findById(id: String): EventDefinition?
    suspend fun findAll(): List<EventDefinition>
    suspend fun create(event: EventDefinition): EventDefinition
}

interface EventInstanceRepository {
    suspend fun findById(id: String): EventInstance?

    suspend fun save(instance: EventInstance): EventInstance

    suspend fun findByDateRange(from: LocalDateTime, to: LocalDateTime): List<EventInstance>

    suspend fun incrementOccupiedSpots(instanceId: String, amount: Int): Int?
    suspend fun decrementOccupiedSpots(instanceId: String, amount: Int): Int?

    suspend fun attemptToReserveSpots(instanceId: String, amount: Int, limit: Int): Boolean
}