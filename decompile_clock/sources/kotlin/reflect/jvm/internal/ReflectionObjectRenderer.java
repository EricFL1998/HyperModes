package kotlin.reflect.jvm.internal;

import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KParameter;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ReceiverParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ValueParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRenderer;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;

/* JADX INFO: compiled from: ReflectionObjectRenderer.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001f\u0010\u0006\u001a\u00020\u0007*\u00060\tj\u0002`\b2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0002¢\u0006\u0002\u0010\fJ\u001d\u0010\r\u001a\u00020\u0007*\u00060\tj\u0002`\b2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002¢\u0006\u0002\u0010\u0010J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u000fH\u0002J\u000e\u0010\u0014\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0015J\u000e\u0010\u0016\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0017J\u000e\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0019\u001a\u00020\u0017J\u000e\u0010\u001a\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u001cJ\u000e\u0010\u001d\u001a\u00020\u00122\u0006\u0010\u001e\u001a\u00020\u001fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006 "}, d2 = {"Lkotlin/reflect/jvm/internal/ReflectionObjectRenderer;", "", "<init>", "()V", "renderer", "Lkotlin/reflect/jvm/internal/impl/renderer/DescriptorRenderer;", "appendReceiverType", "", "Lkotlin/text/StringBuilder;", "Ljava/lang/StringBuilder;", "receiver", "Lkotlin/reflect/jvm/internal/impl/descriptors/ReceiverParameterDescriptor;", "(Ljava/lang/StringBuilder;Lorg/jetbrains/kotlin/descriptors/ReceiverParameterDescriptor;)V", "appendReceivers", "callable", "Lkotlin/reflect/jvm/internal/impl/descriptors/CallableDescriptor;", "(Ljava/lang/StringBuilder;Lorg/jetbrains/kotlin/descriptors/CallableDescriptor;)V", "renderCallable", "", "descriptor", "renderProperty", "Lkotlin/reflect/jvm/internal/impl/descriptors/PropertyDescriptor;", "renderFunction", "Lkotlin/reflect/jvm/internal/impl/descriptors/FunctionDescriptor;", "renderLambda", "invoke", "renderParameter", "parameter", "Lkotlin/reflect/jvm/internal/KParameterImpl;", "renderType", "type", "Lkotlin/reflect/jvm/internal/impl/types/KotlinType;", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class ReflectionObjectRenderer {
    public static final ReflectionObjectRenderer INSTANCE = new ReflectionObjectRenderer();
    private static final DescriptorRenderer renderer = DescriptorRenderer.FQ_NAMES_IN_TYPES;

    /* JADX INFO: compiled from: ReflectionObjectRenderer.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[KParameter.Kind.values().length];
            try {
                iArr[KParameter.Kind.EXTENSION_RECEIVER.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[KParameter.Kind.INSTANCE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[KParameter.Kind.VALUE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    private ReflectionObjectRenderer() {
    }

    private final void appendReceiverType(StringBuilder sb, ReceiverParameterDescriptor receiverParameterDescriptor) {
        if (receiverParameterDescriptor != null) {
            KotlinType type = receiverParameterDescriptor.getType();
            Intrinsics.checkNotNullExpressionValue(type, "getType(...)");
            sb.append(renderType(type));
            sb.append(".");
        }
    }

    private final void appendReceivers(StringBuilder sb, CallableDescriptor callableDescriptor) {
        ReceiverParameterDescriptor instanceReceiverParameter = UtilKt.getInstanceReceiverParameter(callableDescriptor);
        ReceiverParameterDescriptor extensionReceiverParameter = callableDescriptor.getExtensionReceiverParameter();
        appendReceiverType(sb, instanceReceiverParameter);
        boolean z = (instanceReceiverParameter == null || extensionReceiverParameter == null) ? false : true;
        if (z) {
            sb.append("(");
        }
        appendReceiverType(sb, extensionReceiverParameter);
        if (z) {
            sb.append(")");
        }
    }

    private final String renderCallable(CallableDescriptor descriptor) {
        if (descriptor instanceof PropertyDescriptor) {
            return renderProperty((PropertyDescriptor) descriptor);
        }
        if (descriptor instanceof FunctionDescriptor) {
            return renderFunction((FunctionDescriptor) descriptor);
        }
        throw new IllegalStateException(("Illegal callable: " + descriptor).toString());
    }

    public final String renderProperty(PropertyDescriptor descriptor) {
        Intrinsics.checkNotNullParameter(descriptor, "descriptor");
        StringBuilder sb = new StringBuilder();
        sb.append(descriptor.isVar() ? "var " : "val ");
        ReflectionObjectRenderer reflectionObjectRenderer = INSTANCE;
        reflectionObjectRenderer.appendReceivers(sb, descriptor);
        DescriptorRenderer descriptorRenderer = renderer;
        Name name = descriptor.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        sb.append(descriptorRenderer.renderName(name, true));
        sb.append(": ");
        KotlinType type = descriptor.getType();
        Intrinsics.checkNotNullExpressionValue(type, "getType(...)");
        sb.append(reflectionObjectRenderer.renderType(type));
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    public final String renderFunction(FunctionDescriptor descriptor) {
        Intrinsics.checkNotNullParameter(descriptor, "descriptor");
        StringBuilder sb = new StringBuilder();
        sb.append("fun ");
        ReflectionObjectRenderer reflectionObjectRenderer = INSTANCE;
        reflectionObjectRenderer.appendReceivers(sb, descriptor);
        DescriptorRenderer descriptorRenderer = renderer;
        Name name = descriptor.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        sb.append(descriptorRenderer.renderName(name, true));
        List<ValueParameterDescriptor> valueParameters = descriptor.getValueParameters();
        Intrinsics.checkNotNullExpressionValue(valueParameters, "getValueParameters(...)");
        CollectionsKt.joinTo$default(valueParameters, sb, ", ", "(", ")", 0, null, new Function1() { // from class: kotlin.reflect.jvm.internal.ReflectionObjectRenderer$$Lambda$0
            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return ReflectionObjectRenderer.renderFunction$lambda$2$lambda$1((ValueParameterDescriptor) obj);
            }
        }, 48, null);
        sb.append(": ");
        KotlinType returnType = descriptor.getReturnType();
        Intrinsics.checkNotNull(returnType);
        sb.append(reflectionObjectRenderer.renderType(returnType));
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence renderFunction$lambda$2$lambda$1(ValueParameterDescriptor valueParameterDescriptor) {
        ReflectionObjectRenderer reflectionObjectRenderer = INSTANCE;
        KotlinType type = valueParameterDescriptor.getType();
        Intrinsics.checkNotNullExpressionValue(type, "getType(...)");
        return reflectionObjectRenderer.renderType(type);
    }

    public final String renderLambda(FunctionDescriptor invoke) {
        Intrinsics.checkNotNullParameter(invoke, "invoke");
        StringBuilder sb = new StringBuilder();
        ReflectionObjectRenderer reflectionObjectRenderer = INSTANCE;
        reflectionObjectRenderer.appendReceivers(sb, invoke);
        List<ValueParameterDescriptor> valueParameters = invoke.getValueParameters();
        Intrinsics.checkNotNullExpressionValue(valueParameters, "getValueParameters(...)");
        CollectionsKt.joinTo$default(valueParameters, sb, ", ", "(", ")", 0, null, new Function1() { // from class: kotlin.reflect.jvm.internal.ReflectionObjectRenderer$$Lambda$1
            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return ReflectionObjectRenderer.renderLambda$lambda$4$lambda$3((ValueParameterDescriptor) obj);
            }
        }, 48, null);
        sb.append(" -> ");
        KotlinType returnType = invoke.getReturnType();
        Intrinsics.checkNotNull(returnType);
        sb.append(reflectionObjectRenderer.renderType(returnType));
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence renderLambda$lambda$4$lambda$3(ValueParameterDescriptor valueParameterDescriptor) {
        ReflectionObjectRenderer reflectionObjectRenderer = INSTANCE;
        KotlinType type = valueParameterDescriptor.getType();
        Intrinsics.checkNotNullExpressionValue(type, "getType(...)");
        return reflectionObjectRenderer.renderType(type);
    }

    public final String renderParameter(KParameterImpl parameter) {
        Intrinsics.checkNotNullParameter(parameter, "parameter");
        StringBuilder sb = new StringBuilder();
        int i = WhenMappings.$EnumSwitchMapping$0[parameter.getKind().ordinal()];
        if (i == 1) {
            sb.append("extension receiver parameter");
        } else if (i == 2) {
            sb.append("instance parameter");
        } else {
            if (i != 3) {
                throw new NoWhenBranchMatchedException();
            }
            sb.append("parameter #" + parameter.getIndex() + ' ' + parameter.getName());
        }
        sb.append(" of ");
        sb.append(INSTANCE.renderCallable(parameter.getCallable().getDescriptor()));
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    public final String renderType(KotlinType type) {
        Intrinsics.checkNotNullParameter(type, "type");
        return renderer.renderType(type);
    }
}
