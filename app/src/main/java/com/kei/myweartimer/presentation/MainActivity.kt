package com.kei.myweartimer.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.kei.myweartimer.data.DataStore
import com.kei.myweartimer.presentation.theme.MyWearTimerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: DataStore

    @Inject
    lateinit var timerUseCase: TimerUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val composableScope = rememberCoroutineScope()
            MyWearTimerTheme {
                IndicatorAppOnlyDifferenceCalculation(
                    onClickStart = {
                        composableScope.launch {
                            timerUseCase.startTimer()
                        }
                    },
                    onClickStop = {
                        composableScope.launch {
                            timerUseCase.stopTimer()
                        }
                    },
                    timerActivationState = dataStore.isTimerRunning,
                    startTime = dataStore.startTime
                )
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    // todo
}
