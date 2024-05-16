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
import cn.liibang.pinoko.http.appApi
import cn.liibang.pinoko.http.GetCodeDTO
import cn.liibang.pinoko.ui.support.generateUUID
import cn.liibang.pinoko.ui.support.showToast
import kotlinx.coroutines.launch
import java.util.regex.Pattern

data class RegisterDTO(
    val nickname: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val code: String = "",
    val requestUUID: String = generateUUID(),
)

@Composable
fun RegisterScreen(
    loginDTO: LoginDTO,
    changeSignType: () -> Unit,
    getCodeDuration: Int,
    updateGetCodeDuration: (Int) -> Unit,
    updateLoginDTO: (LoginDTO) -> Unit
) {

    var registerDTO by remember {
        mutableStateOf(RegisterDTO())
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
            text = "创建账号",
            fontSize = 40.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 30.dp)
        )
        var isValidNickname by remember { mutableStateOf(true) }
        TextField(
            value = registerDTO.nickname,
            onValueChange = {
                if (registerDTO.nickname.length <= 30) {
                    registerDTO = registerDTO.copy(nickname = it)
                }
                isValidNickname = it.isNotEmpty()
            },
            label = { Text(text = "昵称") },
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
        if (!isValidNickname) {
            Text(
                text = "昵称不能为空",
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
        Spacer(modifier = Modifier.height(15.dp))

        var isValidEmail by remember { mutableStateOf(true) }
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"

        TextField(
            value = registerDTO.email,
            onValueChange = {
                registerDTO = registerDTO.copy(email = it)
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
            modifier = Modifier.fillMaxWidth()
//            horizontalArrangement = Arrangement.End
        ) {
            OutlinedTextField(
                value = registerDTO.code,
                onValueChange = { if (it.length <= 4) registerDTO = registerDTO.copy(code = it) },
                label = { Text(text = "验证码") },
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    scope.launch {
                        val isSuccessful = appApi
                            .getCode(
                                GetCodeDTO(
                                    email = registerDTO.email,
                                    requestUUID = registerDTO.requestUUID,
                                    type = 1
                                )
                            ).isSuccessful()

                        if (isSuccessful) {
                            context.showToast("发送成功，请留意您的邮箱")
                            updateGetCodeDuration(60)
                        }
                    }
                },
                enabled = Pattern.matches(emailRegex, registerDTO.email) && getCodeDuration == 0,
            ) {
                Text(text = if (getCodeDuration == 0) "获取验证码" else "${getCodeDuration}s")
            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        var isValidPassword by remember { mutableStateOf(true) }
        val passwordRegex = "^[^\\s]{6,30}$"
        TextField(
            value = registerDTO.password,
            onValueChange = {
                if (it.length <= 30) {
                    registerDTO = registerDTO.copy(password = it.trim())
                    isValidPassword = Pattern.matches(passwordRegex, it)
                }
            },
            label = { Text(text = "密码") },
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
            value = registerDTO.confirmPassword,
            onValueChange = {
                if (it.length <= 30) {
                    registerDTO = registerDTO.copy(confirmPassword = it.trim())
                }
            },
            label = { Text(text = "确认密码") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            isError = registerDTO.password != registerDTO.confirmPassword
        )
        if (registerDTO.password != registerDTO.confirmPassword) {
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
                    // 加密密码
                    val result = appApi.register(registerDTO)
                    if (result.isSuccessful()) {
                        context.showToast("注册成功")
                        updateLoginDTO(loginDTO.copy(email = registerDTO.email, password = registerDTO.password))
                        changeSignType()
                    } else {
                        context.showToast(result.message)
                    }
                }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .imePadding(),
            enabled = Pattern.matches(emailRegex, registerDTO.email) && Pattern.matches(
                passwordRegex,
                registerDTO.password
            ) && registerDTO.code.isNotBlank() && registerDTO.password == registerDTO.confirmPassword && registerDTO.nickname.isNotEmpty()
        ) {
            Text(text = "注册")
        }

        Spacer(modifier = Modifier.height(50.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "已经有了账号？",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.outline
            )
            TextButton(onClick = { changeSignType() }) {
                Text(text = "前往登入", fontSize = 13.sp)
            }

        }
    }
}