package cn.liibang.pinoko.ui.screen.course

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private enum class SelectMode {
    NONE, ALL, SINGLE, DOUBLE
}


@Composable
fun WeekNumberSelector(
    weekMaxCount: Int,
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
    initValue: List<Int>
) {

    if (isShow) {

        var selectedMode by remember {
            mutableStateOf(SelectMode.NONE)
        }

        var selectedWeekNumbers by remember {
            mutableStateOf(initValue)
        }

        Dialog(onDismissRequest, DialogProperties(usePlatformDefaultWidth = false)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .clip(CardDefaults.shape)
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .padding(15.dp)
            ) {
                // 标题
                Text(text = "选择上课周数", fontWeight = FontWeight.Bold)

                // 选择器
                Row(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedMode == SelectMode.ALL, onClick = {
                            if (selectedMode == SelectMode.ALL) {
                                selectedMode = SelectMode.NONE
                                selectedWeekNumbers = listOf()
                            }
                            else {
                                selectedMode = SelectMode.ALL
                                selectedWeekNumbers = (1..weekMaxCount).toList()
                            }
                        })
                        Text(text = "全选")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedMode == SelectMode.SINGLE, onClick = {
                                if (selectedMode == SelectMode.SINGLE) {
                                    selectedMode = SelectMode.NONE
                                    selectedWeekNumbers = listOf()
                                }
                                else {
                                    selectedMode = SelectMode.SINGLE
                                    selectedWeekNumbers = (1..weekMaxCount).filter { it % 2 != 0 }.toList()
                                }
                        })
                        Text(text = "单周")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedMode == SelectMode.DOUBLE, onClick = {
                            if (selectedMode == SelectMode.DOUBLE) {
                                selectedMode = SelectMode.NONE
                                selectedWeekNumbers = listOf()
                            }
                            else {
                                selectedMode = SelectMode.DOUBLE
                                selectedWeekNumbers = (1..weekMaxCount).filter { it % 2 == 0 }.toList()
                            }
                        })
                        Text(text = "双周")
                    }
                }
                // 周号数
                Column {
                    (1..weekMaxCount).chunked(5).forEachIndexed { index, rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp), // 设置垂直边距
                            horizontalArrangement = Arrangement.Start
                        ) {
                            rowItems.forEach { itemValue ->
                                val selected = itemValue in selectedWeekNumbers
                                Box(
                                    modifier = Modifier
                                        .padding(end = 10.dp)
                                        .width(55.dp)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .border(1.dp, if(selected) MaterialTheme.colorScheme.primary else Color.LightGray, RoundedCornerShape(5.dp))
                                        .background(if (selected) MaterialTheme.colorScheme.primary else Color.Unspecified)
                                        .clickable {
                                            selectedWeekNumbers =
                                                if (itemValue in selectedWeekNumbers)
                                                    selectedWeekNumbers.filter { it != itemValue }
                                                else selectedWeekNumbers
                                                    .toMutableList()
                                                    .also { it.add(itemValue) }
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = itemValue.toString(),
                                        color = if (selected) MaterialTheme.colorScheme.onPrimary else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                // 操作区域
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onDismissRequest() }) {
                        Text(text = "取消")
                    }
                    TextButton(onClick = {
                        onConfirm(selectedWeekNumbers.joinToString(separator = ","))
                        onDismissRequest()
                    }) {
                        Text(text = "确认")
                    }
                }
            }
        }
    }
}
