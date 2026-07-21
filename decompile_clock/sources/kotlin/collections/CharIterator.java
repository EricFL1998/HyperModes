package kotlin.collections;

import java.util.Iterator;
import kotlin.Metadata;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: compiled from: PrimitiveIterators.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010(\n\u0002\u0010\f\n\u0002\b\u0006\b&\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\u000e\u0010\u0005\u001a\u00020\u0002H\u0086\u0002¢\u0006\u0002\u0010\u0006J\b\u0010\u0007\u001a\u00020\u0002H&¨\u0006\b"}, d2 = {"Lkotlin/collections/CharIterator;", "", "", "<init>", "()V", "next", "()Ljava/lang/Character;", "nextChar", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
public abstract class CharIterator implements Iterator<Character>, KMappedMarker {
    public abstract char nextChar();

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    @Override // java.util.Iterator
    public /* bridge */ /* synthetic */ Character next() {
        return Character.valueOf(nextChar());
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Iterator
    public final Character next() {
        return Character.valueOf(nextChar());
    }
}
