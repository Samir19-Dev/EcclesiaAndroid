package com.ecclesia.android.ui.screens.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecclesia.android.ui.components.AppTopBar
import com.ecclesia.android.ui.components.EcclesiaCard
import com.ecclesia.android.ui.components.PageSubtitle
import com.ecclesia.android.ui.components.PageTitle
import com.ecclesia.android.ui.theme.Dorado
import com.ecclesia.android.ui.theme.TextoSuave


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(
    titulo: String,
    subtitulo: String? = null,
    onMenuClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            AppTopBar(
                titulo = titulo,
                onMenuClick = onMenuClick,
                onBackClick = onBackClick
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            EcclesiaCard(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Construction,
                        contentDescription = null,
                        tint = Dorado,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    PageTitle(text = titulo)
                    Text(
                        text = "Esta sección se está implementando.",
                        color = TextoSuave,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    if (subtitulo != null) {
                        PageSubtitle(
                            text = subtitulo,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
