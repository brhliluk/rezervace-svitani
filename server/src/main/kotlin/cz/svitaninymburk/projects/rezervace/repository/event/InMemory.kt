package cz.svitaninymburk.projects.rezervace.repository.event

import cz.svitaninymburk.projects.rezervace.event.EventDefinition
import cz.svitaninymburk.projects.rezervace.event.EventInstance
import kotlinx.datetime.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.ExperimentalTime

class InMemoryEventRepository : EventRepository {
    private val events = ConcurrentHashMap<String, EventDefinition>()

    override suspend fun findById(id: String): EventDefinition? = events[id]

    override suspend fun findAll(): List<EventDefinition> = events.values.toList()

    @OptIn(ExperimentalTime::class)
    override suspend fun create(event: EventDefinition): EventDefinition {
        val id = UUID.randomUUID().toString()
        val newEvent = event.copy(id = id)
        events[id] = newEvent
        return newEvent
    }
}

class InMemoryEventInstanceRepository : EventInstanceRepository {

    private val instances = ConcurrentHashMap<String, EventInstance>()

    override suspend fun findById(id: String): EventInstance? {
        return instances[id]
    }

    override suspend fun findByIds(eventIds: List<String>): List<EventInstance> {
        return instances.filterKeys { it in eventIds }.values.toList()
    }

    override suspend fun save(instance: EventInstance): EventInstance {
        val id = instance.id.ifBlank { UUID.randomUUID().toString() }
        val newInstance = instance.copy(id = id)
        instances[id] = newInstance
        return newInstance
    }

    override suspend fun findByDateRange(from: LocalDateTime, to: LocalDateTime): List<EventInstance> {
        return instances.values.filter { instance -> instance.startDateTime in from..to }.toList()
    }

    override suspend fun incrementOccupiedSpots(instanceId: String, amount: Int): Int? {
        return instances.computeIfPresent(instanceId) { _, currentInstance ->
            currentInstance.copy(occupiedSpots = currentInstance.occupiedSpots + amount)
        }?.occupiedSpots
    }

    override suspend fun decrementOccupiedSpots(instanceId: String, amount: Int): Int? {
        return instances.computeIfPresent(instanceId) { _, currentInstance ->
            currentInstance.copy(occupiedSpots = currentInstance.occupiedSpots - amount)
        }?.occupiedSpots
    }

    override suspend fun attemptToReserveSpots(instanceId: String, amount: Int, limit: Int): Boolean {
        var reservationSuccess = false

        instances.computeIfPresent(instanceId) { _, currentInstance ->
            if (currentInstance.occupiedSpots + amount <= limit) {
                reservationSuccess = true
                currentInstance.copy(occupiedSpots = currentInstance.occupiedSpots + amount)
            } else {
                reservationSuccess = false
                currentInstance
            }
        }

        return reservationSuccess
    }
}