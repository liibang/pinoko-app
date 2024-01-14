package cn.liibang.pinoko

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import androidx.core.app.NotificationCompat

import java.time.LocalDateTime
import java.time.ZoneId

const val messageKEY = "TaskNotifyMessage"


interface AlarmScheduler {
    fun schedule(alarmItem: AlarmItem)
    fun cancel(alarmItemId: String)
}

data class AlarmItem(
    val id: String,
    val alarmTime: LocalDateTime,
    val message: String
)

class AlarmSchedulerImpl(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(alarmItem: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(messageKEY, alarmItem.message)
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
}


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {


        val message = intent?.getStringExtra(messageKEY) ?: return
        context?.also {
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(context, "alarm_id")
                .setContentTitle("皮诺可Todo")
                .setContentText(message)
                .setSmallIcon(R.mipmap.icon)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true)
                .addAction(androidx.core.R.drawable.ic_call_decline, "test", pendingIntent)
                .addAction(androidx.core.R.drawable.ic_call_answer, "test", pendingIntent)
                .build()
            notificationManager.notify(
                1,
                notification
            )
        }
    }
}