-dontwarn **

-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep public class org.example.Main {
    public static void main(java.lang.String[]);
}

-keep,allowobfuscation class org.example.EmptyClass* {
    *** keepByName(...);
}
-keepclassmembernames class org.example.EmptyClass* {
    *** keepByName(...);
}
