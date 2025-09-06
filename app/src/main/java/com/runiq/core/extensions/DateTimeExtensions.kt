package com.runiq.core.extensions

import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Convert milliseconds to formatted duration string (e.g., "05:30")
 */
fun Long.toFormattedDuration(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60
    
    return when {
        hours > 0 -> String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        else -> String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}

/**
 * Convert seconds to formatted duration string
 */
fun Int.secondsToFormattedDuration(): String = (this * 1000L).toFormattedDuration()

/**
 * Convert milliseconds to pace format (min:sec per km)
 */
fun Long.toPaceFormat(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

/**
 * Convert pace in minutes per km to formatted string
 */
fun Float.toPaceString(): String {
    val totalSeconds = (this * 60).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

/**
 * Format timestamp to readable date string
 */
fun Long.toDateString(pattern: String = "MMM dd, yyyy"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(this))
}

/**
 * Format timestamp to readable time string
 */
fun Long.toTimeString(pattern: String = "HH:mm"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(this))
}

/**
 * Format timestamp to readable date and time string
 */
fun Long.toDateTimeString(pattern: String = "MMM dd, yyyy HH:mm"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(this))
}

/**
 * Get start of day timestamp
 */
fun Long.startOfDay(): Long {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
        .toLocalDate()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

/**
 * Get end of day timestamp
 */
fun Long.endOfDay(): Long {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
        .toLocalDate()
        .atTime(23, 59, 59, 999_999_999)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

/**
 * Check if timestamp is today
 */
fun Long.isToday(): Boolean {
    val today = LocalDate.now()
    val timestampDate = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this), 
        ZoneId.systemDefault()
    ).toLocalDate()
    return today == timestampDate
}

/**
 * Check if timestamp is this week
 */
fun Long.isThisWeek(): Boolean {
    val now = LocalDateTime.now()
    val timestampDate = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this), 
        ZoneId.systemDefault()
    )
    
    val startOfWeek = now.minusDays(now.dayOfWeek.value.toLong() - 1).toLocalDate().atStartOfDay()
    val endOfWeek = startOfWeek.plusDays(6).toLocalDate().atTime(23, 59, 59)
    
    return timestampDate.isAfter(startOfWeek.minusSeconds(1)) && 
           timestampDate.isBefore(endOfWeek.plusSeconds(1))
}

/**
 * Get relative time string (e.g., "2 hours ago", "Yesterday")
 */
fun Long.toRelativeTimeString(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            "${minutes}m ago"
        }
        diff < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            "${hours}h ago"
        }
        diff < TimeUnit.DAYS.toMillis(7) -> {
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            "${days}d ago"
        }
        else -> toDateString()
    }
}

/**
 * Convert Duration to human-readable string
 */
fun Duration.toHumanReadableString(): String {
    val hours = toHours()
    val minutes = toMinutesPart()
    val seconds = toSecondsPart()
    
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m ${seconds}s"
        else -> "${seconds}s"
    }
}

/**
 * Get current timestamp
 */
fun now(): Long = System.currentTimeMillis()

/**
 * Get timestamp for start of today
 */
fun todayStart(): Long = now().startOfDay()

/**
 * Get timestamp for end of today
 */
fun todayEnd(): Long = now().endOfDay()