package cn.liibang.pinoko.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.liibang.pinoko.data.StringUUID
import java.time.LocalDateTime

@Entity("course_detail")
data class CourseDetailPO(
    @PrimaryKey override val id: StringUUID,
    @ColumnInfo(name = "course_id") val courseId: String,
    val weeks: String,
    @ColumnInfo(name = "day_of_week") val dayOfWeek: Int,
    val location: String,
    val teacher: String,
    @ColumnInfo("lesson_start_at") val lessonStartAt: Int,
    @ColumnInfo("lesson_end_at") val lessonEndAt: Int,

    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
): BasePO