package com.hahalolo.pickercolor.listener

/**
 * Color Select Listener
 */
interface ColorListener {

    /**
     *
     * Call when user select color
     *
     * @param color Color Resource
     * @param colorHex Hex String of Color Resource
     */
    fun onColorSelected(color: Int, colorHex: String)
}
