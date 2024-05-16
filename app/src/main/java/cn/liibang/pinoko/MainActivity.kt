package cn.liibang.pinoko


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.rememberCoroutineScope
import cn.liibang.pinoko.data.AppDatabase
import cn.liibang.pinoko.data.dao.SettingDao
import cn.liibang.pinoko.data.entity.SettingPO
import cn.liibang.pinoko.ui.screen.main.BestScreen
import cn.liibang.pinoko.data.entity.TaskSortMode
import cn.liibang.pinoko.data.isLogin
import cn.liibang.pinoko.http.AuthException
import cn.liibang.pinoko.ui.support.showToast
import cn.liibang.pinoko.ui.support.toTimestamp
import com.tencent.mmkv.MMKV
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // 使用 viewModels 委托并以Hilt方式注入 ViewModel
    @Inject
    lateinit var settingDao: SettingDao

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("StateFlowValueCalledInComposition", "CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            // Handle your exception here
            when (e) {
                is SocketTimeoutException -> showToast("请求超时，请检查您的网络是否正常")
                is AuthException -> {
                    showToast(e.message)
                    startActivity(Intent(this, SignActivity::class.java))
                    finish()
                }
                is IOException -> {}
                is retrofit2.HttpException -> {}
                else -> {
                    showToast("出错了~，请联系管理员")
                    e.printStackTrace()
                }
            }
            e.printStackTrace()
        }

        // 初始化MMKV
        MMKV.initialize(this)


        if (!isLogin()) {
            startActivity(Intent(this, SignActivity::class.java))
            finish()
        }

        setContent {
            val scope = rememberCoroutineScope()
            scope.launch {
                settingDao.selectOne()
//                settingDao.update()
            }
            BestScreen()
        }
    }
}


fun restartApp(context: Context) {
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    val componentName = intent!!.component
    val mainIntent = Intent.makeRestartActivityTask(componentName)
    context.startActivity(mainIntent)
    System.exit(0)
}

