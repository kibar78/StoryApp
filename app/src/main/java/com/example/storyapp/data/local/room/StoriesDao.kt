package com.example.storyapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface StoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(story: List<StoryEntity>)

    @Query("SELECT * FROM stories")
    fun getAllStory(): PagingSource<Int, StoryEntity>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}