package cn.liibang.pinoko.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.liibang.pinoko.data.StringUUID
import java.time.LocalDateTime
import java.time.LocalTime

@Entity("course")
data class CoursePO(

    @PrimaryKey
    val id: StringUUID,
    @ColumnInfo(name = "term_id") val termId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "color") val color: String,
    @ColumnInfo(name = "start_at") val startAt: LocalTime,
    @ColumnInfo(name = "end_at") val endAt: LocalTime,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "teacher") val teacher: String,


    @ColumnInfo(name = "created_at") val createdAt: LocalDateTime,
    @ColumnInfo(name = "updated_at") val updatedAt: LocalDateTime,
)
