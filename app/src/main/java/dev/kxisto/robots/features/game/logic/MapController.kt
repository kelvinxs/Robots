package dev.kxisto.robots.features.game.logic

import dev.kxisto.robots.models.*
import dev.kxisto.robots.models.interfaces.Player
import kotlinx.coroutines.Job

class MapController(
    private val gameSettings: GameSettings,
    private val players: List<Player>,
) {
    private lateinit var gameMap: GameMap
    var job: Job? = null

    fun generateNewGameMap() {
        val map = GameMap(gameSettings)
        putPlayersOnMap(map)
        gameMap = map
    }

    private fun putPlayersOnMap(map: GameMap) {
        val playersPositions = listOf(
            MapPosition(0, 0),
            MapPosition(gameSettings.xSize - 1, gameSettings.ySize - 1),
            MapPosition(0, gameSettings.ySize - 1),
            MapPosition(gameSettings.xSize - 1, 0)
        )

        for (index in players.indices) {
            val position = playersPositions[index]
            map.changeElement(
                GameMapPlayer(
                    gamePlayer = players[index],
                    distanceFromTarget = map.getAt(position).distanceFromTarget,
                    position = position
                )
            )
        }
    }

    fun movePlayerPerAction(playerAction: PlayerMoving) {
        val playerCurrentPosition = getPlayerCurrentPosition(playerAction.playerId)
        val playerNextPosition = MapPosition(
            x = playerCurrentPosition.x + playerAction.direction.x,
            y = playerCurrentPosition.y + playerAction.direction.y
        )

        val elementAtNextPosition = gameMap.getAt(playerNextPosition)

        gameMap.changeElement(
            GameMapPlayerTrail(
                position = playerCurrentPosition,
                gamePlayer = players.first { it.id == playerAction.playerId },
                distanceFromTarget = gameMap.getAt(playerCurrentPosition).distanceFromTarget,
            )
        )

        if (elementAtNextPosition is GameMapTarget) {
            gameMap.changeElement(
                GameMapPlayerWithTarget(
                    position = elementAtNextPosition.position,
                    gamePlayer = players.first { it.id == playerAction.playerId }
                )
            )
            return
        } else {
            gameMap.changeElement(
                GameMapPlayer(
                    position = elementAtNextPosition.position,
                    gamePlayer = players.first { it.id == playerAction.playerId },
                    distanceFromTarget = elementAtNextPosition.distanceFromTarget,
                )
            )
        }
    }

    private fun getPlayerCurrentPosition(playerId: Int): MapPosition {
        val mapElements = gameMap.listElements()
        return mapElements.first { (it is GameMapPlayer && it.gamePlayer.id == playerId) || (it is GameMapPlayerWithTarget && it.gamePlayer.id == playerId) }.position
    }

    fun getMap(playerId: Int?): GameMap {
        if (playerId == null) return gameMap

        val playerCurrentPosition = getPlayerCurrentPosition(playerId)
        val mapGrid = gameMap.getMapGrid().map {
            it.map { area ->
                val distanceFromTarget =
                    if (gameSettings.showProximity && area.position.getDistance(
                            playerCurrentPosition
                        ) == 1
                    ) area.distanceFromTarget
                    else 0

                var result: GameMapElement =
                    GameMapUnexploredArea(area.position, distanceFromTarget = distanceFromTarget)
                if (area is GameMapTarget && gameSettings.showTargetAdjacent
                    && area.position.getDistance(playerCurrentPosition) == 1
                ) {
                    result = area.copy(distanceFromTarget = distanceFromTarget)
                } else if (area is GameMapUnexploredArea) {
                    result = area.copy(distanceFromTarget = distanceFromTarget)
                } else if (area is GameMapPlayerTrail) {
                    if (area.gamePlayer.id == playerId ||
                        area.position.getDistance(playerCurrentPosition) <= 1
                    ) {
                        result = area.copy(distanceFromTarget = distanceFromTarget)
                    }
                } else if (area is GameMapPlayer) {
                    if (area.gamePlayer.id == playerId ||
                        area.position.getDistance(playerCurrentPosition) <= 1
                    ) {
                        result = area.copy(distanceFromTarget = distanceFromTarget)
                    }
                }

                result
            }.toTypedArray()
        }.toTypedArray()

        return GameMap(mapGrid)
    }

    fun close() {
        job?.cancel()
    }
}