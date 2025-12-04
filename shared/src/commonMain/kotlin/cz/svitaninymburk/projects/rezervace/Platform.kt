package cz.svitaninymburk.projects.rezervace

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform