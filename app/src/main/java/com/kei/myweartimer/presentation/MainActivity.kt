package com.kei.myweartimer.presentation

import android.os.Bundle
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


const val REPEAT_COUNT_SEC = 5

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Bind型
    // 1.画面起動中はForegroundサービスに接続
    // 2.タイマースタートと同時にOnGoingActivity起動、タイマー起動中がわかるように。
    // 3.Loop切り替わりで音or振動で知らせる機能追加
    //   a.タイマー起動時にスケジュールを作って発行しておく、停止時には切るようにする
    //   b.なんらかの不具合でタイマーだけ残らないよう、画面起動時にタイマーがアクティブか調べて、止まっていたらスケジュールも破棄する

    // Foreground型
    // 1.タイマースタートと同時にService起動、OnGoingActivity発行してForeground動作
    // 2.起動時にLoopタイミングでのスケジュール発行
    // 3.スケジュールタイミングになったら音or振動で知らせる、そのまま次のLoopスケジュール発行
    // 4.タイマー停止と同時にServiceもスケジュールも破棄
    // 5.不具合対策に画面起動時にはタイマーがアクティブか調べて、止まっていたらスケジュールもServiceも破棄する

    // ex.タイマー起動中は画面が自動Offにならない設定追加

    // WorkManager or AlarmManager
    // - 音or振動のスケジュール管理はManagerに任せる
    // - 進行中表示のみService管理が必要？
    // -> 定義可能な最小繰り返し間隔は 15 分 ダメだ…
    // 表示中だけでいいか、音は

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
}
