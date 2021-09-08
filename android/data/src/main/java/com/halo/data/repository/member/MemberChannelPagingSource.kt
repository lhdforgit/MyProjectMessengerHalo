package com.halo.data.repository.member

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.halo.data.api.member.MemberRestApi
import com.halo.data.entities.member.MemberChannel
import com.halo.data.entities.response.ResponsePaging
import retrofit2.HttpException
import java.io.IOException

class MemberChannelPagingSource(
    private val api: MemberRestApi,
    private val token: String,
    private val workspaceId: String?,
    private val channelId: String?
) : PagingSource<String, MemberChannel>() {

    override fun getRefreshKey(state: PagingState<String, MemberChannel>): String {
        return ""
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, MemberChannel> {

        return try {
            val entities: MutableList<MemberChannel>
            var response = ResponsePaging<MemberChannel>()
            kotlin.runCatching {
                response = api.getMemberPaging(
                    token = token,
                    workspaceId = workspaceId,
                    channelId = channelId,
                    since = if (params is LoadParams.Append) params.key else null,
                    limit = 20
                ) ?: ResponsePaging()
            }
            entities = response.elements?.filter {!it.isMemberDelete}?.toMutableList() ?: mutableListOf() // loại bỏ member bị delete
            val nextKey = entities.lastOrNull()?.memberId
            LoadResult.Page(
                data = entities,
                prevKey = null,
                nextKey = if (response.isHasNextPage) nextKey else null
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}