package cn.liibang.pinoko.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.liibang.pinoko.data.StringUUID
import java.time.LocalDate
import java.time.LocalDateTime

@Entity("term")
data class TermPO (
    @PrimaryKey
    val id: StringUUID,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "weeks") val weeks: Int,
    @ColumnInfo(name = "start_date") val startDate: LocalDate,

    @ColumnInfo(name = "created_at") val createdAt: LocalDateTime,
    @ColumnInfo(name = "updated_at") val updatedAt: LocalDateTime,
)