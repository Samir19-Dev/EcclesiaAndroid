package com.ecclesia.android.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    error: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { if (label != null) Text(label) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.VerifiedUser, contentDescription = null) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        isError = error != null,
        supportingText = { error?.let { Text(it) } }
    )
}
