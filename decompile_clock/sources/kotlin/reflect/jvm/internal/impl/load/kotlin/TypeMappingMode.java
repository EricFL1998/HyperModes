package kotlin.reflect.jvm.internal.impl.load.kotlin;

import com.android.deskclock.R2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.types.Variance;

/* JADX INFO: compiled from: TypeMappingMode.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class TypeMappingMode {
    public static final TypeMappingMode CLASS_DECLARATION;
    public static final Companion Companion = new Companion(null);
    public static final TypeMappingMode DEFAULT;
    public static final TypeMappingMode DEFAULT_UAST;
    public static final TypeMappingMode GENERIC_ARGUMENT;
    public static final TypeMappingMode GENERIC_ARGUMENT_UAST;
    public static final TypeMappingMode RETURN_TYPE_BOXED;
    public static final TypeMappingMode SUPER_TYPE;
    public static final TypeMappingMode SUPER_TYPE_KOTLIN_COLLECTIONS_AS_IS;
    public static final TypeMappingMode VALUE_FOR_ANNOTATION;
    private final TypeMappingMode genericArgumentMode;
    private final TypeMappingMode genericContravariantArgumentMode;
    private final TypeMappingMode genericInvariantArgumentMode;
    private final boolean isForAnnotationParameter;
    private final boolean kotlinCollectionsToJavaCollections;
    private final boolean mapTypeAliases;
    private final boolean needInlineClassWrapping;
    private final boolean needPrimitiveBoxing;
    private final boolean skipDeclarationSiteWildcards;
    private final boolean skipDeclarationSiteWildcardsIfPossible;

    /* JADX INFO: compiled from: TypeMappingMode.kt */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[Variance.values().length];
            try {
                iArr[Variance.IN_VARIANCE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[Variance.INVARIANT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public TypeMappingMode() {
        this(false, false, false, false, false, null, false, null, null, false, R2.attr.filterSortTabViewTabActivatedTextAppearance, null);
    }

    public TypeMappingMode(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, TypeMappingMode typeMappingMode, boolean z6, TypeMappingMode typeMappingMode2, TypeMappingMode typeMappingMode3, boolean z7) {
        this.needPrimitiveBoxing = z;
        this.needInlineClassWrapping = z2;
        this.isForAnnotationParameter = z3;
        this.skipDeclarationSiteWildcards = z4;
        this.skipDeclarationSiteWildcardsIfPossible = z5;
        this.genericArgumentMode = typeMappingMode;
        this.kotlinCollectionsToJavaCollections = z6;
        this.genericContravariantArgumentMode = typeMappingMode2;
        this.genericInvariantArgumentMode = typeMappingMode3;
        this.mapTypeAliases = z7;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public /* synthetic */ TypeMappingMode(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, TypeMappingMode typeMappingMode, boolean z6, TypeMappingMode typeMappingMode2, TypeMappingMode typeMappingMode3, boolean z7, int i, DefaultConstructorMarker defaultConstructorMarker) {
        boolean z8 = (i & 1) != 0 ? true : z;
        boolean z9 = (i & 2) != 0 ? true : z2;
        boolean z10 = (i & 4) != 0 ? false : z3;
        boolean z11 = (i & 8) != 0 ? false : z4;
        boolean z12 = (i & 16) != 0 ? false : z5;
        TypeMappingMode typeMappingMode4 = (i & 32) != 0 ? null : typeMappingMode;
        this(z8, z9, z10, z11, z12, typeMappingMode4, (i & 64) == 0 ? z6 : true, (i & 128) != 0 ? typeMappingMode4 : typeMappingMode2, (i & 256) != 0 ? typeMappingMode4 : typeMappingMode3, (i & 512) == 0 ? z7 : false);
    }

    public final boolean getNeedPrimitiveBoxing() {
        return this.needPrimitiveBoxing;
    }

    public final boolean getNeedInlineClassWrapping() {
        return this.needInlineClassWrapping;
    }

    public final boolean isForAnnotationParameter() {
        return this.isForAnnotationParameter;
    }

    public final boolean getKotlinCollectionsToJavaCollections() {
        return this.kotlinCollectionsToJavaCollections;
    }

    public final boolean getMapTypeAliases() {
        return this.mapTypeAliases;
    }

    /* JADX INFO: compiled from: TypeMappingMode.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    static {
        DefaultConstructorMarker defaultConstructorMarker = null;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        TypeMappingMode typeMappingMode = null;
        boolean z5 = false;
        TypeMappingMode typeMappingMode2 = null;
        TypeMappingMode typeMappingMode3 = null;
        boolean z6 = false;
        TypeMappingMode typeMappingMode4 = new TypeMappingMode(z, false, z2, z3, z4, typeMappingMode, z5, typeMappingMode2, typeMappingMode3, z6, R2.attr.filterSortTabViewTabActivatedTextAppearance, defaultConstructorMarker);
        GENERIC_ARGUMENT = typeMappingMode4;
        DefaultConstructorMarker defaultConstructorMarker2 = null;
        boolean z7 = false;
        boolean z8 = false;
        boolean z9 = false;
        boolean z10 = false;
        boolean z11 = false;
        boolean z12 = false;
        TypeMappingMode typeMappingMode5 = null;
        TypeMappingMode typeMappingMode6 = null;
        boolean z13 = true;
        TypeMappingMode typeMappingMode7 = new TypeMappingMode(z7, z8, z9, z10, z11, null, z12, typeMappingMode5, typeMappingMode6, z13, 511, defaultConstructorMarker2);
        GENERIC_ARGUMENT_UAST = typeMappingMode7;
        RETURN_TYPE_BOXED = new TypeMappingMode(z, true, z2, z3, z4, typeMappingMode, z5, typeMappingMode2, typeMappingMode3, z6, 1021, defaultConstructorMarker);
        int i = R2.attr.fabAlignmentModeEndMargin;
        DEFAULT = new TypeMappingMode(z, false, z2, z3, z4, typeMappingMode4, z5, typeMappingMode2, typeMappingMode3, z6, i, defaultConstructorMarker);
        DEFAULT_UAST = new TypeMappingMode(z7, z8, z9, z10, z11, typeMappingMode7, z12, typeMappingMode5, typeMappingMode6, z13, R2.attr.cardBlendColorModes, defaultConstructorMarker2);
        CLASS_DECLARATION = new TypeMappingMode(z, true, z2, z3, z4, typeMappingMode4, z5, typeMappingMode2, typeMappingMode3, z6, i, defaultConstructorMarker);
        boolean z14 = false;
        boolean z15 = true;
        SUPER_TYPE = new TypeMappingMode(z, z14, z2, z15, z4, typeMappingMode4, z5, typeMappingMode2, typeMappingMode3, z6, R2.attr.extendedFloatingActionButtonStyle, defaultConstructorMarker);
        SUPER_TYPE_KOTLIN_COLLECTIONS_AS_IS = new TypeMappingMode(z, z14, z2, z15, z4, typeMappingMode4, z5, typeMappingMode2, typeMappingMode3, z6, R2.attr.emptyStateViewButtonStyle, defaultConstructorMarker);
        VALUE_FOR_ANNOTATION = new TypeMappingMode(z, z14, true, false, z4, typeMappingMode4, z5, typeMappingMode2, typeMappingMode3, z6, R2.attr.extendedFloatingActionButtonSurfaceStyle, defaultConstructorMarker);
    }

    public final TypeMappingMode toGenericArgumentMode(Variance effectiveVariance, boolean z) {
        Intrinsics.checkNotNullParameter(effectiveVariance, "effectiveVariance");
        if (!z || !this.isForAnnotationParameter) {
            int i = WhenMappings.$EnumSwitchMapping$0[effectiveVariance.ordinal()];
            if (i == 1) {
                TypeMappingMode typeMappingMode = this.genericContravariantArgumentMode;
                if (typeMappingMode != null) {
                    return typeMappingMode;
                }
            } else if (i == 2) {
                TypeMappingMode typeMappingMode2 = this.genericInvariantArgumentMode;
                if (typeMappingMode2 != null) {
                    return typeMappingMode2;
                }
            } else {
                TypeMappingMode typeMappingMode3 = this.genericArgumentMode;
                if (typeMappingMode3 != null) {
                    return typeMappingMode3;
                }
            }
        }
        return this;
    }

    public final TypeMappingMode wrapInlineClassesMode() {
        return new TypeMappingMode(this.needPrimitiveBoxing, true, this.isForAnnotationParameter, this.skipDeclarationSiteWildcards, this.skipDeclarationSiteWildcardsIfPossible, this.genericArgumentMode, this.kotlinCollectionsToJavaCollections, this.genericContravariantArgumentMode, this.genericInvariantArgumentMode, false, 512, null);
    }
}
