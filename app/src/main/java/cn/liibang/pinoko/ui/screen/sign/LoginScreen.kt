package cn.liibang.pinoko.ui.screen.sign

import android.app.Activity
import android.content.Intent
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
import cn.liibang.pinoko.MainActivity
import cn.liibang.pinoko.data.AppDatabase
import cn.liibang.pinoko.data.DataStore
import cn.liibang.pinoko.http.appApi
import cn.liibang.pinoko.ui.support.showToast
import kotlinx.coroutines.launch
import java.util.regex.Pattern

data class LoginDTO(val email: String = "", val password: String = "")

@Composable
fun LoginScreen(
    changeSignType: () -> Unit,
    changeSignTypeToForgetPWD: () -> Unit,
    loginDTO: LoginDTO,
    updateLoginDTO: (LoginDTO) -> Unit,
    database: AppDatabase
) {

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
            text = "Pinoko",
            fontSize = 40.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 30.dp)
        )
        var isValidEmail by remember { mutableStateOf(true) }
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        TextField(
            value = loginDTO.email,
            onValueChange = {
                updateLoginDTO(loginDTO.copy(email = it))
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
        TextField(
            value = loginDTO.password,
            onValueChange = {
                if (it.length <= 30) {
                    updateLoginDTO(loginDTO.copy(password = it.trim()))
                }
            },
            label = { Text(text = "密码") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
        )
        TextButton(
            onClick = { changeSignTypeToForgetPWD() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "忘记密码？")
        }

        Button(
            onClick = {
                scope.launch {
                    try {
                        val result = appApi.login(loginDTO)
                        if (result.isSuccessful()) {
                            DataStore.putMember(result.data.memberDO)
                            DataStore.saveToken(result.data.token)
                            database.settingDao().insertOrUpdate(result.data.settingPO)
                            // 同步数据
                            val isSyncData = DataStore.isPullData()
                            if (true) {

                                val dataResult = appApi.fetchData()

                                // 先清除所有数据
                                database.changeLogDao().deleteAll()

                                database.taskDao().deleteAll()
                                database.taskCategoryDao().deleteAll()
                                database.focusRecordDao().deleteAll()
                                database.termDao().deleteAll()
                                database.courseDao().deleteAll()
                                database.courseDetailDao().deleteAll()
                                database.habitDao().deleteAll()

                                // 删除所有数据

                                if (dataResult.isSuccessful()) {
                                    dataResult.data.run {
                                        database.taskDao().batchInsert(tasks)
                                        database.taskCategoryDao().batchInsert(taskCategories)
                                        database.focusRecordDao().batchInsert(focusRecords)
                                        database.termDao().batchInsert(terms)
                                        database.courseDao().batchInsert(courses)
                                        database.courseDetailDao().batchInsert(courseDetails)
                                        database.habitDao().batchInsert(habits)
                                    }
                                } else {
                                    throw IllegalStateException("同步数据失败")
                                }
                                DataStore.checkPullDataStatus()
                                context.showToast("同步数据成功，欢迎回来")
                            }
//                            context.showToast("欢迎回来")
                            context.startActivity(Intent(context, MainActivity::class.java))
                            if (context is Activity) {
                                context.finish()
                            }
                        } else {
                            context.showToast(result.message)
                        }
                    } catch (ex: Exception) {
                        if (ex is IllegalStateException) {
                            context.showToast(ex.message ?: "")
                        } else {
                            context.showToast("网络异常请重试")
                        }
                    }
                }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .imePadding(),
            enabled = loginDTO.email.isNotEmpty() || loginDTO.password.isNotEmpty()  ||  !isValidEmail
        ) {
            Text(text = "登入")
        }

        Spacer(modifier = Modifier.height(50.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "还没有一个账号？",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.outline
            )
            TextButton(onClick = { changeSignType() }) {
                Text(text = "前往注册", fontSize = 13.sp)
            }
        }
    }
}


