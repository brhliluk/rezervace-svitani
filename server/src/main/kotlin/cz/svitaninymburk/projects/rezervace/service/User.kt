package cz.svitaninymburk.projects.rezervace.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import cz.svitaninymburk.projects.rezervace.error.UserError
import cz.svitaninymburk.projects.rezervace.repository.user.UserRepository
import cz.svitaninymburk.projects.rezervace.user.User


class UserService(val userRepository: UserRepository) {
    suspend fun raiseToAdmin(userId: String): Either<UserError.RaiseToAdmin, User> = either {
        val user = ensureNotNull(userRepository.findById(userId)) { UserError.UserNotFound(userId) }
        ensure(user.role != User.Role.ADMIN) { UserError.AdminAlready }
        userRepository.update(user.id, when (user) {
            is User.Email -> user.copy(role = User.Role.ADMIN)
            is User.Google -> user.copy(role = User.Role.ADMIN)
        })
    }

    suspend fun downgradeToUser(userId: String): Either<UserError.DowngradeToUser, User> = either {
        val user = ensureNotNull(userRepository.findById(userId)) { UserError.UserNotFound(userId) }
        userRepository.update(user.id, when (user) {
            is User.Email -> user.copy(role = User.Role.USER)
            is User.Google -> user.copy(role = User.Role.USER)
        })
    }

    suspend fun changeName(userId: String, name: String): Either<UserError.ChangeName, User> = either {
        val user = ensureNotNull(userRepository.findById(userId)) { UserError.UserNotFound(userId) }
        userRepository.update(user.id, when (user) {
            is User.Email -> user.copy(name = name)
            is User.Google -> user.copy(name = name)
        })
    }

    suspend fun changeSurname(userId: String, surname: String): Either<UserError.ChangeName, User> = either {
        val user = ensureNotNull(userRepository.findById(userId)) { UserError.UserNotFound(userId) }
        userRepository.update(user.id, when (user) {
            is User.Email -> user.copy(surname = surname)
            is User.Google -> user.copy(surname = surname)
        })
    }

    suspend fun changeEmail(userId: String, email: String): Either<UserError.ChangeEmail, User> = either {
        val user = ensureNotNull(userRepository.findById(userId)) { UserError.UserNotFound(userId) }
        userRepository.update(user.id, when (user) {
            is User.Email -> user.copy(email = email)
            is User.Google -> user.copy(email = email)
        })
    }
}
