package cn.liibang.pinoko.ui.screen.course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.liibang.pinoko.data.entity.ClassTime
import cn.liibang.pinoko.ui.component.InfiniteCircularList
import cn.liibang.pinoko.ui.theme.XShape
import java.time.LocalTime

private val times = generateSequence(
    seed = LocalTime.of(6, 0),
    nextFunction = { it.plusMinutes(5) }
).takeWhile { it != LocalTime.of(23, 55) }.toList()

@Composable
fun LessonTimeRangePicker(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    onSelect: (ClassTime) -> Unit,
    onClear: () -> Unit,
    initValue: ClassTime,
    title: String
) {
    if (isShow) {
        // ====properties====
        val textSize = 12.5
        val width = 65
        val itemHeight = 60

        var currentLessonTime by remember {
            mutableStateOf(initValue)
        }
        val lessonMinute =
            (currentLessonTime.endAt.toSecondOfDay() - currentLessonTime.startAt.toSecondOfDay()) / 60
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
                    .clip(XShape.Card)
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .padding(16.dp)
            ) {

                Text(text = title, fontWeight = FontWeight.SemiBold)

                Column(Modifier.padding(horizontal = 10.dp)) {
                    Row(Modifier.align(Alignment.CenterHorizontally)) {
                        if (lessonMinute > 0) {
                            Text(
                                text = "一节课",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = " $lessonMinute ",
                                fontSize = 13.6.sp,
                                color = MaterialTheme.colorScheme.primary.copy(0.66f),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "分钟",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        } else {
                            Text(
                                text = "结束时间不能早于开始时间",
                                fontSize = 13.sp,
                                color = Color(249, 182, 4)
                            )
                        }

                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Unspecified)
                            .padding(horizontal = 45.dp)
                    ) {
                        InfiniteCircularList(
                            width = width.dp,
                            itemHeight = itemHeight.dp,
                            items = times,
                            initialItem = currentLessonTime.startAt,
                            textStyle = TextStyle(fontSize = textSize.sp),
                            textColor = Color.LightGray,
                            selectedTextColor = Color.Black,
                            onItemSelected = { i, item ->
                                currentLessonTime = currentLessonTime.copy(startAt = item)
                            }
                        )
                        Icon(
                            imageVector = Icons.Default.HorizontalRule,
                            contentDescription = null,
                            tint = Color.Gray.copy(0.69f)
                        )
                        InfiniteCircularList(
                            width = width.dp,
                            itemHeight = itemHeight.dp,
                            items = times,
                            initialItem = currentLessonTime.endAt,
                            textStyle = TextStyle(fontSize = textSize.sp),
                            textColor = Color.LightGray,
                            selectedTextColor = Color.Black,
                            onItemSelected = { i, item ->
                                currentLessonTime = currentLessonTime.copy(endAt = item)
                            }
                        )
                    }
                }
                // =======OPT button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 9.dp),
                ) {
//                    TextButton(
//                        onClick = {
//                            onClear()
//                            onDismissRequest()
//                        },
//                    ) {
//                        Text(text = "清除", color = MaterialTheme.colorScheme.error)
//                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismissRequest) {
                        Text(text = "取消")
                    }
                    TextButton(
                        onClick = {
                            onSelect(currentLessonTime)
                            onDismissRequest()
                        },
                        enabled = lessonMinute > 0
                    ) {
                        Text(text = "保存")
                    }
                }
            }
        }
    }
}
