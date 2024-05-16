package cn.liibang.pinoko.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.ui.theme.CategoryColor
import java.time.LocalDateTime

@Entity(tableName = "task_category")
data class TaskCategoryPO(
    @PrimaryKey override val id: StringUUID,
    val name: String,
    val color: Int = CategoryColor.DEFAULT_BLUE.code,
    @ColumnInfo(defaultValue = "0") val sort: Int = 0,
    // 通用字段
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
) : BasePO