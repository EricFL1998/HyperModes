package androidx.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.res.TypedArrayUtils;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ChangeBounds extends Transition {
    private static final Property<View, PointF> BOTTOM_RIGHT_ONLY_PROPERTY;
    private static final Property<ViewBounds, PointF> BOTTOM_RIGHT_PROPERTY;
    private static final Property<View, PointF> TOP_LEFT_ONLY_PROPERTY;
    private static final Property<ViewBounds, PointF> TOP_LEFT_PROPERTY;
    private boolean mResizeClip;
    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";
    private static final String PROPNAME_CLIP = "android:changeBounds:clip";
    private static final String PROPNAME_PARENT = "android:changeBounds:parent";
    private static final String PROPNAME_WINDOW_X = "android:changeBounds:windowX";
    private static final String PROPNAME_WINDOW_Y = "android:changeBounds:windowY";
    private static final String[] sTransitionProperties = {PROPNAME_BOUNDS, PROPNAME_CLIP, PROPNAME_PARENT, PROPNAME_WINDOW_X, PROPNAME_WINDOW_Y};
    private static final Property<View, PointF> POSITION_PROPERTY = new Property<View, PointF>(PointF.class, "position") { // from class: androidx.transition.ChangeBounds.5
        @Override // android.util.Property
        public PointF get(View view) {
            return null;
        }

        @Override // android.util.Property
        public void set(View view, PointF pointF) {
            int iRound = Math.round(pointF.x);
            int iRound2 = Math.round(pointF.y);
            ViewUtils.setLeftTopRightBottom(view, iRound, iRound2, view.getWidth() + iRound, view.getHeight() + iRound2);
        }
    };
    private static final RectEvaluator sRectEvaluator = new RectEvaluator();

    @Override // androidx.transition.Transition
    public boolean isSeekingSupported() {
        return true;
    }

    static {
        String str = "topLeft";
        TOP_LEFT_PROPERTY = new Property<ViewBounds, PointF>(PointF.class, str) { // from class: androidx.transition.ChangeBounds.1
            @Override // android.util.Property
            public PointF get(ViewBounds viewBounds) {
                return null;
            }

            @Override // android.util.Property
            public void set(ViewBounds viewBounds, PointF pointF) {
                viewBounds.setTopLeft(pointF);
            }
        };
        String str2 = "bottomRight";
        BOTTOM_RIGHT_PROPERTY = new Property<ViewBounds, PointF>(PointF.class, str2) { // from class: androidx.transition.ChangeBounds.2
            @Override // android.util.Property
            public PointF get(ViewBounds viewBounds) {
                return null;
            }

            @Override // android.util.Property
            public void set(ViewBounds viewBounds, PointF pointF) {
                viewBounds.setBottomRight(pointF);
            }
        };
        BOTTOM_RIGHT_ONLY_PROPERTY = new Property<View, PointF>(PointF.class, str2) { // from class: androidx.transition.ChangeBounds.3
            @Override // android.util.Property
            public PointF get(View view) {
                return null;
            }

            @Override // android.util.Property
            public void set(View view, PointF pointF) {
                ViewUtils.setLeftTopRightBottom(view, view.getLeft(), view.getTop(), Math.round(pointF.x), Math.round(pointF.y));
            }
        };
        TOP_LEFT_ONLY_PROPERTY = new Property<View, PointF>(PointF.class, str) { // from class: androidx.transition.ChangeBounds.4
            @Override // android.util.Property
            public PointF get(View view) {
                return null;
            }

            @Override // android.util.Property
            public void set(View view, PointF pointF) {
                ViewUtils.setLeftTopRightBottom(view, Math.round(pointF.x), Math.round(pointF.y), view.getRight(), view.getBottom());
            }
        };
    }

    public ChangeBounds() {
        this.mResizeClip = false;
    }

    public ChangeBounds(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mResizeClip = false;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, Styleable.CHANGE_BOUNDS);
        boolean namedBoolean = TypedArrayUtils.getNamedBoolean(typedArrayObtainStyledAttributes, (XmlResourceParser) attributeSet, "resizeClip", 0, false);
        typedArrayObtainStyledAttributes.recycle();
        setResizeClip(namedBoolean);
    }

    @Override // androidx.transition.Transition
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    public void setResizeClip(boolean z) {
        this.mResizeClip = z;
    }

    public boolean getResizeClip() {
        return this.mResizeClip;
    }

    private void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        if (!view.isLaidOut() && view.getWidth() == 0 && view.getHeight() == 0) {
            return;
        }
        transitionValues.values.put(PROPNAME_BOUNDS, new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
        transitionValues.values.put(PROPNAME_PARENT, transitionValues.view.getParent());
        if (this.mResizeClip) {
            transitionValues.values.put(PROPNAME_CLIP, view.getClipBounds());
        }
    }

    @Override // androidx.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        Rect rect;
        captureValues(transitionValues);
        if (!this.mResizeClip || (rect = (Rect) transitionValues.view.getTag(R.id.transition_clip)) == null) {
            return;
        }
        transitionValues.values.put(PROPNAME_CLIP, rect);
    }

    @Override // androidx.transition.Transition
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // androidx.transition.Transition
    public Animator createAnimator(ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
        int i;
        View view;
        int i2;
        ObjectAnimator objectAnimatorOfObject;
        Animator animatorMergeAnimators;
        if (transitionValues == null || transitionValues2 == null) {
            return null;
        }
        Map<String, Object> map = transitionValues.values;
        Map<String, Object> map2 = transitionValues2.values;
        ViewGroup viewGroup2 = (ViewGroup) map.get(PROPNAME_PARENT);
        ViewGroup viewGroup3 = (ViewGroup) map2.get(PROPNAME_PARENT);
        if (viewGroup2 == null || viewGroup3 == null) {
            return null;
        }
        View view2 = transitionValues2.view;
        Rect rect = (Rect) transitionValues.values.get(PROPNAME_BOUNDS);
        Rect rect2 = (Rect) transitionValues2.values.get(PROPNAME_BOUNDS);
        int i3 = rect.left;
        int i4 = rect2.left;
        int i5 = rect.top;
        int i6 = rect2.top;
        int i7 = rect.right;
        int i8 = rect2.right;
        int i9 = rect.bottom;
        int i10 = rect2.bottom;
        int i11 = i7 - i3;
        int i12 = i9 - i5;
        int i13 = i8 - i4;
        int i14 = i10 - i6;
        Rect rect3 = (Rect) transitionValues.values.get(PROPNAME_CLIP);
        Rect rect4 = (Rect) transitionValues2.values.get(PROPNAME_CLIP);
        if ((i11 == 0 || i12 == 0) && (i13 == 0 || i14 == 0)) {
            i = 0;
        } else {
            i = (i3 == i4 && i5 == i6) ? 0 : 1;
            if (i7 != i8 || i9 != i10) {
                i++;
            }
        }
        if ((rect3 != null && !rect3.equals(rect4)) || (rect3 == null && rect4 != null)) {
            i++;
        }
        int i15 = i;
        if (i15 <= 0) {
            return null;
        }
        if (!this.mResizeClip) {
            ViewUtils.setLeftTopRightBottom(view2, i3, i5, i7, i9);
            if (i15 == 2) {
                if (i11 == i13 && i12 == i14) {
                    animatorMergeAnimators = ObjectAnimatorUtils.ofPointF(view2, POSITION_PROPERTY, getPathMotion().getPath(i3, i5, i4, i6));
                } else {
                    ViewBounds viewBounds = new ViewBounds(view2);
                    ObjectAnimator objectAnimatorOfPointF = ObjectAnimatorUtils.ofPointF(viewBounds, TOP_LEFT_PROPERTY, getPathMotion().getPath(i3, i5, i4, i6));
                    ObjectAnimator objectAnimatorOfPointF2 = ObjectAnimatorUtils.ofPointF(viewBounds, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath(i7, i9, i8, i10));
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(objectAnimatorOfPointF, objectAnimatorOfPointF2);
                    animatorSet.addListener(new AnimatorListenerAdapter(viewBounds) { // from class: androidx.transition.ChangeBounds.6
                        private final ViewBounds mViewBounds;
                        final /* synthetic */ ViewBounds val$viewBounds;

                        {
                            this.val$viewBounds = viewBounds;
                            this.mViewBounds = viewBounds;
                        }
                    });
                    view = view2;
                    animatorMergeAnimators = animatorSet;
                }
            } else if (i3 != i4 || i5 != i6) {
                animatorMergeAnimators = ObjectAnimatorUtils.ofPointF(view2, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath(i3, i5, i4, i6));
            } else {
                animatorMergeAnimators = ObjectAnimatorUtils.ofPointF(view2, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath(i7, i9, i8, i10));
            }
            view = view2;
        } else {
            view = view2;
            ViewUtils.setLeftTopRightBottom(view, i3, i5, Math.max(i11, i13) + i3, i5 + Math.max(i12, i14));
            ObjectAnimator objectAnimatorOfPointF3 = (i3 == i4 && i5 == i6) ? null : ObjectAnimatorUtils.ofPointF(view, POSITION_PROPERTY, getPathMotion().getPath(i3, i5, i4, i6));
            boolean z = rect3 == null;
            if (z) {
                i2 = 0;
                rect3 = new Rect(0, 0, i11, i12);
            } else {
                i2 = 0;
            }
            boolean z2 = rect4 == null ? 1 : i2;
            Rect rect5 = z2 != 0 ? new Rect(i2, i2, i13, i14) : rect4;
            if (rect3.equals(rect5)) {
                objectAnimatorOfObject = null;
            } else {
                view.setClipBounds(rect3);
                objectAnimatorOfObject = ObjectAnimator.ofObject(view, "clipBounds", sRectEvaluator, rect3, rect5);
                ClipListener clipListener = new ClipListener(view, rect3, z, rect5, z2, i3, i5, i7, i9, i4, i6, i8, i10);
                objectAnimatorOfObject.addListener(clipListener);
                addListener(clipListener);
            }
            animatorMergeAnimators = TransitionUtils.mergeAnimators(objectAnimatorOfPointF3, objectAnimatorOfObject);
        }
        if (view.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup4 = (ViewGroup) view.getParent();
            ViewGroupUtils.suppressLayout(viewGroup4, true);
            getRootTransition().addListener(new SuppressLayoutListener(viewGroup4));
        }
        return animatorMergeAnimators;
    }

    private static class ViewBounds {
        private int mBottom;
        private int mBottomRightCalls;
        private int mLeft;
        private int mRight;
        private int mTop;
        private int mTopLeftCalls;
        private final View mView;

        ViewBounds(View view) {
            this.mView = view;
        }

        void setTopLeft(PointF pointF) {
            this.mLeft = Math.round(pointF.x);
            this.mTop = Math.round(pointF.y);
            int i = this.mTopLeftCalls + 1;
            this.mTopLeftCalls = i;
            if (i == this.mBottomRightCalls) {
                setLeftTopRightBottom();
            }
        }

        void setBottomRight(PointF pointF) {
            this.mRight = Math.round(pointF.x);
            this.mBottom = Math.round(pointF.y);
            int i = this.mBottomRightCalls + 1;
            this.mBottomRightCalls = i;
            if (this.mTopLeftCalls == i) {
                setLeftTopRightBottom();
            }
        }

        private void setLeftTopRightBottom() {
            ViewUtils.setLeftTopRightBottom(this.mView, this.mLeft, this.mTop, this.mRight, this.mBottom);
            this.mTopLeftCalls = 0;
            this.mBottomRightCalls = 0;
        }
    }

    private static class ClipListener extends AnimatorListenerAdapter implements Transition.TransitionListener {
        private final int mEndBottom;
        private final Rect mEndClip;
        private final boolean mEndClipIsNull;
        private final int mEndLeft;
        private final int mEndRight;
        private final int mEndTop;
        private boolean mIsCanceled;
        private final int mStartBottom;
        private final Rect mStartClip;
        private final boolean mStartClipIsNull;
        private final int mStartLeft;
        private final int mStartRight;
        private final int mStartTop;
        private final View mView;

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionEnd(Transition transition) {
        }

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionStart(Transition transition) {
        }

        ClipListener(View view, Rect rect, boolean z, Rect rect2, boolean z2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            this.mView = view;
            this.mStartClip = rect;
            this.mStartClipIsNull = z;
            this.mEndClip = rect2;
            this.mEndClipIsNull = z2;
            this.mStartLeft = i;
            this.mStartTop = i2;
            this.mStartRight = i3;
            this.mStartBottom = i4;
            this.mEndLeft = i5;
            this.mEndTop = i6;
            this.mEndRight = i7;
            this.mEndBottom = i8;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            onAnimationStart(animator, false);
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            onAnimationEnd(animator, false);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator, boolean z) {
            int iMax = Math.max(this.mStartRight - this.mStartLeft, this.mEndRight - this.mEndLeft);
            int iMax2 = Math.max(this.mStartBottom - this.mStartTop, this.mEndBottom - this.mEndTop);
            int i = z ? this.mEndLeft : this.mStartLeft;
            int i2 = z ? this.mEndTop : this.mStartTop;
            ViewUtils.setLeftTopRightBottom(this.mView, i, i2, iMax + i, iMax2 + i2);
            this.mView.setClipBounds(z ? this.mEndClip : this.mStartClip);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator, boolean z) {
            if (this.mIsCanceled) {
                return;
            }
            Rect rect = null;
            if (z) {
                if (!this.mStartClipIsNull) {
                    rect = this.mStartClip;
                }
            } else if (!this.mEndClipIsNull) {
                rect = this.mEndClip;
            }
            this.mView.setClipBounds(rect);
            if (z) {
                ViewUtils.setLeftTopRightBottom(this.mView, this.mStartLeft, this.mStartTop, this.mStartRight, this.mStartBottom);
            } else {
                ViewUtils.setLeftTopRightBottom(this.mView, this.mEndLeft, this.mEndTop, this.mEndRight, this.mEndBottom);
            }
        }

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionCancel(Transition transition) {
            this.mIsCanceled = true;
        }

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionPause(Transition transition) {
            this.mView.setTag(R.id.transition_clip, this.mView.getClipBounds());
            this.mView.setClipBounds(this.mEndClipIsNull ? null : this.mEndClip);
        }

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionResume(Transition transition) {
            Rect rect = (Rect) this.mView.getTag(R.id.transition_clip);
            this.mView.setTag(R.id.transition_clip, null);
            this.mView.setClipBounds(rect);
        }
    }

    private static class SuppressLayoutListener extends TransitionListenerAdapter {
        boolean mCanceled = false;
        final ViewGroup mParent;

        SuppressLayoutListener(ViewGroup viewGroup) {
            this.mParent = viewGroup;
        }

        @Override // androidx.transition.TransitionListenerAdapter, androidx.transition.Transition.TransitionListener
        public void onTransitionCancel(Transition transition) {
            ViewGroupUtils.suppressLayout(this.mParent, false);
            this.mCanceled = true;
        }

        @Override // androidx.transition.TransitionListenerAdapter, androidx.transition.Transition.TransitionListener
        public void onTransitionEnd(Transition transition) {
            if (!this.mCanceled) {
                ViewGroupUtils.suppressLayout(this.mParent, false);
            }
            transition.removeListener(this);
        }

        @Override // androidx.transition.TransitionListenerAdapter, androidx.transition.Transition.TransitionListener
        public void onTransitionPause(Transition transition) {
            ViewGroupUtils.suppressLayout(this.mParent, false);
        }

        @Override // androidx.transition.TransitionListenerAdapter, androidx.transition.Transition.TransitionListener
        public void onTransitionResume(Transition transition) {
            ViewGroupUtils.suppressLayout(this.mParent, true);
        }
    }
}
