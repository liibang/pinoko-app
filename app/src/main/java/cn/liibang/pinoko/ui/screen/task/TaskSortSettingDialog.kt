package cn.liibang.pinoko.ui.screen.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.liibang.pinoko.data.entity.SettingPO
import cn.liibang.pinoko.data.entity.TaskSortMode

@Composable
fun TaskSortSettingDialog(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    updateSetting: (SettingPO) -> Unit,
    setting: SettingPO
) {
    if (isShow) {

        var taskSortMode by remember {
            mutableStateOf(setting.taskSortMode)
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
                Text(text = "设置任务排序", fontWeight = FontWeight.Bold)
                // body...
                Spacer(modifier = Modifier.height(10.dp))

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            taskSortMode = TaskSortMode.DUE_DATE_TIME
                        }) {
                        RadioButton(
                            selected = taskSortMode == TaskSortMode.DUE_DATE_TIME,
                            onClick = { taskSortMode = TaskSortMode.DUE_DATE_TIME }
                        )
                        Text(text = "按截至日期和时间")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            taskSortMode = TaskSortMode.CREATED_ON_BOTTOM
                        }) {

                        RadioButton(
                            selected = taskSortMode == TaskSortMode.CREATED_ON_BOTTOM,
                            onClick = { taskSortMode = TaskSortMode.CREATED_ON_BOTTOM }
                        )
                        Text(text = "按创建时间(最新在底部)")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            taskSortMode = TaskSortMode.CREATED_ON_TOP
                        }) {
                        RadioButton(
                            selected = taskSortMode == TaskSortMode.CREATED_ON_TOP,
                            onClick = { taskSortMode = TaskSortMode.CREATED_ON_TOP })
                        Text(text = "按创建时间(最新在顶部)")
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        updateSetting(setting.copy(taskSortMode = taskSortMode))
                        onDismissRequest()
                    }) {
                        Text(text = "选择")
                    }
                }
            }
        }
    }
}