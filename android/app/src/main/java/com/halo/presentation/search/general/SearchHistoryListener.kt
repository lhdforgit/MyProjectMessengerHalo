package com.halo.presentation.search.general


interface SearchHistoryListener {
    fun deleteHistoryById(id : String, name : String?)
    fun onClickItem(query: String?, type : String?, id: String?)
    fun deleteAllHistory()
}