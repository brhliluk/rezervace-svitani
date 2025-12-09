package cz.svitaninymburk.projects.rezervace.repository.auth

import cz.svitaninymburk.projects.rezervace.auth.RefreshToken

interface RefreshTokenRepository {
    suspend fun save(token: RefreshToken)
    suspend fun findByToken(token: String): RefreshToken?
    suspend fun deleteByToken(token: String)
    suspend fun deleteByUserId(userId: String)
}
