package com.kei.myweartimer.presentation

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.kei.myweartimer.data.DataStore
import com.kei.myweartimer.presentation.theme.MyWearTimerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


const val REPEAT_COUNT_SEC = 45

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
                    startTime = dataStore.startTime,
                    onLoopCountUp = { vibrate() }
                )
            }
        }

        // タイマー起動中はScreenOn
        lifecycleScope.launch {
            dataStore.isTimerRunning.collect {
                if (it) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        val effect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator?.vibrate(effect)
    }
}
