package cn.liibang.pinoko.ui.screen.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.liibang.pinoko.ui.component.XTextField

import cn.liibang.pinoko.ui.theme.XShape


data class FeedbackDTO(val contract: String, val content: String, val type: Int)

@Composable
fun FocusNoteDialog(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
    initValue: String,
) {
    if (isShow) {

        var noteValue by remember { mutableStateOf(initValue) }

        Dialog(onDismissRequest, DialogProperties(usePlatformDefaultWidth = false)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .clip(CardDefaults.shape)
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .padding(15.dp)
            ) {
                Text(text = "专注笔记", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(5.dp))

                XTextField(
                    value = noteValue,
                    onValueChange = { noteValue = it },
                    placeholder = {
                        Text(
                            text = "记录你的想法...",
                            color = MaterialTheme.colorScheme.outline
                        )
                    },
                    minLines = 5,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.outline
                            .copy(0.1f),
                        focusedContainerColor = MaterialTheme.colorScheme.outline
                            .copy(0.1f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    shape = XShape.Card,
                    paddingValues = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // 操作区域
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        onDismissRequest()
                    }) {
                        Text(text = "取消")
                    }
                    TextButton(onClick = {
                        onConfirm(noteValue)
                        onDismissRequest()
                    }) {
                        Text(text = "保存")
                    }
                }
            }
        }
    }
}