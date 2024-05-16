package cn.liibang.pinoko.ui.support

import cn.liibang.pinoko.data.entity.HabitPO
import cn.liibang.pinoko.data.entity.HabitType
import cn.liibang.pinoko.data.entity.TaskSortMode
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


val gson: Gson = GsonBuilder()
    .registerTypeAdapter(LocalTime::class.java, LocalTimeTypeAdapter())
    .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
    .registerTypeAdapter(Duration::class.java, DurationAdapter())
    .registerTypeAdapter(TaskSortMode::class.java, TaskSortModeAdapter())
    .registerTypeAdapter(HabitType::class.java, HabitTypeAdapter())
    .create()


class HabitTypeAdapter : JsonSerializer<HabitType?>, JsonDeserializer<HabitType?> {
    override fun serialize(
        src: HabitType?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.code)
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): HabitType? {
        return HabitType.values().find { it.code == json?.asInt }
    }
}


class TaskSortModeAdapter : JsonSerializer<TaskSortMode?>, JsonDeserializer<TaskSortMode?> {
    override fun serialize(
        src: TaskSortMode?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.code)
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): TaskSortMode? {
        return TaskSortMode.values().find { it.code == json?.asInt }
    }
}


class DurationAdapter : JsonSerializer<Duration?>, JsonDeserializer<Duration?> {
    override fun serialize(
        src: Duration?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.seconds ?: 0)
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Duration? {
        return json?.asLong?.let {
            Duration.ofSeconds(it)
        }
    }
}

class LocalTimeTypeAdapter : JsonSerializer<LocalTime?>, JsonDeserializer<LocalTime?> {
    override fun serialize(
        src: LocalTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "")
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalTime? {
        return json?.asString?.let {
            val (hour, minute) = it.split(":")
            LocalTime.of(hour.toInt(), minute.toInt())
        }
    }
}

class LocalDateTypeAdapter : JsonSerializer<LocalDate?>, JsonDeserializer<LocalDate?> {
    override fun serialize(
        src: LocalDate?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.toString() ?: "")
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate? {
        return json?.asString?.let {
            LocalDate.parse(it)
        }
    }
}

val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

class LocalDateTimeTypeAdapter : JsonSerializer<LocalDateTime?>, JsonDeserializer<LocalDateTime?> {

    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.format(dateTimeFormatter) ?: "")
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime? {
        return json?.asString?.let {
            LocalDateTime.parse(it, dateTimeFormatter)
        }
    }
}