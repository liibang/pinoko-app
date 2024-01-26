package cn.liibang.pinoko.ui.screen.main



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cn.liibang.pinoko.AlarmScheduler
import cn.liibang.pinoko.data.AppDatabase

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


enum class AgendaDisplayMode {
    CALENDAR, SCHEDULE,
}

enum class TaskDisplayMode {
    LIST, FOUR,
}


@HiltViewModel
class MainViewModel @Inject constructor(val appDatabase: AppDatabase, val alarmScheduler: AlarmScheduler) : ViewModel() {

    var currentRoute by mutableStateOf(MainRouter.Agenda.route)
        private set

    fun changeRoute(route: String) {
        currentRoute = route
    }


    var agendaDisplayMode by mutableStateOf(AgendaDisplayMode.CALENDAR)
        private set

    fun switchAgendaDisplayMode() {
        agendaDisplayMode = if (agendaDisplayMode == AgendaDisplayMode.CALENDAR) {
            AgendaDisplayMode.SCHEDULE
        } else {
            AgendaDisplayMode.CALENDAR
        }
    }

    var taskDisplayMode by mutableStateOf(TaskDisplayMode.LIST)
        private set

    fun switchTaskDisplayMode() {
        taskDisplayMode = if (taskDisplayMode == TaskDisplayMode.LIST) {
            TaskDisplayMode.FOUR
        } else {
            TaskDisplayMode.LIST
        }
    }


    init {
//        alarmScheduler.schedule(AlarmItem(id = "TEST123",alarmTime = LocalDateTime.now().plusSeconds(12), message = "ok i got u"))
    }

    fun scanNotify() {
//        appDatabase.taskDao().selectJob()
    }

}