package cn.liibang.pinoko.ui.screen.task

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.model.TaskVO
import cn.liibang.pinoko.ui.constant.Priority
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.screen.main.SubRouter
import com.mohamedrejeb.compose.dnd.DragAndDropContainer
import com.mohamedrejeb.compose.dnd.DragAndDropState
import com.mohamedrejeb.compose.dnd.drag.DraggableItem
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import com.mohamedrejeb.compose.dnd.rememberDragAndDropState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun QuadrantList(
    tasks: List<TaskVO>,
    updateTaskPriority: (StringUUID, Int) -> Unit,
    updateCompletedStatus: (String, Boolean) -> Unit
) {

    val taskGroup = tasks.groupBy { Priority.of(it.priority) }

    val dragAndDropState = rememberDragAndDropState<TaskVO>(true)

    DragAndDropContainer(
        state = dragAndDropState,
    ) {
        Column(
            Modifier
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .fillMaxSize()
        ) {

            // 四象限布局
            Row(Modifier.weight(1f)) {
                QuadrantCard(
                    priority = Priority.HIGH,
                    taskList = taskGroup[Priority.HIGH] ?: listOf(),
                    modifier = Modifier.weight(1f),
                    dragAndDropState = dragAndDropState,
                    updateTaskPriority = updateTaskPriority,
                    updateCompletedStatus = updateCompletedStatus
                )
                Spacer(modifier = Modifier.width(10.dp))
                QuadrantCard(
                    priority = Priority.MEDIUM,
                    taskList = taskGroup[Priority.MEDIUM] ?: listOf(),
                    modifier = Modifier.weight(1f),
                    dragAndDropState = dragAndDropState,
                    updateTaskPriority = updateTaskPriority,
                    updateCompletedStatus = updateCompletedStatus,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(Modifier.weight(1f)) {
                QuadrantCard(
                    priority = Priority.LOW,
                    taskList = taskGroup[Priority.LOW] ?: listOf(),
                    modifier = Modifier.weight(1f),
                    dragAndDropState = dragAndDropState,
                    updateTaskPriority = updateTaskPriority,
                    updateCompletedStatus = updateCompletedStatus,
                )
                Spacer(modifier = Modifier.width(10.dp))
                QuadrantCard(
                    priority = Priority.NONE,
                    taskList = taskGroup[Priority.NONE] ?: listOf(),
                    modifier = Modifier.weight(1f),
                    dragAndDropState = dragAndDropState,
                    updateTaskPriority = updateTaskPriority,
                    updateCompletedStatus = updateCompletedStatus,
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuadrantCard(
    priority: Priority,
    taskList: List<TaskVO>,
    modifier: Modifier = Modifier,
    dragAndDropState: DragAndDropState<TaskVO>,
    updateTaskPriority: (StringUUID, Int) -> Unit,
    updateCompletedStatus: (String, Boolean) -> Unit
) {

    val navController = LocalNavController.current

    val priorityColor = if (priority == Priority.NONE) Color(12, 206, 156) else priority.color
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = modifier
    ) {
        Row(
            Modifier
                .background(Color.Unspecified)
                .padding(start = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = "",
                tint = priorityColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = priority.detail,
                fontSize = 12.sp,
                color = priorityColor
            )
        }
        Spacer(modifier = Modifier.height(5.dp))

        LazyColumn(Modifier
            .fillMaxHeight()
            .dropTarget(
                key = priority.code,
                state = dragAndDropState,
                onDrop = { state ->
                    if (state.data.priority != priority.code) {
                        updateTaskPriority(state.data.id, priority.code)
                    }
                }
            )) {
            items(taskList) {
                DraggableItem(
                    state = dragAndDropState,
                    key = it.id,
                    data = it,
                    dropTargets = listOf(),
                    draggableContent = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(animateDpAsState(2.dp, label = "").value)
                                .background(MaterialTheme.colorScheme.surfaceDim)
                                .clip(RoundedCornerShape(10.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Checkbox(
                                    checked = it.completed,
                                    onCheckedChange = { checked ->
                                        updateCompletedStatus(
                                            it.id,
                                            checked
                                        )
                                    },
                                    colors = CheckboxDefaults.colors(
                                        uncheckedColor = priorityColor,
                                        checkedColor = MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier
                                        .scale(0.7f)
                                        .align(if (it.dueDate != null) Alignment.Top else Alignment.CenterVertically)
                                )
                            }
                            Column() {
                                Text(
                                    text = it.name,
                                    fontSize = 14.sp,
                                    lineHeight = 1.sp,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    color = if (it.completed) Color.Gray else Color.Unspecified,
                                    textDecoration = if (it.completed) TextDecoration.LineThrough else TextDecoration.None
                                )
                                if (it.dueDate != null) {
                                    val year =
                                        if (it.dueDate.year == LocalDate.now().year) "" else it.dueDate.year
                                    val dateTimeTip =
                                        "$year ${it.dueDate.format(DateTimeFormatter.ofPattern("MM月dd日"))} ${it.dueTime ?: ""}"

                                    val dateTimeTipColor = if (it.completed) {
                                        Color.Gray
                                    } else if (it.remindTime != null) {
                                        val isOver = LocalDateTime.now() > it.dueDate.atTime(it.dueTime ?: LocalTime.MAX)
                                        if (isOver) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                    } else {
                                        Color.Gray
                                    }

                                    Text(
                                        text = dateTimeTip,
                                        fontSize = 11.sp,
                                        color = dateTimeTipColor,
                                        lineHeight = 1.sp
                                    )
                                }
                            }
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.surfaceDim)
                            .clickable { navController.navigate(SubRouter.TaskForm.routeWithParam(it.id)) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                            Spacer(modifier = Modifier.width(3.dp))
                            Checkbox(
                                checked = it.completed,
                                onCheckedChange = { checked ->
                                    updateCompletedStatus(
                                        it.id,
                                        checked
                                    )
                                },
                                colors = CheckboxDefaults.colors(
                                    uncheckedColor = priorityColor,
                                    checkedColor = MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier
                                    .scale(0.7f)
                                    .align(if (it.dueDate != null) Alignment.Top else Alignment.CenterVertically)
                            )
                        }
                        Column() {
                            Text(
                                text = it.name,
                                fontSize = 14.sp,
                                lineHeight = 1.sp,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = if (it.completed) Color.Gray else Color.Unspecified,
                                textDecoration = if (it.completed) TextDecoration.LineThrough else TextDecoration.None
                            )
                            if (it.dueDate != null) {
                                val year =
                                    if (it.dueDate.year == LocalDate.now().year) "" else it.dueDate.year
                                val dateTimeTip =
                                    "$year ${it.dueDate.format(DateTimeFormatter.ofPattern("MM月dd日"))} ${it.dueTime ?: ""}"

                                val dateTimeTipColor = if (it.completed) {
                                    Color.Gray
                                } else if (it.remindTime != null) {
                                    val isOver = LocalDateTime.now() > it.dueDate.atTime(it.dueTime ?: LocalTime.MAX)
                                    if (isOver) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Gray
                                }

                                Text(
                                    text = dateTimeTip,
                                    fontSize = 11.sp,
                                    color = dateTimeTipColor,
                                    lineHeight = 1.sp
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
            item {
                if (taskList.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "没有事件",
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
