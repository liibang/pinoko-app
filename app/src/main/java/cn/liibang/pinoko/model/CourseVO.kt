package cn.liibang.pinoko.model

import cn.liibang.pinoko.data.StringUUID
import java.time.LocalTime

data class CourseVO(
    val id: StringUUID,
    val name: String,
    val color: String,
    val details: List<CourseDetailVO>
)

data class CourseDetailVO(
    val id: StringUUID,
    val courseId: String,
    val weeks: String,
    val dayOfWeek: Int,
    val location: String,
    val teacher: String,
    val lessonStartAt: LocalTime,
    val lessonEndAt: LocalTime
)