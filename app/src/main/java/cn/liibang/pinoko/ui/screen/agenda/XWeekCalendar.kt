package cn.liibang.pinoko.ui.screen.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.liibang.pinoko.ui.support.bottomElevation


import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.launch
import java.time.Period
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun XWeekCalendar(
    selectedDay: LocalDate,
    changeSelectedDay: (LocalDate) -> Unit,
    isEventOnDayOfWeek: (LocalDate) -> StateFlow<Boolean>,
    inboxSize: Int
) {
    val now = LocalDate.now()
    val weekCalendarState = rememberWeekCalendarState(
        startDate = YearMonth.now().minusMonths(100).atStartOfMonth(),
        endDate = YearMonth.now().plusMonths(100).atEndOfMonth(),
        firstVisibleWeekDate = selectedDay,
        firstDayOfWeek = daysOfWeek(DayOfWeek.MONDAY).first()
    )
    val scope = rememberCoroutineScope()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceDim),
        elevation = 3.dp
    ) {
        Column(Modifier.bottomElevation().background(MaterialTheme.colorScheme.surfaceDim)) {
            // 头部
            Row(
                modifier = Modifier.padding(start = 15.dp, top = 5.dp, bottom = 5.dp, end = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DayText(
                    selectedWeek = rememberFirstVisibleWeekAfterScroll(
                        now = now,
                        state = weekCalendarState,
                        changeSelectDay = changeSelectedDay,
                        selectedDay = selectedDay
                    ),
                    now = now,
                    selectedDay = selectedDay
                )
                Spacer(modifier = Modifier.weight(1f))
                if (now != selectedDay) {
                    IconButton(modifier = Modifier.offset(y = ((-1.5).dp)), onClick = {
                        // 还要修改week TODO
                        scope.launch {
                            changeSelectedDay(now)
                            weekCalendarState.animateScrollToWeek(now)
                        }
                    }) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
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


                IconButton(modifier = Modifier.offset(y = (0.5.dp)), onClick = { /*TODO*/ }) {
                    Box() {
                        BadgedBox(badge = {
                            Badge(
                                modifier = Modifier
                                    .scale(0.75f)
                                    .offset(
                                        x = when {
                                            inboxSize < 10 -> (-6).dp
                                            inboxSize < 100 -> (0).dp
                                            else -> (0).dp
                                        }, y = (-3).dp
                                    ),
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(if (inboxSize < 100) inboxSize.toString() else "99+")
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Inbox,
                                contentDescription = "待办箱",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Box {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "改变日历视图",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }

                }


            }
            // 周
            WeekHeader()
            // 日历
            WeekCalendar(
                state = weekCalendarState,
                dayContent = { weekDay ->
                    val hasEvent by isEventOnDayOfWeek(weekDay.date).collectAsState()
                    Day(
                        dayText = weekDay.date.dayOfMonth.toString(),
                        isSelected = selectedDay == weekDay.date,
                        clickable = true,
                        onClick = { changeSelectedDay(weekDay.date) },
                        isToday = weekDay.date == now,
                        hasEvent = hasEvent
                    )
                }
            )
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
private fun DayText(selectedWeek: Week, now: LocalDate, selectedDay: LocalDate) {

    val monthText = selectedDay.month.getDisplayName(TextStyle.SHORT, Locale.CHINESE) + ", "
    val dateTip = monthText + when {
        selectedDay == now -> "今天"
        now.minusDays(1) == selectedDay -> "昨天"
        now.minusDays(2) == selectedDay -> "前天"
        now.plusDays(1) == selectedDay -> "明天"
        now.plusDays(2) == selectedDay -> "后天"
        selectedDay.year == now.year -> {
            val weeksBetween = Period.between(
                now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                selectedWeek.days.first().date
            ).days / 7
            val monthsBetween = selectedDay.monthValue - now.monthValue
            when {
                monthsBetween == -1 -> "上个月"
                monthsBetween == 1 -> "下个月"
                monthsBetween < -1 -> "${-monthsBetween}个月前"
                monthsBetween > 1 -> "${monthsBetween}个月后"
                else -> {
                    val dayOfWeekText = selectedDay.dayOfWeek.displayText()
                    when {
                        weeksBetween == -1 -> "上 $dayOfWeekText"
                        weeksBetween == 1 -> "下 $dayOfWeekText"
                        weeksBetween > 1 -> "${weeksBetween}周后"
                        weeksBetween < -1 -> "${-weeksBetween}周前"
                        else -> dayOfWeekText
                    }
                }
            }
        }

        else -> "${selectedDay.year}年"
    }
    // 解析当前天
    Text(
        text = dateTip,
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
    hasEvent: Boolean
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
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
    changeSelectDay: (LocalDate) -> Unit,
    now: LocalDate,
    selectedDay: LocalDate,
): Week {
    val visibleWeek = remember(state) { mutableStateOf(state.firstVisibleWeek) }
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { scrolling -> !scrolling }
            .collect {
                visibleWeek.value = state.firstVisibleWeek
                visibleWeek.value.days.map { it.date }.run {
                    if (contains(now)) {
                        changeSelectDay(selectedDay)
                    } else {
                        changeSelectDay(first())
                    }
                }
            }
    }
    return visibleWeek.value
}
