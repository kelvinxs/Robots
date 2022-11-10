package dev.kxisto.robots.features.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import dev.kxisto.robots.features.home.compose.GameSettingsForm
import dev.kxisto.robots.ui.theme.RobotsTheme

class HomeActivity : ComponentActivity() {
    private val scrollState = ScrollState(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RobotsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GameSettingsForm(scrollState = scrollState)
                }
            }
        }
    }
}