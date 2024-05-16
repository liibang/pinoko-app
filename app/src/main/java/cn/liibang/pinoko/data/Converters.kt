package cn.liibang.pinoko.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.estimateAnimationDurationMillis
import androidx.room.TypeConverter
import cn.liibang.pinoko.data.entity.HabitType
import cn.liibang.pinoko.data.entity.LessonInfo
import cn.liibang.pinoko.data.entity.Operation
import cn.liibang.pinoko.data.entity.Table
import cn.liibang.pinoko.data.entity.TaskSortMode
import cn.liibang.pinoko.ui.support.gson
import cn.liibang.pinoko.ui.support.toDateMillis
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset


class Converters {

    @TypeConverter
    fun timestampToDateTime(timestamp: Long?): LocalDateTime? {
        return timestamp?.let {
            Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("UTC")).toLocalDateTime()
        }
    }

    @TypeConverter
    fun dateTimeToTimestamp(dateTime: LocalDateTime?): Long? {
        return dateTime?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
    }

    //============================================================================
    @TypeConverter
    fun secondToTime(second: Int?): LocalTime? {
        return second?.let { LocalTime.ofSecondOfDay(it.toLong()) }
    }

    @TypeConverter
    fun timeToTimestamp(time: LocalTime?): Int? {
        return time?.toSecondOfDay()
    }

    //============================================================================
    @TypeConverter
    fun timestampToDate(timestamp: Long?): LocalDate? {
        return timestamp?.let {
            Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("UTC")).toLocalDate()
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toDateMillis()
    }

    //============================================================================
    @TypeConverter
    fun operationToStr(operation: Operation): String {
        return operation.toString()
    }

    @TypeConverter
    fun strToOperation(operationStr: String): Operation {
        return Operation.valueOf(operationStr)
    }

    //============================================================================
    @TypeConverter
    fun tableToStr(table: Table): String {
        return table.toString()
    }

    @TypeConverter
    fun strToTable(tableStr: String): Table {
        return Table.valueOf(tableStr)
    }

    //============================================================================
    @TypeConverter
    fun jsonStrToLessonTime(lessonTimesJsonStr: String): LessonInfo {
        return gson.fromJson(lessonTimesJsonStr, LessonInfo::class.java)
    }

    @TypeConverter
    fun lessonTimesToJsonStr(lessonTimes: LessonInfo): String {
        return gson.toJson(lessonTimes)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @TypeConverter
    fun durationToInt(duration: Duration): Int {
        return duration.toSeconds().toInt()
    }

    @TypeConverter
    fun intToDuration(number: Int): Duration {
        return Duration.ofSeconds(number.toLong())
    }


    @TypeConverter
    fun taskSortModeToInt(taskSortMode: TaskSortMode): Int {
        return taskSortMode.code
    }

    @TypeConverter
    fun intToTaskSortMode(code: Int): TaskSortMode {
        return TaskSortMode.values().find { code == it.code }!!
    }

    @TypeConverter
    fun habitTypeToInt(habitType: HabitType): Int {
        return habitType.code
    }

    @TypeConverter
    fun intToHabitType(code: Int): HabitType {
        return HabitType.values().find { code == it.code }!!
    }

}