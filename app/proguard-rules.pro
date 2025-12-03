# File Manager Pro - Optimized ProGuard Rules
# Samsung A35 5G - Android 14-16

# Keep - Application and core classes
-keep class com.filemanager.** { *; }
-keepclassmembers class com.filemanager.** { *; }

# Keep - Application class
-keep class com.filemanager.FileManagerApplication { *; }

# Keep - Activities
-keep public class * extends androidx.appcompat.app.AppCompatActivity
-keep public class * extends android.app.Activity

# Keep - Services
-keep public class * extends android.app.Service
-keepclassmembers class * extends android.app.Service {
    public <init>();
    public void on*();
}

# Keep - Models
-keep class com.filemanager.models.** { *; }
-keepclassmembers class com.filemanager.models.** {
    public <init>(...);
    public void set*(***);
    public *** get*();
}

# Keep - File operations
-keep class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep - AndroidX libraries (Google repositories only)
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

# Keep - Google Material Components
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# Keep - RecyclerView adapters
-keep public class * extends androidx.recyclerview.widget.RecyclerView$Adapter
-keep public class * extends androidx.recyclerview.widget.RecyclerView$ViewHolder

# Keep - Resource classes
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep - View binding
-keep class * extends androidx.viewbinding.ViewBinding {
    *;
}

# Keep - Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep - Enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Remove debug logging in release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# Keep - Native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep - Custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Keep - Annotations
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Samsung A35 5G specific optimizations
-optimizationpasses 5
-dontobfuscate
-dontoptimize !code/simplification/arithmetic

# ARM64 optimization
-dontpreverify
-verbose
-ignorewarnings

# For Samsung Exynos 1380
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

# Common Android rules
-keep public class * extends android.app.Application
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference

# Don't warn about missing support library
-dontwarn android.support.**

# File operation specific optimizations
-keep class java.nio.** { *; }
-dontwarn java.nio.**
-keep class java.io.** { *; }

# Keep all string constants used in file operations
-keepclassmembers class * {
    java.lang.String FILE_OPERATION_*;
}

# For debugging
-keepattributes SourceFile,LineNumberTable

# Samsung Knox compatibility (optional)
-keep class com.samsung.android.knox.** { *; }
-dontwarn com.samsung.android.knox.**

# Optimize string operations
-optimizations !code/allocation/variable
