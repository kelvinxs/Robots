package dev.kxisto.robots.features.home

import androidx.compose.ui.graphics.Color
import dev.kxisto.robots.models.interfaces.Player
import kotlin.reflect.KClass

data class PlayerFormData(
    var name: String = "",
    var type: Player,
    var colorPair: Pair<Color, Color>
)