package cn.liibang.pinoko.ui.screen.main



import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cn.liibang.pinoko.service.AlarmScheduler
import cn.liibang.pinoko.data.AppDatabase
import cn.liibang.pinoko.data.DataStore
import cn.liibang.pinoko.http.MemberDO

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


enum class AgendaDisplayMode {
    CALENDAR, SCHEDULE,
}

enum class TaskDisplayMode {
    LIST, FOUR,
}

enum class FocusDisplayMode {
    FOCUS, LIST,
}


@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {


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


    var focusDisplayMode by mutableStateOf(FocusDisplayMode.LIST)
        private set

    fun switchFocusDisplayMode() {
        focusDisplayMode = if (focusDisplayMode == FocusDisplayMode.LIST) {
            FocusDisplayMode.FOCUS
        } else {
            FocusDisplayMode.LIST
        }
    }

    init {
//        alarmScheduler.schedule(AlarmItem(id = "TEST123",alarmTime = LocalDateTime.now().plusSeconds(12), message = "ok i got u"))
    }

    fun scanNotify() {
//        appDatabase.taskDao().selectJob()
    }

}