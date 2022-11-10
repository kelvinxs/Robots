package dev.kxisto.robots.features.game.logic

import dev.kxisto.robots.models.*
import dev.kxisto.robots.models.enum.MovementDirection
import dev.kxisto.robots.models.interfaces.Player
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect

class GameController(
    private val gameSettings: GameSettings,
    players: List<Player>,
) {
    private lateinit var job1: Job
    private lateinit var job2: Job
    private val playerOrder: List<Player> = players.shuffled()
    private val gameStateFlow = MutableStateFlow<GameState>(NotStarted())
    private val mapController: MapController = MapController(
        gameSettings = gameSettings,
        players = playerOrder,
    )
    private val playerActionChannel = Channel<PlayerAction>()
    private val playersControllers: MutableList<PlayerController> = mutableListOf()
    private val listeners: MutableList<(GameState) -> Unit> = mutableListOf()

    init {
        playersControllers.addAll(playerOrder.map { player ->
            PlayerController(player = player, playerActionChannel = playerActionChannel)
        })
        startCoroutines()
    }

    fun addListener(onStateChange: (GameState) -> Unit) {
        listeners.add(onStateChange)
    }

    fun removeListener(onStateChange: (GameState) -> Unit): Boolean {
        val index = listeners.indexOf(onStateChange)
        if (index < 0) return false
        listeners.removeAt(index)
        return true
    }

    private fun startCoroutines() {
        for (controller in playersControllers) {
            controller.start(
                getPlayerMap = this@GameController::getMap,
                getAllowedMovements = this@GameController::getPlayerAllowedMovements,
            );
        }
        job1 = CoroutineScope(Dispatchers.Default).launch {
            gameStateFlow.collect(this@GameController::onGameStateUpdated)
        }
        job2 = CoroutineScope(Dispatchers.Default).launch {
            for (action in playerActionChannel) {
                onPlayerAction(action)
            }
        }
    }

    private suspend fun onGameStateUpdated(state: GameState) {
        listeners.forEach {
            it.invoke(state)
        }

        if (state is Starting) {
            mapController.generateNewGameMap()
            val nextId = playerOrder.first().id
            val nextState = WaitingPlayerAction(nextId)
            gameStateFlow.emit(nextState)
        } else if (state is WaitingPlayerAction) {
            val playerController = playersControllers.first { it.player.id == state.playerId }
            playerController.requestNextMove()
        } else if (state is PlayerActed) {
            val found = mapController.getMap(null).listElements()
                .firstOrNull { it is GameMapPlayerWithTarget } as GameMapPlayerWithTarget?
            if (found != null) {
                gameStateFlow.emit(Finished(winnerId = found.gamePlayer.id))
                for (controller in playersControllers) {
                    controller.stop()
                }
                return
            } else {
                delay(500)

                val currentPlayerIndex = playerOrder.indexOfFirst { it.id == state.playerId }
                val nextId = playerOrder[(currentPlayerIndex + 1) % playerOrder.size].id
                val nextState = WaitingPlayerAction(nextId)
                gameStateFlow.emit(nextState)
            }
        }
    }

    fun startGame() {
        CoroutineScope(Dispatchers.Default).launch {
            gameStateFlow.emit(Starting())
            cancel()
        }
    }

    private suspend fun onPlayerAction(playerAction: PlayerAction) {
        if (!isPlayerActionValid(playerId = playerAction.playerId, playerAction)) {
            gameStateFlow.emit(WaitingPlayerAction(playerAction.playerId))
            return
        }

        if (playerAction is PlayerDidNothing) {
            return
        }

        if (playerAction !is PlayerMoving) {
            return
        }

        mapController.movePlayerPerAction(playerAction)

        gameStateFlow.emit(
            PlayerActed(
                playerAction = playerAction,
                playerId = playerAction.playerId
            )
        )
    }

    private fun isPlayerActionValid(playerId: Int, playerAction: PlayerAction): Boolean {
        return playerAction !is PlayerMoving || getPlayerAllowedMovements(playerId).any { it == playerAction.direction }
    }

    private fun getPlayerCurrentPosition(playerId: Int): MapPosition {
        val mapElements = mapController.getMap(null).listElements()
        return mapElements.first { it is GameMapPlayer && it.gamePlayer.id == playerId }.position
    }

    private fun getPlayerAllowedMovements(playerId: Int): List<MovementDirection> {
        val movements: MutableList<MovementDirection> = mutableListOf()
        val playerCurrentPosition = getPlayerCurrentPosition(playerId)

        val gameMap = mapController.getMap(playerId)

        for (direction in MovementDirection.values()) {
            val predictedXPosition = direction.x + playerCurrentPosition.x
            val predictedYPosition = direction.y + playerCurrentPosition.y

            if (predictedXPosition < 0 || predictedYPosition < 0) continue
            if (predictedXPosition >= gameSettings.xSize || predictedYPosition >= gameSettings.ySize) continue

            val predictedPositionCurrentElement =
                gameMap.getAt(predictedXPosition, predictedYPosition)
            if (predictedPositionCurrentElement is GameMapPlayerTrail) {
                if (predictedPositionCurrentElement.gamePlayer.id == playerId) {
                    movements.add(direction)
                    continue
                }
            } else if (predictedPositionCurrentElement is GameMapPlayer) {
                if (predictedPositionCurrentElement.gamePlayer.id == playerId) {
                    movements.add(direction)
                    continue
                }
            } else {
                movements.add(direction)
                continue
            }
        }

        return movements
    }

    fun getMap(playerId: Int?): GameMap = mapController.getMap(playerId)

    fun close() {
        job1.cancel()
        job2.cancel()
        listeners.clear()
        playerActionChannel.cancel()
        for (controller in playersControllers) {
            controller.destroy()
        }
    }
}