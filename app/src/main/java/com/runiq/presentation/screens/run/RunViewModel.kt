package com.runiq.presentation.screens.run

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runiq.domain.model.RunSession
import com.runiq.domain.model.WorkoutType
import com.runiq.domain.repository.RunRepository
import com.runiq.domain.usecase.StartRunUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Run screen.
 * Manages run session state and user interactions.
 */
@HiltViewModel
class RunViewModel @Inject constructor(
    private val startRunUseCase: StartRunUseCase,
    private val runRepository: RunRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RunUiState())
    val uiState: StateFlow<RunUiState> = _uiState.asStateFlow()
    
    private val _activeSession = MutableStateFlow<RunSession?>(null)
    val activeSession: StateFlow<RunSession?> = _activeSession.asStateFlow()
    
    init {
        observeActiveRun()
    }
    
    fun startRun(userId: String, workoutType: WorkoutType, coachId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            startRunUseCase(userId, workoutType, coachId)
                .onSuccess { session ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRunActive = true
                    )
                    Timber.d("Run started successfully: ${session.sessionId}")
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to start run"
                    )
                    Timber.e(exception, "Failed to start run")
                }
        }
    }
    
    fun endRun() {
        val currentSession = _activeSession.value
        if (currentSession == null) {
            _uiState.value = _uiState.value.copy(error = "No active run to end")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            runRepository.endRun(currentSession.sessionId)
                .onSuccess { endedSession ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRunActive = false
                    )
                    Timber.d("Run ended successfully: ${endedSession.sessionId}")
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to end run"
                    )
                    Timber.e(exception, "Failed to end run")
                }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    private fun observeActiveRun() {
        // For now, observe for a default user - in real app this would come from auth
        val userId = "default_user"
        
        runRepository.observeActiveRun(userId)
            .catch { exception ->
                Timber.e(exception, "Error observing active run")
                _uiState.value = _uiState.value.copy(
                    error = "Failed to observe run state"
                )
            }
            .onEach { session ->
                _activeSession.value = session
                _uiState.value = _uiState.value.copy(
                    isRunActive = session != null && session.isActive
                )
            }
            .launchIn(viewModelScope)
    }
}

/**
 * UI state for the Run screen.
 */
data class RunUiState(
    val isLoading: Boolean = false,
    val isRunActive: Boolean = false,
    val error: String? = null
)