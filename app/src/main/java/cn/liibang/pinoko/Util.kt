package cn.liibang.pinoko

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

object Util {




}

fun LocalDate.toTimestamp(): Long {
    return this.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli()
}

fun Long?.toLocalDate(): LocalDate? {
    return this?.let {
        Instant.ofEpochMilli(this).atZone(ZoneOffset.ofHours(8)).toLocalDate()
    }
}