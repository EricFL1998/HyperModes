package kotlin.reflect.jvm.internal.impl.descriptors.annotations;

import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns;
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.constants.AnnotationValue;
import kotlin.reflect.jvm.internal.impl.resolve.constants.ArrayValue;
import kotlin.reflect.jvm.internal.impl.resolve.constants.EnumValue;
import kotlin.reflect.jvm.internal.impl.resolve.constants.StringValue;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.Variance;

/* JADX INFO: compiled from: annotationUtil.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class AnnotationUtilKt {
    private static final Name DEPRECATED_LEVEL_NAME;
    private static final Name DEPRECATED_MESSAGE_NAME;
    private static final Name DEPRECATED_REPLACE_WITH_NAME;
    private static final Name REPLACE_WITH_EXPRESSION_NAME;
    private static final Name REPLACE_WITH_IMPORTS_NAME;

    public static /* synthetic */ AnnotationDescriptor createDeprecatedAnnotation$default(KotlinBuiltIns kotlinBuiltIns, String str, String str2, String str3, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            str2 = "";
        }
        if ((i & 4) != 0) {
            str3 = "WARNING";
        }
        if ((i & 8) != 0) {
            z = false;
        }
        return createDeprecatedAnnotation(kotlinBuiltIns, str, str2, str3, z);
    }

    public static final AnnotationDescriptor createDeprecatedAnnotation(final KotlinBuiltIns kotlinBuiltIns, String message, String replaceWith, String level, boolean z) {
        Intrinsics.checkNotNullParameter(kotlinBuiltIns, "<this>");
        Intrinsics.checkNotNullParameter(message, "message");
        Intrinsics.checkNotNullParameter(replaceWith, "replaceWith");
        Intrinsics.checkNotNullParameter(level, "level");
        BuiltInAnnotationDescriptor builtInAnnotationDescriptor = new BuiltInAnnotationDescriptor(kotlinBuiltIns, StandardNames.FqNames.replaceWith, MapsKt.mapOf(TuplesKt.to(REPLACE_WITH_EXPRESSION_NAME, new StringValue(replaceWith)), TuplesKt.to(REPLACE_WITH_IMPORTS_NAME, new ArrayValue(CollectionsKt.emptyList(), new Function1(kotlinBuiltIns) { // from class: kotlin.reflect.jvm.internal.impl.descriptors.annotations.AnnotationUtilKt$$Lambda$0
            private final KotlinBuiltIns arg$0;

            {
                this.arg$0 = kotlinBuiltIns;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return AnnotationUtilKt.createDeprecatedAnnotation$lambda$0(this.arg$0, (ModuleDescriptor) obj);
            }
        }))), false, 8, null);
        FqName fqName = StandardNames.FqNames.deprecated;
        Name name = DEPRECATED_LEVEL_NAME;
        ClassId classId = ClassId.Companion.topLevel(StandardNames.FqNames.deprecationLevel);
        Name nameIdentifier = Name.identifier(level);
        Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
        return new BuiltInAnnotationDescriptor(kotlinBuiltIns, fqName, MapsKt.mapOf(TuplesKt.to(DEPRECATED_MESSAGE_NAME, new StringValue(message)), TuplesKt.to(DEPRECATED_REPLACE_WITH_NAME, new AnnotationValue(builtInAnnotationDescriptor)), TuplesKt.to(name, new EnumValue(classId, nameIdentifier))), z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final KotlinType createDeprecatedAnnotation$lambda$0(KotlinBuiltIns this_createDeprecatedAnnotation, ModuleDescriptor module) {
        Intrinsics.checkNotNullParameter(this_createDeprecatedAnnotation, "$this_createDeprecatedAnnotation");
        Intrinsics.checkNotNullParameter(module, "module");
        SimpleType arrayType = module.getBuiltIns().getArrayType(Variance.INVARIANT, this_createDeprecatedAnnotation.getStringType());
        Intrinsics.checkNotNullExpressionValue(arrayType, "getArrayType(...)");
        return arrayType;
    }

    static {
        Name nameIdentifier = Name.identifier("message");
        Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
        DEPRECATED_MESSAGE_NAME = nameIdentifier;
        Name nameIdentifier2 = Name.identifier("replaceWith");
        Intrinsics.checkNotNullExpressionValue(nameIdentifier2, "identifier(...)");
        DEPRECATED_REPLACE_WITH_NAME = nameIdentifier2;
        Name nameIdentifier3 = Name.identifier("level");
        Intrinsics.checkNotNullExpressionValue(nameIdentifier3, "identifier(...)");
        DEPRECATED_LEVEL_NAME = nameIdentifier3;
        Name nameIdentifier4 = Name.identifier("expression");
        Intrinsics.checkNotNullExpressionValue(nameIdentifier4, "identifier(...)");
        REPLACE_WITH_EXPRESSION_NAME = nameIdentifier4;
        Name nameIdentifier5 = Name.identifier("imports");
        Intrinsics.checkNotNullExpressionValue(nameIdentifier5, "identifier(...)");
        REPLACE_WITH_IMPORTS_NAME = nameIdentifier5;
    }
}
