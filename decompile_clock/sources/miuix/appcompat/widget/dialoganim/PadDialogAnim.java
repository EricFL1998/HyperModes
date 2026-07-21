package miuix.appcompat.widget.dialoganim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import java.lang.ref.WeakReference;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.property.ViewProperty;
import miuix.animation.utils.EaseManager;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.widget.DialogAnimHelper;
import miuix.core.util.MiuixTraceUtils;
import miuix.internal.util.AnimHelper;

/* JADX INFO: loaded from: classes2.dex */
public class PadDialogAnim implements IDialogAnim {
    private static final float DAMPING_SHOW = 0.8f;
    private static final int DISMISS_DURATION = 200;
    private static final float RESPONSE_SHOW = 0.3f;
    private static final float SCALE_FACTOR = 30.0f;
    private static final float SCALE_FULL = 1.0f;
    private static final float SCALE_SMALl = 0.8f;
    private static final String TAG = "PhoneDialogAnim";
    private static final String TAG_HIDE = "hide";
    private static final String TAG_SHOW = "show";

    @Override // miuix.appcompat.widget.dialoganim.IDialogAnim
    public void cancelAnimator() {
    }

    @Override // miuix.appcompat.widget.dialoganim.IDialogAnim
    public void executeDismissAnim(View view, View view2, DialogAnimHelper.OnDismiss onDismiss) {
        if ("hide".equals(view.getTag())) {
            return;
        }
        dismissPanel(view, new WeakRefDismissListener(onDismiss, view));
        DimAnimator.dismiss(view2);
    }

    private void dismissPanel(View view, WeakRefDismissListener weakRefDismissListener) {
        PropertyValuesHolder propertyValuesHolderOfFloat = PropertyValuesHolder.ofFloat(ViewProperty.ALPHA, 1.0f, 0.0f);
        float scale = getScale(view);
        ObjectAnimator objectAnimatorOfPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(view, propertyValuesHolderOfFloat, PropertyValuesHolder.ofFloat(ViewProperty.SCALE_X, 1.0f, scale), PropertyValuesHolder.ofFloat(ViewProperty.SCALE_Y, 1.0f, scale));
        objectAnimatorOfPropertyValuesHolder.setInterpolator(new DecelerateInterpolator(1.5f));
        objectAnimatorOfPropertyValuesHolder.addListener(weakRefDismissListener);
        objectAnimatorOfPropertyValuesHolder.setDuration(200L);
        objectAnimatorOfPropertyValuesHolder.start();
    }

    @Override // miuix.appcompat.widget.dialoganim.IDialogAnim
    public void executeShowAnim(View view, View view2, boolean z, AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener) {
        if ("show".equals(view.getTag())) {
            return;
        }
        if (view.getScaleX() != 1.0f) {
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
        }
        if (AnimHelper.isDialogDebugInAndroidUIThreadEnabled()) {
            executeShowAnimAndroidUIThread(view, onDialogShowAnimListener);
        } else {
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase(EaseManager.getStyle(-2, 0.8f, 0.3f));
            animConfig.addListeners(new WeakRefShowListener(onDialogShowAnimListener, view));
            Folme.useAt(view).state().setFlags(1L).fromTo(getState(true, true, view), getState(true, false, view), animConfig);
        }
        DimAnimator.show(view2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v3 */
    private AnimState getState(boolean z, boolean z2, View view) {
        AnimState animState = new AnimState();
        float scale = 1.0f;
        if (z) {
            if (z2 != 0) {
                scale = getScale(view);
            }
        } else if (z2 == 0) {
            scale = getScale(view);
        }
        if (z) {
            z2 = !z2;
        }
        double d = scale;
        animState.add(ViewProperty.SCALE_X, d);
        animState.add(ViewProperty.SCALE_Y, d);
        animState.add(ViewProperty.ALPHA, z2);
        return animState;
    }

    private float getScale(View view) {
        return Math.max(0.8f, 1.0f - (60.0f / Math.max(view.getWidth(), view.getHeight())));
    }

    private void executeShowAnimAndroidUIThread(View view, AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener) {
        PropertyValuesHolder propertyValuesHolderOfFloat = PropertyValuesHolder.ofFloat(ViewProperty.ALPHA, 0.0f, 1.0f);
        float scale = getScale(view);
        ObjectAnimator objectAnimatorOfPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(view, propertyValuesHolderOfFloat, PropertyValuesHolder.ofFloat(ViewProperty.SCALE_X, scale, 1.0f), PropertyValuesHolder.ofFloat(ViewProperty.SCALE_Y, scale, 1.0f));
        objectAnimatorOfPropertyValuesHolder.setInterpolator(new DecelerateInterpolator(1.5f));
        objectAnimatorOfPropertyValuesHolder.addListener(new WeakRefShowOnAndroidUIListener(onDialogShowAnimListener, view));
        objectAnimatorOfPropertyValuesHolder.setDuration(300L);
        objectAnimatorOfPropertyValuesHolder.start();
    }

    private class WeakRefShowOnAndroidUIListener extends AnimatorListenerAdapter {
        WeakReference<AlertDialog.OnDialogShowAnimListener> mOnDismiss;
        int mTraceCookie = MiuixTraceUtils.generateUniqueCookie();
        WeakReference<View> mView;

        WeakRefShowOnAndroidUIListener(AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener, View view) {
            this.mOnDismiss = new WeakReference<>(onDialogShowAnimListener);
            this.mView = new WeakReference<>(view);
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            super.onAnimationStart(animator);
            MiuixTraceUtils.beginAsyncTrace(MiuixTraceUtils.ANIM_TRACE_TAG, this.mTraceCookie);
            View view = this.mView.get();
            if (view != null) {
                view.setTag("show");
            }
            AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener = this.mOnDismiss.get();
            if (onDialogShowAnimListener != null) {
                onDialogShowAnimListener.onShowAnimStart();
            }
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            super.onAnimationEnd(animator);
            View view = this.mView.get();
            if (view != null) {
                view.setTag(null);
            }
            AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener = this.mOnDismiss.get();
            if (onDialogShowAnimListener != null) {
                onDialogShowAnimListener.onShowAnimComplete();
            }
            MiuixTraceUtils.endAsyncTrace(MiuixTraceUtils.ANIM_TRACE_TAG, this.mTraceCookie);
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
            super.onAnimationCancel(animator);
            MiuixTraceUtils.endAsyncTrace(MiuixTraceUtils.ANIM_TRACE_TAG, this.mTraceCookie);
        }
    }

    class WeakRefDismissListener implements Animator.AnimatorListener {
        WeakReference<DialogAnimHelper.OnDismiss> mOnDismiss;
        int mTraceCookie = MiuixTraceUtils.generateUniqueCookie();
        WeakReference<View> mView;

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        WeakRefDismissListener(DialogAnimHelper.OnDismiss onDismiss, View view) {
            this.mOnDismiss = new WeakReference<>(onDismiss);
            this.mView = new WeakReference<>(view);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            MiuixTraceUtils.beginAsyncTrace(MiuixTraceUtils.ANIM_TRACE_TAG, this.mTraceCookie);
            View view = this.mView.get();
            if (view != null) {
                view.setTag("hide");
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            View view = this.mView.get();
            if (view != null) {
                view.setTag(null);
            }
            DialogAnimHelper.OnDismiss onDismiss = this.mOnDismiss.get();
            if (onDismiss != null) {
                onDismiss.end();
            } else {
                Log.d(PadDialogAnim.TAG, "weak dismiss onComplete mOnDismiss get null");
            }
            MiuixTraceUtils.endAsyncTrace(MiuixTraceUtils.ANIM_TRACE_TAG, this.mTraceCookie);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
            View view = this.mView.get();
            if (view != null) {
                view.setTag(null);
            }
            DialogAnimHelper.OnDismiss onDismiss = this.mOnDismiss.get();
            if (onDismiss != null) {
                onDismiss.end();
            } else {
                Log.d(PadDialogAnim.TAG, "weak dismiss onCancel mOnDismiss get null");
            }
            MiuixTraceUtils.endAsyncTrace(MiuixTraceUtils.ANIM_TRACE_TAG, this.mTraceCookie);
        }
    }

    class WeakRefShowListener extends TransitionListener {
        WeakReference<AlertDialog.OnDialogShowAnimListener> mShowDismiss;
        int mTraceCookie = MiuixTraceUtils.generateUniqueCookie();
        WeakReference<View> mView;

        WeakRefShowListener(AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener, View view) {
            this.mShowDismiss = new WeakReference<>(onDialogShowAnimListener);
            this.mView = new WeakReference<>(view);
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj) {
            super.onComplete(obj);
            AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener = this.mShowDismiss.get();
            if (onDialogShowAnimListener != null) {
                onDialogShowAnimListener.onShowAnimComplete();
            } else {
                Log.d(PadDialogAnim.TAG, "weak show onComplete mOnDismiss get null");
            }
            MiuixTraceUtils.endAsyncTrace(MiuixTraceUtils.ANIM_TRACE_TAG, this.mTraceCookie);
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onBegin(Object obj) {
            super.onBegin(obj);
            MiuixTraceUtils.beginAsyncTrace(MiuixTraceUtils.ANIM_TRACE_TAG, this.mTraceCookie);
            View view = this.mView.get();
            if (view != null) {
                view.setTag("show");
            }
            AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener = this.mShowDismiss.get();
            if (onDialogShowAnimListener != null) {
                onDialogShowAnimListener.onShowAnimStart();
            } else {
                Log.d(PadDialogAnim.TAG, "weak show onCancel mOnDismiss get null");
            }
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onCancel(Object obj) {
            super.onCancel(obj);
            MiuixTraceUtils.endAsyncTrace(MiuixTraceUtils.ANIM_TRACE_TAG, this.mTraceCookie);
        }
    }
}
