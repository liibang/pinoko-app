package cn.liibang.pinoko

import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoField
import java.util.UUID
import kotlin.reflect.KClass


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

    }

    //    data class Emoji(
//        val label: String,
//        val hexcode: String,
//        val emoji: String,
//        val text: String,
//        val type: Int,
//        val version: Int
//    )
    enum class GroupNames(val code: Int, val desc: String) {
        EMOTION(0, "表情")
    }


    @Test
    fun ok() {
        println("dfc3eae2-aa0e-11ee-868d-0242ac110002")
        println(UUID.randomUUID().toString())
    }

    @Test
    fun test_uuid() {
        println(LocalDate.now().toTimestamp())
        println(LocalTime.now().getLong(ChronoField.SECOND_OF_DAY))
    }
}

