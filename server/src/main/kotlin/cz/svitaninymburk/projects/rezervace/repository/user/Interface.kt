package cz.svitaninymburk.projects.rezervace.repository.user

import cz.svitaninymburk.projects.rezervace.user.User

interface UserRepository {
    suspend fun findByEmail(email: String): User?
    suspend fun findById(id: String): User?
    suspend fun create(user: User): User
    suspend fun linkGoogleAccount(userId: String, googleSub: String): User.Google
}
