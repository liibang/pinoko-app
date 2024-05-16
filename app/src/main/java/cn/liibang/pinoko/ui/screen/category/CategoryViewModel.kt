package cn.liibang.pinoko.ui.screen.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.liibang.pinoko.data.AppDatabase
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.dao.FocusRecordDao
import cn.liibang.pinoko.data.dao.TaskCategoryDao
import cn.liibang.pinoko.data.dao.TaskDao
import cn.liibang.pinoko.data.entity.TaskCategoryPO
import cn.liibang.pinoko.model.TaskCategoryVO
import cn.liibang.pinoko.ui.theme.CategoryColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

val DEFAULT_CATEGORY_ALL = TaskCategoryVO(
    id = "0",
    name = "全部",
    color = CategoryColor.DEFAULT_BLUE.code,
    sort = -1,
    createdAt = LocalDateTime.MAX,
    taskCount = 0
)

val DEFAULT_CATEGORY_NONE = TaskCategoryVO(
    id = "-1",
    name = "未分类",
    color = CategoryColor.DEFAULT_BLUE.code,
    sort = -2,
    createdAt = LocalDateTime.MAX,
    taskCount = 0
)


@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val taskCategoryDao: TaskCategoryDao,
    private val focusRecordDao: FocusRecordDao,
    private val appDatabase: AppDatabase,
    private val taskDao: TaskDao
) : ViewModel() {

    val taskCategories = taskCategoryDao
        .selectAndCountTask()
        .map { vos -> vos.toMutableList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    fun save(category: TaskCategoryPO) {
        viewModelScope.launch {
            val maxSort = taskCategoryDao.selectMaxSort()
            taskCategoryDao.save(category.copy(sort = maxSort + 1))
        }
    }

    fun update(category: TaskCategoryPO) {
        viewModelScope.launch {
            taskCategoryDao.update(category)
        }
    }

    fun updateCategorySort(list: List<Pair<String, Int>>) {
        viewModelScope.launch {
            list.forEach { (id, sort) ->
                taskCategoryDao.updateSort(id, sort, LocalDateTime.now())
            }
        }
    }

    fun delete(id: StringUUID) {
        appDatabase.transactionExecutor.execute {
            viewModelScope.launch {
                val taskIds = taskDao.selectIdsByCategoryId(categoryId = id)
                focusRecordDao._unlinkTaskByTaskIds(taskIds)
                taskDao._deleteByCategoryId(id)
                taskCategoryDao.delete(id)
            }
        }
    }

    suspend fun getByID(id: StringUUID): TaskCategoryPO {
        return taskCategoryDao.selectById(id)
    }

}