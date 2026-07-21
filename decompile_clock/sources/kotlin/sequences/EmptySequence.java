package kotlin.sequences;

import java.util.Iterator;
import kotlin.Metadata;
import kotlin.collections.EmptyIterator;

/* JADX INFO: compiled from: Sequences.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0001\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010(\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\bÂ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u00012\b\u0012\u0004\u0012\u00020\u00020\u0003B\t\b\u0002¢\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00020\u0007H\u0096\u0002J\u0010\u0010\b\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\nH\u0016J\u0010\u0010\u000b\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\nH\u0016¨\u0006\f"}, d2 = {"Lkotlin/sequences/EmptySequence;", "Lkotlin/sequences/Sequence;", "", "Lkotlin/sequences/DropTakeSequence;", "<init>", "()V", "iterator", "", "drop", "n", "", "take", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
final class EmptySequence implements Sequence, DropTakeSequence {
    public static final EmptySequence INSTANCE = new EmptySequence();

    private EmptySequence() {
    }

    @Override // kotlin.sequences.Sequence
    public Iterator iterator() {
        return EmptyIterator.INSTANCE;
    }

    @Override // kotlin.sequences.DropTakeSequence
    public EmptySequence drop(int n) {
        return INSTANCE;
    }

    @Override // kotlin.sequences.DropTakeSequence
    public EmptySequence take(int n) {
        return INSTANCE;
    }
}
