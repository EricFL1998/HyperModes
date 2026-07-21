package kotlin.sequences;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import kotlin.Deprecated;
import kotlin.DeprecatedSinceKotlin;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: _SequencesJvm.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000T\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u001f\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000f\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a(\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u0006\u0012\u0002\b\u00030\u00012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0004\u001aA\u0010\u0005\u001a\u0002H\u0006\"\u0010\b\u0000\u0010\u0006*\n\u0012\u0006\b\u0000\u0012\u0002H\u00020\u0007\"\u0004\b\u0001\u0010\u0002*\u0006\u0012\u0002\b\u00030\u00012\u0006\u0010\b\u001a\u0002H\u00062\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0004¢\u0006\u0002\u0010\t\u001a&\u0010\n\u001a\b\u0012\u0004\u0012\u0002H\f0\u000b\"\u000e\b\u0000\u0010\f*\b\u0012\u0004\u0012\u0002H\f0\r*\b\u0012\u0004\u0012\u0002H\f0\u0001\u001a8\u0010\n\u001a\b\u0012\u0004\u0012\u0002H\f0\u000b\"\u0004\b\u0000\u0010\f*\b\u0012\u0004\u0012\u0002H\f0\u00012\u001a\u0010\u000e\u001a\u0016\u0012\u0006\b\u0000\u0012\u0002H\f0\u000fj\n\u0012\u0006\b\u0000\u0012\u0002H\f`\u0010\u001a\u0019\u0010\u0011\u001a\u0004\u0018\u00010\u0012*\b\u0012\u0004\u0012\u00020\u00120\u0001H\u0007¢\u0006\u0002\u0010\u0013\u001a\u0019\u0010\u0011\u001a\u0004\u0018\u00010\u0014*\b\u0012\u0004\u0012\u00020\u00140\u0001H\u0007¢\u0006\u0002\u0010\u0015\u001a)\u0010\u0011\u001a\u0004\u0018\u0001H\f\"\u000e\b\u0000\u0010\f*\b\u0012\u0004\u0012\u0002H\f0\r*\b\u0012\u0004\u0012\u0002H\f0\u0001H\u0007¢\u0006\u0002\u0010\u0016\u001aG\u0010\u0017\u001a\u0004\u0018\u0001H\f\"\u0004\b\u0000\u0010\f\"\u000e\b\u0001\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\r*\b\u0012\u0004\u0012\u0002H\f0\u00012\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u0002H\f\u0012\u0004\u0012\u0002H\u00020\u0019H\u0087\bø\u0001\u0000¢\u0006\u0002\u0010\u001a\u001a;\u0010\u001b\u001a\u0004\u0018\u0001H\f\"\u0004\b\u0000\u0010\f*\b\u0012\u0004\u0012\u0002H\f0\u00012\u001a\u0010\u000e\u001a\u0016\u0012\u0006\b\u0000\u0012\u0002H\f0\u000fj\n\u0012\u0006\b\u0000\u0012\u0002H\f`\u0010H\u0007¢\u0006\u0002\u0010\u001c\u001a\u0019\u0010\u001d\u001a\u0004\u0018\u00010\u0012*\b\u0012\u0004\u0012\u00020\u00120\u0001H\u0007¢\u0006\u0002\u0010\u0013\u001a\u0019\u0010\u001d\u001a\u0004\u0018\u00010\u0014*\b\u0012\u0004\u0012\u00020\u00140\u0001H\u0007¢\u0006\u0002\u0010\u0015\u001a)\u0010\u001d\u001a\u0004\u0018\u0001H\f\"\u000e\b\u0000\u0010\f*\b\u0012\u0004\u0012\u0002H\f0\r*\b\u0012\u0004\u0012\u0002H\f0\u0001H\u0007¢\u0006\u0002\u0010\u0016\u001aG\u0010\u001e\u001a\u0004\u0018\u0001H\f\"\u0004\b\u0000\u0010\f\"\u000e\b\u0001\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\r*\b\u0012\u0004\u0012\u0002H\f0\u00012\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u0002H\f\u0012\u0004\u0012\u0002H\u00020\u0019H\u0087\bø\u0001\u0000¢\u0006\u0002\u0010\u001a\u001a;\u0010\u001f\u001a\u0004\u0018\u0001H\f\"\u0004\b\u0000\u0010\f*\b\u0012\u0004\u0012\u0002H\f0\u00012\u001a\u0010\u000e\u001a\u0016\u0012\u0006\b\u0000\u0012\u0002H\f0\u000fj\n\u0012\u0006\b\u0000\u0012\u0002H\f`\u0010H\u0007¢\u0006\u0002\u0010\u001c\u001a5\u0010 \u001a\u00020!\"\u0004\b\u0000\u0010\f*\b\u0012\u0004\u0012\u0002H\f0\u00012\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u0002H\f\u0012\u0004\u0012\u00020!0\u0019H\u0087\bø\u0001\u0000¢\u0006\u0002\b\"\u001a5\u0010 \u001a\u00020#\"\u0004\b\u0000\u0010\f*\b\u0012\u0004\u0012\u0002H\f0\u00012\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u0002H\f\u0012\u0004\u0012\u00020#0\u0019H\u0087\bø\u0001\u0000¢\u0006\u0002\b$\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006%"}, d2 = {"filterIsInstance", "Lkotlin/sequences/Sequence;", "R", "klass", "Ljava/lang/Class;", "filterIsInstanceTo", "C", "", "destination", "(Lkotlin/sequences/Sequence;Ljava/util/Collection;Ljava/lang/Class;)Ljava/util/Collection;", "toSortedSet", "Ljava/util/SortedSet;", "T", "", "comparator", "Ljava/util/Comparator;", "Lkotlin/Comparator;", "max", "", "(Lkotlin/sequences/Sequence;)Ljava/lang/Double;", "", "(Lkotlin/sequences/Sequence;)Ljava/lang/Float;", "(Lkotlin/sequences/Sequence;)Ljava/lang/Comparable;", "maxBy", "selector", "Lkotlin/Function1;", "(Lkotlin/sequences/Sequence;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "maxWith", "(Lkotlin/sequences/Sequence;Ljava/util/Comparator;)Ljava/lang/Object;", "min", "minBy", "minWith", "sumOf", "Ljava/math/BigDecimal;", "sumOfBigDecimal", "Ljava/math/BigInteger;", "sumOfBigInteger", "kotlin-stdlib"}, k = 5, mv = {2, 1, 0}, xi = 49, xs = "kotlin/sequences/SequencesKt")
class SequencesKt___SequencesJvmKt extends SequencesKt__SequencesKt {
    public static final <R> Sequence<R> filterIsInstance(Sequence<?> sequence, final Class<R> klass) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Intrinsics.checkNotNullParameter(klass, "klass");
        Sequence<R> sequenceFilter = SequencesKt.filter(sequence, new Function1() { // from class: kotlin.sequences.SequencesKt___SequencesJvmKt$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(klass.isInstance(obj));
            }
        });
        Intrinsics.checkNotNull(sequenceFilter, "null cannot be cast to non-null type kotlin.sequences.Sequence<R of kotlin.sequences.SequencesKt___SequencesJvmKt.filterIsInstance>");
        return sequenceFilter;
    }

    public static final <C extends Collection<? super R>, R> C filterIsInstanceTo(Sequence<?> sequence, C destination, Class<R> klass) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Intrinsics.checkNotNullParameter(destination, "destination");
        Intrinsics.checkNotNullParameter(klass, "klass");
        for (Object obj : sequence) {
            if (klass.isInstance(obj)) {
                destination.add(obj);
            }
        }
        return destination;
    }

    public static final <T extends Comparable<? super T>> SortedSet<T> toSortedSet(Sequence<? extends T> sequence) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        return (SortedSet) SequencesKt.toCollection(sequence, new TreeSet());
    }

    public static final <T> SortedSet<T> toSortedSet(Sequence<? extends T> sequence, Comparator<? super T> comparator) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Intrinsics.checkNotNullParameter(comparator, "comparator");
        return (SortedSet) SequencesKt.toCollection(sequence, new TreeSet(comparator));
    }

    @Deprecated(message = "Use maxOrNull instead.", replaceWith = @ReplaceWith(expression = "this.maxOrNull()", imports = {}))
    @DeprecatedSinceKotlin(errorSince = "1.5", hiddenSince = "1.6", warningSince = "1.4")
    /* JADX INFO: renamed from: max, reason: collision with other method in class */
    public static final /* synthetic */ Double m1499max(Sequence sequence) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        return SequencesKt.maxOrNull(sequence);
    }

    @Deprecated(message = "Use maxOrNull instead.", replaceWith = @ReplaceWith(expression = "this.maxOrNull()", imports = {}))
    @DeprecatedSinceKotlin(errorSince = "1.5", hiddenSince = "1.6", warningSince = "1.4")
    /* JADX INFO: renamed from: max, reason: collision with other method in class */
    public static final /* synthetic */ Float m1500max(Sequence sequence) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        return SequencesKt.maxOrNull(sequence);
    }

    @Deprecated(message = "Use maxOrNull instead.", replaceWith = @ReplaceWith(expression = "this.maxOrNull()", imports = {}))
    @DeprecatedSinceKotlin(errorSince = "1.5", hiddenSince = "1.6", warningSince = "1.4")
    public static final /* synthetic */ Comparable max(Sequence sequence) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        return SequencesKt.maxOrNull(sequence);
    }

    @Deprecated(message = "Use maxWithOrNull instead.", replaceWith = @ReplaceWith(expression = "this.maxWithOrNull(comparator)", imports = {}))
    @DeprecatedSinceKotlin(errorSince = "1.5", hiddenSince = "1.6", warningSince = "1.4")
    public static final /* synthetic */ Object maxWith(Sequence sequence, Comparator comparator) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Intrinsics.checkNotNullParameter(comparator, "comparator");
        return SequencesKt.maxWithOrNull(sequence, comparator);
    }

    @Deprecated(message = "Use minOrNull instead.", replaceWith = @ReplaceWith(expression = "this.minOrNull()", imports = {}))
    @DeprecatedSinceKotlin(errorSince = "1.5", hiddenSince = "1.6", warningSince = "1.4")
    /* JADX INFO: renamed from: min, reason: collision with other method in class */
    public static final /* synthetic */ Double m1501min(Sequence sequence) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        return SequencesKt.minOrNull(sequence);
    }

    @Deprecated(message = "Use minOrNull instead.", replaceWith = @ReplaceWith(expression = "this.minOrNull()", imports = {}))
    @DeprecatedSinceKotlin(errorSince = "1.5", hiddenSince = "1.6", warningSince = "1.4")
    /* JADX INFO: renamed from: min, reason: collision with other method in class */
    public static final /* synthetic */ Float m1502min(Sequence sequence) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        return SequencesKt.minOrNull(sequence);
    }

    @Deprecated(message = "Use minOrNull instead.", replaceWith = @ReplaceWith(expression = "this.minOrNull()", imports = {}))
    @DeprecatedSinceKotlin(errorSince = "1.5", hiddenSince = "1.6", warningSince = "1.4")
    public static final /* synthetic */ Comparable min(Sequence sequence) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        return SequencesKt.minOrNull(sequence);
    }

    @Deprecated(message = "Use minWithOrNull instead.", replaceWith = @ReplaceWith(expression = "this.minWithOrNull(comparator)", imports = {}))
    @DeprecatedSinceKotlin(errorSince = "1.5", hiddenSince = "1.6", warningSince = "1.4")
    public static final /* synthetic */ Object minWith(Sequence sequence, Comparator comparator) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Intrinsics.checkNotNullParameter(comparator, "comparator");
        return SequencesKt.minWithOrNull(sequence, comparator);
    }

    private static final <T> BigDecimal sumOfBigDecimal(Sequence<? extends T> sequence, Function1<? super T, ? extends BigDecimal> selector) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Intrinsics.checkNotNullParameter(selector, "selector");
        BigDecimal bigDecimalValueOf = BigDecimal.valueOf(0L);
        Intrinsics.checkNotNullExpressionValue(bigDecimalValueOf, "valueOf(...)");
        Iterator<? extends T> it = sequence.iterator();
        while (it.hasNext()) {
            bigDecimalValueOf = bigDecimalValueOf.add(selector.invoke(it.next()));
            Intrinsics.checkNotNullExpressionValue(bigDecimalValueOf, "add(...)");
        }
        return bigDecimalValueOf;
    }

    private static final <T> BigInteger sumOfBigInteger(Sequence<? extends T> sequence, Function1<? super T, ? extends BigInteger> selector) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Intrinsics.checkNotNullParameter(selector, "selector");
        BigInteger bigIntegerValueOf = BigInteger.valueOf(0L);
        Intrinsics.checkNotNullExpressionValue(bigIntegerValueOf, "valueOf(...)");
        Iterator<? extends T> it = sequence.iterator();
        while (it.hasNext()) {
            bigIntegerValueOf = bigIntegerValueOf.add(selector.invoke(it.next()));
            Intrinsics.checkNotNullExpressionValue(bigIntegerValueOf, "add(...)");
        }
        return bigIntegerValueOf;
    }

    @Deprecated(message = "Use maxByOrNull instead.", replaceWith = @ReplaceWith(expression = "this.maxByOrNull(selector)", imports = {}))
    @DeprecatedSinceKotlin(errorSince = "1.5", hiddenSince = "1.6", warningSince = "1.4")
    public static final /* synthetic */ <T, R extends Comparable<? super R>> T maxBy(Sequence<? extends T> sequence, Function1<? super T, ? extends R> selector) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Intrinsics.checkNotNullParameter(selector, "selector");
        Iterator<? extends T> it = sequence.iterator();
        if (!it.hasNext()) {
            return null;
        }
        T next = it.next();
        if (it.hasNext()) {
            R rInvoke = selector.invoke(next);
            do {
                T next2 = it.next();
                R rInvoke2 = selector.invoke(next2);
                if (rInvoke.compareTo(rInvoke2) < 0) {
                    next = next2;
                    rInvoke = rInvoke2;
                }
            } while (it.hasNext());
        }
        return (T) next;
    }

    @Deprecated(message = "Use minByOrNull instead.", replaceWith = @ReplaceWith(expression = "this.minByOrNull(selector)", imports = {}))
    @DeprecatedSinceKotlin(errorSince = "1.5", hiddenSince = "1.6", warningSince = "1.4")
    public static final /* synthetic */ <T, R extends Comparable<? super R>> T minBy(Sequence<? extends T> sequence, Function1<? super T, ? extends R> selector) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Intrinsics.checkNotNullParameter(selector, "selector");
        Iterator<? extends T> it = sequence.iterator();
        if (!it.hasNext()) {
            return null;
        }
        T next = it.next();
        if (it.hasNext()) {
            R rInvoke = selector.invoke(next);
            do {
                T next2 = it.next();
                R rInvoke2 = selector.invoke(next2);
                if (rInvoke.compareTo(rInvoke2) > 0) {
                    next = next2;
                    rInvoke = rInvoke2;
                }
            } while (it.hasNext());
        }
        return (T) next;
    }
}
