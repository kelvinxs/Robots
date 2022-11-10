package dev.kxisto.robots.models

import dev.kxisto.robots.models.interfaces.Player

interface GameMapElement {
    val position: MapPosition
    val distanceFromTarget: Int
}

data class GameMapUnexploredArea(
    override val position: MapPosition,
    override val distanceFromTarget: Int = 0
) : GameMapElement

data class GameMapTarget(
    override val position: MapPosition,
    override val distanceFromTarget: Int = 0,
    var found: Boolean
) : GameMapElement

data class GameMapPlayer(
    override val position: MapPosition,
    override val distanceFromTarget: Int = 0,
    val gamePlayer: Player,
) : GameMapElement

data class GameMapPlayerWithTarget(
    override val position: MapPosition,
    override val distanceFromTarget: Int = 0,
    val gamePlayer: Player
) :
    GameMapElement

data class GameMapPlayerTrail(
    override val position: MapPosition,
    override val distanceFromTarget: Int = 0,
    val gamePlayer: Player,
) : GameMapElement