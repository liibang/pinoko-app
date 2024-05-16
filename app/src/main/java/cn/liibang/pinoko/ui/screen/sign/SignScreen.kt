package cn.liibang.pinoko.ui.screen.sign

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cn.liibang.pinoko.data.AppDatabase
import kotlinx.coroutines.delay

enum class SignType {
    LOGIN, REGISTER, FORGET_PASSWORD
}

@Composable
fun SignScreen(database: AppDatabase) {
    var signType by remember {
        mutableStateOf(SignType.LOGIN)
    }
    var getCodeDuration by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(getCodeDuration) {
        while (true) {
            delay(1000)
            if (getCodeDuration > 0) {
                getCodeDuration -= 1
            }
        }
    }

    var loginDTO by remember {
        mutableStateOf(LoginDTO())
    }

    val activity = (LocalContext.current as? Activity)
    DisposableEffect(activity) {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        onDispose {
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 创建一个登录屏幕动画
        AnimatedVisibility(
            visible = signType == SignType.LOGIN,
            enter = expandVertically(animationSpec = tween(durationMillis = 1000)) +
                    fadeIn(animationSpec = tween(durationMillis = 1000)),
            exit = shrinkVertically(animationSpec = tween(durationMillis = 1000)) +
                    fadeOut(animationSpec = tween(durationMillis = 1000)),
            // EnterTransition 和 ExitTransition 已根据建议进行微调
        ) {
            LoginScreen(
                loginDTO = loginDTO,
                updateLoginDTO = { loginDTO = it },
                changeSignType = { signType = SignType.REGISTER },
                changeSignTypeToForgetPWD = {
                    signType = SignType.FORGET_PASSWORD
                },
                database = database
            )
        }

        // 创建一个注册屏幕动画
        AnimatedVisibility(
            visible = signType == SignType.REGISTER,
            enter = expandVertically(animationSpec = tween(durationMillis = 1000)) +
                    fadeIn(animationSpec = tween(durationMillis = 1000)),
            exit = shrinkVertically(animationSpec = tween(durationMillis = 1000)) +
                    fadeOut(animationSpec = tween(durationMillis = 1000)),
            // EnterTransition 和 ExitTransition 已根据建议进行微调
        ) {
            RegisterScreen(
                loginDTO = loginDTO,
                getCodeDuration = getCodeDuration,
                updateGetCodeDuration = { getCodeDuration = it },
                changeSignType = { signType = SignType.LOGIN }
            ) { loginDTO = it }
        }

        // 创建一个注册屏幕动画
        AnimatedVisibility(
            visible = signType == SignType.FORGET_PASSWORD,
            enter = expandVertically(animationSpec = tween(durationMillis = 1000)) +
                    fadeIn(animationSpec = tween(durationMillis = 1000)),
            exit = shrinkVertically(animationSpec = tween(durationMillis = 1000)) +
                    fadeOut(animationSpec = tween(durationMillis = 1000)),
            // EnterTransition 和 ExitTransition 已根据建议进行微调
        ) {
            ForgetPasswordScreen(
                backToLoginPage = { signType = SignType.LOGIN },
                getCodeDuration = getCodeDuration,
                updateGetCodeDuration = { getCodeDuration = it }
            )
        }
    }

}


