package cn.liibang.pinoko.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.liibang.pinoko.data.StringUUID
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity("term")
data class TermPO(
    @PrimaryKey override val id: StringUUID,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "week_count") val weekCount: Int,
    @ColumnInfo(name = "start_date") val startDate: LocalDate,
//    @ColumnInfo(name = "lesson_info") val lessonInfo: LessonInfo, // json字段
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
) : BasePO

data class ClassTime(val startAt: LocalTime, val endAt: LocalTime)
data class LessonInfo(
    val maxCount: Int = 14,
    val classTimes: List<ClassTime?> = (1..maxCount).map { null }
)
