package cn.liibang.pinoko.ui.theme

import androidx.compose.ui.graphics.Color

enum class CategoryColor(val code: Int, val color: Color) {
    RED(2, Color(240, 86, 36).copy(0.9f)),
    YELLOW(3, Color(243, 198, 57)),
    GREEN(4, Color(130, 173, 68)),
    CYAN(5, Color(45, 145, 133)),
    DEFAULT_BLUE(1, Color(52, 144, 231)),
    PURPLE(6, Color(181, 155, 220));
}

fun categoryColor(colorId: Int?): Color {
    return CategoryColor.values().find { it.code == colorId }?.color ?: CategoryColor.DEFAULT_BLUE.color
}

fun categoryColor(colorId: Int): Color {
    return CategoryColor.values().find { it.code == colorId }?.color ?: CategoryColor.DEFAULT_BLUE.color
}

fun categoryColorEnum(colorId: Int): CategoryColor {
    return CategoryColor.values().find { it.code == colorId } ?: CategoryColor.DEFAULT_BLUE
}