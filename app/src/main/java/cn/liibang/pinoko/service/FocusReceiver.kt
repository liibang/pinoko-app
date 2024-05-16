package cn.liibang.pinoko.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import cn.liibang.pinoko.MainActivity
import cn.liibang.pinoko.R

class FocusReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("FocusMessage") ?: return
        context?.also {
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(context, "focus_id")
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