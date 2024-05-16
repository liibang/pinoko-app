package cn.liibang.pinoko.ui.support

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.hutool.core.util.HashUtil
import cn.hutool.core.util.IdUtil
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale

fun hashPassword(): Nothing = TODO()

fun getWeekDates(currentDate: LocalDate): List<LocalDate> {
    // 获得当前日期所在周的星期一
    val startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val weekDates: MutableList<LocalDate> = ArrayList()
    // 添加当前周的每一天到列表
    for (i in 0..6) {
        weekDates.add(startOfWeek.plusDays(i.toLong()))
    }
    return weekDates
}



fun calculateWeekNumber(termStartDate: LocalDate, dateToCheck: LocalDate): Int {
//    val termStartDate: LocalDate = LocalDate.of(2024, 3, 30) // 开学日期
//    val dateToCheck: LocalDate = LocalDate.of(2024, 3, 31) // 指定要检查的日期

    if (!getWeekDates(dateToCheck).any { it >= termStartDate })  {
        return 0
    }

    // 计算开学日期和那周周六之间的天数，包括开学当天
    val daysInFirstWeek = 7 - termStartDate.dayOfWeek.value

    // 然后计算指定日期和开学日期之间的总天数
    val totalDaysSinceStart = ChronoUnit.DAYS.between(termStartDate, dateToCheck)

    // 计算周数
    val currentWeek = if (totalDaysSinceStart < daysInFirstWeek) {
        1 // 如果指定日期在第一周的天数范围内，则为第一周
    } else {
        // 否则计算随后的周数，第一周已计算，所以直接加1
        Math.ceil((totalDaysSinceStart - daysInFirstWeek) / 7.0).toInt() + 1
    }
    return currentWeek
}

fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Modifier.bottomShadow(shadow: Dp) =
    this
        .clip(GenericShape { size, _ ->
            lineTo(size.width, 0f)
            lineTo(size.width, Float.MAX_VALUE)
            lineTo(0f, Float.MAX_VALUE)
        })
        .shadow(shadow)


fun Modifier.bottomElevation(): Modifier = this.then(Modifier.drawWithContent {
    val paddingPx = 8.dp.toPx()
    clipRect(
        left = 0f,
        top = 0f,
        right = size.width,
        bottom = size.height + paddingPx
    ) {
        this@drawWithContent.drawContent()
    }
})

fun generateUUID(): String = IdUtil.simpleUUID()

private fun digits(`val`: Long, digits: Int): String? {
    val hi = 1L shl digits * 4
    return java.lang.Long.toHexString(hi or (`val` and hi - 1L)).substring(1)
}

fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    composed {
        clickable(
            onClick = onClick,
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        )
    }


fun YearMonth.displayText(short: Boolean = false): String {
    return "${this.month.displayText(short = short)} ${this.year}"
}

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return getDisplayName(style, Locale.CHINESE)
}

fun DayOfWeek.displayText(uppercase: Boolean = false): String {
    return getDisplayName(TextStyle.SHORT, Locale.CHINESE).let { value ->
        if (uppercase) value.uppercase(Locale.CHINESE) else value
    }
}

fun LocalDate.toDateMillis() = this.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()

fun LocalDate.toMonthMillis() =
    this.withDayOfMonth(1).atStartOfDay().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()


fun LocalDateTime.toTimestamp(): Long {
    return this.toInstant(ZoneOffset.UTC).toEpochMilli()
}

fun timestampToDateTime(timestamp: Long): LocalDateTime {
    return timestamp.let {
        Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("UTC")).toLocalDateTime()
    }
}


@RequiresApi(Build.VERSION_CODES.S)
fun Duration.formatToHM(): String {
    val hours = this.toHoursPart()
    val minutes = this.toMinutesPart()
    return when {
        hours > 0 -> "${hours}h${minutes}m"
        else -> "${minutes}m"
    }
}