# consumer-rules.pro

# Retain all public classes and methods in your library's API package
-keep class io.velocitycareerlabs.** {
    public *;
}

# Keep annotations that may be needed by reflection or tooling
-keepattributes *Annotation*

# Kotlin metadata
-keep class kotlin.Metadata { *; }

# If you're using coroutines or suspend functions
-dontwarn kotlinx.coroutines.**
