package cn.liibang.pinoko.ui.screen.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.liibang.pinoko.http.AppApi
import cn.liibang.pinoko.http.appApi
import cn.liibang.pinoko.ui.component.XTextField
import cn.liibang.pinoko.ui.screen.focus.FeedbackDTO
import cn.liibang.pinoko.ui.support.showToast
import cn.liibang.pinoko.ui.theme.XShape
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

@Composable
fun FeedbackScreen(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
) {
    if (isShow) {

        val ctx = LocalContext.current

        var feedbackDTO by remember {
            mutableStateOf(FeedbackDTO(contract = "", content = "", type = 1))
        }

        val scope = rememberCoroutineScope()

        Dialog(onDismissRequest, DialogProperties(usePlatformDefaultWidth = false)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .clip(CardDefaults.shape)
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .padding(15.dp)
            ) {
                Text(text = "反馈与建议", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(15.dp))
                Spacer(modifier = Modifier.height(5.dp))
                XTextField(
                    value = feedbackDTO.contract,
                    onValueChange = { feedbackDTO = feedbackDTO.copy(contract = it) },
                    label = {
                        Text(
                            text = "联系方式",
                            color = MaterialTheme.colorScheme.outline
                        )
                    },
                    placeholder = {
                        Text(
                            text = "微信/邮箱/手机号",
                            color = MaterialTheme.colorScheme.outline
                        )
                    },
                    minLines = 1,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.outline
                            .copy(0.1f),
                        focusedContainerColor = MaterialTheme.colorScheme.outline
                            .copy(0.1f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    shape = XShape.Card,
                    paddingValues = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = feedbackDTO.type == 1, onClick = { feedbackDTO = feedbackDTO.copy(type = 1) })
                    Text(text = "问题反馈")
                    Spacer(modifier = Modifier.width(10.dp))
                    RadioButton(selected = feedbackDTO.type == 2, onClick = { feedbackDTO = feedbackDTO.copy(type = 2) })
                    Text(text = "建议")
                }

                XTextField(
                    value = feedbackDTO.content,
                    onValueChange = {
                        feedbackDTO = feedbackDTO.copy(content = it)
                    },
                    placeholder = {
                        Text(
                            text = "在这里输入~",
                            color = MaterialTheme.colorScheme.outline
                        )
                    },
                    minLines = 5,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.outline
                            .copy(0.1f),
                        focusedContainerColor = MaterialTheme.colorScheme.outline
                            .copy(0.1f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    shape = XShape.Card,
                    paddingValues = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // 操作区域
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        onDismissRequest()
                    }) {
                        Text(text = "取消")
                    }
                    TextButton(onClick = {
                        scope.launch {
                            try {
                                val result = appApi.feedback(feedbackDTO = feedbackDTO)
                                if (result.isSuccessful()) {
                                    ctx.showToast("反馈成功，请您留意回复~")
                                    onDismissRequest()
                                } else {
                                    ctx.showToast(result.message)
                                }
                            } catch (ex: Exception) {
                                ctx.showToast("网络异常，请重试~")
                            }
                        }
                    }, enabled = feedbackDTO.content.isNotBlank() && feedbackDTO.contract.isNotBlank()) {
                        Text(text = "提交")
                    }
                }
            }
        }
    }
}

val exceptionHandler = CoroutineExceptionHandler { _, exception ->
    println("CoroutineExceptionHandler got $exception")
}