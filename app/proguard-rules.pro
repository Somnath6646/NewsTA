-keepclassmembers class * extends androidx.work.CoroutineWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}


-keep class com.newsta.android.remote.data.*{ *; }

-keep class com.newsta.android.responses.*{ *; }

-keep class com.newsta.android.utils.models.*{ *; }

#Crashlytics
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.