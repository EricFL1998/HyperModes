package miuix.theme.token;

/* JADX INFO: loaded from: classes3.dex */
public class BlurToken {
    private BlurToken() {
    }

    @Deprecated
    public static class BlendColor {

        public static class Dark {
            public static final int[] DEFAULT = {1970500467, -1977211354};
            public static final int[] EXTRA_HEAVY = {1970500467, -1979711488, 184549375};
            public static final int[] HEAVY = {-2141430692, -1088479457};
            public static final int[] LIGHT = {1636469386, 1296187970};
            public static final int[] EXTRA_LIGHT = {1303227821, 862019937};
        }

        public static class Light {
            public static final int[] DEFAULT = {-1889443744, -1544359182};
            public static final int[] EXTRA_HEAVY = {-1889443744, -1543503873};
            public static final int[] HEAVY = {-1502909589, -856295947};
            public static final int[] LIGHT = {-2057807784, 1088676835};
            public static final int[] EXTRA_LIGHT = {-2142417587, 651811289};
        }

        private BlendColor() {
        }
    }

    @Deprecated
    public static class BlendMode {

        public static class Dark {
            public static final int[] DEFAULT = {19, 3, 3};
        }

        public static class Light {
            public static final int[] DEFAULT = {18, 3};
        }

        private BlendMode() {
        }
    }

    public static class Effect {

        @Deprecated
        public static final int DEFAULT = 66;

        @Deprecated
        public static final int EXTRA_THIN = 30;
        public static final int FOLLOW_BG = -1;

        @Deprecated
        public static final int HEAVY = 74;
        public static final int L = 70;
        public static final int M = 40;
        public static final int S = 30;

        @Deprecated
        public static final int THIN = 52;
        public static final int XL = 100;

        private Effect() {
        }
    }

    public static class ContainerMode {
        public static final int ENABLE = 1;
        public static final int NONE = 0;
        public static final int ONLY_BLUR = 2;

        private ContainerMode() {
        }
    }

    public static class ElementMode {
        public static final int ENABLE = 1;
        public static final int ENABLE_ANY_SHAPE = 2;
        public static final int ENABLE_SELF = 3;
        public static final int ENABLE_SELF_LIGHT_BLEND = 4;
        public static final int NONE = 0;

        private ElementMode() {
        }
    }

    public static class Type {
        public static final int GAUSS = 0;
        public static final int GRADIENT = 2;
        public static final int KAWASSE = 1;

        private Type() {
        }
    }

    public static class GradientType {
        public static final int LINEAR = 1;
        public static final int MULTI_POINTS = 2;
        public static final int NONE = 0;

        private GradientType() {
        }
    }
}
