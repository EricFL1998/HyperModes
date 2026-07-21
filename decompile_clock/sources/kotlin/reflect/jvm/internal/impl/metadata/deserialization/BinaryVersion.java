package kotlin.reflect.jvm.internal.impl.metadata.deserialization;

import androidx.core.os.EnvironmentCompat;
import java.util.ArrayList;
import java.util.List;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: BinaryVersion.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class BinaryVersion {
    public static final Companion Companion = new Companion(null);
    private final int major;
    private final int minor;
    private final int[] numbers;
    private final int patch;
    private final List<Integer> rest;

    public BinaryVersion(int... numbers) {
        List<Integer> listEmptyList;
        Intrinsics.checkNotNullParameter(numbers, "numbers");
        this.numbers = numbers;
        Integer orNull = ArraysKt.getOrNull(numbers, 0);
        this.major = orNull != null ? orNull.intValue() : -1;
        Integer orNull2 = ArraysKt.getOrNull(numbers, 1);
        this.minor = orNull2 != null ? orNull2.intValue() : -1;
        Integer orNull3 = ArraysKt.getOrNull(numbers, 2);
        this.patch = orNull3 != null ? orNull3.intValue() : -1;
        if (numbers.length > 3) {
            if (numbers.length > 1024) {
                throw new IllegalArgumentException("BinaryVersion with length more than 1024 are not supported. Provided length " + numbers.length + '.');
            }
            listEmptyList = CollectionsKt.toList(ArraysKt.asList(numbers).subList(3, numbers.length));
        } else {
            listEmptyList = CollectionsKt.emptyList();
        }
        this.rest = listEmptyList;
    }

    public final int getMajor() {
        return this.major;
    }

    public final int getMinor() {
        return this.minor;
    }

    public final int[] toArray() {
        return this.numbers;
    }

    protected final boolean isCompatibleTo(BinaryVersion ourVersion) {
        Intrinsics.checkNotNullParameter(ourVersion, "ourVersion");
        int i = this.major;
        if (i == 0) {
            if (ourVersion.major == 0 && this.minor == ourVersion.minor) {
                return true;
            }
        } else if (i == ourVersion.major && this.minor <= ourVersion.minor) {
            return true;
        }
        return false;
    }

    public final boolean isAtLeast(BinaryVersion version) {
        Intrinsics.checkNotNullParameter(version, "version");
        return isAtLeast(version.major, version.minor, version.patch);
    }

    public final boolean isAtLeast(int i, int i2, int i3) {
        int i4 = this.major;
        if (i4 > i) {
            return true;
        }
        if (i4 < i) {
            return false;
        }
        int i5 = this.minor;
        if (i5 > i2) {
            return true;
        }
        return i5 >= i2 && this.patch >= i3;
    }

    public final boolean isAtMost(int i, int i2, int i3) {
        int i4 = this.major;
        if (i4 < i) {
            return true;
        }
        if (i4 > i) {
            return false;
        }
        int i5 = this.minor;
        if (i5 < i2) {
            return true;
        }
        return i5 <= i2 && this.patch <= i3;
    }

    public String toString() {
        int[] array = toArray();
        ArrayList arrayList = new ArrayList();
        for (int i : array) {
            if (i == -1) {
                break;
            }
            arrayList.add(Integer.valueOf(i));
        }
        ArrayList arrayList2 = arrayList;
        return arrayList2.isEmpty() ? EnvironmentCompat.MEDIA_UNKNOWN : CollectionsKt.joinToString$default(arrayList2, ".", null, null, 0, null, null, 62, null);
    }

    public boolean equals(Object obj) {
        if (obj != null && Intrinsics.areEqual(getClass(), obj.getClass())) {
            BinaryVersion binaryVersion = (BinaryVersion) obj;
            if (this.major == binaryVersion.major && this.minor == binaryVersion.minor && this.patch == binaryVersion.patch && Intrinsics.areEqual(this.rest, binaryVersion.rest)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int i = this.major;
        int i2 = i + (i * 31) + this.minor;
        int i3 = i2 + (i2 * 31) + this.patch;
        return i3 + (i3 * 31) + this.rest.hashCode();
    }

    /* JADX INFO: compiled from: BinaryVersion.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
