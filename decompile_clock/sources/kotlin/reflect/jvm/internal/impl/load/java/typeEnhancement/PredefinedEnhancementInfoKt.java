package kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement;

import java.util.Map;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.load.kotlin.SignatureBuildingComponents;
import kotlin.reflect.jvm.internal.impl.resolve.jvm.JvmPrimitiveType;

/* JADX INFO: compiled from: predefinedEnhancementInfo.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class PredefinedEnhancementInfoKt {
    private static final Map<String, PredefinedFunctionEnhancementInfo> PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE;
    private static final JavaTypeQualifiers NULLABLE = new JavaTypeQualifiers(NullabilityQualifier.NULLABLE, null, false, false, 8, null);
    private static final JavaTypeQualifiers NOT_PLATFORM = new JavaTypeQualifiers(NullabilityQualifier.NOT_NULL, null, false, false, 8, null);
    private static final JavaTypeQualifiers NOT_NULLABLE = new JavaTypeQualifiers(NullabilityQualifier.NOT_NULL, null, true, false, 8, null);

    static {
        final SignatureBuildingComponents signatureBuildingComponents = SignatureBuildingComponents.INSTANCE;
        final String strJavaLang = signatureBuildingComponents.javaLang("Object");
        final String strJavaFunction = signatureBuildingComponents.javaFunction("Predicate");
        final String strJavaFunction2 = signatureBuildingComponents.javaFunction("Function");
        final String strJavaFunction3 = signatureBuildingComponents.javaFunction("Consumer");
        final String strJavaFunction4 = signatureBuildingComponents.javaFunction("BiFunction");
        final String strJavaFunction5 = signatureBuildingComponents.javaFunction("BiConsumer");
        final String strJavaFunction6 = signatureBuildingComponents.javaFunction("UnaryOperator");
        final String strJavaUtil = signatureBuildingComponents.javaUtil("stream/Stream");
        final String strJavaUtil2 = signatureBuildingComponents.javaUtil("Optional");
        SignatureEnhancementBuilder signatureEnhancementBuilder = new SignatureEnhancementBuilder();
        new SignatureEnhancementBuilder.ClassEnhancementBuilder(signatureEnhancementBuilder, signatureBuildingComponents.javaUtil("Iterator")).function("forEachRemaining", new Function1(strJavaFunction3) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$0
            private final String arg$0;

            {
                this.arg$0 = strJavaFunction3;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$1$lambda$0(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        new SignatureEnhancementBuilder.ClassEnhancementBuilder(signatureEnhancementBuilder, signatureBuildingComponents.javaLang("Iterable")).function("spliterator", new Function1(signatureBuildingComponents) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$1
            private final SignatureBuildingComponents arg$0;

            {
                this.arg$0 = signatureBuildingComponents;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$3$lambda$2(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        SignatureEnhancementBuilder.ClassEnhancementBuilder classEnhancementBuilder = new SignatureEnhancementBuilder.ClassEnhancementBuilder(signatureEnhancementBuilder, signatureBuildingComponents.javaUtil("Collection"));
        classEnhancementBuilder.function("removeIf", new Function1(strJavaFunction) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$2
            private final String arg$0;

            {
                this.arg$0 = strJavaFunction;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$7$lambda$4(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        classEnhancementBuilder.function("stream", new Function1(strJavaUtil) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$3
            private final String arg$0;

            {
                this.arg$0 = strJavaUtil;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$7$lambda$5(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        classEnhancementBuilder.function("parallelStream", new Function1(strJavaUtil) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$4
            private final String arg$0;

            {
                this.arg$0 = strJavaUtil;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$7$lambda$6(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        new SignatureEnhancementBuilder.ClassEnhancementBuilder(signatureEnhancementBuilder, signatureBuildingComponents.javaUtil("List")).function("replaceAll", new Function1(strJavaFunction6) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$5
            private final String arg$0;

            {
                this.arg$0 = strJavaFunction6;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$9$lambda$8(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        SignatureEnhancementBuilder.ClassEnhancementBuilder classEnhancementBuilder2 = new SignatureEnhancementBuilder.ClassEnhancementBuilder(signatureEnhancementBuilder, signatureBuildingComponents.javaUtil("Map"));
        classEnhancementBuilder2.function("forEach", new Function1(strJavaFunction5) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$6
            private final String arg$0;

            {
                this.arg$0 = strJavaFunction5;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$10(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        classEnhancementBuilder2.function("putIfAbsent", new Function1(strJavaLang) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$7
            private final String arg$0;

            {
                this.arg$0 = strJavaLang;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$11(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        classEnhancementBuilder2.function("replace", new Function1(strJavaLang) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$8
            private final String arg$0;

            {
                this.arg$0 = strJavaLang;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$12(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        classEnhancementBuilder2.function("replace", new Function1(strJavaLang) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$9
            private final String arg$0;

            {
                this.arg$0 = strJavaLang;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$13(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        classEnhancementBuilder2.function("replaceAll", new Function1(strJavaFunction4) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$10
            private final String arg$0;

            {
                this.arg$0 = strJavaFunction4;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$14(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        classEnhancementBuilder2.function("compute", new Function1(strJavaLang, strJavaFunction4) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$11
            private final String arg$0;
            private final String arg$1;

            {
                this.arg$0 = strJavaLang;
                this.arg$1 = strJavaFunction4;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$15(this.arg$0, this.arg$1, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        classEnhancementBuilder2.function("computeIfAbsent", new Function1(strJavaLang, strJavaFunction2) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$12
            private final String arg$0;
            private final String arg$1;

            {
                this.arg$0 = strJavaLang;
                this.arg$1 = strJavaFunction2;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$16(this.arg$0, this.arg$1, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        classEnhancementBuilder2.function("computeIfPresent", new Function1(strJavaLang, strJavaFunction4) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$13
            private final String arg$0;
            private final String arg$1;

            {
                this.arg$0 = strJavaLang;
                this.arg$1 = strJavaFunction4;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$17(this.arg$0, this.arg$1, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        classEnhancementBuilder2.function("merge", new Function1(strJavaLang, strJavaFunction4) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$14
            private final String arg$0;
            private final String arg$1;

            {
                this.arg$0 = strJavaLang;
                this.arg$1 = strJavaFunction4;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$18(this.arg$0, this.arg$1, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        SignatureEnhancementBuilder.ClassEnhancementBuilder classEnhancementBuilder3 = new SignatureEnhancementBuilder.ClassEnhancementBuilder(signatureEnhancementBuilder, strJavaUtil2);
        classEnhancementBuilder3.function("empty", new Function1(strJavaUtil2) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$15
            private final String arg$0;

            {
                this.arg$0 = strJavaUtil2;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$25$lambda$20(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        classEnhancementBuilder3.function("of", new Function1(strJavaLang, strJavaUtil2) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$16
            private final String arg$0;
            private final String arg$1;

            {
                this.arg$0 = strJavaLang;
                this.arg$1 = strJavaUtil2;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$25$lambda$21(this.arg$0, this.arg$1, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        classEnhancementBuilder3.function("ofNullable", new Function1(strJavaLang, strJavaUtil2) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$17
            private final String arg$0;
            private final String arg$1;

            {
                this.arg$0 = strJavaLang;
                this.arg$1 = strJavaUtil2;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$25$lambda$22(this.arg$0, this.arg$1, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        classEnhancementBuilder3.function("get", new Function1(strJavaLang) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$18
            private final String arg$0;

            {
                this.arg$0 = strJavaLang;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$25$lambda$23(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        classEnhancementBuilder3.function("ifPresent", new Function1(strJavaFunction3) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$19
            private final String arg$0;

            {
                this.arg$0 = strJavaFunction3;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$25$lambda$24(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        new SignatureEnhancementBuilder.ClassEnhancementBuilder(signatureEnhancementBuilder, signatureBuildingComponents.javaLang("ref/Reference")).function("get", new Function1(strJavaLang) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$20
            private final String arg$0;

            {
                this.arg$0 = strJavaLang;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$27$lambda$26(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        new SignatureEnhancementBuilder.ClassEnhancementBuilder(signatureEnhancementBuilder, strJavaFunction).function("test", new Function1(strJavaLang) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$21
            private final String arg$0;

            {
                this.arg$0 = strJavaLang;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$29$lambda$28(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        new SignatureEnhancementBuilder.ClassEnhancementBuilder(signatureEnhancementBuilder, signatureBuildingComponents.javaFunction("BiPredicate")).function("test", new Function1(strJavaLang) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$22
            private final String arg$0;

            {
                this.arg$0 = strJavaLang;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$31$lambda$30(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        new SignatureEnhancementBuilder.ClassEnhancementBuilder(signatureEnhancementBuilder, strJavaFunction3).function("accept", new Function1(strJavaLang) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$23
            private final String arg$0;

            {
                this.arg$0 = strJavaLang;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$33$lambda$32(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        new SignatureEnhancementBuilder.ClassEnhancementBuilder(signatureEnhancementBuilder, strJavaFunction5).function("accept", new Function1(strJavaLang) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$24
            private final String arg$0;

            {
                this.arg$0 = strJavaLang;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$35$lambda$34(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        new SignatureEnhancementBuilder.ClassEnhancementBuilder(signatureEnhancementBuilder, strJavaFunction2).function("apply", new Function1(strJavaLang) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$25
            private final String arg$0;

            {
                this.arg$0 = strJavaLang;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$37$lambda$36(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        new SignatureEnhancementBuilder.ClassEnhancementBuilder(signatureEnhancementBuilder, strJavaFunction4).function("apply", new Function1(strJavaLang) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$26
            private final String arg$0;

            {
                this.arg$0 = strJavaLang;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$39$lambda$38(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        new SignatureEnhancementBuilder.ClassEnhancementBuilder(signatureEnhancementBuilder, signatureBuildingComponents.javaFunction("Supplier")).function("get", new Function1(strJavaLang) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.PredefinedEnhancementInfoKt$$Lambda$27
            private final String arg$0;

            {
                this.arg$0 = strJavaLang;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return PredefinedEnhancementInfoKt.PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$41$lambda$40(this.arg$0, (SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder) obj);
            }
        });
        PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE = signatureEnhancementBuilder.build();
    }

    public static final Map<String, PredefinedFunctionEnhancementInfo> getPREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE() {
        return PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$1$lambda$0(String JFConsumer, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JFConsumer, "$JFConsumer");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JFConsumer, javaTypeQualifiers, javaTypeQualifiers);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$3$lambda$2(SignatureBuildingComponents this_signatures, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(this_signatures, "$this_signatures");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        String strJavaUtil = this_signatures.javaUtil("Spliterator");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.returns(strJavaUtil, javaTypeQualifiers, javaTypeQualifiers);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$7$lambda$4(String JFPredicate, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JFPredicate, "$JFPredicate");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JFPredicate, javaTypeQualifiers, javaTypeQualifiers);
        function.returns(JvmPrimitiveType.BOOLEAN);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$7$lambda$5(String JUStream, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JUStream, "$JUStream");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.returns(JUStream, javaTypeQualifiers, javaTypeQualifiers);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$7$lambda$6(String JUStream, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JUStream, "$JUStream");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.returns(JUStream, javaTypeQualifiers, javaTypeQualifiers);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$9$lambda$8(String JFUnaryOperator, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JFUnaryOperator, "$JFUnaryOperator");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JFUnaryOperator, javaTypeQualifiers, javaTypeQualifiers);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$10(String JFBiConsumer, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JFBiConsumer, "$JFBiConsumer");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JFBiConsumer, javaTypeQualifiers, javaTypeQualifiers, javaTypeQualifiers);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$11(String JLObject, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JLObject, javaTypeQualifiers);
        function.parameter(JLObject, javaTypeQualifiers);
        function.returns(JLObject, NULLABLE);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$12(String JLObject, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JLObject, javaTypeQualifiers);
        function.parameter(JLObject, javaTypeQualifiers);
        function.returns(JLObject, NULLABLE);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$13(String JLObject, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JLObject, javaTypeQualifiers);
        function.parameter(JLObject, javaTypeQualifiers);
        function.parameter(JLObject, javaTypeQualifiers);
        function.returns(JvmPrimitiveType.BOOLEAN);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$14(String JFBiFunction, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JFBiFunction, "$JFBiFunction");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JFBiFunction, javaTypeQualifiers, javaTypeQualifiers, javaTypeQualifiers, javaTypeQualifiers);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$15(String JLObject, String JFBiFunction, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(JFBiFunction, "$JFBiFunction");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JLObject, javaTypeQualifiers);
        JavaTypeQualifiers javaTypeQualifiers2 = NULLABLE;
        function.parameter(JFBiFunction, javaTypeQualifiers, javaTypeQualifiers, javaTypeQualifiers2, javaTypeQualifiers2);
        function.returns(JLObject, javaTypeQualifiers2);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$16(String JLObject, String JFFunction, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(JFFunction, "$JFFunction");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JLObject, javaTypeQualifiers);
        function.parameter(JFFunction, javaTypeQualifiers, javaTypeQualifiers, javaTypeQualifiers);
        function.returns(JLObject, javaTypeQualifiers);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$17(String JLObject, String JFBiFunction, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(JFBiFunction, "$JFBiFunction");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JLObject, javaTypeQualifiers);
        JavaTypeQualifiers javaTypeQualifiers2 = NULLABLE;
        function.parameter(JFBiFunction, javaTypeQualifiers, javaTypeQualifiers, NOT_NULLABLE, javaTypeQualifiers2);
        function.returns(JLObject, javaTypeQualifiers2);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$19$lambda$18(String JLObject, String JFBiFunction, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(JFBiFunction, "$JFBiFunction");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JLObject, javaTypeQualifiers);
        JavaTypeQualifiers javaTypeQualifiers2 = NOT_NULLABLE;
        function.parameter(JLObject, javaTypeQualifiers2);
        JavaTypeQualifiers javaTypeQualifiers3 = NULLABLE;
        function.parameter(JFBiFunction, javaTypeQualifiers, javaTypeQualifiers2, javaTypeQualifiers2, javaTypeQualifiers3);
        function.returns(JLObject, javaTypeQualifiers3);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$25$lambda$20(String JUOptional, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JUOptional, "$JUOptional");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        function.returns(JUOptional, NOT_PLATFORM, NOT_NULLABLE);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$25$lambda$21(String JLObject, String JUOptional, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(JUOptional, "$JUOptional");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_NULLABLE;
        function.parameter(JLObject, javaTypeQualifiers);
        function.returns(JUOptional, NOT_PLATFORM, javaTypeQualifiers);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$25$lambda$22(String JLObject, String JUOptional, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(JUOptional, "$JUOptional");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        function.parameter(JLObject, NULLABLE);
        function.returns(JUOptional, NOT_PLATFORM, NOT_NULLABLE);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$25$lambda$23(String JLObject, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        function.returns(JLObject, NOT_NULLABLE);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$25$lambda$24(String JFConsumer, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JFConsumer, "$JFConsumer");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        function.parameter(JFConsumer, NOT_PLATFORM, NOT_NULLABLE);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$27$lambda$26(String JLObject, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        function.returns(JLObject, NULLABLE);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$29$lambda$28(String JLObject, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        function.parameter(JLObject, NOT_PLATFORM);
        function.returns(JvmPrimitiveType.BOOLEAN);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$31$lambda$30(String JLObject, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JLObject, javaTypeQualifiers);
        function.parameter(JLObject, javaTypeQualifiers);
        function.returns(JvmPrimitiveType.BOOLEAN);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$33$lambda$32(String JLObject, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        function.parameter(JLObject, NOT_PLATFORM);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$35$lambda$34(String JLObject, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JLObject, javaTypeQualifiers);
        function.parameter(JLObject, javaTypeQualifiers);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$37$lambda$36(String JLObject, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JLObject, javaTypeQualifiers);
        function.returns(JLObject, javaTypeQualifiers);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$39$lambda$38(String JLObject, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        JavaTypeQualifiers javaTypeQualifiers = NOT_PLATFORM;
        function.parameter(JLObject, javaTypeQualifiers);
        function.parameter(JLObject, javaTypeQualifiers);
        function.returns(JLObject, javaTypeQualifiers);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PREDEFINED_FUNCTION_ENHANCEMENT_INFO_BY_SIGNATURE$lambda$43$lambda$42$lambda$41$lambda$40(String JLObject, SignatureEnhancementBuilder.ClassEnhancementBuilder.FunctionEnhancementBuilder function) {
        Intrinsics.checkNotNullParameter(JLObject, "$JLObject");
        Intrinsics.checkNotNullParameter(function, "$this$function");
        function.returns(JLObject, NOT_PLATFORM);
        return Unit.INSTANCE;
    }
}
