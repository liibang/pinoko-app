package cn.liibang.pinoko.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.liibang.pinoko.data.StringUUID
import java.time.LocalDateTime
import java.time.LocalTime

@Entity("course")
data class CoursePO(
    @PrimaryKey override val id: StringUUID,
    @ColumnInfo(name = "term_id") val termId: String,
    val name: String,
    val color: String,

    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
): BasePO
