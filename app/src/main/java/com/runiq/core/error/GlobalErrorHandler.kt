package com.runiq.core.error

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Global error handler for managing errors across the entire application
 */
@Singleton
class GlobalErrorHandler @Inject constructor(
    private val context: Context
) {
    
    private val _errorEvents = MutableSharedFlow<ErrorEvent>()
    val errorEvents: SharedFlow<ErrorEvent> = _errorEvents.asSharedFlow()
    
    /**
     * Handle an error and emit appropriate error event
     */
    fun handleError(
        throwable: Throwable,
        context: String? = null,
        shouldShowToUser: Boolean = true
    ) {
        // Log the error
        Timber.e(throwable, "Global error in context: $context")
        
        // Map to RunIQ exception
        val runiqException = ErrorMapper.mapToRunIQException(throwable)
        
        // Create error event
        val errorEvent = ErrorEvent(
            exception = runiqException,
            userMessage = ErrorMapper.mapToUserMessage(runiqException),
            context = context,
            shouldShowToUser = shouldShowToUser,
            isRetryable = ErrorMapper.isRetryable(runiqException),
            timestamp = System.currentTimeMillis()
        )
        
        // Emit error event
        try {
            _errorEvents.tryEmit(errorEvent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to emit error event")
        }
        
        // Report to crash analytics if critical
        if (isCriticalError(runiqException)) {
            reportToCrashlytics(runiqException, context)
        }
    }
    
    /**
     * Handle error and show snackbar
     */
    fun handleErrorWithSnackbar(
        throwable: Throwable,
        snackbarHostState: SnackbarHostState,
        scope: CoroutineScope,
        context: String? = null,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null
    ) {
        handleError(throwable, context, shouldShowToUser = false)
        
        val message = ErrorMapper.mapToUserMessage(throwable)
        scope.launch {
            try {
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    withDismissAction = true
                )
                
                if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                    onAction?.invoke()
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to show snackbar")
            }
        }
    }
    
    /**
     * Check if error should be reported to crash analytics
     */
    private fun isCriticalError(exception: RunIQException): Boolean {
        return when (exception) {
            is DatabaseException.MigrationException -> true
            is HealthConnectException.DataWriteException -> true
            is LocationException.TrackingFailed -> true
            is AIException.ServiceUnavailable -> false // Not critical
            is NetworkException -> false // Expected network issues
            is AuthException -> false // User-level auth issues
            is ValidationException -> false // User input validation
            else -> true // Unknown exceptions are critical
        }
    }
    
    /**
     * Report error to Firebase Crashlytics
     */
    private fun reportToCrashlytics(
        exception: RunIQException,
        context: String?
    ) {
        try {
            // TODO: Implement Firebase Crashlytics reporting
            // FirebaseCrashlytics.getInstance().apply {
            //     setCustomKey("error_context", context ?: "unknown")
            //     setCustomKey("error_type", exception::class.simpleName ?: "unknown")
            //     recordException(exception)
            // }
            Timber.i("Would report to Crashlytics: ${exception::class.simpleName} in $context")
        } catch (e: Exception) {
            Timber.e(e, "Failed to report to Crashlytics")
        }
    }
}

/**
 * Error event data class
 */
data class ErrorEvent(
    val exception: RunIQException,
    val userMessage: String,
    val context: String?,
    val shouldShowToUser: Boolean,
    val isRetryable: Boolean,
    val timestamp: Long
)

/**
 * Composition local for global error handler
 */
val LocalErrorHandler = staticCompositionLocalOf<GlobalErrorHandler> {
    error("GlobalErrorHandler not provided")
}