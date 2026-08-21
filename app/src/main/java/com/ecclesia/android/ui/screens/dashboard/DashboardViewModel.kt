package com.ecclesia.android.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.repository.DashboardData
import com.ecclesia.android.data.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: DashboardRepository = DashboardRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        cargar()
    }

    fun cargar() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val data: DashboardData = repository.cargar()
                _uiState.update {
                    it.copy(isLoading = false, data = data)
                }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(isLoading = false, error = ApiErrorParser.mensaje(t))
                }
            }
        }
    }
}
