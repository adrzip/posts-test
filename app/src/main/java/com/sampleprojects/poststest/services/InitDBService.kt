package com.sampleprojects.poststest.services

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.sampleprojects.poststest.model.AppDatabase
import com.sampleprojects.poststest.model.Post

class InitDBService : IntentService("InitDBService") {

    companion object {
        val TAG = InitDBService::class.java.simpleName
    }
    override fun onHandleIntent(intent: Intent?) {
        val db = AppDatabase.getInstance(applicationContext)?.postDAO()
        val timer = System.currentTimeMillis()
        Log.d(TAG, "Adding 100 demo posts to the database")
        db?.clearPosts()
        for (i in 1..100) {
            val generatedTitle = "Title $i"
            val generatedAuthor = "Author $i"
            val generatedDate = System.currentTimeMillis()
            var post = Post( id = null, title = generatedTitle, author = generatedAuthor, date = generatedDate)
            db?.insertPost(post)
        }
        val lap = System.currentTimeMillis() - timer
        Log.d(TAG, "Added demo posts successfully in $lap ms")
    }
}