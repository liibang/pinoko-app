package cn.liibang.pinoko.ui.screen.agenda.calendar

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.entity.TermPO
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.screen.main.MainRouter
import cn.liibang.pinoko.ui.support.calculateWeekNumber
import cn.liibang.pinoko.ui.support.displayText
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlin.reflect.KFunction1


@Composable
fun WeekScheduleView(
    now: LocalDate,
    weekCalendarState: WeekCalendarState,
    termSetId: StringUUID?,
    fetchTermById: suspend (StringUUID) -> TermPO?,
    selectedDay: LocalDate,
    changeSelectedDay: (LocalDate) -> Unit
) {

    val scope = rememberCoroutineScope()

    var term by remember {
        mutableStateOf<TermPO?>(null)
    }

    LaunchedEffect(Unit) {
        if (termSetId != null) {
            term = fetchTermById(termSetId)
        }
    }


     val navController = LocalNavController.current

    Column() {
        // 头部
        Row(
            modifier = Modifier.padding(
                start = 15.dp,
                top = 5.dp,
                bottom = 5.dp,
                end = 5.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            DayText(
                selectedWeek = rememberFirstVisibleWeekAfterScroll(
                    state = weekCalendarState,
                    changeSelectDay = changeSelectedDay,
                    now = now,
                    selectedDay = selectedDay,
                ),
                now = now,
            )

            if (term != null && weekCalendarState.firstVisibleWeek.days.any { it.date >= term!!.startDate }) {
                val currentWeekValue = calculateWeekNumber(termStartDate = term!!.startDate, dateToCheck = selectedDay)
                Text(text = " (第${currentWeekValue}周)", fontSize = 13.5.sp)
            }
            Spacer(modifier = Modifier.weight(1f))

            if (!weekCalendarState.firstVisibleWeek.days.map { it.date }.contains(now)) {
                IconButton(modifier = Modifier.offset(y = ((-1.5).dp)), onClick = {
                    // 还要修改week TODO
                    scope.launch {
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


            TextButton(onClick = {
                navController.navigate(MainRouter.SchoolTimeTable.route)
            }) {
                Text(text = "学期管理")
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
                        Day(
                            dayText = weekDay.date.dayOfMonth.toString(),
                            isSelected = false,
                            clickable = false,
                            onClick = { },
                            isToday = weekDay.date == now,
                            hasEvent = false,
                        )
                    }
                )
            }
        }
    }

}


@Composable
private fun DayText(selectedWeek: Week, now: LocalDate) {
    val firstDate = selectedWeek.days.first().date
    val lastDate = selectedWeek.days.last().date

    val between =
        selectedWeek.days.first().date.compareTo(now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))

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


//@Composable
//private fun rememberFirstVisibleWeekAfterScroll(
//    state: WeekCalendarState,
//): Week {
//    val visibleWeek = remember(state) { mutableStateOf(state.firstVisibleWeek) }
//    LaunchedEffect(state) {
//        snapshotFlow { state.isScrollInProgress }
//            .filter { scrolling -> !scrolling }
//            .collect {
//                visibleWeek.value = state.firstVisibleWeek
//            }
//    }
//    return visibleWeek.value
//}
