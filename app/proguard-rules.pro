# Sunshine Loan ProGuard & R8 Configuration

# Preserve line numbers for stack traces
-keepattributes SourceFile,LineNumberTable

# Preserve models used with Firestore / serialization
-keep class com.sunshineloan.app.model.** { *; }

# Firebase Auth & Firestore rules
-keepattributes *Annotation*
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
-dontwarn javax.annotation.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

