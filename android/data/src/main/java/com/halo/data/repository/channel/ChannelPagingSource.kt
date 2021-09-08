/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.repository.channel

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.halo.data.api.channel.ChannelRestApi
import com.halo.data.entities.channel.Channel
import retrofit2.HttpException
import java.io.IOException

/**
 * Create by ndn
 * Create on 7/20/20
 * com.halo.data.repository.feeds
 */
class ChannelPagingSource(
    private val api: ChannelRestApi,
    private var token: String,
    private var workspaceId:String,
) : PagingSource<String, Channel>() {

    override suspend fun load(
        params: LoadParams<String>
    ): LoadResult<String, Channel> {
        val sinceId = params.key
        return try {
            val response = api.channels(
                token = token,
                workspaceId = workspaceId,
                limit = 20,
                since = sinceId,
            )
            val entities = response?.docs ?: mutableListOf()
            LoadResult.Page(
                data = entities,
                prevKey = null,
                nextKey = if (response?.hasNextPage==true) {entities.last().channelId} else null
            )
        } catch (e: IOException) {
            e.printStackTrace()
            LoadResult.Error(e)
        } catch (e: HttpException) {
            e.printStackTrace()
            LoadResult.Error(e)
        }catch (e: Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Channel>): String? {
        return null
    }
}