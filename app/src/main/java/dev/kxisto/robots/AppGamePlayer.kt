package dev.kxisto.robots

import androidx.compose.ui.graphics.Color
import dev.kxisto.robots.models.interfaces.Player

data class AppGamePlayer(val gamePlayer: Player, val color: Color, val trailColor: Color) :
    java.io.Serializable