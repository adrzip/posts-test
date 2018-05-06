package com.sampleprojects.poststest.model

import android.arch.persistence.room.*

@Entity(tableName = "posts")
data class Post(@PrimaryKey(autoGenerate = true) var id: Long?,
                @ColumnInfo(name = "title") var title: String,
                @ColumnInfo(name = "author") var author: String,
                @ColumnInfo(name = "date") var date: Long)
