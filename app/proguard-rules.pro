# Sunshine Loan ProGuard & R8 Configuration

# Preserve line numbers for stack traces
-keepattributes SourceFile,LineNumberTable

# Preserve models used with Firestore / serialization
-keep class com.sunshineloan.app.model.** { *; }

# Firebase Auth & Firestore rules
-keepattributes *Annotation*
-keep class com.google.firebase.** { *; }
-keep interface com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keep interface com.google.android.gms.** { *; }
-keep class * extends com.google.firebase.components.ComponentRegistrar
-keep class com.google.firebase.provider.FirebaseInitProvider { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
-dontwarn javax.annotation.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

