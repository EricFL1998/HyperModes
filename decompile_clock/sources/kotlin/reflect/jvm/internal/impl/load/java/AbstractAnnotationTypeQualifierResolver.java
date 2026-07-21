package kotlin.reflect.jvm.internal.impl.load.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Pair;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames;
import kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.MutabilityQualifier;
import kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.NullabilityQualifierWithMigrationStatus;
import kotlin.reflect.jvm.internal.impl.name.FqName;

/* JADX INFO: compiled from: AbstractAnnotationTypeQualifierResolver.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class AbstractAnnotationTypeQualifierResolver<TAnnotation> {
    private static final Companion Companion = new Companion(null);
    private static final Map<String, AnnotationQualifierApplicabilityType> JAVA_APPLICABILITY_TYPES;
    private final JavaTypeEnhancementState javaTypeEnhancementState;
    private final ConcurrentHashMap<Object, TAnnotation> resolvedNicknames;

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean extractDefaultQualifiers$lambda$16(Object extractNullability) {
        Intrinsics.checkNotNullParameter(extractNullability, "$this$extractNullability");
        return false;
    }

    protected abstract Iterable<String> enumArguments(TAnnotation tannotation, boolean z);

    protected abstract FqName getFqName(TAnnotation tannotation);

    protected abstract Object getKey(TAnnotation tannotation);

    protected abstract Iterable<TAnnotation> getMetaAnnotations(TAnnotation tannotation);

    public AbstractAnnotationTypeQualifierResolver(JavaTypeEnhancementState javaTypeEnhancementState) {
        Intrinsics.checkNotNullParameter(javaTypeEnhancementState, "javaTypeEnhancementState");
        this.javaTypeEnhancementState = javaTypeEnhancementState;
        this.resolvedNicknames = new ConcurrentHashMap<>();
    }

    private final TAnnotation findAnnotation(TAnnotation tannotation, FqName fqName) {
        for (TAnnotation tannotation2 : getMetaAnnotations(tannotation)) {
            if (Intrinsics.areEqual(getFqName(tannotation2), fqName)) {
                return tannotation2;
            }
        }
        return null;
    }

    private final boolean hasAnnotation(TAnnotation tannotation, FqName fqName) {
        Iterable<TAnnotation> metaAnnotations = getMetaAnnotations(tannotation);
        if ((metaAnnotations instanceof Collection) && ((Collection) metaAnnotations).isEmpty()) {
            return false;
        }
        Iterator<TAnnotation> it = metaAnnotations.iterator();
        while (it.hasNext()) {
            if (Intrinsics.areEqual(getFqName(it.next()), fqName)) {
                return true;
            }
        }
        return false;
    }

    public final TAnnotation resolveTypeQualifierAnnotation(TAnnotation annotation) {
        TAnnotation tannotationResolveTypeQualifierAnnotation;
        Intrinsics.checkNotNullParameter(annotation, "annotation");
        if (this.javaTypeEnhancementState.getJsr305().isDisabled()) {
            return null;
        }
        if (CollectionsKt.contains(AnnotationQualifiersFqNamesKt.getBUILT_IN_TYPE_QUALIFIER_FQ_NAMES(), getFqName(annotation)) || hasAnnotation(annotation, AnnotationQualifiersFqNamesKt.getTYPE_QUALIFIER_FQNAME())) {
            return annotation;
        }
        if (!hasAnnotation(annotation, AnnotationQualifiersFqNamesKt.getTYPE_QUALIFIER_NICKNAME_FQNAME())) {
            return null;
        }
        ConcurrentHashMap<Object, TAnnotation> concurrentHashMap = this.resolvedNicknames;
        Object key = getKey(annotation);
        TAnnotation tannotation = concurrentHashMap.get(key);
        if (tannotation != null) {
            return tannotation;
        }
        Iterator<TAnnotation> it = getMetaAnnotations(annotation).iterator();
        do {
            if (!it.hasNext()) {
                tannotationResolveTypeQualifierAnnotation = null;
                break;
            }
            tannotationResolveTypeQualifierAnnotation = resolveTypeQualifierAnnotation(it.next());
        } while (tannotationResolveTypeQualifierAnnotation == null);
        if (tannotationResolveTypeQualifierAnnotation == null) {
            return null;
        }
        TAnnotation tannotationPutIfAbsent = concurrentHashMap.putIfAbsent(key, tannotationResolveTypeQualifierAnnotation);
        return tannotationPutIfAbsent == null ? tannotationResolveTypeQualifierAnnotation : tannotationPutIfAbsent;
    }

    private final JavaDefaultQualifiers resolveQualifierBuiltInDefaultAnnotation(TAnnotation tannotation) {
        JavaDefaultQualifiers javaDefaultQualifiers;
        if (this.javaTypeEnhancementState.getDisabledDefaultAnnotations() || (javaDefaultQualifiers = AnnotationQualifiersFqNamesKt.getBUILT_IN_TYPE_QUALIFIER_DEFAULT_ANNOTATIONS().get(getFqName(tannotation))) == null) {
            return null;
        }
        ReportLevel reportLevelResolveDefaultAnnotationState = resolveDefaultAnnotationState(tannotation);
        if (reportLevelResolveDefaultAnnotationState == ReportLevel.IGNORE) {
            reportLevelResolveDefaultAnnotationState = null;
        }
        if (reportLevelResolveDefaultAnnotationState == null) {
            return null;
        }
        return JavaDefaultQualifiers.copy$default(javaDefaultQualifiers, NullabilityQualifierWithMigrationStatus.copy$default(javaDefaultQualifiers.getNullabilityQualifier(), null, reportLevelResolveDefaultAnnotationState.isWarning(), 1, null), null, false, 6, null);
    }

    private final ReportLevel resolveDefaultAnnotationState(TAnnotation tannotation) {
        FqName fqName = getFqName(tannotation);
        if (fqName != null && AnnotationQualifiersFqNamesKt.getJSPECIFY_DEFAULT_ANNOTATIONS().containsKey(fqName)) {
            return this.javaTypeEnhancementState.getGetReportLevelForAnnotation().invoke(fqName);
        }
        return resolveJsr305AnnotationState(tannotation);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final Set<AnnotationQualifierApplicabilityType> allIfTypeUse(Set<? extends AnnotationQualifierApplicabilityType> set) {
        return set.contains(AnnotationQualifierApplicabilityType.TYPE_USE) ? SetsKt.plus(SetsKt.minus((Set<? extends AnnotationQualifierApplicabilityType>) ArraysKt.toSet(AnnotationQualifierApplicabilityType.values()), AnnotationQualifierApplicabilityType.TYPE_PARAMETER_BOUNDS), (Iterable) set) : set;
    }

    private final Pair<TAnnotation, Set<AnnotationQualifierApplicabilityType>> resolveTypeQualifierDefaultAnnotation(TAnnotation tannotation) {
        TAnnotation tannotationFindAnnotation;
        TAnnotation next;
        if (this.javaTypeEnhancementState.getJsr305().isDisabled() || (tannotationFindAnnotation = findAnnotation(tannotation, AnnotationQualifiersFqNamesKt.getTYPE_QUALIFIER_DEFAULT_FQNAME())) == null) {
            return null;
        }
        Iterator<TAnnotation> it = getMetaAnnotations(tannotation).iterator();
        do {
            if (!it.hasNext()) {
                next = null;
                break;
            }
            next = it.next();
        } while (resolveTypeQualifierAnnotation(next) == null);
        if (next == null) {
            return null;
        }
        Iterable<String> iterableEnumArguments = enumArguments(tannotationFindAnnotation, true);
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        Iterator<String> it2 = iterableEnumArguments.iterator();
        while (it2.hasNext()) {
            AnnotationQualifierApplicabilityType annotationQualifierApplicabilityType = JAVA_APPLICABILITY_TYPES.get(it2.next());
            if (annotationQualifierApplicabilityType != null) {
                linkedHashSet.add(annotationQualifierApplicabilityType);
            }
        }
        return new Pair<>(next, allIfTypeUse(linkedHashSet));
    }

    public final boolean isTypeUseAnnotation(TAnnotation annotation) {
        Intrinsics.checkNotNullParameter(annotation, "annotation");
        TAnnotation tannotationFindAnnotation = findAnnotation(annotation, StandardNames.FqNames.target);
        if (tannotationFindAnnotation == null) {
            return false;
        }
        Iterable<String> iterableEnumArguments = enumArguments(tannotationFindAnnotation, false);
        if ((iterableEnumArguments instanceof Collection) && ((Collection) iterableEnumArguments).isEmpty()) {
            return false;
        }
        Iterator<String> it = iterableEnumArguments.iterator();
        while (it.hasNext()) {
            if (Intrinsics.areEqual(it.next(), "TYPE")) {
                return true;
            }
        }
        return false;
    }

    private final ReportLevel resolveJsr305AnnotationState(TAnnotation tannotation) {
        ReportLevel reportLevelResolveJsr305CustomState = resolveJsr305CustomState(tannotation);
        return reportLevelResolveJsr305CustomState != null ? reportLevelResolveJsr305CustomState : this.javaTypeEnhancementState.getJsr305().getGlobalLevel();
    }

    private final ReportLevel resolveJsr305CustomState(TAnnotation tannotation) {
        Iterable<String> iterableEnumArguments;
        String str;
        ReportLevel reportLevel = this.javaTypeEnhancementState.getJsr305().getUserDefinedLevelForSpecificAnnotation().get(getFqName(tannotation));
        if (reportLevel != null) {
            return reportLevel;
        }
        TAnnotation tannotationFindAnnotation = findAnnotation(tannotation, AnnotationQualifiersFqNamesKt.getMIGRATION_ANNOTATION_FQNAME());
        if (tannotationFindAnnotation == null || (iterableEnumArguments = enumArguments(tannotationFindAnnotation, false)) == null || (str = (String) CollectionsKt.firstOrNull(iterableEnumArguments)) == null) {
            return null;
        }
        ReportLevel migrationLevel = this.javaTypeEnhancementState.getJsr305().getMigrationLevel();
        if (migrationLevel != null) {
            return migrationLevel;
        }
        int iHashCode = str.hashCode();
        if (iHashCode == -2137067054) {
            if (str.equals("IGNORE")) {
                return ReportLevel.IGNORE;
            }
            return null;
        }
        if (iHashCode == -1838656823) {
            if (str.equals("STRICT")) {
                return ReportLevel.STRICT;
            }
            return null;
        }
        if (iHashCode == 2656902 && str.equals("WARN")) {
            return ReportLevel.WARN;
        }
        return null;
    }

    private final NullabilityQualifierWithMigrationStatus extractNullability(TAnnotation tannotation, Function1<? super TAnnotation, Boolean> function1) {
        NullabilityQualifierWithMigrationStatus nullabilityQualifierWithMigrationStatusKnownNullability;
        NullabilityQualifierWithMigrationStatus nullabilityQualifierWithMigrationStatusKnownNullability2 = knownNullability(tannotation, function1.invoke(tannotation).booleanValue());
        if (nullabilityQualifierWithMigrationStatusKnownNullability2 != null) {
            return nullabilityQualifierWithMigrationStatusKnownNullability2;
        }
        TAnnotation tannotationResolveTypeQualifierAnnotation = resolveTypeQualifierAnnotation(tannotation);
        if (tannotationResolveTypeQualifierAnnotation == null) {
            return null;
        }
        ReportLevel reportLevelResolveJsr305AnnotationState = resolveJsr305AnnotationState(tannotation);
        if (reportLevelResolveJsr305AnnotationState.isIgnore() || (nullabilityQualifierWithMigrationStatusKnownNullability = knownNullability(tannotationResolveTypeQualifierAnnotation, function1.invoke(tannotationResolveTypeQualifierAnnotation).booleanValue())) == null) {
            return null;
        }
        return NullabilityQualifierWithMigrationStatus.copy$default(nullabilityQualifierWithMigrationStatusKnownNullability, null, reportLevelResolveJsr305AnnotationState.isWarning(), 1, null);
    }

    private final JavaDefaultQualifiers extractDefaultQualifiers(TAnnotation tannotation) {
        NullabilityQualifierWithMigrationStatus nullabilityQualifierWithMigrationStatusExtractNullability;
        JavaDefaultQualifiers javaDefaultQualifiersResolveQualifierBuiltInDefaultAnnotation = resolveQualifierBuiltInDefaultAnnotation(tannotation);
        if (javaDefaultQualifiersResolveQualifierBuiltInDefaultAnnotation != null) {
            return javaDefaultQualifiersResolveQualifierBuiltInDefaultAnnotation;
        }
        Pair<TAnnotation, Set<AnnotationQualifierApplicabilityType>> pairResolveTypeQualifierDefaultAnnotation = resolveTypeQualifierDefaultAnnotation(tannotation);
        if (pairResolveTypeQualifierDefaultAnnotation == null) {
            return null;
        }
        TAnnotation tannotationComponent1 = pairResolveTypeQualifierDefaultAnnotation.component1();
        Set<AnnotationQualifierApplicabilityType> setComponent2 = pairResolveTypeQualifierDefaultAnnotation.component2();
        ReportLevel reportLevelResolveJsr305CustomState = resolveJsr305CustomState(tannotation);
        if (reportLevelResolveJsr305CustomState == null) {
            reportLevelResolveJsr305CustomState = resolveJsr305AnnotationState(tannotationComponent1);
        }
        if (reportLevelResolveJsr305CustomState.isIgnore() || (nullabilityQualifierWithMigrationStatusExtractNullability = extractNullability(tannotationComponent1, new Function1() { // from class: kotlin.reflect.jvm.internal.impl.load.java.AbstractAnnotationTypeQualifierResolver$$Lambda$1
            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return Boolean.valueOf(AbstractAnnotationTypeQualifierResolver.extractDefaultQualifiers$lambda$16(obj));
            }
        })) == null) {
            return null;
        }
        return new JavaDefaultQualifiers(NullabilityQualifierWithMigrationStatus.copy$default(nullabilityQualifierWithMigrationStatusExtractNullability, null, reportLevelResolveJsr305CustomState.isWarning(), 1, null), setComponent2, false, 4, null);
    }

    public final JavaTypeQualifiersByElementType extractAndMergeDefaultQualifiers(JavaTypeQualifiersByElementType javaTypeQualifiersByElementType, Iterable<? extends TAnnotation> annotations) {
        EnumMap<AnnotationQualifierApplicabilityType, JavaDefaultQualifiers> defaultQualifiers;
        Intrinsics.checkNotNullParameter(annotations, "annotations");
        if (this.javaTypeEnhancementState.getDisabledDefaultAnnotations()) {
            return javaTypeQualifiersByElementType;
        }
        ArrayList arrayList = new ArrayList();
        Iterator<? extends TAnnotation> it = annotations.iterator();
        while (it.hasNext()) {
            JavaDefaultQualifiers javaDefaultQualifiersExtractDefaultQualifiers = extractDefaultQualifiers(it.next());
            if (javaDefaultQualifiersExtractDefaultQualifiers != null) {
                arrayList.add(javaDefaultQualifiersExtractDefaultQualifiers);
            }
        }
        ArrayList<JavaDefaultQualifiers> arrayList2 = arrayList;
        if (arrayList2.isEmpty()) {
            return javaTypeQualifiersByElementType;
        }
        EnumMap enumMap = (javaTypeQualifiersByElementType == null || (defaultQualifiers = javaTypeQualifiersByElementType.getDefaultQualifiers()) == null) ? new EnumMap(AnnotationQualifierApplicabilityType.class) : new EnumMap((EnumMap) defaultQualifiers);
        boolean z = false;
        for (JavaDefaultQualifiers javaDefaultQualifiers : arrayList2) {
            Iterator<AnnotationQualifierApplicabilityType> it2 = javaDefaultQualifiers.getQualifierApplicabilityTypes().iterator();
            while (it2.hasNext()) {
                enumMap.put(it2.next(), javaDefaultQualifiers);
                z = true;
            }
        }
        return !z ? javaTypeQualifiersByElementType : new JavaTypeQualifiersByElementType(enumMap);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0096, code lost:
    
        if (r6.equals("ALWAYS") != false) goto L49;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00ab, code lost:
    
        if (r6.equals("NEVER") == false) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00b4, code lost:
    
        if (r6.equals("MAYBE") == false) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00b7, code lost:
    
        r6 = kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.NullabilityQualifier.NULLABLE;
     */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.NullabilityQualifierWithMigrationStatus knownNullability(TAnnotation r6, boolean r7) {
        /*
            Method dump skipped, instruction units count: 282
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.reflect.jvm.internal.impl.load.java.AbstractAnnotationTypeQualifierResolver.knownNullability(java.lang.Object, boolean):kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.NullabilityQualifierWithMigrationStatus");
    }

    /* JADX INFO: compiled from: AbstractAnnotationTypeQualifierResolver.kt */
    private static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    static {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (AnnotationQualifierApplicabilityType annotationQualifierApplicabilityType : AnnotationQualifierApplicabilityType.values()) {
            String javaTarget = annotationQualifierApplicabilityType.getJavaTarget();
            if (linkedHashMap.get(javaTarget) == null) {
                linkedHashMap.put(javaTarget, annotationQualifierApplicabilityType);
            }
        }
        JAVA_APPLICABILITY_TYPES = linkedHashMap;
    }

    public final NullabilityQualifierWithMigrationStatus extractNullability(Iterable<? extends TAnnotation> annotations, Function1<? super TAnnotation, Boolean> forceWarning) {
        Intrinsics.checkNotNullParameter(annotations, "annotations");
        Intrinsics.checkNotNullParameter(forceWarning, "forceWarning");
        Iterator<? extends TAnnotation> it = annotations.iterator();
        NullabilityQualifierWithMigrationStatus nullabilityQualifierWithMigrationStatus = null;
        while (it.hasNext()) {
            NullabilityQualifierWithMigrationStatus nullabilityQualifierWithMigrationStatusExtractNullability = extractNullability(it.next(), forceWarning);
            if (nullabilityQualifierWithMigrationStatus != null) {
                if (nullabilityQualifierWithMigrationStatusExtractNullability != null && !Intrinsics.areEqual(nullabilityQualifierWithMigrationStatusExtractNullability, nullabilityQualifierWithMigrationStatus) && (!nullabilityQualifierWithMigrationStatusExtractNullability.isForWarningOnly() || nullabilityQualifierWithMigrationStatus.isForWarningOnly())) {
                    if (nullabilityQualifierWithMigrationStatusExtractNullability.isForWarningOnly() || !nullabilityQualifierWithMigrationStatus.isForWarningOnly()) {
                        return null;
                    }
                }
            }
            nullabilityQualifierWithMigrationStatus = nullabilityQualifierWithMigrationStatusExtractNullability;
        }
        return nullabilityQualifierWithMigrationStatus;
    }

    public final MutabilityQualifier extractMutability(Iterable<? extends TAnnotation> annotations) {
        MutabilityQualifier mutabilityQualifier;
        Intrinsics.checkNotNullParameter(annotations, "annotations");
        Iterator<? extends TAnnotation> it = annotations.iterator();
        MutabilityQualifier mutabilityQualifier2 = null;
        while (it.hasNext()) {
            FqName fqName = getFqName(it.next());
            if (JvmAnnotationNamesKt.getREAD_ONLY_ANNOTATIONS().contains(fqName)) {
                mutabilityQualifier = MutabilityQualifier.READ_ONLY;
            } else if (JvmAnnotationNamesKt.getMUTABLE_ANNOTATIONS().contains(fqName)) {
                mutabilityQualifier = MutabilityQualifier.MUTABLE;
            } else {
                continue;
            }
            if (mutabilityQualifier2 != null && mutabilityQualifier2 != mutabilityQualifier) {
                return null;
            }
            mutabilityQualifier2 = mutabilityQualifier;
        }
        return mutabilityQualifier2;
    }
}
