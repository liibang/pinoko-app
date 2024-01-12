package cn.liibang.pinoko.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.ui.theme.CategoryColor
import java.time.LocalDateTime

@Entity(tableName = "task_category")
data class TaskCategoryPO(
    @PrimaryKey val id: StringUUID,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "color") val color: Int = CategoryColor.DEFAULT_BLUE.code,
    @ColumnInfo(name = "sort", defaultValue = "0") val sort: Int = 0,
    // 通用字段
    @ColumnInfo(name = "created_at") val createAt: LocalDateTime,
    @ColumnInfo(name = "updated_at") val updatedAt: LocalDateTime,
)