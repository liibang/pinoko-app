package cn.liibang.pinoko.ui.screen.stats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.liibang.pinoko.data.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject


data class DateRange(val startAt: LocalDateTime, val endAt: LocalDateTime) {
    fun previousPeriod(): DateRange {
        val period = Duration.between(startAt, endAt)
        return DateRange(startAt.minus(period), endAt.minus(period))
    }
}

data class TaskAchievementVO(val completedCount: Int = 0, val taskCount: Int = 0)

data class TaskAchievementInfo(
    val completedCount: Int = 0,
    val taskCount: Int = 0,
    val previousTaskCount: Int = 0,
    val previousCompletedCount: Int = 0,
) {
    fun getCompletionRate(): Float {
        return if (taskCount != 0) {
            (completedCount.toFloat() / taskCount.toFloat()) * 100
        } else {
            0.0f
        }
    }

    fun getPreviousCompletionRate(): Float {
        return if (previousTaskCount != 0) {
            (previousCompletedCount.toFloat() / previousTaskCount.toFloat()) * 100
        } else {
            0.0f
        }
    }

    fun getCompletionGrowthRate(): Float {
        val currentCompletionRate = getCompletionRate()
        val previousCompletionRate = getPreviousCompletionRate()

        return if (previousCompletionRate != 0.0f) {
            ((currentCompletionRate - previousCompletionRate) / previousCompletionRate) * 100f
        } else if (currentCompletionRate != 0.0f) {
            currentCompletionRate
        } else {
            0.0f
        }
    }
}

data class TaskWeekStatVO(val weekValue: Int, val taskCount: Int)

data class FocusDurationWeekStatVO(val weekValue: Int, val focusDuration: Duration)

data class TomatoFocusAchievementVO(
    val tomatoCount: Int = 0,
    val focusDuration: Duration = Duration.ZERO,
)

data class TomatoFocusAchievementInfo(
    val tomatoCount: Int = 0,
    val focusDuration: Duration = Duration.ZERO,
    val previousTomatoCount: Int = 0,
    val previousFocusDuration: Duration = Duration.ZERO
)

data class DateRangeItem(
    val desc: String,
    val getDateRange: () -> DateRange,
)

@HiltViewModel
class StatsViewModel @Inject constructor(private val database: AppDatabase) : ViewModel() {

    var _customerDateRange = mutableStateOf(
        DateRange(
            LocalDate.now().atTime(LocalTime.MIN),
            LocalDate.now().atTime(LocalTime.MAX)
        )
    )
        private set

    fun changeCustomerDateRange(dateRange: DateRange) {
        _customerDateRange.value = dateRange
    }

    internal val dateRangeItems = listOf(
        DateRangeItem(
            "最近七天",
            getDateRange = {
                LocalDate.now().run {
                    DateRange(
                        startAt = minusWeeks(1).atTime(LocalTime.MIN),
                        endAt = atTime(LocalTime.MAX)
                    )
                }
            },
        ),
        DateRangeItem(
            desc = "最近1个月",
            getDateRange = {
                LocalDate.now()
                    .run { DateRange(atTime(LocalTime.MIN).minusMonths(1), atTime(LocalTime.MAX)) }
            },
        ),
        DateRangeItem(
            desc = "最近3个月",
            getDateRange = {
                LocalDate.now()
                    .run { DateRange(atTime(LocalTime.MIN).minusMonths(3), atTime(LocalTime.MAX)) }
            },
        ),
        DateRangeItem(
            desc = "最近半年",
            getDateRange = {
                LocalDate.now()
                    .run { DateRange(atTime(LocalTime.MIN).minusMonths(6), atTime(LocalTime.MAX)) }
            },
        ),
        DateRangeItem(
            desc = "最近1年",
            getDateRange = {
                LocalDate.now()
                    .run { DateRange(atTime(LocalTime.MIN).minusYears(1), atTime(LocalTime.MAX)) }
            },
        ),
        DateRangeItem("自定义范围", getDateRange = { _customerDateRange.value }),
    )

    fun changeDateRangeItem(tabIndex: Int) {
        _selectedMenuIndex = tabIndex
        _dateRange.value = dateRangeItems[_selectedMenuIndex].getDateRange()
    }

    var _selectedMenuIndex by mutableStateOf(0)
        private set

    private var _dateRange = MutableStateFlow(
        dateRangeItems[_selectedMenuIndex].getDateRange()
    )

    var _selectedTabIndex = mutableStateOf(0)
        private set


    fun changeTab(tabIndex: Int) {
        _selectedTabIndex.value = tabIndex
    }

    // ================番茄统计逻辑

    val totalTomatoCount = database.taskDao().countTotalTomatoCount()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)


    @OptIn(ExperimentalCoroutinesApi::class)
    val focusDurationWeekStatVO = _dateRange
        .flatMapLatest {
            // 查询达成量和工作量
            database.focusRecordDao().countFocusDurationOnWeek(it.startAt, it.endAt)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    @OptIn(ExperimentalCoroutinesApi::class)
    val tomatoFocusAchievement = _dateRange
        .flatMapLatest {
            combine(
                database.focusRecordDao().countAchievement(it.startAt, it.endAt),
                it.previousPeriod()
                    .run { database.focusRecordDao().countAchievement(startAt, endAt) },
            ) { current, previous ->
                TomatoFocusAchievementInfo(
                    tomatoCount = current.tomatoCount,
                    focusDuration = current.focusDuration,
                    previousTomatoCount = previous.tomatoCount,
                    previousFocusDuration = previous.focusDuration
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, TomatoFocusAchievementInfo())


    @OptIn(ExperimentalCoroutinesApi::class)
    val todayTomatoFocusAchievement = combine(
        database.focusRecordDao().countAchievement(
            startAt = LocalDate.now().atTime(LocalTime.MIN),
            endAt = LocalDate.now().atTime(LocalTime.MAX)
        ),
        database.focusRecordDao().countAchievement(
            startAt = LocalDate.now().atTime(LocalTime.MIN).minusDays(1),
            endAt = LocalDate.now().atTime(LocalTime.MAX).minusDays(1)
        )
    ) { current, previous ->
        TomatoFocusAchievementInfo(
            tomatoCount = current.tomatoCount,
            focusDuration = current.focusDuration,
            previousTomatoCount = previous.tomatoCount,
            previousFocusDuration = previous.focusDuration
        )
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, TomatoFocusAchievementInfo())

    // 任务统计逻辑=========================

    @OptIn(ExperimentalCoroutinesApi::class)
    val todayTaskAchievement = combine(
        database.taskDao().countTaskAchievement(
            startAt = LocalDate.now().atTime(LocalTime.MIN),
            endAt = LocalDate.now().atTime(LocalTime.MAX)
        ),
        database.taskDao().countTaskAchievement(
            startAt = LocalDate.now().atTime(LocalTime.MIN).minusDays(1),
            endAt = LocalDate.now().atTime(LocalTime.MAX).minusDays(1)
        )
    ) { todayTaskAchievement, lastDayTaskAchievement ->
        TaskAchievementInfo(
            completedCount = todayTaskAchievement.completedCount,
            taskCount = todayTaskAchievement.taskCount,
            previousCompletedCount = lastDayTaskAchievement.completedCount,
            previousTaskCount = lastDayTaskAchievement.taskCount
        )
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, TaskAchievementInfo())


    @OptIn(ExperimentalCoroutinesApi::class)
    val taskAchievement = _dateRange
        .flatMapLatest {
            combine(
                database.taskDao().countTaskAchievement(it.startAt, it.endAt),
                it.previousPeriod().run {
                    database.taskDao().countTaskAchievement(startAt, endAt)
                }) { current, previous ->

                TaskAchievementInfo(
                    completedCount = current.completedCount,
                    taskCount = current.taskCount,
                    previousCompletedCount = previous.completedCount,
                    previousTaskCount = previous.taskCount
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, TaskAchievementInfo())

    // 本周
    @OptIn(ExperimentalCoroutinesApi::class)
    val workloadOnWeek = _dateRange
        .flatMapLatest {
            // 查询达成量和工作量
            database.taskDao().countWorkloadOnWeek(it.startAt, it.endAt)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    @OptIn(ExperimentalCoroutinesApi::class)
    val completedTaskCount = database.taskDao().countCompletedTaskCount()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

}
