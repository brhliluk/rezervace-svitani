package cz.svitaninymburk.projects.rezervace.auth

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import cz.svitaninymburk.projects.rezervace.error.AuthError
import cz.svitaninymburk.projects.rezervace.repository.user.UserRepository
import cz.svitaninymburk.projects.rezervace.user.User
import kotlin.reflect.KClass
import kotlin.uuid.Uuid


class AuthService(
    private val userRepo: UserRepository,
    private val googleAuth: GoogleAuthService,
    private val tokenService: JwtTokenService,
    private val hashingService: HashingService,
) {

    suspend fun loginWithGoogle(token: String): Either<AuthError.LoginWithGoogle, AuthResponse> = either {

        val googleUser = ensureNotNull(googleAuth.verifyToken(token)) {
            AuthError.InvalidGoogleToken
        }

        var user = userRepo.findByEmail(googleUser.email)

        if (user == null) {
            user = userRepo.create(
                User.Google(
                    id = Uuid.random().toString(),
                    email = googleUser.email,
                    name = googleUser.name,
                    googleSub = googleUser.googleSub,
                    role = User.Role.USER,
                )
            )
        } else {
            if (user is User.Email) {
                user = userRepo.linkGoogleAccount(user.id, googleUser.googleSub)
            }
        }

        val accessToken = tokenService.generateToken(user)

        AuthResponse(accessToken, user.toDto())
    }

    suspend fun register(request: RegisterRequest): Either<AuthError.Register, AuthResponse> = either {

        val existingUser = userRepo.findByEmail(request.email)
        ensure(existingUser == null) { AuthError.UserAlreadyExists }

        val hash = hashingService.generateSaltedHash(request.password)

        val newUser = userRepo.create(
            User.Email(
                id = Uuid.random().toString(),
                email = request.email,
                name = request.fullName,
                role = User.Role.USER,
                passwordHash = hash,
            )
        )

        val token = tokenService.generateToken(newUser)

        AuthResponse(token, newUser.toDto())
    }

    suspend fun login(request: LoginRequest): Either<AuthError.LoginWithEmail, AuthResponse> = either {

        val user = userRepo.findByEmail(request.email)

        ensureNotNull(user) { AuthError.InvalidCredentials }

        ensure(user is User.Email) { AuthError.LoggedInWithAnotherProvider(User.Google::class.java.kotlin as KClass<User>) }

        val isValid = hashingService.verify(request.password, user.passwordHash)

        ensure(isValid) { AuthError.InvalidCredentials }

        val token = tokenService.generateToken(user)

        AuthResponse(token, user.toDto())
    }
}