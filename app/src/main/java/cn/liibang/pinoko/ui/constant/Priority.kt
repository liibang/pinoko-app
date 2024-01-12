package cn.liibang.pinoko.ui.constant

import androidx.compose.ui.graphics.Color

enum class Priority(val code: Int, val color: Color, val desc: String, val detail: String) {
    HIGH(3, Color(224, 61, 56), "高优先级", "重要且紧急"),
    MEDIUM(2, Color(255, 176, 1), "中优先级", "重要不紧急"),
    LOW(1, Color(71, 115, 250), "低优先级", "不重要但紧急"),
    NONE(0, Color(163, 163, 163), "无优先级", "不重要不紧急")
}

fun priorityColor(id: Int): Color {
    return Priority.values().find { it.code == id }!!.color
}