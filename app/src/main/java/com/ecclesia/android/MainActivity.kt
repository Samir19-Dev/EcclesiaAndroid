package com.ecclesia.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ecclesia.android.data.network.SessionManager
import com.ecclesia.android.data.repository.NotificacionesRepository
import com.ecclesia.android.ui.navigation.EcclesiaNavHost
import com.ecclesia.android.ui.theme.EcclesiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(this)
        NotificacionesRepository.init(this)
        enableEdgeToEdge()
        setContent {
            EcclesiaTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EcclesiaNavHost()
                }
            }
        }
    }
}
