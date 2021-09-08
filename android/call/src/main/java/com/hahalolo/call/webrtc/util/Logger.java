package com.hahalolo.call.webrtc.util;

import com.hahalolo.call.webrtc.QBRTCConfig;

import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

public class Logger
{
    private static Logger INSTANCE;
    private final String prefixTag;
    private static Set<String> omitLoggingClasses;
    
    public static Logger getInstance(final String prefixName) {
        synchronized (Logger.class) {
            if (Logger.INSTANCE == null) {
                Logger.INSTANCE = new Logger(prefixName);
            }
        }
        return Logger.INSTANCE;
    }
    
    public Logger(final String prefixTag) {
        this.prefixTag = prefixTag;
    }
    
    public static void setEnabledLog(final String tag, final boolean enable) {
        if (enable) {
            Logger.omitLoggingClasses.remove(tag);
        }
        else {
            Logger.omitLoggingClasses.add(tag);
        }
    }
    
    public void v(final String tag, final String msg) {
        if (QBRTCConfig.isDebugEnabled() && !Logger.omitLoggingClasses.contains(tag)) {
            Timber.v(msg);
        }
    }
    
    public void d(final String tag, final String msg) {
        if (QBRTCConfig.isDebugEnabled() && !Logger.omitLoggingClasses.contains(tag)) {
            Timber.d(msg);
        }
    }
    
    public void e(final String tag, final String msg) {
        if (QBRTCConfig.isDebugEnabled() && !Logger.omitLoggingClasses.contains(tag)) {
            Timber.e(msg);
        }
    }
    
    public void w(final String tag, final String msg) {
        if (QBRTCConfig.isDebugEnabled() && !Logger.omitLoggingClasses.contains(tag)) {
            Timber.w(msg);
        }
    }
    
    static {
        Logger.INSTANCE = null;
        Logger.omitLoggingClasses = new HashSet<>();
    }
}
