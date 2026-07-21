package kotlin.reflect.jvm.internal.impl.load.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.reflect.jvm.internal.impl.load.kotlin.SignatureBuildingComponents;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.jvm.JvmPrimitiveType;

/* JADX INFO: compiled from: SpecialGenericSignatures.kt */
/* JADX INFO: loaded from: classes2.dex */
public class SpecialGenericSignatures {
    public static final Companion Companion = new Companion(null);
    private static final List<String> ERASED_COLLECTION_PARAMETER_NAMES;
    private static final List<Companion.NameAndSignature> ERASED_COLLECTION_PARAMETER_NAME_AND_SIGNATURES;
    private static final List<String> ERASED_COLLECTION_PARAMETER_SIGNATURES;
    private static final Set<Name> ERASED_VALUE_PARAMETERS_SHORT_NAMES;
    private static final Set<String> ERASED_VALUE_PARAMETERS_SIGNATURES;
    private static final Map<Companion.NameAndSignature, TypeSafeBarrierDescription> GENERIC_PARAMETERS_METHODS_TO_DEFAULT_VALUES_MAP;
    private static final Map<Name, Name> JVM_SHORT_NAME_TO_BUILTIN_SHORT_NAMES_MAP;
    private static final Set<String> JVM_SIGNATURES_FOR_RENAMED_BUILT_INS;
    private static final Map<Companion.NameAndSignature, Name> NAME_AND_SIGNATURE_TO_JVM_REPRESENTATION_NAME_MAP;
    private static final Set<Name> ORIGINAL_SHORT_NAMES;
    private static final Companion.NameAndSignature REMOVE_AT_NAME_AND_SIGNATURE;
    private static final Map<String, TypeSafeBarrierDescription> SIGNATURE_TO_DEFAULT_VALUES_MAP;
    private static final Map<String, Name> SIGNATURE_TO_JVM_REPRESENTATION_NAME;

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* JADX INFO: compiled from: SpecialGenericSignatures.kt */
    public static final class TypeSafeBarrierDescription {
        private static final /* synthetic */ EnumEntries $ENTRIES;
        private static final /* synthetic */ TypeSafeBarrierDescription[] $VALUES;
        private final Object defaultValue;
        public static final TypeSafeBarrierDescription NULL = new TypeSafeBarrierDescription("NULL", 0, null);
        public static final TypeSafeBarrierDescription INDEX = new TypeSafeBarrierDescription("INDEX", 1, -1);
        public static final TypeSafeBarrierDescription FALSE = new TypeSafeBarrierDescription("FALSE", 2, false);
        public static final TypeSafeBarrierDescription MAP_GET_OR_DEFAULT = new MAP_GET_OR_DEFAULT("MAP_GET_OR_DEFAULT", 3);

        private static final /* synthetic */ TypeSafeBarrierDescription[] $values() {
            return new TypeSafeBarrierDescription[]{NULL, INDEX, FALSE, MAP_GET_OR_DEFAULT};
        }

        public /* synthetic */ TypeSafeBarrierDescription(String str, int i, Object obj, DefaultConstructorMarker defaultConstructorMarker) {
            this(str, i, obj);
        }

        private TypeSafeBarrierDescription(String str, int i, Object obj) {
            super(str, i);
            this.defaultValue = obj;
        }

        static {
            TypeSafeBarrierDescription[] typeSafeBarrierDescriptionArr$values = $values();
            $VALUES = typeSafeBarrierDescriptionArr$values;
            $ENTRIES = EnumEntriesKt.enumEntries(typeSafeBarrierDescriptionArr$values);
        }

        /* JADX INFO: compiled from: SpecialGenericSignatures.kt */
        static final class MAP_GET_OR_DEFAULT extends TypeSafeBarrierDescription {
            /* JADX WARN: Illegal instructions before constructor call */
            MAP_GET_OR_DEFAULT(String str, int i) {
                DefaultConstructorMarker defaultConstructorMarker = null;
                super(str, i, defaultConstructorMarker, defaultConstructorMarker);
            }
        }

        public static TypeSafeBarrierDescription valueOf(String str) {
            return (TypeSafeBarrierDescription) Enum.valueOf(TypeSafeBarrierDescription.class, str);
        }

        public static TypeSafeBarrierDescription[] values() {
            return (TypeSafeBarrierDescription[]) $VALUES.clone();
        }
    }

    /* JADX INFO: compiled from: SpecialGenericSignatures.kt */
    public enum SpecialSignatureInfo {
        ONE_COLLECTION_PARAMETER("Ljava/util/Collection<+Ljava/lang/Object;>;", false),
        OBJECT_PARAMETER_NON_GENERIC(null, true),
        OBJECT_PARAMETER_GENERIC("Ljava/lang/Object;", true);

        private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());
        private final boolean isObjectReplacedWithTypeParameter;
        private final String valueParametersSignature;

        SpecialSignatureInfo(String str, boolean z) {
            this.valueParametersSignature = str;
            this.isObjectReplacedWithTypeParameter = z;
        }
    }

    /* JADX INFO: compiled from: SpecialGenericSignatures.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final SpecialSignatureInfo getSpecialSignatureInfo(String builtinSignature) {
            Intrinsics.checkNotNullParameter(builtinSignature, "builtinSignature");
            if (getERASED_COLLECTION_PARAMETER_SIGNATURES().contains(builtinSignature)) {
                return SpecialSignatureInfo.ONE_COLLECTION_PARAMETER;
            }
            if (((TypeSafeBarrierDescription) MapsKt.getValue(getSIGNATURE_TO_DEFAULT_VALUES_MAP(), builtinSignature)) == TypeSafeBarrierDescription.NULL) {
                return SpecialSignatureInfo.OBJECT_PARAMETER_GENERIC;
            }
            return SpecialSignatureInfo.OBJECT_PARAMETER_NON_GENERIC;
        }

        /* JADX INFO: compiled from: SpecialGenericSignatures.kt */
        public static final class NameAndSignature {
            private final String classInternalName;
            private final Name name;
            private final String parameters;
            private final String returnType;
            private final String signature;

            public static /* synthetic */ NameAndSignature copy$default(NameAndSignature nameAndSignature, String str, Name name, String str2, String str3, int i, Object obj) {
                if ((i & 1) != 0) {
                    str = nameAndSignature.classInternalName;
                }
                if ((i & 2) != 0) {
                    name = nameAndSignature.name;
                }
                if ((i & 4) != 0) {
                    str2 = nameAndSignature.parameters;
                }
                if ((i & 8) != 0) {
                    str3 = nameAndSignature.returnType;
                }
                return nameAndSignature.copy(str, name, str2, str3);
            }

            public final NameAndSignature copy(String classInternalName, Name name, String parameters, String returnType) {
                Intrinsics.checkNotNullParameter(classInternalName, "classInternalName");
                Intrinsics.checkNotNullParameter(name, "name");
                Intrinsics.checkNotNullParameter(parameters, "parameters");
                Intrinsics.checkNotNullParameter(returnType, "returnType");
                return new NameAndSignature(classInternalName, name, parameters, returnType);
            }

            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (!(obj instanceof NameAndSignature)) {
                    return false;
                }
                NameAndSignature nameAndSignature = (NameAndSignature) obj;
                return Intrinsics.areEqual(this.classInternalName, nameAndSignature.classInternalName) && Intrinsics.areEqual(this.name, nameAndSignature.name) && Intrinsics.areEqual(this.parameters, nameAndSignature.parameters) && Intrinsics.areEqual(this.returnType, nameAndSignature.returnType);
            }

            public int hashCode() {
                return (((((this.classInternalName.hashCode() * 31) + this.name.hashCode()) * 31) + this.parameters.hashCode()) * 31) + this.returnType.hashCode();
            }

            public String toString() {
                return "NameAndSignature(classInternalName=" + this.classInternalName + ", name=" + this.name + ", parameters=" + this.parameters + ", returnType=" + this.returnType + ')';
            }

            public NameAndSignature(String classInternalName, Name name, String parameters, String returnType) {
                Intrinsics.checkNotNullParameter(classInternalName, "classInternalName");
                Intrinsics.checkNotNullParameter(name, "name");
                Intrinsics.checkNotNullParameter(parameters, "parameters");
                Intrinsics.checkNotNullParameter(returnType, "returnType");
                this.classInternalName = classInternalName;
                this.name = name;
                this.parameters = parameters;
                this.returnType = returnType;
                this.signature = SignatureBuildingComponents.INSTANCE.signature(classInternalName, name + '(' + parameters + ')' + returnType);
            }

            public final Name getName() {
                return this.name;
            }

            public final String getSignature() {
                return this.signature;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final NameAndSignature method(String str, String str2, String str3, String str4) {
            Name nameIdentifier = Name.identifier(str2);
            Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
            return new NameAndSignature(str, nameIdentifier, str3, str4);
        }

        public final List<String> getERASED_COLLECTION_PARAMETER_SIGNATURES() {
            return SpecialGenericSignatures.ERASED_COLLECTION_PARAMETER_SIGNATURES;
        }

        public final Map<String, TypeSafeBarrierDescription> getSIGNATURE_TO_DEFAULT_VALUES_MAP() {
            return SpecialGenericSignatures.SIGNATURE_TO_DEFAULT_VALUES_MAP;
        }

        public final Set<Name> getERASED_VALUE_PARAMETERS_SHORT_NAMES() {
            return SpecialGenericSignatures.ERASED_VALUE_PARAMETERS_SHORT_NAMES;
        }

        public final Set<String> getERASED_VALUE_PARAMETERS_SIGNATURES() {
            return SpecialGenericSignatures.ERASED_VALUE_PARAMETERS_SIGNATURES;
        }

        public final NameAndSignature getREMOVE_AT_NAME_AND_SIGNATURE() {
            return SpecialGenericSignatures.REMOVE_AT_NAME_AND_SIGNATURE;
        }

        public final Map<String, Name> getSIGNATURE_TO_JVM_REPRESENTATION_NAME() {
            return SpecialGenericSignatures.SIGNATURE_TO_JVM_REPRESENTATION_NAME;
        }

        public final Set<Name> getORIGINAL_SHORT_NAMES() {
            return SpecialGenericSignatures.ORIGINAL_SHORT_NAMES;
        }

        public final Map<Name, Name> getJVM_SHORT_NAME_TO_BUILTIN_SHORT_NAMES_MAP() {
            return SpecialGenericSignatures.JVM_SHORT_NAME_TO_BUILTIN_SHORT_NAMES_MAP;
        }

        public final Name getBuiltinFunctionNamesByJvmName(Name name) {
            Intrinsics.checkNotNullParameter(name, "name");
            return getJVM_SHORT_NAME_TO_BUILTIN_SHORT_NAMES_MAP().get(name);
        }

        public final boolean getSameAsRenamedInJvmBuiltin(Name name) {
            Intrinsics.checkNotNullParameter(name, "<this>");
            return getORIGINAL_SHORT_NAMES().contains(name);
        }
    }

    static {
        Set<String> of = SetsKt.setOf((Object[]) new String[]{"containsAll", "removeAll", "retainAll"});
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(of, 10));
        for (String str : of) {
            Companion companion = Companion;
            String desc = JvmPrimitiveType.BOOLEAN.getDesc();
            Intrinsics.checkNotNullExpressionValue(desc, "getDesc(...)");
            arrayList.add(companion.method("java/util/Collection", str, "Ljava/util/Collection;", desc));
        }
        ArrayList arrayList2 = arrayList;
        ERASED_COLLECTION_PARAMETER_NAME_AND_SIGNATURES = arrayList2;
        ArrayList arrayList3 = arrayList2;
        ArrayList arrayList4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(arrayList3, 10));
        Iterator it = arrayList3.iterator();
        while (it.hasNext()) {
            arrayList4.add(((Companion.NameAndSignature) it.next()).getSignature());
        }
        ERASED_COLLECTION_PARAMETER_SIGNATURES = arrayList4;
        List<Companion.NameAndSignature> list = ERASED_COLLECTION_PARAMETER_NAME_AND_SIGNATURES;
        ArrayList arrayList5 = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        Iterator<T> it2 = list.iterator();
        while (it2.hasNext()) {
            arrayList5.add(((Companion.NameAndSignature) it2.next()).getName().asString());
        }
        ERASED_COLLECTION_PARAMETER_NAMES = arrayList5;
        SignatureBuildingComponents signatureBuildingComponents = SignatureBuildingComponents.INSTANCE;
        Companion companion2 = Companion;
        String strJavaUtil = signatureBuildingComponents.javaUtil("Collection");
        String desc2 = JvmPrimitiveType.BOOLEAN.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc2, "getDesc(...)");
        String strJavaUtil2 = signatureBuildingComponents.javaUtil("Collection");
        String desc3 = JvmPrimitiveType.BOOLEAN.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc3, "getDesc(...)");
        String strJavaUtil3 = signatureBuildingComponents.javaUtil("Map");
        String desc4 = JvmPrimitiveType.BOOLEAN.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc4, "getDesc(...)");
        String strJavaUtil4 = signatureBuildingComponents.javaUtil("Map");
        String desc5 = JvmPrimitiveType.BOOLEAN.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc5, "getDesc(...)");
        String strJavaUtil5 = signatureBuildingComponents.javaUtil("Map");
        String desc6 = JvmPrimitiveType.BOOLEAN.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc6, "getDesc(...)");
        String strJavaUtil6 = signatureBuildingComponents.javaUtil("List");
        String desc7 = JvmPrimitiveType.INT.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc7, "getDesc(...)");
        String strJavaUtil7 = signatureBuildingComponents.javaUtil("List");
        String desc8 = JvmPrimitiveType.INT.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc8, "getDesc(...)");
        Map<Companion.NameAndSignature, TypeSafeBarrierDescription> mapMapOf = MapsKt.mapOf(TuplesKt.to(companion2.method(strJavaUtil, "contains", "Ljava/lang/Object;", desc2), TypeSafeBarrierDescription.FALSE), TuplesKt.to(companion2.method(strJavaUtil2, "remove", "Ljava/lang/Object;", desc3), TypeSafeBarrierDescription.FALSE), TuplesKt.to(companion2.method(strJavaUtil3, "containsKey", "Ljava/lang/Object;", desc4), TypeSafeBarrierDescription.FALSE), TuplesKt.to(companion2.method(strJavaUtil4, "containsValue", "Ljava/lang/Object;", desc5), TypeSafeBarrierDescription.FALSE), TuplesKt.to(companion2.method(strJavaUtil5, "remove", "Ljava/lang/Object;Ljava/lang/Object;", desc6), TypeSafeBarrierDescription.FALSE), TuplesKt.to(companion2.method(signatureBuildingComponents.javaUtil("Map"), "getOrDefault", "Ljava/lang/Object;Ljava/lang/Object;", "Ljava/lang/Object;"), TypeSafeBarrierDescription.MAP_GET_OR_DEFAULT), TuplesKt.to(companion2.method(signatureBuildingComponents.javaUtil("Map"), "get", "Ljava/lang/Object;", "Ljava/lang/Object;"), TypeSafeBarrierDescription.NULL), TuplesKt.to(companion2.method(signatureBuildingComponents.javaUtil("Map"), "remove", "Ljava/lang/Object;", "Ljava/lang/Object;"), TypeSafeBarrierDescription.NULL), TuplesKt.to(companion2.method(strJavaUtil6, "indexOf", "Ljava/lang/Object;", desc7), TypeSafeBarrierDescription.INDEX), TuplesKt.to(companion2.method(strJavaUtil7, "lastIndexOf", "Ljava/lang/Object;", desc8), TypeSafeBarrierDescription.INDEX));
        GENERIC_PARAMETERS_METHODS_TO_DEFAULT_VALUES_MAP = mapMapOf;
        LinkedHashMap linkedHashMap = new LinkedHashMap(MapsKt.mapCapacity(mapMapOf.size()));
        Iterator<T> it3 = mapMapOf.entrySet().iterator();
        while (it3.hasNext()) {
            Map.Entry entry = (Map.Entry) it3.next();
            linkedHashMap.put(((Companion.NameAndSignature) entry.getKey()).getSignature(), entry.getValue());
        }
        SIGNATURE_TO_DEFAULT_VALUES_MAP = linkedHashMap;
        Set setPlus = SetsKt.plus((Set) GENERIC_PARAMETERS_METHODS_TO_DEFAULT_VALUES_MAP.keySet(), (Iterable) ERASED_COLLECTION_PARAMETER_NAME_AND_SIGNATURES);
        ArrayList arrayList6 = new ArrayList(CollectionsKt.collectionSizeOrDefault(setPlus, 10));
        Iterator it4 = setPlus.iterator();
        while (it4.hasNext()) {
            arrayList6.add(((Companion.NameAndSignature) it4.next()).getName());
        }
        ERASED_VALUE_PARAMETERS_SHORT_NAMES = CollectionsKt.toSet(arrayList6);
        ArrayList arrayList7 = new ArrayList(CollectionsKt.collectionSizeOrDefault(setPlus, 10));
        Iterator it5 = setPlus.iterator();
        while (it5.hasNext()) {
            arrayList7.add(((Companion.NameAndSignature) it5.next()).getSignature());
        }
        ERASED_VALUE_PARAMETERS_SIGNATURES = CollectionsKt.toSet(arrayList7);
        Companion companion3 = Companion;
        String desc9 = JvmPrimitiveType.INT.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc9, "getDesc(...)");
        Companion.NameAndSignature nameAndSignatureMethod = companion3.method("java/util/List", "removeAt", desc9, "Ljava/lang/Object;");
        REMOVE_AT_NAME_AND_SIGNATURE = nameAndSignatureMethod;
        SignatureBuildingComponents signatureBuildingComponents2 = SignatureBuildingComponents.INSTANCE;
        String strJavaLang = signatureBuildingComponents2.javaLang("Number");
        String desc10 = JvmPrimitiveType.BYTE.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc10, "getDesc(...)");
        String strJavaLang2 = signatureBuildingComponents2.javaLang("Number");
        String desc11 = JvmPrimitiveType.SHORT.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc11, "getDesc(...)");
        String strJavaLang3 = signatureBuildingComponents2.javaLang("Number");
        String desc12 = JvmPrimitiveType.INT.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc12, "getDesc(...)");
        String strJavaLang4 = signatureBuildingComponents2.javaLang("Number");
        String desc13 = JvmPrimitiveType.LONG.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc13, "getDesc(...)");
        String strJavaLang5 = signatureBuildingComponents2.javaLang("Number");
        String desc14 = JvmPrimitiveType.FLOAT.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc14, "getDesc(...)");
        String strJavaLang6 = signatureBuildingComponents2.javaLang("Number");
        String desc15 = JvmPrimitiveType.DOUBLE.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc15, "getDesc(...)");
        String strJavaLang7 = signatureBuildingComponents2.javaLang("CharSequence");
        String desc16 = JvmPrimitiveType.INT.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc16, "getDesc(...)");
        String desc17 = JvmPrimitiveType.CHAR.getDesc();
        Intrinsics.checkNotNullExpressionValue(desc17, "getDesc(...)");
        Map<Companion.NameAndSignature, Name> mapMapOf2 = MapsKt.mapOf(TuplesKt.to(companion3.method(strJavaLang, "toByte", "", desc10), Name.identifier("byteValue")), TuplesKt.to(companion3.method(strJavaLang2, "toShort", "", desc11), Name.identifier("shortValue")), TuplesKt.to(companion3.method(strJavaLang3, "toInt", "", desc12), Name.identifier("intValue")), TuplesKt.to(companion3.method(strJavaLang4, "toLong", "", desc13), Name.identifier("longValue")), TuplesKt.to(companion3.method(strJavaLang5, "toFloat", "", desc14), Name.identifier("floatValue")), TuplesKt.to(companion3.method(strJavaLang6, "toDouble", "", desc15), Name.identifier("doubleValue")), TuplesKt.to(nameAndSignatureMethod, Name.identifier("remove")), TuplesKt.to(companion3.method(strJavaLang7, "get", desc16, desc17), Name.identifier("charAt")));
        NAME_AND_SIGNATURE_TO_JVM_REPRESENTATION_NAME_MAP = mapMapOf2;
        LinkedHashMap linkedHashMap2 = new LinkedHashMap(MapsKt.mapCapacity(mapMapOf2.size()));
        Iterator<T> it6 = mapMapOf2.entrySet().iterator();
        while (it6.hasNext()) {
            Map.Entry entry2 = (Map.Entry) it6.next();
            linkedHashMap2.put(((Companion.NameAndSignature) entry2.getKey()).getSignature(), entry2.getValue());
        }
        SIGNATURE_TO_JVM_REPRESENTATION_NAME = linkedHashMap2;
        Map<Companion.NameAndSignature, Name> map = NAME_AND_SIGNATURE_TO_JVM_REPRESENTATION_NAME_MAP;
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (Map.Entry<Companion.NameAndSignature, Name> entry3 : map.entrySet()) {
            linkedHashSet.add(Companion.NameAndSignature.copy$default(entry3.getKey(), null, entry3.getValue(), null, null, 13, null).getSignature());
        }
        JVM_SIGNATURES_FOR_RENAMED_BUILT_INS = linkedHashSet;
        Set<Companion.NameAndSignature> setKeySet = NAME_AND_SIGNATURE_TO_JVM_REPRESENTATION_NAME_MAP.keySet();
        HashSet hashSet = new HashSet();
        Iterator<T> it7 = setKeySet.iterator();
        while (it7.hasNext()) {
            hashSet.add(((Companion.NameAndSignature) it7.next()).getName());
        }
        ORIGINAL_SHORT_NAMES = hashSet;
        Set<Map.Entry<Companion.NameAndSignature, Name>> setEntrySet = NAME_AND_SIGNATURE_TO_JVM_REPRESENTATION_NAME_MAP.entrySet();
        ArrayList arrayList8 = new ArrayList(CollectionsKt.collectionSizeOrDefault(setEntrySet, 10));
        Iterator<T> it8 = setEntrySet.iterator();
        while (it8.hasNext()) {
            Map.Entry entry4 = (Map.Entry) it8.next();
            arrayList8.add(new Pair(((Companion.NameAndSignature) entry4.getKey()).getName(), entry4.getValue()));
        }
        ArrayList<Pair> arrayList9 = arrayList8;
        LinkedHashMap linkedHashMap3 = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(arrayList9, 10)), 16));
        for (Pair pair : arrayList9) {
            linkedHashMap3.put((Name) pair.getSecond(), (Name) pair.getFirst());
        }
        JVM_SHORT_NAME_TO_BUILTIN_SHORT_NAMES_MAP = linkedHashMap3;
    }
}
