##---------------Begin: proguard configuration for Glide  ----------
-verbose
# Use ProGuard only to get rid of unused classes
-dontobfuscate
-dontoptimize
-keepattributes *
-keep class !com.bumptech.glide.repackaged.**,com.bumptech.glide.**

# Keep the entry point to this library, see META-INF\services\javax.annotation.processing.Processor
-keep class com.bumptech.glide.annotation.compiler.GlideAnnotationProcessor


# "duplicate definition of library class"
-dontnote sun.applet.**
# "duplicate definition of library class"
-dontnote sun.tools.jar.**
# Reflective accesses in com.google.common.util.concurrent.* and some others
-dontnote com.bumptech.glide.repackaged.com.google.common.**
# com.google.common.collect.* and some others (….common.*.*)
-dontwarn com.google.j2objc.annotations.Weak
# com.google.common.util.concurrent.FuturesGetChecked$GetCheckedTypeValidatorHolder$ClassValueValidator
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#-dontwarn **

#java.lang.IllegalStateException: GeneratedAppGlideModuleImpl is implemented incorrectly.
#If you've manually implemented this class, remove your implementation.
#The Annotation processor will generate a correct implementation.
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
##---------------End: proguard configuration for Glide  ----------