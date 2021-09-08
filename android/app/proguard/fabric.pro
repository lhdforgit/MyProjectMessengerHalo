##---------------Start: proguard configuration for Fabric  ----------
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
# If you're using custom Eception
-keep public class * extends java.lang.Exception
##---------------End: proguard configuration for Fabric  ----------