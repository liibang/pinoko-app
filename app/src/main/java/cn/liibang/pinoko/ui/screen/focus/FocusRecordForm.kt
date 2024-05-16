package cn.liibang.pinoko.ui.screen.focus

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.entity.FocusRecordPO
import cn.liibang.pinoko.ui.component.OptButton
import cn.liibang.pinoko.ui.component.XTextField
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.data.DataStore
import cn.liibang.pinoko.data.entity.SettingPO
import cn.liibang.pinoko.ui.support.formatToHM
import cn.liibang.pinoko.ui.support.showToast
import cn.liibang.pinoko.ui.support.toDateMillis
import cn.liibang.pinoko.ui.support.toLocalDate
import cn.liibang.pinoko.ui.theme.XShape
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class FocusRecordFormState(
    val id: StringUUID? = null,
    val startAt: LocalDateTime,
    val taskId: StringUUID?, // 绑定任务ID（可选）
    val taskName: String,
    val note: String = "", // 笔记
    val tomatoCount: Int = 1,
    val tomaDuration: Duration,
    val focusRecordPO: FocusRecordPO? = null
)

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusRecordForm(id: StringUUID?, focusViewModel: FocusViewModel = hiltViewModel(), setting: SettingPO) {

    val navController = LocalNavController.current
    val context = LocalContext.current

    var formState by remember {
        mutableStateOf(
            Unit.run {
                val startAt = LocalDateTime.now().minusHours(2)
                FocusRecordFormState(
                    startAt = startAt,
                    taskId = null,
                    taskName = "",
                    note = "",
                    tomatoCount = 1,
                    tomaDuration = setting.focusTomatoDuration,
                )
            }
        )
    }
    LaunchedEffect(Unit) {
        if (id != null) {
            focusViewModel.getByID(id)?.let {
                formState = FocusRecordFormState(
                    id = it.id,
                    startAt = it.startAt,
                    taskId = it.taskId,
                    taskName = focusViewModel.getRecordLinkTaskName(it.taskId),
                    note = it.note,
                    tomatoCount = 1,
                    tomaDuration = setting.focusTomatoDuration,
                    focusRecordPO = it
                )
            } ?: context.showToast("无法获取专注记录")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp))
            .padding(10.dp)
    ) {
        // 专注列表
        // 头部
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
            Spacer(modifier = Modifier.width(18.dp))
            Text(
                text = if (id == null) "添加专注记录" else "编辑专注记录",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            OptButton(
                enabled = true,
                onClick = {
                    if (id == null) {
                        val now = LocalDateTime.now()
                        val endAt =
                            formState.startAt.plusSeconds(formState.tomatoCount * setting.focusTomatoDuration.toSeconds())
                        if (endAt.compareTo(now) == 1) {
                            context.showToast("只能添加往前的记录")
                        } else {
                            focusViewModel.batchSaveRecord(formState)
                            navController.popBackStack()
                        }
                    } else {
                        // Update the record TODO
                        focusViewModel.updateRecord(
                            formState.id!!,
                            formState.note,
                            formState.taskId
                        )
                        navController.popBackStack()
                    }
                }
            )
        }
        // Header END
        Spacer(Modifier.padding(top = 10.dp))


        // ========关联任务======================

        var isShowFocusSelectTasDialog by remember {
            mutableStateOf(false)
        }
        FocusSelectTaskDialog(
            isShow = isShowFocusSelectTasDialog,
            isSelected = formState.taskId != null,
            onDismissRequest = { isShowFocusSelectTasDialog = false },
            onSelect = { taskVO ->
                formState = formState.copy(taskId = taskVO?.id, taskName = taskVO?.name ?: "")
                isShowFocusSelectTasDialog = false
            }
        )
        Row(
            Modifier
                .padding(start = 12.dp, top = 10.dp, bottom = 10.dp)
                .clickable { isShowFocusSelectTasDialog = true }
        ) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "关联任务",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.weight(1f))
            if (formState.taskId != null) {
                Text(
                    text = formState.taskName,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .clip(CircleShape.copy(CornerSize(10.dp)))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 5.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }


        if (id == null) {
            // ================ 开始日期 ===========================
            var isShowStartDatePicker by remember { mutableStateOf(false) }
            TextButton(
                shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp)),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Unspecified,
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp)
                ),
                onClick = { isShowStartDatePicker = true },
                enabled = id == null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "开始日期",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = formState.startAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .clip(CircleShape.copy(CornerSize(10.dp)))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 5.dp, vertical = 3.dp)
                    )
                }
                if (isShowStartDatePicker) {
                    val dateState =
                        rememberDatePickerState(selectableDates = object : SelectableDates {})
                    dateState.selectedDateMillis = formState.startAt.toLocalDate().toDateMillis()
                    dateState.displayedMonthMillis = formState.startAt.toLocalDate().toDateMillis()
                    DatePickerDialog(
                        onDismissRequest = { isShowStartDatePicker = false },
                        confirmButton = {
                            Row {
                                TextButton(onClick = { isShowStartDatePicker = false }) {
                                    Text(text = "取消")
                                }
                                TextButton(onClick = {
                                    formState =
                                        formState.copy(
                                            startAt = LocalDateTime.of(
                                                dateState.selectedDateMillis.toLocalDate(),
                                                formState.startAt.toLocalTime()
                                            )
                                        )
                                    isShowStartDatePicker = false
                                }) {
                                    Text(text = "确定")
                                }
                            }
                        }) {
                        DatePicker(state = dateState)
                    }
                }
            }

            var isShow by remember { mutableStateOf(false) }
            TextButton(
                shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp)),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Unspecified,
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp)
                ),
                onClick = { isShow = true },
                enabled = id == null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "",
                        tint = Color.Transparent
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "开始时间",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = formState.startAt.toLocalTime()
                            .format(DateTimeFormatter.ofPattern("HH:mm")),
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .clip(CircleShape.copy(CornerSize(10.dp)))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(start = 5.dp, top = 3.dp, bottom = 3.dp)
                    )
                }
                if (isShow) {
                    val timeState = rememberTimePickerState(
                        formState.startAt.hour,
                        formState.startAt.minute,
                        true
                    )
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
                                            startAt = LocalDateTime.of(
                                                formState.startAt.toLocalDate(),
                                                LocalTime.of(timeState.hour, timeState.minute)
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
        } else {
            // 开始时间
            TextButton(
                shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp)),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Unspecified,
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp)
                ),
                onClick = { },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "",
                        tint = Color.Transparent
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "开始时间",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = formState.startAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .clip(CircleShape.copy(CornerSize(10.dp)))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

        }


        // ==========================时间
        Column {

            // 结束日期和时间
            TextButton(
                shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp)),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Unspecified,
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp)
                ),
                onClick = { },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "",
                        tint = Color.Transparent
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (id == null) "结束于" else "结束时间",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = Unit.run {
                            if (id == null) {
                                formState.startAt.plusSeconds(formState.tomatoCount * setting.focusTomatoDuration.toSeconds())
                            } else {
                                formState.startAt
                            }
                        }.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .clip(CircleShape.copy(CornerSize(10.dp)))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

            // =============== 番茄数量 ======================
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                if (id == null) {
                    Text(
                        text = "番茄数量",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        if (formState.tomatoCount > 1) {
                            formState = formState.copy(tomatoCount = formState.tomatoCount - 1)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "${formState.tomatoCount}",
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 10.dp)
                            .width(30.dp),
                        textAlign = TextAlign.Center
                    )
                    IconButton(onClick = {
                        if (formState.tomatoCount < 10)
                        // 检查时间是否够
                            formState = formState.copy(
                                tomatoCount = formState.tomatoCount + 1,
                            )
                        else {
                            context.showToast("最大数不能超过10")
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    TextButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "",
                            tint = Color.Transparent
                        )
                        Text(
                            text = "专注时长",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = formState.focusRecordPO?.focusDuration?.formatToHM() ?: "",
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(horizontal = 10.dp),
//                            .width(30.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }


            // ===================== 笔记部分 ===========
            Column(modifier = Modifier.padding(start = 12.dp, top = 10.dp)) {
                Text(
                    text = "专注笔记",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(5.dp))
                XTextField(
                    value = formState.note,
                    onValueChange = { formState = formState.copy(note = it) },
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
            }
        }
        if (id != null) {

            var isShowFocusRecordDeleteConfirmDialog by remember {
                mutableStateOf(false)
            }
            FocusRecordDeleteConfirmDialog(
                isShow = isShowFocusRecordDeleteConfirmDialog,
                onDismissRequest = { isShowFocusRecordDeleteConfirmDialog = false },
                onConfirm = {
                    // doDelete
                    focusViewModel.delete(id)
                    context.showToast("删除成功")
                    navController.popBackStack()
                }
            )

            Spacer(modifier = Modifier.height(15.dp))
            TextButton(
                onClick = {
                    isShowFocusRecordDeleteConfirmDialog = true
                },
                modifier = Modifier.align(CenterHorizontally)
            ) {
                Text(text = "删除", color = MaterialTheme.colorScheme.error)
            }
        }
    }

}