##---------------Start: proguard configuration for WorkManager  ----------
-keepclassmembers class * extends androidx.work.Worker {
    public <init>(android.content.Context,androidx.work.WorkerParameters);
}
##---------------End: proguard configuration for WorkManager  ----------