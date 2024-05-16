package cn.liibang.pinoko.ui.screen.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.liibang.pinoko.data.DataStore
import cn.liibang.pinoko.data.entity.SettingPO
import java.time.Duration
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@Composable
fun FocusSettingDialog(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    onSave: (Long, Long) -> Unit,
    setting: SettingPO
) {

    if (isShow) {
        var focusSliderPosition by remember { mutableFloatStateOf(setting.focusTomatoDuration.toMinutes().toFloat()) }
        var restSliderPosition by remember { mutableFloatStateOf(setting.focusRestDuration.toMinutes().toFloat()) }

        Dialog(onDismissRequest, DialogProperties(usePlatformDefaultWidth = false)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .clip(CardDefaults.shape)
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .padding(15.dp)
            ) {
                Text(text = "番茄专注设置", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Column {
                    Text(text = "番茄专注时长：${focusSliderPosition.roundToInt()}分钟")
                    Slider(
                        value = focusSliderPosition,
                        valueRange = (5f..120f),
                        onValueChange = { focusSliderPosition = it },
                        steps = 120,
                    )
                }
                Column {
                    Text(text = "休息时长：${restSliderPosition.roundToInt()}分钟")
                    Slider(
                        value = restSliderPosition,
                        valueRange = (2f..30f),
                        onValueChange = { restSliderPosition = it },
                        steps = 30,
                    )
                }
                Text(text = "科学建议：专注时长25分钟，休息时长5分钟", fontSize = 13.sp, color = Color.LightGray, modifier = Modifier.padding(vertical = 5.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = CenterVertically) {
                    TextButton(onClick = { onDismissRequest() }) {
                        Text(
                            text = "取消",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    TextButton(onClick = {
                        onSave(focusSliderPosition.roundToLong(), restSliderPosition.roundToLong())
                        onDismissRequest()
                        // doSave
                    }) {
                        Text(
                            text = "保存",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}