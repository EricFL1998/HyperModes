package kotlin.reflect.jvm.internal.impl.types.error;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.TypeProjection;
import kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt;

/* JADX INFO: compiled from: ErrorUtils.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ErrorUtils {
    private static final ErrorClassDescriptor errorClass;
    private static final PropertyDescriptor errorProperty;
    private static final Set<PropertyDescriptor> errorPropertyGroup;
    private static final KotlinType errorPropertyType;
    private static final KotlinType errorTypeForLoopInSupertypes;
    public static final ErrorUtils INSTANCE = new ErrorUtils();
    private static final ModuleDescriptor errorModule = ErrorModuleDescriptor.INSTANCE;

    private ErrorUtils() {
    }

    static {
        String str = String.format(ErrorEntity.ERROR_CLASS.getDebugText(), Arrays.copyOf(new Object[]{"unknown class"}, 1));
        Intrinsics.checkNotNullExpressionValue(str, "format(...)");
        Name nameSpecial = Name.special(str);
        Intrinsics.checkNotNullExpressionValue(nameSpecial, "special(...)");
        errorClass = new ErrorClassDescriptor(nameSpecial);
        errorTypeForLoopInSupertypes = createErrorType(ErrorTypeKind.CYCLIC_SUPERTYPES, new String[0]);
        errorPropertyType = createErrorType(ErrorTypeKind.ERROR_PROPERTY_TYPE, new String[0]);
        ErrorPropertyDescriptor errorPropertyDescriptor = new ErrorPropertyDescriptor();
        errorProperty = errorPropertyDescriptor;
        errorPropertyGroup = SetsKt.setOf(errorPropertyDescriptor);
    }

    public final ModuleDescriptor getErrorModule() {
        return errorModule;
    }

    public final ErrorClassDescriptor getErrorClass() {
        return errorClass;
    }

    public final KotlinType getErrorTypeForLoopInSupertypes() {
        return errorTypeForLoopInSupertypes;
    }

    public final KotlinType getErrorPropertyType() {
        return errorPropertyType;
    }

    public final Set<PropertyDescriptor> getErrorPropertyGroup() {
        return errorPropertyGroup;
    }

    @JvmStatic
    public static final ErrorScope createErrorScope(ErrorScopeKind kind, String... formatParams) {
        Intrinsics.checkNotNullParameter(kind, "kind");
        Intrinsics.checkNotNullParameter(formatParams, "formatParams");
        return createErrorScope(kind, false, (String[]) Arrays.copyOf(formatParams, formatParams.length));
    }

    @JvmStatic
    public static final ErrorScope createErrorScope(ErrorScopeKind kind, boolean z, String... formatParams) {
        Intrinsics.checkNotNullParameter(kind, "kind");
        Intrinsics.checkNotNullParameter(formatParams, "formatParams");
        return z ? new ThrowingScope(kind, (String[]) Arrays.copyOf(formatParams, formatParams.length)) : new ErrorScope(kind, (String[]) Arrays.copyOf(formatParams, formatParams.length));
    }

    @JvmStatic
    public static final ErrorType createErrorType(ErrorTypeKind kind, String... formatParams) {
        Intrinsics.checkNotNullParameter(kind, "kind");
        Intrinsics.checkNotNullParameter(formatParams, "formatParams");
        return INSTANCE.createErrorTypeWithArguments(kind, CollectionsKt.emptyList(), (String[]) Arrays.copyOf(formatParams, formatParams.length));
    }

    public final ErrorType createErrorType(ErrorTypeKind kind, TypeConstructor typeConstructor, String... formatParams) {
        Intrinsics.checkNotNullParameter(kind, "kind");
        Intrinsics.checkNotNullParameter(typeConstructor, "typeConstructor");
        Intrinsics.checkNotNullParameter(formatParams, "formatParams");
        return createErrorTypeWithArguments(kind, CollectionsKt.emptyList(), typeConstructor, (String[]) Arrays.copyOf(formatParams, formatParams.length));
    }

    public final ErrorType createErrorTypeWithArguments(ErrorTypeKind kind, List<? extends TypeProjection> arguments, String... formatParams) {
        Intrinsics.checkNotNullParameter(kind, "kind");
        Intrinsics.checkNotNullParameter(arguments, "arguments");
        Intrinsics.checkNotNullParameter(formatParams, "formatParams");
        return createErrorTypeWithArguments(kind, arguments, createErrorTypeConstructor(kind, (String[]) Arrays.copyOf(formatParams, formatParams.length)), (String[]) Arrays.copyOf(formatParams, formatParams.length));
    }

    public final ErrorType createErrorTypeWithArguments(ErrorTypeKind kind, List<? extends TypeProjection> arguments, TypeConstructor typeConstructor, String... formatParams) {
        Intrinsics.checkNotNullParameter(kind, "kind");
        Intrinsics.checkNotNullParameter(arguments, "arguments");
        Intrinsics.checkNotNullParameter(typeConstructor, "typeConstructor");
        Intrinsics.checkNotNullParameter(formatParams, "formatParams");
        return new ErrorType(typeConstructor, createErrorScope(ErrorScopeKind.ERROR_TYPE_SCOPE, typeConstructor.toString()), kind, arguments, false, (String[]) Arrays.copyOf(formatParams, formatParams.length));
    }

    public final ErrorTypeConstructor createErrorTypeConstructor(ErrorTypeKind kind, String... formatParams) {
        Intrinsics.checkNotNullParameter(kind, "kind");
        Intrinsics.checkNotNullParameter(formatParams, "formatParams");
        return new ErrorTypeConstructor(kind, (String[]) Arrays.copyOf(formatParams, formatParams.length));
    }

    @JvmStatic
    public static final boolean isError(DeclarationDescriptor declarationDescriptor) {
        if (declarationDescriptor != null) {
            ErrorUtils errorUtils = INSTANCE;
            if (errorUtils.isErrorClass(declarationDescriptor) || errorUtils.isErrorClass(declarationDescriptor.getContainingDeclaration()) || declarationDescriptor == errorModule) {
                return true;
            }
        }
        return false;
    }

    private final boolean isErrorClass(DeclarationDescriptor declarationDescriptor) {
        return declarationDescriptor instanceof ErrorClassDescriptor;
    }

    @JvmStatic
    public static final boolean isUninferredTypeVariable(KotlinType kotlinType) {
        if (kotlinType == null) {
            return false;
        }
        TypeConstructor constructor = kotlinType.getConstructor();
        return (constructor instanceof ErrorTypeConstructor) && ((ErrorTypeConstructor) constructor).getKind() == ErrorTypeKind.UNINFERRED_TYPE_VARIABLE;
    }

    public final String unresolvedTypeAsItIs(KotlinType type) {
        Intrinsics.checkNotNullParameter(type, "type");
        TypeUtilsKt.isUnresolvedType(type);
        TypeConstructor constructor = type.getConstructor();
        Intrinsics.checkNotNull(constructor, "null cannot be cast to non-null type org.jetbrains.kotlin.types.error.ErrorTypeConstructor");
        return ((ErrorTypeConstructor) constructor).getParam(0);
    }
}
