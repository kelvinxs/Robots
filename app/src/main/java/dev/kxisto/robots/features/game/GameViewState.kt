package dev.kxisto.robots.features.game

import dev.kxisto.robots.AppGamePlayer
import dev.kxisto.robots.features.game.logic.GameState
import dev.kxisto.robots.features.game.logic.NotStarted
import dev.kxisto.robots.models.GameMap

data class GameViewState(
    val gameState: GameState,
    val players: List<AppGamePlayer>
) {
    constructor() : this(gameState = NotStarted(), players = listOf())
}