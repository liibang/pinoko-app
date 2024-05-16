package cn.liibang.pinoko.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
// 创建一个虚线分隔线
fun DashedHorizontalDivider(
    // 修饰符，默认为Modifier
    modifier: Modifier = Modifier,
    // 分隔线的颜色，默认为红色
    color: Color = Color.Gray,
    // 虚线的宽度，默认为10f
    dashWidth: Float = 10f,
    // 虚线的间隔，默认为10f
    dashGap: Float = 10f,
    // 线条的宽度，默认为1f
    strokeWidth: Float = 1f,
) {
    val strokeHeightDp = with(LocalDensity.current) { strokeWidth.toDp() }
    // 创建虚线的效果
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth, dashGap), 0f)
    androidx.compose.foundation.Canvas(
        modifier
            .fillMaxWidth()
            .height(strokeHeightDp)
    ) {
        // 绘制虚线
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = strokeWidth,
            pathEffect = pathEffect
        )
    }
}

@Composable
// 创建一个竖直的虚线分隔线
fun DashedDividerVertical(
    // 分隔线的颜色，默认为红色
    color: Color = Color.Gray,
    // 虚线的宽度，默认为10f
    dashWidth: Float = 10f,
    // 虚线的间隔，默认为10f
    dashGap: Float = 10f,
    // 线条的宽度，默认为1f
    strokeWidth: Float = 1f,
    // 修饰符，默认为Modifier
    modifier: Modifier = Modifier,
    height: Dp
) {
    // 将 strokeWidth 的浮点数像素值转换为 DP 单位
    val strokeWidthDp = with(LocalDensity.current) { strokeWidth.toDp() }
    println("strokeWidthDp: $strokeWidthDp")
    // 创建虚线的效果
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth, dashGap), 0f)
    androidx.compose.foundation.Canvas(
        modifier
            .height(height)
            .width(strokeWidthDp)) {
        // 绘制虚线
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            strokeWidth = strokeWidth,
            pathEffect = pathEffect
        )
    }
}

