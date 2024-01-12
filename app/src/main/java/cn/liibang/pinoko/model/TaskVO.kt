package cn.liibang.pinoko.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class TaskVO(
    val id: String,
    val name: String,
    val dueDate: LocalDate?, // 这里我假设dueDate是一个字符串，你可能需要根据实际情况调整类型
    val dueTime: LocalTime?, // 同上
    val remindTime: LocalDateTime?, // 同上
    val categoryName: String?,
    val categoryColor: Int?, // 如果颜色是以整数形式存储的，你可能需要将这个字段的类型改为Int
    val priority: Int,
    val completed: Boolean, // 如果completed是以整数形式存储的，你可能需要将这个字段的类型改为Int
    val createdAt: String, // 同dueDate
    val sort: Int
)