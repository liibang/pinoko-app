package cn.liibang.pinoko.ui.screen.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.LocalMinimumTouchTargetEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import cn.liibang.pinoko.model.TaskVO
import cn.liibang.pinoko.ui.screen.task.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusSelectTaskDialog(
    isShow: Boolean,
    isSelected: Boolean,
    onDismissRequest: () -> Unit,
    onSelect: (FocusTaskVO?) -> Unit,
    todoTaskViewModel: TaskViewModel = hiltViewModel()
) {
    if (isShow) {

        var todayTasks by remember {
            mutableStateOf<List<TaskVO>>(listOf())
        }


        LaunchedEffect(Unit) {
            todayTasks = todoTaskViewModel.fetchList()
        }

        Dialog(onDismissRequest, DialogProperties(usePlatformDefaultWidth = false)) {
            Column(
                Modifier
//                    .fillMaxWidth()
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .padding(horizontal = 30.dp)
                    .clip(CardDefaults.shape)
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .padding(15.dp)
            ) {
                Row(verticalAlignment = CenterVertically) {
                    Text(text = "选择关联事件", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    if (isSelected) {
                        CompositionLocalProvider(
                            LocalMinimumInteractiveComponentEnforcement provides false,
                        ) {
                            TextButton(onClick = {
                                onSelect(null)
                                onDismissRequest()
                            }) {
                                Text(
                                    text = "取消关联",
                                    color = MaterialTheme.colorScheme.primary.copy(0.9f)
                                )
                            }
                        }
                    }
                }
                LazyColumn(Modifier.padding(top = 10.dp)) {
                    items(todayTasks) {
                        CompositionLocalProvider(
                            LocalMinimumInteractiveComponentEnforcement provides false,
                        ) {
                            TextButton(
                                onClick = {
                                    onSelect(FocusTaskVO(id = it.id, name = it.name))
                                    onDismissRequest()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                border = null,
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = it.name, color = Color.Black.copy(0.9f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}