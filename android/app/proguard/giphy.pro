##---------------Start: proguard configuration for Giphy  ----------
-keep class com.giphy.sdk.core.** { *; }
-dontwarn com.giphy.sdk.core.*
-keepclassmembernames class com.giphy.sdk.core.models.** { *; }
-keepclassmembernames class com.giphy.sdk.core.network.response.** { *; }
##---------------End: proguard configuration for Giphy  ----------