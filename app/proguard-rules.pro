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
# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-keep class java.text.SimpleDateFormat

# For Cipher
-keep class javax.crypto.** { *; }
-dontwarn javax.crypto.**

-keep class java.security.**{  *;}
-dontwarn java.security.**

# Retrofit 2.X
## https://square.github.io/retrofit/ ##
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn javax.annotation.**
-dontwarn okio.**
-dontwarn okhttp3.internal.platform.* ## Add this to solve warnings like okhttp3.internal.platform.ConscryptPlatform: can't find referenced class org.conscrypt.OpenSSLProvider

-dontwarn com.fasterxml.jackson.core.JsonFactory
-dontwarn com.fasterxml.jackson.core.JsonToken
-dontwarn com.fasterxml.jackson.core.JsonParser
-dontwarn com.fasterxml.jackson.core.JsonGenerator



-dontwarn com.fasterxml.jackson.databind.**
-dontwarn javax.xml.stream.events.**
-dontwarn org.codehaus.jackson.**
-dontwarn org.apache.commons.logging.impl.**
-dontwarn org.apache.http.conn.scheme.**
-dontwarn org.apache.http.annotation.**
-dontwarn org.ietf.jgss.**
-dontwarn org.joda.convert.**
-dontwarn com.amazonaws.org.joda.convert.**
-dontwarn org.w3c.dom.bootstrap.**

#SDK split into multiple jars so certain classes may be referenced but not used
-dontwarn com.amazonaws.services.s3.**
-dontwarn com.amazonaws.services.sqs.**

-dontnote com.amazonaws.services.sqs.QueueUrlHandler

#Android-Link-Preview
-keeppackagenames org.jsoup.nodes


-keep class com.twilio.conversations.** { *; }
-keepattributes InnerClasses
-keep interface com.twilio.conversations.** { *; }
-keep enum com.twilio.conversations.** { *; }

## Keep native methods

-keepclasseswithmembernames class com.twilio.conversations.** {
    native <methods>;
}

## Keep callbacks from native
# ?

#======================================
# Local demo application configuration
#======================================

-keepclassmembers class **.R$* {
    public static <fields>;
}


## Google libraries

-dontwarn android.support.**
-keep class com.google.ads.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }
-keepattributes Signature