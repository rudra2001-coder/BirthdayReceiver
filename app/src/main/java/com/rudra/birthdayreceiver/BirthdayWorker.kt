package com.rudra.birthdayreceiver
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.Calendar
class BirthdayWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("birthday_prefs", Context.MODE_PRIVATE)
        val day = prefs.getInt("day", -1)
        val month = prefs.getInt("month", -1)
        val imageUri = prefs.getString("image_uri", null)

        val now = Calendar.getInstance()
        if (now.get(Calendar.DAY_OF_MONTH) == day && now.get(Calendar.MONTH) == month) {
            sendNotification(imageUri)
        }

        return Result.success()
    }

    private fun sendNotification(imageUri: String?) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "birthday_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Birthday Wishes", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("🎉 Happy Birthday!")
            .setContentText("Wishing you a joyful day! 🎂")
            .setSmallIcon(android.R.drawable.star_big_on)
            .setAutoCancel(true)

        imageUri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, Uri.parse(it))
            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        }

        notificationManager.notify(101, builder.build())
    }
}
