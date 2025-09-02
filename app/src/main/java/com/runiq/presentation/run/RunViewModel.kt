package com.runiq.presentation.run

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runiq.domain.repository.RunRepository
import com.runiq.domain.repository.HealthRepository
import com.runiq.domain.usecase.StartRunUseCase
import com.runiq.domain.usecase.ObserveRunStateUseCase
import com.runiq.domain.model.RunState
import com.runiq.domain.model.WorkoutType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RunViewModel @Inject constructor(
    private val runRepository: RunRepository,
    private val healthRepository: HealthRepository,
    private val startRunUseCase: StartRunUseCase,
    private val observeRunStateUseCase: ObserveRunStateUseCase
) : ViewModel() {
    
    private val _runState = MutableStateFlow<RunState>(RunState.Idle)
    val runState: StateFlow<RunState> = _runState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        Timber.d("RunViewModel initialized with injected dependencies")
        observeRunState()
    }
    
    private fun observeRunState() {
        observeRunStateUseCase()
            .onEach { state ->
                _runState.value = state
                Timber.d("Run state updated: $state")
            }
            .launchIn(viewModelScope)
    }
    
    fun startRun(workoutType: WorkoutType) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = startRunUseCase(workoutType)
                
                result.fold(
                    onSuccess = { sessionId ->
                        Timber.d("Run started successfully: $sessionId")
                    },
                    onFailure = { error ->
                        Timber.e(error, "Failed to start run")
                        // Handle error state
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error starting run")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun pauseRun() {
        viewModelScope.launch {
            try {
                runRepository.pauseCurrentRun()
                Timber.d("Run paused")
            } catch (e: Exception) {
                Timber.e(e, "Failed to pause run")
            }
        }
    }
    
    fun stopRun() {
        viewModelScope.launch {
            try {
                runRepository.stopCurrentRun()
                Timber.d("Run stopped")
            } catch (e: Exception) {
                Timber.e(e, "Failed to stop run")
            }
        }
    }
}