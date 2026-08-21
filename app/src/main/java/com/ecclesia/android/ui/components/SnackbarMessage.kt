package com.ecclesia.android.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
fun SnackbarMessage(
    message: String?,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    actionLabel: String? = null,
    onActionPerformed: () -> Unit = {}
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier
    )

    LaunchedEffect(message) {
        message?.let {
            val result = snackbarHostState.showSnackbar(
                message = it,
                actionLabel = actionLabel,
                duration = SnackbarDuration.Short
            )

            when (result) {
                SnackbarResult.ActionPerformed -> onActionPerformed()
                SnackbarResult.Dismissed -> onDismiss()
            }
        }
    }
}
