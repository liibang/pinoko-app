package cn.liibang.pinoko.model

import java.time.LocalDateTime

data class TaskCategoryVO(
    val id: String,
    val name: String,
    val color: Int,
    val sort: Int = 0,
    val createdAt: LocalDateTime,
    val taskCount: Int
)

data class TaskCategoryRecordVO(
    val id: String,
    val name: String,
    val color: Int,
    val sort: Int = 0,
    val createdAt: LocalDateTime,
    val taskCount: Int
)