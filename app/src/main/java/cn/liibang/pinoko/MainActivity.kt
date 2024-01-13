package cn.liibang.pinoko


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope

import cn.liibang.pinoko.data.AppDatabase
import cn.liibang.pinoko.ui.screen.main.BestScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    sealed class Router(val route: String) {
        object Login : Router("login")
        object Main : Router("main")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("StateFlowValueCalledInComposition", "CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化数据库
        val database = AppDatabase.init(context = this.applicationContext)
        setContent {
            val scope = rememberCoroutineScope()
            scope.launch {
                database.taskDao().run {
                    selectByDueDate(LocalDate.now().toTimestamp())
                }
            }
            BestScreen()
        }
    }
}




