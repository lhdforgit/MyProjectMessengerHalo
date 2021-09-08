package com.hahalolo.incognito.presentation.create.forward.container

import android.text.TextUtils
import com.hahalolo.incognito.presentation.create.forward.view.ForwardStatus
import com.hahalolo.incognito.presentation.setting.model.ContactModel
import com.halo.common.utils.list.ListUtils
import kotlinx.coroutines.*

object ForwardTaskUtil {
    fun createTask(
        item: ContactModel,
        listTask: MutableList<Pair<String, CoroutineScope>>,
        callback: ForwardTaskCallback
    ) {
        when (item.status) {
            ForwardStatus.SEND -> {
                val scope = CoroutineScope(Dispatchers.Main)
                val task = item.id to scope
                scope.launch {
                    callback.loading(item)
                    listTask.add(task)
                    delay(5000)
                    listTask.remove(task)
                    callback.success(item)
                }
            }
            ForwardStatus.LOADING -> {
                getTask(item.id, listTask)?.apply {
                    second.let { task ->
                        task.cancel()
                        listTask.remove(this)
                        callback.cancel(item)
                    }
                }
            }
        }
    }

    private fun getTask(
        id: String,
        listTask: MutableList<Pair<String, CoroutineScope>>
    ): Pair<String, CoroutineScope>? {
        ListUtils.getDataPosition(listTask) { input -> TextUtils.equals(input?.first, id) }?.let {
            return it
        }
        return null
    }
}

interface ForwardTaskCallback {
    fun loading(item: ContactModel)
    fun cancel(item: ContactModel)
    fun success(item: ContactModel)
}