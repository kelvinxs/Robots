package dev.kxisto.robots.features.game

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.kxisto.robots.AppGamePlayer
import dev.kxisto.robots.R
import dev.kxisto.robots.features.game.compose.GameMapCompose
import dev.kxisto.robots.features.game.compose.GameScoreCompose
import dev.kxisto.robots.features.game.logic.Finished
import dev.kxisto.robots.models.GameMap
import dev.kxisto.robots.models.GameSettings
import dev.kxisto.robots.models.interfaces.Player
import dev.kxisto.robots.ui.theme.RobotsTheme

class GameActivity : ComponentActivity() {
    companion object {
        val settingsTag = "Game.settings"
        val numPlayersTag = "Game.numPlayers"
        fun getPlayerTagByIndex(index: Int) = "Game.player[$index]"
    }

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val settings = intent.getSerializableExtra(settingsTag) as GameSettings
            val numPlayers = intent.getIntExtra(numPlayersTag, 1)
            val players = mutableListOf<AppGamePlayer>()
            for (index in 0 until numPlayers) {
                players.add(intent.getSerializableExtra(getPlayerTagByIndex(index)) as AppGamePlayer)
            }

            gameViewModel.configGame(players = players, gameSettings = settings)
            gameViewModel.startNewGame()
        }

        setContent {
            RobotsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val map = gameViewModel.mapState.collectAsState().value
                    val viewState = gameViewModel.gameState.collectAsState().value
                    val scores = gameViewModel.scoreState.collectAsState().value


                    val configuration = LocalConfiguration.current

                    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
                            Content(
                                map, viewState, scores,
                                this@GameActivity::onNewGame,
                                this@GameActivity::onStopGame,
                                gameViewModel::changePerspective
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 16.dp),
                        ) {
                            Content(
                                map, viewState, scores,
                                this@GameActivity::onNewGame,
                                this@GameActivity::onStopGame,
                                gameViewModel::changePerspective
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onNewGame() {
        gameViewModel.nextRound()
    }

    private fun onStopGame() {
        this.onBackPressed()
    }
}


@Composable
private fun Content(
    map: GameMap?,
    viewState: GameViewState,
    scores: Map<AppGamePlayer, Int>,
    onNewGame: () -> Unit,
    onStopGame: () -> Unit,
    onPlayerSelected: (Player) -> Unit
) {
    val configuration = LocalConfiguration.current

    if (map != null) {
        GameMapCompose(map, viewState.players)
    }

    if (viewState.gameState is Finished) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Column() {
                Button(onClick = onNewGame) {
                    Text(stringResource(R.string.next_round))
                }
                Button(onClick = onStopGame) {
                    Text(stringResource(R.string.back_settings))
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                Button(onClick = onNewGame) {
                    Text(stringResource(R.string.next_round))
                }
                Button(onClick = onStopGame) {
                    Text(stringResource(R.string.back_settings))
                }
            }
        }
    }

    GameScoreCompose(
        scores = scores,
        onPlayerSelected = onPlayerSelected
    )
}