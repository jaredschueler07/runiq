package com.runiq.core.extensions

import java.text.DecimalFormat
import java.util.Locale

/**
 * Check if string is a valid email
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Capitalize first letter of each word
 */
fun String.toTitleCase(): String {
    return split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
    }
}

/**
 * Remove all whitespace and special characters
 */
fun String.alphanumericOnly(): String {
    return replace(Regex("[^A-Za-z0-9]"), "")
}

/**
 * Truncate string to specified length with ellipsis
 */
fun String.truncate(maxLength: Int, ellipsis: String = "..."): String {
    return if (length <= maxLength) {
        this
    } else {
        take(maxLength - ellipsis.length) + ellipsis
    }
}

/**
 * Format distance in meters to human-readable string
 */
fun Float.formatDistance(): String {
    return when {
        this >= 1000 -> {
            val km = this / 1000
            DecimalFormat("#.##").format(km) + " km"
        }
        else -> {
            DecimalFormat("#").format(this) + " m"
        }
    }
}

/**
 * Format speed in m/s to km/h
 */
fun Float.formatSpeed(): String {
    val kmh = this * 3.6f
    return DecimalFormat("#.#").format(kmh) + " km/h"
}

/**
 * Format pace in min/km
 */
fun Float.formatPace(): String {
    if (this <= 0) return "--:--"
    
    val totalSeconds = (this * 60).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

/**
 * Format calories with proper units
 */
fun Int.formatCalories(): String {
    return "$this cal"
}

/**
 * Format heart rate with BPM
 */
fun Int.formatHeartRate(): String {
    return "$this BPM"
}

/**
 * Format weight in kg
 */
fun Float.formatWeight(): String {
    return DecimalFormat("#.#").format(this) + " kg"
}

/**
 * Format height in cm
 */
fun Int.formatHeight(): String {
    return "$this cm"
}

/**
 * Convert string to initials (e.g., "John Doe" -> "JD")
 */
fun String.toInitials(): String {
    return split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")
}

/**
 * Mask sensitive information (e.g., email, phone)
 */
fun String.maskSensitive(): String {
    return when {
        isValidEmail() -> {
            val parts = split("@")
            if (parts.size == 2) {
                val username = parts[0]
                val domain = parts[1]
                val maskedUsername = if (username.length > 2) {
                    username.take(2) + "*".repeat(username.length - 2)
                } else {
                    "*".repeat(username.length)
                }
                "$maskedUsername@$domain"
            } else {
                this
            }
        }
        length > 4 -> {
            take(2) + "*".repeat(length - 4) + takeLast(2)
        }
        else -> "*".repeat(length)
    }
}

/**
 * Convert camelCase to snake_case
 */
fun String.toSnakeCase(): String {
    return replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
}

/**
 * Convert snake_case to camelCase
 */
fun String.toCamelCase(): String {
    return split("_").mapIndexed { index, word ->
        if (index == 0) word.lowercase()
        else word.lowercase().replaceFirstChar { it.uppercaseChar() }
    }.joinToString("")
}

/**
 * Check if string contains only digits
 */
fun String.isNumeric(): Boolean {
    return matches(Regex("\\d+"))
}

/**
 * Safe conversion to Int with default value
 */
fun String.toIntOrDefault(default: Int = 0): Int {
    return toIntOrNull() ?: default
}

/**
 * Safe conversion to Float with default value
 */
fun String.toFloatOrDefault(default: Float = 0f): Float {
    return toFloatOrNull() ?: default
}

/**
 * Safe conversion to Long with default value
 */
fun String.toLongOrDefault(default: Long = 0L): Long {
    return toLongOrNull() ?: default
}

/**
 * Remove extra whitespace and trim
 */
fun String.normalizeWhitespace(): String {
    return trim().replace(Regex("\\s+"), " ")
}