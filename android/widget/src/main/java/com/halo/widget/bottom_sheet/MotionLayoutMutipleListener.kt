/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.bottom_sheet

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import com.halo.common.utils.LogTrace
import com.halo.common.utils.T
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.resume

open class MotionLayoutMutipleListener @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    def: Int = 0) : MotionLayout(context, attributes, def) {
    private val listeners = CopyOnWriteArrayList<TransitionListener>()

    init {
        super.setTransitionListener(object : TransitionListener {
            override fun onTransitionTrigger(motionLayout: MotionLayout, triggerId: Int, positive: Boolean, progress: Float) {
                listeners.forEach {
                    it.onTransitionTrigger(motionLayout, triggerId, positive, progress)
                }
            }

            override fun onTransitionStarted(motionLayout: MotionLayout, startId: Int, endId: Int) {
                listeners.forEach {
                    it.onTransitionStarted(motionLayout, startId, endId)
                }
            }

            override fun onTransitionChange(motionLayout: MotionLayout, startId: Int, endId: Int, progress: Float) {
                listeners.forEach {
                    it.onTransitionChange(motionLayout, startId, endId, progress)
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                listeners.forEach {
                    it.onTransitionCompleted(motionLayout, currentId)
                }
            }
        })
    }

    suspend fun awaitTransitionComplete(transitionId: Int, timeout: Long = 5000L) {
        // If we're already at the specified state, return now
        if (currentState == transitionId) return

        var listener: TransitionListener? = null

        try {
            withTimeout(timeout) {
                suspendCancellableCoroutine<Unit>  { continuation ->
                    val l = object : TransitionAdapter() {
                        override fun onTransitionCompleted(
                            motionLayout: MotionLayout,
                            currentId: Int) {
                            LogTrace.traceE(
                                    T.TEST,
                            "Transition  onTransitionCompleted $transitionId"
                            )
                            if (currentId == transitionId) {
                                LogTrace.trace(
                                    T.TEST,
                                    "Transition  onTransitionCompleted $transitionId"
                                )
                                continuation.resume(Unit)
                                removeTransitionListener(this)
                            }
                        }
                    }

                    continuation.invokeOnCancellation {
                        LogTrace.trace(T.TEST, "Transition  Remove Listener")
                        removeTransitionListener(l)
                    }
                    // And finally add the listener
                    addTransitionListener(l)
                    listener = l
                }
            }
        } catch (tex: TimeoutCancellationException) {
            // Transition didn't happen in time. Remove our listener and throw a cancellation
            // exception to let the coroutine know
            listener?.let(::removeTransitionListener)
            throw CancellationException(
                "Transition to state with id: $transitionId did not" +
                        " complete in timeout.", tex
            )
        }
    }

    override fun addTransitionListener(listener: TransitionListener) {
        listeners.addIfAbsent(listener)
    }

    override fun removeTransitionListener(listener: TransitionListener): Boolean {
        listeners.remove(listener)
        return true
    }
}