package com.hahalolo.messager.bubble.head

import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.Observer
import com.facebook.rebound.Spring

interface BubbleHeadListener {

    fun currentBubbleHead(v: View?): BubbleHead?

    fun activeBubbleHead(): BubbleHead?

    fun updateActiveBubbleHead(bubbleHead: BubbleHead)

    fun topBubbleHead(): BubbleHead?

    fun bubbleHeadIndex(bubbleHead: BubbleHead): Int

    fun collapse()

    fun showContentMove()

    fun hideContentMove()

    fun onChatHeadSpringUpdate(bubbleHead: BubbleHead, spring: Spring, totalVelocity: Int)

    fun removeBubble(bubbleId : String)

    fun observerActiveBubble(observer: Observer<BubbleHead>)

    fun observerToggled(observer: Observer<Boolean>)

    //TODO UPDATE CLOSE WHEN MOVE BUBBLE_HEAD
    fun showBubbleClose(head: BubbleHead)
    fun hideBubbleClose(head: BubbleHead)
    //Return true if can update location, Trả về true nếu bubble không bị rơi vào khoảng remove
    fun moveBubbleHead(event: MotionEvent, head: BubbleHead): Boolean

    //TODO for Bubble Home
    fun showHomeConversation()

    fun canTouchToBubbleHead(event: MotionEvent, bubbleHead: BubbleHead): Boolean

}