package kotlin.jvm.internal;

import com.xiaomi.onetrack.api.g;
import kotlin.Metadata;

/* JADX INFO: compiled from: PrimitiveSpreadBuilders.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0018\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\f\u0010\b\u001a\u00020\u0004*\u00020\u0002H\u0014J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\u0006\u0010\r\u001a\u00020\u0002R\u000e\u0010\u0007\u001a\u00020\u0002X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lkotlin/jvm/internal/BooleanSpreadBuilder;", "Lkotlin/jvm/internal/PrimitiveSpreadBuilder;", "", "size", "", "<init>", "(I)V", "values", "getSize", "add", "", g.p, "", "toArray", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
public final class BooleanSpreadBuilder extends PrimitiveSpreadBuilder<boolean[]> {
    private final boolean[] values;

    public BooleanSpreadBuilder(int i) {
        super(i);
        this.values = new boolean[i];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // kotlin.jvm.internal.PrimitiveSpreadBuilder
    public int getSize(boolean[] zArr) {
        Intrinsics.checkNotNullParameter(zArr, "<this>");
        return zArr.length;
    }

    public final void add(boolean value) {
        boolean[] zArr = this.values;
        int position = getPosition();
        setPosition(position + 1);
        zArr[position] = value;
    }

    public final boolean[] toArray() {
        return toArray(this.values, new boolean[size()]);
    }
}
