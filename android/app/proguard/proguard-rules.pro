##---------------Begin: proguard configuration for Common  ----------
# https://gist.github.com/jemshit/767ab25a9670eb0083bafa65f8d786bb

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

# For stack traces
-keepattributes SourceFile, LineNumberTable

# Get rid of package names, makes file smaller
-repackageclasses

# This optimization conflicts with how Retrofit uses proxy objects without concrete implementations
-optimizations !method/removal/parameter

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Required for classes created and used from JNI code (on C/C++ side)
-keep, includedescriptorclasses class in.uncod.android.bypass.Document { *; }
-keep, includedescriptorclasses class in.uncod.android.bypass.Element { *; }

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class org.jsoup.nodes.Entities

-dontwarn com.android.org.conscrypt.SSLParametersImpl
-dontwarn dalvik.system.CloseGuard
-dontwarn kotlin.internal.**
-dontwarn kotlin.reflect.jvm.internal.ReflectionFactoryImpl
-dontwarn org.apache.harmony.xnet.provdier.jsse.SSLParametersImpl
-dontwarn org.conscrypt.**
-dontwarn sun.security.ssl.SSLContext.Impl
-dontwarn javax.**
-dontwarn sun.reflect.**
-dontwarn java.awt.event.**
-dontwarn org.mozilla.**
-dontwarn org.joda.convert.**
-dontwarn com.uber.javaxextras.FieldsMethodsAndParametersAreNonNullByDefault

-keepclasseswithmembernames class * {
    native <methods>;
}
##---------------End: proguard configuration for Common  ----------



##---------------Start: proguard configuration for LeakCanary  ----------
-keep class org.eclipse.mat.** { *; }
-keep class com.squareup.leakcanary.** { *; }
##---------------End: proguard configuration for LeakCanary  ----------



##---------------Start: proguard configuration for Stetho  ----------
-keep class com.facebook.stetho.** {
  *;
}
-dontwarn com.facebook.stetho.**

-keep class com.uphyca.** { *; }
##---------------End: proguard configuration for Stetho  ----------


##---------------Start: proguard configuration for OkDownload  ----------
-keepnames class com.halo.okdownload.core.connection.DownloadOkHttp3Connection
-keep class com.halo.okdownload.core.** { *; }
##---------------End: proguard configuration for OkDownload  ----------



##---------------Start: proguard configuration for MongoResponseEntity  ----------
# Fix bug TypeToken.getParameterized(List.class, dataType).getType()
-keep class * extends com.halo.data.entities.mongo.common.MongoResponseEntity
##---------------End: proguard configuration for MongoResponseEntity  ----------

-keep class com.halo.presentation.chat.** { *; }