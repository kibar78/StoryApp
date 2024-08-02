package com.example.storyapp.utils

import com.example.storyapp.data.local.room.StoryEntity
import com.example.storyapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoriesResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val stories = StoryEntity(
                i.toString(),
                "photoUrl = $i",
                "createAt $i",
                "name $i",
                "desc $i",
                i.toDouble(),
                i.toDouble()
            )
            items.add(stories)
        }
        return items
    }
}