package dev.kxisto.robots.models

data class GameSettings(
    val xSize: Int = 5,
    val ySize: Int = 5,
    val showProximity: Boolean = false,
    val showTargetAdjacent: Boolean = false,
) : java.io.Serializable {
    constructor(
        size: Int,
        showProximity: Boolean = false,
        showTargetAdjacent: Boolean = false
    ) : this(
        xSize = size,
        ySize = size,
        showProximity = showProximity,
        showTargetAdjacent = showTargetAdjacent,
    )
}
