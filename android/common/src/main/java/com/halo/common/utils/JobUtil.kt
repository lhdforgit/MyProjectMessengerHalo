/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import java.util.*


object JobUtil {

    /**
     * Run 1 job and waiting to finish
     * */
    inline fun <reified T : CoroutineDispatcher> doJob(
        dispatcher: T,
        noinline doF: suspend CoroutineScope.() -> Unit
    ) {
        val scope = CoroutineScope(dispatcher)
        val job = scope.launch {
            try {
                doF()
            } finally {
                if (!this.isActive) cancel()
            }
        }
        job.invokeOnCompletion {
            if (!job.isCancelled) {
                job.cancel()
            }
            scope.cancel()
        }
    }

    inline fun <reified T : CoroutineDispatcher> joinJob(
        dispatcher: T,
        noinline doF: suspend CoroutineScope.() -> Unit
    ) {
        CoroutineScope(dispatcher).launch {
            try {
                launch { doF() }.join()
            } finally {
                delay(10)
            }
        }
    }

    inline fun <reified T : CoroutineDispatcher> joinJob(
        scope: CoroutineScope,
        dispatcher: T,
        noinline doF: suspend CoroutineScope.() -> Unit
    ) {
        scope.launch(dispatcher) {
            try {
                launch { doF() }.join()
            } finally {
                delay(10)
            }
        }
    }

    inline fun <reified T : CoroutineDispatcher> joinJobAndCancel(
        scope: CoroutineScope,
        dispatcher: T,
        noinline doF: suspend CoroutineScope.() -> Unit
    ) {
        scope.launch(dispatcher) {
            val job = launch { doF() }
            try {
                job.join()
            } finally {
                job.cancel()
            }
        }
    }

    inline fun <reified T : CoroutineDispatcher> joinJobAndCancelScope(
        scope: CoroutineScope,
        dispatcher: T,
        noinline doF: suspend CoroutineScope.() -> Unit
    ) {
        scope.launch(dispatcher) {
            try {
                launch { doF() }.join()
            } finally {
                delay(10)
                cancel()
            }
        }
    }


    inline fun <reified T : CoroutineDispatcher> doJobComplete(
        dispatcher: T,
        noinline doF: suspend CoroutineScope.() -> Unit,
        noinline onComplete: (CoroutineScope.() -> Unit)? = null
    ) {
        val scope = CoroutineScope(dispatcher)
        val job = scope.launch {
            doF()
        }
        job.invokeOnCompletion {
            onComplete?.invoke(scope)
            if (!job.isCancelled) {
                job.cancel()
            }
            scope.cancel()
        }
    }


    private var listGlobalJobAutoCancel = Collections.synchronizedList(
        mutableListOf<Pair<String, Job>>()
    )

    internal fun <T : CoroutineDispatcher> globalJobAutoCancel(
        dispatcher: T,
        tag: String,
        delayTime: Long = 0L,
        doF: suspend CoroutineScope.() -> Unit
    ) {
        val oldList = listGlobalJobAutoCancel
        var jobNew: Job? = null
        val listTag = listGlobalJobAutoCancel.filter { it.first == tag }
        synchronized(listTag) {
            if (listTag.isEmpty()) {
                jobNew = CoroutineScope(dispatcher).launch(dispatcher) {
                    jobNew?.let {
                        oldList.add(tag to it)
                        listGlobalJobAutoCancel = oldList
                    }
                    delay(delayTime)
                    doF(this)
                    cancel("Complete now cancel")
                }
            } else {
                listTag.forEach {
                    val job = it.second
                    job.cancel("Cancel")
                    oldList.remove(it)
                }

                listTag.lastOrNull()?.apply {
                    second.invokeOnCompletion {
                        jobNew = CoroutineScope(dispatcher).launch {
                            jobNew?.let {
                                oldList.add(tag to it)
                                listGlobalJobAutoCancel = oldList
                            }
                            delay(delayTime)
                            doF(this)
                        }
                        jobNew?.invokeOnCompletion {
                            jobNew?.cancel()
                            oldList.filter { it.first == tag }.forEach {
                                oldList.remove(it)
                            }
                            listGlobalJobAutoCancel = oldList
                        }
                    }
                }
            }
        }
    }

    private var job: Job? = null
    fun <T : CoroutineDispatcher> doJobNotCancelable(
        dispatcher: T,
        lifecycleOwner: LifecycleOwner,
        doF: suspend CoroutineScope.() -> Unit
    ) {

        if (job?.isActive == false || job == null) {
            job = lifecycleOwner.lifecycleScope.launch {
                withContext(dispatcher) {
                    doF.invoke(this)
                }
            }
            job?.invokeOnCompletion {
                job?.cancel()
            }
        }
    }

    fun <T : CoroutineDispatcher> doJobNotCancelable(
        dispatcher: T,
        doF: suspend CoroutineScope.() -> Unit
    ) {
        if (job?.isActive == false || job == null) {
            job = CoroutineScope(dispatcher).launch {
                withContext(dispatcher) {
                    doF.invoke(this)
                }
            }
            job?.invokeOnCompletion {
                job?.cancel()
            }
        }
    }
}

/**
 * --------------------------------------------------------------------------------------------------
 * */
fun runJobMain(doF: suspend CoroutineScope.() -> Unit) = runJob(Dispatchers.Main) { doF() }

fun runJobMainComplete(
    doF: suspend CoroutineScope.() -> Unit,
    onComplete: (CoroutineScope.() -> Unit)? = null
) = runJob(Dispatchers.Main, { doF() }, onComplete)

infix fun CoroutineScope.runDefault(value: suspend CoroutineScope.() -> Unit) {
    launch {
        runJob(Dispatchers.Default) { value.invoke(this) }
    }
}

infix fun CoroutineScope.joinJobAndCancel(value: suspend CoroutineScope.() -> Unit) {
    joinJobAndCancel(Dispatchers.Main) { value.invoke(this) }
}

infix fun CoroutineScope.joinJobAndCancelScope(value: suspend CoroutineScope.() -> Unit) {
    joinJobAndCancelScope(Dispatchers.Main) { value.invoke(this) }
}

infix fun CoroutineScope.joinJob(value: suspend CoroutineScope.() -> Unit) {
    joinJob(Dispatchers.Main) { value.invoke(this) }
}

infix fun CoroutineScope.shiftMain(doF: suspend CoroutineScope.() -> Unit) =
    runJob(Dispatchers.Main) { doF() }

inline fun <reified T : CoroutineDispatcher> LifecycleOwner.runNotCancelable(
    value: T,
    noinline doF: suspend CoroutineScope.() -> Unit
) = JobUtil.doJobNotCancelable(value, this, doF)

inline fun <reified T : CoroutineDispatcher> runJob(
    value: T,
    noinline doF: suspend CoroutineScope.() -> Unit
) = JobUtil.doJob(value, doF)

inline fun <reified T : CoroutineDispatcher> CoroutineScope.joinJob(
    value: T,
    noinline doF: suspend CoroutineScope.() -> Unit
) = JobUtil.joinJob(this, value, doF)

inline fun <reified T : CoroutineDispatcher> CoroutineScope.joinJobAndCancelScope(
    value: T,
    noinline doF: suspend CoroutineScope.() -> Unit
) {
    JobUtil.joinJobAndCancelScope(this, value, doF)
}

inline fun <reified T : CoroutineDispatcher> CoroutineScope.joinJobAndCancel(
    value: T,
    noinline doF: suspend CoroutineScope.() -> Unit
) = JobUtil.joinJobAndCancel(this, value, doF)

inline fun <reified T : CoroutineDispatcher> runJob(
    value: T,
    noinline doF: suspend CoroutineScope.() -> Unit,
    noinline onComplete: (CoroutineScope.() -> Unit)? = null
) = JobUtil.doJobComplete(value, doF, onComplete)