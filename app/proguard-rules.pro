
-keepattributes *Annotation*,InnerClasses
-dontnote retrofit2.Platform
-keepattributes Signature
-keepattributes Exceptions

# Kotlinx serialization
-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.** { *; }