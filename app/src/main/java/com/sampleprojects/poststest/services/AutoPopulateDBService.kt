package com.sampleprojects.poststest.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.mooveit.library.Fakeit
import com.sampleprojects.poststest.MainActivity
import com.sampleprojects.poststest.model.AppDatabase
import com.sampleprojects.poststest.model.Post
import com.sampleprojects.poststest.model.PostDAO
import java.util.*

class AutoPopulateDBService : Service() {

    companion object {
        private val LOG_TAG = "ForegroundService"
        val MAIN_ACTION = "com.sampleprojects.poststest.action.main"
        val STOP_ACTION = "com.sampleprojects.poststest.action.stop"
        val STARTFOREGROUND_ACTION = "com.sampleprojects.poststest.action.startforeground"
        val STOPFOREGROUND_ACTION = "com.sampleprojects.poststest.action.stopforeground"
        val NOTIFICATION_ID_FOREGROUND_SERVICE = 101
    }

    lateinit var timer: Timer
    var db: PostDAO? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG,"onStartCommand - action = ${intent.action}" )
        when {
            intent.action == STARTFOREGROUND_ACTION -> {
                Log.i(LOG_TAG, "Received Start Foreground Intent ")
                db = AppDatabase.getInstance(applicationContext)?.postDAO()
                Fakeit.init()
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        Log.d(LOG_TAG, "Adding 5 demo posts to the database")
                        for (i in 1..5) {
                            val book = Fakeit.book()
                            val generatedTitle = book.title()
                            val generatedAuthor = book.author()
                            val generatedDate = System.currentTimeMillis()
                            var post = Post( id = null, title = generatedTitle, author = generatedAuthor, date = generatedDate)
                            db?.insertPost(post)
                        }
                    }
                }, 1000, 1000)

                val notificationIntent = Intent(this, MainActivity::class.java)
                notificationIntent.action = MAIN_ACTION
                notificationIntent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                val pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, 0)

                val stopIntent = Intent(this, AutoPopulateDBService::class.java)
                stopIntent.action = STOPFOREGROUND_ACTION
                val pStopIntent = PendingIntent.getService(this, 0,
                        stopIntent, 0)

                val icon = BitmapFactory.decodeResource(resources,
                        android.R.drawable.ic_menu_add)

                val channelId =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            createNotificationChannel()
                        } else {
                            // If earlier version channel ID is not used
                            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                            ""
                        }

                val notification = NotificationCompat.Builder(this, channelId)
                        .setContentTitle("Posts test App")
                        .setTicker("Posts test App")
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentText("Adding new posts automatically (5/s)")
                        .setSmallIcon(android.R.drawable.ic_menu_add)
                        .setLargeIcon(
                                Bitmap.createScaledBitmap(icon, 128, 128, false))
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .addAction(android.R.drawable.ic_media_pause, "Stop",
                                pStopIntent).build()
                startForeground(NOTIFICATION_ID_FOREGROUND_SERVICE, notification)
            }
            intent.action == STOP_ACTION -> Log.i(LOG_TAG, "Clicked Stop")
            intent.action == STOPFOREGROUND_ACTION -> {
                Log.i(LOG_TAG, "Received Stop Foreground Intent")
                timer.cancel()
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(LOG_TAG, "In onDestroy")
    }

    override fun onBind(intent: Intent): IBinder? {
        // Used only in case of bound services.
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String{
        val channelId = "my_service"
        val channelName = "My Background Service"
        val chan = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
}