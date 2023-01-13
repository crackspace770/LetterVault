package com.google.developers.lettervault.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.developers.lettervault.R
import com.google.developers.lettervault.data.DataRepository
import com.google.developers.lettervault.data.Letter
import com.google.developers.lettervault.ui.detail.LetterDetailActivity
import com.google.developers.lettervault.util.LETTER_ID
import com.google.developers.lettervault.util.NOTIFICATION_CHANNEL_ID
import com.google.developers.lettervault.util.NOTIFICATION_ID

/**
 * Run a work to show a notification on a background thread by the {@link WorkManger}.
 */
class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val channelName = inputData.getString(NOTIFICATION_CHANNEL_ID)
    private val letterId: Long = inputData.getLong(LETTER_ID, 0)
    private val letterRepository = DataRepository.getInstance(ctx)
    /**
     * Create an intent with extended data to the letter.
     */
    private fun getContentIntent(): PendingIntent? {
        val intent = Intent(applicationContext, LetterDetailActivity::class.java).apply {
            putExtra(LETTER_ID, letterId)
        }
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            return@run getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun doWork(): Result {
        val prefManager = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val shouldNotify = prefManager.getBoolean(
            applicationContext.getString(R.string.pref_key_notify),
            false
        )

        if (shouldNotify) showNotification()

        return Result.success()
    }

    private fun showNotification(){
        val notificationManagerCompat = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentIntent(getContentIntent())
            .setSmallIcon(R.drawable.ic_mail)
            .setContentTitle("Open a Letter")
            .setContentText(applicationContext.getString(R.string.notify_content))
            .setAutoCancel(true)
            .setOngoing(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000)

            builder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManagerCompat.notify(32, notification)
    }

    companion object{

    }
}
