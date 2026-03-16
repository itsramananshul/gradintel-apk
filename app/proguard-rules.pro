# Gradintel ProGuard Rules

# Keep WebView JS interfaces
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep all classes in our package
-keep class com.gradintel.app.** { *; }

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# AndroidX
-keep class androidx.** { *; }

# Supabase / networking (called from WebView JS — no native SDK needed)
-dontwarn okhttp3.**
-dontwarn okio.**
