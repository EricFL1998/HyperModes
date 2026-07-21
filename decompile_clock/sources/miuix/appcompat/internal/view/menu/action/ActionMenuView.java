package miuix.appcompat.internal.view.menu.action;

import android.R;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.appcompat.internal.view.menu.MenuItemImpl;
import miuix.appcompat.internal.view.menu.MenuView;
import miuix.internal.util.DeviceHelper;
import miuix.view.BlurableWidget;

/* JADX INFO: loaded from: classes2.dex */
public abstract class ActionMenuView extends LinearLayout implements MenuBuilder.ItemInvoker, MenuView, BlurableWidget {
    protected boolean mBackgroundViewApplyBlur;
    private MenuBuilder mMenu;
    private OpenCloseAnimator mOpenCloseAnimator;
    private ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;

    public interface ActionMenuChildView {
        boolean needsDividerAfter();

        boolean needsDividerBefore();
    }

    protected void clearBackground() {
    }

    protected float computeAlpha(float f, boolean z, boolean z2) {
        int i;
        if (z && z2) {
            return 1.0f;
        }
        if (z) {
            i = (int) ((1.0f - f) * 10.0f);
        } else {
            if (!z2) {
                return 1.0f;
            }
            i = (int) (f * 10.0f);
        }
        return i / 10.0f;
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        return false;
    }

    public abstract int getCollapsedHeight();

    @Override // miuix.appcompat.internal.view.menu.MenuView
    public int getWindowAnimations() {
        return 0;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuView
    public boolean hasBackgroundView() {
        return false;
    }

    public boolean hasOnlyCustomView() {
        return false;
    }

    public void onWillRemoved() {
    }

    protected void resetBackground() {
    }

    public ActionMenuView(Context context) {
        this(context, null);
    }

    public ActionMenuView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mBackgroundViewApplyBlur = false;
        setBaselineAligned(false);
        this.mOpenCloseAnimator = new OpenCloseAnimator();
        setLayoutAnimation(null);
    }

    public void setPresenter(ActionMenuPresenter actionMenuPresenter) {
        this.mPresenter = actionMenuPresenter;
    }

    public ActionMenuPresenter getPresenter() {
        return this.mPresenter;
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.updateMenuView(false);
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        if (getChildCount() == 0) {
            setMeasuredDimension(0, 0);
        } else {
            super.onMeasure(i, i2);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mPresenter.dismissPopupMenus(false);
    }

    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }

    public void setOverflowReserved(boolean z) {
        this.mReserveOverflow = z;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuView
    public boolean filterLeftoverView(int i) {
        View childAt = getChildAt(i);
        childAt.clearAnimation();
        removeView(childAt);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) layoutParams);
        }
        return generateDefaultLayoutParams();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams != null && (layoutParams instanceof LayoutParams);
    }

    public LayoutParams generateOverflowButtonLayoutParams(View view) {
        LayoutParams layoutParamsGenerateDefaultLayoutParams = generateDefaultLayoutParams();
        layoutParamsGenerateDefaultLayoutParams.isOverflowButton = true;
        return layoutParamsGenerateDefaultLayoutParams;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder.ItemInvoker
    public boolean invokeItem(MenuItemImpl menuItemImpl, int i) {
        return this.mMenu.performItemAction(menuItemImpl, i);
    }

    @Override // miuix.appcompat.internal.view.menu.MenuView
    public void initialize(MenuBuilder menuBuilder) {
        this.mMenu = menuBuilder;
    }

    protected boolean hasDividerBeforeChildAt(int i) {
        KeyEvent.Callback childAt = getChildAt(i - 1);
        KeyEvent.Callback childAt2 = getChildAt(i);
        boolean zNeedsDividerAfter = (i >= getChildCount() || !(childAt instanceof ActionMenuChildView)) ? false : ((ActionMenuChildView) childAt).needsDividerAfter();
        return (i <= 0 || !(childAt2 instanceof ActionMenuChildView)) ? zNeedsDividerAfter : zNeedsDividerAfter | ((ActionMenuChildView) childAt2).needsDividerBefore();
    }

    public void updateBackground(boolean z) {
        this.mBackgroundViewApplyBlur = z;
        if (z) {
            clearBackground();
        } else {
            resetBackground();
        }
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {

        @ViewDebug.ExportedProperty
        public int cellsUsed;

        @ViewDebug.ExportedProperty
        public boolean expandable;
        public boolean expanded;

        @ViewDebug.ExportedProperty
        public int extraPixels;

        @ViewDebug.ExportedProperty
        public boolean isOverflowButton;

        @ViewDebug.ExportedProperty
        public boolean preventEdgeOffset;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((LinearLayout.LayoutParams) layoutParams);
            this.isOverflowButton = layoutParams.isOverflowButton;
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.isOverflowButton = false;
        }

        public LayoutParams(int i, int i2, boolean z) {
            super(i, i2);
            this.isOverflowButton = z;
        }
    }

    public void playOpenAnimator() {
        this.mOpenCloseAnimator.open();
    }

    public void playCloseAnimator() {
        this.mOpenCloseAnimator.close();
    }

    protected float computeTranslationY(float f, boolean z, boolean z2) {
        float collapsedHeight = getCollapsedHeight();
        if (z && z2) {
            f = ((double) f) < 0.5d ? f * 2.0f : (1.0f - f) * 2.0f;
        } else if (z2) {
            f = 1.0f - f;
        }
        return f * collapsedHeight;
    }

    public void onPageScrolled(int i, float f, boolean z, boolean z2) {
        if (DeviceHelper.isFeatureWholeAnim()) {
            setAlpha(computeAlpha(f, z, z2));
        }
        float fComputeTranslationY = computeTranslationY(f, z, z2);
        if (!z || !z2 || getTranslationY() != 0.0f) {
            setTranslationY(fComputeTranslationY);
        }
        this.mOpenCloseAnimator.setTranslationY(fComputeTranslationY);
    }

    class OpenCloseAnimator implements Animator.AnimatorListener {
        Animator mCloseAnimator;
        Animator mCurrentAnimator;
        boolean mIsOpen;

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        public void setTranslationY(float f) {
            for (int i = 0; i < ActionMenuView.this.getChildCount(); i++) {
                ActionMenuView.this.getChildAt(i).setTranslationY(f);
            }
        }

        public OpenCloseAnimator() {
        }

        void initialize() {
            if (this.mCloseAnimator == null) {
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this, "TranslationY", ActionMenuView.this.getHeight());
                this.mCloseAnimator = objectAnimatorOfFloat;
                objectAnimatorOfFloat.setDuration(ActionMenuView.this.getResources().getInteger(R.integer.config_shortAnimTime));
                this.mCloseAnimator.addListener(this);
            }
        }

        void open() {
            cancel();
            this.mIsOpen = true;
            setTranslationY(0.0f);
            ActionMenuView.this.startLayoutAnimation();
        }

        void close() {
            cancel();
            this.mIsOpen = false;
            this.mCloseAnimator.start();
        }

        void cancel() {
            initialize();
            Animator animator = this.mCurrentAnimator;
            if (animator != null) {
                animator.cancel();
                this.mCurrentAnimator = null;
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            this.mCurrentAnimator = animator;
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            this.mCurrentAnimator = null;
        }
    }
}
