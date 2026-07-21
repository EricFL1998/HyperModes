package kotlin.reflect.jvm.internal;

/* JADX INFO: loaded from: classes2.dex */
class Util {
    public static Object getEnumConstantByName(Class<? extends Enum<?>> cls, String str) {
        return Enum.valueOf(cls, str);
    }
}
