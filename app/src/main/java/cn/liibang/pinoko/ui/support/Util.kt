package cn.liibang.pinoko.ui.support

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.random.Random

object Util {




}

fun LocalDate.toTimestamp(): Long {
    return this.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
}

fun Long?.toLocalDate(): LocalDate? {
    return this?.let {
        Instant.ofEpochMilli(this).atZone(ZoneId.of("UTC")).toLocalDate()
    }
}

fun getRandomColorHex(): String {
    val r = Random.nextInt(256)
    val g = Random.nextInt(256)
    val b = Random.nextInt(256)
    return String.format("#%02X%02X%02X", r, g, b)
}

fun Color.toHexCode() = String.format("#%06X", (0xFFFFFF and this.toArgb()))