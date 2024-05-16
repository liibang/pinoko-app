package cn.liibang.pinoko.ui.screen.focus

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.dao.FocusRecordDao
import cn.liibang.pinoko.data.dao.SettingDao
import cn.liibang.pinoko.data.dao.TaskDao
import cn.liibang.pinoko.data.entity.FocusRecordPO
import cn.liibang.pinoko.model.FocusRecordVO
import cn.liibang.pinoko.service.FocusNotifier
import cn.liibang.pinoko.ui.support.generateUUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject


data class FocusTodayData(val tomatoCount: Int, val totalDuration: Duration)

data class FocusTaskVO(val id: StringUUID, val name: String)

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FocusViewModel @Inject constructor(
//    private val appDatabase: AppDatabase,
    private val focusRecordDao: FocusRecordDao,
    private val settingDao: SettingDao,
    private val taskDao: TaskDao,
    private val focusNotifier: FocusNotifier
) : ViewModel() {


//    val setting = settingDao.selectOneByFlow()
//        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // 时钟区域
    var usedDuration by mutableStateOf(Duration.ofMinutes(0))
        private set

    var targetDuration by mutableStateOf(Duration.ofMinutes(25))
        private set

    var restDuration by mutableStateOf(Duration.ofMinutes(5))
        private set

    var focusRecordState by mutableStateOf(getNewFocusRecordPO(targetDuration))
        private set

    var playState by mutableStateOf(PlayState.STOP)
        private set

    var _task by mutableStateOf<FocusTaskVO?>(null)
        private set



    // 计时逻辑
    init {
        // 设置targetDuration
        viewModelScope.launch {
            settingDao.selectOneByFlow().collectLatest {
                targetDuration = it.focusTomatoDuration
                focusRecordState = getNewFocusRecordPO(it.focusTomatoDuration)
                restDuration = it.focusRestDuration
            } // 从数据库中读取数据的操作
        }

        tryToStartTimer()
    }

    fun updateTask(task: FocusTaskVO?) {
        this._task = task
        focusRecordState = focusRecordState.copy(taskId = task?.id)
    }

    fun changePlayState(playState: PlayState) {
        this.playState = playState
    }

    fun updateTargetDuration(duration: Duration) {
        this.targetDuration = duration
    }

    fun updateUsedDuration(duration: Duration) {
        this.usedDuration = duration
    }

    fun updateFocusRecordState(focusRecord: FocusRecordPO) {
        if (focusRecord.taskId == null) {
            _task = null
        }
        focusRecordState = focusRecord
    }

    fun getNewFocusRecordPO(focusDuration: Duration) = FocusRecordPO(
        id = "",
        startAt = LocalDateTime.MIN,
        endAt = LocalDateTime.MIN,
        taskId = null,
        note = "",
        focusDuration = Duration.ZERO,
        createdAt = LocalDateTime.MIN,
        updatedAt = LocalDateTime.MIN
    )


    @RequiresApi(Build.VERSION_CODES.S)
    private fun tryToStartTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                if ((playState == PlayState.PROCEED || playState == PlayState.REST) && usedDuration.seconds < targetDuration.seconds) {
                    usedDuration = usedDuration.plus(Duration.ofSeconds(1L))
                } else if (usedDuration.seconds == targetDuration.seconds) {
                    if (playState == PlayState.PROCEED) {
                        // 顺利结束就保存 专注了多少时长
                            saveRecord(
                                focusRecordState.copy(
                                    endAt = LocalDateTime.now(),
                                    focusDuration = Duration.ofSeconds(usedDuration.seconds)
                                ),
                                onSave = { focusRecordState = it }
                            )
                        playState = PlayState.FINISH
                        notifyMember("您刚收获了一个番茄")
                    } else {
                        // 当前可能是休息模式 所以设置一下
                        val setting =  settingDao.selectOne()
                        restDuration = setting.focusRestDuration
                        targetDuration = setting.focusTomatoDuration
                        focusRecordState = focusRecordState.copy(note = "")
                        playState = PlayState.STOP
                        notifyMember("休息完毕~")
                    }
                    usedDuration = Duration.ofSeconds(0)
                }
            }
        }
    }


    val todayFocusData: StateFlow<FocusTodayData> =
        MutableStateFlow(LocalDateTime.now().toLocalDate().atStartOfDay())
            .flatMapLatest {
                focusRecordDao.countTodayData(it)
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, FocusTodayData(0, Duration.ZERO))


    val records: StateFlow<List<FocusRecordVO>> = focusRecordDao.selectVOList()
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    @RequiresApi(Build.VERSION_CODES.S)
    fun saveRecord(focusRecord: FocusRecordPO, onSave: (FocusRecordPO) -> Unit) {
        viewModelScope.launch {
            val now = LocalDateTime.now()
            // 填充必要属性
            val po = covertToSaveFocusRecord(focusRecord, now)
            // 保存记录
            focusRecordDao.insert(po)
            // 执行回调
            onSave(po)
        }
    }

    fun updateRecord(id: StringUUID, note: String, taskId: StringUUID?) {
        viewModelScope.launch {
            focusRecordDao.modify(id, note, taskId)
        }
    }

    fun delete(id: StringUUID) {
        viewModelScope.launch {
            focusRecordDao.delete(id)
        }
    }


    private fun covertToSaveFocusRecord(
        focusRecord: FocusRecordPO,
        now: LocalDateTime
    ): FocusRecordPO {
        val po = focusRecord.copy(
            id = generateUUID(),
            createdAt = now,
            updatedAt = now
        )
        return po
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun notifyMember(message: String) {
        focusNotifier.sendMessage(message)
    }

    suspend fun getByID(id: StringUUID): FocusRecordPO? {
        return focusRecordDao.selectById(id)
    }

    suspend fun getRecordLinkTaskName(taskId: StringUUID?): String {
        return taskId?.let { taskDao.selectById(it)?.name } ?: ""
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun batchSaveRecord(formState: FocusRecordFormState) {
        viewModelScope.launch {
            // 计算出endAt
            (1..formState.tomatoCount).forEach { index ->
                // 计算出开始时间和结束时间
                val startAt =
                    formState.startAt.plusSeconds((index - 1) * formState.tomaDuration.toSeconds())
                val endAt =
                    formState.startAt.plusSeconds(index * formState.tomaDuration.toSeconds())
                focusRecordDao.insert(
                    FocusRecordPO(
                        id = generateUUID(),
                        startAt = startAt,
                        endAt = endAt,
                        taskId = formState.taskId,
                        note = if (index == formState.tomatoCount) formState.note else "",
                        focusDuration = formState.tomaDuration,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
            }
        }
    }



    fun updateDurationSetting(tomatoDuration: Duration, restDuration: Duration) {
        targetDuration = tomatoDuration
        this.restDuration = restDuration
    }

}