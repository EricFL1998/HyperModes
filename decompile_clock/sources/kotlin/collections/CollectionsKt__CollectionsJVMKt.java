package kotlin.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.builders.ListBuilder;
import kotlin.internal.PlatformImplementationsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.CollectionToArray;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: compiled from: CollectionsJVM.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000P\n\u0000\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0010!\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u001c\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u001e\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0006\u001a\u001f\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u00022\u0006\u0010\u0003\u001a\u0002H\u0002¢\u0006\u0002\u0010\u0004\u001a7\u0010\u0005\u001a\b\u0012\u0004\u0012\u0002H\u00060\u0001\"\u0004\b\u0000\u0010\u00062\u001d\u0010\u0007\u001a\u0019\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00060\t\u0012\u0004\u0012\u00020\n0\b¢\u0006\u0002\b\u000bH\u0081\bø\u0001\u0000\u001a?\u0010\u0005\u001a\b\u0012\u0004\u0012\u0002H\u00060\u0001\"\u0004\b\u0000\u0010\u00062\u0006\u0010\f\u001a\u00020\r2\u001d\u0010\u0007\u001a\u0019\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00060\t\u0012\u0004\u0012\u00020\n0\b¢\u0006\u0002\b\u000bH\u0081\bø\u0001\u0000\u001a\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u0002H\u00060\t\"\u0004\b\u0000\u0010\u0006H\u0001\u001a\u001c\u0010\u000e\u001a\b\u0012\u0004\u0012\u0002H\u00060\t\"\u0004\b\u0000\u0010\u00062\u0006\u0010\f\u001a\u00020\rH\u0001\u001a\"\u0010\u000f\u001a\b\u0012\u0004\u0012\u0002H\u00060\u0001\"\u0004\b\u0000\u0010\u00062\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u0002H\u00060\tH\u0001\u001a\u001f\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0012H\u0087\b\u001a\u001e\u0010\u0013\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0014H\u0007\u001a&\u0010\u0013\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0007\u001a\"\u0010\u0017\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00190\u00182\n\u0010\u001a\u001a\u0006\u0012\u0002\b\u00030\u001bH\u0081\b¢\u0006\u0002\u0010\u001c\u001a4\u0010\u0017\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0018\"\u0004\b\u0000\u0010\u00022\n\u0010\u001a\u001a\u0006\u0012\u0002\b\u00030\u001b2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0018H\u0081\b¢\u0006\u0002\u0010\u001e\u001a/\u0010\u001f\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0018\"\u0004\b\u0000\u0010\u00022\u0006\u0010 \u001a\u00020\r2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0018H\u0000¢\u0006\u0002\u0010!\u001a1\u0010\"\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u00190\u0018\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u00182\u0006\u0010#\u001a\u00020$H\u0000¢\u0006\u0002\u0010%\u001a\u0011\u0010&\u001a\u00020\r2\u0006\u0010'\u001a\u00020\rH\u0081\b\u001a\u0011\u0010(\u001a\u00020\r2\u0006\u0010)\u001a\u00020\rH\u0081\b\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006*"}, d2 = {"listOf", "", "T", "element", "(Ljava/lang/Object;)Ljava/util/List;", "buildListInternal", "E", "builderAction", "Lkotlin/Function1;", "", "", "Lkotlin/ExtensionFunctionType;", "capacity", "", "createListBuilder", "build", "builder", "toList", "Ljava/util/Enumeration;", "shuffled", "", "random", "Ljava/util/Random;", "collectionToArray", "", "", "collection", "", "(Ljava/util/Collection;)[Ljava/lang/Object;", "array", "(Ljava/util/Collection;[Ljava/lang/Object;)[Ljava/lang/Object;", "terminateCollectionToArray", "collectionSize", "(I[Ljava/lang/Object;)[Ljava/lang/Object;", "copyToArrayOfAny", "isVarargs", "", "([Ljava/lang/Object;Z)[Ljava/lang/Object;", "checkIndexOverflow", "index", "checkCountOverflow", "count", "kotlin-stdlib"}, k = 5, mv = {2, 1, 0}, xi = 49, xs = "kotlin/collections/CollectionsKt")
public class CollectionsKt__CollectionsJVMKt {
    public static final <T> List<T> listOf(T t) {
        List<T> listSingletonList = Collections.singletonList(t);
        Intrinsics.checkNotNullExpressionValue(listSingletonList, "singletonList(...)");
        return listSingletonList;
    }

    private static final <E> List<E> buildListInternal(Function1<? super List<E>, Unit> builderAction) {
        Intrinsics.checkNotNullParameter(builderAction, "builderAction");
        List listCreateListBuilder = CollectionsKt.createListBuilder();
        builderAction.invoke(listCreateListBuilder);
        return CollectionsKt.build(listCreateListBuilder);
    }

    private static final <E> List<E> buildListInternal(int i, Function1<? super List<E>, Unit> builderAction) {
        Intrinsics.checkNotNullParameter(builderAction, "builderAction");
        List listCreateListBuilder = CollectionsKt.createListBuilder(i);
        builderAction.invoke(listCreateListBuilder);
        return CollectionsKt.build(listCreateListBuilder);
    }

    public static final <E> List<E> createListBuilder() {
        return new ListBuilder(0, 1, null);
    }

    public static final <E> List<E> createListBuilder(int i) {
        return new ListBuilder(i);
    }

    public static final <E> List<E> build(List<E> builder) {
        Intrinsics.checkNotNullParameter(builder, "builder");
        return ((ListBuilder) builder).build();
    }

    private static final <T> List<T> toList(Enumeration<T> enumeration) {
        Intrinsics.checkNotNullParameter(enumeration, "<this>");
        ArrayList list = Collections.list(enumeration);
        Intrinsics.checkNotNullExpressionValue(list, "list(...)");
        return list;
    }

    public static final <T> List<T> shuffled(Iterable<? extends T> iterable) {
        Intrinsics.checkNotNullParameter(iterable, "<this>");
        List<T> mutableList = CollectionsKt.toMutableList(iterable);
        Collections.shuffle(mutableList);
        return mutableList;
    }

    public static final <T> List<T> shuffled(Iterable<? extends T> iterable, Random random) {
        Intrinsics.checkNotNullParameter(iterable, "<this>");
        Intrinsics.checkNotNullParameter(random, "random");
        List<T> mutableList = CollectionsKt.toMutableList(iterable);
        Collections.shuffle(mutableList, random);
        return mutableList;
    }

    private static final Object[] collectionToArray(Collection<?> collection) {
        Intrinsics.checkNotNullParameter(collection, "collection");
        return CollectionToArray.toArray(collection);
    }

    private static final <T> T[] collectionToArray(Collection<?> collection, T[] array) {
        Intrinsics.checkNotNullParameter(collection, "collection");
        Intrinsics.checkNotNullParameter(array, "array");
        return (T[]) CollectionToArray.toArray(collection, array);
    }

    public static final <T> T[] terminateCollectionToArray(int i, T[] array) {
        Intrinsics.checkNotNullParameter(array, "array");
        if (i < array.length) {
            array[i] = null;
        }
        return array;
    }

    public static final <T> Object[] copyToArrayOfAny(T[] tArr, boolean z) {
        Intrinsics.checkNotNullParameter(tArr, "<this>");
        if (z && Intrinsics.areEqual(tArr.getClass(), Object[].class)) {
            return tArr;
        }
        Object[] objArrCopyOf = Arrays.copyOf(tArr, tArr.length, Object[].class);
        Intrinsics.checkNotNullExpressionValue(objArrCopyOf, "copyOf(...)");
        return objArrCopyOf;
    }

    private static final int checkIndexOverflow(int i) {
        if (i < 0) {
            if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0)) {
                CollectionsKt.throwIndexOverflow();
            } else {
                throw new ArithmeticException("Index overflow has happened.");
            }
        }
        return i;
    }

    private static final int checkCountOverflow(int i) {
        if (i < 0) {
            if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0)) {
                CollectionsKt.throwCountOverflow();
            } else {
                throw new ArithmeticException("Count overflow has happened.");
            }
        }
        return i;
    }
}
