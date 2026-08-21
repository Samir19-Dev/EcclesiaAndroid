package com.ecclesia.android.ui.screens.verifyemail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecclesia.android.ui.components.ClickableText
import com.ecclesia.android.ui.components.ErrorMessage
import com.ecclesia.android.ui.components.InputField
import com.ecclesia.android.ui.components.PrimaryButton

@Composable
fun VerifyEmailScreen(
    correo: String,
    onLoginClick: () -> Unit = {},
    viewModel: VerifyEmailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(correo) {
        viewModel.setCorreo(correo)
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Verifica tu correo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Te enviamos un enlace de validación a ${uiState.correo.ifBlank { correo }}. Revisa tu bandeja de entrada (y el spam).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            uiState.mensaje?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            uiState.error?.let {
                ErrorMessage(message = it)
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedButton(
                onClick = { viewModel.reenviar {} },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.isLoading) "Enviando..." else "Reenviar email de validación")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¿Recibiste el token? Pégalo aquí:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            InputField(
                value = uiState.token,
                onValueChange = viewModel::onTokenChange,
                label = "Token de validación"
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = if (uiState.validando) "Validando..." else "Validar correo",
                onClick = { viewModel.validar {} },
                enabled = !uiState.validando
            )

            Spacer(modifier = Modifier.height(16.dp))

            ClickableText(text = "Ya validé mi correo, iniciar sesión", onClick = onLoginClick)
        }
    }
}
