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

# Preserve vcl api
-keep class io.velocitycareerlabs.api.** { *; }

# Preserve EncryptedSharedPreferences classes
-keep class androidx.security.crypto.** { *; }

# Gson / JSON mapping safety
-keep class com.nimbusds.** { *; }
-dontwarn com.nimbusds.**

# Keep all annotations
-keepattributes *Annotation*

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# AndroidX test compatibility
-dontwarn org.junit.**
-dontwarn org.mockito.**
-dontwarn org.robolectric.**
