package cn.liibang.pinoko.data

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset


class Converters {

    @TypeConverter
    fun timestampToDateTime(timestamp: Long?): LocalDateTime? {
        return timestamp?.let {
            Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime()
        }
    }

    @TypeConverter
    fun dateTimeToTimestamp(dateTime: LocalDateTime?): Long? {
        return dateTime?.toInstant(ZoneOffset.ofHours(8))?.toEpochMilli()
    }

    @TypeConverter
    fun secondToTime(second: Int?): LocalTime? {
        return second?.let { LocalTime.ofSecondOfDay(it.toLong()) }
    }

    @TypeConverter
    fun timeToTimestamp(time: LocalTime?): Int? {
        return time?.toSecondOfDay()
    }

    @TypeConverter
    fun timestampToDate(timestamp: Long?): LocalDate? {
        return timestamp?.let {
            Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDate()
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.atStartOfDay(ZoneOffset.ofHours(8))?.toInstant()?.toEpochMilli()
    }

}