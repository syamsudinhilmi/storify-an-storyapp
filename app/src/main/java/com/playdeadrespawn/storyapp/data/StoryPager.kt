package com.playdeadrespawn.storyapp.data

import androidx.datastore.dataStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.playdeadrespawn.storyapp.data.api.ApiService
import com.playdeadrespawn.storyapp.data.pref.UserPreference
import com.playdeadrespawn.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.flow.first

class StoryPager (
    private val api: ApiService,
    private val dsUserPref: UserPreference
): PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        val pos = params.key ?: STARTING_PAGE_INDEX
        return try{
            val token = dsUserPref.getSession().first()
            val tokenUser = "Bearer ${token.token}"
            val qParam = HashMap<String, Int>()
            qParam["page"] = pos
            qParam["size"] = params.loadSize
            qParam["location"] = 0

            val responseData = api.getStory(tokenUser, qParam)
            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (pos == STARTING_PAGE_INDEX) null else pos - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else pos + 1
            )
        } catch (e: Exception) { LoadResult.Error(e) }
    }

    private companion object {
        const val STARTING_PAGE_INDEX = 1
    }
}