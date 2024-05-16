package cn.liibang.pinoko.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.liibang.pinoko.data.AppDatabase
import cn.liibang.pinoko.data.dao.ChangeLogDao
import cn.liibang.pinoko.data.entity.ChangeLogPO
import cn.liibang.pinoko.data.entity.CourseDetailPO
import cn.liibang.pinoko.data.entity.CoursePO
import cn.liibang.pinoko.data.entity.FocusRecordPO
import cn.liibang.pinoko.data.entity.HabitPO
import cn.liibang.pinoko.data.entity.Operation
import cn.liibang.pinoko.data.entity.SettingPO
import cn.liibang.pinoko.data.entity.Table
import cn.liibang.pinoko.data.entity.TaskCategoryPO
import cn.liibang.pinoko.data.entity.TaskPO
import cn.liibang.pinoko.data.entity.TermPO
import cn.liibang.pinoko.http.appApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

data class ChangeLogDTO(// // 这里设置唯一索引
    val tasks: MutableList<ChangeLogDO<TaskPO>?> = mutableListOf(),
    val taskCategories: MutableList<ChangeLogDO<TaskCategoryPO>?> = mutableListOf(),
    val terms: MutableList<ChangeLogDO<TermPO>?> = mutableListOf(),
    val courses: MutableList<ChangeLogDO<CoursePO>?> = mutableListOf(),
    val courseDetails: MutableList<ChangeLogDO<CourseDetailPO>?> = mutableListOf(),
    val focusRecords: MutableList<ChangeLogDO<FocusRecordPO>?> = mutableListOf(),
    val habits: MutableList<ChangeLogDO<HabitPO>?> = mutableListOf(),
    var setting: SettingPO? = null
)

data class ChangeLogDO<T>(
    val uuid: String,
    val table: Table,
    // 这里设置唯一索引
    val operation: Operation,
    val data: T?
)

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val changeLogDao: ChangeLogDao,
    val database: AppDatabase
) : ViewModel() {

    init {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("同步数据出现异常，稍后重试\n")
        }
        doPushDataToServer(handler)
    }

    private fun doPushDataToServer(handler: CoroutineExceptionHandler) {
        viewModelScope.launch(handler) {
            while (true) {
                delay(Duration.ofSeconds(10).toMillis())
                syncToServer()
            }
        }
    }

    suspend fun syncToServer(): Boolean {
        val timestamp = System.currentTimeMillis()
        val changelogs = changeLogDao.selectNeedSyncData(timestamp)
        println("\n=============开始同步数据================")
        println("数据量为${changelogs.size}条")
        if (changelogs.isEmpty()) return true

        // 分析变更关系，避免同步无效数据（操作）
        val map = mutableMapOf<String, ChangeLogPO>()

        changelogs.forEach {
            val key = it.uuid
            // 当前用户插入数据，但是又删除了的情况
            if (map.containsKey(key) && it.operation == Operation.DELETE) {
                map.remove(key)
            }
            // 当用户新增数据，但是又更新了数据
            else if (map.containsKey(key) && map[key]!!.operation == Operation.INSERT && it.operation == Operation.UPDATE) {
                map[key] = it.copy(operation = Operation.INSERT)
            } else {
                map[key] = it
            }
        }
        val changeLogDTO = ChangeLogDTO()

        println("过滤后的同步的数量：${map.values.size}")

        map.values.groupBy { it.table }.forEach { (table, logs) ->
            when (table) {
                Table.TASK -> handleLog(
                    logs,
                    database.taskDao()::selectById,
                    changeLogDTO.tasks
                )

                Table.TASK_CATEGORY -> handleLog(
                    logs,
                    database.taskCategoryDao()::selectById,
                    changeLogDTO.taskCategories
                )

                Table.TERM -> handleLog(
                    logs,
                    database.termDao()::selectById,
                    changeLogDTO.terms
                )

                Table.COURSE -> {
                    handleLog(
                        logs,
                        database.courseDao()::selectById,
                        changeLogDTO.courses
                    )
                    val courseIds = logs.map { it.uuid }
                    database.courseDetailDao().selectListInCourseId(courseIds)
                        .map {
                            ChangeLogDO(
                                table = Table.COURSE_DETAIL,
                                operation = Operation.INSERT_OR_UPDATE,
                                data = it,
                                uuid = it.id
                            )
                        }.let {
                            changeLogDTO.courseDetails.addAll(it)
                        }
                }
                Table.COURSE_DETAIL -> {}

                Table.FOCUS_RECORD -> handleLog(
                    logs,
                    database.focusRecordDao()::selectById,
                    changeLogDTO.focusRecords
                )
                Table.HABIT -> handleLog(
                    logs,
                    database.habitDao()::selectById,
                    changeLogDTO.habits
                )
                Table.SETTING -> changeLogDTO.setting = database.settingDao().selectOne()
            }
        }

        try {
            val result = appApi.syncData(changeLogDTO)
            return if (result.isSuccessful()) {
                // 成功后删除数据
                println("同步成功")
                changeLogDao.delete(timestamp)
                println("清理已同步的记录")
                true
            } else {
                println("上传数据到服务端失败")
                false
            }
        }catch (ex: Exception) {
            println("同步失败")
            println(ex)
            return false
        }

    }

    private suspend fun <T> handleLog(
        logs: List<ChangeLogPO>,
        daoMethod: suspend (String) -> T?, // 传递 DAO 方法的引用
        operationList: MutableList<ChangeLogDO<T>?>
    ) {
        logs.forEach { logEntry ->
            val data = when (logEntry.operation) {
                Operation.DELETE -> null
                else -> daoMethod(logEntry.uuid)
            }
            val changeLogDO = ChangeLogDO(
                table = logEntry.table,
                operation = if (data == null) Operation.DELETE else logEntry.operation,
                data = data,
                uuid = logEntry.uuid
            )
            operationList.add(changeLogDO)
        }
    }

//    map.values.groupBy { it.table }.forEach { (table, logs) ->
//
//        when (table) {
//            Table.TASK -> {
//                logs.forEach {
//                    val changeLogDO = ChangeLogDO(
//                        table = table,
//                        operation = Operation.INSERT_OR_UPDATE,
//                        data = if (it.operation == Operation.DELETE) null else database.taskDao().selectById(it.uuid)
//                    )
//                    changeLogDTO.tasks.add(changeLogDO)
//                }
//            }
//
//            Table.TASK_CATEGORY -> {
//                logs.forEach {
//                    val changeLogDO = ChangeLogDO(
//                        table = table,
//                        operation = Operation.INSERT_OR_UPDATE,
//                        data = if (it.operation == Operation.DELETE) null else database.taskCategoryDao().selectById(it.uuid)
//                    )
//                    changeLogDTO.taskCategories.add(changeLogDO)
//                }
//            }
//            Table.TERM -> {
//                logs.forEach {
//                    val changeLogDO = ChangeLogDO(
//                        table = table,
//                        operation = Operation.INSERT_OR_UPDATE,
//                        data = if (it.operation == Operation.DELETE) null else database.termDao().selectById(it.uuid)
//                    )
//                    changeLogDTO.terms.add(changeLogDO)
//                }
//            }
//            Table.COURSE -> {
//                logs.forEach {
//                    val changeLogDO = ChangeLogDO(
//                        table = table,
//                        operation = Operation.INSERT_OR_UPDATE,
//                        data = if (it.operation == Operation.DELETE) null else database.courseDao().selectById(it.uuid)
//                    )
//                    changeLogDTO.courses.add(changeLogDO)
//                }
//            }
//
//            Table.COURSE_DETAIL -> {
//                logs.forEach {
//                    val changeLogDO = ChangeLogDO(
//                        table = table,
//                        operation = Operation.INSERT_OR_UPDATE,
//                        data = if (it.operation == Operation.DELETE) null else database.courseDetailDao().selectById(it.uuid)
//                    )
//                    changeLogDTO.courseDetails.add(changeLogDO)
//                }
//            }
//            Table.FOCUS_RECORD -> {
//                logs.forEach {
//                    val changeLogDO = ChangeLogDO(
//                        table = table,
//                        operation = Operation.INSERT_OR_UPDATE,
//                        data = if (it.operation == Operation.DELETE) null else database.focusRecordDao().selectById(it.uuid)
//                    )
//                    changeLogDTO.focusRecords.add(changeLogDO)
//                }
//            }
//            Table.SETTING -> changeLogDTO.setting = database.settingDao().selectOne()
//        }
//    }


}