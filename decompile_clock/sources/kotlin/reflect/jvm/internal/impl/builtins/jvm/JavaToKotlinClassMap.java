package kotlin.reflect.jvm.internal.impl.builtins.jvm;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.CompanionObjectMapping;
import kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType;
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames;
import kotlin.reflect.jvm.internal.impl.builtins.functions.FunctionTypeKind;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.FqNameUnsafe;
import kotlin.reflect.jvm.internal.impl.name.FqNamesUtilKt;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.name.SpecialNames;
import kotlin.reflect.jvm.internal.impl.name.StandardClassIds;
import kotlin.reflect.jvm.internal.impl.resolve.jvm.JvmPrimitiveType;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: JavaToKotlinClassMap.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JavaToKotlinClassMap {
    private static final ClassId CLASS_CLASS_ID;
    private static final ClassId FUNCTION_N_CLASS_ID;
    private static final FqName FUNCTION_N_FQ_NAME;
    public static final JavaToKotlinClassMap INSTANCE;
    private static final ClassId K_CLASS_CLASS_ID;
    private static final ClassId K_FUNCTION_CLASS_ID;
    private static final String NUMBERED_FUNCTION_PREFIX;
    private static final String NUMBERED_K_FUNCTION_PREFIX;
    private static final String NUMBERED_K_SUSPEND_FUNCTION_PREFIX;
    private static final String NUMBERED_SUSPEND_FUNCTION_PREFIX;
    private static final HashMap<FqNameUnsafe, ClassId> javaToKotlin;
    private static final HashMap<FqNameUnsafe, ClassId> kotlinToJava;
    private static final List<PlatformMutabilityMapping> mutabilityMappings;
    private static final HashMap<FqNameUnsafe, FqName> mutableToReadOnly;
    private static final HashMap<ClassId, ClassId> mutableToReadOnlyClassId;
    private static final HashMap<FqNameUnsafe, FqName> readOnlyToMutable;
    private static final HashMap<ClassId, ClassId> readOnlyToMutableClassId;

    private JavaToKotlinClassMap() {
    }

    static {
        JavaToKotlinClassMap javaToKotlinClassMap = new JavaToKotlinClassMap();
        INSTANCE = javaToKotlinClassMap;
        NUMBERED_FUNCTION_PREFIX = FunctionTypeKind.Function.INSTANCE.getPackageFqName() + '.' + FunctionTypeKind.Function.INSTANCE.getClassNamePrefix();
        NUMBERED_K_FUNCTION_PREFIX = FunctionTypeKind.KFunction.INSTANCE.getPackageFqName() + '.' + FunctionTypeKind.KFunction.INSTANCE.getClassNamePrefix();
        NUMBERED_SUSPEND_FUNCTION_PREFIX = FunctionTypeKind.SuspendFunction.INSTANCE.getPackageFqName() + '.' + FunctionTypeKind.SuspendFunction.INSTANCE.getClassNamePrefix();
        NUMBERED_K_SUSPEND_FUNCTION_PREFIX = FunctionTypeKind.KSuspendFunction.INSTANCE.getPackageFqName() + '.' + FunctionTypeKind.KSuspendFunction.INSTANCE.getClassNamePrefix();
        ClassId classId = ClassId.Companion.topLevel(new FqName("kotlin.jvm.functions.FunctionN"));
        FUNCTION_N_CLASS_ID = classId;
        FUNCTION_N_FQ_NAME = classId.asSingleFqName();
        K_FUNCTION_CLASS_ID = StandardClassIds.INSTANCE.getKFunction();
        K_CLASS_CLASS_ID = StandardClassIds.INSTANCE.getKClass();
        CLASS_CLASS_ID = javaToKotlinClassMap.classId(Class.class);
        javaToKotlin = new HashMap<>();
        kotlinToJava = new HashMap<>();
        mutableToReadOnly = new HashMap<>();
        readOnlyToMutable = new HashMap<>();
        mutableToReadOnlyClassId = new HashMap<>();
        readOnlyToMutableClassId = new HashMap<>();
        ClassId classId2 = ClassId.Companion.topLevel(StandardNames.FqNames.iterable);
        ClassId classId3 = new ClassId(classId2.getPackageFqName(), FqNamesUtilKt.tail(StandardNames.FqNames.mutableIterable, classId2.getPackageFqName()), false);
        ClassId classId4 = ClassId.Companion.topLevel(StandardNames.FqNames.iterator);
        ClassId classId5 = new ClassId(classId4.getPackageFqName(), FqNamesUtilKt.tail(StandardNames.FqNames.mutableIterator, classId4.getPackageFqName()), false);
        ClassId classId6 = ClassId.Companion.topLevel(StandardNames.FqNames.collection);
        ClassId classId7 = new ClassId(classId6.getPackageFqName(), FqNamesUtilKt.tail(StandardNames.FqNames.mutableCollection, classId6.getPackageFqName()), false);
        ClassId classId8 = ClassId.Companion.topLevel(StandardNames.FqNames.list);
        ClassId classId9 = new ClassId(classId8.getPackageFqName(), FqNamesUtilKt.tail(StandardNames.FqNames.mutableList, classId8.getPackageFqName()), false);
        ClassId classId10 = ClassId.Companion.topLevel(StandardNames.FqNames.set);
        ClassId classId11 = new ClassId(classId10.getPackageFqName(), FqNamesUtilKt.tail(StandardNames.FqNames.mutableSet, classId10.getPackageFqName()), false);
        ClassId classId12 = ClassId.Companion.topLevel(StandardNames.FqNames.listIterator);
        ClassId classId13 = new ClassId(classId12.getPackageFqName(), FqNamesUtilKt.tail(StandardNames.FqNames.mutableListIterator, classId12.getPackageFqName()), false);
        ClassId classId14 = ClassId.Companion.topLevel(StandardNames.FqNames.map);
        ClassId classId15 = new ClassId(classId14.getPackageFqName(), FqNamesUtilKt.tail(StandardNames.FqNames.mutableMap, classId14.getPackageFqName()), false);
        ClassId classId16 = ClassId.Companion.topLevel(StandardNames.FqNames.map);
        Name nameShortName = StandardNames.FqNames.mapEntry.shortName();
        Intrinsics.checkNotNullExpressionValue(nameShortName, "shortName(...)");
        ClassId classIdCreateNestedClassId = classId16.createNestedClassId(nameShortName);
        List<PlatformMutabilityMapping> listListOf = CollectionsKt.listOf((Object[]) new PlatformMutabilityMapping[]{new PlatformMutabilityMapping(javaToKotlinClassMap.classId(Iterable.class), classId2, classId3), new PlatformMutabilityMapping(javaToKotlinClassMap.classId(Iterator.class), classId4, classId5), new PlatformMutabilityMapping(javaToKotlinClassMap.classId(Collection.class), classId6, classId7), new PlatformMutabilityMapping(javaToKotlinClassMap.classId(List.class), classId8, classId9), new PlatformMutabilityMapping(javaToKotlinClassMap.classId(Set.class), classId10, classId11), new PlatformMutabilityMapping(javaToKotlinClassMap.classId(ListIterator.class), classId12, classId13), new PlatformMutabilityMapping(javaToKotlinClassMap.classId(Map.class), classId14, classId15), new PlatformMutabilityMapping(javaToKotlinClassMap.classId(Map.Entry.class), classIdCreateNestedClassId, new ClassId(classIdCreateNestedClassId.getPackageFqName(), FqNamesUtilKt.tail(StandardNames.FqNames.mutableMapEntry, classIdCreateNestedClassId.getPackageFqName()), false))});
        mutabilityMappings = listListOf;
        javaToKotlinClassMap.addTopLevel(Object.class, StandardNames.FqNames.any);
        javaToKotlinClassMap.addTopLevel(String.class, StandardNames.FqNames.string);
        javaToKotlinClassMap.addTopLevel(CharSequence.class, StandardNames.FqNames.charSequence);
        javaToKotlinClassMap.addTopLevel(Throwable.class, StandardNames.FqNames.throwable);
        javaToKotlinClassMap.addTopLevel(Cloneable.class, StandardNames.FqNames.cloneable);
        javaToKotlinClassMap.addTopLevel(Number.class, StandardNames.FqNames.number);
        javaToKotlinClassMap.addTopLevel(Comparable.class, StandardNames.FqNames.comparable);
        javaToKotlinClassMap.addTopLevel(Enum.class, StandardNames.FqNames._enum);
        javaToKotlinClassMap.addTopLevel(Annotation.class, StandardNames.FqNames.annotation);
        Iterator<PlatformMutabilityMapping> it = listListOf.iterator();
        while (it.hasNext()) {
            INSTANCE.addMapping(it.next());
        }
        for (JvmPrimitiveType jvmPrimitiveType : JvmPrimitiveType.values()) {
            JavaToKotlinClassMap javaToKotlinClassMap2 = INSTANCE;
            ClassId.Companion companion = ClassId.Companion;
            FqName wrapperFqName = jvmPrimitiveType.getWrapperFqName();
            Intrinsics.checkNotNullExpressionValue(wrapperFqName, "getWrapperFqName(...)");
            ClassId classId17 = companion.topLevel(wrapperFqName);
            ClassId.Companion companion2 = ClassId.Companion;
            PrimitiveType primitiveType = jvmPrimitiveType.getPrimitiveType();
            Intrinsics.checkNotNullExpressionValue(primitiveType, "getPrimitiveType(...)");
            javaToKotlinClassMap2.add(classId17, companion2.topLevel(StandardNames.getPrimitiveFqName(primitiveType)));
        }
        for (ClassId classId18 : CompanionObjectMapping.INSTANCE.allClassesWithIntrinsicCompanions()) {
            INSTANCE.add(ClassId.Companion.topLevel(new FqName("kotlin.jvm.internal." + classId18.getShortClassName().asString() + "CompanionObject")), classId18.createNestedClassId(SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT));
        }
        for (int i = 0; i < 23; i++) {
            JavaToKotlinClassMap javaToKotlinClassMap3 = INSTANCE;
            javaToKotlinClassMap3.add(ClassId.Companion.topLevel(new FqName("kotlin.jvm.functions.Function" + i)), StandardNames.getFunctionClassId(i));
            javaToKotlinClassMap3.addKotlinToJava(new FqName(NUMBERED_K_FUNCTION_PREFIX + i), K_FUNCTION_CLASS_ID);
        }
        for (int i2 = 0; i2 < 22; i2++) {
            FunctionTypeKind.KSuspendFunction kSuspendFunction = FunctionTypeKind.KSuspendFunction.INSTANCE;
            INSTANCE.addKotlinToJava(new FqName((kSuspendFunction.getPackageFqName() + '.' + kSuspendFunction.getClassNamePrefix()) + i2), K_FUNCTION_CLASS_ID);
        }
        JavaToKotlinClassMap javaToKotlinClassMap4 = INSTANCE;
        FqName safe = StandardNames.FqNames.nothing.toSafe();
        Intrinsics.checkNotNullExpressionValue(safe, "toSafe(...)");
        javaToKotlinClassMap4.addKotlinToJava(safe, javaToKotlinClassMap4.classId(Void.class));
    }

    public final FqName getFUNCTION_N_FQ_NAME() {
        return FUNCTION_N_FQ_NAME;
    }

    /* JADX INFO: compiled from: JavaToKotlinClassMap.kt */
    public static final class PlatformMutabilityMapping {
        private final ClassId javaClass;
        private final ClassId kotlinMutable;
        private final ClassId kotlinReadOnly;

        public final ClassId component1() {
            return this.javaClass;
        }

        public final ClassId component2() {
            return this.kotlinReadOnly;
        }

        public final ClassId component3() {
            return this.kotlinMutable;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof PlatformMutabilityMapping)) {
                return false;
            }
            PlatformMutabilityMapping platformMutabilityMapping = (PlatformMutabilityMapping) obj;
            return Intrinsics.areEqual(this.javaClass, platformMutabilityMapping.javaClass) && Intrinsics.areEqual(this.kotlinReadOnly, platformMutabilityMapping.kotlinReadOnly) && Intrinsics.areEqual(this.kotlinMutable, platformMutabilityMapping.kotlinMutable);
        }

        public int hashCode() {
            return (((this.javaClass.hashCode() * 31) + this.kotlinReadOnly.hashCode()) * 31) + this.kotlinMutable.hashCode();
        }

        public String toString() {
            return "PlatformMutabilityMapping(javaClass=" + this.javaClass + ", kotlinReadOnly=" + this.kotlinReadOnly + ", kotlinMutable=" + this.kotlinMutable + ')';
        }

        public PlatformMutabilityMapping(ClassId javaClass, ClassId kotlinReadOnly, ClassId kotlinMutable) {
            Intrinsics.checkNotNullParameter(javaClass, "javaClass");
            Intrinsics.checkNotNullParameter(kotlinReadOnly, "kotlinReadOnly");
            Intrinsics.checkNotNullParameter(kotlinMutable, "kotlinMutable");
            this.javaClass = javaClass;
            this.kotlinReadOnly = kotlinReadOnly;
            this.kotlinMutable = kotlinMutable;
        }

        public final ClassId getJavaClass() {
            return this.javaClass;
        }
    }

    public final List<PlatformMutabilityMapping> getMutabilityMappings() {
        return mutabilityMappings;
    }

    public final ClassId mapJavaToKotlin(FqName fqName) {
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        return javaToKotlin.get(fqName.toUnsafe());
    }

    public final ClassId mapKotlinToJava(FqNameUnsafe kotlinFqName) {
        Intrinsics.checkNotNullParameter(kotlinFqName, "kotlinFqName");
        if (!isKotlinFunctionWithBigArity(kotlinFqName, NUMBERED_FUNCTION_PREFIX) && !isKotlinFunctionWithBigArity(kotlinFqName, NUMBERED_SUSPEND_FUNCTION_PREFIX)) {
            if (!isKotlinFunctionWithBigArity(kotlinFqName, NUMBERED_K_FUNCTION_PREFIX) && !isKotlinFunctionWithBigArity(kotlinFqName, NUMBERED_K_SUSPEND_FUNCTION_PREFIX)) {
                return kotlinToJava.get(kotlinFqName);
            }
            return K_FUNCTION_CLASS_ID;
        }
        return FUNCTION_N_CLASS_ID;
    }

    private final boolean isKotlinFunctionWithBigArity(FqNameUnsafe fqNameUnsafe, String str) {
        Integer intOrNull;
        String strAsString = fqNameUnsafe.asString();
        Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
        if (!StringsKt.startsWith$default(strAsString, str, false, 2, (Object) null)) {
            return false;
        }
        String strSubstring = strAsString.substring(str.length());
        Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
        return (StringsKt.startsWith$default((CharSequence) strSubstring, '0', false, 2, (Object) null) || (intOrNull = StringsKt.toIntOrNull(strSubstring)) == null || intOrNull.intValue() < 23) ? false : true;
    }

    private final void addMapping(PlatformMutabilityMapping platformMutabilityMapping) {
        ClassId classIdComponent1 = platformMutabilityMapping.component1();
        ClassId classIdComponent2 = platformMutabilityMapping.component2();
        ClassId classIdComponent3 = platformMutabilityMapping.component3();
        add(classIdComponent1, classIdComponent2);
        addKotlinToJava(classIdComponent3.asSingleFqName(), classIdComponent1);
        mutableToReadOnlyClassId.put(classIdComponent3, classIdComponent2);
        readOnlyToMutableClassId.put(classIdComponent2, classIdComponent3);
        FqName fqNameAsSingleFqName = classIdComponent2.asSingleFqName();
        FqName fqNameAsSingleFqName2 = classIdComponent3.asSingleFqName();
        mutableToReadOnly.put(classIdComponent3.asSingleFqName().toUnsafe(), fqNameAsSingleFqName);
        readOnlyToMutable.put(fqNameAsSingleFqName.toUnsafe(), fqNameAsSingleFqName2);
    }

    private final void add(ClassId classId, ClassId classId2) {
        addJavaToKotlin(classId, classId2);
        addKotlinToJava(classId2.asSingleFqName(), classId);
    }

    private final void addTopLevel(Class<?> cls, FqNameUnsafe fqNameUnsafe) {
        FqName safe = fqNameUnsafe.toSafe();
        Intrinsics.checkNotNullExpressionValue(safe, "toSafe(...)");
        addTopLevel(cls, safe);
    }

    private final void addTopLevel(Class<?> cls, FqName fqName) {
        add(classId(cls), ClassId.Companion.topLevel(fqName));
    }

    private final void addJavaToKotlin(ClassId classId, ClassId classId2) {
        javaToKotlin.put(classId.asSingleFqName().toUnsafe(), classId2);
    }

    private final void addKotlinToJava(FqName fqName, ClassId classId) {
        kotlinToJava.put(fqName.toUnsafe(), classId);
    }

    public final FqName mutableToReadOnly(FqNameUnsafe fqNameUnsafe) {
        return mutableToReadOnly.get(fqNameUnsafe);
    }

    public final FqName readOnlyToMutable(FqNameUnsafe fqNameUnsafe) {
        return readOnlyToMutable.get(fqNameUnsafe);
    }

    public final boolean isMutable(FqNameUnsafe fqNameUnsafe) {
        return mutableToReadOnly.containsKey(fqNameUnsafe);
    }

    public final boolean isReadOnly(FqNameUnsafe fqNameUnsafe) {
        return readOnlyToMutable.containsKey(fqNameUnsafe);
    }

    private final ClassId classId(Class<?> cls) {
        if (!cls.isPrimitive()) {
            cls.isArray();
        }
        Class<?> declaringClass = cls.getDeclaringClass();
        if (declaringClass == null) {
            return ClassId.Companion.topLevel(new FqName(cls.getCanonicalName()));
        }
        ClassId classId = classId(declaringClass);
        Name nameIdentifier = Name.identifier(cls.getSimpleName());
        Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
        return classId.createNestedClassId(nameIdentifier);
    }
}
