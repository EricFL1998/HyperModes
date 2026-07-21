package kotlin.coroutines;

import kotlin.Metadata;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: CoroutineContext.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\bg\u0018\u00002\u00020\u0001:\u0002\u0011\u0012J(\u0010\u0002\u001a\u0004\u0018\u0001H\u0003\"\b\b\u0000\u0010\u0003*\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u0002H\u00030\u0006H¦\u0002¢\u0006\u0002\u0010\u0007J5\u0010\b\u001a\u0002H\t\"\u0004\b\u0000\u0010\t2\u0006\u0010\n\u001a\u0002H\t2\u0018\u0010\u000b\u001a\u0014\u0012\u0004\u0012\u0002H\t\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u0002H\t0\fH&¢\u0006\u0002\u0010\rJ\u0011\u0010\u000e\u001a\u00020\u00002\u0006\u0010\u000f\u001a\u00020\u0000H\u0096\u0002J\u0014\u0010\u0010\u001a\u00020\u00002\n\u0010\u0005\u001a\u0006\u0012\u0002\b\u00030\u0006H&¨\u0006\u0013"}, d2 = {"Lkotlin/coroutines/CoroutineContext;", "", "get", "E", "Lkotlin/coroutines/CoroutineContext$Element;", "key", "Lkotlin/coroutines/CoroutineContext$Key;", "(Lkotlin/coroutines/CoroutineContext$Key;)Lkotlin/coroutines/CoroutineContext$Element;", "fold", "R", "initial", "operation", "Lkotlin/Function2;", "(Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)Ljava/lang/Object;", "plus", "context", "minusKey", "Key", "Element", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
public interface CoroutineContext {

    /* JADX INFO: compiled from: CoroutineContext.kt */
    @Metadata(d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\bf\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u00022\u00020\u0003¨\u0006\u0004"}, d2 = {"Lkotlin/coroutines/CoroutineContext$Key;", "E", "Lkotlin/coroutines/CoroutineContext$Element;", "", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
    public interface Key<E extends Element> {
    }

    <R> R fold(R initial, Function2<? super R, ? super Element, ? extends R> operation);

    <E extends Element> E get(Key<E> key);

    CoroutineContext minusKey(Key<?> key);

    CoroutineContext plus(CoroutineContext context);

    /* JADX INFO: compiled from: CoroutineContext.kt */
    @Metadata(k = 3, mv = {2, 1, 0}, xi = 48)
    public static final class DefaultImpls {
        public static CoroutineContext plus(CoroutineContext coroutineContext, CoroutineContext context) {
            Intrinsics.checkNotNullParameter(context, "context");
            return context == EmptyCoroutineContext.INSTANCE ? coroutineContext : (CoroutineContext) context.fold(coroutineContext, new Function2() { // from class: kotlin.coroutines.CoroutineContext$DefaultImpls$$ExternalSyntheticLambda0
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return CoroutineContext.DefaultImpls.plus$lambda$0((CoroutineContext) obj, (CoroutineContext.Element) obj2);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static CoroutineContext plus$lambda$0(CoroutineContext acc, Element element) {
            CombinedContext combinedContext;
            Intrinsics.checkNotNullParameter(acc, "acc");
            Intrinsics.checkNotNullParameter(element, "element");
            CoroutineContext coroutineContextMinusKey = acc.minusKey(element.getKey());
            if (coroutineContextMinusKey == EmptyCoroutineContext.INSTANCE) {
                return element;
            }
            ContinuationInterceptor continuationInterceptor = (ContinuationInterceptor) coroutineContextMinusKey.get(ContinuationInterceptor.INSTANCE);
            if (continuationInterceptor == null) {
                combinedContext = new CombinedContext(coroutineContextMinusKey, element);
            } else {
                CoroutineContext coroutineContextMinusKey2 = coroutineContextMinusKey.minusKey(ContinuationInterceptor.INSTANCE);
                combinedContext = coroutineContextMinusKey2 == EmptyCoroutineContext.INSTANCE ? new CombinedContext(element, continuationInterceptor) : new CombinedContext(new CombinedContext(coroutineContextMinusKey2, element), continuationInterceptor);
            }
            return combinedContext;
        }
    }

    /* JADX INFO: compiled from: CoroutineContext.kt */
    @Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J(\u0010\u0006\u001a\u0004\u0018\u0001H\u0007\"\b\b\u0000\u0010\u0007*\u00020\u00002\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u0002H\u00070\u0003H\u0096\u0002¢\u0006\u0002\u0010\bJ5\u0010\t\u001a\u0002H\n\"\u0004\b\u0000\u0010\n2\u0006\u0010\u000b\u001a\u0002H\n2\u0018\u0010\f\u001a\u0014\u0012\u0004\u0012\u0002H\n\u0012\u0004\u0012\u00020\u0000\u0012\u0004\u0012\u0002H\n0\rH\u0016¢\u0006\u0002\u0010\u000eJ\u0014\u0010\u000f\u001a\u00020\u00012\n\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003H\u0016R\u0016\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005¨\u0006\u0010"}, d2 = {"Lkotlin/coroutines/CoroutineContext$Element;", "Lkotlin/coroutines/CoroutineContext;", "key", "Lkotlin/coroutines/CoroutineContext$Key;", "getKey", "()Lkotlin/coroutines/CoroutineContext$Key;", "get", "E", "(Lkotlin/coroutines/CoroutineContext$Key;)Lkotlin/coroutines/CoroutineContext$Element;", "fold", "R", "initial", "operation", "Lkotlin/Function2;", "(Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)Ljava/lang/Object;", "minusKey", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
    public interface Element extends CoroutineContext {
        @Override // kotlin.coroutines.CoroutineContext
        <R> R fold(R initial, Function2<? super R, ? super Element, ? extends R> operation);

        @Override // kotlin.coroutines.CoroutineContext
        <E extends Element> E get(Key<E> key);

        Key<?> getKey();

        @Override // kotlin.coroutines.CoroutineContext
        CoroutineContext minusKey(Key<?> key);

        /* JADX INFO: compiled from: CoroutineContext.kt */
        @Metadata(k = 3, mv = {2, 1, 0}, xi = 48)
        public static final class DefaultImpls {
            public static CoroutineContext plus(Element element, CoroutineContext context) {
                Intrinsics.checkNotNullParameter(context, "context");
                return DefaultImpls.plus(element, context);
            }

            /* JADX WARN: Multi-variable type inference failed */
            public static <E extends Element> E get(Element element, Key<E> key) {
                Intrinsics.checkNotNullParameter(key, "key");
                if (!Intrinsics.areEqual(element.getKey(), key)) {
                    return null;
                }
                Intrinsics.checkNotNull(element, "null cannot be cast to non-null type E of kotlin.coroutines.CoroutineContext.Element.get");
                return element;
            }

            public static <R> R fold(Element element, R r, Function2<? super R, ? super Element, ? extends R> operation) {
                Intrinsics.checkNotNullParameter(operation, "operation");
                return operation.invoke(r, element);
            }

            public static CoroutineContext minusKey(Element element, Key<?> key) {
                Intrinsics.checkNotNullParameter(key, "key");
                boolean zAreEqual = Intrinsics.areEqual(element.getKey(), key);
                CoroutineContext coroutineContext = element;
                if (zAreEqual) {
                    coroutineContext = EmptyCoroutineContext.INSTANCE;
                }
                return coroutineContext;
            }
        }
    }
}
