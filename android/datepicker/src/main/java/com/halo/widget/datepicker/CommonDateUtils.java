/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.datepicker;


import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by ndn
 * <p>
 * Common date utilities.
 */
public class CommonDateUtils {
    // All the SimpleDateFormats in this class use the UTC timezone
    public static final SimpleDateFormat NO_YEAR_DATE_FORMAT =
            new SimpleDateFormat("--MM-dd", Locale.US);
    public static final SimpleDateFormat FULL_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public static final SimpleDateFormat DATE_AND_TIME_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    public static final SimpleDateFormat NO_YEAR_DATE_AND_TIME_FORMAT =
            new SimpleDateFormat("--MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    /**
     * Exchange requires 8:00 for birthdays
     */
    public final static int DEFAULT_HOUR = 8;
}