package dev.kxisto.robots.features.game.logic

import dev.kxisto.robots.models.GameMap
import dev.kxisto.robots.models.PlayerAction
import dev.kxisto.robots.models.enum.MovementDirection
import dev.kxisto.robots.models.interfaces.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch

class PlayerController(
    val player: Player,
    private val playerActionChannel: SendChannel<PlayerAction>,
) {
    private val actionRequestChannel = Channel<GameState>()
    private var job: Job? = null

    suspend fun requestNextMove() {
        actionRequestChannel.send(WaitingPlayerAction(playerId = player.id))
    }

    fun start(
        getPlayerMap: (Int) -> GameMap,
        getAllowedMovements: ((Int) -> List<MovementDirection>)
    ) {
        player.onClear()
        job = CoroutineScope(Dispatchers.Default).launch {
            for(state in actionRequestChannel){
                if (state !is WaitingPlayerAction || state.playerId != player.id) continue

                val allowedMovements = getAllowedMovements(player.id)
                val nextAction = player.chooseNextMove(getPlayerMap, allowedMovements)
                playerActionChannel.send(nextAction)
            }
        }
    }

    fun stop() {
        player.onClear()
        job?.cancel()
    }

    fun destroy() {
        stop()
        player.onDestroy()
    }
}