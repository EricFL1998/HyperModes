package kotlin.jvm.internal;

import com.xiaomi.onetrack.api.g;
import kotlin.Metadata;

/* JADX INFO: compiled from: PrimitiveSpreadBuilders.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0017\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\n\n\u0002\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\f\u0010\b\u001a\u00020\u0004*\u00020\u0002H\u0014J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\u0006\u0010\r\u001a\u00020\u0002R\u000e\u0010\u0007\u001a\u00020\u0002X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lkotlin/jvm/internal/ShortSpreadBuilder;", "Lkotlin/jvm/internal/PrimitiveSpreadBuilder;", "", "size", "", "<init>", "(I)V", "values", "getSize", "add", "", g.p, "", "toArray", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
public final class ShortSpreadBuilder extends PrimitiveSpreadBuilder<short[]> {
    private final short[] values;

    public ShortSpreadBuilder(int i) {
        super(i);
        this.values = new short[i];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // kotlin.jvm.internal.PrimitiveSpreadBuilder
    public int getSize(short[] sArr) {
        Intrinsics.checkNotNullParameter(sArr, "<this>");
        return sArr.length;
    }

    public final void add(short value) {
        short[] sArr = this.values;
        int position = getPosition();
        setPosition(position + 1);
        sArr[position] = value;
    }

    public final short[] toArray() {
        return toArray(this.values, new short[size()]);
    }
}
