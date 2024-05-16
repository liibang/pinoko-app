package cn.liibang.pinoko

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import cn.liibang.pinoko.data.AppDatabase
import cn.liibang.pinoko.ui.screen.sign.SignScreen
import cn.liibang.pinoko.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import javax.inject.Inject

val SignCoroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
    println("操作异常，稍后重试\n")
}

@AndroidEntryPoint
class SignActivity: ComponentActivity() {

    @Inject
    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContent {
            val surface = MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp).toArgb()
            window.statusBarColor = surface
            AppTheme {
                SignScreen(database)
            }
        }
    }
}
