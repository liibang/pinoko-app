package cn.liibang.pinoko.ui.screen.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.liibang.pinoko.data.AppDatabase
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.entity.TermPO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TermViewModel @Inject constructor(internal val database: AppDatabase) : ViewModel() {

    val termList = database.termDao()
        .selectList()
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf<TermPO>())

    fun saveOrUpdate(po: TermPO) {
        viewModelScope.launch {
            database.termDao().insertOrUpdate(po)
        }
    }

    suspend fun fetchById(id: StringUUID): TermPO? = database.termDao().selectById(id)

}