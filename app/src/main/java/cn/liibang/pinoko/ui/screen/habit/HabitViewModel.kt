package cn.liibang.pinoko.ui.screen.habit

import android.app.AlarmManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.liibang.pinoko.data.dao.HabitDao
import cn.liibang.pinoko.data.entity.HabitPO
import cn.liibang.pinoko.data.entity.HabitType
import cn.liibang.pinoko.service.AlarmItem
import cn.liibang.pinoko.service.AlarmScheduler
import cn.liibang.pinoko.service.AlarmType
import cn.liibang.pinoko.ui.support.generateUUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HabitViewModel @Inject constructor(private val habitDao: HabitDao, private val alarmScheduler: AlarmScheduler) : ViewModel() {

    val _selectedDate = MutableStateFlow(LocalDate.now())
    @OptIn(ExperimentalCoroutinesApi::class)
    val habits = _selectedDate
        .flatMapLatest { habitDao.selectList(it) }
        .map {
            it.filter { habitPO ->
                isInTodayByHabitType(_selectedDate.value, habitPO)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())


    @OptIn(ExperimentalCoroutinesApi::class)
    val allHabits = _selectedDate
        .flatMapLatest { habitDao.selectAll() }
        .stateIn(viewModelScope, SharingStarted.Lazily, listOf())


    fun changeSelectedDay(newDate: LocalDate) {
        _selectedDate.value = newDate
    }

    fun saveOrUpdate(habit: HabitPO) {
        viewModelScope.launch {
            val now = LocalDateTime.now()
            if (habit.id.isEmpty()) {
                habitDao.insert(
                    habit.copy(
                        createdAt = now,
                        updatedAt = now,
                        id = generateUUID()
                    )
                )
            } else {
                habitDao.update(habit.copy(updatedAt = now))
            }

            if (habit.id.isEmpty() || habit.remindTime == null) {
                alarmScheduler.cancel(habit.id)
                return@launch
            }

            // 判断是否在今天有闹钟需求
            alarmScheduler.scheduleHabit(habit)
        }
    }

    suspend fun fetchById(id: String): HabitPO? {
        return habitDao.selectById(id)
    }

    fun remove(habit: HabitPO) {
        viewModelScope.launch {
            habitDao.delete(habit)
            alarmScheduler.cancel(habit.id)
        }
    }
}

fun isInTodayByHabitType(selectedDay: LocalDate, habitPO: HabitPO): Boolean {
   return when(habitPO.type) {
        HabitType.WEEKLY_SPECIFIC_DAYS -> selectedDay >= habitPO.startAt && selectedDay.dayOfWeek.value.toString() in habitPO.value.split(",")
        HabitType.MONTHLY_SPECIFIC_DAY -> selectedDay.dayOfMonth == habitPO.value.toInt()
        HabitType.EVERY_FEW_DAYS -> isCurrentDayInInterval(
            startDate = habitPO.startAt,
            interval = habitPO.value.toInt(),
            selectedDay
        )
    }
}

fun isCurrentDayInInterval(startDate: LocalDate, interval: Int, currentDate: LocalDate): Boolean {
    val daysSinceStart = currentDate.toEpochDay() - startDate.toEpochDay()
    return daysSinceStart >= 0 && daysSinceStart % interval == 0L
}
