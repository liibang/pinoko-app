package cn.liibang.pinoko.ui.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.liibang.pinoko.data.dao.SettingDao
import cn.liibang.pinoko.data.entity.SettingPO
import cn.liibang.pinoko.data.entity.TaskSortMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val settingDao: SettingDao) : ViewModel() {

    val setting = settingDao.selectOneByFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingPO(
            id = "",
            termSetId = null,
            memberId = "",
            focusTomatoDuration = Duration.ofSeconds(10),
            focusRestDuration = Duration.ofSeconds(5),
            taskShowCompleted = true,
            taskSortMode = TaskSortMode.DUE_DATE_TIME,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        )

    fun saveOrUpdate(settingPO: SettingPO) {
        val po = if (settingPO.termSetId.isNullOrEmpty())
            settingPO.copy(termSetId = null) else settingPO
        viewModelScope.launch { settingDao.update(po) }
    }

}