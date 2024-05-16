package cn.liibang.pinoko.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.ui.constant.Priority
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity("task")
data class TaskPO(
    @PrimaryKey override val id: StringUUID,
    @ColumnInfo(name = "category_id") val categoryId: String? = null,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(
        name = "priority",
        defaultValue = "0"
    ) val priority: Int = Priority.NONE.code,
    @ColumnInfo(name = "note") val note: String? = null,
    @ColumnInfo(name = "reminder_time") val reminderTime: LocalDateTime? = null,
    @ColumnInfo(name = "due_date") val dueDate: LocalDate? = null,
    @ColumnInfo(name = "due_time") val dueTime: LocalTime? = null,
    @ColumnInfo(name = "sort", defaultValue = "0") val sort: Int = 0,
    @ColumnInfo(name = "completed", defaultValue = "0") val completed: Boolean = false,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
): BasePO