package cn.liibang.pinoko.ui.screen.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.liibang.pinoko.data.AppDatabase
import cn.liibang.pinoko.model.TaskVO
import cn.liibang.pinoko.toTimestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed class AgendaEvent {
    data class ChangeSelectedDay(val newSelectedDay: LocalDate) : AgendaEvent()
}

//@HiltViewModel
class AgendaViewModel constructor(private val appDatabase: AppDatabase = AppDatabase.getDatabase()) :
    ViewModel() {

    private val _selectedDay = MutableStateFlow(LocalDate.now())
    val selectedDay = _selectedDay.asStateFlow()

    fun changeSelectedDay(day: LocalDate) {
        _selectedDay.value = day
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<TaskVO>> = _selectedDay
        .flatMapLatest {
            appDatabase.taskDao().selectByDueDate(it.toTimestamp())
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())


    val inboxSize: StateFlow<Int> = appDatabase.taskDao().countUndatedTask()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun updateCompletedStatus(taskId: String, completed: Boolean) {
        viewModelScope.launch {
            appDatabase.taskDao().updateTaskCompleted(taskId, completed)
        }
    }

    fun isEventOnDayOfWeek(date: LocalDate): StateFlow<Boolean> {
        return appDatabase
            .taskDao()
            .countTaskByDueDate(date)
            .map { it != null }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    }

}