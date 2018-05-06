package com.sampleprojects.poststest.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.content.Context
import android.content.Intent
import com.sampleprojects.poststest.model.AppDatabase
import com.sampleprojects.poststest.model.Post
import com.sampleprojects.poststest.services.ClearDBService
import com.sampleprojects.poststest.services.InitDBService


class MainViewModel : ViewModel() {

    private val POSTS_PAGE_SIZE = 10

    // AppDatabase is our custom implementation of the Architecture Components Database API
    private var appDb: AppDatabase? = null

    // LiveData is a reactive object of PagedList, the Paging library component
    private var posts: LiveData<PagedList<Post>>? = null

    fun getPosts(context: Context): LiveData<PagedList<Post>> {
        // Initialize on start up
        if(appDb == null) {
            appDb = AppDatabase.getInstance(context)
        }
        if(posts == null) {
            // LivePagedListBuilder is part of the Architecture Components Paging library that manages the paged access to the DB
            posts = LivePagedListBuilder<Int, Post>(appDb?.postDAO()?.getPostsByDate()!!, POSTS_PAGE_SIZE).build()
        }
        // It returns the LiveData reference right away which will get updated one available
        return posts!!
    }

    fun initPosts(context: Context) {
        // This just starts a background service and returns immediately
        val i = Intent(context, InitDBService::class.java)
        context.startService(i)
    }

    fun clearPosts(context: Context) {
        // This just starts a background service and returns immediately
        val i = Intent(context, ClearDBService::class.java)
        context.startService(i)
    }

}