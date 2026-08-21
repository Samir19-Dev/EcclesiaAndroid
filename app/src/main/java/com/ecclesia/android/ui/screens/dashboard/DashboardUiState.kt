package com.ecclesia.android.ui.screens.dashboard

import com.ecclesia.android.data.repository.DashboardData

data class DashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val data: DashboardData = DashboardData()
)
