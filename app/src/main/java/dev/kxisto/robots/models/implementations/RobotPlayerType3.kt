package dev.kxisto.robots.models.implementations

import dev.kxisto.robots.models.*
import dev.kxisto.robots.models.enum.MovementDirection
import dev.kxisto.robots.models.interfaces.Player

class RobotPlayerType3(override val name: String = "", override val id: Int = 0) : Player {
    override val typeName: String
        get() = "Robot: Seek by distance then random"

    override fun onClear() {}

    override fun onDestroy() {}

    override fun chooseNextMove(
        getMap: (Int) -> GameMap,
        allowedMovements: List<MovementDirection>
    ): PlayerAction {
        if (allowedMovements.isEmpty()) {
            return PlayerDidNothing(id)
        }

        val map = getMap(id)
        val playerCurrentPosition =
            map.listElements().first { it is GameMapPlayer && it.gamePlayer.id == id }.position

        val targets = allowedMovements.map { direction ->
            val xPosition = playerCurrentPosition.x + direction.x
            val yPosition = playerCurrentPosition.y + direction.y
            Pair(direction, map.getAt(xPosition, yPosition))
        }.toList()

        targets.sortedBy { it.second.distanceFromTarget }

        val selectedDirection: MovementDirection =
            if (targets.all { it.second.distanceFromTarget == 0 }) {
                if (targets.any { it.second is GameMapTarget }) {
                    targets.first { it.second is GameMapTarget }.first
                } else if (targets.any { it.second is GameMapUnexploredArea }) {
                    val unexplored =
                        targets.filter { it.second is GameMapUnexploredArea }.shuffled()
                    unexplored.first { it.second is GameMapUnexploredArea }.first
                } else {
                    targets.shuffled().first().first
                }
            } else {
                targets.minByOrNull { it.second.distanceFromTarget }!!.first
            }

        return PlayerMoving(direction = selectedDirection, playerId = id)
    }

    override fun toString(): String {
        return typeName
    }
}