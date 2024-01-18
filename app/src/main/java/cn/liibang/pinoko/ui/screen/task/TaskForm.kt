package cn.liibang.pinoko.ui.screen.task

import android.widget.Toast
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.liibang.pinoko.data.entity.TaskPO
import cn.liibang.pinoko.model.TaskCategoryVO
import cn.liibang.pinoko.toLocalDate
import cn.liibang.pinoko.ui.component.XTextField
import cn.liibang.pinoko.ui.constant.Priority
import cn.liibang.pinoko.ui.constant.priorityColor
import cn.liibang.pinoko.ui.screen.category.CategoryAddForm
import cn.liibang.pinoko.ui.screen.category.CategoryViewModel
import cn.liibang.pinoko.ui.screen.form.OptButton
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.support.showToast
import cn.liibang.pinoko.ui.theme.categoryColor
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

enum class EditMode() {
    CREATE, UPDATE
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskForm(
    id: String?,
    taskViewModel: TaskViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
) {

    val editMode = if (id == null) EditMode.CREATE else EditMode.UPDATE

    val context = LocalContext.current

    var formState by remember {
        mutableStateOf(
            TaskPO(
                id = "",
                name = "",
                createdAt = LocalDateTime.MIN,
                updatedAt = LocalDateTime.MIN
            )
        )
    }

    LaunchedEffect(Unit) {
        if (editMode == EditMode.UPDATE) {
            val detail = taskViewModel.detail(id!!)
            formState = detail
        }
    }

    var enableOptButton by remember {
        mutableStateOf(editMode != EditMode.CREATE)
    }

    val categories by categoryViewModel.taskCategories.collectAsState()

    var isShowFlag by remember {
        mutableStateOf(false)
    }

    var categoryExpandable by remember {
        mutableStateOf(false)
    }

    val navController = LocalNavController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {

        Row(
            modifier = Modifier.padding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    "close",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = if (editMode == EditMode.CREATE) "添加事件" else "编辑事件",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            OptButton(enableOptButton, onClick = {
                taskViewModel.saveOrUpdate(editMode, formState)
                navController.popBackStack()
            })
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Checkbox(
                checked = formState.completed,
                onCheckedChange = { formState = formState.copy(completed = it) },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
            CateGorySelector(
                taskCategories = categories,
                expanded = categoryExpandable,
                onClick = { categoryExpandable = true },
                onDismissRequest = { categoryExpandable = false },
                selectedCategoryId = formState.categoryId,
                selectCategory = {
                    formState = formState.copy(categoryId = it)
                    categoryExpandable = false
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            PrioritySelector(
                isShow = isShowFlag,
                selectedFlagId = formState.priority,
                changeShowState = { isShowFlag = !isShowFlag },
                selectFlag = { type ->
                    formState = formState.copy(priority = type)
                    isShowFlag = false
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        // 标题
        Column(Modifier.padding(horizontal = 10.dp)) {
            TaskNameInput(taskName = formState.name) {
                formState = formState.copy(name = it)
                enableOptButton = formState.name.isNotBlank()
            }
            Spacer(modifier = Modifier.height(20.dp))
            // 描述、备注
            NoteInput(formState.note, onValueChange = { formState = formState.copy(note = it) })
        }

        // ===================日期
        Column {
            val dateState = rememberDatePickerState(selectableDates = object : SelectableDates {})
            var isShow by remember { mutableStateOf(false) }
            val dateText =
                formState.dueDate?.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
            FormItem(
                Icons.Default.Event,
                title = "截止日期",
                value = dateText,
                onClick = { isShow = true },
                enabled = true,
                onUnEnable = {},
                cleanValue = {
                    formState = formState.copy(dueDate = null, dueTime = null, reminderTime = null)
                }
            )
            if (isShow) {
                DatePickerDialog(onDismissRequest = { isShow = false }, confirmButton = {
                    Row {
                        TextButton(onClick = { isShow = false }) {
                            Text(text = "取消")
                        }
                        TextButton(onClick = {
                            formState =
                                formState.copy(dueDate = dateState.selectedDateMillis.toLocalDate())
                            isShow = false
                        }) {
                            Text(text = "确定")
                        }
                    }
                }) {
                    DatePicker(state = dateState)
                }
            }
        }

        // ==========================时间
        Column {
            val enabled = formState.dueDate != null
            var isShow by remember { mutableStateOf(false) }
            FormItem(
                icon = Icons.Default.AccessTime,
                title = "时间",
                value = formState.dueTime?.toString(),
                onClick = { isShow = true },
                enabled = enabled,
                onUnEnable = { context.showToast("请先选择截止日期", Toast.LENGTH_SHORT) },
                cleanValue = { formState = formState.copy(dueTime = null, reminderTime = null) }
            )
            if (isShow) {
                val now = LocalTime.now()
                val timeState = rememberTimePickerState(now.hour, now.minute, true)
                Dialog(onDismissRequest = { isShow = false }) {
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
                                TextButton(onClick = { isShow = false }) {
                                    Text(text = "取消")
                                }
                                TextButton(onClick = {
                                    formState = formState.copy(
                                        dueTime = LocalTime.of(
                                            timeState.hour,
                                            timeState.minute
                                        )
                                    )
                                    isShow = false
                                }) {
                                    Text(text = "确定")
                                }
                            }
                        }
                    }
                }
            }
        }

        // ================提醒时间=================================
        Column {
            val enabled = formState.dueTime != null
            var isShowDialog by remember { mutableStateOf(false) }
            var menuExpanded by remember { mutableStateOf(false) }
            FormItem(
                icon = Icons.Default.Alarm,
                title = "提醒",
                value = formState.reminderTime?.let {
                    if (formState.dueDate == it.toLocalDate()) {
                        it.toLocalTime().toString()
                    } else {
                        it.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
                    }
                },
                onClick = { menuExpanded = true },
                enabled = enabled,
                onUnEnable = { context.showToast("请先选择时间", Toast.LENGTH_SHORT) },
                cleanValue = { formState = formState.copy(reminderTime = null) }
            )
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim),
            ) {
                val dueDateTime = formState.dueDate!!.atTime(formState.dueTime)
                DropdownMenuItem(
                    text = { Text(text = "与截止日期相同", fontWeight = FontWeight.SemiBold) },
                    onClick = { formState = formState.copy(reminderTime = dueDateTime) },
                )
                DropdownMenuItem(
                    text = { Text(text = "5分钟前", fontWeight = FontWeight.SemiBold) },
                    onClick = {
                        formState = formState.copy(reminderTime = dueDateTime.minusMinutes(5))
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "10分钟前", fontWeight = FontWeight.SemiBold) },
                    onClick = {
                        formState = formState.copy(reminderTime = dueDateTime.minusMinutes(10))
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "30分钟前", fontWeight = FontWeight.SemiBold) },
                    onClick = {
                        formState = formState.copy(reminderTime = dueDateTime.minusMinutes(30))
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "提前一天", fontWeight = FontWeight.SemiBold) },
                    onClick = {
                        formState = formState.copy(reminderTime = dueDateTime.minusDays(1))
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "自定义时间", fontWeight = FontWeight.SemiBold) },
                    onClick = { isShowDialog = true }
                )
            }
            // 提醒时间滑块（弹窗）
            if (enabled) {
                ReminderPicker(
                    isShowDialog,
                    onDismissRequest = { isShowDialog = false },
                    dueDateTime = formState.dueDate!!.atTime(formState.dueTime),
                    updateReminderTime = { formState = formState.copy(reminderTime = it) }
                )
            }
        }

    }
}


@Composable
fun CateGorySelector(
    taskCategories: List<TaskCategoryVO>,
    expanded: Boolean,
    onClick: () -> Unit,
    onDismissRequest: () -> Unit,
    selectedCategoryId: String?,
    selectCategory: (String?) -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.outline.copy(
                0.1f
            )
        )
    ) {
        val category = taskCategories.find { it.id == selectedCategoryId }
        Text(
            text = category?.name ?: "没有分类",
            color = MaterialTheme.colorScheme.outline
        )
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.outline
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim)
        ) {
            // TODO 转成lazy
            Column(modifier = Modifier.scrollable(rememberScrollState(), Orientation.Vertical)) {

                DropdownMenuItem(
                    text = {
                        Text(
                            "没有分类",
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedCategoryId == null) MaterialTheme.colorScheme.primary else Color.Unspecified
                        )
                    },
                    onClick = { selectCategory(null) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.FiberManualRecord,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                )

                taskCategories.forEach {

                    DropdownMenuItem(
                        text = {
                            Text(
                                it.name,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selectedCategoryId == it.id) MaterialTheme.colorScheme.primary else Color.Unspecified
                            )
                        },
                        onClick = { selectCategory(it.id) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.FiberManualRecord,
                                contentDescription = "",
                                tint = categoryColor(it.color)
                            )
                        }
                    )
                }

                var showCategoryForm by remember {
                    mutableStateOf(false)
                }
                DropdownMenuItem(
                    text = {
                        Text(
                            "新建",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = { showCategoryForm = true },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
                CategoryAddForm(
                    showCategoryForm,
                    onDismissRequest = { showCategoryForm = false },
                    onCreate = selectCategory
                )

            }
        }
    }
}


@Composable
fun TaskNameInput(taskName: String, onValueChange: (String) -> Unit) {
    XTextField(
        value = taskName,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "准备做什么？",
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.outline,
            )
        },
        textStyle = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
        ),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary.copy(0.8f),
//                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
        ),
        singleLine = true,
        modifier = Modifier
            .background(Color.Unspecified)
            .fillMaxWidth(),
    )
}

// TODO 添加背景 或者 字数限制
@Composable
private fun NoteInput(note: String?, onValueChange: (String) -> Unit) {
    Box {
        XTextField(
            value = note ?: "",
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = "加个备注",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.outline,
                )
            },
            textStyle = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary.copy(0.8f),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .background(Color.Unspecified)
                .fillMaxWidth(),
            minLines = 8
        )
        Text(
            text = "0 /1024",
            modifier = Modifier
                .align(Alignment.BottomEnd),
//                .padding(bottom = 5.dp, end = 5.dp),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun PrioritySelector(
    isShow: Boolean,
    selectedFlagId: Int,
    changeShowState: () -> Unit,
    selectFlag: (Int) -> Unit
) {
    IconButton(onClick = changeShowState) {
        Icon(
            imageVector = Icons.Default.Flag,
            contentDescription = null,
            tint = priorityColor(selectedFlagId)
        )
        DropdownMenu(
            expanded = isShow,
            onDismissRequest = changeShowState,
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim)
        ) {
            Priority.values().forEach {
                DropdownMenuItem(
                    text = { Text(it.desc, fontWeight = FontWeight.SemiBold) },
                    onClick = { selectFlag(it.code) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "",
                            tint = it.color
                        )
                    }
                )
            }
        }
    }
}


@Composable
private fun FormItem(
    icon: ImageVector,
    title: String,
    value: String?,
    onClick: () -> Unit,
    enabled: Boolean,
    onUnEnable: () -> Unit,
    cleanValue: () -> Unit
) {

    val colorAlpha = if (enabled) 1f else 0.5f
    TextButton(
        onClick = if (enabled) onClick else onUnEnable,
        shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp)),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Unspecified,
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp)
        ),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.outline.copy(colorAlpha)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.outline.copy(colorAlpha)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = value ?: "无",
                color = MaterialTheme.colorScheme.outline.copy(colorAlpha),
                modifier = Modifier
                    .clip(CircleShape.copy(CornerSize(10.dp)))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(colorAlpha))
                    .padding(horizontal = 5.dp, vertical = 3.dp)
            )
            if (value != null) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "clear",
                    tint = MaterialTheme.colorScheme.outline.copy(colorAlpha),
                    modifier = Modifier
//                        .scale(0.7f)
                        .padding(start = 9.dp)
                        .size(21.5.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable(
                            onClick = cleanValue
                        )
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(colorAlpha))
                )
            }
        }
    }
}
