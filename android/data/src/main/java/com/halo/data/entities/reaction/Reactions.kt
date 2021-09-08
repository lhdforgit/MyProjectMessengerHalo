package com.halo.data.entities.reaction

import android.text.TextUtils
import com.google.gson.Gson

class Reactions : HashMap<String, MutableList<String>> {
    constructor(initialCapacity: Int, loadFactor: Float) : super(initialCapacity, loadFactor)
    constructor(initialCapacity: Int) : super(initialCapacity)
    constructor() : super()
    constructor(m: MutableMap<out String, out MutableList<String>>) : super(m)

    override fun toString(): String {
        return kotlin.runCatching {
            Gson().toJson(this)
        }.getOrElse {
            ""
        }
    }

    fun haveReaction(): Boolean {
        return this.size > 0
    }

    fun reacted(ownerId: String, emoji: String) : Boolean{
        return get(emoji)?.find { TextUtils.equals(it, ownerId) }!=null
    }

    fun reacted(ownerId: String) : Boolean{
        this.entries.forEach {
            it.value.find { TextUtils.equals(ownerId, it) }?.takeIf { it.isNotEmpty() }?.run {
                return true
            }
        }
        return false
    }
}