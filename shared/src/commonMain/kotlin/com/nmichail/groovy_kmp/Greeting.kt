package com.nmichail.groovy_kmp

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return "Hi azazazazaza, ${platform.name}!"
    }
}