package cz.svitaninymburk.projects.rezervace.auth

import kotlin.time.Instant


data class RefreshToken(
    val token: String,
    val userId: String,
    val expiresAt: Instant,
)
