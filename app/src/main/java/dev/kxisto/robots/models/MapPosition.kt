package dev.kxisto.robots.models

import java.lang.Integer.sum
import kotlin.math.abs

data class MapPosition(val x: Int, val y: Int) {
    fun getDistance(position: MapPosition): Int =
        sum(abs(position.x - x), abs(position.y - y))
}