package cn.liibang.pinoko.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import cn.liibang.pinoko.R
import cn.liibang.pinoko.data.entity.HabitPO
import cn.liibang.pinoko.data.entity.HabitType
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar


const val messageKEY = "TaskNotifyMessage"


interface AlarmScheduler {
    fun schedule(alarmItem: AlarmItem)
    fun cancel(alarmItemId: String)
    fun scheduleHabit(habit: HabitPO)
}


enum class AlarmType(val desc: String) { TASK("事件提醒"), HABIT("习惯提醒") }
data class AlarmItem(
    val id: String,
    val alarmTime: LocalDateTime,
    val message: String,
    val type: AlarmType
)

class AlarmSchedulerImpl(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    init {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = "alarm_channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("alarm_channel", name, importance)
        channel.description = name
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(alarmItem: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(messageKEY, alarmItem.message)
            putExtra("typeDesc", alarmItem.type.desc)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmItem.alarmTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L,
            PendingIntent.getBroadcast(
                context,
                alarmItem.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(alarmItemId: String) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarmItemId.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun scheduleHabit(habit: HabitPO) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(messageKEY, habit.name)
            putExtra("typeDesc", habit.type.desc)
        }

        val pendingIntent = PendingIntent.getBroadcast(context, habit.id.hashCode(), intent,
            PendingIntent.FLAG_IMMUTABLE)

        when(habit.type) {
            HabitType.WEEKLY_SPECIFIC_DAYS -> {


                val calendarMonday: Calendar = Calendar.getInstance()
                calendarMonday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                calendarMonday.set(Calendar.HOUR_OF_DAY, 8)
                calendarMonday.set(Calendar.MINUTE, 0)
                calendarMonday.set(Calendar.SECOND, 0)

                val calendarTuesday: Calendar = Calendar.getInstance()
                calendarTuesday.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
                calendarTuesday.set(Calendar.HOUR_OF_DAY, 8)
                calendarTuesday.set(Calendar.MINUTE, 0)
                calendarTuesday.set(Calendar.SECOND, 0)


                // 检查时间是否已经过去，如果已经过去，则设置为下周
                if (calendarMonday.getTimeInMillis() < System.currentTimeMillis()) {
                    calendarMonday.add(Calendar.DAY_OF_YEAR, 7)
                }
                // 检查时间是否已经过去，如果已经过去，则设置为下周
                if (calendarTuesday.getTimeInMillis() < System.currentTimeMillis()) {
                    calendarTuesday.add(Calendar.DAY_OF_YEAR, 7)
                }

                alarmManager!!.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendarMonday.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7,
                    pendingIntent
                )
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendarTuesday.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7,
                    pendingIntent
                )
            }
            HabitType.MONTHLY_SPECIFIC_DAY -> {
                // 设置特定日期和时间

                // 设置特定日期和时间
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = System.currentTimeMillis()
                calendar[Calendar.DAY_OF_MONTH] = habit.value.toInt() // specificDay是每月的特定日期

                calendar[Calendar.HOUR_OF_DAY] = habit.remindTime!!.hour // specificHour是小时

                calendar[Calendar.MINUTE] = habit.remindTime!!.minute // specificMinute是分钟

                calendar[Calendar.SECOND] = 0

                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    calendar.add(Calendar.MONTH, 1)
                }

                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY * 30,
                    pendingIntent
                )
            }
            HabitType.EVERY_FEW_DAYS -> {

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, habit.remindTime!!.hour)
                    set(Calendar.MINUTE, habit.remindTime!!.minute)
                    set(Calendar.SECOND, 0)
                }

                // 确保设置的时间不是过去的时间，如果是，则加上间隔天数
                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_MONTH, habit.value.toInt())
                }

                // 设置重复闹钟
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY * habit.value.toInt(),
                    pendingIntent
                )
            }
        }

    }
}


class AlarmReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context?, intent: Intent?) {

        val message = intent?.getStringExtra(messageKEY) ?: return
        val typeMessage = intent.getStringExtra("typeDesc")
        context?.also {

            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.vibrate(
                CombinedVibration.createParallel(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(context, "alarm_channel")
                .setContentTitle(typeMessage)
                .setContentText(message)
                .setSmallIcon(R.mipmap.icon)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true)
//                .addAction(androidx.core.R.drawable.ic_call_decline, "test", pendingIntent)
//                .addAction(androidx.core.R.drawable.ic_call_answer, "test", pendingIntent)
                .build()
            notificationManager.notify(
                1,
                notification
            )
        }
    }
}