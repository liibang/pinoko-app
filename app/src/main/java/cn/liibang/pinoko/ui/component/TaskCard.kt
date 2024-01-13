package cn.liibang.pinoko.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.model.TaskVO
import cn.liibang.pinoko.ui.constant.priorityColor
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.screen.main.Router
import cn.liibang.pinoko.ui.support.Border
import cn.liibang.pinoko.ui.support.border
import cn.liibang.pinoko.ui.theme.categoryColor
import kotlin.reflect.KFunction1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: TaskVO,
    onCheckedChange: (String, Boolean) -> Unit,
    isShowCategoryColor: Boolean = false,
    bottomPadding: Dp = 13.dp,
    shapeSize: Dp = 10.dp,
    shadowElevation: Dp = 2.5.dp,
    categoryColorBorderWidth: Dp = 0.dp,
    deleteTask: (StringUUID) -> Unit,
) {

    var isShowMenu by remember {
        mutableStateOf(false)
    }

    val isShowSecondRow = task.dueDate != null

    val navController = LocalNavController.current

    Surface(
        modifier = Modifier
            .padding(bottom = bottomPadding)
//            .background(MaterialTheme.colorScheme.surfaceDim)
//            .clip(RoundedCornerShape(shapeSize))
            ,
        shape = RoundedCornerShape(shapeSize),
        shadowElevation = shadowElevation,
    ) {
        Row(
            modifier = Modifier
                .clickable() { navController.navigate(Router.TaskForm.route + "?id=" + task.id) }
                .background(MaterialTheme.colorScheme.surfaceDim)
                .border(
                    start = Border(
                        strokeWidth = if (isShowCategoryColor) categoryColorBorderWidth else 0.dp,
                        color = categoryColor(task.categoryColor)
                    )
                )
                .padding(vertical = if (isShowSecondRow) (7.5).dp else 5.dp)
                .padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                Checkbox(
                    checked = task.completed,
                    onCheckedChange = { onCheckedChange(task.id, it) },
                    modifier = Modifier
                        .scale(0.9f)
                        .padding(top = 3.dp)
                        .align(if (isShowSecondRow) Alignment.Top else Alignment.CenterVertically),
                    colors = CheckboxDefaults.colors(uncheckedColor = MaterialTheme.colorScheme.outline)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(
                    text = task.name,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (isShowSecondRow) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 5.dp)
                    ) {
                        Text(
                            text = task.dueDate.toString() + " " + (task.dueTime ?: ""),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        if (task.remindTime != null) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.scale(0.6f)
                            )
                        }
                    }
                }
            }
            // 是否有优先级
            if (task.priority != 0) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    modifier = Modifier.scale(0.7f),
                    tint = priorityColor(task.priority)
                )
            }
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                IconButton(onClick = { isShowMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.outline
                    )
                    DropdownMenu(
                        expanded = isShowMenu,
                        onDismissRequest = { isShowMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (task.completed) "标记未完成" else "标记完成",
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            onClick = { onCheckedChange(task.id, !task.completed) },
                        )
                        DropdownMenuItem(
                            text = { Text("开始专注", fontWeight = FontWeight.SemiBold) },
                            onClick = { },
                        )
                        DropdownMenuItem(
                            text = { Text("编辑", fontWeight = FontWeight.SemiBold) },
                            onClick = { navController.navigate(Router.TaskForm.route + "?id=" + task.id) },
                        )
                        DropdownMenuItem(
                            text = { Text("删除", fontWeight = FontWeight.SemiBold) },
                            onClick = { deleteTask(task.id) }
                        )
                    }
                }
            }

        }


    }


}