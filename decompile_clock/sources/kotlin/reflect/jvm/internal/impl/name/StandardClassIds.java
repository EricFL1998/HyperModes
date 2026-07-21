package kotlin.reflect.jvm.internal.impl.name;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;

/* JADX INFO: compiled from: StandardClassIds.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class StandardClassIds {
    private static final ClassId Annotation;
    private static final ClassId AnnotationRetention;
    private static final ClassId AnnotationTarget;
    private static final ClassId Any;
    private static final ClassId Array;
    private static final FqName BASE_ANNOTATION_PACKAGE;
    private static final FqName BASE_COLLECTIONS_PACKAGE;
    private static final FqName BASE_CONCURRENT_PACKAGE;
    private static final FqName BASE_CONTRACTS_PACKAGE;
    private static final FqName BASE_COROUTINES_PACKAGE;
    private static final FqName BASE_ENUMS_PACKAGE;
    private static final FqName BASE_INTERNAL_IR_PACKAGE;
    private static final FqName BASE_INTERNAL_PACKAGE;
    private static final FqName BASE_JVM_FUNCTIONS_PACKAGE;
    private static final FqName BASE_JVM_INTERNAL_PACKAGE;
    private static final FqName BASE_JVM_PACKAGE;
    private static final FqName BASE_KOTLIN_PACKAGE;
    private static final FqName BASE_RANGES_PACKAGE;
    private static final FqName BASE_REFLECT_PACKAGE;
    private static final FqName BASE_TEST_PACKAGE;
    private static final ClassId Boolean;
    private static final ClassId Byte;
    private static final ClassId Char;
    private static final ClassId CharIterator;
    private static final ClassId CharRange;
    private static final ClassId CharSequence;
    private static final ClassId Cloneable;
    private static final ClassId Collection;
    private static final ClassId Comparable;
    private static final ClassId Continuation;
    private static final ClassId DeprecationLevel;
    private static final ClassId Double;
    private static final ClassId Enum;
    private static final ClassId EnumEntries;
    private static final ClassId Float;
    private static final ClassId Function;
    public static final StandardClassIds INSTANCE = new StandardClassIds();
    private static final ClassId Int;
    private static final ClassId IntRange;
    private static final ClassId Iterable;
    private static final ClassId Iterator;
    private static final ClassId KCallable;
    private static final ClassId KClass;
    private static final ClassId KFunction;
    private static final ClassId KMutableProperty;
    private static final ClassId KMutableProperty0;
    private static final ClassId KMutableProperty1;
    private static final ClassId KMutableProperty2;
    private static final ClassId KProperty;
    private static final ClassId KProperty0;
    private static final ClassId KProperty1;
    private static final ClassId KProperty2;
    private static final ClassId KType;
    private static final ClassId List;
    private static final ClassId ListIterator;
    private static final ClassId Long;
    private static final ClassId LongRange;
    private static final ClassId Map;
    private static final ClassId MapEntry;
    private static final ClassId MutableCollection;
    private static final ClassId MutableIterable;
    private static final ClassId MutableIterator;
    private static final ClassId MutableList;
    private static final ClassId MutableListIterator;
    private static final ClassId MutableMap;
    private static final ClassId MutableMapEntry;
    private static final ClassId MutableSet;
    private static final ClassId Nothing;
    private static final ClassId Number;
    private static final ClassId Result;
    private static final ClassId Set;
    private static final ClassId Short;
    private static final ClassId String;
    private static final ClassId Throwable;
    private static final ClassId UByte;
    private static final ClassId UInt;
    private static final ClassId ULong;
    private static final ClassId UShort;
    private static final ClassId Unit;
    private static final Set<ClassId> allBuiltinTypes;
    private static final Set<FqName> builtInsPackages;
    private static final Set<FqName> builtInsPackagesWithDefaultNamedImport;
    private static final Set<ClassId> constantAllowedTypes;
    private static final Map<ClassId, ClassId> elementTypeByPrimitiveArrayType;
    private static final Map<ClassId, ClassId> elementTypeByUnsignedArrayType;
    private static final Map<ClassId, ClassId> primitiveArrayTypeByElementType;
    private static final Set<ClassId> primitiveTypes;
    private static final Map<ClassId, ClassId> unsignedArrayTypeByElementType;
    private static final Set<ClassId> unsignedTypes;

    private StandardClassIds() {
    }

    static {
        FqName fqName = new FqName("kotlin");
        BASE_KOTLIN_PACKAGE = fqName;
        FqName fqNameChild = fqName.child(Name.identifier("reflect"));
        Intrinsics.checkNotNullExpressionValue(fqNameChild, "child(...)");
        BASE_REFLECT_PACKAGE = fqNameChild;
        FqName fqNameChild2 = fqName.child(Name.identifier("collections"));
        Intrinsics.checkNotNullExpressionValue(fqNameChild2, "child(...)");
        BASE_COLLECTIONS_PACKAGE = fqNameChild2;
        FqName fqNameChild3 = fqName.child(Name.identifier("ranges"));
        Intrinsics.checkNotNullExpressionValue(fqNameChild3, "child(...)");
        BASE_RANGES_PACKAGE = fqNameChild3;
        FqName fqNameChild4 = fqName.child(Name.identifier("jvm"));
        Intrinsics.checkNotNullExpressionValue(fqNameChild4, "child(...)");
        BASE_JVM_PACKAGE = fqNameChild4;
        FqName fqNameChild5 = fqNameChild4.child(Name.identifier("internal"));
        Intrinsics.checkNotNullExpressionValue(fqNameChild5, "child(...)");
        BASE_JVM_INTERNAL_PACKAGE = fqNameChild5;
        FqName fqNameChild6 = fqNameChild4.child(Name.identifier("functions"));
        Intrinsics.checkNotNullExpressionValue(fqNameChild6, "child(...)");
        BASE_JVM_FUNCTIONS_PACKAGE = fqNameChild6;
        FqName fqNameChild7 = fqName.child(Name.identifier("annotation"));
        Intrinsics.checkNotNullExpressionValue(fqNameChild7, "child(...)");
        BASE_ANNOTATION_PACKAGE = fqNameChild7;
        FqName fqNameChild8 = fqName.child(Name.identifier("internal"));
        Intrinsics.checkNotNullExpressionValue(fqNameChild8, "child(...)");
        BASE_INTERNAL_PACKAGE = fqNameChild8;
        FqName fqNameChild9 = fqNameChild8.child(Name.identifier("ir"));
        Intrinsics.checkNotNullExpressionValue(fqNameChild9, "child(...)");
        BASE_INTERNAL_IR_PACKAGE = fqNameChild9;
        FqName fqNameChild10 = fqName.child(Name.identifier("coroutines"));
        Intrinsics.checkNotNullExpressionValue(fqNameChild10, "child(...)");
        BASE_COROUTINES_PACKAGE = fqNameChild10;
        FqName fqNameChild11 = fqName.child(Name.identifier("enums"));
        Intrinsics.checkNotNullExpressionValue(fqNameChild11, "child(...)");
        BASE_ENUMS_PACKAGE = fqNameChild11;
        FqName fqNameChild12 = fqName.child(Name.identifier("contracts"));
        Intrinsics.checkNotNullExpressionValue(fqNameChild12, "child(...)");
        BASE_CONTRACTS_PACKAGE = fqNameChild12;
        FqName fqNameChild13 = fqName.child(Name.identifier("concurrent"));
        Intrinsics.checkNotNullExpressionValue(fqNameChild13, "child(...)");
        BASE_CONCURRENT_PACKAGE = fqNameChild13;
        FqName fqNameChild14 = fqName.child(Name.identifier("test"));
        Intrinsics.checkNotNullExpressionValue(fqNameChild14, "child(...)");
        BASE_TEST_PACKAGE = fqNameChild14;
        builtInsPackagesWithDefaultNamedImport = SetsKt.setOf((Object[]) new FqName[]{fqName, fqNameChild2, fqNameChild3, fqNameChild7});
        builtInsPackages = SetsKt.setOf((Object[]) new FqName[]{fqName, fqNameChild2, fqNameChild3, fqNameChild7, fqNameChild, fqNameChild8, fqNameChild10});
        Nothing = StandardClassIdsKt.baseId("Nothing");
        Unit = StandardClassIdsKt.baseId("Unit");
        Any = StandardClassIdsKt.baseId("Any");
        Enum = StandardClassIdsKt.baseId("Enum");
        Annotation = StandardClassIdsKt.baseId("Annotation");
        Array = StandardClassIdsKt.baseId("Array");
        ClassId classIdBaseId = StandardClassIdsKt.baseId("Boolean");
        Boolean = classIdBaseId;
        ClassId classIdBaseId2 = StandardClassIdsKt.baseId("Char");
        Char = classIdBaseId2;
        ClassId classIdBaseId3 = StandardClassIdsKt.baseId("Byte");
        Byte = classIdBaseId3;
        ClassId classIdBaseId4 = StandardClassIdsKt.baseId("Short");
        Short = classIdBaseId4;
        ClassId classIdBaseId5 = StandardClassIdsKt.baseId("Int");
        Int = classIdBaseId5;
        ClassId classIdBaseId6 = StandardClassIdsKt.baseId("Long");
        Long = classIdBaseId6;
        ClassId classIdBaseId7 = StandardClassIdsKt.baseId("Float");
        Float = classIdBaseId7;
        ClassId classIdBaseId8 = StandardClassIdsKt.baseId("Double");
        Double = classIdBaseId8;
        UByte = StandardClassIdsKt.unsignedId(classIdBaseId3);
        UShort = StandardClassIdsKt.unsignedId(classIdBaseId4);
        UInt = StandardClassIdsKt.unsignedId(classIdBaseId5);
        ULong = StandardClassIdsKt.unsignedId(classIdBaseId6);
        CharSequence = StandardClassIdsKt.baseId("CharSequence");
        String = StandardClassIdsKt.baseId("String");
        Throwable = StandardClassIdsKt.baseId("Throwable");
        Cloneable = StandardClassIdsKt.baseId("Cloneable");
        KProperty = StandardClassIdsKt.reflectId("KProperty");
        KMutableProperty = StandardClassIdsKt.reflectId("KMutableProperty");
        KProperty0 = StandardClassIdsKt.reflectId("KProperty0");
        KMutableProperty0 = StandardClassIdsKt.reflectId("KMutableProperty0");
        KProperty1 = StandardClassIdsKt.reflectId("KProperty1");
        KMutableProperty1 = StandardClassIdsKt.reflectId("KMutableProperty1");
        KProperty2 = StandardClassIdsKt.reflectId("KProperty2");
        KMutableProperty2 = StandardClassIdsKt.reflectId("KMutableProperty2");
        KFunction = StandardClassIdsKt.reflectId("KFunction");
        KClass = StandardClassIdsKt.reflectId("KClass");
        KCallable = StandardClassIdsKt.reflectId("KCallable");
        KType = StandardClassIdsKt.reflectId("KType");
        Comparable = StandardClassIdsKt.baseId("Comparable");
        Number = StandardClassIdsKt.baseId("Number");
        Function = StandardClassIdsKt.baseId("Function");
        Set<ClassId> of = SetsKt.setOf((Object[]) new ClassId[]{classIdBaseId, classIdBaseId2, classIdBaseId3, classIdBaseId4, classIdBaseId5, classIdBaseId6, classIdBaseId7, classIdBaseId8});
        primitiveTypes = of;
        Set<ClassId> set = of;
        LinkedHashMap linkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(set, 10)), 16));
        for (Object obj : set) {
            linkedHashMap.put(obj, StandardClassIdsKt.primitiveArrayId(((ClassId) obj).getShortClassName()));
        }
        LinkedHashMap linkedHashMap2 = linkedHashMap;
        primitiveArrayTypeByElementType = linkedHashMap2;
        elementTypeByPrimitiveArrayType = StandardClassIdsKt.inverseMap(linkedHashMap2);
        Set<ClassId> of2 = SetsKt.setOf((Object[]) new ClassId[]{UByte, UShort, UInt, ULong});
        unsignedTypes = of2;
        Set<ClassId> set2 = of2;
        LinkedHashMap linkedHashMap3 = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(set2, 10)), 16));
        for (Object obj2 : set2) {
            linkedHashMap3.put(obj2, StandardClassIdsKt.primitiveArrayId(((ClassId) obj2).getShortClassName()));
        }
        LinkedHashMap linkedHashMap4 = linkedHashMap3;
        unsignedArrayTypeByElementType = linkedHashMap4;
        elementTypeByUnsignedArrayType = StandardClassIdsKt.inverseMap(linkedHashMap4);
        Set<ClassId> set3 = primitiveTypes;
        Set<ClassId> set4 = unsignedTypes;
        Set setPlus = SetsKt.plus((Set) set3, (Iterable) set4);
        ClassId classId = String;
        constantAllowedTypes = SetsKt.plus((Set<? extends ClassId>) setPlus, classId);
        Continuation = StandardClassIdsKt.coroutinesId("Continuation");
        Iterator = StandardClassIdsKt.collectionsId("Iterator");
        Iterable = StandardClassIdsKt.collectionsId("Iterable");
        Collection = StandardClassIdsKt.collectionsId("Collection");
        List = StandardClassIdsKt.collectionsId("List");
        ListIterator = StandardClassIdsKt.collectionsId("ListIterator");
        Set = StandardClassIdsKt.collectionsId("Set");
        ClassId classIdCollectionsId = StandardClassIdsKt.collectionsId("Map");
        Map = classIdCollectionsId;
        MutableIterator = StandardClassIdsKt.collectionsId("MutableIterator");
        CharIterator = StandardClassIdsKt.collectionsId("CharIterator");
        MutableIterable = StandardClassIdsKt.collectionsId("MutableIterable");
        MutableCollection = StandardClassIdsKt.collectionsId("MutableCollection");
        MutableList = StandardClassIdsKt.collectionsId("MutableList");
        MutableListIterator = StandardClassIdsKt.collectionsId("MutableListIterator");
        MutableSet = StandardClassIdsKt.collectionsId("MutableSet");
        ClassId classIdCollectionsId2 = StandardClassIdsKt.collectionsId("MutableMap");
        MutableMap = classIdCollectionsId2;
        Name nameIdentifier = Name.identifier("Entry");
        Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
        MapEntry = classIdCollectionsId.createNestedClassId(nameIdentifier);
        Name nameIdentifier2 = Name.identifier("MutableEntry");
        Intrinsics.checkNotNullExpressionValue(nameIdentifier2, "identifier(...)");
        MutableMapEntry = classIdCollectionsId2.createNestedClassId(nameIdentifier2);
        Result = StandardClassIdsKt.baseId("Result");
        IntRange = StandardClassIdsKt.rangesId("IntRange");
        LongRange = StandardClassIdsKt.rangesId("LongRange");
        CharRange = StandardClassIdsKt.rangesId("CharRange");
        AnnotationRetention = StandardClassIdsKt.annotationId("AnnotationRetention");
        AnnotationTarget = StandardClassIdsKt.annotationId("AnnotationTarget");
        DeprecationLevel = StandardClassIdsKt.baseId("DeprecationLevel");
        EnumEntries = StandardClassIdsKt.enumsId("EnumEntries");
        allBuiltinTypes = SetsKt.plus((Set<? extends ClassId>) SetsKt.plus((Set<? extends ClassId>) SetsKt.plus((Set<? extends ClassId>) SetsKt.plus((Set<? extends ClassId>) SetsKt.plus((Set) set3, (Iterable) set4), classId), Unit), Any), Enum);
    }

    public final FqName getBASE_KOTLIN_PACKAGE() {
        return BASE_KOTLIN_PACKAGE;
    }

    public final FqName getBASE_REFLECT_PACKAGE() {
        return BASE_REFLECT_PACKAGE;
    }

    public final FqName getBASE_COLLECTIONS_PACKAGE() {
        return BASE_COLLECTIONS_PACKAGE;
    }

    public final FqName getBASE_RANGES_PACKAGE() {
        return BASE_RANGES_PACKAGE;
    }

    public final FqName getBASE_ANNOTATION_PACKAGE() {
        return BASE_ANNOTATION_PACKAGE;
    }

    public final FqName getBASE_COROUTINES_PACKAGE() {
        return BASE_COROUTINES_PACKAGE;
    }

    public final FqName getBASE_ENUMS_PACKAGE() {
        return BASE_ENUMS_PACKAGE;
    }

    public final ClassId getArray() {
        return Array;
    }

    public final ClassId getKFunction() {
        return KFunction;
    }

    public final ClassId getKClass() {
        return KClass;
    }

    public final ClassId getMutableList() {
        return MutableList;
    }

    public final ClassId getMutableSet() {
        return MutableSet;
    }

    public final ClassId getMutableMap() {
        return MutableMap;
    }

    public final ClassId getEnumEntries() {
        return EnumEntries;
    }
}
