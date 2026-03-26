package com.wiztek.freader

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform