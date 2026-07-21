package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EdgeEffect;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.recyclerview.R;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import miuix.os.Build;
import miuix.spring.view.SpringHelper;
import miuix.spring.view.SpringStateListener;
import miuix.util.HapticFeedbackCompat;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes.dex */
public abstract class SpringRecyclerView extends RemixRecyclerView {
    private static final Field NESTED_SCROLL_HELPER;
    private static final RecyclerView.EdgeEffectFactory NON_EFFECT_FACTORY;
    private static final String TAG = "SpringRecyclerView";
    private static final Field VIEW_FLINGER;
    private boolean mHorizontalOverScrolling;
    private boolean mInGlobalRomMode;
    private int mManagedScrollState;
    private SpringFlinger mSpringFlinger;
    private SpringHelper mSpringHelper;
    private SpringNestedScrollingHelper mSpringNestedScrollingHelper;
    private List<SpringStateListener> mSpringStateListeners;
    private float mSpringX;
    private float mSpringY;
    private boolean mVerticalOverScrolling;

    @Override // androidx.recyclerview.widget.RemixRecyclerView
    public /* bridge */ /* synthetic */ boolean getSpringEnabled() {
        return super.getSpringEnabled();
    }

    @Override // androidx.recyclerview.widget.RemixRecyclerView, androidx.recyclerview.widget.RecyclerView, android.view.View
    public /* bridge */ /* synthetic */ boolean onGenericMotionEvent(MotionEvent motionEvent) {
        return super.onGenericMotionEvent(motionEvent);
    }

    @Override // androidx.recyclerview.widget.RemixRecyclerView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
    public /* bridge */ /* synthetic */ boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override // androidx.recyclerview.widget.RemixRecyclerView, androidx.recyclerview.widget.RecyclerView, android.view.View
    public /* bridge */ /* synthetic */ boolean onTouchEvent(MotionEvent motionEvent) {
        return super.onTouchEvent(motionEvent);
    }

    @Override // androidx.recyclerview.widget.RemixRecyclerView, android.view.View
    public /* bridge */ /* synthetic */ void setOverScrollMode(int i) {
        super.setOverScrollMode(i);
    }

    public SpringRecyclerView(Context context) {
        this(context, null);
    }

    public SpringRecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.recyclerViewStyle);
    }

    public SpringRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSpringStateListeners = new ArrayList();
        this.mSpringX = 0.0f;
        this.mSpringY = 0.0f;
        this.mManagedScrollState = 0;
        this.mSpringHelper = new SpringHelper() { // from class: androidx.recyclerview.widget.SpringRecyclerView.1
            HapticFeedbackCompat mHapticFeedbackCompat;

            @Override // miuix.spring.view.SpringHelper
            protected boolean canScrollHorizontally() {
                return SpringRecyclerView.this.mLayout != null && SpringRecyclerView.this.mLayout.canScrollHorizontally();
            }

            @Override // miuix.spring.view.SpringHelper
            protected boolean canScrollVertically() {
                return SpringRecyclerView.this.mLayout != null && SpringRecyclerView.this.mLayout.canScrollVertically();
            }

            @Override // miuix.spring.view.SpringHelper
            protected int getHeight() {
                return SpringRecyclerView.this.getHeight();
            }

            @Override // miuix.spring.view.SpringHelper
            protected int getWidth() {
                return SpringRecyclerView.this.getWidth();
            }

            @Override // miuix.spring.view.SpringHelper
            protected boolean springAvailable() {
                return SpringRecyclerView.this.springAvailable();
            }

            @Override // miuix.spring.view.SpringStateListener
            public void onSpringDistanceChanged(float f, float f2) {
                SpringRecyclerView.this.mSpringX = f;
                SpringRecyclerView.this.mSpringY = f2;
                for (int i2 = 0; i2 < SpringRecyclerView.this.mSpringStateListeners.size(); i2++) {
                    ((SpringStateListener) SpringRecyclerView.this.mSpringStateListeners.get(0)).onSpringDistanceChanged(f, f2);
                }
            }

            @Override // miuix.spring.view.SpringHelper
            protected void vibrate() {
                if (HapticCompat.doesSupportHaptic(HapticCompat.HapticVersion.HAPTIC_VERSION_2)) {
                    if (SpringRecyclerView.this.isHapticFeedbackEnabled()) {
                        getHapticFeedbackCompat().performExtHapticFeedbackAsync(201);
                        return;
                    }
                    return;
                }
                HapticCompat.performHapticFeedbackAsync(SpringRecyclerView.this, HapticFeedbackConstants.MIUI_SCROLL_EDGE);
            }

            private HapticFeedbackCompat getHapticFeedbackCompat() {
                if (this.mHapticFeedbackCompat == null) {
                    this.mHapticFeedbackCompat = new HapticFeedbackCompat(SpringRecyclerView.this.getContext());
                }
                return this.mHapticFeedbackCompat;
            }

            @Override // miuix.spring.view.SpringHelper
            protected boolean dispatchNestedPreScroll(int i2, int i3, int[] iArr, int[] iArr2, int i4) {
                if (SpringRecyclerView.this.mHorizontalOverScrolling && getHorizontalDistance() == 0) {
                    SpringRecyclerView.this.mHorizontalOverScrolling = false;
                }
                if (SpringRecyclerView.this.mVerticalOverScrolling && getVerticalDistance() == 0) {
                    SpringRecyclerView.this.mVerticalOverScrolling = false;
                }
                return SpringRecyclerView.this.mSpringNestedScrollingHelper.super_dispatchNestedPreScroll(i2, i3, iArr, iArr2, i4);
            }

            @Override // miuix.spring.view.SpringHelper
            protected void dispatchNestedScroll(int i2, int i3, int i4, int i5, int[] iArr, int i6, int[] iArr2) {
                SpringRecyclerView.this.mSpringNestedScrollingHelper.super_dispatchNestedScroll(i2, i3, i4, i5, iArr, i6, iArr2);
                if (springAvailable() && SpringRecyclerView.this.mManagedScrollState == 2) {
                    if (!SpringRecyclerView.this.mHorizontalOverScrolling && canScrollHorizontally() && i4 != 0) {
                        SpringRecyclerView.this.mSpringFlinger.notifyHorizontalEdgeReached(i4);
                    }
                    if (SpringRecyclerView.this.mVerticalOverScrolling || !canScrollVertically() || i5 == 0) {
                        return;
                    }
                    SpringRecyclerView.this.mSpringFlinger.notifyVerticalEdgeReached(i5);
                }
            }
        };
        this.mInGlobalRomMode = Build.IS_INTERNATIONAL_BUILD;
        this.mSpringFlinger = new SpringFlinger();
        SpringNestedScrollingHelper springNestedScrollingHelper = new SpringNestedScrollingHelper(this);
        this.mSpringNestedScrollingHelper = springNestedScrollingHelper;
        springNestedScrollingHelper.setNestedScrollingEnabled(isNestedScrollingEnabled());
        replaceViewFlinger(this.mSpringFlinger);
        replaceNestedScrollingHelper(this.mSpringNestedScrollingHelper);
        if (this.mInGlobalRomMode) {
            setSpringEnabled(false);
        } else {
            super.setEdgeEffectFactory(NON_EFFECT_FACTORY);
        }
    }

    @Override // androidx.recyclerview.widget.RemixRecyclerView
    public void setSpringEnabled(boolean z) {
        if (this.mInGlobalRomMode && z) {
            return;
        }
        super.setSpringEnabled(z);
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View, androidx.core.view.NestedScrollingChild
    public void setNestedScrollingEnabled(boolean z) {
        super.setNestedScrollingEnabled(z);
        SpringNestedScrollingHelper springNestedScrollingHelper = this.mSpringNestedScrollingHelper;
        if (springNestedScrollingHelper != null) {
            springNestedScrollingHelper.setNestedScrollingEnabled(z);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void stopScroll() {
        stopSpringFling();
        super.stopScroll();
    }

    public void stopSpringFling() {
        SpringFlinger springFlinger = this.mSpringFlinger;
        if (springFlinger != null) {
            springFlinger.stop();
        }
    }

    public void addSpringStateListener(SpringStateListener springStateListener) {
        this.mSpringStateListeners.add(springStateListener);
    }

    public void removeSpringStateListener(SpringStateListener springStateListener) {
        this.mSpringStateListeners.remove(springStateListener);
    }

    public float getSpringX() {
        return this.mSpringX;
    }

    public float getSpringY() {
        return this.mSpringY;
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public void draw(Canvas canvas) {
        int horizontalDistance = this.mSpringHelper.getHorizontalDistance();
        int verticalDistance = this.mSpringHelper.getVerticalDistance();
        if (horizontalDistance != 0 || verticalDistance != 0) {
            int iSave = canvas.save();
            canvas.translate(-horizontalDistance, -verticalDistance);
            super.draw(canvas);
            canvas.restoreToCount(iSave);
            return;
        }
        super.draw(canvas);
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    void setScrollState(int i) {
        if (this.mManagedScrollState == 1 && i == 0) {
            int horizontalDistance = this.mSpringHelper.getHorizontalDistance();
            int verticalDistance = this.mSpringHelper.getVerticalDistance();
            SpringFlinger springFlinger = this.mSpringFlinger;
            if (springFlinger != null && (horizontalDistance != 0 || verticalDistance != 0)) {
                springFlinger.springBack(horizontalDistance, verticalDistance);
                return;
            }
        }
        super.setScrollState(i);
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void onScrollStateChanged(int i) {
        super.onScrollStateChanged(i);
        this.mManagedScrollState = i;
        if (springAvailable()) {
            boolean zIsOverScrolling = isOverScrolling();
            if (i != 2 && zIsOverScrolling) {
                stopSpringFling();
                this.mHorizontalOverScrolling = false;
                this.mVerticalOverScrolling = false;
            }
            if (i == 0 && zIsOverScrolling) {
                this.mSpringHelper.resetDistance();
            }
        }
    }

    @Override // androidx.recyclerview.widget.RemixRecyclerView
    protected boolean isOverScrolling() {
        return this.mHorizontalOverScrolling || this.mVerticalOverScrolling;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean springAvailable() {
        return getOverScrollMode() != 2 && getSpringEnabled();
    }

    protected int getDragFlingVelocityX() {
        SpringFlinger springFlinger = this.mSpringFlinger;
        if (springFlinger != null) {
            return springFlinger.mDragFlingVelocityX;
        }
        return 0;
    }

    protected int getDragFlingVelocityY() {
        SpringFlinger springFlinger = this.mSpringFlinger;
        if (springFlinger != null) {
            return springFlinger.mDragFlingVelocityY;
        }
        return 0;
    }

    private class SpringFlinger extends RemixRecyclerView.ViewFlinger {
        private SpringFlinger() {
            super();
        }

        @Override // androidx.recyclerview.widget.RemixRecyclerView.ViewFlinger, androidx.recyclerview.widget.RecyclerView.ViewFlinger
        public void fling(int i, int i2) {
            int horizontalDistance = SpringRecyclerView.this.mSpringHelper.getHorizontalDistance();
            int verticalDistance = SpringRecyclerView.this.mSpringHelper.getVerticalDistance();
            if (SpringRecyclerView.this.springAvailable() && (horizontalDistance != 0 || verticalDistance != 0)) {
                overFling(i, i2, horizontalDistance, verticalDistance);
                return;
            }
            if (SpringRecyclerView.this.getOverScrollMode() == 1) {
                SpringRecyclerView.this.mHorizontalOverScrolling = false;
                SpringRecyclerView.this.mVerticalOverScrolling = false;
            }
            super.fling(i, i2);
        }

        void springBack(int i, int i2) {
            if (i != 0) {
                SpringRecyclerView.this.mHorizontalOverScrolling = true;
            }
            if (i2 != 0) {
                SpringRecyclerView.this.mVerticalOverScrolling = true;
            }
            SpringRecyclerView.this.setScrollState(2);
            resetFlingPosition();
            int i3 = -i;
            int i4 = -i2;
            this.mOverScroller.springBack(0, 0, i3, i3, i4, i4);
            postOnAnimation();
        }

        void overFling(int i, int i2, int i3, int i4) {
            int i5;
            int i6;
            int i7;
            int i8;
            SpringRecyclerView.this.mHorizontalOverScrolling = i3 != 0;
            SpringRecyclerView.this.mVerticalOverScrolling = i4 != 0;
            SpringRecyclerView.this.setScrollState(2);
            resetFlingPosition();
            int i9 = Integer.MIN_VALUE;
            int i10 = Integer.MAX_VALUE;
            if (Integer.signum(i) * i3 > 0) {
                i5 = -i3;
                i6 = i5;
            } else if (i < 0) {
                i6 = -i3;
                i5 = Integer.MIN_VALUE;
            } else {
                i5 = -i3;
                i6 = Integer.MAX_VALUE;
            }
            if (Integer.signum(i2) * i4 > 0) {
                i7 = -i4;
                i8 = i7;
            } else {
                if (i2 < 0) {
                    i10 = -i4;
                } else {
                    i9 = -i4;
                }
                i7 = i9;
                i8 = i10;
            }
            this.mOverScroller.fling(0, 0, i, i2, i5, i6, i7, i8, SpringRecyclerView.this.getWidth(), SpringRecyclerView.this.getHeight());
            postOnAnimation();
        }

        void notifyVerticalEdgeReached(int i) {
            SpringRecyclerView.this.mVerticalOverScrolling = true;
            SpringRecyclerView.this.setScrollState(2);
            resetFlingPosition();
            this.mOverScroller.notifyVerticalEdgeReached(0, -i, SpringRecyclerView.this.getHeight());
        }

        void notifyHorizontalEdgeReached(int i) {
            SpringRecyclerView.this.mHorizontalOverScrolling = true;
            SpringRecyclerView.this.setScrollState(2);
            resetFlingPosition();
            this.mOverScroller.notifyHorizontalEdgeReached(0, -i, SpringRecyclerView.this.getWidth());
        }

        @Override // androidx.recyclerview.widget.RemixRecyclerView.ViewFlinger
        protected void checkDoneScrolling() {
            super.checkDoneScrolling();
            int horizontalDistance = SpringRecyclerView.this.mSpringHelper.getHorizontalDistance();
            int verticalDistance = SpringRecyclerView.this.mSpringHelper.getVerticalDistance();
            if (horizontalDistance == 0 && verticalDistance == 0) {
                return;
            }
            springBack(horizontalDistance, verticalDistance);
        }
    }

    private class SpringNestedScrollingHelper extends NestedScrollingChildHelper {
        SpringNestedScrollingHelper(View view) {
            super(view);
        }

        @Override // androidx.core.view.NestedScrollingChildHelper
        public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2, int i3) {
            return SpringRecyclerView.this.mSpringHelper.handleNestedPreScroll(i, i2, iArr, iArr2, i3);
        }

        @Override // androidx.core.view.NestedScrollingChildHelper
        public void dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr, int i5, int[] iArr2) {
            SpringRecyclerView.this.mSpringHelper.handleNestedScroll(i, i2, i3, i4, iArr, i5, iArr2);
        }

        boolean super_dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2, int i3) {
            if (SpringRecyclerView.this.mHorizontalOverScrolling || SpringRecyclerView.this.mVerticalOverScrolling) {
                return false;
            }
            if (i == 0 && i2 == 0) {
                return false;
            }
            return super.dispatchNestedPreScroll(i, i2, iArr, iArr2, i3);
        }

        void super_dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr, int i5, int[] iArr2) {
            if (SpringRecyclerView.this.mHorizontalOverScrolling || SpringRecyclerView.this.mVerticalOverScrolling) {
                return;
            }
            super.dispatchNestedScroll(i, i2, i3, i4, iArr, i5, iArr2);
        }
    }

    static {
        try {
            Field declaredField = RecyclerView.class.getDeclaredField("mViewFlinger");
            VIEW_FLINGER = declaredField;
            declaredField.setAccessible(true);
            try {
                Field declaredField2 = RecyclerView.class.getDeclaredField("mScrollingChildHelper");
                NESTED_SCROLL_HELPER = declaredField2;
                declaredField2.setAccessible(true);
                NON_EFFECT_FACTORY = new NonEdgeEffectFactory();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchFieldException e2) {
            throw new RuntimeException(e2);
        }
    }

    private void replaceNestedScrollingHelper(NestedScrollingChildHelper nestedScrollingChildHelper) {
        try {
            NESTED_SCROLL_HELPER.set(this, nestedScrollingChildHelper);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void replaceViewFlinger(RemixRecyclerView.ViewFlinger viewFlinger) {
        try {
            VIEW_FLINGER.set(this, viewFlinger);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static class NonEdgeEffectFactory extends RecyclerView.EdgeEffectFactory {
        private NonEdgeEffectFactory() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.EdgeEffectFactory
        protected EdgeEffect createEdgeEffect(RecyclerView recyclerView, int i) {
            return new NonEdgeEffect(recyclerView.getContext());
        }
    }

    private static class NonEdgeEffect extends EdgeEffect {
        @Override // android.widget.EdgeEffect
        public boolean draw(Canvas canvas) {
            return false;
        }

        @Override // android.widget.EdgeEffect
        public void finish() {
        }

        @Override // android.widget.EdgeEffect
        public BlendMode getBlendMode() {
            return null;
        }

        @Override // android.widget.EdgeEffect
        public int getColor() {
            return 0;
        }

        @Override // android.widget.EdgeEffect
        public int getMaxHeight() {
            return 0;
        }

        @Override // android.widget.EdgeEffect
        public boolean isFinished() {
            return true;
        }

        @Override // android.widget.EdgeEffect
        public void onAbsorb(int i) {
        }

        @Override // android.widget.EdgeEffect
        public void onPull(float f) {
        }

        @Override // android.widget.EdgeEffect
        public void onPull(float f, float f2) {
        }

        @Override // android.widget.EdgeEffect
        public void onRelease() {
        }

        @Override // android.widget.EdgeEffect
        public void setBlendMode(BlendMode blendMode) {
        }

        @Override // android.widget.EdgeEffect
        public void setColor(int i) {
        }

        @Override // android.widget.EdgeEffect
        public void setSize(int i, int i2) {
        }

        NonEdgeEffect(Context context) {
            super(context);
        }
    }
}
