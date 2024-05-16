package cn.liibang.pinoko.ui.screen.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import cn.liibang.pinoko.service.AlarmItem
import cn.liibang.pinoko.service.AlarmScheduler
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.dao.FocusRecordDao
import cn.liibang.pinoko.data.dao.SettingDao
import cn.liibang.pinoko.data.dao.TaskDao
import cn.liibang.pinoko.data.entity.TaskPO
import cn.liibang.pinoko.data.entity.TaskSortMode
import cn.liibang.pinoko.model.TaskVO
import cn.liibang.pinoko.service.AlarmType
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
import java.time.temporal.ChronoUnit
import javax.inject.Inject


@HiltViewModel
class TaskViewModel @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val taskDao: TaskDao,
    private val settingDao: SettingDao,
    private val focusRecordDao: FocusRecordDao,
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
        searchValue, selectedCategoryID, settingDao.selectOneByFlow()
    ) { newSearchValue, newSelectedCategoryId, setting ->
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
        t.createdAt as createdAt,
        t.sort
        from task t left join task_category c on t.category_id = c.id
        where 1=1
        ${if (setting.taskShowCompleted) "" else "and t.completed = 0"}
        ${if (newSelectedCategoryId == "0") "" else if(newSelectedCategoryId == "-1") " and category_id is null "  else "and category_id = '$newSelectedCategoryId'"}
        ${if (newSearchValue.isEmpty()) "" else "and t.name like '%$newSearchValue%'"} 
        order by t.completed, ${
            when (setting.taskSortMode) {
                TaskSortMode.DUE_DATE_TIME -> "t.due_date desc, t.due_time desc"
                TaskSortMode.CREATED_ON_TOP -> "t.createdAt desc"
                TaskSortMode.CREATED_ON_BOTTOM -> "t.createdAt asc"
            }
        } 
    """
        taskDao.selectAll(SimpleSQLiteQuery(rawQuerySql))
    }
        .flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())


    fun updateCompletedStatus(taskId: String, completed: Boolean) {
        viewModelScope.launch {
            taskDao.updateTaskCompleted(taskId, completed)
        }
    }

    suspend fun detail(id: String): TaskPO {
        return taskDao.selectById(id)
    }

    fun saveOrUpdate(editMode: EditMode, task: TaskPO) {
        viewModelScope.launch {
            val now = LocalDateTime.now()
            // 如果是添加
            if (editMode == EditMode.CREATE)
                taskDao.insert(covertTOSavePO(task, now))
            // 如果是修改
            else taskDao.update(task.copy(updatedAt = now))
            // 设置提醒
            schedule(task)
        }
    }

    private fun covertTOSavePO(task: TaskPO, now: LocalDateTime): TaskPO {
        return task.copy(id = generateUUID(), createdAt = now, updatedAt = now)
    }


    fun delete(id: StringUUID) {
        viewModelScope.launch {
            taskDao.delete(id)
            // 取消关联的记录
            focusRecordDao._unlinkTask(taskId = id)
            // 这里饿汉的去取消事件 TODO
            alarmScheduler.cancel(id)
        }
    }


    private fun schedule(task: TaskPO) {
        if (task.reminderTime != null && task.dueDate!!.atTime(task.dueTime) >= LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)) {
            // 先取消 再设置
            alarmScheduler.cancel(task.id)
            alarmScheduler.schedule(
                AlarmItem(
                    id = task.id,
                    alarmTime = task.reminderTime,
                    message = task.name,
                    type = AlarmType.TASK
                )
            )
        } else {
            alarmScheduler.cancel(task.id)
        }
    }

    fun updateTaskPriority(id: StringUUID, priorityCode: Int) {
        viewModelScope.launch {
            taskDao.updatePriority(id, priorityCode)
        }
    }

    suspend fun fetchList(): List<TaskVO> {
        return taskDao.selectList()
    }

//    fun checkTask(id: StringUUID, isCheck: Boolean) {
//        viewModelScope.launch {
//            taskDao.updateTaskCompleted(id, if (isCheck) 1 else 0)
//        }
//    }

}