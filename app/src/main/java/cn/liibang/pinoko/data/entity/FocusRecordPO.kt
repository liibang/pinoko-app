package cn.liibang.pinoko.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.liibang.pinoko.data.StringUUID
import java.time.Duration
import java.time.LocalDateTime

@Entity("focus_record")
data class FocusRecordPO(
    @PrimaryKey override val id: StringUUID,
    @ColumnInfo(name = "start_at") val startAt: LocalDateTime,
    @ColumnInfo(name = "end_at") val endAt: LocalDateTime,
//    @ColumnInfo(name = "tomato_count") val tomatoCount: Int,
//    @ColumnInfo(name = "tomato_duration") val tomatoDuration: Duration,
    @ColumnInfo(name = "task_id") val taskId: StringUUID?, // 绑定任务ID（可选）
    @ColumnInfo(name = "note", defaultValue = "") val note: String = "", // 笔记
    @ColumnInfo(name = "focus_duration") val focusDuration: Duration, // 笔记
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
) : BasePO
