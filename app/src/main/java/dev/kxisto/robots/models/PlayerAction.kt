package dev.kxisto.robots.models

import dev.kxisto.robots.models.enum.MovementDirection

interface PlayerAction {
    val playerId: Int
}
data class PlayerMoving(val direction: MovementDirection, override val playerId: Int) : PlayerAction
class PlayerDidNothing(override val playerId: Int) : PlayerAction