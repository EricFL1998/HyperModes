package kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import com.xiaomi.onetrack.api.h;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.Function;
import kotlin.TuplesKt;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function10;
import kotlin.jvm.functions.Function11;
import kotlin.jvm.functions.Function12;
import kotlin.jvm.functions.Function13;
import kotlin.jvm.functions.Function14;
import kotlin.jvm.functions.Function15;
import kotlin.jvm.functions.Function16;
import kotlin.jvm.functions.Function17;
import kotlin.jvm.functions.Function18;
import kotlin.jvm.functions.Function19;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function20;
import kotlin.jvm.functions.Function21;
import kotlin.jvm.functions.Function22;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.functions.Function5;
import kotlin.jvm.functions.Function6;
import kotlin.jvm.functions.Function7;
import kotlin.jvm.functions.Function8;
import kotlin.jvm.functions.Function9;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: reflectClassUtil.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ReflectClassUtilKt {
    private static final Map<Class<? extends Function<?>>, Integer> FUNCTION_CLASSES;
    private static final List<KClass<? extends Object>> PRIMITIVE_CLASSES;
    private static final Map<Class<? extends Object>, Class<? extends Object>> PRIMITIVE_TO_WRAPPER;
    private static final Map<Class<? extends Object>, Class<? extends Object>> WRAPPER_TO_PRIMITIVE;

    public static final ClassLoader getSafeClassLoader(Class<?> cls) {
        Intrinsics.checkNotNullParameter(cls, "<this>");
        ClassLoader classLoader = cls.getClassLoader();
        if (classLoader != null) {
            return classLoader;
        }
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        Intrinsics.checkNotNullExpressionValue(systemClassLoader, "getSystemClassLoader(...)");
        return systemClassLoader;
    }

    public static final boolean isEnumClassOrSpecializedEnumEntryClass(Class<?> cls) {
        Intrinsics.checkNotNullParameter(cls, "<this>");
        return Enum.class.isAssignableFrom(cls);
    }

    static {
        int i = 0;
        List<KClass<? extends Object>> listListOf = CollectionsKt.listOf((Object[]) new KClass[]{Reflection.getOrCreateKotlinClass(Boolean.TYPE), Reflection.getOrCreateKotlinClass(Byte.TYPE), Reflection.getOrCreateKotlinClass(Character.TYPE), Reflection.getOrCreateKotlinClass(Double.TYPE), Reflection.getOrCreateKotlinClass(Float.TYPE), Reflection.getOrCreateKotlinClass(Integer.TYPE), Reflection.getOrCreateKotlinClass(Long.TYPE), Reflection.getOrCreateKotlinClass(Short.TYPE)});
        PRIMITIVE_CLASSES = listListOf;
        List<KClass<? extends Object>> list = listListOf;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            KClass kClass = (KClass) it.next();
            arrayList.add(TuplesKt.to(JvmClassMappingKt.getJavaObjectType(kClass), JvmClassMappingKt.getJavaPrimitiveType(kClass)));
        }
        WRAPPER_TO_PRIMITIVE = MapsKt.toMap(arrayList);
        List<KClass<? extends Object>> list2 = PRIMITIVE_CLASSES;
        ArrayList arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(list2, 10));
        Iterator<T> it2 = list2.iterator();
        while (it2.hasNext()) {
            KClass kClass2 = (KClass) it2.next();
            arrayList2.add(TuplesKt.to(JvmClassMappingKt.getJavaPrimitiveType(kClass2), JvmClassMappingKt.getJavaObjectType(kClass2)));
        }
        PRIMITIVE_TO_WRAPPER = MapsKt.toMap(arrayList2);
        List listListOf2 = CollectionsKt.listOf((Object[]) new Class[]{Function0.class, Function1.class, Function2.class, Function3.class, Function4.class, Function5.class, Function6.class, Function7.class, Function8.class, Function9.class, Function10.class, Function11.class, Function12.class, Function13.class, Function14.class, Function15.class, Function16.class, Function17.class, Function18.class, Function19.class, Function20.class, Function21.class, Function22.class});
        ArrayList arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(listListOf2, 10));
        for (Object obj : listListOf2) {
            int i2 = i + 1;
            if (i < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            arrayList3.add(TuplesKt.to((Class) obj, Integer.valueOf(i)));
            i = i2;
        }
        FUNCTION_CLASSES = MapsKt.toMap(arrayList3);
    }

    public static final Class<?> getPrimitiveByWrapper(Class<?> cls) {
        Intrinsics.checkNotNullParameter(cls, "<this>");
        return WRAPPER_TO_PRIMITIVE.get(cls);
    }

    public static final Class<?> getWrapperByPrimitive(Class<?> cls) {
        Intrinsics.checkNotNullParameter(cls, "<this>");
        return PRIMITIVE_TO_WRAPPER.get(cls);
    }

    public static final Integer getFunctionClassArity(Class<?> cls) {
        Intrinsics.checkNotNullParameter(cls, "<this>");
        return FUNCTION_CLASSES.get(cls);
    }

    public static final ClassId getClassId(Class<?> cls) {
        ClassId classId;
        Intrinsics.checkNotNullParameter(cls, "<this>");
        if (cls.isPrimitive()) {
            throw new IllegalArgumentException("Can't compute ClassId for primitive type: " + cls);
        }
        if (cls.isArray()) {
            throw new IllegalArgumentException("Can't compute ClassId for array type: " + cls);
        }
        if (cls.getEnclosingMethod() == null && cls.getEnclosingConstructor() == null) {
            String simpleName = cls.getSimpleName();
            Intrinsics.checkNotNullExpressionValue(simpleName, "getSimpleName(...)");
            if (simpleName.length() != 0) {
                Class<?> declaringClass = cls.getDeclaringClass();
                if (declaringClass != null && (classId = getClassId(declaringClass)) != null) {
                    Name nameIdentifier = Name.identifier(cls.getSimpleName());
                    Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
                    ClassId classIdCreateNestedClassId = classId.createNestedClassId(nameIdentifier);
                    if (classIdCreateNestedClassId != null) {
                        return classIdCreateNestedClassId;
                    }
                }
                return ClassId.Companion.topLevel(new FqName(cls.getName()));
            }
        }
        FqName fqName = new FqName(cls.getName());
        FqName fqNameParent = fqName.parent();
        Intrinsics.checkNotNullExpressionValue(fqNameParent, "parent(...)");
        FqName fqName2 = FqName.topLevel(fqName.shortName());
        Intrinsics.checkNotNullExpressionValue(fqName2, "topLevel(...)");
        return new ClassId(fqNameParent, fqName2, true);
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    public static final String getDesc(Class<?> cls) {
        Intrinsics.checkNotNullParameter(cls, "<this>");
        if (!cls.isPrimitive()) {
            if (cls.isArray()) {
                String name = cls.getName();
                Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
                return StringsKt.replace$default(name, '.', '/', false, 4, (Object) null);
            }
            StringBuilder sb = new StringBuilder("L");
            String name2 = cls.getName();
            Intrinsics.checkNotNullExpressionValue(name2, "getName(...)");
            return sb.append(StringsKt.replace$default(name2, '.', '/', false, 4, (Object) null)).append(';').toString();
        }
        String name3 = cls.getName();
        if (name3 != null) {
            switch (name3.hashCode()) {
                case -1325958191:
                    if (name3.equals("double")) {
                        return "D";
                    }
                    break;
                case 104431:
                    if (name3.equals("int")) {
                        return "I";
                    }
                    break;
                case 3039496:
                    if (name3.equals("byte")) {
                        return h.a;
                    }
                    break;
                case 3052374:
                    if (name3.equals("char")) {
                        return "C";
                    }
                    break;
                case 3327612:
                    if (name3.equals("long")) {
                        return "J";
                    }
                    break;
                case 3625364:
                    if (name3.equals("void")) {
                        return "V";
                    }
                    break;
                case 64711720:
                    if (name3.equals(TypedValues.Custom.S_BOOLEAN)) {
                        return "Z";
                    }
                    break;
                case 97526364:
                    if (name3.equals(TypedValues.Custom.S_FLOAT)) {
                        return "F";
                    }
                    break;
                case 109413500:
                    if (name3.equals("short")) {
                        return "S";
                    }
                    break;
            }
        }
        throw new IllegalArgumentException("Unsupported primitive type: " + cls);
    }

    public static final List<Type> getParameterizedTypeArguments(Type type) {
        Intrinsics.checkNotNullParameter(type, "<this>");
        if (!(type instanceof ParameterizedType)) {
            return CollectionsKt.emptyList();
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        if (parameterizedType.getOwnerType() != null) {
            return SequencesKt.toList(SequencesKt.flatMap(SequencesKt.generateSequence(type, new Function1() { // from class: kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectClassUtilKt$$Lambda$0
                @Override // kotlin.jvm.functions.Function1
                public Object invoke(Object obj) {
                    return ReflectClassUtilKt._get_parameterizedTypeArguments_$lambda$3((ParameterizedType) obj);
                }
            }), new Function1() { // from class: kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectClassUtilKt$$Lambda$1
                @Override // kotlin.jvm.functions.Function1
                public Object invoke(Object obj) {
                    return ReflectClassUtilKt._get_parameterizedTypeArguments_$lambda$4((ParameterizedType) obj);
                }
            }));
        }
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Intrinsics.checkNotNullExpressionValue(actualTypeArguments, "getActualTypeArguments(...)");
        return ArraysKt.toList(actualTypeArguments);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final ParameterizedType _get_parameterizedTypeArguments_$lambda$3(ParameterizedType it) {
        Intrinsics.checkNotNullParameter(it, "it");
        Type ownerType = it.getOwnerType();
        if (ownerType instanceof ParameterizedType) {
            return (ParameterizedType) ownerType;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Sequence _get_parameterizedTypeArguments_$lambda$4(ParameterizedType it) {
        Intrinsics.checkNotNullParameter(it, "it");
        Type[] actualTypeArguments = it.getActualTypeArguments();
        Intrinsics.checkNotNullExpressionValue(actualTypeArguments, "getActualTypeArguments(...)");
        return ArraysKt.asSequence(actualTypeArguments);
    }
}
