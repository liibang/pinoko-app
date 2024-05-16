package cn.liibang.pinoko.ui.screen.member

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.hutool.crypto.digest.BCrypt
import cn.liibang.pinoko.http.appApi
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.support.showToast
import kotlinx.coroutines.launch
import java.util.regex.Pattern


data class ChangePasswordDTO(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = ""
)

@Composable
fun ChangePasswordScreen() {

    var changePasswordDTO by remember {
        mutableStateOf(ChangePasswordDTO())
    }

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val navController = LocalNavController.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp
        )
    ) {

        Text(
            text = "修改密码",
            fontSize = 40.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        var isValidPassword by remember { mutableStateOf(true) }
        var isValidNewPassword by remember { mutableStateOf(true) }
        val passwordRegex = "^[^\\s]{6,30}$"
        TextField(
            value = changePasswordDTO.oldPassword,
            onValueChange = {
                if (it.length <= 30) {
                    changePasswordDTO = changePasswordDTO.copy(oldPassword = it.trim())
                    isValidPassword = Pattern.matches(passwordRegex, it)
                }
            },
            label = { Text(text = "旧密码") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            isError = !isValidPassword
        )
        if (!isValidPassword) {
            Text(
                text = "密码长度至少6位",
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }


        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = changePasswordDTO.newPassword,
            onValueChange = {
                if (it.length <= 30) {
                    changePasswordDTO = changePasswordDTO.copy(newPassword = it.trim())
                    isValidNewPassword = Pattern.matches(passwordRegex, it)
                }
            },
            label = { Text(text = "新密码") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            isError = !isValidNewPassword
        )
        if (!isValidNewPassword) {
            Text(
                text = "密码长度至少6位",
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))
        TextField(
            value = changePasswordDTO.confirmPassword,
            onValueChange = {
                if (it.length <= 30) {
                    changePasswordDTO = changePasswordDTO.copy(confirmPassword = it.trim())
                }
            },
            label = { Text(text = "确认密码") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            isError = changePasswordDTO.newPassword != changePasswordDTO.confirmPassword
        )
        if (changePasswordDTO.newPassword != changePasswordDTO.confirmPassword) {
            Text(
                text = "两次输入密码不一致",
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val result = appApi.changePassword(changePasswordDTO)
                        if (result.isSuccessful()) {
                            context.showToast("修改成功")
                            navController.popBackStack()
                        } else {
                            context.showToast(result.message)
                        }
                    } catch (ex: Exception) {
                        context.showToast("网络异常，请重试~")
                    }
                }

            }, shape = RoundedCornerShape(10.dp), modifier = Modifier
                .fillMaxWidth()
                .imePadding(),
            enabled = Pattern.matches(passwordRegex, changePasswordDTO.newPassword)
                    && Pattern.matches(passwordRegex, changePasswordDTO.oldPassword)
                    && changePasswordDTO.newPassword == changePasswordDTO.confirmPassword
        ) {
            Text(text = "修改")
        }
    }
}