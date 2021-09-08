/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.repository.contact

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.halo.common.utils.ktx.lastOrNull
import com.halo.data.api.contact.ContactRestApi
import com.halo.data.common.utils.Strings
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.contact.Contact
import com.halo.data.entities.contact.ContactType
import com.halo.data.entities.member.MemberChannel
import com.halo.data.entities.response.ResponsePaging
import retrofit2.HttpException
import java.io.IOException

/**
 * Create by ndn
 * Create on 7/20/20
 * com.halo.data.repository.feeds
 */
class ContactPagingSource(
    private val api: ContactRestApi,
    private var token: String,
) : PagingSource<String, Contact>() {

    override suspend fun load(
        params: LoadParams<String>
    ): LoadResult<String, Contact> {
        val since = params is LoadParams.Refresh
        return try {
            if (since) {
                try {
                    val refreshResponse = api.contactsRefresh(token)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: HttpException) {
                    e.printStackTrace()
                }
            }
            val entities: MutableList<Contact>
            var response = ResponsePaging<Contact>()
            kotlin.runCatching {
                response = api.contactsV2(
                    token = token,
                    limit = 100,
                    since = if (params is LoadParams.Append) params.key else null,
                    status = ContactType.CONTACTED
                ) ?: ResponsePaging()
            }
            entities = response.elements ?: mutableListOf()
            val nextKey = entities.lastOrNull()?.contactId
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

    override fun getRefreshKey(state: PagingState<String, Contact>): String {
        return ""
    }
}