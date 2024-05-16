package cn.liibang.pinoko.ui.screen.term

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import cn.liibang.pinoko.data.entity.ClassTime
import cn.liibang.pinoko.data.entity.LessonInfo
import java.time.LocalTime


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LessonTimeSettingDialog(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    lessonInfo: LessonInfo,
    onChange: (LessonInfo) -> Unit,
) {

    val cardBackgroundColor = MaterialTheme.colorScheme.surfaceDim
    
    if (isShow) {
        Popup(
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                focusable = true
            ),
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp))
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.width(3.dp))
                    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                        IconButton(onClick = onDismissRequest) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "上课时间", fontWeight = FontWeight.SemiBold, fontSize = 16.5.sp)
                }

                // ================Content==============

                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.5.dp),
                    horizontalAlignment = CenterHorizontally
                ) {

                    // ============================================
                    item {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "填写上课时间以便在日历中显示",
                                fontSize = 13.sp,
                                color = Color.Gray.copy(0.5f)
                            )
                        }
                    }

                    item {
                        var isShowLessonMaxCountPicker by remember {
                            mutableStateOf(false)
                        }
                        LessonMaxCountPicker(
                            isShow = isShowLessonMaxCountPicker,
                            onDismissRequest = { isShowLessonMaxCountPicker = false },
                            initValue = lessonInfo.maxCount,
                            onSelect = {
                                lessonInfo
                                    .copy(
                                        maxCount = it,
                                        classTimes = when {
                                            it < lessonInfo.classTimes.size -> lessonInfo.classTimes.take(
                                                it
                                            )

                                            it > lessonInfo.classTimes.size -> lessonInfo.classTimes.toMutableList()
                                                .apply {
                                                    (1..(it - lessonInfo.classTimes.size)).forEach { _ ->
                                                        add(
                                                            null
                                                        )
                                                    }
                                                }

                                            else -> lessonInfo.classTimes
                                        }
                                    )
                                    .let(onChange)
                            }
                        )
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = cardBackgroundColor,
                                contentColor = Color.Black
                            ), elevation = CardDefaults.cardElevation(1.1.dp)
                        ) {
                            Row(
                                Modifier
                                    .clickable { isShowLessonMaxCountPicker = true }
                                    .padding(12.5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "课表最大节数",
                                    fontSize = 15.sp,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = "${lessonInfo.maxCount}",
                                    fontSize = 14.sp,
                                    color = Color.Gray.copy(0.5f)
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "",
                                    tint = Color.Gray.copy(0.5f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = cardBackgroundColor,
                                contentColor = Color.Black
                            ), elevation = CardDefaults.cardElevation(1.1.dp)
                        ) {
                            lessonInfo.classTimes.forEachIndexed { index, it ->
                                var isShowPicker by remember {
                                    mutableStateOf(false)
                                }
                                Row(
                                    Modifier
                                        .clickable { isShowPicker = true }
                                        .padding(12.5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "第 ${index + 1} 节",
                                        fontSize = 15.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (it != null) {
                                        Text(text = "${it.startAt} - ${it.endAt}", color = Color.Gray.copy(0.7f), fontSize = 15.sp)
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = "",
                                            tint = Color.Gray.copy(0.5f)
                                        )
                                    }
                                }
                                // Picker弹窗
                                LessonTimeRangePicker(
                                    isShow = isShowPicker,
                                    onDismissRequest = { isShowPicker = false },
                                    onSelect = {
                                        lessonInfo.classTimes.toMutableList().let { list ->
                                            list[index] = it
                                            onChange(lessonInfo.copy(classTimes = list))
                                        }
                                    },
                                    onClear = {
                                        lessonInfo.classTimes.toMutableList().let { list ->
                                            list[index] = null
                                            onChange(lessonInfo.copy(classTimes = list))
                                        }
                                    },
                                    initValue = lessonInfo.classTimes.last()?.run {
                                        val minuteOfDay =
                                            (endAt.toSecondOfDay() - startAt.toSecondOfDay()) / 60
                                        copy(
                                            startAt = endAt.plusMinutes(10),
                                            endAt = endAt.plusMinutes(minuteOfDay.toLong())
                                        )
                                    } ?: LocalTime.of(8, 0)
                                        .plusMinutes(index.toLong() * (45 + 10)).let {
                                            ClassTime(
                                                startAt = it,
                                                endAt = it.plusMinutes(45)
                                            )
                                        },
                                    // initValue = LessonTime(startAt =, endAt =),
                                    title = "第${index + 1}节",
                                )
                            }
                        }
                    }

                    item {
                        // ========================
                        var isShowClearLessonTimesDialog by remember {
                            mutableStateOf(false)
                        }
                        ClearLessonTimesDialog(
                            isShow = isShowClearLessonTimesDialog,
                            onDismissRequest = { isShowClearLessonTimesDialog = false },
                            onConfirm = { onChange(lessonInfo.copy(classTimes = lessonInfo.classTimes.map { null })) }
                        )
                        TextButton(onClick = { isShowClearLessonTimesDialog = true }, Modifier) {
                            Text(text = "清除", color = Color.Gray, modifier = Modifier.scale(1.1f))
                        }
                    }
                    // ===========END===========
                }

            }
        }
    }
}


@Composable
fun ClearLessonTimesDialog(isShow: Boolean, onDismissRequest: () -> Unit, onConfirm: () -> Unit) {
    if (isShow) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = "清空上课时间", fontWeight = FontWeight.SemiBold) },
            text = { Text(text = "将清空所有上下课时间，本课表的时间详情不会显示在日历中") },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "取消", color = MaterialTheme.colorScheme.primary.copy(0.66f))
                }
            },
            confirmButton = {
                TextButton(onClick = { onConfirm(); onDismissRequest() }) {
                    Text(text = "清除", color = MaterialTheme.colorScheme.error)
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceDim
        )
    }
}


