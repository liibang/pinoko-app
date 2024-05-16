package cn.liibang.pinoko.ui.screen.agenda

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.liibang.pinoko.model.CourseVO
import cn.liibang.pinoko.ui.component.DashedDividerVertical
import cn.liibang.pinoko.ui.component.DashedHorizontalDivider
import cn.liibang.pinoko.ui.screen.course.hexToColor
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.screen.main.SubRouter
import cn.liibang.pinoko.ui.theme.AppTheme
import java.time.Duration
import java.time.LocalTime
import java.time.temporal.ChronoUnit

private enum class BlockType {
    TASK, COURSE
}


private data class ScheduleBlockVO(
    val uuid: String,
    val title: String,
    val color: Color,
    val location: String,
    val teacher: String,
    val type: BlockType,
    val dayOfWeekNumber: Int,
    val startTime: LocalTime,
    val endTime: LocalTime
)

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ScheduleContent(coursesOfAWeek: List<CourseVO>) {

    // 查询出本周的所有任务 和 课程

    // 1.查询课程要先 查询学期，知道现在是第几周

    // 2.查询任务知道现在日历的state

//    val weekTaskMap = tasksOfWeek
//        .filter { it.dueTime != null }
//        .groupBy (
////            keySelector = { "${it.dueDate!!.dayOfWeek.value}:${it.dueTime!!.hour}" },
//            keySelector = { it.dueDate!!.dayOfWeek.value },
//            valueTransform = { toScheduleBlockVO(it) }
//        )

    val weekCourseMap = coursesOfAWeek
        .map { po ->
            po.details.map { detail ->
                ScheduleBlockVO(
                    uuid = po.id,
                    title = po.name,
                    color = hexToColor(po.color),
                    type = BlockType.COURSE,
                    dayOfWeekNumber = detail.dayOfWeek,
                    startTime = detail.lessonStartAt,
                    endTime = detail.lessonEndAt,
                    location = detail.location,
                    teacher = detail.teacher,
                )
            }
        }
        .flatten().groupBy(
            keySelector = { it.dayOfWeekNumber },
            valueTransform = { it }
        )


    val rowHeight = 80.dp // 这里设置你想要的高度
    val maxHeight = rowHeight * 18
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(maxHeight)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
//        val hourList = listOf(6..23, listOf(0), 1..5).flatten()
        val hourList = listOf(6..23).flatten()
        Column(
            Modifier
                .height(maxHeight)
                .wrapContentWidth()
        ) {
            hourList.forEach { hour ->
                Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.height(rowHeight)) {
                    Text(
                        text = if (hour == 23) "00" else "${hour + 1}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .offset(y = 10.dp)
                            .width(20.dp), // 设置一个固定的宽度
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        (1..7).forEachIndexed { index, weekOfDay ->
            DashedDividerVertical(
                dashWidth = 10f,
                strokeWidth = 1f,
                dashGap = 0f,
                color = if (index != 0) Color.Gray.copy(0.5f) else Color.Transparent,
                height = maxHeight
            )
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                val columnWidth = maxWidth
                (1 .. hourList.size).forEach {
                    DashedHorizontalDivider(modifier = Modifier.offset(y = (it * rowHeight.value - (rowHeight.value / 2)).dp))
                    if (it != hourList.size) {
                        DashedHorizontalDivider(modifier = Modifier.offset(y = (it * rowHeight.value).dp), dashGap = 0f)
                    }
                }
                weekCourseMap[weekOfDay]?.let { vos ->
                    val blocksWithOffsets = calculateOffsets(vos, columnWidth, LocalDensity.current)
//                        vos.forEach { if (it.startTime.hour == hour)  ScheduleItem(it)}
                    blocksWithOffsets.forEach { (block, xOffset, width) ->
                        ScheduleItem(block, rowHeight.value, xOffset, width)
                    }
//                    vos.forEach { ScheduleItem(it, rowHeight.value, xOffset) }
                }
            }
        }
    }
}

// 计算冲突的时间块的xOffset
private fun calculateOffsets(blocks: List<ScheduleBlockVO>, columnWidth: Dp, density: Density): List<Triple<ScheduleBlockVO, Float, Float>> {
    val sortedBlocks = blocks.sortedBy { it.startTime }
    val offsets = mutableListOf<Triple<ScheduleBlockVO, Float, Float>>()
    val columns = mutableListOf<MutableList<ScheduleBlockVO>>()

    sortedBlocks.forEach { block ->
        var placed = false
        for (column in columns) {
            if (column.none { it.endTime > block.startTime }) {
                column.add(block)
                placed = true
                break
            }
        }
        if (!placed) {
            columns.add(mutableListOf(block))
        }
    }
    val blockWidth = columnWidth.value / columns.size

    columns.forEachIndexed { columnIndex, column ->
        column.forEach { block ->
            val xOffset = blockWidth * columnIndex
            offsets.add(Triple(block, xOffset, blockWidth))
        }
    }
    return offsets
}


@Composable
private fun ScheduleItem(vo: ScheduleBlockVO, rowHeight: Float, xOffset: Float, width: Float) {

    // 计算开始时间和结束时间之间的时间差(分钟数)
    val durationInMinutes = Duration.between(vo.startTime, vo.endTime).toMinutes().toFloat()
    // 每分钟代表的dp数
    val minuteHeight = rowHeight / 60.toFloat()
    // 计算整个时间块的高度（dp）
    val height = minuteHeight * durationInMinutes

    val realMinute = ChronoUnit.MINUTES.between(LocalTime.of(6, 0), vo.startTime);
    val minutes =  realMinute // 分钟数
    val minutesPerBlock = 60 // 每个块代表的分钟数

    val blockIndex = minutes / minutesPerBlock // 第几个块
    val minuteOffsetInBlock = minutes % minutesPerBlock // 块内的余数分钟数

    val yOffset = (blockIndex * rowHeight) + (minuteOffsetInBlock.toFloat() / minutesPerBlock) * rowHeight


    val navController = LocalNavController.current

    Box(
        modifier = Modifier
            .offset(x = xOffset.dp, y = yOffset.dp)
            .width(width.dp)
            .height(height.dp) // 设置高度 TODO
            .clip(RoundedCornerShape(10))
            .background(vo.color)
            .clickable { navController.navigate(SubRouter.CourseForm.routeWithParam(vo.uuid)) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = vo.title + if (vo.location.isEmpty()) "" else "@${vo.location}",
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            color = vo.color.contrastTextColor(),
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

fun Color.contrastTextColor() = if (luminance() > 0.5) Color.Black else Color.White

@Preview(showBackground = false)
@Composable
fun AA(){
    AppTheme {
        ScheduleContent(coursesOfAWeek = listOf())
    }
}