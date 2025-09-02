package com.nmichail.groovy_kmp.feature.core.utils

/**
 * Common utility functions
 */
object CommonUtils {
    
    /**
     * Check if string is not null or empty
     */
    fun String?.isNotNullOrEmpty(): Boolean = !this.isNullOrEmpty()
    
    /**
     * Check if string is null or empty
     */
    fun String?.isNullOrEmpty(): Boolean = this.isNullOrEmpty()
    
    /**
     * Get string or default value if null or empty
     */
    fun String?.orDefault(default: String): String = if (isNotNullOrEmpty()) this!! else default
    
    /**
     * Safe cast with default value
     */
    inline fun <reified T> Any?.safeCast(default: T): T = this as? T ?: default
    
    /**
     * Safe cast with null result
     */
    inline fun <reified T> Any?.safeCastOrNull(): T? = this as? T
}

/**
 * Extension function for safe string operations
 */
fun String?.takeIfNotEmpty(): String? = if (this.isNullOrEmpty()) null else this

/**
 * Extension function for safe string operations with default
 */
fun String?.takeIfNotEmptyOr(default: String): String = if (this.isNullOrEmpty()) default else this
