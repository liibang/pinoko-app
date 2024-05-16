package cn.liibang.pinoko.ui.screen.agenda

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.liibang.pinoko.ui.screen.agenda.calendar.WeekCalendarView
import cn.liibang.pinoko.ui.screen.agenda.calendar.WeekScheduleView
import cn.liibang.pinoko.ui.screen.course.CourseViewModel
import cn.liibang.pinoko.ui.screen.main.AgendaDisplayMode
import cn.liibang.pinoko.ui.screen.setting.SettingViewModel
import cn.liibang.pinoko.ui.screen.task.TaskViewModel
import cn.liibang.pinoko.ui.screen.term.TermViewModel
import cn.liibang.pinoko.ui.support.bottomElevation
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun AgendaScreen(
    agendaDisplayMode: AgendaDisplayMode,
    agendaViewModel: AgendaViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel(),
    courseViewModel: CourseViewModel = hiltViewModel(),
    settingViewModel: SettingViewModel,
    termViewModel: TermViewModel,
) {

    val tasks by agendaViewModel.tasks.collectAsState()
    val courses by agendaViewModel.courses.collectAsState()
    val setting by settingViewModel.setting.collectAsState()
    val selectedDay by agendaViewModel.selectedDay.collectAsState()
    val inboxSize by agendaViewModel.inboxSize.collectAsState()
    val hasDataDatesOnWeek by agendaViewModel.hasDataDatesOnWeek.collectAsState()


    val now = LocalDate.now()
    val weekCalendarState = rememberWeekCalendarState(
        startDate = YearMonth.now().minusMonths(100).atStartOfMonth(),
        endDate = YearMonth.now().plusMonths(100).atEndOfMonth(),
        firstVisibleWeekDate = now,
        firstDayOfWeek = daysOfWeek(DayOfWeek.MONDAY).first()
    )

    val weekMap = weekCalendarState.firstVisibleWeek.days.associate { it.date.dayOfWeek.value to it.date }

    val hasCourseOnWeek = agendaViewModel.coursesOfAWeek.collectAsState()
        .value
        .asSequence()
        .map { it.details }
        .flatten()
        .map { it.dayOfWeek }
        .filter {   weekMap.containsKey(it)   }
        .map { weekMap[it]!! }
        .toList()

    val hasTaskAndCourseOnDateOfWeek = listOf(hasDataDatesOnWeek, hasCourseOnWeek).flatten()

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
                    visible = agendaDisplayMode == AgendaDisplayMode.CALENDAR,
                    enter = slideInHorizontally(),
//                    exit = slideOutHorizontally(),
                ) {
                    WeekCalendarView(
                        now = now,
                        weekCalendarState = weekCalendarState,
                        selectedDay = selectedDay,
                        changeSelectedDay = agendaViewModel::changeSelectedDay,
                        hasTaskAndCourseOnDateOfWeek = hasTaskAndCourseOnDateOfWeek,
                        inboxSize = inboxSize,
                    )
                }

                AnimatedVisibility(
                    visible = agendaDisplayMode != AgendaDisplayMode.CALENDAR,
                    enter = slideInHorizontally(),
//                    exit = slideOutHorizontally(),
                ) {
                    WeekScheduleView(
                        now = now,
                        weekCalendarState = weekCalendarState,
                        termSetId = setting!!.termSetId,
                        changeSelectedDay = agendaViewModel::changeSelectedDay,
                        fetchTermById = termViewModel::fetchById,
                        selectedDay = selectedDay
                    )
                }

            }
        }
        if (agendaDisplayMode == AgendaDisplayMode.CALENDAR) {
            CalendarContent(
                tasks,
                taskViewModel::updateCompletedStatus,
                taskViewModel::delete,
                courses,
                courseViewModel::deleteCourseByCourseId,
                selectedDay
            )
        } else {
//            val tasksOfWeek by agendaViewModel.tasksOfAWeek.collectAsState()
            val coursesOfAWeek by agendaViewModel.coursesOfAWeek.collectAsState()
            ScheduleContent(coursesOfAWeek)
        }
    }
}


