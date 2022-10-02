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
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.temporal.ChronoUnit


/**
 *
 * @author Jakub Uhlarik
 */
class Notification(private val context: Context) {
    private var runningNotificationJob = Job()
    private var notifyId = 0

    @RequiresApi(Build.VERSION_CODES.O)
    fun notifyInTime(minutes: Int = 30, seconds: Int = 0) {
        notifyId += 1
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
        var timeEnd = LocalTime.now()
        val maxTime = 60 * minutes + seconds
        timeEnd = timeEnd.plusMinutes(minutes.toLong())
        timeEnd = timeEnd.plusSeconds(seconds.toLong())
        var timeNow = LocalTime.now()
        var timeCurrent = 0


        NotificationManagerCompat.from(context).apply {
            builder.setProgress(maxTime, timeCurrent, false)
            notify(notifyId, builder.build())
            runningNotificationJob = Job()
            scope.launch(runningNotificationJob) {
                while (timeNow < timeEnd) {
                    timeNow = LocalTime.now()
                    val time = timeNow.until(timeEnd, ChronoUnit.SECONDS)
                    timeCurrent = (maxTime - time).toInt()

                    val stringTime = (" ${time / 60} minutes ${time % 60} seconds")
                    builder.setContentText(stringTime)
                    builder.setProgress(maxTime, timeCurrent, false)
                    notify(notifyId, builder.build())
                    delay(1000)
                }
                builder.setProgress(0, 0, false)
                builder.setContentText("Time to do pushups")
                notify(notifyId, builder.build())
            }
        }
    }
}