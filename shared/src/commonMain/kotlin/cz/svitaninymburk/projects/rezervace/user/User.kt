package cz.svitaninymburk.projects.rezervace.user

import kotlinx.serialization.Serializable

@Serializable
sealed class User {
    abstract val id: String
    abstract val email: String
    abstract val fullName: String
    abstract val role: Role

    @Serializable
    data class Google(
        override val id: String,
        override val email: String,
        override val fullName: String,
        override val role: Role,
        val googleSub: String
    ): User()

    @Serializable
    data class Email(
        override val id: String,
        override val email: String,
        override val fullName: String,
        override val role: Role,
        val passwordHash: String
    ): User() {
        fun toGoogle(googleSub: String): Google {
            return Google(id = id, email = email, fullName = fullName, role = role, googleSub = googleSub)
        }
    }

    @Serializable
    enum class Role { USER, ADMIN }
}
