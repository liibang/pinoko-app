package cn.liibang.pinoko.ui.screen.agenda

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.liibang.pinoko.ui.screen.agenda.calendar.WeekCalendarView
import cn.liibang.pinoko.ui.screen.agenda.calendar.WeekScheduleView
import cn.liibang.pinoko.ui.screen.main.AgendaDisplayMode
import cn.liibang.pinoko.ui.screen.task.TaskViewModel
import cn.liibang.pinoko.ui.support.bottomElevation
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun AgendaScreen(
    displayMode: AgendaDisplayMode,
    agendaViewModel: AgendaViewModel = viewModel(),
    taskViewModel: TaskViewModel = hiltViewModel()
) {

    val tasks by agendaViewModel.tasks.collectAsState()
    val selectedDay by agendaViewModel.selectedDay.collectAsState()
    val inboxSize by agendaViewModel.inboxSize.collectAsState()

    val now = LocalDate.now()
    val weekCalendarState = rememberWeekCalendarState(
        startDate = YearMonth.now().minusMonths(100).atStartOfMonth(),
        endDate = YearMonth.now().plusMonths(100).atEndOfMonth(),
        firstVisibleWeekDate = now,
        firstDayOfWeek = daysOfWeek(DayOfWeek.MONDAY).first()
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.material.Surface(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceDim),
            elevation = 3.dp
        ) {
            Column(
                Modifier
                    .bottomElevation()
                    .background(MaterialTheme.colorScheme.surfaceDim)
            ) {
                AnimatedVisibility(
                    visible = displayMode == AgendaDisplayMode.CALENDAR,
                    enter = slideInHorizontally() ,
//                    exit = slideOutHorizontally(),
                ) {
                    WeekCalendarView(
                        now = now,
                        weekCalendarState = weekCalendarState,
                        selectedDay = selectedDay,
                        changeSelectedDay = agendaViewModel::changeSelectedDay,
                        isEventOnDayOfWeek = agendaViewModel::isEventOnDayOfWeek,
                        inboxSize = inboxSize
                    )
                }

                AnimatedVisibility(
                    visible = displayMode != AgendaDisplayMode.CALENDAR,
                    enter = slideInHorizontally() ,
//                    exit = slideOutHorizontally(),
                ) {
                    WeekScheduleView(
                        now = now,
                        weekCalendarState = weekCalendarState,
                    )
                }

            }
        }
        if (displayMode == AgendaDisplayMode.CALENDAR) {
            CalendarContent(tasks, taskViewModel::updateCompletedStatus, taskViewModel::delete)
        } else {
            ScheduleContent()
        }
    }
}


