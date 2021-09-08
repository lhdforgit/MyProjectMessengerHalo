/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.common.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TypefaceSpan;
import android.util.Patterns;
import android.widget.EditText;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.halo.common.utils.SpanBuilderUtil;
import com.halo.data.BuildConfig;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * @author ndn
 * Created by ndn
 * Created on 6/11/18.
 */
public final class Strings {
    private static final String FULLNAME_PATTERN =
            "^[a-zA-Z_ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶ" +
                    "ẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợ" +
                    "ụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s]+$";


    private Strings() {

    }

    public static String md5(String s) {
        if (s == null) {
            return "";
        }
        HashFunction hf = LegacyHashing.Companion.md5();
        HashCode hc = hf.newHasher().putString(s, Charsets.UTF_8).hash();
        return hc.toString();
    }

    public static String base64(String s) {
        byte[] data = s.getBytes(StandardCharsets.UTF_8);
        return Joiner.on(" ").join("Basic", BaseEncoding.base64().encode(data));
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidPhone(CharSequence target) {
        if (target != null && target.length() >= 8 && target.length() <= 20) {
            return !TextUtils.isEmpty(target) && android.util.Patterns.PHONE.matcher(target).matches();
        }
        return false;
    }

    public static boolean isValidPhonePayment(CharSequence target) {
        if (target != null && target.length() >= 8 && target.length() <= 13) {
            return !TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches();
        }
        return false;
    }

    public static boolean isValidLenghtInputInfo(CharSequence target) {
        if (target != null && target.length() <= 256) {
            return !TextUtils.isEmpty(target);
        }
        return false;
    }

    public static boolean verifyFullname(String fullname) {
        if (fullname == null) return false;
        return fullname.matches(FULLNAME_PATTERN);
    }

    public static boolean isValidName(CharSequence target) {
        String regx = "[a-zA-Z]";
        Pattern pattern = Pattern.compile(regx, CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isValidFulName(String target) {
        String regx = "[^a-zA-Z ]";
        Pattern pattern = Pattern.compile(regx, CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return !matcher.find();
    }

    /**
     * link special character in app
     */
    public static boolean isHaveSpecialCheractor(CharSequence target) {

        if (TextUtils.isEmpty(String.valueOf(target).trim())) {
            return true;
        }

        if (String.valueOf(target).contains("\\")) {
            return true;
        }

        String regx1 = "[$&+,:;=?@#|'<>.^*()%!/0-9_{}\"`~\\[\\]-]";
        Pattern pattern1 = Pattern.compile(regx1);
        Matcher matcher1 = pattern1.matcher(target);
        return matcher1.find();
    }

    /**
     * cho phép nhập số
     * */
    public static boolean isHaveSpecialCharacterTravelPort(CharSequence target) {

        if (TextUtils.isEmpty(String.valueOf(target).trim())) {
            return true;
        }

        if (String.valueOf(target).contains("\\")) {
            return true;
        }

        String regx1 = "[$&+,:;=?@#|'<>.^*()%!/_{}\"`~\\[\\]-]";
        Pattern pattern1 = Pattern.compile(regx1);
        Matcher matcher1 = pattern1.matcher(target);
        return matcher1.find();
    }

    /**
     * Chỉ các ký tự Latin là dc phép
     */
    public static boolean isValidFirstNameTravelPort(CharSequence target) {

        if (TextUtils.isEmpty(String.valueOf(target).trim())) {
            return true;
        }

        if (String.valueOf(target).contains("\\")) {
            return true;
        }

        String regx1 = "^(?=(.*[a-zA-Z]))([a-zA-Z 0-9]){2,55}$";
        Pattern pattern1 = Pattern.compile(regx1);
        Matcher matcher1 = pattern1.matcher(target);
        return matcher1.matches();
    }

    public static boolean isValidLastNameTravelPort(CharSequence target) {

        if (TextUtils.isEmpty(String.valueOf(target).trim())) {
            return true;
        }

        if (String.valueOf(target).contains("\\")) {
            return true;
        }

        String regx1 = "^(?=(.*[a-zA-Z]){2})([a-zA-Z 0-9]){2,55}$";
        Pattern pattern1 = Pattern.compile(regx1);
        Matcher matcher1 = pattern1.matcher(target);
        return matcher1.matches();
    }

    public static boolean isHaveSpecialPageName(CharSequence target) {
        if (TextUtils.isEmpty(String.valueOf(target).trim())) {
            return true;
        }
        if (String.valueOf(target).contains("\\")) {
            return true;
        }
        String regx1 = "[$&+,:;=?@#|'<>.^*()%!/_{}\"`~\\[\\]-]";
        Pattern pattern1 = Pattern.compile(regx1);
        Matcher matcher1 = pattern1.matcher(target);
        return matcher1.find();
    }

    public static boolean isValidInput(CharSequence target) {
        String regx = "[A-Za-z\\sÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]";
        Pattern pattern = Pattern.compile(regx, CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isValidNameTour(CharSequence target) {
        if (TextUtils.isEmpty(String.valueOf(target).trim())) {
            return true;
        }

        if (String.valueOf(target).contains("\\")) {
            return true;
        }

        String regx1 = "[0-9_!¡?÷?¿/\\\\+=@#$%ˆ&*(){}|~<>;:\\[\\]-]";
        Pattern pattern1 = Pattern.compile(regx1);
        Matcher matcher1 = pattern1.matcher(target);
        return matcher1.find();
    }

    public static boolean isHaveAccent(CharSequence target) {
        String regx = "[ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]";
        Pattern pattern = Pattern.compile(regx, CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isValidPassword(CharSequence target) {
        String regx = "[^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$]";
        Pattern pattern = Pattern.compile(regx, CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isContainsNumber(CharSequence target) {
        String regx = "([0-9])";
        Pattern pattern = Pattern.compile(regx, CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isUserNameLimit(CharSequence target) {
        int counter = 0;
        for (int i = 0; i < target.length(); i++) {
            if (Character.isWhitespace(target.charAt(i)))
                counter++;
        }

        return counter <= 4;
    }

    public static boolean isWebLinkType(String toString) {
        if (!TextUtils.isEmpty(toString)) {
            return toString.contains("https://") || toString.contains("http://");
        }
        return false;
    }

    public static boolean isValidPassport(CharSequence target) {
        String regx = "[$&+,:;=?@#|'<>.^*()%!/_{}\"`~\\[\\]-]";
        Pattern pattern = Pattern.compile(regx, CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return !matcher.find();
    }

    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<>();
        if (!TextUtils.isEmpty(text)) {
            String urlRegex = "((https?|http|https):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
            Pattern pattern = Pattern.compile(urlRegex, CASE_INSENSITIVE);
            Matcher urlMatcher = pattern.matcher(text);

            while (urlMatcher.find()) {
                containedUrls.add(text.substring(urlMatcher.start(0),
                        urlMatcher.end(0)));
            }
        }
        return containedUrls;
    }

    public static String formatSpace(String text) {
        if (!TextUtils.isEmpty(text)) {
            String result = text.replace("&nbsp;", " ").trim();
            result = result.replace("<br>", "\n");
            return result;
        }
        return text.trim();
    }

    public static void log(Object o) {
        if (BuildConfig.DEBUG) log("", o);
    }

    public static void log(String message, Object o) {

        String json = "";
        if (o instanceof String) {
            json = (String) o;
        } else {
            try {
                json = (o == null ? "NULL" : new Gson().toJson(o));
            }catch ( Exception e){
                json = "";
            }
        }
        int maxLogSize = 2600;
        int partSize = json.length() / maxLogSize;
        if (partSize > 0 && BuildConfig.DEBUG)
            Timber.d((TextUtils.isEmpty(message) ? "" : (message + ": ")) + "Size: " + json.length());

        for (int i = 0; i <= partSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = Math.min(end, json.length());
            if (BuildConfig.DEBUG) Timber.d(
                    (TextUtils.isEmpty(message)
                            ? "" : (message + ": "))
                            + (partSize > 0 ? "PART " + (i + 1) + ": " : "")
                            + json.substring(start, end));
        }
    }

    public static void checkMoreSpace(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains("  ")) {
                    editText.setText(s.toString().replace("  ", " "));
                    editText.setSelection(Objects.requireNonNull(editText.getText()).toString().length());
                }
            }
        });
    }

    public static void checkNotSpace(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains(" ")) {
                    editText.setText(s.toString().replace(" ", ""));
                    editText.setSelection(Objects.requireNonNull(editText.getText()).toString().length());
                }
            }
        });
    }

    public static void resetEditText(EditText editText) {
        editText.setText("");
        editText.requestFocus();
    }

    public static String convertNullToEmpty(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    public static CharSequence getStringStyle(String text, String subText, TypefaceSpan typefaceSpan) {
        if (text != null && !text.isEmpty()) {
            int index = text.indexOf('%');
            if (index >= 0 && index < text.length()) {
                String sub1 = text.substring(0, index - 1);
                String sub2 = text.substring(index + 3);
                SpanBuilderUtil sp = new SpanBuilderUtil();
                sp.appendWithSpace(sub1);
                sp.appendWithSpace(subText, typefaceSpan);
                sp.append(sub2);
                return sp.build();
            }
        }
        return null;
    }


}
