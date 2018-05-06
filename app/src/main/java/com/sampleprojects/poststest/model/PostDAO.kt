package com.sampleprojects.poststest.model

import android.arch.paging.DataSource
import android.arch.persistence.room.*

@Dao
interface PostDAO {
    @Query("SELECT * FROM posts ORDER BY date DESC")
    fun getPostsByDate() : DataSource.Factory<Int, Post>

    @Insert
    fun insertPost(post: Post)

    @Delete
    fun deletePost(post: Post)

    @Query("delete from posts")
    fun clearPosts()
}