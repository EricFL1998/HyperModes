package miuix.animation;

import miuix.animation.motion.Motion;
import miuix.animation.physics.FactorOperator;
import miuix.animation.utils.EaseManager;

/* JADX INFO: loaded from: classes2.dex */
public interface FolmeEase {
    public static final String ACCELERATE = "accelerate";
    public static final String ACCELERATE_DECELERATE = "accelerateDecelerate";
    public static final String ACCELERATE_INTERPOLATOR = "accelerateInterpolator";
    public static final String BEZIER = "bezier";
    public static final String BOUNCE = "bounce";
    public static final String BOUNCE_EASE_IN = "bounceEaseIn";
    public static final String BOUNCE_EASE_INOUT = "bounceEaseInOut";
    public static final String BOUNCE_EASE_OUT = "bounceEaseOut";
    public static final String CUBIC_IN = "cubicIn";
    public static final String CUBIC_INOUT = "cubicInOut";
    public static final String CUBIC_OUT = "cubicOut";
    public static final String DAMPING = "damping";
    public static final String DECELERATE = "decelerate";
    public static final String DURATION = "duration";
    public static final String EXPO_IN = "expoIn";
    public static final String EXPO_INOUT = "expoInOut";
    public static final String EXPO_OUT = "expoOut";
    public static final String FRICTION = "friction";
    public static final String LINEAR = "linear";
    public static final String PERLIN = "perlin";
    public static final String PERLIN2 = "perlin2";
    public static final String QUAD_IN = "quadIn";
    public static final String QUAD_INOUT = "quadInOut";
    public static final String QUAD_OUT = "quadOut";
    public static final String QUART_IN = "quartIn";
    public static final String QUART_INOUT = "quartInOut";
    public static final String QUART_OUT = "quartOut";
    public static final String QUINT_IN = "quintIn";
    public static final String QUINT_INOUT = "quintInOut";
    public static final String QUINT_OUT = "quintOut";
    public static final String SINE_IN = "sineIn";
    public static final String SINE_INOUT = "sineInOut";
    public static final String SINE_OUT = "sineOut";
    public static final String SPRING = "spring";
    public static final String SPRING_FUNCTION = "functionSpring";
    public static final String SPRING_GRAVITY = "springGravity";
    public static final String SPRING_LEGACY = "classicSpring";

    Motion newMotion();

    static EaseManager.EaseStyle friction(float f) {
        return EaseManager.getStyle(-4, f);
    }

    static EaseManager.EaseStyle friction(FactorOperator factorOperator) {
        return EaseManager.getStyle(-4, factorOperator);
    }

    static EaseManager.EaseStyle accelerate(float f) {
        return EaseManager.getStyle(-3, f);
    }

    static EaseManager.EaseStyle accelerate(FactorOperator factorOperator) {
        return EaseManager.getStyle(-3, factorOperator);
    }

    static EaseManager.EaseStyle spring(float f, float f2) {
        return spring(f, f2, 1.0f);
    }

    static EaseManager.EaseStyle spring(float f, float f2, float f3) {
        return EaseManager.getStyle(-2, f, f2, f3);
    }

    static EaseManager.EaseStyle spring(FactorOperator factorOperator, FactorOperator factorOperator2, FactorOperator factorOperator3) {
        return EaseManager.getStyle(-2, factorOperator, factorOperator2, factorOperator3);
    }

    static EaseManager.EaseStyle springLegacy(float f, float f2, float f3) {
        return EaseManager.getStyle(0, f, f2, f3);
    }

    static EaseManager.EaseStyle springFunction(float f, float f2) {
        return EaseManager.getStyle(102, f, f2);
    }

    static EaseManager.EaseStyle bezier(long j, float f, float f2, float f3, float f4) {
        return EaseManager.getStyle(100, j, f, f2, f3, f4);
    }

    static EaseManager.EaseStyle bezier(float f, float f2, float f3, float f4, long j) {
        return EaseManager.getStyle(100, j, f, f2, f3, f4);
    }

    static EaseManager.EaseStyle linear(long j) {
        return EaseManager.getStyle(1, j);
    }

    static EaseManager.EaseStyle quadIn(long j) {
        return EaseManager.getStyle(2, j);
    }

    static EaseManager.EaseStyle quadOut(long j) {
        return EaseManager.getStyle(3, j);
    }

    static EaseManager.EaseStyle quadInOut(long j) {
        return EaseManager.getStyle(4, j);
    }

    static EaseManager.EaseStyle cubicIn(long j) {
        return EaseManager.getStyle(5, j);
    }

    static EaseManager.EaseStyle cubicOut(long j) {
        return EaseManager.getStyle(6, j);
    }

    static EaseManager.EaseStyle cubicInOut(long j) {
        return EaseManager.getStyle(7, j);
    }

    static EaseManager.EaseStyle quartIn(long j) {
        return EaseManager.getStyle(8, j);
    }

    static EaseManager.EaseStyle quartOut(long j) {
        return EaseManager.getStyle(9, j);
    }

    static EaseManager.EaseStyle quartInOut(long j) {
        return EaseManager.getStyle(10, j);
    }

    static EaseManager.EaseStyle quintIn(long j) {
        return EaseManager.getStyle(11, j);
    }

    static EaseManager.EaseStyle quintOut(long j) {
        return EaseManager.getStyle(12, j);
    }

    static EaseManager.EaseStyle quintInOut(long j) {
        return EaseManager.getStyle(13, j);
    }

    static EaseManager.EaseStyle sinIn(long j) {
        return sineIn(j);
    }

    static EaseManager.EaseStyle sineIn(long j) {
        return EaseManager.getStyle(14, j);
    }

    static EaseManager.EaseStyle sinOut(long j) {
        return sineOut(j);
    }

    static EaseManager.EaseStyle sineOut(long j) {
        return EaseManager.getStyle(15, j);
    }

    static EaseManager.EaseStyle sinInOut(long j) {
        return sineInOut(j);
    }

    static EaseManager.EaseStyle sineInOut(long j) {
        return EaseManager.getStyle(16, j);
    }

    static EaseManager.EaseStyle expoIn(long j) {
        return EaseManager.getStyle(17, j);
    }

    static EaseManager.EaseStyle expoOut(long j) {
        return EaseManager.getStyle(18, j);
    }

    static EaseManager.EaseStyle expoInOut(long j) {
        return EaseManager.getStyle(19, j);
    }

    static EaseManager.EaseStyle decelerate(float f, long j) {
        return EaseManager.getStyle(20, j, f);
    }

    static EaseManager.EaseStyle decelerate(long j) {
        return EaseManager.getStyle(20, j);
    }

    static EaseManager.EaseStyle accelerateDecelerate(long j) {
        return EaseManager.getStyle(21, j);
    }

    static EaseManager.EaseStyle accelerateInterpolator(float f, long j) {
        return EaseManager.getStyle(22, j, f);
    }

    static EaseManager.EaseStyle accelerateInterpolator(long j) {
        return EaseManager.getStyle(22, j);
    }

    static EaseManager.EaseStyle bounce(long j) {
        return EaseManager.getStyle(23, j);
    }

    static EaseManager.EaseStyle bounceEaseIn(long j) {
        return EaseManager.getStyle(24, j);
    }

    static EaseManager.EaseStyle bounceEaseOut(long j) {
        return EaseManager.getStyle(25, j);
    }

    static EaseManager.EaseStyle bounceEaseInOut(long j) {
        return EaseManager.getStyle(26, j);
    }

    static int getStyleDef(String str) {
        if (str.equals(SPRING)) {
            return -2;
        }
        if (str.equals(SPRING_GRAVITY)) {
            return 101;
        }
        if (str.equals(ACCELERATE)) {
            return -3;
        }
        if (str.equals(FRICTION)) {
            return -4;
        }
        if (str.equals(SPRING_LEGACY)) {
            return 0;
        }
        if (str.equals(SPRING_FUNCTION)) {
            return 102;
        }
        if (str.equals("duration")) {
            return -1;
        }
        if (str.equals(LINEAR)) {
            return 1;
        }
        if (str.equals(QUAD_IN)) {
            return 2;
        }
        if (str.equals(QUAD_OUT)) {
            return 3;
        }
        if (str.equals(QUAD_INOUT)) {
            return 4;
        }
        if (str.equals(CUBIC_IN)) {
            return 5;
        }
        if (str.equals(CUBIC_OUT)) {
            return 6;
        }
        if (str.equals(CUBIC_INOUT)) {
            return 7;
        }
        if (str.equals(QUART_IN)) {
            return 8;
        }
        if (str.equals(QUART_OUT)) {
            return 9;
        }
        if (str.equals(QUART_INOUT)) {
            return 10;
        }
        if (str.equals(QUINT_IN)) {
            return 11;
        }
        if (str.equals(QUINT_OUT)) {
            return 12;
        }
        if (str.equals(QUINT_INOUT)) {
            return 13;
        }
        if (str.equals(SINE_IN)) {
            return 14;
        }
        if (str.equals(SINE_OUT)) {
            return 15;
        }
        if (str.equals(SINE_INOUT)) {
            return 16;
        }
        if (str.equals(EXPO_IN)) {
            return 17;
        }
        if (str.equals(EXPO_OUT)) {
            return 18;
        }
        if (str.equals(EXPO_INOUT)) {
            return 19;
        }
        if (str.equals(DECELERATE)) {
            return 20;
        }
        if (str.equals(ACCELERATE_INTERPOLATOR)) {
            return 22;
        }
        if (str.equals(ACCELERATE_DECELERATE)) {
            return 21;
        }
        if (str.equals(BOUNCE)) {
            return 23;
        }
        if (str.equals(BOUNCE_EASE_IN)) {
            return 24;
        }
        if (str.equals(BOUNCE_EASE_OUT)) {
            return 25;
        }
        return str.equals(BOUNCE_EASE_INOUT) ? 26 : 0;
    }

    static String getStyleName(int i) {
        if (i == 100) {
            return BEZIER;
        }
        if (i == 102) {
            return SPRING_FUNCTION;
        }
        if (i == 103) {
            return DAMPING;
        }
        if (i == 200) {
            return PERLIN2;
        }
        if (i != 201) {
            switch (i) {
                case -4:
                    return FRICTION;
                case -3:
                    return ACCELERATE;
                case -2:
                    return SPRING;
                case -1:
                    return "duration";
                case 0:
                    return SPRING_LEGACY;
                case 1:
                    return LINEAR;
                case 2:
                    return QUAD_IN;
                case 3:
                    return QUAD_OUT;
                case 4:
                    return QUAD_INOUT;
                case 5:
                    return CUBIC_IN;
                case 6:
                    return CUBIC_OUT;
                case 7:
                    return CUBIC_INOUT;
                case 8:
                    return QUART_IN;
                case 9:
                    return QUART_OUT;
                case 10:
                    return QUART_INOUT;
                case 11:
                    return QUINT_IN;
                case 12:
                    return QUINT_OUT;
                case 13:
                    return QUINT_INOUT;
                case 14:
                    return SINE_IN;
                case 15:
                    return SINE_OUT;
                case 16:
                    return SINE_INOUT;
                case 17:
                    return EXPO_IN;
                case 18:
                    return EXPO_OUT;
                case 19:
                    return EXPO_INOUT;
                case 20:
                    return DECELERATE;
                case 21:
                    return ACCELERATE_DECELERATE;
                case 22:
                    return ACCELERATE_INTERPOLATOR;
                case 23:
                    return BOUNCE;
                case 24:
                    return BOUNCE_EASE_IN;
                case 25:
                    return BOUNCE_EASE_OUT;
                case 26:
                    return BOUNCE_EASE_INOUT;
                default:
                    return "UNKNOWN";
            }
        }
        return PERLIN;
    }

    static FolmeEase get(int i, double... dArr) {
        return EaseManager.getEasing(i, dArr);
    }
}
