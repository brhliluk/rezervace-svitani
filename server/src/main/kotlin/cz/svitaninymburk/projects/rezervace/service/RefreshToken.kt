package cz.svitaninymburk.projects.rezervace.service

import cz.svitaninymburk.projects.rezervace.auth.JwtTokenService
import cz.svitaninymburk.projects.rezervace.auth.RefreshToken
import cz.svitaninymburk.projects.rezervace.repository.auth.RefreshTokenRepository
import cz.svitaninymburk.projects.rezervace.user.User
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days


class RefreshTokenService(
    val refreshTokenRepository: RefreshTokenRepository,
    private val tokenService: JwtTokenService,
) {
    suspend fun getToken(userId: String): String {
        val refreshTokenString = tokenService.generateRefreshToken()

        refreshTokenRepository.save(
            RefreshToken(
                token = refreshTokenString,
                userId = userId,
                expiresAt = Clock.System.now() + 30.days
            )
        )
        return refreshTokenString
    }
}