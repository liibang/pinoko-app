package cn.liibang.pinoko.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.liibang.pinoko.data.StringUUID
import java.time.Duration
import java.time.LocalDateTime

@Entity(tableName = "setting")
data class SettingPO(
    @PrimaryKey override val id: StringUUID,
    val termSetId: StringUUID?, // 当前学期ID
    val memberId: String, // 会员ID
    val focusTomatoDuration: Duration, // 专注 番茄时长
    val focusRestDuration: Duration, // 专注 休息时长

    val taskShowCompleted: Boolean, // 任务 是否显示 已完成
    val taskSortMode: TaskSortMode, // 任务 排序方式
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
    ) : BasePO

enum class TaskSortMode(val code: Int) {
    DUE_DATE_TIME(1), CREATED_ON_TOP(2), CREATED_ON_BOTTOM(3)
}
