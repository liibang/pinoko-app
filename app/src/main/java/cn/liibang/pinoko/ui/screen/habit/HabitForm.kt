package cn.liibang.pinoko.ui.screen.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.liibang.pinoko.data.entity.HabitPO
import cn.liibang.pinoko.data.entity.HabitType
import cn.liibang.pinoko.ui.component.OptButton
import cn.liibang.pinoko.ui.component.XTextField
import cn.liibang.pinoko.ui.screen.agenda.calendar.displayText
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.support.showToast
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun HabitForm(id: String?, habitViewModel: HabitViewModel) {

    val navController = LocalNavController.current

    val ctx = LocalContext.current

    var formState by remember {
        mutableStateOf(
            HabitPO(
                id = "",
                name = "",
                type = HabitType.WEEKLY_SPECIFIC_DAYS,
                value = "1,2,3,4,5,6,7",
                remindTime = null,
                startAt = LocalDate.now(),
                createdAt = LocalDateTime.MIN,
                updatedAt = LocalDateTime.MIN
            )
        )
    }

    LaunchedEffect(Unit) {
        if (id != null) {
            habitViewModel.fetchById(id)?.let { formState = it }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 10.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    "close",
                    tint = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            Text(
                text = if (id == null) "创建习惯" else "修改习惯",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))

            if (id != null) {
                var isShowHabitDeleteConfirmDialog by remember {
                    mutableStateOf(false)
                }
                HabitDeleteConfirmDialog(
                    isShow = isShowHabitDeleteConfirmDialog,
                    onDismissRequest = { isShowHabitDeleteConfirmDialog = false },
                    onConfirm = {
                        habitViewModel.remove(formState)
                        ctx.showToast("删除成功")
                        navController.popBackStack()
                    }
                )
                Spacer(modifier = Modifier.width(10.dp))
                TextButton(onClick = { isShowHabitDeleteConfirmDialog = true }) {
                    Text(text = "删除", color = MaterialTheme.colorScheme.error)
                }
            }

            OptButton(enabled = formState.name.isNotEmpty(), onClick = {
                habitViewModel.saveOrUpdate(formState)
                navController.popBackStack()
            })
        }

        Spacer(modifier = Modifier.height(15.dp))

        XTextField(
            value = formState.name,
            onValueChange = { formState = formState.copy(name = it) },
            placeholder = {
                Text(
                    text = "定义习惯名称",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.outline,
                )
            },
            supportingText = {
                Text(
                    text = "例如早起、背单词",
                    color = Color.Gray.copy(0.5f),
                    modifier = Modifier
                        .padding(start = 0.dp)
                        .offset(x = (-12).dp)
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
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true,
            modifier = Modifier
                .height(70.dp)
                .background(Color.Unspecified)
                .fillMaxWidth()
                .padding(start = 20.dp),
        )


        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "习惯周期",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.outline
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            HabitType.values().forEach {
                FilterChip(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    onClick = {
                        formState = formState.copy(
                            type = it, value =
                            when (it) {
                                HabitType.WEEKLY_SPECIFIC_DAYS -> "1,2,3,4,5,6,7"
                                HabitType.MONTHLY_SPECIFIC_DAY -> "1"
                                HabitType.EVERY_FEW_DAYS -> "2"
                            }
                        )
                    },
                    label = { Text(it.desc) },
                    selected = formState.type == it,
                    colors = FilterChipDefaults.filterChipColors(
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(enabled = false, selected = true)
                )
            }
        }
        HabitTypeElement(
            habitType = formState.type,
            onValueChange = { formState = formState.copy(value = it) },
            value = formState.value
        )

        Spacer(modifier = Modifier.height(20.dp))
        // =============提醒时间==============
        var isShowTimePicker by remember {
            mutableStateOf(false)
        }
        ReminderTimeDialog(
            isShow = isShowTimePicker,
            onDismissRequest = { isShowTimePicker = false },
            onClick = { formState = formState.copy(remindTime = it) },
            initData = formState.remindTime
        )
        Row(modifier = Modifier
            .background(Color.Unspecified)
            .clickable { isShowTimePicker = true }
            .padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "提醒时间",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = formState.remindTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: " + ",
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 10.dp),
                textAlign = TextAlign.Center
            )
            if (formState.remindTime != null) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "clear",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .padding(start = 9.dp)
                        .size(21.5.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable(
                            onClick = { formState = formState.copy(remindTime = null) }
                        )
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
        // ==================== 开始日期 =======================
        var isShowStartDatePicker by remember {
            mutableStateOf(false)
        }
        StartDatePicker(
            isShow = isShowStartDatePicker,
            onDismissRequest = { isShowStartDatePicker = false },
            onConfirm = { formState = formState.copy(startAt = it) },
            initData = formState.startAt
        )
        Row(modifier = Modifier
            .background(Color.Unspecified)
            .clickable { isShowStartDatePicker = true }
            .padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
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
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 10.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(10.dp))
        }

    }
}

@Composable
fun HabitTypeElement(habitType: HabitType, value: String, onValueChange: (String) -> Unit) {

    val ctx = LocalContext.current

    when (habitType) {
        HabitType.WEEKLY_SPECIFIC_DAYS -> {
            val dayValueOfWeeks = value.split(",").filter { it.isNotEmpty() }.map { it.toInt() }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek(DayOfWeek.MONDAY).forEach {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    if (it.value in dayValueOfWeeks) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primaryContainer
                                )
                                .clickable {

                                    val newDayValueOfWeeksStringValue =
                                        if (it.value in dayValueOfWeeks) {

                                            if (dayValueOfWeeks.size == 1) {
                                                ctx.showToast("至少选择一天")
                                                return@clickable
                                            }

                                            dayValueOfWeeks
                                                .toMutableList()
                                                .apply { remove(it.value) }
                                                .joinToString(separator = ",")
                                        } else {
                                            dayValueOfWeeks
                                                .toMutableList()
                                                .apply {
                                                    add(it.value)
                                                    sort()
                                                }
                                                .joinToString(separator = ",")
                                        }
                                    onValueChange(newDayValueOfWeeksStringValue)
                                }
                        ) {
                            Text(
                                text = it.displayText().replace("周", ""),
                                modifier = Modifier.align(Alignment.Center),
                                fontSize = 12.sp,
                                color = if (it.value in dayValueOfWeeks) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        HabitType.MONTHLY_SPECIFIC_DAY -> {
            var isShowDayOfMonthSelector by remember {
                mutableStateOf(false)
            }
            val dayOfMonthValue = value.toInt()
            DayOfMonthSelector(
                isShow = isShowDayOfMonthSelector,
                onDismissRequest = { isShowDayOfMonthSelector = false },
                onSelect = { onValueChange(it.toString()) },
                initValue = dayOfMonthValue,
                title = ""
            )
            Row(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("每月的 ")
                Text(
                    text = "$dayOfMonthValue",
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 10.dp)
                        .width(30.dp)
                        .clickable { isShowDayOfMonthSelector = true },
                    textAlign = TextAlign.Center,
                )
                Text(" 日")
            }
        }

        HabitType.EVERY_FEW_DAYS -> {
            var isShowDayOfMonthSelector by remember {
                mutableStateOf(false)
            }
            val everyFewDayValue = value.toInt()
            EveryFewDaysSelector(
                isShow = isShowDayOfMonthSelector,
                onDismissRequest = { isShowDayOfMonthSelector = false },
                onSelect = { onValueChange(it.toString()) },
                initValue = everyFewDayValue,
                title = ""
            )
            Row(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("每隔 ")
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 10.dp)
                        .width(30.dp)
                        .clickable { isShowDayOfMonthSelector = true },
                    textAlign = TextAlign.Center,
                )
                Text(" 天")
            }
        }
    }


}














