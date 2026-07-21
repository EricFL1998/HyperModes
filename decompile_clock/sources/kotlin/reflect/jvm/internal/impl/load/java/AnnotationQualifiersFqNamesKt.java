package kotlin.reflect.jvm.internal.impl.load.java;

import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.NullabilityQualifier;
import kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.NullabilityQualifierWithMigrationStatus;
import kotlin.reflect.jvm.internal.impl.name.FqName;

/* JADX INFO: compiled from: AnnotationQualifiersFqNames.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class AnnotationQualifiersFqNamesKt {
    private static final Map<FqName, JavaDefaultQualifiers> BUILT_IN_TYPE_QUALIFIER_DEFAULT_ANNOTATIONS;
    private static final Set<FqName> BUILT_IN_TYPE_QUALIFIER_FQ_NAMES;
    private static final List<AnnotationQualifierApplicabilityType> DEFAULT_JSPECIFY_APPLICABILITY;
    private static final Map<FqName, JavaDefaultQualifiers> JSPECIFY_DEFAULT_ANNOTATIONS;
    private static final FqName TYPE_QUALIFIER_NICKNAME_FQNAME = new FqName("javax.annotation.meta.TypeQualifierNickname");
    private static final FqName TYPE_QUALIFIER_FQNAME = new FqName("javax.annotation.meta.TypeQualifier");
    private static final FqName TYPE_QUALIFIER_DEFAULT_FQNAME = new FqName("javax.annotation.meta.TypeQualifierDefault");
    private static final FqName MIGRATION_ANNOTATION_FQNAME = new FqName("kotlin.annotations.jvm.UnderMigration");

    static {
        List<AnnotationQualifierApplicabilityType> listListOf = CollectionsKt.listOf((Object[]) new AnnotationQualifierApplicabilityType[]{AnnotationQualifierApplicabilityType.FIELD, AnnotationQualifierApplicabilityType.METHOD_RETURN_TYPE, AnnotationQualifierApplicabilityType.VALUE_PARAMETER, AnnotationQualifierApplicabilityType.TYPE_PARAMETER_BOUNDS, AnnotationQualifierApplicabilityType.TYPE_USE});
        DEFAULT_JSPECIFY_APPLICABILITY = listListOf;
        Map<FqName, JavaDefaultQualifiers> mapMapOf = MapsKt.mapOf(TuplesKt.to(JvmAnnotationNamesKt.getJSPECIFY_OLD_NULL_MARKED(), new JavaDefaultQualifiers(new NullabilityQualifierWithMigrationStatus(NullabilityQualifier.NOT_NULL, false, 2, null), listListOf, false)), TuplesKt.to(JvmAnnotationNamesKt.getJSPECIFY_NULL_MARKED(), new JavaDefaultQualifiers(new NullabilityQualifierWithMigrationStatus(NullabilityQualifier.NOT_NULL, false, 2, null), listListOf, false)));
        JSPECIFY_DEFAULT_ANNOTATIONS = mapMapOf;
        BUILT_IN_TYPE_QUALIFIER_DEFAULT_ANNOTATIONS = MapsKt.plus(MapsKt.mapOf(TuplesKt.to(new FqName("javax.annotation.ParametersAreNullableByDefault"), new JavaDefaultQualifiers(new NullabilityQualifierWithMigrationStatus(NullabilityQualifier.NULLABLE, false, 2, null), CollectionsKt.listOf(AnnotationQualifierApplicabilityType.VALUE_PARAMETER), false, 4, null)), TuplesKt.to(new FqName("javax.annotation.ParametersAreNonnullByDefault"), new JavaDefaultQualifiers(new NullabilityQualifierWithMigrationStatus(NullabilityQualifier.NOT_NULL, false, 2, null), CollectionsKt.listOf(AnnotationQualifierApplicabilityType.VALUE_PARAMETER), false, 4, null))), mapMapOf);
        BUILT_IN_TYPE_QUALIFIER_FQ_NAMES = SetsKt.setOf((Object[]) new FqName[]{JvmAnnotationNamesKt.getJAVAX_NONNULL_ANNOTATION(), JvmAnnotationNamesKt.getJAVAX_CHECKFORNULL_ANNOTATION()});
    }

    public static final FqName getTYPE_QUALIFIER_NICKNAME_FQNAME() {
        return TYPE_QUALIFIER_NICKNAME_FQNAME;
    }

    public static final FqName getTYPE_QUALIFIER_FQNAME() {
        return TYPE_QUALIFIER_FQNAME;
    }

    public static final FqName getTYPE_QUALIFIER_DEFAULT_FQNAME() {
        return TYPE_QUALIFIER_DEFAULT_FQNAME;
    }

    public static final FqName getMIGRATION_ANNOTATION_FQNAME() {
        return MIGRATION_ANNOTATION_FQNAME;
    }

    public static final Map<FqName, JavaDefaultQualifiers> getJSPECIFY_DEFAULT_ANNOTATIONS() {
        return JSPECIFY_DEFAULT_ANNOTATIONS;
    }

    public static final Map<FqName, JavaDefaultQualifiers> getBUILT_IN_TYPE_QUALIFIER_DEFAULT_ANNOTATIONS() {
        return BUILT_IN_TYPE_QUALIFIER_DEFAULT_ANNOTATIONS;
    }

    public static final Set<FqName> getBUILT_IN_TYPE_QUALIFIER_FQ_NAMES() {
        return BUILT_IN_TYPE_QUALIFIER_FQ_NAMES;
    }
}
