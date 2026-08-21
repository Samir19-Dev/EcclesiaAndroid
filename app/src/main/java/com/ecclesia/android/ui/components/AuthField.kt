package com.ecclesia.android.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ecclesia.android.ui.theme.AzulClaro
import com.ecclesia.android.ui.theme.BordeInput
import com.ecclesia.android.ui.theme.ErrorLogin
import com.ecclesia.android.ui.theme.TextoSuave


@Composable
fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String? = null,
    icono: ImageVector,
    esContrasena: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null,
    mostrarError: Boolean = true
) {
    var visible by remember { mutableStateOf(false) }
    val trazo = if (error != null) ErrorLogin else null

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label, color = TextoSuave) },
        placeholder = { placeholder?.let { Text(it, color = TextoSuave.copy(alpha = 0.6f)) } },
        singleLine = true,
        leadingIcon = { Icon(icono, contentDescription = null, tint = TextoSuave) },
        trailingIcon = {
            if (esContrasena) {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        imageVector = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = TextoSuave
                    )
                }
            }
        },
        visualTransformation = if (esContrasena && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
        isError = error != null,
        supportingText = {
            if (error != null && mostrarError) {
                Text(error, color = ErrorLogin)
            }
        },
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (trazo != null) trazo else AzulClaro,
            unfocusedBorderColor = if (trazo != null) trazo else BordeInput,
            cursorColor = AzulClaro,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorBorderColor = ErrorLogin,
            errorContainerColor = Color.White
        )
    )
}
