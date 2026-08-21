package com.ecclesia.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun ClickableText(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier.clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.primary,
        textDecoration = TextDecoration.Underline
    )
}
