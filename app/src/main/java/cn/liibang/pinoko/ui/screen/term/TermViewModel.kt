package cn.liibang.pinoko.ui.screen.term

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.liibang.pinoko.data.AppDatabase
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.dao.CourseDao
import cn.liibang.pinoko.data.dao.CourseDetailDao
import cn.liibang.pinoko.data.dao.TermDao
import cn.liibang.pinoko.data.entity.TermPO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TermViewModel @Inject constructor(
    private val termDao: TermDao,
    private val courseDao: CourseDao,
    private val courseDetailDao: CourseDetailDao,
    private val appDatabase: AppDatabase,
) : ViewModel() {

    val termList = termDao
        .selectList()
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    fun saveOrUpdate(po: TermPO, isCreate: Boolean) {
        viewModelScope.launch {
            if (isCreate) {
                termDao.insert(po)
            } else {
                termDao.update(po)
            }
        }
    }

    suspend fun fetchById(id: StringUUID): TermPO? = termDao.selectById(id)

    // TODO 测试
    fun delete(termPO: TermPO) {
        appDatabase.transactionExecutor.execute {
            viewModelScope.launch {
                termDao.delete(termPO)
                val courseIds = appDatabase.courseDao().selectByTermId(termPO.id).map { it.id }
                courseDao._deleteByTermId(termPO.id)
                courseDetailDao._deleteInCourseIds(courseIds)
            }
        }
    }

}