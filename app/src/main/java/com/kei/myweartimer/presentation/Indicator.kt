package com.kei.myweartimer.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun IndicatorApp(
    onClickStart: () -> Unit,
    onClickStop: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(false) }
    var ticks by remember { mutableStateOf(0) }

    if (isPlaying) {
        LaunchedEffect(Unit) {
            ticks = 0
            while (isPlaying) {
                delay(1.seconds)
                ticks++
            }
        }
    }

    Scaffold(timeText = { TimeText() }) {
        Box(modifier = Modifier.fillMaxSize()) {
            Indicator(progress = ticks)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CurrentTimer(ticks)
                StartButton(isPlaying) {
                    if (isPlaying) {
                        onClickStop.invoke()
                    } else {
                        onClickStart.invoke()
                    }
                    isPlaying = isPlaying.not()
                }
            }
        }
    }
}

@Composable
fun Indicator(progress: Int) {
    CircularProgressIndicator(
        progress = (progress / 100f),
        modifier = Modifier.fillMaxSize(),
        startAngle = 290f,
        endAngle = 250f,
        strokeWidth = 10.dp
    )
}

@Composable
fun CurrentTimer(ticks: Int) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = ticks.toString()
    )
}

@Composable
fun StartButton(isPlaying: Boolean, onClickPlay: () -> Unit) {
    Button(
        modifier = Modifier.size(ButtonDefaults.LargeButtonSize),
        onClick = onClickPlay
    ) {
        Icon(
            imageVector = if (isPlaying) {
                Icons.Rounded.Stop
            } else {
                Icons.Rounded.PlayArrow
            },
            contentDescription = ""
        )
    }
}
