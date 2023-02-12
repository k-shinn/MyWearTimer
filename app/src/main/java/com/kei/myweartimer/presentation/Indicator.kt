package com.kei.myweartimer.presentation

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.runtime.collectAsState
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
import androidx.wear.compose.material.ProgressIndicatorDefaults
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration.Companion.milliseconds

const val REPEAT_COUNT_MILL_SEC = REPEAT_COUNT_SEC.toFloat() * 1000

@Composable
fun IndicatorAppOnlyDifferenceCalculation(
    onClickStart: () -> Unit,
    onClickStop: () -> Unit,
    timerActivationState: Flow<Boolean>,
    startTime: Flow<Long>
) {
    val isActive = timerActivationState.collectAsState(initial = false)
    val startState = startTime.collectAsState(initial = 0)

    var loopCountState by remember { mutableStateOf(0) }
    var currentTimerState by remember { mutableStateOf(0f) }

    if (isActive.value) {
        LaunchedEffect(Unit) {
            while (isActive.value) {
                delay(1000.milliseconds / 15)
                val progressTime = System.currentTimeMillis() - startState.value
                loopCountState = (progressTime / REPEAT_COUNT_MILL_SEC).toInt()
                currentTimerState = progressTime % REPEAT_COUNT_MILL_SEC / 1000
            }
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = currentTimerState,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    Scaffold(timeText = { TimeText() }) {
        Box(modifier = Modifier.fillMaxSize()) {
            Indicator(progress = animatedProgress)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoopCountText(countText = loopCountState.toString())
                CurrentUnitTimeText(countText = "%.2f".format(currentTimerState))
                StartButton(isActive.value) {
                    if (isActive.value) {
                        onClickStop.invoke()
                    } else {
                        onClickStart.invoke()
                    }
                }
            }
        }
    }
}

@Composable
fun Indicator(progress: Float, timeUnit: Float = REPEAT_COUNT_SEC.toFloat()) {
    CircularProgressIndicator(
        progress = (progress / timeUnit),
        modifier = Modifier.fillMaxSize(),
        startAngle = 290f,
        endAngle = 250f,
        strokeWidth = 10.dp
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

@Composable
fun LoopCountText(countText: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = countText,
        style = MaterialTheme.typography.title3
    )
}

@Composable
fun CurrentUnitTimeText(countText: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = countText,
        style = MaterialTheme.typography.title1
    )
}
