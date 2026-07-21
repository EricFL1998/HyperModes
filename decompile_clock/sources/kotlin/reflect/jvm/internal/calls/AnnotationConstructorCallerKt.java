package kotlin.reflect.jvm.internal.calls;

import com.xiaomi.onetrack.api.as;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import kotlin.reflect.jvm.internal.KotlinReflectionInternalError;
import kotlin.text.Typography;

/* JADX INFO: compiled from: AnnotationConstructorCaller.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u00004\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0001\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010$\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u001c\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u0004\u0018\u00010\u00012\n\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003H\u0002\u001a$\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\n\u0010\n\u001a\u0006\u0012\u0002\b\u00030\u0003H\u0002\u001aI\u0010\u000b\u001a\u0002H\f\"\b\b\u0000\u0010\f*\u00020\u00012\f\u0010\r\u001a\b\u0012\u0004\u0012\u0002H\f0\u00032\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\u000f2\u000e\b\u0002\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0000¢\u0006\u0002\u0010\u0013¨\u0006\u0014²\u0006\n\u0010\u0015\u001a\u00020\u0007X\u008a\u0084\u0002²\u0006\n\u0010\u0016\u001a\u00020\tX\u008a\u0084\u0002"}, d2 = {"transformKotlinToJvm", "", "expectedType", "Ljava/lang/Class;", "throwIllegalArgumentType", "", "index", "", as.a, "", "expectedJvmType", "createAnnotationInstance", "T", "annotationClass", "values", "", "methods", "", "Ljava/lang/reflect/Method;", "(Ljava/lang/Class;Ljava/util/Map;Ljava/util/List;)Ljava/lang/Object;", "kotlin-reflection", "hashCode", "toString"}, k = 2, mv = {2, 0, 0}, xi = 48)
public final class AnnotationConstructorCallerKt {
    /* JADX INFO: Access modifiers changed from: private */
    public static final Object transformKotlinToJvm(Object obj, Class<?> cls) {
        if (obj instanceof Class) {
            return null;
        }
        if (obj instanceof KClass) {
            obj = JvmClassMappingKt.getJavaClass((KClass) obj);
        } else if (obj instanceof Object[]) {
            Object[] objArr = (Object[]) obj;
            if (objArr instanceof Class[]) {
                return null;
            }
            if (objArr instanceof KClass[]) {
                Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlin.Array<kotlin.reflect.KClass<*>>");
                KClass[] kClassArr = (KClass[]) obj;
                ArrayList arrayList = new ArrayList(kClassArr.length);
                for (KClass kClass : kClassArr) {
                    arrayList.add(JvmClassMappingKt.getJavaClass(kClass));
                }
                obj = arrayList.toArray(new Class[0]);
            } else {
                obj = objArr;
            }
        }
        if (cls.isInstance(obj)) {
            return obj;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Void throwIllegalArgumentType(int i, String str, Class<?> cls) {
        KClass orCreateKotlinClass;
        String qualifiedName;
        if (Intrinsics.areEqual(cls, Class.class)) {
            orCreateKotlinClass = Reflection.getOrCreateKotlinClass(KClass.class);
        } else {
            orCreateKotlinClass = (cls.isArray() && Intrinsics.areEqual(cls.getComponentType(), Class.class)) ? Reflection.getOrCreateKotlinClass(KClass[].class) : JvmClassMappingKt.getKotlinClass(cls);
        }
        if (Intrinsics.areEqual(orCreateKotlinClass.getQualifiedName(), Reflection.getOrCreateKotlinClass(Object[].class).getQualifiedName())) {
            StringBuilder sbAppend = new StringBuilder().append(orCreateKotlinClass.getQualifiedName()).append(Typography.less);
            Class<?> componentType = JvmClassMappingKt.getJavaClass(orCreateKotlinClass).getComponentType();
            Intrinsics.checkNotNullExpressionValue(componentType, "getComponentType(...)");
            qualifiedName = sbAppend.append(JvmClassMappingKt.getKotlinClass(componentType).getQualifiedName()).append(Typography.greater).toString();
        } else {
            qualifiedName = orCreateKotlinClass.getQualifiedName();
        }
        throw new IllegalArgumentException("Argument #" + i + ' ' + str + " is not of the required type " + qualifiedName);
    }

    public static /* synthetic */ Object createAnnotationInstance$default(Class cls, Map map, List list, int i, Object obj) {
        if ((i & 4) != 0) {
            Set setKeySet = map.keySet();
            ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(setKeySet, 10));
            Iterator it = setKeySet.iterator();
            while (it.hasNext()) {
                arrayList.add(cls.getDeclaredMethod((String) it.next(), new Class[0]));
            }
            list = arrayList;
        }
        return createAnnotationInstance(cls, map, list);
    }

    private static final <T> boolean createAnnotationInstance$equals(Class<T> cls, List<Method> list, Map<String, ? extends Object> map, Object obj) throws IllegalAccessException, InvocationTargetException {
        boolean zAreEqual;
        KClass annotationClass;
        Class javaClass = null;
        Annotation annotation = obj instanceof Annotation ? (Annotation) obj : null;
        if (annotation != null && (annotationClass = JvmClassMappingKt.getAnnotationClass(annotation)) != null) {
            javaClass = JvmClassMappingKt.getJavaClass(annotationClass);
        }
        if (!Intrinsics.areEqual(javaClass, cls)) {
            return false;
        }
        List<Method> list2 = list;
        if (!(list2 instanceof Collection) || !list2.isEmpty()) {
            for (Method method : list2) {
                Object obj2 = map.get(method.getName());
                Object objInvoke = method.invoke(obj, new Object[0]);
                if (obj2 instanceof boolean[]) {
                    Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type kotlin.BooleanArray");
                    zAreEqual = Arrays.equals((boolean[]) obj2, (boolean[]) objInvoke);
                } else if (obj2 instanceof char[]) {
                    Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type kotlin.CharArray");
                    zAreEqual = Arrays.equals((char[]) obj2, (char[]) objInvoke);
                } else if (obj2 instanceof byte[]) {
                    Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type kotlin.ByteArray");
                    zAreEqual = Arrays.equals((byte[]) obj2, (byte[]) objInvoke);
                } else if (obj2 instanceof short[]) {
                    Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type kotlin.ShortArray");
                    zAreEqual = Arrays.equals((short[]) obj2, (short[]) objInvoke);
                } else if (obj2 instanceof int[]) {
                    Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type kotlin.IntArray");
                    zAreEqual = Arrays.equals((int[]) obj2, (int[]) objInvoke);
                } else if (obj2 instanceof float[]) {
                    Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type kotlin.FloatArray");
                    zAreEqual = Arrays.equals((float[]) obj2, (float[]) objInvoke);
                } else if (obj2 instanceof long[]) {
                    Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type kotlin.LongArray");
                    zAreEqual = Arrays.equals((long[]) obj2, (long[]) objInvoke);
                } else if (obj2 instanceof double[]) {
                    Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type kotlin.DoubleArray");
                    zAreEqual = Arrays.equals((double[]) obj2, (double[]) objInvoke);
                } else if (obj2 instanceof Object[]) {
                    Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type kotlin.Array<*>");
                    zAreEqual = Arrays.equals((Object[]) obj2, (Object[]) objInvoke);
                } else {
                    zAreEqual = Intrinsics.areEqual(obj2, objInvoke);
                }
                if (!zAreEqual) {
                    return false;
                }
            }
        }
        return true;
    }

    public static final <T> T createAnnotationInstance(final Class<T> annotationClass, final Map<String, ? extends Object> values, final List<Method> methods) {
        Intrinsics.checkNotNullParameter(annotationClass, "annotationClass");
        Intrinsics.checkNotNullParameter(values, "values");
        Intrinsics.checkNotNullParameter(methods, "methods");
        final Lazy lazy = LazyKt.lazy(new Function0(values) { // from class: kotlin.reflect.jvm.internal.calls.AnnotationConstructorCallerKt$$Lambda$0
            private final Map arg$0;

            {
                this.arg$0 = values;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return Integer.valueOf(AnnotationConstructorCallerKt.createAnnotationInstance$lambda$3(this.arg$0));
            }
        });
        final Lazy lazy2 = LazyKt.lazy(new Function0(annotationClass, values) { // from class: kotlin.reflect.jvm.internal.calls.AnnotationConstructorCallerKt$$Lambda$1
            private final Class arg$0;
            private final Map arg$1;

            {
                this.arg$0 = annotationClass;
                this.arg$1 = values;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return AnnotationConstructorCallerKt.createAnnotationInstance$lambda$7(this.arg$0, this.arg$1);
            }
        });
        T t = (T) Proxy.newProxyInstance(annotationClass.getClassLoader(), new Class[]{annotationClass}, new InvocationHandler(annotationClass, values, lazy2, lazy, methods) { // from class: kotlin.reflect.jvm.internal.calls.AnnotationConstructorCallerKt$$Lambda$2
            private final Class arg$0;
            private final Map arg$1;
            private final Lazy arg$2;
            private final Lazy arg$3;
            private final List arg$4;

            {
                this.arg$0 = annotationClass;
                this.arg$1 = values;
                this.arg$2 = lazy2;
                this.arg$3 = lazy;
                this.arg$4 = methods;
            }

            @Override // java.lang.reflect.InvocationHandler
            public Object invoke(Object obj, Method method, Object[] objArr) {
                return AnnotationConstructorCallerKt.createAnnotationInstance$lambda$9(this.arg$0, this.arg$1, this.arg$2, this.arg$3, this.arg$4, obj, method, objArr);
            }
        });
        Intrinsics.checkNotNull(t, "null cannot be cast to non-null type T of kotlin.reflect.jvm.internal.calls.AnnotationConstructorCallerKt.createAnnotationInstance");
        return t;
    }

    private static final int createAnnotationInstance$lambda$4(Lazy<Integer> lazy) {
        return lazy.getValue().intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final int createAnnotationInstance$lambda$3(Map values) {
        int iHashCode;
        Intrinsics.checkNotNullParameter(values, "$values");
        int iHashCode2 = 0;
        for (Map.Entry entry : values.entrySet()) {
            String str = (String) entry.getKey();
            Object value = entry.getValue();
            if (value instanceof boolean[]) {
                iHashCode = Arrays.hashCode((boolean[]) value);
            } else if (value instanceof char[]) {
                iHashCode = Arrays.hashCode((char[]) value);
            } else if (value instanceof byte[]) {
                iHashCode = Arrays.hashCode((byte[]) value);
            } else if (value instanceof short[]) {
                iHashCode = Arrays.hashCode((short[]) value);
            } else if (value instanceof int[]) {
                iHashCode = Arrays.hashCode((int[]) value);
            } else if (value instanceof float[]) {
                iHashCode = Arrays.hashCode((float[]) value);
            } else if (value instanceof long[]) {
                iHashCode = Arrays.hashCode((long[]) value);
            } else if (value instanceof double[]) {
                iHashCode = Arrays.hashCode((double[]) value);
            } else {
                iHashCode = value instanceof Object[] ? Arrays.hashCode((Object[]) value) : value.hashCode();
            }
            iHashCode2 += iHashCode ^ (str.hashCode() * 127);
        }
        return iHashCode2;
    }

    private static final String createAnnotationInstance$lambda$8(Lazy<String> lazy) {
        return lazy.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String createAnnotationInstance$lambda$7(Class annotationClass, Map values) {
        Intrinsics.checkNotNullParameter(annotationClass, "$annotationClass");
        Intrinsics.checkNotNullParameter(values, "$values");
        StringBuilder sb = new StringBuilder();
        sb.append('@');
        sb.append(annotationClass.getCanonicalName());
        CollectionsKt.joinTo$default(values.entrySet(), sb, ", ", "(", ")", 0, null, new Function1() { // from class: kotlin.reflect.jvm.internal.calls.AnnotationConstructorCallerKt$$Lambda$3
            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return AnnotationConstructorCallerKt.createAnnotationInstance$lambda$7$lambda$6$lambda$5((Map.Entry) obj);
            }
        }, 48, null);
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence createAnnotationInstance$lambda$7$lambda$6$lambda$5(Map.Entry entry) {
        String string;
        Intrinsics.checkNotNullParameter(entry, "entry");
        String str = (String) entry.getKey();
        Object value = entry.getValue();
        if (value instanceof boolean[]) {
            string = Arrays.toString((boolean[]) value);
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        } else if (value instanceof char[]) {
            string = Arrays.toString((char[]) value);
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        } else if (value instanceof byte[]) {
            string = Arrays.toString((byte[]) value);
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        } else if (value instanceof short[]) {
            string = Arrays.toString((short[]) value);
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        } else if (value instanceof int[]) {
            string = Arrays.toString((int[]) value);
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        } else if (value instanceof float[]) {
            string = Arrays.toString((float[]) value);
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        } else if (value instanceof long[]) {
            string = Arrays.toString((long[]) value);
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        } else if (value instanceof double[]) {
            string = Arrays.toString((double[]) value);
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        } else if (value instanceof Object[]) {
            string = Arrays.toString((Object[]) value);
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        } else {
            string = value.toString();
        }
        return str + '=' + string;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Object createAnnotationInstance$lambda$9(Class annotationClass, Map values, Lazy toString$delegate, Lazy hashCode$delegate, List methods, Object obj, Method method, Object[] objArr) {
        Intrinsics.checkNotNullParameter(annotationClass, "$annotationClass");
        Intrinsics.checkNotNullParameter(values, "$values");
        Intrinsics.checkNotNullParameter(toString$delegate, "$toString$delegate");
        Intrinsics.checkNotNullParameter(hashCode$delegate, "$hashCode$delegate");
        Intrinsics.checkNotNullParameter(methods, "$methods");
        String name = method.getName();
        if (name != null) {
            int iHashCode = name.hashCode();
            if (iHashCode != -1776922004) {
                if (iHashCode != 147696667) {
                    if (iHashCode == 1444986633 && name.equals("annotationType")) {
                        return annotationClass;
                    }
                } else if (name.equals("hashCode")) {
                    return Integer.valueOf(createAnnotationInstance$lambda$4(hashCode$delegate));
                }
            } else if (name.equals("toString")) {
                return createAnnotationInstance$lambda$8(toString$delegate);
            }
        }
        if (Intrinsics.areEqual(name, "equals") && objArr != null && objArr.length == 1) {
            return Boolean.valueOf(createAnnotationInstance$equals(annotationClass, methods, values, ArraysKt.single(objArr)));
        }
        if (values.containsKey(name)) {
            return values.get(name);
        }
        StringBuilder sbAppend = new StringBuilder("Method is not supported: ").append(method).append(" (args: ");
        if (objArr == null) {
            objArr = new Object[0];
        }
        throw new KotlinReflectionInternalError(sbAppend.append(ArraysKt.toList(objArr)).append(')').toString());
    }
}
