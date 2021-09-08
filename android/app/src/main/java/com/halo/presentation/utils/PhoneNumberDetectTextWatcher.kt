/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.utils

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.halo.common.utils.ktx.ValidUtils
import java.util.*

/**
 * Watches a [android.widget.TextView] and if a phone number is entered
 * will format it.
 *
 *
 * Stop formatting when the user
 *
 *  * Inputs non-dialable characters
 *  * Removes the separator in the middle of string.
 *
 *
 *
 * The formatting will be restarted once the text is cleared.
 *
 * The formatting is based on the given `countryCode`.
 *
 * @param countryCode the ISO 3166-1 two-letter country code that indicates the country/region
 * where the phone number is being entered.
 */
class PhoneNumberDetectTextWatcher(private var countryCode: String, private val phoneNumberDetectListener: PhoneNumberDetectListener) : TextWatcher {

    private val phoneUtil = PhoneNumberUtil.getInstance()

    interface PhoneNumberDetectListener {
        fun onDetected(isPhoneNumber: Boolean)

        fun onParsePhoneNumberResult(result: ParsePhoneNumber)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    @Synchronized
    override fun afterTextChanged(s: Editable) {
        try {
            parsePhoneNumber(s.toString(), countryCode, Locale.getDefault())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parsePhoneNumber(phoneNumber: String, defaultCountry: String, geocodingLocale: Locale) {
        // Trường hợp chưa nhập, hoặc xoá hết chữ
        if (TextUtils.isEmpty(phoneNumber) || !checkPhoneNumberFormat(phoneNumber)) {
            phoneNumberDetectListener.onDetected(false)
            return
        }

        val parsePhoneNumberResult = ParsePhoneNumber()
        var isPossible = false
        var isNumberValid = false
        var numberType: PhoneNumberUtil.PhoneNumberType = PhoneNumberUtil.PhoneNumberType.PERSONAL_NUMBER

        try {
            val number = phoneUtil.parseAndKeepRawInput(phoneNumber, defaultCountry)

            parsePhoneNumberResult.phoneNumber = phoneNumber

            parsePhoneNumberResult.defaultCountry = defaultCountry
            parsePhoneNumberResult.language = geocodingLocale.toLanguageTag()

            // Kiểm tra xem một số điện thoại có phải là một số có thể được cung cấp cho một số ở dạng chuỗi hay không và khu vực có thể quay số từ đó.
            isPossible = phoneUtil.isPossibleNumber(number)
            if (isPossible) {
                parsePhoneNumberResult.region = phoneUtil.getRegionCodeForNumber(number) ?: null
            }
            isNumberValid = phoneUtil.isValidNumber(number)
            if (isNumberValid) {
                parsePhoneNumberResult.countryCode = number.countryCode.toString()
                parsePhoneNumberResult.e164Format = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164)
                parsePhoneNumberResult.internationalFormat = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
            }
            parsePhoneNumberResult.nationalNumber = number.nationalNumber.toString()
            parsePhoneNumberResult.countryCodeSource = number.countryCodeSource.toString()
            parsePhoneNumberResult.originalFormat = phoneUtil.formatInOriginalFormat(number, defaultCountry)
            parsePhoneNumberResult.nationalFormat = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)

            numberType = phoneUtil.getNumberType(number)
        } catch (e: NumberParseException) {
            Log.e(javaClass.name, "NumberParseException was thrown: $e")
        } catch (e: Exception) {
            Log.e(javaClass.name, "Exception was thrown: $e")
        }

        // Trường hợp người dùng nhập số quốc tế mà không thêm vào dấu + hoặc 00 trước mã quốc tế


        // Trường hợp lib không thể parse được số điện thoại
        // 1. Sử dụng số điện thoại nội bộ mà không liên quan đến mã quốc gia
        // 2. Lib không thể parse
        // Check xem liệu có phải định dạng số điện thoại không, và người dùng phải chọn thủ công mã quốc gia
        if (!isNumberValid) {
            isNumberValid = checkPhoneNumberFormat(phoneNumber)
        }
        phoneNumberDetectListener.onDetected(isNumberValid)

        parsePhoneNumberResult.isNumberValid = isNumberValid
        parsePhoneNumberResult.isPossible = isPossible
        parsePhoneNumberResult.phoneNumber = phoneNumber
        parsePhoneNumberResult.numberType = numberType.toString()
        phoneNumberDetectListener.onParsePhoneNumberResult(parsePhoneNumberResult)

        if (parsePhoneNumberResult.isInvalid) {
            reparsePhoneNumberWithPlus(phoneNumber, defaultCountry, geocodingLocale)
        }
    }

    private fun reparsePhoneNumberWithPlus(phoneNumber: String, defaultCountry: String, geocodingLocale: Locale) {
        // Truong hop so dien thoai da bat dau bang dau +
        if (phoneNumber.startsWith("+")) {
            return
        }
        val phoneNumberPlus = formatPhoneNumberWithPlus(phoneNumber)

        val parsePhoneNumberResult = ParsePhoneNumber()
        var isPossible = false
        var isNumberValid = false
        var numberType: PhoneNumberUtil.PhoneNumberType = PhoneNumberUtil.PhoneNumberType.PERSONAL_NUMBER

        try {
            val number = phoneUtil.parseAndKeepRawInput(phoneNumberPlus, defaultCountry)

            parsePhoneNumberResult.phoneNumber = phoneNumberPlus

            parsePhoneNumberResult.defaultCountry = defaultCountry
            parsePhoneNumberResult.language = geocodingLocale.toLanguageTag()

            // Kiểm tra xem một số điện thoại có phải là một số có thể được cung cấp cho một số ở dạng chuỗi hay không và khu vực có thể quay số từ đó.
            isPossible = phoneUtil.isPossibleNumber(number)
            if (isPossible) {
                parsePhoneNumberResult.region = phoneUtil.getRegionCodeForNumber(number) ?: null
            }
            isNumberValid = phoneUtil.isValidNumber(number)
            if (isNumberValid) {
                parsePhoneNumberResult.countryCode = number.countryCode.toString()
                parsePhoneNumberResult.e164Format = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164)
                parsePhoneNumberResult.internationalFormat = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
            }
            parsePhoneNumberResult.nationalNumber = number.nationalNumber.toString()
            parsePhoneNumberResult.countryCodeSource = number.countryCodeSource.toString()
            parsePhoneNumberResult.originalFormat = phoneUtil.formatInOriginalFormat(number, defaultCountry)
            parsePhoneNumberResult.nationalFormat = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)

            numberType = phoneUtil.getNumberType(number)
        } catch (e: NumberParseException) {
            Log.e(javaClass.name, "NumberParseException was thrown: $e")
        } catch (e: Exception) {
            Log.e(javaClass.name, "Exception was thrown: $e")
        }

        // Trường hợp người dùng nhập số quốc tế mà không thêm vào dấu + hoặc 00 trước mã quốc tế


        // Trường hợp lib không thể parse được số điện thoại
        // 1. Sử dụng số điện thoại nội bộ mà không liên quan đến mã quốc gia
        // 2. Lib không thể parse
        // Check xem liệu có phải định dạng số điện thoại không, và người dùng phải chọn thủ công mã quốc gia
        if (!isNumberValid) {
            isNumberValid = checkPhoneNumberFormat(phoneNumber)
        }
        phoneNumberDetectListener.onDetected(isNumberValid)

        parsePhoneNumberResult.isNumberValid = isNumberValid
        parsePhoneNumberResult.isPossible = isPossible
        parsePhoneNumberResult.phoneNumber = phoneNumber
        parsePhoneNumberResult.numberType = numberType.toString()
        phoneNumberDetectListener.onParsePhoneNumberResult(parsePhoneNumberResult)
    }

    private fun formatPhoneNumberWithPlus(phoneNumber: String?): String {
        return phoneNumber?.run {
            return if (phoneNumber.startsWith("+")) {
                phoneNumber
            } else {
                "+$phoneNumber"
            }
        } ?: ""
    }

    private fun checkPhoneNumberFormat(phoneNumber: String): Boolean {
        return ValidUtils.isValidPhone(phoneNumber)
    }

    public class ParsePhoneNumber {
        // Số điện thoại được người dùng nhập vào từ bàn phím
        var phoneNumber: String? = null
        // Mã quốc gia được dùng để parse số điện thoại người dùng nhập vào. Và được lấy từ dữ liệu nhập vào
        var defaultCountry: String? = null
        // Ngôn ngữ hiển thị
        var language: String? = null
        // Mã số điện thoại quốc gia được parse từ PhoneNumberUtils
        var countryCode: String? = null
        /* Các định dạng hiển thị số điện thoại */
        var nationalNumber: String? = null
        var countryCodeSource: String? = null

        // Phone Number region
        var region: String? = null

        /* Các định dạng hiển thị số điện thoại */
        var e164Format: String? = null
        var originalFormat: String? = null
        var nationalFormat: String? = null
        var internationalFormat: String? = null

        /* nhanh chóng đoán xem một số có phải là số điện thoại có thể hay không bằng cách chỉ sử dụng thông tin độ dài,
         nhanh hơn nhiều so với xác thực đầy đủ.*/
        var isPossible: Boolean = false

        /* xác nhận đầy đủ số điện thoại cho một vùng bằng thông tin về độ dài và tiền tố. */
        var isNumberValid: Boolean = false
        /* có được loại số dựa trên chính số đó; có thể phân biệt Đường dây cố định, Di động, Không thu phí, Tỷ lệ cao cấp, Chi phí chung, VoIP, Số cá nhân, UAN, Máy nhắn tin và Thư thoại (bất cứ khi nào khả thi). */
        var numberType: String? = null

        val isInvalid: Boolean
            get() {
                // Không xác định được mã quốc gia và khu vực
                return TextUtils.isEmpty(countryCode) && TextUtils.isEmpty(region)
            }

        override fun toString(): String {
            return "ParsePhoneNumber(\nphoneNumber=$phoneNumber \n defaultCountry=$defaultCountry \n language=$language \n countryCode=$countryCode " +
                    "\nnationalNumber=$nationalNumber \n countryCodeSource=$countryCodeSource \n region=$region \n e164Format=$e164Format " +
                    " \noriginalFormat=$originalFormat  \nnationalFormat=$nationalFormat  \ninternationalFormat=$internationalFormat  " +
                    "\nisPossible=$isPossible  \nisNumberValid=$isNumberValid \n numberType=$numberType)"
        }
    }
}
