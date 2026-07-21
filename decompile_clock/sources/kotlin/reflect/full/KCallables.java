package kotlin.reflect.full;

import com.xiaomi.onetrack.api.as;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SpreadBuilder;
import kotlin.reflect.KCallable;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.jvm.internal.KCallableImpl;
import kotlin.reflect.jvm.internal.KotlinReflectionInternalError;
import kotlin.reflect.jvm.internal.UtilKt;

/* JADX INFO: compiled from: KCallables.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u00002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0002\u001a\u001a\u0010\u000f\u001a\u0004\u0018\u00010\u0001*\u0006\u0012\u0002\b\u00030\u00022\u0006\u0010\u0010\u001a\u00020\u0011H\u0007\u001a6\u0010\u0012\u001a\u0002H\u0013\"\u0004\b\u0000\u0010\u0013*\b\u0012\u0004\u0012\u0002H\u00130\u00022\u0016\u0010\u0014\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u00160\u0015\"\u0004\u0018\u00010\u0016H\u0087@¢\u0006\u0002\u0010\u0017\u001a4\u0010\u0018\u001a\u0002H\u0013\"\u0004\b\u0000\u0010\u0013*\b\u0012\u0004\u0012\u0002H\u00130\u00022\u0014\u0010\u0014\u001a\u0010\u0012\u0004\u0012\u00020\u0001\u0012\u0006\u0012\u0004\u0018\u00010\u00160\u0019H\u0087@¢\u0006\u0002\u0010\u001a\"$\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u0006\u0012\u0002\b\u00030\u00028FX\u0087\u0004¢\u0006\f\u0012\u0004\b\u0003\u0010\u0004\u001a\u0004\b\u0005\u0010\u0006\"$\u0010\u0007\u001a\u0004\u0018\u00010\u0001*\u0006\u0012\u0002\b\u00030\u00028FX\u0087\u0004¢\u0006\f\u0012\u0004\b\b\u0010\u0004\u001a\u0004\b\t\u0010\u0006\"(\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u000b*\u0006\u0012\u0002\b\u00030\u00028FX\u0087\u0004¢\u0006\f\u0012\u0004\b\f\u0010\u0004\u001a\u0004\b\r\u0010\u000e¨\u0006\u001b"}, d2 = {"instanceParameter", "Lkotlin/reflect/KParameter;", "Lkotlin/reflect/KCallable;", "getInstanceParameter$annotations", "(Lkotlin/reflect/KCallable;)V", "getInstanceParameter", "(Lkotlin/reflect/KCallable;)Lkotlin/reflect/KParameter;", "extensionReceiverParameter", "getExtensionReceiverParameter$annotations", "getExtensionReceiverParameter", "valueParameters", "", "getValueParameters$annotations", "getValueParameters", "(Lkotlin/reflect/KCallable;)Ljava/util/List;", "findParameterByName", as.a, "", "callSuspend", "R", "args", "", "", "(Lkotlin/reflect/KCallable;[Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "callSuspendBy", "", "(Lkotlin/reflect/KCallable;Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "kotlin-reflection"}, k = 2, mv = {2, 0, 0}, xi = 48)
public final class KCallables {

    /* JADX INFO: renamed from: kotlin.reflect.full.KCallables$callSuspend$1, reason: invalid class name */
    /* JADX INFO: compiled from: KCallables.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    static final class AnonymousClass1<R> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        int label;
        /* synthetic */ Object result;

        AnonymousClass1(Continuation<? super AnonymousClass1> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return KCallables.callSuspend(null, null, this);
        }
    }

    /* JADX INFO: renamed from: kotlin.reflect.full.KCallables$callSuspendBy$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: KCallables.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    static final class C00731<R> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        int label;
        /* synthetic */ Object result;

        C00731(Continuation<? super C00731> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return KCallables.callSuspendBy(null, null, this);
        }
    }

    public static /* synthetic */ void getExtensionReceiverParameter$annotations(KCallable kCallable) {
    }

    public static /* synthetic */ void getInstanceParameter$annotations(KCallable kCallable) {
    }

    public static /* synthetic */ void getValueParameters$annotations(KCallable kCallable) {
    }

    public static final KParameter getInstanceParameter(KCallable<?> kCallable) {
        Intrinsics.checkNotNullParameter(kCallable, "<this>");
        Object obj = null;
        boolean z = false;
        Object obj2 = null;
        for (Object obj3 : kCallable.getParameters()) {
            if (((KParameter) obj3).getKind() == KParameter.Kind.INSTANCE) {
                if (z) {
                    return (KParameter) obj;
                }
                z = true;
                obj2 = obj3;
            }
        }
        if (z) {
            obj = obj2;
        }
        return (KParameter) obj;
    }

    public static final KParameter getExtensionReceiverParameter(KCallable<?> kCallable) {
        Intrinsics.checkNotNullParameter(kCallable, "<this>");
        Object obj = null;
        boolean z = false;
        Object obj2 = null;
        for (Object obj3 : kCallable.getParameters()) {
            if (((KParameter) obj3).getKind() == KParameter.Kind.EXTENSION_RECEIVER) {
                if (z) {
                    return (KParameter) obj;
                }
                z = true;
                obj2 = obj3;
            }
        }
        if (z) {
            obj = obj2;
        }
        return (KParameter) obj;
    }

    public static final List<KParameter> getValueParameters(KCallable<?> kCallable) {
        Intrinsics.checkNotNullParameter(kCallable, "<this>");
        List<KParameter> parameters = kCallable.getParameters();
        ArrayList arrayList = new ArrayList();
        for (Object obj : parameters) {
            if (((KParameter) obj).getKind() == KParameter.Kind.VALUE) {
                arrayList.add(obj);
            }
        }
        return arrayList;
    }

    public static final KParameter findParameterByName(KCallable<?> kCallable, String name) {
        Intrinsics.checkNotNullParameter(kCallable, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        Object obj = null;
        boolean z = false;
        Object obj2 = null;
        for (Object obj3 : kCallable.getParameters()) {
            if (Intrinsics.areEqual(((KParameter) obj3).getName(), name)) {
                if (z) {
                    return (KParameter) obj;
                }
                z = true;
                obj2 = obj3;
            }
        }
        if (z) {
            obj = obj2;
        }
        return (KParameter) obj;
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    public static final <R> Object callSuspend(KCallable<? extends R> kCallable, Object[] objArr, Continuation<? super R> continuation) throws Throwable {
        AnonymousClass1 anonymousClass1;
        if (continuation instanceof AnonymousClass1) {
            anonymousClass1 = (AnonymousClass1) continuation;
            if ((anonymousClass1.label & Integer.MIN_VALUE) != 0) {
                anonymousClass1.label -= Integer.MIN_VALUE;
            } else {
                anonymousClass1 = new AnonymousClass1(continuation);
            }
        } else {
            anonymousClass1 = new AnonymousClass1(continuation);
        }
        Object objCall = anonymousClass1.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = anonymousClass1.label;
        if (i == 0) {
            ResultKt.throwOnFailure(objCall);
            if (!kCallable.isSuspend()) {
                return kCallable.call(Arrays.copyOf(objArr, objArr.length));
            }
            if (!(kCallable instanceof KFunction)) {
                throw new IllegalArgumentException("Cannot callSuspend on a property " + kCallable + ": suspend properties are not supported yet");
            }
            anonymousClass1.L$0 = kCallable;
            anonymousClass1.L$1 = objArr;
            anonymousClass1.label = 1;
            AnonymousClass1 anonymousClass2 = anonymousClass1;
            SpreadBuilder spreadBuilder = new SpreadBuilder(2);
            spreadBuilder.addSpread(objArr);
            spreadBuilder.add(anonymousClass2);
            objCall = kCallable.call(spreadBuilder.toArray(new Object[spreadBuilder.size()]));
            if (objCall == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                DebugProbesKt.probeCoroutineSuspended(anonymousClass2);
            }
            if (objCall == coroutine_suspended) {
                return coroutine_suspended;
            }
        } else {
            if (i != 1) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            kCallable = (KCallable) anonymousClass1.L$0;
            ResultKt.throwOnFailure(objCall);
        }
        KFunction kFunction = (KFunction) kCallable;
        return (!Intrinsics.areEqual(kFunction.getReturnType().getClassifier(), Reflection.getOrCreateKotlinClass(Unit.class)) || kFunction.getReturnType().isMarkedNullable()) ? objCall : Unit.INSTANCE;
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    public static final <R> Object callSuspendBy(KCallable<? extends R> kCallable, Map<KParameter, ? extends Object> map, Continuation<? super R> continuation) throws Throwable {
        C00731 c00731;
        if (continuation instanceof C00731) {
            c00731 = (C00731) continuation;
            if ((c00731.label & Integer.MIN_VALUE) != 0) {
                c00731.label -= Integer.MIN_VALUE;
            } else {
                c00731 = new C00731(continuation);
            }
        } else {
            c00731 = new C00731(continuation);
        }
        Object objCallDefaultMethod$kotlin_reflection = c00731.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = c00731.label;
        if (i == 0) {
            ResultKt.throwOnFailure(objCallDefaultMethod$kotlin_reflection);
            if (!kCallable.isSuspend()) {
                return kCallable.callBy(map);
            }
            if (!(kCallable instanceof KFunction)) {
                throw new IllegalArgumentException("Cannot callSuspendBy on a property " + kCallable + ": suspend properties are not supported yet");
            }
            KCallableImpl<?> kCallableImplAsKCallableImpl = UtilKt.asKCallableImpl(kCallable);
            if (kCallableImplAsKCallableImpl == null) {
                throw new KotlinReflectionInternalError("This callable does not support a default call: " + kCallable);
            }
            c00731.L$0 = kCallable;
            c00731.L$1 = map;
            c00731.L$2 = kCallableImplAsKCallableImpl;
            c00731.label = 1;
            C00731 c00732 = c00731;
            objCallDefaultMethod$kotlin_reflection = kCallableImplAsKCallableImpl.callDefaultMethod$kotlin_reflection(map, c00732);
            if (objCallDefaultMethod$kotlin_reflection == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                DebugProbesKt.probeCoroutineSuspended(c00732);
            }
            if (objCallDefaultMethod$kotlin_reflection == coroutine_suspended) {
                return coroutine_suspended;
            }
        } else {
            if (i != 1) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            kCallable = (KCallable) c00731.L$0;
            ResultKt.throwOnFailure(objCallDefaultMethod$kotlin_reflection);
        }
        KFunction kFunction = (KFunction) kCallable;
        return (!Intrinsics.areEqual(kFunction.getReturnType().getClassifier(), Reflection.getOrCreateKotlinClass(Unit.class)) || kFunction.getReturnType().isMarkedNullable()) ? objCallDefaultMethod$kotlin_reflection : Unit.INSTANCE;
    }
}
