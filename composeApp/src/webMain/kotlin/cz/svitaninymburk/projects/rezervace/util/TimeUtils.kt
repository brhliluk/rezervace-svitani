package cz.svitaninymburk.projects.rezervace.util

import kotlinx.datetime.LocalDateTime

val LocalDateTime.humanReadable: String get() = buildString {
    append(day)
    append('.')
    append(month.ordinal)
    append('.')
    append(year)
    append(' ')
    append(hour)
    append(':')
    append(minute)
}