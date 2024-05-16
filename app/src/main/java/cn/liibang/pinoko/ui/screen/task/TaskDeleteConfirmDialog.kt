package cn.liibang.pinoko.ui.screen.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
fun TaskDeleteConfirmDialog(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
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
                Text(text = "删除事件", fontWeight = FontWeight.Bold)
                // body...
                Text(text = "确定要删除该事件吗？")
                // 操作区域
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onDismissRequest() }) {
                        Text(text = "取消")
                    }
                    TextButton(onClick = {
                        onConfirm()
                        onDismissRequest()
                    }) {
                        Text(text = "确认")
                    }
                }
            }
        }
    }
}