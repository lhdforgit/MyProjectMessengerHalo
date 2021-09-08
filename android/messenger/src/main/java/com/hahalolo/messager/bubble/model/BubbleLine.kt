package com.hahalolo.messager.bubble.model

class BubbleLine(val x1: Double, val y1: Double, var x2: Double, var y2: Double) {
    fun intersects(r: BubbleRectangle): Boolean {
        return r.intersectsLine(x1, y1, x2, y2)
    }

    private fun f(x: Double): Double {
        val slope = (y2 - y1) / (x2 - x1)
        return y1 + (x - x1) * slope
    }

    fun changeLength(length: Double): BubbleLine {
        val newX1 = x1 - length / 2
        val newX2 = x2 - length / 2
        return BubbleLine(newX1, f(newX1), newX2, f(newX2))
    }
}