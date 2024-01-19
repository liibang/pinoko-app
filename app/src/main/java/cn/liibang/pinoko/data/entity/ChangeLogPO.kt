package cn.liibang.pinoko.data.entity

import androidx.room.ColumnInfo
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

enum class OptMode(val code: Int) {
    INSERT(1), UPDATE(2), DELETE(3)
}

@Entity("change_log")
data class ChangeLogPO(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val uuid: String,
    val timestamp: Long,
    @ColumnInfo("opt_mode") val optMode: OptMode
)

