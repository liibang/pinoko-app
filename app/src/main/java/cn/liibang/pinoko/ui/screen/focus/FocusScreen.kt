package cn.liibang.pinoko.ui.screen.focus

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.liibang.pinoko.R
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.ui.screen.task.TaskViewModel
import cn.liibang.pinoko.data.entity.SettingPO
import cn.liibang.pinoko.ui.screen.setting.SettingViewModel
import cn.liibang.pinoko.ui.support.showToast
import java.time.Duration
import java.time.LocalDateTime
import kotlin.reflect.KFunction1

enum class PlayState {
    PROCEED, PAUSE, STOP, FINISH, REST
}

private val healthColor = Color(12, 206, 156)

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun FocusScreen(
    focusViewModel: FocusViewModel = hiltViewModel(),
    taskID: StringUUID?,
    taskViewModel: TaskViewModel,
    settingViewModel: SettingViewModel,
) {

    val todayFocusData by focusViewModel.todayFocusData.collectAsState()
    val setting by settingViewModel.setting.collectAsState()

    // 5-120

    val usedDuration = focusViewModel.usedDuration
    val targetDuration = focusViewModel.targetDuration
    val playState = focusViewModel.playState
    val focusRecordState = focusViewModel.focusRecordState
    val task = focusViewModel._task

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        taskID?.let {
            val taskPO = taskViewModel.detail(it)
            if (taskPO != null) {
                focusViewModel.updateTask(FocusTaskVO(taskPO.id, taskPO.name))
            } else {
                focusViewModel.updateTask(null)
                focusViewModel.updateFocusRecordState(focusRecordState.copy(taskId = null))
            }
        }
    }

    // ==============body==============
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp))
            .padding(10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 头部
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Text(
                text = "番茄专注",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )

            // 设置弹窗
            var isShowFocusSettingDialog by remember { mutableStateOf(false) }
            FocusSettingDialog(
                isShow = isShowFocusSettingDialog,
                onDismissRequest = { isShowFocusSettingDialog = false },
                onSave = { tomatoMin, restMin ->
                    settingViewModel.saveOrUpdate(
                        setting.copy(
                            focusTomatoDuration = Duration.ofMinutes(tomatoMin),
                            focusRestDuration = Duration.ofMinutes(restMin)
                        )
                    )
                    focusViewModel.updateDurationSetting(
                        Duration.ofMinutes(tomatoMin),
                        Duration.ofMinutes(restMin)
                    )
                },
                setting = setting
            )
            IconButton(onClick = {
                if (playState != PlayState.STOP) {
                    context.showToast("您正在专注当中，无法修改配置")
                } else {
                    isShowFocusSettingDialog = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "setting",
                    tint = Color.Gray
                )
            }
        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(modifier = Modifier.height(80.dp))

            // 选择事件

            var isShowFocusSelectTaskDialog by remember { mutableStateOf(false) }
            FocusSelectTaskDialog(
                isShow = isShowFocusSelectTaskDialog,
                onDismissRequest = { isShowFocusSelectTaskDialog = false },
                onSelect = {
                    focusViewModel.updateTask(it)
                    focusViewModel.updateFocusRecordState(focusRecordState.copy(taskId = it?.id))
                },
                isSelected = focusRecordState.taskId != null,
            )

            if (playState != PlayState.FINISH) {
                TextButton(
                    onClick = { isShowFocusSelectTaskDialog = true },
                    modifier = Modifier.padding(bottom = 30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary.copy(0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (task == null) {
                        Text(text = "轻触选择专注事件")
                    } else {
                        Text(text = task.name)
                    }
                }
            }

            if (playState == PlayState.FINISH) {
                Column(
                    modifier = Modifier.wrapContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tomato),
                        contentDescription = "",
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = "你刚收获了1个番茄",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                }
            } else {
                val circleStroke = 5.5.dp
                val circleSize = 300.dp
                val primaryColor = MaterialTheme.colorScheme.primary
                val lowPrimaryColor = Color.Gray.copy(0.1f)
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(circleSize)) {
                        // Draw the full circle in the secondary color
                        drawCircle(
                            color = lowPrimaryColor,
                            style = Stroke(width = circleStroke.toPx())
                        )
                        // Draw a part of the circle in the primary color
                        drawArc(
                            color = if (playState == PlayState.REST) healthColor else primaryColor,
                            startAngle = -90f,
                            sweepAngle = (360f * usedDuration.toSeconds() / targetDuration.toSeconds()),
                            useCenter = false,
                            style = Stroke(width = circleStroke.toPx())
                        )
                    }
                    // 计算出当前的时间差
                    val downTime = targetDuration.minus(usedDuration)
                    Text(
                        text = "${downTime.toMinutesPart()}:${
                            String.format(
                                "%02d",
                                downTime.toSecondsPart()
                            )
                        }",
                        style = TextStyle(fontSize = 40.sp, color = Color.Gray),
                        modifier = Modifier.align(Alignment.Center),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (playState == PlayState.REST) {
                Text(
                    text = "扭扭脖子，走动一下",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
            Column(
                Modifier.padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (playState) {
                    PlayState.STOP -> {
                        ElevatedButton(
                            onClick = {
                                focusViewModel.changePlayState(playState = PlayState.PROCEED)
                                // 开始后记录 开始时间
                                focusViewModel.updateFocusRecordState(focusRecordState.copy(startAt = LocalDateTime.now()))
                            },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(0.9f),
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = "开始专注", fontSize = 13.5.sp)
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }

                    PlayState.PROCEED -> {
                        androidx.compose.material3.OutlinedButton(
                            onClick = { focusViewModel.changePlayState(PlayState.PAUSE) },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Text(text = "暂停", fontSize = 13.5.sp)
                        }
                    }

                    PlayState.PAUSE -> {
                        Row {
                            ElevatedButton(
                                onClick = { focusViewModel.changePlayState(PlayState.PROCEED) },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(0.9f),
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(text = "继续", fontSize = 13.5.sp)
                            }
                            Spacer(modifier = Modifier.width(16.5.dp))

                            // =======================Result Handler==============================
                            var isShowFocusRecordConfirmDialog by remember { mutableStateOf(false) }
                            FocusRecordConfirmDialog(
                                isSaveOk = usedDuration >= Duration.ofSeconds(5),
                                isShow = isShowFocusRecordConfirmDialog,
                                onDismissRequest = { isShowFocusRecordConfirmDialog = false },
                                onConfirm = {
                                    // 结束后 保存信息 TODO
                                    focusViewModel.changePlayState(PlayState.STOP)
                                    // 保存到数据库
                                    focusViewModel.saveRecord(
                                        focusRecordState.copy(
                                            endAt = LocalDateTime.now(),
                                            focusDuration = usedDuration
                                        ),
                                        onSave = { focusViewModel.updateFocusRecordState(it) }
                                    )
                                    focusViewModel.updateUsedDuration(Duration.ZERO)
                                    isShowFocusRecordConfirmDialog = false
                                    context.showToast("保存成功")
                                },
                                onAbandon = {
                                    focusViewModel.changePlayState(PlayState.STOP)
                                    focusViewModel.updateUsedDuration(Duration.ZERO)
                                    isShowFocusRecordConfirmDialog = false

                                    // 不管怎样 放弃都要 清除表单
                                    focusViewModel.updateFocusRecordState(
                                        focusViewModel.getNewFocusRecordPO(
                                            setting!!.focusTomatoDuration
                                        )
                                    )
                                }
                            )
                            ElevatedButton(
                                onClick = {
                                    // 当已经专注5分钟时
                                    isShowFocusRecordConfirmDialog = true
                                },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.error.copy(0.9f),
                                    contentColor = MaterialTheme.colorScheme.onError
                                )
                            ) {
                                Text(text = "结束", fontSize = 13.5.sp)
                            }
                        }
                    }

                    PlayState.FINISH -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            ElevatedButton(
                                onClick = {
                                    focusViewModel.updateUsedDuration(Duration.ZERO)
                                    focusViewModel.updateTargetDuration(setting!!.focusRestDuration)
                                    focusViewModel.changePlayState(PlayState.REST)
                                },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = healthColor,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(text = "休息一下", fontSize = 13.5.sp)
                            }
                            androidx.compose.material3.OutlinedButton(
                                onClick = {
                                    focusViewModel.updateTargetDuration(setting!!.focusTomatoDuration)
                                    focusViewModel.changePlayState(PlayState.PROCEED)
                                    // 开始后记录 开始时间
                                    focusViewModel.updateFocusRecordState(
                                        focusRecordState.copy(
                                            startAt = LocalDateTime.now()
                                        )
                                    )

                                },
                                border = BorderStroke(1.dp, healthColor)
                            ) {
                                Text(text = "跳过休息", fontSize = 13.5.sp, color = healthColor)
                            }
                            androidx.compose.material3.OutlinedButton(
                                onClick = {
                                    focusViewModel.updateUsedDuration(Duration.ZERO)
                                    focusViewModel.changePlayState(PlayState.STOP)
                                    focusViewModel.updateFocusRecordState(
                                        focusViewModel.getNewFocusRecordPO(
                                            setting!!.focusTomatoDuration
                                        )
                                    )
                                },
                                border = BorderStroke(1.dp, healthColor)
                            ) {
                                Text(text = "返回", fontSize = 13.5.sp, color = healthColor)
                            }
                        }
                    }

                    PlayState.REST -> {
                        androidx.compose.material3.OutlinedButton(
                            onClick = {
                                focusViewModel.updateUsedDuration(Duration.ZERO)
                                focusViewModel.changePlayState(PlayState.STOP)
                                focusViewModel.updateTargetDuration(setting.focusTomatoDuration)
                                focusViewModel.updateFocusRecordState(
                                    focusViewModel.getNewFocusRecordPO(
                                        setting.focusTomatoDuration
                                    )
                                )
                            },
                            border = BorderStroke(1.dp, healthColor)
                        ) {
                            Text(text = "结束休息", fontSize = 13.5.sp, color = healthColor)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (playState == PlayState.FINISH || playState == PlayState.PROCEED || playState == PlayState.PAUSE) {
                        var isShowFocusNoteDialog by remember { mutableStateOf(false) }
                        FocusNoteDialog(
                            isShow = isShowFocusNoteDialog,
                            onDismissRequest = { isShowFocusNoteDialog = false },
                            onConfirm = {
                                if (playState == PlayState.PAUSE) {
                                    focusViewModel.updateFocusRecordState(focusRecordState.copy(note = it))
                                } else {
                                    focusViewModel.updateRecord(
                                        focusRecordState.id,
                                        it,
                                        focusRecordState.taskId
                                    )
                                }
                                context.showToast("已保存")
                            },
                            initValue = focusRecordState.note
                        )
                        TextButton(onClick = {
                            focusViewModel.changePlayState(PlayState.PAUSE)
                            isShowFocusNoteDialog = true
                        }) {
                            Text(text = "记录专注笔记")
                        }
                    }
                    // TODO 查询数据
                    Text(
                        text = "今日收成: ${todayFocusData.tomatoCount} 番茄 · ${todayFocusData.totalDuration.toMinutes()}分钟",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }

    }
}


