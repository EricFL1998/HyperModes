package miuix.view.animation;

/* JADX INFO: loaded from: classes3.dex */
public class AnimationUtils extends android.view.animation.AnimationUtils {
    private static ThreadLocal<AnimationNanoState> sAnimationNanoState = new ThreadLocal<AnimationNanoState>() { // from class: miuix.view.animation.AnimationUtils.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public AnimationNanoState initialValue() {
            return new AnimationNanoState();
        }
    };

    private static class AnimationNanoState {
        long lastReportedTimeNanos;

        private AnimationNanoState() {
        }
    }

    public static long currentAnimationTimeNanos() {
        AnimationNanoState animationNanoState = sAnimationNanoState.get();
        animationNanoState.lastReportedTimeNanos = System.nanoTime();
        return animationNanoState.lastReportedTimeNanos;
    }
}
