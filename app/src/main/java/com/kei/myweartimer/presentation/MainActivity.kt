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

    // 1.画面起動中はForegroundサービスに接続
    // 2.タイマースタートと同時にOnGoingActivity起動、タイマー起動中がわかるように。
    // 3.タイマー起動中は画面が自動Offにならない設定追加
    // 4.Loop切り替わりで音or振動で知らせる機能追加
    //   a.タイマー起動時にスケジュールを作って発行しておく、停止時には切るようにする
    //   b.なんらかの不具合でタイマーだけ残らないよう、画面起動時にタイマーがアクティブか調べて、止まっていたらスケジュールも破棄する

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
