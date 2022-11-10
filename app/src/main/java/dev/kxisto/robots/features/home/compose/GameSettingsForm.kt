package dev.kxisto.robots.features.home.compose

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.kxisto.robots.AppGamePlayer
import dev.kxisto.robots.R
import dev.kxisto.robots.features.game.GameActivity
import dev.kxisto.robots.features.home.PlayerFormData
import dev.kxisto.robots.models.GameSettings
import dev.kxisto.robots.models.implementations.RobotPlayerType1
import dev.kxisto.robots.models.implementations.RobotPlayerType2
import dev.kxisto.robots.models.implementations.RobotPlayerType3
import dev.kxisto.robots.models.interfaces.Player
import dev.kxisto.robots.ui.theme.AppColors
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun GameSettingsForm(scrollState: ScrollState) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(32.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
    ) {

        val availablePlayerColors = listOf(
            Pair(
                AppColors.PlayerColor1,
                AppColors.PlayerTrailColor1
            ),
            Pair(
                AppColors.PlayerColor2,
                AppColors.PlayerTrailColor2
            ),
            Pair(
                AppColors.PlayerColor3,
                AppColors.PlayerTrailColor3
            ),
            Pair(
                AppColors.PlayerColor4,
                AppColors.PlayerTrailColor4
            )
        )
        val players = remember {
            mutableStateListOf(
                PlayerFormData(
                    name = "",
                    colorPair = availablePlayerColors.first(),
                    type = RobotPlayerType1(),
                )
            )
        }

        val numRows = remember { mutableStateOf(7) }
        val numColumns = remember { mutableStateOf(7) }
        val allowPlayerSeeNextBlockDistance = remember { mutableStateOf(false) }
        val allowPlayerSeeTarget = remember { mutableStateOf(false) }

        Text(text = stringResource(R.string.row_count, numRows.value))
        Slider(
            value = numRows.value.toFloat(),
            onValueChange = {
                val sizeInt = if ((it - it.toInt() >= .5)) ceil(it).toInt() else floor(it).toInt()
                numRows.value = sizeInt
            },
            steps = 1,
            valueRange = 5f..10f
        )

        Text(text = stringResource(R.string.column_count, numColumns.value))
        Slider(
            modifier = Modifier.padding(top = 16.dp),
            value = numColumns.value.toFloat(),
            onValueChange = {
                val sizeInt = if ((it - it.toInt() >= .5)) ceil(it).toInt() else floor(it).toInt()
                numColumns.value = sizeInt
            },
            steps = 1,
            valueRange = 5f..10f
        )

        Text(text = stringResource(R.string.players_count, players.count()))
        Slider(
            modifier = Modifier.padding(top = 16.dp),
            value = players.size.toFloat(),
            onValueChange = { size ->
                val sizeInt =
                    if ((size - size.toInt() >= .5)) ceil(size).toInt() else floor(size).toInt()
                if (sizeInt > players.size) {
                    val newPlayers = mutableListOf<PlayerFormData>()
                    for (count in players.size until sizeInt) {
                        val colorPair =
                            availablePlayerColors.first { colorPair -> players.none { p -> p.colorPair == colorPair } && newPlayers.none { p2 -> p2.colorPair == colorPair } }
                        newPlayers.add(
                            PlayerFormData(
                                name = "",
                                colorPair = colorPair,
                                type = RobotPlayerType1(),
                            )
                        )
                    }
                    players.addAll(newPlayers)
                } else if (sizeInt < players.size) {
                    players.removeRange(sizeInt - 1, players.size)
                }
            },
            steps = 1,
            valueRange = 1f..4f
        )

        for (index in players.indices) {
            if (index > 0) {
                Box(modifier = Modifier.height(8.dp))
            }
            PlayerEditor(
                player = players[index],
                getAvailableColors = { currentColor ->
                    return@PlayerEditor availablePlayerColors.filter { colorPair -> colorPair == currentColor || players.none { player -> player.colorPair == colorPair } }
                },
                { map ->
                    players.removeAt(index)
                    players.add(index, map)
                },
            )
        }


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Checkbox(
                checked = allowPlayerSeeNextBlockDistance.value,
                onCheckedChange = {
                    allowPlayerSeeNextBlockDistance.value = it
                },
            )
            Text(stringResource(R.string.allow_players_see_distance))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Checkbox(
                checked = allowPlayerSeeTarget.value,
                onCheckedChange = {
                    allowPlayerSeeTarget.value = it
                },
            )
            Text(stringResource(R.string.allow_players_see_target_adjacent))
        }

        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = {
                val intent = Intent(context, GameActivity::class.java)

                intent.putExtra(
                    GameActivity.settingsTag,
                    GameSettings(
                        ySize = numRows.value,
                        xSize = numColumns.value,
                        showProximity = allowPlayerSeeNextBlockDistance.value,
                        showTargetAdjacent = allowPlayerSeeTarget.value,
                    )
                )
                val playersConfirmed = players.mapIndexed { index, formData ->
                    val gamePlayer: Player = when (formData.type) {
                        is RobotPlayerType1 -> {
                            RobotPlayerType1(id = index + 1, name = formData.name)
                        }
                        is RobotPlayerType2 -> {
                            RobotPlayerType2(id = index + 1, name = formData.name)
                        }
                        is RobotPlayerType3 -> {
                            RobotPlayerType3(id = index + 1, name = formData.name)
                        }
                        else -> {
                            return@mapIndexed null
                        }
                    }

                    return@mapIndexed AppGamePlayer(
                        color = formData.colorPair.first,
                        trailColor = formData.colorPair.second,
                        gamePlayer = gamePlayer
                    )
                }.filterNotNull()
                intent.putExtra(GameActivity.numPlayersTag, playersConfirmed.size)
                for (playerIndex in playersConfirmed.indices) {
                    intent.putExtra(
                        GameActivity.getPlayerTagByIndex(playerIndex),
                        playersConfirmed[playerIndex]
                    )
                }

                context.startActivity(intent)
            },
        ) {
            Text(stringResource(R.string.start_game))
        }
    }
}

@Composable
fun PlayerEditor(
    player: PlayerFormData,
    getAvailableColors: (Pair<Color, Color>) -> List<Pair<Color, Color>>,
    onValueChanged: (PlayerFormData) -> Any,
) {
    val colorExpanded = remember { mutableStateOf(false) }
    val playerTypeExpanded = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .border(width = 1.dp, color = Color(0x30000000))
            .padding(all = 8.dp),
    ) {
        TextField(
            value = player.name,
            onValueChange = {
                player.name = it
                onValueChanged(player)
            },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
        )
        Box(modifier = Modifier.height(16.dp))
        Row() {
            Box(
                modifier = Modifier
                    .height(75.dp)
                    .weight(2f)
                    .background(color = Color(0xFFD1D1D1))
                    .clickable {
                        colorExpanded.value = true
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(color = player.colorPair.first)
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(color = player.colorPair.second)
                    )
                }
                DropdownMenu(
                    expanded = colorExpanded.value,
                    onDismissRequest = { colorExpanded.value = true },
                ) {
                    for (colorPair in getAvailableColors(player.colorPair)) {
                        DropdownMenuItem(onClick = {
                            player.colorPair = colorPair
                            colorExpanded.value = false
                            onValueChanged(player)
                        }) {
                            Row() {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(color = colorPair.first)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(color = colorPair.second)
                                )
                            }
                        }
                    }
                }
            }
            Box(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .weight(2f)
                    .height(75.dp)
                    .background(color = Color(0xFFD1D1D1))
                    .clickable {
                        playerTypeExpanded.value = true
                    }
            ) {
                val name = when (player.type) {
                    is RobotPlayerType1 -> {
                        RobotPlayerType1().typeName
                    }
                    is RobotPlayerType2 -> {
                        RobotPlayerType2().typeName
                    }
                    is RobotPlayerType3 -> {
                        RobotPlayerType3().typeName
                    }
                    else -> {
                        "Not implemented"
                    }
                }

                Text(
                    text = name,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
                DropdownMenu(
                    expanded = playerTypeExpanded.value,
                    onDismissRequest = { playerTypeExpanded.value = false },
                    modifier = Modifier.width(250.dp),
                ) {
                    DropdownMenuItem(onClick = {
                        player.type = RobotPlayerType1()
                        playerTypeExpanded.value = false
                        onValueChanged(player)
                    }) {
                        Text(RobotPlayerType1().typeName)
                    }
                    DropdownMenuItem(onClick = {
                        player.type = RobotPlayerType2()
                        playerTypeExpanded.value = false
                        onValueChanged(player)
                    }) {
                        Text(RobotPlayerType2().typeName)
                    }
                    DropdownMenuItem(onClick = {
                        player.type = RobotPlayerType3()
                        playerTypeExpanded.value = false
                        onValueChanged(player)
                    }) {
                        Text(RobotPlayerType3().typeName)
                    }
                }
            }
        }
    }
}