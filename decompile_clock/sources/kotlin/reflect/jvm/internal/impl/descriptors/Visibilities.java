package kotlin.reflect.jvm.internal.impl.descriptors;

import androidx.core.os.EnvironmentCompat;
import java.util.Map;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Visibilities.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class Visibilities {
    private static final Public DEFAULT_VISIBILITY;
    public static final Visibilities INSTANCE = new Visibilities();
    private static final Map<Visibility, Integer> ORDERED_VISIBILITIES;

    /* JADX INFO: compiled from: Visibilities.kt */
    public static final class Private extends Visibility {
        public static final Private INSTANCE = new Private();

        private Private() {
            super("private", false);
        }
    }

    private Visibilities() {
    }

    /* JADX INFO: compiled from: Visibilities.kt */
    public static final class PrivateToThis extends Visibility {
        public static final PrivateToThis INSTANCE = new PrivateToThis();

        private PrivateToThis() {
            super("private_to_this", false);
        }

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.Visibility
        public String getInternalDisplayName() {
            return "private/*private to this*/";
        }
    }

    /* JADX INFO: compiled from: Visibilities.kt */
    public static final class Protected extends Visibility {
        public static final Protected INSTANCE = new Protected();

        private Protected() {
            super("protected", true);
        }
    }

    /* JADX INFO: compiled from: Visibilities.kt */
    public static final class Internal extends Visibility {
        public static final Internal INSTANCE = new Internal();

        private Internal() {
            super("internal", false);
        }
    }

    /* JADX INFO: compiled from: Visibilities.kt */
    public static final class Public extends Visibility {
        public static final Public INSTANCE = new Public();

        private Public() {
            super("public", true);
        }
    }

    /* JADX INFO: compiled from: Visibilities.kt */
    public static final class Local extends Visibility {
        public static final Local INSTANCE = new Local();

        private Local() {
            super("local", false);
        }
    }

    /* JADX INFO: compiled from: Visibilities.kt */
    public static final class Inherited extends Visibility {
        public static final Inherited INSTANCE = new Inherited();

        private Inherited() {
            super("inherited", false);
        }
    }

    /* JADX INFO: compiled from: Visibilities.kt */
    public static final class InvisibleFake extends Visibility {
        public static final InvisibleFake INSTANCE = new InvisibleFake();

        private InvisibleFake() {
            super("invisible_fake", false);
        }
    }

    /* JADX INFO: compiled from: Visibilities.kt */
    public static final class Unknown extends Visibility {
        public static final Unknown INSTANCE = new Unknown();

        private Unknown() {
            super(EnvironmentCompat.MEDIA_UNKNOWN, false);
        }
    }

    static {
        Map mapCreateMapBuilder = MapsKt.createMapBuilder();
        mapCreateMapBuilder.put(PrivateToThis.INSTANCE, 0);
        mapCreateMapBuilder.put(Private.INSTANCE, 0);
        mapCreateMapBuilder.put(Internal.INSTANCE, 1);
        mapCreateMapBuilder.put(Protected.INSTANCE, 1);
        mapCreateMapBuilder.put(Public.INSTANCE, 2);
        ORDERED_VISIBILITIES = MapsKt.build(mapCreateMapBuilder);
        DEFAULT_VISIBILITY = Public.INSTANCE;
    }

    public final Integer compareLocal$compiler_common(Visibility first, Visibility second) {
        Intrinsics.checkNotNullParameter(first, "first");
        Intrinsics.checkNotNullParameter(second, "second");
        if (first == second) {
            return 0;
        }
        Map<Visibility, Integer> map = ORDERED_VISIBILITIES;
        Integer num = map.get(first);
        Integer num2 = map.get(second);
        if (num == null || num2 == null || Intrinsics.areEqual(num, num2)) {
            return null;
        }
        return Integer.valueOf(num.intValue() - num2.intValue());
    }

    public final boolean isPrivate(Visibility visibility) {
        Intrinsics.checkNotNullParameter(visibility, "visibility");
        return visibility == Private.INSTANCE || visibility == PrivateToThis.INSTANCE;
    }
}
