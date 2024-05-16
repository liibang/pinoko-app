package cn.liibang.pinoko.ui.screen.main

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch

@Composable
fun LogoutConfirmDialog(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    syncViewModel: SyncViewModel
) {
    if (isShow) {

        var syncIsFailed by remember {
            mutableStateOf(false)
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
                Text(text = "注销", fontWeight = FontWeight.Bold)
                if (syncIsFailed) {
                    Text(text = "同步数据失败，确定要注销吗？这将放弃未同步数据")
                } else {
                    Text(text = "确定要注销吗？")
                }
                // body...

                // 操作区域
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onDismissRequest() }) {
                        Text(text = "取消")
                    }
                    TextButton(onClick = {
                        if (syncIsFailed) {
                            onConfirm()
                            onDismissRequest()
                        } else {
                            scope.launch {
                                val isSync = syncViewModel.syncToServer()
                                if (isSync) {
                                    onConfirm()
                                    onDismissRequest()
                                } else {
                                    syncIsFailed = true
                                }
                            }
                        }
                    }) {
                        Text(text = "确认")
                    }
                }
            }
        }
    }
}