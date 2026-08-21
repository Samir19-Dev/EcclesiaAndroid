package com.ecclesia.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ecclesia.android.ui.theme.AzulClaro
import com.ecclesia.android.ui.theme.BordeInput
import com.ecclesia.android.ui.theme.TextoSuave


@Composable
fun EcclesiaSearchField(
    valor: String,
    onCambio: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar…",
    hint: String? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = TextoSuave) },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null, tint = TextoSuave, modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
                if (valor.isNotEmpty()) {
                    IconButton(onClick = { onCambio("") }) {
                        Icon(Icons.Filled.Close, contentDescription = "Limpiar", tint = TextoSuave, modifier = Modifier.size(18.dp))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AzulClaro,
                unfocusedBorderColor = BordeInput,
                cursorColor = AzulClaro,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        if (hint != null) {
            Text(
                text = hint,
                color = TextoSuave,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}
