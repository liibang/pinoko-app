package cn.liibang.pinoko.ui.screen.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.liibang.pinoko.data.AppDatabase
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
    LocalDateTime.MAX
)

@HiltViewModel
class CategoryViewModel @Inject constructor(private val appDataBase: AppDatabase) : ViewModel() {

    val taskCategories = appDataBase.taskCategoryDao()
        .selectAll()
        .map { vos ->
            vos.toMutableList().apply {
                sortWith(compareBy<TaskCategoryVO> { it.sort }.thenByDescending { it.createdAt })
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    fun save(category: TaskCategoryPO) {
        viewModelScope.launch { appDataBase.taskCategoryDao().save(category) }
    }

}