package cn.liibang.pinoko

import cn.hutool.core.io.file.FileWriter
import cn.liibang.pinoko.data.dao.TaskDao
import org.junit.Test
import java.io.File
import java.io.RandomAccessFile
import java.lang.Math.ceil
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URI
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Date
import java.util.UUID


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
        println()
    }


    interface IDatabase {
        fun taskDao(): TaskDao?
    }

    abstract class AbstractDatabase() : IDatabase {
        override fun taskDao(): TaskDao? {
            println("TaskDAO")
            return null
        }
    }


    interface SimplePO {
        val id: String
        val createdAt: LocalDateTime
        val modified: LocalDateTime
    }

    data class PersonPO(
        val name: String,
        override val id: String,
        override val createdAt: LocalDateTime,
        override val modified: LocalDateTime
    ) : SimplePO

    @Test
    fun data_class_test() {
        val now = LocalDate.now().minusDays(1)

        val monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        println("Monday: $monday")
        println("Sunday: $sunday")
    }

    @Test
    fun countCost() {
        println(1600 + 10 + 17 + 138 + 750 + 235 + 40 + 3.6 + 5 + 290 + 3.6 + 5 + 41 + 5 + 650)
        println(138 + 17 + 40 + 41 + 10 + 10 + 200)
    }

    @Test
    fun sout() {
        val file = File("C:\\Users\\aka1298\\Videos\\4月19日 (1).txt")
        val string = file.readLines().joinToString("  ")
        println(string)
    }


    @Test
    fun transalge() {
        val srtFile = File("C:\\Users\\aka1298\\Desktop\\Sdb\\ENG.sdb", "r")

        


    }

    @Test
    fun readFile() {

        val file = RandomAccessFile("C:\\Users\\aka1298\\Desktop\\Sdb\\ENG.sdb", "r")

        val path = Paths.get("C:\\Users\\aka1298\\Desktop\\Sdb\\ENG.sdb")
        val bytes = Files.readAllBytes(path)
        val buffer = ByteBuffer.wrap(bytes)

        var n = 0
        while (buffer.hasRemaining()) {
            val index = buffer.int
            val string = readNullTerminatedString(buffer)
//            val offset = buffer.long
//            val id = buffer.int
            n += 1
//            println("Index: $index")
//            println("String: $string.")
//            println("Offset: $offset")
//            println("ID: $id")
        }
        println(n)
    }


    @Test
    fun jishu() {
        println("".split(",", ).map { it.toInt() })
    }



}





fun readNullTerminatedString(buffer: ByteBuffer): String {
    val start = buffer.position()
    while (buffer.get() != 0.toByte()) {}
    val end = buffer.position()
    val bytes = ByteArray(end - start)
    buffer.position(start)
    buffer.get(bytes)
    return String(bytes, Charsets.US_ASCII)
}