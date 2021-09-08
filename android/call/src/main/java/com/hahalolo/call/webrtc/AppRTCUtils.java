package com.hahalolo.call.webrtc;

import android.os.Build;
import android.util.Log;

final class AppRTCUtils
{
    private AppRTCUtils() {
    }
    
    public static void assertIsTrue(final boolean condition) {
        if (!condition) {
            throw new AssertionError((Object)"Expected condition to be true");
        }
    }
    
    public static String getThreadInfo() {
        return "@[name=" + Thread.currentThread().getName() + ", id=" + Thread.currentThread().getId() + "]";
    }
    
    public static void logDeviceInfo(final String tag) {
        Log.d(tag, "Android SDK: " + Build.VERSION.SDK_INT + ", Release: " + Build.VERSION.RELEASE + ", Brand: " + Build.BRAND + ", Device: " + Build.DEVICE + ", Id: " + Build.ID + ", Hardware: " + Build.HARDWARE + ", Manufacturer: " + Build.MANUFACTURER + ", Model: " + Build.MODEL + ", Product: " + Build.PRODUCT);
    }
    
    public static class NonThreadSafe
    {
        private final Long threadId;
        
        public NonThreadSafe() {
            this.threadId = Thread.currentThread().getId();
        }
        
        public boolean calledOnValidThread() {
            return this.threadId.equals(Thread.currentThread().getId());
        }
    }
}
