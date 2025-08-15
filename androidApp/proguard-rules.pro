# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep Kotlin Metadata
-keep class kotlin.Metadata { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}

# Keep Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.cherry.kmp.**$$serializer { *; }
-keepclassmembers class com.cherry.kmp.** {
    *** Companion;
}
-keepclasseswithmembers class com.cherry.kmp.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Ktor client classes
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean

# Keep Compose runtime classes
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.navigation.** { *; }

# Keep Koin DI classes
-keep class org.koin.** { *; }
-keep class kotlin.reflect.jvm.internal.** { *; }

# Keep Room database classes
-keep class androidx.room.** { *; }
-keep class androidx.sqlite.** { *; }
-keep @androidx.room.Entity class *
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Dao class * { *; }

# Keep data classes and models
-keep class com.cherry.kmp.domain.model.** { *; }
-keep class com.cherry.kmp.data.local.entity.** { *; }

# Keep DataStore classes
-keep class androidx.datastore.** { *; }

# Keep Firebase classes (if using)
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Keep native method names for KMP
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enum classes
-keepclassmembers enum * { *; }

# Remove logging in release builds for security
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-assumenosideeffects class java.io.PrintStream {
    public void println(%);
    public void println(**);
}

# Remove debug information
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile