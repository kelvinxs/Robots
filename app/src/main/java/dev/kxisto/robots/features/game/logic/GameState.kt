package dev.kxisto.robots.features.game.logic

import dev.kxisto.robots.models.PlayerAction

interface GameState
class NotStarted():GameState
class Starting():GameState
data class WaitingPlayerAction(val playerId: Int) : GameState
data class PlayerActed(val playerId: Int, val playerAction: PlayerAction) : GameState
data class Finished(val winnerId: Int?) : GameState