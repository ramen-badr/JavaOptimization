-dontshrink
-dontoptimize
-dontobfuscate
-dontpreverify
-dontwarn **

-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep public class org.example.Main {
    public static void main(java.lang.String[]);
}
-keep class org.example.** {
    *;
}
