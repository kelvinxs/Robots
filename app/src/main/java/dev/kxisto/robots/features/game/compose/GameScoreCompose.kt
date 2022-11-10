package dev.kxisto.robots.features.game.compose

import android.content.res.Configuration
import dev.kxisto.robots.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.kxisto.robots.AppGamePlayer
import dev.kxisto.robots.models.interfaces.Player

@Composable
fun GameScoreCompose(scores: Map<AppGamePlayer, Int>, onPlayerSelected: (Player) -> Unit) {
    val configuration = LocalConfiguration.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            for (score in scores) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .border(width = 1.dp, color = Color(0x30000000))
                        .padding(4.dp)
                        .clickable { onPlayerSelected(score.key.gamePlayer) },
                ) {
                    Row() {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(color = score.key.color)
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(color = score.key.trailColor)
                        )
                    }
                    Text(score.key.gamePlayer.name)
                    Text(stringResource(R.string.score_value, score.value))
                }
            }
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            for (score in scores) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .border(width = 1.dp, color = Color(0x30000000))
                        .padding(4.dp)
                        .clickable { onPlayerSelected(score.key.gamePlayer) },
                ) {
                    Row() {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(color = score.key.color)
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(color = score.key.trailColor)
                        )
                    }
                    Text(score.key.gamePlayer.name)
                    Text(stringResource(R.string.score_value, score.value))
                }
            }
        }
    }
}