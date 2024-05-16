package cn.liibang.pinoko.ui.screen.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderTimeDialog(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    onClick: (LocalTime) -> Unit,
    initData: LocalTime?
) {
    if (isShow) {
        val newInitData = initData ?: LocalTime.now()
        val timeState = rememberTimePickerState(newInitData?.hour!!, newInitData.minute, true)
        Dialog(onDismissRequest = onDismissRequest) {
            Column(
                modifier = Modifier
                    // TODO 颜色不对
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
            ) {
                Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timeState, modifier = Modifier)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text(text = "取消")
                        }
                        TextButton(onClick = {
                            onClick(LocalTime.of(timeState.hour, timeState.minute))
                            onDismissRequest()
                        }) {
                            Text(text = "确定")
                        }
                    }
                }
            }
        }
    }
}