-dontwarn **

-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep public class org.example.Main {
    public static void main(java.lang.String[]);
}

-keep class org.example.EmptyClassOne {
    *;
}
-keep class org.example.EmptyClassTwo {
    *;
}
