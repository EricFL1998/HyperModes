package kotlin.time;

import com.android.deskclock.settings.SearchContract;
import com.xiaomi.onetrack.api.g;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: measureTime.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002B\u0017\u0012\u0006\u0010\u0003\u001a\u00028\u0000\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\u000e\u001a\u00028\u0000HÆ\u0003¢\u0006\u0002\u0010\tJ\u0010\u0010\u000f\u001a\u00020\u0005HÆ\u0003¢\u0006\u0004\b\u0010\u0010\fJ*\u0010\u0011\u001a\b\u0012\u0004\u0012\u00028\u00000\u00002\b\b\u0002\u0010\u0003\u001a\u00028\u00002\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001¢\u0006\u0004\b\u0012\u0010\u0013J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0002HÖ\u0003J\t\u0010\u0017\u001a\u00020\u0018HÖ\u0001J\t\u0010\u0019\u001a\u00020\u001aHÖ\u0001R\u0013\u0010\u0003\u001a\u00028\u0000¢\u0006\n\n\u0002\u0010\n\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0004\u001a\u00020\u0005¢\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u000b\u0010\f¨\u0006\u001b"}, d2 = {"Lkotlin/time/TimedValue;", "T", "", g.p, "duration", "Lkotlin/time/Duration;", "<init>", "(Ljava/lang/Object;JLkotlin/jvm/internal/DefaultConstructorMarker;)V", "getValue", "()Ljava/lang/Object;", "Ljava/lang/Object;", "getDuration-UwyO8pc", "()J", "J", "component1", "component2", "component2-UwyO8pc", "copy", "copy-RFiDyg4", "(Ljava/lang/Object;J)Lkotlin/time/TimedValue;", "equals", "", SearchContract.SearchResultColumn.COLUMN_OTHER, "hashCode", "", "toString", "", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
public final /* data */ class TimedValue<T> {
    private final long duration;
    private final T value;

    public /* synthetic */ TimedValue(Object obj, long j, DefaultConstructorMarker defaultConstructorMarker) {
        this(obj, j);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: copy-RFiDyg4$default, reason: not valid java name */
    public static /* synthetic */ TimedValue m1692copyRFiDyg4$default(TimedValue timedValue, Object obj, long j, int i, Object obj2) {
        if ((i & 1) != 0) {
            obj = timedValue.value;
        }
        if ((i & 2) != 0) {
            j = timedValue.duration;
        }
        return timedValue.m1694copyRFiDyg4(obj, j);
    }

    public final T component1() {
        return this.value;
    }

    /* JADX INFO: renamed from: component2-UwyO8pc, reason: not valid java name and from getter */
    public final long getDuration() {
        return this.duration;
    }

    /* JADX INFO: renamed from: copy-RFiDyg4, reason: not valid java name */
    public final TimedValue<T> m1694copyRFiDyg4(T value, long duration) {
        return new TimedValue<>(value, duration, null);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TimedValue)) {
            return false;
        }
        TimedValue timedValue = (TimedValue) other;
        return Intrinsics.areEqual(this.value, timedValue.value) && Duration.m1569equalsimpl0(this.duration, timedValue.duration);
    }

    public int hashCode() {
        T t = this.value;
        return ((t == null ? 0 : t.hashCode()) * 31) + Duration.m1585hashCodeimpl(this.duration);
    }

    public String toString() {
        return "TimedValue(value=" + this.value + ", duration=" + ((Object) Duration.m1604toStringimpl(this.duration)) + ')';
    }

    private TimedValue(T t, long j) {
        this.value = t;
        this.duration = j;
    }

    /* JADX INFO: renamed from: getDuration-UwyO8pc, reason: not valid java name */
    public final long m1695getDurationUwyO8pc() {
        return this.duration;
    }

    public final T getValue() {
        return this.value;
    }
}
