package kotlin.reflect.jvm.internal;

import androidx.core.app.NotificationCompat;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.coroutines.Continuation;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KCallable;
import kotlin.reflect.KClass;
import kotlin.reflect.KParameter;
import kotlin.reflect.KType;
import kotlin.reflect.KTypeParameter;
import kotlin.reflect.KVisibility;
import kotlin.reflect.full.IllegalCallableAccessException;
import kotlin.reflect.jvm.KTypesJvm;
import kotlin.reflect.jvm.ReflectJvmMapping;
import kotlin.reflect.jvm.internal.calls.Caller;
import kotlin.reflect.jvm.internal.calls.ValueClassAwareCallerKt;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibility;
import kotlin.reflect.jvm.internal.impl.descriptors.Modality;
import kotlin.reflect.jvm.internal.impl.descriptors.ParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ReceiverParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ValueParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.JavaCallableMemberDescriptor;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.TypeSubstitutionKt;

/* JADX INFO: compiled from: KCallableImpl.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000¢\u0001\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0010\u001b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\b \u0018\u0000*\u0006\b\u0000\u0010\u0001 \u00012\b\u0012\u0004\u0012\u0002H\u00010\u00022\u00020\u0003B\u0007¢\u0006\u0004\b\u0004\u0010\u0005J%\u00107\u001a\u00028\u00002\u0016\u00108\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010:09\"\u0004\u0018\u00010:H\u0016¢\u0006\u0002\u0010;J#\u0010<\u001a\u00028\u00002\u0014\u00108\u001a\u0010\u0012\u0004\u0012\u00020!\u0012\u0006\u0012\u0004\u0018\u00010:0=H\u0016¢\u0006\u0002\u0010>J\u0015\u0010@\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010:09H\u0002¢\u0006\u0002\u0010AJ3\u0010B\u001a\u00028\u00002\u0014\u00108\u001a\u0010\u0012\u0004\u0012\u00020!\u0012\u0006\u0012\u0004\u0018\u00010:0=2\f\u0010C\u001a\b\u0012\u0002\b\u0003\u0018\u00010DH\u0000¢\u0006\u0004\bE\u0010FJ\u0010\u0010I\u001a\u00020J2\u0006\u0010K\u001a\u00020!H\u0002J#\u0010L\u001a\u00028\u00002\u0014\u00108\u001a\u0010\u0012\u0004\u0012\u00020!\u0012\u0006\u0012\u0004\u0018\u00010:0=H\u0002¢\u0006\u0002\u0010>J\u0010\u0010M\u001a\u00020:2\u0006\u0010N\u001a\u00020'H\u0002J\n\u0010O\u001a\u0004\u0018\u00010PH\u0002R\u0012\u0010\u0006\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\tR\u0016\u0010\n\u001a\u0006\u0012\u0002\b\u00030\u000bX¦\u0004¢\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0018\u0010\u000e\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u000bX¦\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\rR\u0012\u0010\u0010\u001a\u00020\u0011X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013R\u0012\u0010\u0014\u001a\u00020\u0015X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u0016R(\u0010\u0017\u001a\u001c\u0012\u0018\u0012\u0016\u0012\u0004\u0012\u00020\u001a \u001b*\n\u0012\u0004\u0012\u00020\u001a\u0018\u00010\u00190\u00190\u0018X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001a0\u00198VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u001eR(\u0010\u001f\u001a\u001c\u0012\u0018\u0012\u0016\u0012\u0004\u0012\u00020! \u001b*\n\u0012\u0004\u0012\u00020!\u0018\u00010 0 0\u0018X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\"\u001a\b\u0012\u0004\u0012\u00020!0\u00198VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b#\u0010\u001eR\u001c\u0010$\u001a\u0010\u0012\f\u0012\n \u001b*\u0004\u0018\u00010%0%0\u0018X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010&\u001a\u00020'8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b(\u0010)R(\u0010*\u001a\u001c\u0012\u0018\u0012\u0016\u0012\u0004\u0012\u00020+ \u001b*\n\u0012\u0004\u0012\u00020+\u0018\u00010\u00190\u00190\u0018X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010,\u001a\b\u0012\u0004\u0012\u00020-0\u00198VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b.\u0010\u001eR\u0016\u0010/\u001a\u0004\u0018\u0001008VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b1\u00102R\u0014\u00103\u001a\u00020\u00158VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b3\u0010\u0016R\u0014\u00104\u001a\u00020\u00158VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b4\u0010\u0016R\u0014\u00105\u001a\u00020\u00158VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b5\u0010\u0016R\u0014\u00106\u001a\u00020\u00158DX\u0084\u0004¢\u0006\u0006\u001a\u0004\b6\u0010\u0016R,\u0010?\u001a \u0012\u001c\u0012\u001a\u0012\u0006\u0012\u0004\u0018\u00010: \u001b*\f\u0012\u0006\u0012\u0004\u0018\u00010:\u0018\u000109090\u0018X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010G\u001a\b\u0012\u0004\u0012\u00020\u00150HX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006Q"}, d2 = {"Lkotlin/reflect/jvm/internal/KCallableImpl;", "R", "Lkotlin/reflect/KCallable;", "Lkotlin/reflect/jvm/internal/KTypeParameterOwnerImpl;", "<init>", "()V", "descriptor", "Lkotlin/reflect/jvm/internal/impl/descriptors/CallableMemberDescriptor;", "getDescriptor", "()Lorg/jetbrains/kotlin/descriptors/CallableMemberDescriptor;", "caller", "Lkotlin/reflect/jvm/internal/calls/Caller;", "getCaller", "()Lkotlin/reflect/jvm/internal/calls/Caller;", "defaultCaller", "getDefaultCaller", "container", "Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl;", "getContainer", "()Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl;", "isBound", "", "()Z", "_annotations", "Lkotlin/reflect/jvm/internal/ReflectProperties$LazySoftVal;", "", "", "kotlin.jvm.PlatformType", "annotations", "getAnnotations", "()Ljava/util/List;", "_parameters", "Ljava/util/ArrayList;", "Lkotlin/reflect/KParameter;", "parameters", "getParameters", "_returnType", "Lkotlin/reflect/jvm/internal/KTypeImpl;", "returnType", "Lkotlin/reflect/KType;", "getReturnType", "()Lkotlin/reflect/KType;", "_typeParameters", "Lkotlin/reflect/jvm/internal/KTypeParameterImpl;", "typeParameters", "Lkotlin/reflect/KTypeParameter;", "getTypeParameters", "visibility", "Lkotlin/reflect/KVisibility;", "getVisibility", "()Lkotlin/reflect/KVisibility;", "isFinal", "isOpen", "isAbstract", "isAnnotationConstructor", NotificationCompat.CATEGORY_CALL, "args", "", "", "([Ljava/lang/Object;)Ljava/lang/Object;", "callBy", "", "(Ljava/util/Map;)Ljava/lang/Object;", "_absentArguments", "getAbsentArguments", "()[Ljava/lang/Object;", "callDefaultMethod", "continuationArgument", "Lkotlin/coroutines/Continuation;", "callDefaultMethod$kotlin_reflection", "(Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "parametersNeedMFVCFlattening", "Lkotlin/Lazy;", "getParameterTypeSize", "", "parameter", "callAnnotationConstructor", "defaultEmptyArray", "type", "extractContinuationArgument", "Ljava/lang/reflect/Type;", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
public abstract class KCallableImpl<R> implements KCallable<R>, KTypeParameterOwnerImpl {
    private final ReflectProperties.LazySoftVal<Object[]> _absentArguments;
    private final ReflectProperties.LazySoftVal<List<Annotation>> _annotations;
    private final ReflectProperties.LazySoftVal<ArrayList<KParameter>> _parameters;
    private final ReflectProperties.LazySoftVal<KTypeImpl> _returnType;
    private final ReflectProperties.LazySoftVal<List<KTypeParameterImpl>> _typeParameters;
    private final Lazy<Boolean> parametersNeedMFVCFlattening;

    public abstract Caller<?> getCaller();

    public abstract KDeclarationContainerImpl getContainer();

    public abstract Caller<?> getDefaultCaller();

    public abstract CallableMemberDescriptor getDescriptor();

    public abstract boolean isBound();

    public KCallableImpl() {
        ReflectProperties.LazySoftVal<List<Annotation>> lazySoftValLazySoft = ReflectProperties.lazySoft(new Function0(this) { // from class: kotlin.reflect.jvm.internal.KCallableImpl$$Lambda$0
            private final KCallableImpl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return KCallableImpl._annotations$lambda$0(this.arg$0);
            }
        });
        Intrinsics.checkNotNullExpressionValue(lazySoftValLazySoft, "lazySoft(...)");
        this._annotations = lazySoftValLazySoft;
        ReflectProperties.LazySoftVal<ArrayList<KParameter>> lazySoftValLazySoft2 = ReflectProperties.lazySoft(new Function0(this) { // from class: kotlin.reflect.jvm.internal.KCallableImpl$$Lambda$1
            private final KCallableImpl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return KCallableImpl._parameters$lambda$5(this.arg$0);
            }
        });
        Intrinsics.checkNotNullExpressionValue(lazySoftValLazySoft2, "lazySoft(...)");
        this._parameters = lazySoftValLazySoft2;
        ReflectProperties.LazySoftVal<KTypeImpl> lazySoftValLazySoft3 = ReflectProperties.lazySoft(new Function0(this) { // from class: kotlin.reflect.jvm.internal.KCallableImpl$$Lambda$2
            private final KCallableImpl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return KCallableImpl._returnType$lambda$7(this.arg$0);
            }
        });
        Intrinsics.checkNotNullExpressionValue(lazySoftValLazySoft3, "lazySoft(...)");
        this._returnType = lazySoftValLazySoft3;
        ReflectProperties.LazySoftVal<List<KTypeParameterImpl>> lazySoftValLazySoft4 = ReflectProperties.lazySoft(new Function0(this) { // from class: kotlin.reflect.jvm.internal.KCallableImpl$$Lambda$3
            private final KCallableImpl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return KCallableImpl._typeParameters$lambda$9(this.arg$0);
            }
        });
        Intrinsics.checkNotNullExpressionValue(lazySoftValLazySoft4, "lazySoft(...)");
        this._typeParameters = lazySoftValLazySoft4;
        ReflectProperties.LazySoftVal<Object[]> lazySoftValLazySoft5 = ReflectProperties.lazySoft(new Function0(this) { // from class: kotlin.reflect.jvm.internal.KCallableImpl$$Lambda$4
            private final KCallableImpl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return KCallableImpl._absentArguments$lambda$14(this.arg$0);
            }
        });
        Intrinsics.checkNotNullExpressionValue(lazySoftValLazySoft5, "lazySoft(...)");
        this._absentArguments = lazySoftValLazySoft5;
        this.parametersNeedMFVCFlattening = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, new Function0(this) { // from class: kotlin.reflect.jvm.internal.KCallableImpl$$Lambda$5
            private final KCallableImpl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return Boolean.valueOf(KCallableImpl.parametersNeedMFVCFlattening$lambda$20(this.arg$0));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final List _annotations$lambda$0(KCallableImpl this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return UtilKt.computeAnnotations(this$0.getDescriptor());
    }

    @Override // kotlin.reflect.KAnnotatedElement
    public List<Annotation> getAnnotations() {
        List<Annotation> listInvoke = this._annotations.invoke();
        Intrinsics.checkNotNullExpressionValue(listInvoke, "invoke(...)");
        return listInvoke;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final ArrayList _parameters$lambda$5(KCallableImpl this$0) {
        int i;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        final CallableMemberDescriptor descriptor = this$0.getDescriptor();
        ArrayList arrayList = new ArrayList();
        final int i2 = 0;
        if (this$0.isBound()) {
            i = 0;
        } else {
            final ReceiverParameterDescriptor instanceReceiverParameter = UtilKt.getInstanceReceiverParameter(descriptor);
            if (instanceReceiverParameter != null) {
                arrayList.add(new KParameterImpl(this$0, 0, KParameter.Kind.INSTANCE, new Function0(instanceReceiverParameter) { // from class: kotlin.reflect.jvm.internal.KCallableImpl$$Lambda$6
                    private final ReceiverParameterDescriptor arg$0;

                    {
                        this.arg$0 = instanceReceiverParameter;
                    }

                    @Override // kotlin.jvm.functions.Function0
                    public Object invoke() {
                        return KCallableImpl._parameters$lambda$5$lambda$1(this.arg$0);
                    }
                }));
                i = 1;
            } else {
                i = 0;
            }
            final ReceiverParameterDescriptor extensionReceiverParameter = descriptor.getExtensionReceiverParameter();
            if (extensionReceiverParameter != null) {
                arrayList.add(new KParameterImpl(this$0, i, KParameter.Kind.EXTENSION_RECEIVER, new Function0(extensionReceiverParameter) { // from class: kotlin.reflect.jvm.internal.KCallableImpl$$Lambda$7
                    private final ReceiverParameterDescriptor arg$0;

                    {
                        this.arg$0 = extensionReceiverParameter;
                    }

                    @Override // kotlin.jvm.functions.Function0
                    public Object invoke() {
                        return KCallableImpl._parameters$lambda$5$lambda$2(this.arg$0);
                    }
                }));
                i++;
            }
        }
        int size = descriptor.getValueParameters().size();
        while (i2 < size) {
            arrayList.add(new KParameterImpl(this$0, i, KParameter.Kind.VALUE, new Function0(descriptor, i2) { // from class: kotlin.reflect.jvm.internal.KCallableImpl$$Lambda$8
                private final CallableMemberDescriptor arg$0;
                private final int arg$1;

                {
                    this.arg$0 = descriptor;
                    this.arg$1 = i2;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return KCallableImpl._parameters$lambda$5$lambda$3(this.arg$0, this.arg$1);
                }
            }));
            i2++;
            i++;
        }
        if (this$0.isAnnotationConstructor() && (descriptor instanceof JavaCallableMemberDescriptor)) {
            ArrayList arrayList2 = arrayList;
            if (arrayList2.size() > 1) {
                CollectionsKt.sortWith(arrayList2, new Comparator() { // from class: kotlin.reflect.jvm.internal.KCallableImpl$_parameters$lambda$5$$inlined$sortBy$1
                    @Override // java.util.Comparator
                    public final int compare(T t, T t2) {
                        return ComparisonsKt.compareValues(((KParameter) t).getName(), ((KParameter) t2).getName());
                    }
                });
            }
        }
        arrayList.trimToSize();
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final ParameterDescriptor _parameters$lambda$5$lambda$1(ReceiverParameterDescriptor receiverParameterDescriptor) {
        return receiverParameterDescriptor;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final ParameterDescriptor _parameters$lambda$5$lambda$2(ReceiverParameterDescriptor receiverParameterDescriptor) {
        return receiverParameterDescriptor;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final ParameterDescriptor _parameters$lambda$5$lambda$3(CallableMemberDescriptor descriptor, int i) {
        Intrinsics.checkNotNullParameter(descriptor, "$descriptor");
        ValueParameterDescriptor valueParameterDescriptor = descriptor.getValueParameters().get(i);
        Intrinsics.checkNotNullExpressionValue(valueParameterDescriptor, "get(...)");
        return valueParameterDescriptor;
    }

    @Override // kotlin.reflect.KCallable
    public List<KParameter> getParameters() {
        ArrayList<KParameter> arrayListInvoke = this._parameters.invoke();
        Intrinsics.checkNotNullExpressionValue(arrayListInvoke, "invoke(...)");
        return arrayListInvoke;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final KTypeImpl _returnType$lambda$7(final KCallableImpl this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        KotlinType returnType = this$0.getDescriptor().getReturnType();
        Intrinsics.checkNotNull(returnType);
        return new KTypeImpl(returnType, new Function0(this$0) { // from class: kotlin.reflect.jvm.internal.KCallableImpl$$Lambda$9
            private final KCallableImpl arg$0;

            {
                this.arg$0 = this$0;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return KCallableImpl._returnType$lambda$7$lambda$6(this.arg$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Type _returnType$lambda$7$lambda$6(KCallableImpl this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Type typeExtractContinuationArgument = this$0.extractContinuationArgument();
        return typeExtractContinuationArgument == null ? this$0.getCaller().getReturnType() : typeExtractContinuationArgument;
    }

    @Override // kotlin.reflect.KCallable
    public KType getReturnType() {
        KTypeImpl kTypeImplInvoke = this._returnType.invoke();
        Intrinsics.checkNotNullExpressionValue(kTypeImplInvoke, "invoke(...)");
        return kTypeImplInvoke;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final List _typeParameters$lambda$9(KCallableImpl this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        List<TypeParameterDescriptor> typeParameters = this$0.getDescriptor().getTypeParameters();
        Intrinsics.checkNotNullExpressionValue(typeParameters, "getTypeParameters(...)");
        List<TypeParameterDescriptor> list = typeParameters;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        for (TypeParameterDescriptor typeParameterDescriptor : list) {
            Intrinsics.checkNotNull(typeParameterDescriptor);
            arrayList.add(new KTypeParameterImpl(this$0, typeParameterDescriptor));
        }
        return arrayList;
    }

    @Override // kotlin.reflect.KCallable
    public List<KTypeParameter> getTypeParameters() {
        List<KTypeParameterImpl> listInvoke = this._typeParameters.invoke();
        Intrinsics.checkNotNullExpressionValue(listInvoke, "invoke(...)");
        return listInvoke;
    }

    @Override // kotlin.reflect.KCallable
    public KVisibility getVisibility() {
        DescriptorVisibility visibility = getDescriptor().getVisibility();
        Intrinsics.checkNotNullExpressionValue(visibility, "getVisibility(...)");
        return UtilKt.toKVisibility(visibility);
    }

    @Override // kotlin.reflect.KCallable
    public boolean isFinal() {
        return getDescriptor().getModality() == Modality.FINAL;
    }

    @Override // kotlin.reflect.KCallable
    public boolean isOpen() {
        return getDescriptor().getModality() == Modality.OPEN;
    }

    @Override // kotlin.reflect.KCallable
    public boolean isAbstract() {
        return getDescriptor().getModality() == Modality.ABSTRACT;
    }

    protected final boolean isAnnotationConstructor() {
        return Intrinsics.areEqual(getName(), "<init>") && getContainer().getJClass().isAnnotation();
    }

    @Override // kotlin.reflect.KCallable
    public R call(Object... args) throws IllegalCallableAccessException {
        Intrinsics.checkNotNullParameter(args, "args");
        try {
            return (R) getCaller().call(args);
        } catch (IllegalAccessException e) {
            throw new IllegalCallableAccessException(e);
        }
    }

    @Override // kotlin.reflect.KCallable
    public R callBy(Map<KParameter, ? extends Object> args) {
        Intrinsics.checkNotNullParameter(args, "args");
        return isAnnotationConstructor() ? callAnnotationConstructor(args) : callDefaultMethod$kotlin_reflection(args, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Object[] _absentArguments$lambda$14(KCallableImpl this$0) {
        int parameterTypeSize;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        List<KParameter> parameters = this$0.getParameters();
        int size = parameters.size() + (this$0.isSuspend() ? 1 : 0);
        if (this$0.parametersNeedMFVCFlattening.getValue().booleanValue()) {
            parameterTypeSize = 0;
            for (KParameter kParameter : parameters) {
                parameterTypeSize += kParameter.getKind() == KParameter.Kind.VALUE ? this$0.getParameterTypeSize(kParameter) : 0;
            }
        } else {
            List<KParameter> list = parameters;
            if ((list instanceof Collection) && list.isEmpty()) {
                parameterTypeSize = 0;
            } else {
                Iterator<T> it = list.iterator();
                parameterTypeSize = 0;
                while (it.hasNext()) {
                    if (((KParameter) it.next()).getKind() == KParameter.Kind.VALUE && (parameterTypeSize = parameterTypeSize + 1) < 0) {
                        CollectionsKt.throwCountOverflow();
                    }
                }
            }
        }
        int i = (parameterTypeSize + 31) / 32;
        Object[] objArr = new Object[size + i + 1];
        for (KParameter kParameter2 : parameters) {
            if (kParameter2.isOptional() && !UtilKt.isInlineClassType(kParameter2.getType())) {
                objArr[kParameter2.getIndex()] = UtilKt.defaultPrimitiveValue(ReflectJvmMapping.getJavaType(kParameter2.getType()));
            } else if (kParameter2.isVararg()) {
                objArr[kParameter2.getIndex()] = this$0.defaultEmptyArray(kParameter2.getType());
            }
        }
        for (int i2 = 0; i2 < i; i2++) {
            objArr[size + i2] = 0;
        }
        return objArr;
    }

    private final Object[] getAbsentArguments() {
        return (Object[]) this._absentArguments.invoke().clone();
    }

    public final R callDefaultMethod$kotlin_reflection(Map<KParameter, ? extends Object> args, Continuation<?> continuationArgument) throws IllegalCallableAccessException {
        Intrinsics.checkNotNullParameter(args, "args");
        List<KParameter> parameters = getParameters();
        boolean z = false;
        if (!parameters.isEmpty()) {
            int size = parameters.size() + (isSuspend() ? 1 : 0);
            Object[] absentArguments = getAbsentArguments();
            if (isSuspend()) {
                absentArguments[parameters.size()] = continuationArgument;
            }
            boolean zBooleanValue = this.parametersNeedMFVCFlattening.getValue().booleanValue();
            int i = 0;
            for (KParameter kParameter : parameters) {
                int parameterTypeSize = zBooleanValue ? getParameterTypeSize(kParameter) : 1;
                if (args.containsKey(kParameter)) {
                    absentArguments[kParameter.getIndex()] = args.get(kParameter);
                } else if (kParameter.isOptional()) {
                    if (zBooleanValue) {
                        int i2 = i + parameterTypeSize;
                        for (int i3 = i; i3 < i2; i3++) {
                            int i4 = (i3 / 32) + size;
                            Object obj = absentArguments[i4];
                            Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlin.Int");
                            absentArguments[i4] = Integer.valueOf(((Integer) obj).intValue() | (1 << (i3 % 32)));
                        }
                    } else {
                        int i5 = (i / 32) + size;
                        Object obj2 = absentArguments[i5];
                        Intrinsics.checkNotNull(obj2, "null cannot be cast to non-null type kotlin.Int");
                        absentArguments[i5] = Integer.valueOf(((Integer) obj2).intValue() | (1 << (i % 32)));
                    }
                    z = true;
                } else if (!kParameter.isVararg()) {
                    throw new IllegalArgumentException("No argument provided for a required parameter: " + kParameter);
                }
                if (kParameter.getKind() == KParameter.Kind.VALUE) {
                    i += parameterTypeSize;
                }
            }
            if (!z) {
                try {
                    Caller<?> caller = getCaller();
                    Object[] objArrCopyOf = Arrays.copyOf(absentArguments, size);
                    Intrinsics.checkNotNullExpressionValue(objArrCopyOf, "copyOf(...)");
                    return (R) caller.call(objArrCopyOf);
                } catch (IllegalAccessException e) {
                    throw new IllegalCallableAccessException(e);
                }
            }
            Caller<?> defaultCaller = getDefaultCaller();
            if (defaultCaller == null) {
                throw new KotlinReflectionInternalError("This callable does not support a default call: " + getDescriptor());
            }
            try {
                return (R) defaultCaller.call(absentArguments);
            } catch (IllegalAccessException e2) {
                throw new IllegalCallableAccessException(e2);
            }
        }
        try {
            return (R) getCaller().call(isSuspend() ? new Continuation[]{continuationArgument} : new Continuation[0]);
        } catch (IllegalAccessException e3) {
            throw new IllegalCallableAccessException(e3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean parametersNeedMFVCFlattening$lambda$20(KCallableImpl this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        List<KParameter> parameters = this$0.getParameters();
        if ((parameters instanceof Collection) && parameters.isEmpty()) {
            return false;
        }
        Iterator<T> it = parameters.iterator();
        while (it.hasNext()) {
            if (UtilKt.getNeedsMultiFieldValueClassFlattening(((KParameter) it.next()).getType())) {
                return true;
            }
        }
        return false;
    }

    private final int getParameterTypeSize(KParameter parameter) {
        if (!this.parametersNeedMFVCFlattening.getValue().booleanValue()) {
            throw new IllegalArgumentException("Check if parametersNeedMFVCFlattening is true before".toString());
        }
        if (!UtilKt.getNeedsMultiFieldValueClassFlattening(parameter.getType())) {
            return 1;
        }
        KType type = parameter.getType();
        Intrinsics.checkNotNull(type, "null cannot be cast to non-null type kotlin.reflect.jvm.internal.KTypeImpl");
        List<Method> mfvcUnboxMethods = ValueClassAwareCallerKt.getMfvcUnboxMethods(TypeSubstitutionKt.asSimpleType(((KTypeImpl) type).getType()));
        Intrinsics.checkNotNull(mfvcUnboxMethods);
        return mfvcUnboxMethods.size();
    }

    private final R callAnnotationConstructor(Map<KParameter, ? extends Object> args) throws IllegalCallableAccessException {
        Object objDefaultEmptyArray;
        List<KParameter> parameters = getParameters();
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(parameters, 10));
        for (KParameter kParameter : parameters) {
            if (args.containsKey(kParameter)) {
                objDefaultEmptyArray = args.get(kParameter);
                if (objDefaultEmptyArray == null) {
                    throw new IllegalArgumentException("Annotation argument value cannot be null (" + kParameter + ')');
                }
            } else if (kParameter.isOptional()) {
                objDefaultEmptyArray = null;
            } else {
                if (!kParameter.isVararg()) {
                    throw new IllegalArgumentException("No argument provided for a required parameter: " + kParameter);
                }
                objDefaultEmptyArray = defaultEmptyArray(kParameter.getType());
            }
            arrayList.add(objDefaultEmptyArray);
        }
        ArrayList arrayList2 = arrayList;
        Caller<?> defaultCaller = getDefaultCaller();
        if (defaultCaller == null) {
            throw new KotlinReflectionInternalError("This callable does not support a default call: " + getDescriptor());
        }
        try {
            return (R) defaultCaller.call(arrayList2.toArray(new Object[0]));
        } catch (IllegalAccessException e) {
            throw new IllegalCallableAccessException(e);
        }
    }

    private final Object defaultEmptyArray(KType type) {
        Class javaClass = JvmClassMappingKt.getJavaClass((KClass) KTypesJvm.getJvmErasure(type));
        if (javaClass.isArray()) {
            Object objNewInstance = Array.newInstance(javaClass.getComponentType(), 0);
            Intrinsics.checkNotNullExpressionValue(objNewInstance, "run(...)");
            return objNewInstance;
        }
        throw new KotlinReflectionInternalError("Cannot instantiate the default empty array of type " + javaClass.getSimpleName() + ", because it is not an array type");
    }

    private final Type extractContinuationArgument() {
        Type[] lowerBounds;
        if (!isSuspend()) {
            return null;
        }
        Object objLastOrNull = CollectionsKt.lastOrNull((List<? extends Object>) getCaller().getParameterTypes());
        ParameterizedType parameterizedType = objLastOrNull instanceof ParameterizedType ? (ParameterizedType) objLastOrNull : null;
        if (!Intrinsics.areEqual(parameterizedType != null ? parameterizedType.getRawType() : null, Continuation.class)) {
            return null;
        }
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Intrinsics.checkNotNullExpressionValue(actualTypeArguments, "getActualTypeArguments(...)");
        Object objSingle = ArraysKt.single(actualTypeArguments);
        WildcardType wildcardType = objSingle instanceof WildcardType ? (WildcardType) objSingle : null;
        if (wildcardType == null || (lowerBounds = wildcardType.getLowerBounds()) == null) {
            return null;
        }
        return (Type) ArraysKt.first(lowerBounds);
    }
}
