package cn.liibang.pinoko.ui.screen.sign

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import cn.liibang.pinoko.http.GetCodeDTO
import cn.liibang.pinoko.http.appApi
import cn.liibang.pinoko.ui.support.generateUUID
import cn.liibang.pinoko.ui.support.showToast
import kotlinx.coroutines.launch
import java.util.regex.Pattern

data class ForgetPasswordDTO(
    val email: String = "",
    val code: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val requestCodeUUID: String = generateUUID()
)

@Composable
fun ForgetPasswordScreen(
    backToLoginPage: () -> Unit, getCodeDuration: Int,
    updateGetCodeDuration: (Int) -> Unit,
) {
    var forgetPasswordDTO by remember {
        mutableStateOf(ForgetPasswordDTO())
    }

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "找回密码",
            fontSize = 40.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        var isValidEmail by remember { mutableStateOf(true) }
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        TextField(
            value = forgetPasswordDTO.email,
            onValueChange = {
                forgetPasswordDTO = forgetPasswordDTO.copy(email = it)
                isValidEmail = Pattern.matches(emailRegex, it)
            },
            maxLines = 1,
            label = { Text(text = "邮箱") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            isError = !isValidEmail
        )
        if (!isValidEmail) {
            Text(
                text = "请输入正确的邮箱",
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(0.6f))
            OutlinedTextField(
                value = forgetPasswordDTO.code,
                onValueChange = {
                    if (it.length <= 4) forgetPasswordDTO = forgetPasswordDTO.copy(code = it)
                },
                label = { Text(text = "验证码") }
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    scope.launch {
                        val isSuccessful = appApi
                            .getCode(
                                GetCodeDTO(
                                    email = forgetPasswordDTO.email,
                                    requestUUID = forgetPasswordDTO.requestCodeUUID,
                                    type = 2
                                )
                            ).isSuccessful()

                        if (isSuccessful) {
                            context.showToast("发送成功，请留意您的邮箱")
                            updateGetCodeDuration(60)
                        }
                    }
                },
                enabled = Pattern.matches(
                    emailRegex,
                    forgetPasswordDTO.email
                ) && getCodeDuration == 0
            ) {
                Text(text = if (getCodeDuration == 0) "获取验证码" else "${getCodeDuration}s")
            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        var isValidPassword by remember { mutableStateOf(true) }
        val passwordRegex = "^[^\\s]{6,30}$"
        TextField(
            value = forgetPasswordDTO.newPassword,
            onValueChange = {
                if (it.length <= 30) {
                    forgetPasswordDTO = forgetPasswordDTO.copy(newPassword = it.trim())
                    isValidPassword = Pattern.matches(passwordRegex, it)
                }
            },
            label = { Text(text = "新密码") },
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
            value = forgetPasswordDTO.confirmPassword,
            onValueChange = {
                if (it.length <= 30) {
                    forgetPasswordDTO = forgetPasswordDTO.copy(confirmPassword = it.trim())
                }
            },
            label = { Text(text = "确认密码") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            isError = forgetPasswordDTO.newPassword != forgetPasswordDTO.confirmPassword
        )
        if (forgetPasswordDTO.newPassword != forgetPasswordDTO.confirmPassword) {
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

                    val result = appApi.forgetThenChangePassword(forgetPasswordDTO)
                    if (result.isSuccessful()) {
                        context.showToast("修改成功")
                        backToLoginPage()
                    } else {
                        context.showToast(result.message)
                    }
                }

            }, shape = RoundedCornerShape(10.dp), modifier = Modifier
                .fillMaxWidth()
                .imePadding(),
            enabled = Pattern.matches(emailRegex, forgetPasswordDTO.email)
                    && Pattern.matches(passwordRegex, forgetPasswordDTO.newPassword)
                    && forgetPasswordDTO.code.isNotBlank()
                    && forgetPasswordDTO.newPassword == forgetPasswordDTO.confirmPassword
        ) {
            Text(text = "修改")
        }

        Spacer(modifier = Modifier.height(20.dp))
        TextButton(onClick = { backToLoginPage() }) {
            Text(text = "返回登录页面")
        }
    }
}