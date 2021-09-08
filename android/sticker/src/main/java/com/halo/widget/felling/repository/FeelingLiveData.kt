/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.felling.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.halo.widget.felling.model.FeelingEntity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class FeelingLiveData(
    val context: Context
) : LiveData<MutableList<FeelingEntity>>() {

    init {
        loadData()
    }

    private fun loadData() {
        DoAsyncTask(context).execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class DoAsyncTask
    constructor(
        val context: Context
    ) : AsyncTask<Void, Void, MutableList<FeelingEntity>?>() {

        override fun doInBackground(vararg params: Void?): MutableList<FeelingEntity>? {
            try {
                val buf = StringBuilder()
                val json = context.assets.open(EditorFeelingRepositoryImpl.DATA_FEELING)
                val `in` = BufferedReader(InputStreamReader(json, "UTF-8"))
                var str: String?
                while (`in`.readLine().also { str = it } != null) {
                    buf.append(str)
                }
                val listType = object : TypeToken<MutableList<FeelingEntity>>() {}.type
                val feelingList = Gson().fromJson<MutableList<FeelingEntity>>(
                    buf.toString(),
                    listType
                )
                `in`.close()
                return feelingList
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }

        override fun onPostExecute(result: MutableList<FeelingEntity>?) {
            value = result
        }
    }
}