package com.sampleprojects.poststest.services

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.sampleprojects.poststest.model.AppDatabase
import com.sampleprojects.poststest.model.Post

class ClearDBService : IntentService("ClearDBService") {

    companion object {
        val TAG = ClearDBService::class.java.simpleName
    }
    override fun onHandleIntent(intent: Intent?) {
        val db = AppDatabase.getInstance(applicationContext)?.postDAO()
        val timer = System.currentTimeMillis()
        db?.clearPosts()
        val lap = System.currentTimeMillis() - timer
        Log.d(TAG, "Added demo posts successfully in $lap ms")
    }
}