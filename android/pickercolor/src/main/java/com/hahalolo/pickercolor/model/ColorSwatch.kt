package com.hahalolo.pickercolor.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Used for Color Palette Selection
 */
@Parcelize
enum class ColorSwatch(val value: String) : Parcelable {
    _50("50"),
    _100("100"),
    _200("200"),
    _300("300"),
    _400("400"),
    _500("500"),
    _600("600"),
    _700("700"),
    _800("800"),
    _900("900"),
    A100("a100"),
    A200("a200"),
    A300("a300"),
    A400("a400");
}
