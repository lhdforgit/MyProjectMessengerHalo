package com.halo.data.repository.attachment

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.halo.data.api.attachment.AttachmentRestApi
import com.halo.data.entities.attachment.Attachment
import com.halo.data.entities.response.ResponsePaging
import retrofit2.HttpException
import java.io.IOException

class AttachmentPagingSource(
    private val api: AttachmentRestApi,
    private var token: String,
    private var workspaceId: String,
    private var channelId: String,
    private var type: String
) : PagingSource<String, Attachment>() {

    override fun getRefreshKey(state: PagingState<String, Attachment>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Attachment> {
        return try {
            val entities: MutableList<Attachment>
            var response: ResponsePaging<Attachment>? = null
            kotlin.runCatching {
                response = api.getAttachments(
                    token = token,
                    workspaceId = workspaceId,
                    channelId = channelId,
                    limit = 20,
                    since = if (params is LoadParams.Append) params.key else null,
                    type = type
                )
            }
            entities = response?.elements ?: mutableListOf()
            val hasNextPage = response?.isHasNextPage ?: false
            val nextKey = entities.lastOrNull()?.messageId
            LoadResult.Page(
                data = entities,
                prevKey = null,
                nextKey = if (hasNextPage) nextKey else null
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}