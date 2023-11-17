package com.playdeadrespawn.storyapp.utils

import com.playdeadrespawn.storyapp.data.response.ListStoryItem
import com.playdeadrespawn.storyapp.data.response.StoryResponse

object DummyData {
    fun generateDummyStoriesEntity(): List<ListStoryItem> {
        val listStory: MutableList<ListStoryItem> = arrayListOf()
        for (j in 0..10) {
            val story = ListStoryItem(
                "https://www.toyota.astra.co.id/sites/default/files/2022-10/TC%2047%20SEPTEMBER%20-%20TOYOTA%20GR%20SUPRA%20MANUAL%20%28UMUM%29.jpg",
                "2023-11-12T21:12:12Z",
                "Hilmi$j",
                "OSJDFOASIDJASODIJSADJSAKLJOQWIRQWO QWODIJOWQI QDOQWIJD",
                22.8922,
                "$j",
                22.8922
            )
            listStory.add(story)
        }
        return listStory
    }

    fun generateDummyStoriesResponse(): StoryResponse {
        return StoryResponse(
            generateDummyStoriesEntity(),
            false,
            "Stories fetched successfully"
        )
    }
}