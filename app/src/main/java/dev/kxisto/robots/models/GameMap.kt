package dev.kxisto.robots.models

import kotlin.math.abs
import kotlin.random.Random

class GameMap {
    private val mapGrid: Array<Array<GameMapElement>>

    constructor(gameSettings: GameSettings) {
        val targetPosition = generateItemPosition(gameSettings)
        mapGrid = generateNewGameGrid(targetPosition, gameSettings)
    }

    constructor(mapGrid: Array<Array<GameMapElement>>) {
        this.mapGrid = mapGrid
    }

    private fun generateItemPosition(gameSettings: GameSettings): GameMapTarget {
        var randPositionX = 0
        while ((randPositionX == 0) || (randPositionX == (gameSettings.xSize - 1))) {
            randPositionX = abs(Random.nextInt()) % gameSettings.xSize
        }

        var randPositionY = 0
        while ((randPositionY == 0) || (randPositionY == (gameSettings.ySize - 1))) {
            randPositionY = abs(Random.nextInt()) % gameSettings.ySize
        }

        return GameMapTarget(
            position = MapPosition(x = randPositionX, y = randPositionY),
            found = false
        )
    }

    private fun generateNewGameGrid(
        target: GameMapTarget,
        gameSettings: GameSettings
    ): Array<Array<GameMapElement>> {
        val map = Array<Array<GameMapElement>>(size = gameSettings.ySize) { y ->
            Array(size = gameSettings.xSize) { x ->
                GameMapUnexploredArea(
                    position = MapPosition(x, y),
                    distanceFromTarget = if (gameSettings.showProximity) target.position.getDistance(
                        MapPosition(x, y)
                    ) else 0
                )
            }
        }

        map[target.position.y][target.position.x] = target
        return map
    }

    fun getAt(mapPosition: MapPosition): GameMapElement = mapGrid[mapPosition.y][mapPosition.x]

    fun getAt(x: Int, y: Int): GameMapElement = mapGrid[y][x]

    fun changeElement(element: GameMapElement) {
        mapGrid[element.position.y][element.position.x] = element
    }

    fun listElements(): List<GameMapElement> {
        val list = mutableListOf<GameMapElement>()
        for (row in mapGrid) {
            list.addAll(row)
        }
        return list
    }

    fun getMapGrid(): Array<Array<GameMapElement>> = mapGrid
}