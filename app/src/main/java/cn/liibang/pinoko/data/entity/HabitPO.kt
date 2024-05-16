package cn.liibang.pinoko.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

enum class HabitType(val code: Int, val desc: String) {
    WEEKLY_SPECIFIC_DAYS(1, "固定"), MONTHLY_SPECIFIC_DAY(2, "按月"), EVERY_FEW_DAYS(3, "每隔几天"),
}

@Entity("habit")
data class HabitPO(
    @PrimaryKey override val id: String,
    val name: String,
    val type: HabitType,
    val value: String,
    @ColumnInfo("remind_time") val remindTime: LocalTime?,
    @ColumnInfo("start_at") val startAt: LocalDate,
    @ColumnInfo("created_at") override val createdAt: LocalDateTime,
    @ColumnInfo("updated_at") override val updatedAt: LocalDateTime,
) : BasePO