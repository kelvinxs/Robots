package dev.kxisto.robots.models.enum

enum class MovementDirection(val x: Int, val y: Int) {
    Up(0, -1), Down(0, 1), Left(-1, 0), Right(1, 0)
}
