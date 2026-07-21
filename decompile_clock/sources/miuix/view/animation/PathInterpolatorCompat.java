package miuix.view.animation;

import android.graphics.Path;
import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes3.dex */
public final class PathInterpolatorCompat {
    private PathInterpolatorCompat() {
    }

    public static Interpolator create(Path path) {
        return Api26Impl.createPathInterpolator(path);
    }

    public static Interpolator create(float f, float f2) {
        return Api26Impl.createPathInterpolator(f, f2);
    }

    public static Interpolator create(float f, float f2, float f3, float f4) {
        return Api26Impl.createPathInterpolator(f, f2, f3, f4);
    }

    static class Api26Impl {
        private Api26Impl() {
        }

        static PathInterpolator createPathInterpolator(Path path) {
            return new PathInterpolator(path);
        }

        static PathInterpolator createPathInterpolator(float f, float f2) {
            return new PathInterpolator(f, f2);
        }

        static PathInterpolator createPathInterpolator(float f, float f2, float f3, float f4) {
            return new PathInterpolator(f, f2, f3, f4);
        }
    }
}
