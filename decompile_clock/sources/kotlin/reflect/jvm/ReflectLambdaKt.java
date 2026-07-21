package kotlin.reflect.jvm;

import kotlin.Function;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KFunction;
import kotlin.reflect.jvm.internal.EmptyContainerForLocal;
import kotlin.reflect.jvm.internal.KFunctionImpl;
import kotlin.reflect.jvm.internal.UtilKt;
import kotlin.reflect.jvm.internal.impl.descriptors.SimpleFunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.TypeTable;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization.JvmMetadataVersion;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization.JvmNameResolver;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization.JvmProtoBufUtil;

/* JADX INFO: compiled from: reflectLambda.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a \u0010\u0000\u001a\n\u0012\u0004\u0012\u0002H\u0002\u0018\u00010\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0003H\u0007¨\u0006\u0004"}, d2 = {"reflect", "Lkotlin/reflect/KFunction;", "R", "Lkotlin/Function;", "kotlin-reflection"}, k = 2, mv = {2, 0, 0}, xi = 48)
public final class ReflectLambdaKt {
    public static final <R> KFunction<R> reflect(Function<? extends R> function) {
        Intrinsics.checkNotNullParameter(function, "<this>");
        Metadata metadata = (Metadata) function.getClass().getAnnotation(Metadata.class);
        if (metadata == null) {
            return null;
        }
        String[] strArrD1 = metadata.d1();
        if (strArrD1.length == 0) {
            strArrD1 = null;
        }
        if (strArrD1 == null) {
            return null;
        }
        Pair<JvmNameResolver, ProtoBuf.Function> functionDataFrom = JvmProtoBufUtil.readFunctionDataFrom(strArrD1, metadata.d2());
        JvmNameResolver jvmNameResolverComponent1 = functionDataFrom.component1();
        ProtoBuf.Function functionComponent2 = functionDataFrom.component2();
        JvmMetadataVersion jvmMetadataVersion = new JvmMetadataVersion(metadata.mv(), (metadata.xi() & 8) != 0);
        ProtoBuf.TypeTable typeTable = functionComponent2.getTypeTable();
        Intrinsics.checkNotNullExpressionValue(typeTable, "getTypeTable(...)");
        return new KFunctionImpl(EmptyContainerForLocal.INSTANCE, (SimpleFunctionDescriptor) UtilKt.deserializeToDescriptor(function.getClass(), functionComponent2, jvmNameResolverComponent1, new TypeTable(typeTable), jvmMetadataVersion, ReflectLambdaKt$reflect$descriptor$1.INSTANCE));
    }
}
