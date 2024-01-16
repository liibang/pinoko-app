package cn.liibang.pinoko.ui.screen.test

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.liibang.pinoko.ui.component.DashedDivider
import cn.liibang.pinoko.ui.component.DashedDividerVertical
import cn.liibang.pinoko.ui.screen.agenda.AgendaViewModel
import cn.liibang.pinoko.ui.screen.task.TaskViewModel
import cn.liibang.pinoko.ui.support.Border
import cn.liibang.pinoko.ui.support.border
import cn.liibang.pinoko.ui.support.bottomElevation
import cn.liibang.pinoko.ui.support.displayText
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.Period
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale


@Composable
fun TaskModalForm(
    agendaViewModel: AgendaViewModel = viewModel(),
    taskViewModel: TaskViewModel = hiltViewModel()
) {

    Column(Modifier.fillMaxSize()) {
        XWeekCalendar(
            agendaViewModel::changeSelectedDay,
            agendaViewModel::isEventOnDayOfWeek,
        )
        val rowHeight = 80.dp // 这里设置你想要的高度
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight * 24)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            for (i in 1..24) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$i",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .offset(y = 10.dp)
                            .width(20.dp), // 设置一个固定的宽度
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                    Column {
                        Box(
                            Modifier
                                .height(rowHeight)
                                .fillMaxWidth()
                        ) {
                            Row(modifier = Modifier) {
                                (1..7).forEach { _ ->

                                    val bili =
                                        (LocalTime.of(16, 30).minute - LocalTime.of(
                                            16,
                                            10
                                        ).minute) / 60

                                    Column(modifier = Modifier.weight(1f)) {
//                                // 在这里添加你的内容
//                                Box(
//                                    Modifier
//                                        .offset(y = ((80 / 60.toFloat()) * 30).dp)
//                                        .wrapContentHeight()
//                                        .fillMaxWidth()
//                                        .clip(RoundedCornerShape(10))
//                                        .background(MaterialTheme.colorScheme.tertiary)
//                                ) {
//                                    Text(text = "1231")
//                                }
                                    }
                                    DashedDividerVertical(
                                        dashWidth = 10f,
                                        strokeWidth = 2.5f,
                                        color = Color.Gray.copy(0.5f)
                                    )
                                }
                            }
                            // 中央线条
                            DashedDivider(
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.Gray.copy(0.5f)
                            )
                        }
                        DashedDivider(
                            dashWidth = 10f,
                            strokeWidth = 2.5f,
                            color = Color.Gray.copy(0.5f)
                        )
                    }
                }

            }

        }
    }
}


@Composable
fun XWeekCalendar(
//    selectedDay: LocalDate,
    changeSelectedDay: (LocalDate) -> Unit,
    isEventOnDayOfWeek: (LocalDate) -> StateFlow<Boolean>,
) {
    val now = LocalDate.now()
    val weekCalendarState = rememberWeekCalendarState(
        startDate = YearMonth.now().minusMonths(100).atStartOfMonth(),
        endDate = YearMonth.now().plusMonths(100).atEndOfMonth(),
        firstVisibleWeekDate = now,
        firstDayOfWeek = daysOfWeek(DayOfWeek.MONDAY).first()
    )
    val scope = rememberCoroutineScope()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceDim),
        elevation = 3.dp
    ) {
        Column(
            Modifier
                .bottomElevation()
                .background(MaterialTheme.colorScheme.surfaceDim),
        ) {
            // 头部
            Row(
                modifier = Modifier.padding(
                    start = 15.dp,
                    top = 5.dp,
                    bottom = 5.dp,
                    end = 5.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DayText(
                    selectedWeek = rememberFirstVisibleWeekAfterScroll(state = weekCalendarState),
                    now = now,
                )
                Spacer(modifier = Modifier.weight(1f))
                if (!weekCalendarState.firstVisibleWeek.days.map { it.date }.contains(now)) {
                    IconButton(modifier = Modifier.offset(y = ((-1.5).dp)), onClick = {
                        // 还要修改week TODO
                        scope.launch {
                            changeSelectedDay(now)
                            weekCalendarState.animateScrollToWeek(now)
                        }
                    }) {
                        Box(contentAlignment = Alignment.Center) {
                            androidx.compose.material.Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "回到当天",
                                tint = MaterialTheme.colorScheme.outline,
                            )
                            Text(
                                text = now.dayOfMonth.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .offset(x = (0).dp, y = (2.5).dp),
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }


                IconButton(onClick = { /*TODO*/ }) {
                    Box {
                        androidx.compose.material.Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "管理菜单",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }


            Row {
                // 这个字是用来凑空间的
                Text(
                    text = "0",
                    fontSize = 12.sp,
                    color = Color.Transparent,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
//                    .offset(y = 10.dp)
                        .width(20.dp), // 设置一个固定的宽度
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
                Column {
                    // 周
                    WeekHeader()
                    // 日历
                    WeekCalendar(
                        state = weekCalendarState,
                        dayContent = { weekDay ->
                            val hasEvent by isEventOnDayOfWeek(weekDay.date).collectAsState()
                            Day(
                                dayText = weekDay.date.dayOfMonth.toString(),
                                isSelected = false,
                                clickable = false,
                                onClick = { },
                                isToday = weekDay.date == now,
                                hasEvent = hasEvent,
                            )
                        }
                    )
                }
            }
        }
    }

}

@Composable
private fun WeekHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
            .testTag("MonthHeader"),
    ) {
        daysOfWeek(DayOfWeek.MONDAY).forEach { dayOfWeek ->
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                text = dayOfWeek.displayText().replace("周", ""),
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

fun DayOfWeek.displayText(): String {
    return this.getDisplayName(TextStyle.SHORT, Locale.CHINESE)
}

@Composable
private fun DayText(selectedWeek: Week, now: LocalDate) {
    val firstDate = selectedWeek.days.first().date
    val lastDate = selectedWeek.days.last().date

    val between = selectedWeek.days.first().date.compareTo(now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))

    val weeksBetween = between / 7
    Log.i(
        "TEST",
        "between: $between, weeksBetween: $weeksBetween"
    )
    val tip = when {
        weeksBetween == 0 && between % 7 == 0 -> "本周"
        weeksBetween == -1 -> "上周"
        weeksBetween == 1 -> "下周"
        firstDate.yearMonth == lastDate.yearMonth -> {
            firstDate.yearMonth.displayText()
        }

        firstDate.year == lastDate.year -> {
            "${firstDate.month.displayText(short = false)} - ${lastDate.yearMonth.displayText()}"
        }

        else -> {
            "${firstDate.yearMonth.displayText()} - ${lastDate.yearMonth.displayText()}"
        }
    }


    // 解析当前天
    Text(
        text = tip,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.onSurface,
    )
}


@Composable
private fun Day(
    dayText: String,
    isSelected: Boolean,
    clickable: Boolean,
    onClick: () -> Unit,
    isToday: Boolean,
    hasEvent: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f) // This is important for square-sizing!
            .testTag("MonthDay")
            .padding(9.4.dp)
            .shadow(if (isSelected) 2.5.dp else 0.dp, CircleShape)
            .clip(CircleShape)
            .background(color = if (isSelected) MaterialTheme.colorScheme.primary else if (isToday) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .clickable(
                enabled = clickable,
                onClick = onClick,
            ),
    ) {
        Text(
            text = dayText,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
        )
        val markColor = if (hasEvent) {
            if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        }
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(markColor)
                .align(BiasAlignment(0f, 0.7f))
        )


    }
}


@Composable
fun rememberFirstVisibleWeekAfterScroll(
    state: WeekCalendarState,
): Week {
    val visibleWeek = remember(state) { mutableStateOf(state.firstVisibleWeek) }
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { scrolling -> !scrolling }
            .collect {
                visibleWeek.value = state.firstVisibleWeek
            }
    }
    return visibleWeek.value
}
