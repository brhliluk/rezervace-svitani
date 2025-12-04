package cz.svitaninymburk.projects.rezervace

import cz.svitaninymburk.projects.rezervace.user.User
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val appSerializersModule = SerializersModule {
    polymorphic(User::class) {
        subclass(User.Google::class)
        subclass(User.Email::class)
    }
}