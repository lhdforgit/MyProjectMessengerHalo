package com.hahalolo.messager.bubble.close

import com.facebook.rebound.Spring

interface BubbleCloseListener{
    fun onPositionUpdate(valueX: Double, valueY:Double, width:Int, height:Int )
    fun onXSpringUpdate()
    fun onYSpringUpdate(spring: Spring)
}