package cn.liibang.pinoko.model

import cn.liibang.pinoko.data.StringUUID
import java.time.Duration
import java.time.LocalDateTime

data class FocusRecordVO(
    val id: StringUUID,
    val taskId: StringUUID?,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val taskName: String?,
    val focusDuration: Duration,
    val note: String
)
