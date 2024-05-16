package cn.liibang.pinoko.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class Table {
    TASK, TASK_CATEGORY, TERM, COURSE, COURSE_DETAIL, FOCUS_RECORD, SETTING, HABIT
}

enum class Operation {
    INSERT, UPDATE, DELETE, INSERT_OR_UPDATE
}


@Entity(
    tableName = "change_log",)
//    indices = [Index(value = ["uuid", "table", "operation"], unique = true)])
data class ChangeLogPO(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val uuid: String,
    val timestamp: Long,
    // // 这里设置唯一索引
    val table: Table,
    // 这里设置唯一索引
    val operation: Operation
)

