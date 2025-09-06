package com.runiq.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runiq.core.util.Result
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Base ViewModel providing common functionality for all ViewModels in the app.
 * Includes error handling, loading states, and common patterns.
 */
abstract class BaseViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Global exception handler for coroutines launched in this ViewModel
     */
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable, "Unhandled exception in ${this::class.simpleName}")
        handleError(throwable)
    }

    /**
     * Launch a coroutine with proper error handling
     */
    protected fun launchWithErrorHandling(
        block: suspend () -> Unit
    ) {
        viewModelScope.launch(exceptionHandler) {
            block()
        }
    }

    /**
     * Execute a suspend function with loading and error handling
     */
    protected suspend fun <T> executeWithLoading(
        action: suspend () -> Result<T>
    ): Result<T> {
        return try {
            setLoading(true)
            clearError()
            val result = action()
            
            if (result is Result.Error) {
                handleError(result.exception)
            }
            
            result
        } catch (e: Exception) {
            Timber.e(e, "Error executing action in ${this::class.simpleName}")
            handleError(e)
            Result.Error(e)
        } finally {
            setLoading(false)
        }
    }

    /**
     * Handle errors by logging and updating error state
     */
    protected open fun handleError(throwable: Throwable) {
        Timber.e(throwable, "Error in ${this::class.simpleName}")
        _error.value = throwable.message ?: "An unknown error occurred"
    }

    /**
     * Set loading state
     */
    protected fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    /**
     * Clear current error
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Show a custom error message
     */
    protected fun showError(message: String) {
        _error.value = message
    }
}