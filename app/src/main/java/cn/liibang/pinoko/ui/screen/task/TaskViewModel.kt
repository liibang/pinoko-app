package cn.liibang.pinoko.ui.screen.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import cn.liibang.pinoko.AlarmItem
import cn.liibang.pinoko.AlarmScheduler
import cn.liibang.pinoko.data.AppDatabase
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.entity.TaskPO
import cn.liibang.pinoko.model.TaskVO
import cn.liibang.pinoko.ui.screen.category.DEFAULT_CATEGORY_ALL
import cn.liibang.pinoko.ui.support.generateUUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject


@HiltViewModel
class TaskViewModel @Inject constructor(
    val appDatabase: AppDatabase,
    val alarmScheduler: AlarmScheduler
) :
    ViewModel() {

    private val _selectedCategoryID = MutableStateFlow(DEFAULT_CATEGORY_ALL.id)
    val selectedCategoryID = _selectedCategoryID.asStateFlow()


    fun changeSelectedCategory(categoryID: String) {
        _selectedCategoryID.value = categoryID
    }


    private val _searchValue = MutableStateFlow("")
    val searchValue = _searchValue.asStateFlow()

    fun updateSearchValue(value: String) {
        _searchValue.value = value
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<TaskVO>> = combine(
        searchValue, selectedCategoryID
    ) { newSearchValue, newSelectedCategoryId ->
        val rawQuerySql = """
        SELECT
        t.id as id,
        t.name as name,
        t.due_date as dueDate,
        t.due_time as dueTime,
        t.reminder_time as remindTime,
        c.name as categoryName,
        c.color as categoryColor,
        t.priority,
        t.completed,
        t.created_at as createdAt,
        t.sort
        from task t left join task_category c on t.category_id = c.id
        where 1=1
        ${if (newSelectedCategoryId == "0") "" else "and category_id = '$newSelectedCategoryId'"}
        ${if (newSearchValue.isEmpty()) "" else "and t.name like '%$newSearchValue%'"} 
    """
        appDatabase.taskDao().selectAll(SimpleSQLiteQuery(rawQuerySql))
    }
        .flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())


    fun updateCompletedStatus(taskId: String, completed: Boolean) {
        viewModelScope.launch {
            appDatabase.taskDao().updateTaskCompleted(taskId, completed)
        }
    }

    suspend fun detail(id: String): TaskPO {
        return appDatabase.taskDao().selectById(id)
    }

    fun saveOrUpdate(editMode: EditMode, task: TaskPO) {
        viewModelScope.launch {
            appDatabase.taskDao().run {
                val now = LocalDateTime.now()
                if (editMode == EditMode.CREATE)
                    insert(
                        task.copy(
                            id = generateUUID(),
                            createdAt = now,
                            updatedAt = now
                        )
                    )
                else update(task.copy(updatedAt = now))
                schedule(task)
            }
        }
    }

    fun delete(id: StringUUID) {
        viewModelScope.launch {
            appDatabase.taskDao().delete(id)
            // 这里饿汉的去取消事件 TODO
            alarmScheduler.cancel(id)
        }
    }

    private fun schedule(task: TaskPO) {
        if (task.reminderTime != null) {
            // 先取消 再设置
            alarmScheduler.cancel(task.id)
            alarmScheduler.schedule(
                AlarmItem(
                    id = task.id,
                    alarmTime = task.reminderTime,
                    message = task.name
                )
            )
        } else {
            alarmScheduler.cancel(task.id)
        }
    }

}