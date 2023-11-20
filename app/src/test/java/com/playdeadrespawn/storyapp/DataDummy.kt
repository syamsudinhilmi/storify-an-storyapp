package com.playdeadrespawn.storyapp

import com.playdeadrespawn.storyapp.data.response.ListStoryItem
import com.playdeadrespawn.storyapp.data.response.StoryResponse

object DataDummy {
    fun generateDummyStoriesEntity(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "https://www.toyota.astra.co.id/sites/default/files/2022-10/TC%2047%20SEPTEMBER%20-%20TOYOTA%20GR%20SUPRA%20MANUAL%20%28UMUM%29.jpg",
                "2023-09-20T20:22:22Z",
                "hilmi$i",
                "ashdgaskdjhsad",
                21.8822,
                "$i",
                21.8822
            )
            items.add(story)
        }
        return items
    }
    fun generateDummyStoriesResponse(): StoryResponse {
        return StoryResponse(
            generateDummyStoriesEntity(),
            false,
            "Stories fetched successfully"
        )
    }
}