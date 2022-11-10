package dev.kxisto.robots.features.game

import androidx.lifecycle.ViewModel
import dev.kxisto.robots.AppGamePlayer
import dev.kxisto.robots.features.game.logic.*
import dev.kxisto.robots.models.GameMap
import dev.kxisto.robots.models.GameSettings
import dev.kxisto.robots.models.interfaces.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class GameViewModel() : ViewModel() {
    private lateinit var gameController: GameController

    lateinit var players: List<AppGamePlayer>
    lateinit var gameSettings: GameSettings
    private var scores = mutableMapOf<AppGamePlayer, Int>()
    private var gamePlayerView: Player? = null

    private val _gameState = MutableStateFlow(GameViewState())
    val gameState: StateFlow<GameViewState> = _gameState.asStateFlow()

    private val _mapState = MutableStateFlow<GameMap?>(null)
    val mapState: StateFlow<GameMap?> = _mapState.asStateFlow()

    private val _scoreState = MutableStateFlow<Map<AppGamePlayer, Int>>(mapOf())
    val scoreState: StateFlow<Map<AppGamePlayer, Int>> = _scoreState.asStateFlow()

    fun configGame(players: List<AppGamePlayer>, gameSettings: GameSettings) {
        this.players = players
        this.gameSettings = gameSettings
        scores = players.associateWith { 0 }.toMutableMap()
        _scoreState.update {
            scores
        }
        gameController = GameController(
            players = players.map { it.gamePlayer },
            gameSettings = gameSettings,
        )
        gameController.addListener(::onGameStateChanged)
    }

    fun startNewGame() {
        scores = players.associateWith { 0 }.toMutableMap()
        runGame()
    }

    fun nextRound() {
        runGame()
    }

    private fun runGame() {
        gameController.startGame()
    }

    private fun onGameStateChanged(state: GameState) {
        if (state is Finished && state.winnerId != null) {
            val winnerPlayer = players.first { it.gamePlayer.id == state.winnerId }
            val currentScore = scores[winnerPlayer]!!
            scores[winnerPlayer] = currentScore + 1
            _scoreState.update {
                return@update scores
            }
        }
        _mapState.update {
            if (state is Starting || state is NotStarted) {
                return@update null
            }
            if (state is Finished) {
                return@update gameController.getMap(null)
            }

            return@update gameController.getMap(gamePlayerView?.id)
        }
        _gameState.update {
            return@update GameViewState(
                players = players,
                gameState = state
            )
        }
    }

    fun changePerspective(player: Player) {
        if (gamePlayerView == player) {
            gamePlayerView = null
        } else {
            gamePlayerView = player
        }
        _mapState.update {
            gameController.getMap(gamePlayerView?.id)
        }
    }

    fun closeControllers() {
        gameController.removeListener(::onGameStateChanged)
        gameController.close()
    }

    override fun onCleared() {
        closeControllers()
        super.onCleared()
    }
}