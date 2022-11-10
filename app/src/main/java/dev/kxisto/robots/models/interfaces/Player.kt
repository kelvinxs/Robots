package dev.kxisto.robots.models.interfaces

import dev.kxisto.robots.models.GameMap
import dev.kxisto.robots.models.PlayerAction
import dev.kxisto.robots.models.enum.MovementDirection

interface Player : java.io.Serializable {
    val name: String
    val id: Int
    val typeName: String

    fun onDestroy()
    fun onClear()
    fun chooseNextMove(getMap: (Int) -> GameMap, allowedMovements: List<MovementDirection>): PlayerAction
}