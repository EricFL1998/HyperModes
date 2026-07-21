package miuix.appcompat.internal.app.widget;

import android.R;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ViewProperty;
import miuix.appcompat.app.ActionBarTransitionListener;
import miuix.appcompat.internal.app.widget.actionbar.CollapseTitle;
import miuix.appcompat.internal.app.widget.actionbar.ExpandTitle;
import miuix.appcompat.internal.view.menu.action.ActionMenuPresenter;
import miuix.appcompat.internal.view.menu.action.ActionMenuView;
import miuix.appcompat.internal.view.menu.action.EndActionMenuPresenter;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes2.dex */
abstract class AbsActionBarView extends ViewGroup {
    protected static final int COLLAPSE_LAYOUT_MAX_TRANSY = 20;
    static final int INNER_STATE_COLLAPSE = 0;
    static final int INNER_STATE_EXPAND = 1;
    static final int INNER_STATE_RESIZING = 2;
    protected static final int MAX_ACTION_MENU_ITEM_COUNT_UNSET = Integer.MIN_VALUE;
    List<ActionBarTransitionListener> mActionBarTransitionListeners;
    protected ActionMenuPresenter mActionMenuPresenter;
    protected TransitionListener mAnimConfigListener;
    int mBottomMenuMode;
    protected AnimConfig mCollapseAnimHideConfig;
    protected AnimConfig mCollapseAnimShowConfig;
    protected boolean mCollapseTitleColorTransitEnable;
    protected boolean mEndActionMenuEnable;
    int mExpandState;
    int mExpandStateBeforeResizing;
    int mExpandStateOnLayout;
    protected boolean mExpandTitleColorTransitEnable;
    protected AnimConfig mHideProcessConfig;
    protected boolean mHyperActionMenuEnable;
    protected boolean mHyperSplitMenuEnabled;
    int mInnerExpandState;
    protected boolean mIsInWideMode;
    float mLastProcess;
    protected int mMaxActionMenuItemCount;
    protected ActionMenuView mMenuView;
    protected AnimConfig mMovableAnimNormalConfig;
    protected AnimConfig mMovableAnimShowConfig;
    protected Rect mPendingInset;
    private boolean mResizable;
    protected AnimConfig mShowProcessConfig;
    protected boolean mSplitActionBarEnable;
    protected ActionBarContainer mSplitView;
    protected boolean mSplitWhenNarrow;
    protected int mSubtitlePaddingV;
    protected boolean mTitleClickable;
    protected int mTitleMaxHeight;
    protected int mTitleMinHeight;
    protected int mTitlePaddingV;
    protected int mUserExpandState;
    protected boolean mUserSetExpandState;
    protected View.OnClickListener mUserSubTitleClickListener;

    int getActionBarStyle() {
        return R.attr.actionBarStyle;
    }

    public abstract CollapseTitle getCollapseTitle();

    public abstract ExpandTitle getExpandTitle();

    public abstract View getSubTitleView(int i);

    public abstract View getTitleView(int i);

    protected void onAnimatedExpandStateChanged(int i, int i2) {
    }

    protected void onExpandStateChanged(int i, int i2) {
    }

    public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3, int[] iArr2) {
    }

    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr, int[] iArr2) {
    }

    public void onNestedScrollAccepted(View view, View view2, int i, int i2) {
    }

    public boolean onStartNestedScroll(View view, View view2, int i, int i2) {
        return false;
    }

    public void onStopNestedScroll(View view, int i) {
    }

    public abstract void refreshBottomMenu();

    AbsActionBarView(Context context) {
        this(context, null);
    }

    AbsActionBarView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    AbsActionBarView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mInnerExpandState = 1;
        this.mExpandStateBeforeResizing = 1;
        this.mExpandState = 1;
        this.mExpandStateOnLayout = 1;
        this.mResizable = true;
        this.mTitleClickable = false;
        this.mLastProcess = 0.0f;
        this.mBottomMenuMode = 2;
        this.mMaxActionMenuItemCount = Integer.MIN_VALUE;
        this.mIsInWideMode = false;
        this.mAnimConfigListener = new TransitionListener() { // from class: miuix.appcompat.internal.app.widget.AbsActionBarView.1
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj) {
                super.onBegin(obj);
                if (AbsActionBarView.this.mActionBarTransitionListeners != null) {
                    Iterator<ActionBarTransitionListener> it = AbsActionBarView.this.mActionBarTransitionListeners.iterator();
                    while (it.hasNext()) {
                        it.next().onTransitionBegin(obj);
                    }
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                super.onUpdate(obj, collection);
                if (AbsActionBarView.this.mActionBarTransitionListeners != null) {
                    Iterator<ActionBarTransitionListener> it = AbsActionBarView.this.mActionBarTransitionListeners.iterator();
                    while (it.hasNext()) {
                        it.next().onTransitionUpdate(obj, collection);
                    }
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                super.onComplete(obj);
                if (AbsActionBarView.this.mActionBarTransitionListeners != null) {
                    Iterator<ActionBarTransitionListener> it = AbsActionBarView.this.mActionBarTransitionListeners.iterator();
                    while (it.hasNext()) {
                        it.next().onTransitionComplete(obj);
                    }
                }
            }
        };
        this.mUserSubTitleClickListener = null;
        this.mUserSetExpandState = false;
        this.mUserExpandState = -1;
        this.mTitlePaddingV = context.getResources().getDimensionPixelSize(miuix.appcompat.R.dimen.miuix_appcompat_action_bar_title_collapse_padding_vertical);
        this.mSubtitlePaddingV = context.getResources().getDimensionPixelSize(miuix.appcompat.R.dimen.miuix_appcompat_action_bar_subtitle_collapse_padding_vertical);
        this.mCollapseAnimShowConfig = new AnimConfig().setEase(-2, 1.0f, 0.3f);
        this.mShowProcessConfig = new AnimConfig().setEase(-2, 1.0f, 0.3f).addListeners(this.mAnimConfigListener);
        this.mCollapseAnimHideConfig = new AnimConfig().setEase(-2, 1.0f, 0.15f);
        this.mHideProcessConfig = new AnimConfig().setEase(-2, 1.0f, 0.15f).addListeners(this.mAnimConfigListener);
        this.mMovableAnimShowConfig = new AnimConfig().setEase(-2, 1.0f, 0.6f);
        this.mMovableAnimNormalConfig = new AnimConfig().setEase(-2, 1.0f, 0.6f);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, miuix.appcompat.R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        int i2 = typedArrayObtainStyledAttributes.getInt(miuix.appcompat.R.styleable.ActionBar_expandState, 1);
        boolean z = typedArrayObtainStyledAttributes.getBoolean(miuix.appcompat.R.styleable.ActionBar_resizable, true);
        boolean z2 = typedArrayObtainStyledAttributes.getBoolean(miuix.appcompat.R.styleable.ActionBar_titleClickable, false);
        typedArrayObtainStyledAttributes.recycle();
        if (isUserSetExpandState()) {
            int i3 = this.mUserExpandState;
            this.mInnerExpandState = i3;
            this.mExpandState = i3;
        } else if (i2 == 0) {
            this.mInnerExpandState = 0;
            this.mExpandState = 0;
        } else {
            this.mInnerExpandState = 1;
            this.mExpandState = 1;
        }
        this.mResizable = z;
        this.mTitleClickable = z2;
    }

    void bindActionBarTransitionListeners(List<ActionBarTransitionListener> list) {
        this.mActionBarTransitionListeners = list;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mSplitWhenNarrow) {
            setSplitActionBar(getContext().getResources().getBoolean(miuix.appcompat.R.bool.abc_split_action_bar_is_narrow));
        }
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.onConfigurationChanged(configuration);
        }
    }

    public void setTitleClickable(boolean z) {
        this.mTitleClickable = z;
    }

    public void setSubTitleClickListener(View.OnClickListener onClickListener) {
        this.mUserSubTitleClickListener = onClickListener;
    }

    public void setSplitActionBar(boolean z) {
        this.mSplitActionBarEnable = z;
    }

    public void setSplitWhenNarrow(boolean z) {
        this.mSplitWhenNarrow = z;
    }

    private void setTitleMinHeight(int i) {
        this.mTitleMinHeight = i;
        requestLayout();
    }

    private void setTitleMaxHeight(int i) {
        this.mTitleMaxHeight = i;
        requestLayout();
    }

    public void setSplitView(ActionBarContainer actionBarContainer) {
        this.mSplitView = actionBarContainer;
    }

    public int getAnimatedVisibility() {
        return getVisibility();
    }

    public ActionMenuView getActionMenuView() {
        return this.mMenuView;
    }

    public void animateToVisibility(int i) {
        ActionMenuView actionMenuView;
        clearAnimation();
        if (i != getVisibility()) {
            Animation animationLoadAnimation = AnimationUtils.loadAnimation(getContext(), i == 0 ? miuix.appcompat.R.anim.action_bar_fade_in : miuix.appcompat.R.anim.action_bar_fade_out);
            startAnimation(animationLoadAnimation);
            setVisibility(i);
            if (this.mSplitView == null || (actionMenuView = this.mMenuView) == null) {
                return;
            }
            actionMenuView.startAnimation(animationLoadAnimation);
            this.mMenuView.setVisibility(i);
        }
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        if (i != getVisibility()) {
            super.setVisibility(i);
        }
    }

    public boolean showOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.showOverflowMenu();
    }

    public void postShowOverflowMenu() {
        post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.AbsActionBarView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.showOverflowMenu();
            }
        });
    }

    public boolean hideOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.hideOverflowMenu(false);
    }

    public boolean isOverflowMenuShowing() {
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.isOverflowMenuShowing();
    }

    public boolean isOverflowReserved() {
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.isOverflowReserved();
    }

    public void dismissPopupMenus() {
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.dismissPopupMenus(false);
        }
    }

    protected int measureChildView(View view, int i, int i2, int i3) {
        view.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), i2);
        return Math.max(0, (i - view.getMeasuredWidth()) - i3);
    }

    protected int positionChild(View view, int i, int i2, int i3) {
        return positionChild(view, i, i2, i3, true);
    }

    protected int positionChild(View view, int i, int i2, int i3, boolean z) {
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        int i4 = i2 + ((i3 - measuredHeight) / 2);
        if (!z) {
            i4 = (this.mTitleMinHeight - measuredHeight) / 2;
        }
        int i5 = i4;
        ViewUtils.layoutChildView(this, view, i, i5, i + measuredWidth, i5 + measuredHeight);
        return measuredWidth;
    }

    protected int positionChildWithOffset(View view, int i, int i2, int i3, boolean z, int i4) {
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        int i5 = i2 + ((i3 - measuredHeight) / 2);
        if (!z) {
            i5 = (this.mTitleMinHeight - measuredHeight) / 2;
        }
        int i6 = i5;
        ViewUtils.layoutChildView(this, view, i + i4, i6, i + measuredWidth + i4, i6 + measuredHeight);
        return measuredWidth + i4;
    }

    protected int positionChildInverse(View view, int i, int i2, int i3) {
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        int i4 = (this.mTitleMinHeight - measuredHeight) / 2;
        ViewUtils.layoutChildView(this, view, i - measuredWidth, i4, i, i4 + measuredHeight);
        return measuredWidth;
    }

    public ActionMenuView getMenuView() {
        return this.mMenuView;
    }

    protected void setExpandStateByUser(int i) {
        if (i != -1) {
            this.mUserSetExpandState = true;
            this.mUserExpandState = i;
        } else {
            this.mUserSetExpandState = false;
            this.mUserExpandState = -1;
        }
    }

    public boolean isUserSetExpandState() {
        return this.mUserSetExpandState;
    }

    public int getExpandState() {
        return this.mExpandState;
    }

    public void setExpandState(int i) {
        setExpandState(i, false, false);
    }

    public void setExpandState(int i, boolean z, boolean z2) {
        int i2;
        if ((this.mResizable || z2) && (i2 = this.mInnerExpandState) != i) {
            if (z) {
                onAnimatedExpandStateChanged(i2, i);
                return;
            }
            if (i == 2) {
                this.mExpandStateBeforeResizing = this.mExpandState;
            }
            this.mInnerExpandState = i;
            if (i == 0) {
                this.mExpandState = 0;
            } else if (i == 1) {
                this.mExpandState = 1;
            }
            onExpandStateChanged(i2, i);
            this.mExpandStateOnLayout = this.mExpandState;
            requestLayout();
        }
    }

    public void setResizable(boolean z) {
        this.mResizable = z;
    }

    public boolean isResizable() {
        return this.mResizable;
    }

    public void setBottomMenuMode(int i) {
        this.mBottomMenuMode = i;
    }

    public void setPendingInset(Rect rect) {
        Rect rect2;
        if (rect == null) {
            return;
        }
        boolean z = this.mMenuView != null && ((rect2 = this.mPendingInset) == null || rect2.bottom != rect.bottom);
        if (this.mPendingInset == null) {
            this.mPendingInset = new Rect();
        }
        this.mPendingInset.set(rect);
        if (z) {
            refreshBottomMenu();
        }
    }

    protected static class CollapseView {
        private float mAlpha;
        private boolean mDetached;
        private List<View> mViews = new ArrayList();
        private boolean mIsAcceptAlphaChange = true;

        protected CollapseView() {
        }

        public void attachViews(View view) {
            if (this.mViews.contains(view)) {
                return;
            }
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: miuix.appcompat.internal.app.widget.AbsActionBarView.CollapseView.1
                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewAttachedToWindow(View view2) {
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View view2) {
                    Folme.clean(view2);
                }
            });
            this.mViews.add(view);
        }

        public void detachView(View view) {
            if (view == null || !this.mViews.contains(view)) {
                return;
            }
            this.mViews.remove(view);
        }

        public void setAlpha(float f) {
            if (this.mDetached) {
                return;
            }
            this.mAlpha = f;
            Iterator<View> it = this.mViews.iterator();
            while (it.hasNext()) {
                Folme.useAt(it.next()).state().setTo(ViewProperty.ALPHA, Float.valueOf(f));
            }
        }

        public void setAcceptAlphaChange(boolean z) {
            this.mIsAcceptAlphaChange = z;
        }

        public void setTransparent(int i, int i2) {
            if (this.mDetached) {
                return;
            }
            for (View view : this.mViews) {
                if (view.isAttachedToWindow()) {
                    Folme.useAt(view).state().setTo(ViewProperty.TRANSLATION_X, Integer.valueOf(i), ViewProperty.TRANSLATION_Y, Integer.valueOf(i2));
                }
            }
        }

        public void setVisibility(int i) {
            for (View view : this.mViews) {
                view.setVisibility(i);
                if (i != 0) {
                    view.clearFocus();
                }
            }
        }

        public void setAnimFrom(float f, int i, int i2) {
            setAnimFrom(f, i, i2, false);
        }

        public void setAnimFrom(float f, int i, int i2, boolean z) {
            if (this.mDetached) {
                return;
            }
            AnimState animStateAdd = new AnimState(TypedValues.TransitionType.S_FROM).add(ViewProperty.ALPHA, this.mIsAcceptAlphaChange ? f : this.mAlpha).add(ViewProperty.TRANSLATION_X, i).add(ViewProperty.TRANSLATION_Y, i2);
            for (View view : this.mViews) {
                if (z) {
                    view.setAlpha(f);
                    view.setTranslationX(i);
                    view.setTranslationY(i2);
                }
                if (view.isAttachedToWindow()) {
                    Folme.useAt(view).state().setTo(animStateAdd);
                }
            }
        }

        public void animTo(float f, int i, int i2, AnimConfig animConfig) {
            if (this.mDetached) {
                return;
            }
            if (!this.mIsAcceptAlphaChange) {
                f = this.mAlpha;
            }
            AnimState animStateAdd = new AnimState(TypedValues.TransitionType.S_TO).add(ViewProperty.ALPHA, f).add(ViewProperty.TRANSLATION_X, i).add(ViewProperty.TRANSLATION_Y, i2);
            for (View view : this.mViews) {
                if (view.isAttachedToWindow() && (view.getAlpha() != f || view.getTranslationX() != i || view.getTranslationY() != i2)) {
                    Folme.useAt(view).state().to(animStateAdd, animConfig);
                }
            }
        }

        public void onShow() {
            Iterator<View> it = this.mViews.iterator();
            while (it.hasNext()) {
                it.next().setEnabled(true);
            }
        }

        public void onHide() {
            for (View view : this.mViews) {
                view.clearFocus();
                view.setEnabled(false);
                view.setVisibility(4);
            }
        }

        public void onDetachedFromWindow() {
            this.mDetached = true;
            Iterator<View> it = this.mViews.iterator();
            while (it.hasNext()) {
                Folme.clean(it.next());
            }
        }

        public void onAttachedToWindow() {
            this.mDetached = false;
        }
    }

    protected void setActionMenuItemLimit(int i) {
        this.mMaxActionMenuItemCount = i;
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        if (actionMenuPresenter == null || (actionMenuPresenter instanceof EndActionMenuPresenter)) {
            return;
        }
        actionMenuPresenter.setItemLimit(i);
    }
}
