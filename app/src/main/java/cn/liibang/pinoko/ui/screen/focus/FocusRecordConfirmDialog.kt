package cn.liibang.pinoko.ui.screen.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun FocusRecordConfirmDialog(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onAbandon: () -> Unit,
    isSaveOk: Boolean
) {
    if (isShow) {
        Dialog(onDismissRequest, DialogProperties(usePlatformDefaultWidth = false)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .clip(CardDefaults.shape)
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .padding(15.dp)
            ) {
                if (isSaveOk) {
                    Text(text = "结束番茄专注", fontWeight = FontWeight.Bold)
                    Text(text = "当前番茄尚未结束，确定要提前结束并保存专注记录吗？")
                    // 操作区域
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = {
                            onAbandon()
                            onDismissRequest()
                        }) {
                            Text(text = "放弃")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            onDismissRequest()
                        }) {
                            Text(text = "取消")
                        }
                        TextButton(onClick = {
                            onConfirm()
                            onDismissRequest()
                        }) {
                            Text(text = "结束并保存")
                        }
                    }
                } else {
                    Text(text = "提前放弃专注", fontWeight = FontWeight.Bold)
                    Text(text = "本次专注不足5分钟，记录将不会被保存")
                    // 操作区域
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            onDismissRequest()
                        }) {
                            Text(text = "取消")
                        }
                        TextButton(onClick = {
                            onAbandon()
                            onDismissRequest()
                        }) {
                            Text(text = "放弃")
                        }
                    }
                }
            }
        }
    }
}