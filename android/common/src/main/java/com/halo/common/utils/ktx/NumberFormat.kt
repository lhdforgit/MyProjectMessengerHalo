/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils.ktx

import android.annotation.SuppressLint
import com.halo.common.utils.ktx.NumberFormat.suffixes
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*

/**
 * Created by ndn on 10/15/18.
 */
object NumberFormat {

    /**
     * {@see https://stackoverflow.com/questions/12490793/android-currency-symbol-ordering}
     * Not all countries expect the currency symbol before the amount.
     *
     *
     * If you always want the currency format to match an Americanised expectation, leave the locale as Locale.US.
     * If you want the currency to display in a localised way, leave your implementation as is.
     *
     *
     * See this brief guide (from Microsoft, no less):
     *
     *
     * Globalisation Step-by-Step {@see https://docs.microsoft.com/en-us/globalization/locale/currency-formatting}
     *
     *
     * I'd guess what you may be trying to achieve is to display the currency in a format appropriate to its locale?
     * If that's the case, just match the locale to the currency you're using, before calling the method.
     *
     *
     * Note that the format can even vary in the same country.
     * In Canada, it's reasonably common to see English speakers use the format $50.00, whereas French-Canadians may use 50,00 $.
     *
     *
     * Also see this question on UX:
     *
     *
     * https://ux.stackexchange.com/questions/22574/where-to-place-currency-symbol-when-localizing-and-what-to-do-with-odd-symbols
     *
     *
     * Without currency {@see https://stackoverflow.com/questions/8658205/format-currency-without-currency-symbol}
     *
     * @param curr  the ISO 4217 code of the currency, user choose in app setting
     * @param value price number
     * @return price format with curr
     */
    @JvmStatic
    fun priceFormatWithCurr(curr: String, value: Double): String {
        return try {
            val format = NumberFormat.getCurrencyInstance()
            val currency = Currency.getInstance(curr)
            format.currency = currency
            //https://en.wikipedia.org/wiki/ISO_4217
            // The number of digits after the decimal separator
            format.maximumFractionDigits = currency.defaultFractionDigits
            format.format(value)
        } catch (e: Exception) {
            priceFormatWithoutCurr(
                curr,
                value
            )
        }
    }

    @SuppressLint("DefaultLocale")
    @JvmStatic
    fun priceFormatWithoutCurr(curr: String, value: Double): String {
        return try {
            val locale = Locale.getDefault()
            val sym = DecimalFormatSymbols(locale)
            sym.groupingSeparator = '.'
            val decimalFormat: DecimalFormat =
                NumberFormat.getNumberInstance(locale) as DecimalFormat
            decimalFormat.applyPattern(curr.toUpperCase() + " ##,###")
            decimalFormat.decimalFormatSymbols = sym
            decimalFormat.maximumFractionDigits = Currency.getInstance(curr).defaultFractionDigits
            return decimalFormat.format(value)
        } catch (e: Exception) {
            "${curr.toUpperCase()} ${value.toInt()}"
        }
    }
    /* --- Hiển thị giá trị lớn theo 1k, 1m --- */

    val suffixes = TreeMap<Long, String>()

    init {
        suffixes[1_000L] = "k"
        suffixes[1_000_000L] = "M"
        suffixes[1_000_000_000L] = "G"
        suffixes[1_000_000_000_000L] = "T"
        suffixes[1_000_000_000_000_000L] = "P"
        suffixes[1_000_000_000_000_000_000L] = "E"
    }

    @JvmStatic
    fun formatK(value: Long): String {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == java.lang.Long.MIN_VALUE) return formatK(
            java.lang.Long.MIN_VALUE + 1
        )
        if (value < 0) return "-" + formatK(
            -value
        )
        if (value < 1000) return value.toString() //deal with easy case

        val e = suffixes.floorEntry(value)
        val divideBy = e!!.key
        val suffix = e.value

        val truncated = value / (divideBy!! / 10) //the number part of the output times 10
        val hasDecimal = truncated < 100 && truncated / 10.0 != (truncated / 10).toDouble()
        return if (hasDecimal) (truncated / 10.0).toString() + suffix else (truncated / 10).toString() + suffix
    }
}

fun Double.priceFormatWithCurr(curr: String): String {
    return try {
        val format = NumberFormat.getCurrencyInstance()
        val currency = Currency.getInstance(curr)
        format.currency = currency
        //https://en.wikipedia.org/wiki/ISO_4217
        // The number of digits after the decimal separator
        format.maximumFractionDigits = currency.defaultFractionDigits
        format.format(this)
    } catch (e: Exception) {
        this.priceFormatWithoutCurr(curr)
    }
}

@SuppressLint("DefaultLocale")
fun Double.priceFormatWithoutCurr(curr: String): String {
    return try {
        val locale = Locale.getDefault()
        val sym = DecimalFormatSymbols(locale)
        sym.groupingSeparator = '.'
        val decimalFormat: DecimalFormat =
            NumberFormat.getNumberInstance(locale) as DecimalFormat
        decimalFormat.applyPattern(curr.toUpperCase() + " ##,###")
        decimalFormat.decimalFormatSymbols = sym
        decimalFormat.maximumFractionDigits = Currency.getInstance(curr).defaultFractionDigits
        return decimalFormat.format(this)
    } catch (e: Exception) {
        "${curr.toUpperCase()} ${this.toInt()}"
    }
}

fun Long.formatK(): String {
    //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
    if (this == java.lang.Long.MIN_VALUE) return (java.lang.Long.MIN_VALUE + 1).formatK()
    if (this < 0) return "-" + (-this).formatK()
    if (this < 1000) return toString() //deal with easy case

    val e = suffixes.floorEntry(this)
    val divideBy = e!!.key
    val suffix = e.value

    val truncated = this / (divideBy!! / 10) //the number part of the output times 10
    val hasDecimal = truncated < 100 && truncated / 10.0 != (truncated / 10).toDouble()
    return if (hasDecimal) (truncated / 10.0).toString() + suffix else (truncated / 10).toString() + suffix
}
