package miuix.animation.physics;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.view.Choreographer;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class AnimationHandler {
    private static final long FRAME_DELAY_MS = 10;
    public static final ThreadLocal<AnimationHandler> sAnimatorHandler = new ThreadLocal<>();
    private AnimationFrameCallbackProvider mProvider;
    private final ArrayMap<AnimationFrameCallback, Long> mDelayedCallbackStartTime = new ArrayMap<>();
    private final ArrayList<AnimationFrameCallback> mAnimationCallbacks = new ArrayList<>();
    private final AnimationCallbackDispatcher mCallbackDispatcher = new AnimationCallbackDispatcher();
    private long mCurrentFrameTime = 0;
    private boolean mListDirty = false;

    public interface AnimationFrameCallback {
        boolean doAnimationFrame(long j);
    }

    class AnimationCallbackDispatcher {
        AnimationCallbackDispatcher() {
        }

        void dispatchAnimationFrame(long j) {
            AnimationHandler.this.doAnimationFrame(j);
            if (AnimationHandler.this.mAnimationCallbacks.size() > 0) {
                AnimationHandler.this.getProvider().postFrameCallback();
            }
        }
    }

    public static AnimationHandler getInstance() {
        ThreadLocal<AnimationHandler> threadLocal = sAnimatorHandler;
        if (threadLocal.get() == null) {
            threadLocal.set(new AnimationHandler());
        }
        return threadLocal.get();
    }

    public static long getFrameTime() {
        ThreadLocal<AnimationHandler> threadLocal = sAnimatorHandler;
        if (threadLocal.get() == null) {
            return 0L;
        }
        return threadLocal.get().mCurrentFrameTime;
    }

    public void setProvider(AnimationFrameCallbackProvider animationFrameCallbackProvider) {
        this.mProvider = animationFrameCallbackProvider;
    }

    public void postVsyncCallback() {
        getProvider().postVsyncCallback();
    }

    public void recreateProvider() {
        if (Build.VERSION.SDK_INT >= 33) {
            this.mProvider = new FrameCallbackProvider33(this.mCallbackDispatcher);
        } else {
            this.mProvider = new FrameCallbackProvider16(this.mCallbackDispatcher);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AnimationFrameCallbackProvider getProvider() {
        if (this.mProvider == null) {
            if (Build.VERSION.SDK_INT >= 33) {
                this.mProvider = new FrameCallbackProvider33(this.mCallbackDispatcher);
            } else {
                this.mProvider = new FrameCallbackProvider16(this.mCallbackDispatcher);
            }
        }
        return this.mProvider;
    }

    public void addAnimationFrameCallback(AnimationFrameCallback animationFrameCallback, long j) {
        if (this.mAnimationCallbacks.size() == 0) {
            getProvider().postFrameCallback();
        }
        if (!this.mAnimationCallbacks.contains(animationFrameCallback)) {
            this.mAnimationCallbacks.add(animationFrameCallback);
        }
        if (j > 0) {
            this.mDelayedCallbackStartTime.put(animationFrameCallback, Long.valueOf(SystemClock.uptimeMillis() + j));
        }
    }

    public void removeCallback(AnimationFrameCallback animationFrameCallback) {
        this.mDelayedCallbackStartTime.remove(animationFrameCallback);
        int iIndexOf = this.mAnimationCallbacks.indexOf(animationFrameCallback);
        if (iIndexOf >= 0) {
            this.mAnimationCallbacks.set(iIndexOf, null);
            this.mListDirty = true;
        }
    }

    public Looper getLooper() {
        return getProvider().getLooper();
    }

    boolean isCurrentThread() {
        return getProvider().isCurrentThread();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doAnimationFrame(long j) {
        long jUptimeMillis = SystemClock.uptimeMillis();
        for (int i = 0; i < this.mAnimationCallbacks.size(); i++) {
            AnimationFrameCallback animationFrameCallback = this.mAnimationCallbacks.get(i);
            if (animationFrameCallback != null && isCallbackDue(animationFrameCallback, jUptimeMillis)) {
                animationFrameCallback.doAnimationFrame(j);
            }
        }
        cleanUpList();
    }

    private boolean isCallbackDue(AnimationFrameCallback animationFrameCallback, long j) {
        Long l = this.mDelayedCallbackStartTime.get(animationFrameCallback);
        if (l == null) {
            return true;
        }
        if (l.longValue() >= j) {
            return false;
        }
        this.mDelayedCallbackStartTime.remove(animationFrameCallback);
        return true;
    }

    private void cleanUpList() {
        if (this.mListDirty) {
            for (int size = this.mAnimationCallbacks.size() - 1; size >= 0; size--) {
                if (this.mAnimationCallbacks.get(size) == null) {
                    this.mAnimationCallbacks.remove(size);
                }
            }
            this.mListDirty = false;
        }
    }

    public long getFrameDeltaNanos() {
        return getProvider().getFrameDeltaNanos();
    }

    private static class FrameCallbackProvider33 extends AnimationFrameCallbackProvider {
        private final Choreographer mChoreographer;
        private final Choreographer.FrameCallback mChoreographerCallback;
        private long mFrameDeltaNanos;
        private final Looper mLooper;
        private final Choreographer.VsyncCallback mVsyncCallback;

        FrameCallbackProvider33(AnimationCallbackDispatcher animationCallbackDispatcher) {
            super(animationCallbackDispatcher);
            this.mChoreographer = Choreographer.getInstance();
            this.mLooper = Looper.myLooper();
            this.mFrameDeltaNanos = 0L;
            this.mVsyncCallback = new Choreographer.VsyncCallback() { // from class: miuix.animation.physics.AnimationHandler.FrameCallbackProvider33.1
                @Override // android.view.Choreographer.VsyncCallback
                public void onVsync(Choreographer.FrameData frameData) {
                    Choreographer.FrameTimeline[] frameTimelines = frameData.getFrameTimelines();
                    int length = frameTimelines.length;
                    if (length > 1) {
                        int i = length - 1;
                        FrameCallbackProvider33.this.mFrameDeltaNanos = Math.round(((frameTimelines[i].getExpectedPresentationTimeNanos() - frameTimelines[0].getExpectedPresentationTimeNanos()) * 1.0d) / ((double) i));
                    }
                }
            };
            this.mChoreographerCallback = new Choreographer.FrameCallback() { // from class: miuix.animation.physics.AnimationHandler.FrameCallbackProvider33.2
                @Override // android.view.Choreographer.FrameCallback
                public void doFrame(long j) {
                    FrameCallbackProvider33.this.mDispatcher.dispatchAnimationFrame(j);
                }
            };
        }

        @Override // miuix.animation.physics.AnimationHandler.AnimationFrameCallbackProvider
        void postFrameCallback() {
            this.mChoreographer.postVsyncCallback(this.mVsyncCallback);
            this.mChoreographer.postFrameCallback(this.mChoreographerCallback);
        }

        @Override // miuix.animation.physics.AnimationHandler.AnimationFrameCallbackProvider
        public void postVsyncCallback() {
            this.mChoreographer.postVsyncCallback(this.mVsyncCallback);
        }

        @Override // miuix.animation.physics.AnimationHandler.AnimationFrameCallbackProvider
        boolean isCurrentThread() {
            return Thread.currentThread() == this.mLooper.getThread();
        }

        @Override // miuix.animation.physics.AnimationHandler.AnimationFrameCallbackProvider
        Looper getLooper() {
            return this.mLooper;
        }

        @Override // miuix.animation.physics.AnimationHandler.AnimationFrameCallbackProvider
        long getFrameDeltaNanos() {
            return this.mFrameDeltaNanos;
        }
    }

    private static class FrameCallbackProvider16 extends AnimationFrameCallbackProvider {
        private final Choreographer mChoreographer;
        private final Choreographer.FrameCallback mChoreographerCallback;
        private final Looper mLooper;

        FrameCallbackProvider16(AnimationCallbackDispatcher animationCallbackDispatcher) {
            super(animationCallbackDispatcher);
            this.mChoreographer = Choreographer.getInstance();
            this.mLooper = Looper.myLooper();
            this.mChoreographerCallback = new Choreographer.FrameCallback() { // from class: miuix.animation.physics.AnimationHandler.FrameCallbackProvider16.1
                @Override // android.view.Choreographer.FrameCallback
                public void doFrame(long j) {
                    FrameCallbackProvider16.this.mDispatcher.dispatchAnimationFrame(j);
                }
            };
        }

        @Override // miuix.animation.physics.AnimationHandler.AnimationFrameCallbackProvider
        void postFrameCallback() {
            this.mChoreographer.postFrameCallback(this.mChoreographerCallback);
        }

        @Override // miuix.animation.physics.AnimationHandler.AnimationFrameCallbackProvider
        boolean isCurrentThread() {
            return Thread.currentThread() == this.mLooper.getThread();
        }

        @Override // miuix.animation.physics.AnimationHandler.AnimationFrameCallbackProvider
        Looper getLooper() {
            return this.mLooper;
        }
    }

    @Deprecated
    private static class FrameCallbackProvider14 extends AnimationFrameCallbackProvider {
        private final Handler mHandler;
        private long mLastFrameTime;
        private final Runnable mRunnable;

        FrameCallbackProvider14(AnimationCallbackDispatcher animationCallbackDispatcher) {
            super(animationCallbackDispatcher);
            this.mLastFrameTime = -1L;
            this.mRunnable = new Runnable() { // from class: miuix.animation.physics.AnimationHandler.FrameCallbackProvider14.1
                @Override // java.lang.Runnable
                public void run() {
                    FrameCallbackProvider14.this.mLastFrameTime = SystemClock.uptimeMillis();
                    FrameCallbackProvider14.this.mDispatcher.dispatchAnimationFrame(FrameCallbackProvider14.this.mLastFrameTime);
                }
            };
            this.mHandler = new Handler(Looper.myLooper());
        }

        @Override // miuix.animation.physics.AnimationHandler.AnimationFrameCallbackProvider
        void postFrameCallback() {
            this.mHandler.postDelayed(this.mRunnable, Math.max(AnimationHandler.FRAME_DELAY_MS - (SystemClock.uptimeMillis() - this.mLastFrameTime), 0L));
        }

        @Override // miuix.animation.physics.AnimationHandler.AnimationFrameCallbackProvider
        boolean isCurrentThread() {
            return Thread.currentThread() == this.mHandler.getLooper().getThread();
        }

        @Override // miuix.animation.physics.AnimationHandler.AnimationFrameCallbackProvider
        Looper getLooper() {
            return this.mHandler.getLooper();
        }
    }

    static abstract class AnimationFrameCallbackProvider {
        final AnimationCallbackDispatcher mDispatcher;

        long getFrameDeltaNanos() {
            return 0L;
        }

        abstract Looper getLooper();

        abstract boolean isCurrentThread();

        abstract void postFrameCallback();

        void postVsyncCallback() {
        }

        AnimationFrameCallbackProvider(AnimationCallbackDispatcher animationCallbackDispatcher) {
            this.mDispatcher = animationCallbackDispatcher;
        }
    }
}
