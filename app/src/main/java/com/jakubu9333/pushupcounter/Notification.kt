package com.jakubu9333.pushupcounter

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 *
 * @author Jakub Uhlarik
 */
class Notification(private val context: Context) {
    private var runningNotificationJob =Job()
    @RequiresApi(Build.VERSION_CODES.M)
    fun notifyInTime(minutes: Int = 30, seconds: Int = 0) {
        val scope = MainScope()

        //cancel of past notification
        runningNotificationJob.cancel()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(context, "my_channel_01")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Push ups")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)

        //#Todo repair overlaping notifications
        val maxTime = 60 * minutes + seconds
        var timeCurrent = 0
        val timer = (0..maxTime)
            .asSequence()
            .asFlow()
            .onEach { delay(1_000) }

        NotificationManagerCompat.from(context).apply {
            builder.setProgress(maxTime, timeCurrent, false)
            notify(0, builder.build())
            runningNotificationJob = Job()
            scope.launch(runningNotificationJob) {
                timer.collect {
                    timeCurrent += 1
                    val time = (maxTime - timeCurrent)
                    val stringTime = (" ${time / 60} minutes ${time % 60} seconds")
                    builder.setContentText(stringTime)
                    builder.setProgress(maxTime, timeCurrent, false)
                    notify(0, builder.build())
                }
                builder.setProgress(0, 0, false)
                builder.setContentText("Time to do pushups")
                notify(0, builder.build())
            }
        }
    }
}