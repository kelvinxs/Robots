package dev.kxisto.robots.features.game.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import dev.kxisto.robots.AppGamePlayer
import dev.kxisto.robots.models.*
import dev.kxisto.robots.ui.theme.AppColors

@Composable
fun GameMapCompose(gameMap: GameMap, players: List<AppGamePlayer>) {
    val configuration = LocalConfiguration.current
    val mapGrid = gameMap.getMapGrid()
    val context = LocalContext.current
    val biggestCount: Int = Integer.max(gameMap.listElements().maxOf { it.position.x },
        gameMap.listElements().maxOf { it.position.y }) + 1

    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val blockSize = (min(screenHeight, screenWidth) - 32.dp) / biggestCount

    val unexploredItemColor = AppColors.GameUnknownAreaColor
    val targetColor = AppColors.GameTargetColor

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        for (row in mapGrid) {
            Row() {
                for (item in row) {
                    val color = if (item is GameMapTarget) {
                        targetColor
                    } else if (item is GameMapPlayerTrail) {
                        players.first { it.gamePlayer.id == item.gamePlayer.id }.trailColor
                    } else if (item is GameMapPlayer) {
                        players.first { it.gamePlayer.id == item.gamePlayer.id }.color
                    } else if (item is GameMapPlayerWithTarget) {
                        players.first { it.gamePlayer.id == item.gamePlayer.id }.color
                    } else {
                        unexploredItemColor
                    }

                    var modifier = Modifier
                        .background(color = color)
                        .size(blockSize)

                    if (item is GameMapPlayerWithTarget) {
                        modifier = modifier.border(width = 5.dp, color = targetColor)
                    } else {
                        modifier =
                            modifier.border(width = 2.dp, color = AppColors.GameAreaBorderColor)
                    }

                    Box(
                        modifier = modifier
                    ) {
                        if(item.distanceFromTarget > 0 && item is GameMapUnexploredArea){
                            Box(modifier = modifier.background(targetColor.copy(alpha = 1f / (item.distanceFromTarget+1))))
                        }
                    }
                }
            }
        }
    }
}