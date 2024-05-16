package cn.liibang.pinoko.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import cn.liibang.pinoko.MainActivity
import cn.liibang.pinoko.R

class FocusNotifier(private val vibratorManager: VibratorManager, private val context: Context) {

    init {
// Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = "FocusChannel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("FocusChannel", name, importance)
        channel.description = name
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun sendMessage(message: String) {
        // 先振动
        vibratorManager.vibrate(
            CombinedVibration.createParallel(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
        )
        // 然后在发消息到通知栏
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val builder = NotificationCompat.Builder(context, "FocusChannel")
            .setContentTitle("番茄专注")
            .setContentText(message)
            .setSmallIcon(R.mipmap.icon)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE))
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder)

    }
}

