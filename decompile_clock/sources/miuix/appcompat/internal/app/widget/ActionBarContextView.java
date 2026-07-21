package miuix.appcompat.internal.app.widget;

import android.R;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.physics.DynamicAnimation;
import miuix.animation.physics.SpringAnimation;
import miuix.animation.property.ViewProperty;
import miuix.appcompat.internal.app.widget.actionbar.CollapseTitle;
import miuix.appcompat.internal.app.widget.actionbar.ExpandTitle;
import miuix.appcompat.internal.view.ActionBarPolicy;
import miuix.appcompat.internal.view.EditActionModeImpl;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.appcompat.internal.view.menu.action.ActionMenuItem;
import miuix.appcompat.internal.view.menu.action.ActionMenuPresenter;
import miuix.appcompat.internal.view.menu.action.ActionMenuView;
import miuix.appcompat.internal.view.menu.action.ResponsiveActionMenuView;
import miuix.core.util.MiuixTraceUtils;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.RomUtils;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;
import miuix.theme.Typography;
import miuix.view.ActionModeAnimationListener;

/* JADX INFO: loaded from: classes2.dex */
public class ActionBarContextView extends AbsActionBarView implements ActionModeView {
    private static final int ANIMATE_IDLE = 0;
    private static final int ANIMATE_IN = 1;
    private static final int ANIMATE_OUT = 2;
    private static final float DAMPING = 0.9f;
    private static final int DELAY_DURATION_50 = 50;
    private static final float STIFFNESS_HIGH = 986.96f;
    private static final float STIFFNESS_LOW = 322.27f;
    private static final int TYPE_NON_TOUCH = 1;
    private static final int TYPE_TOUCH = 0;
    private ActionBarView mActionBarView;
    private WeakReference<ActionMode> mActionMode;
    private Drawable mActionModeBackground;
    private boolean mAnimateStart;
    private boolean mAnimateToVisible;
    private List<ActionModeAnimationListener> mAnimationListeners;
    private int mAnimationMode;
    private float mAnimationProgress;
    private boolean mBackgroundViewApplyBlur;
    private Button mButton1;
    private ActionMenuItem mButton1MenuItem;
    private Button mButton2;
    private ActionMenuItem mButton2MenuItem;
    private AbsActionBarView.CollapseView mCollapseController;
    private int mCollapseTotalHeight;
    private int mContentInset;
    private int mExpandTitleStyleRes;
    private TextView mExpandTitleView;
    private int mExpandTotalHeight;
    private final Handler mHandler;
    private boolean mIsAnimating;
    private View mMainContainer;
    private AnimConfig mMenuAnimConfig;
    private AbsActionBarView.CollapseView mMovableController;
    private FrameLayout mMovableMainContainer;
    private boolean mNonTouchScrolling;
    private View.OnClickListener mOnMenuItemClickListener;
    private int mPendingHeight;
    private final Runnable mPostAnimationRunnable;
    private Runnable mPostScroll;
    private Scroller mPostScroller;
    private boolean mRequestAnimation;
    private Drawable mSplitBackground;
    private boolean mStartWithAnim;
    private CharSequence mTitle;
    private LinearLayout mTitleLayout;
    private boolean mTitleOptional;
    private int mTitleStyleRes;
    private TextView mTitleView;
    private boolean mTouchScrolling;
    private TransitionListener mTransitionListener;
    private SpringAnimation mVisibilityAnim;

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    int getActionBarStyle() {
        return R.attr.actionModeStyle;
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public CollapseTitle getCollapseTitle() {
        return null;
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public ExpandTitle getExpandTitle() {
        return null;
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public View getSubTitleView(int i) {
        return null;
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public boolean onStartNestedScroll(View view, View view2, int i, int i2) {
        return true;
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void animateToVisibility(int i) {
        super.animateToVisibility(i);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void dismissPopupMenus() {
        super.dismissPopupMenus();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ ActionMenuView getActionMenuView() {
        return super.getActionMenuView();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ int getAnimatedVisibility() {
        return super.getAnimatedVisibility();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ int getExpandState() {
        return super.getExpandState();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ ActionMenuView getMenuView() {
        return super.getMenuView();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ boolean isOverflowReserved() {
        return super.isOverflowReserved();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ boolean isResizable() {
        return super.isResizable();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ boolean isUserSetExpandState() {
        return super.isUserSetExpandState();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void postShowOverflowMenu() {
        super.postShowOverflowMenu();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void setBottomMenuMode(int i) {
        super.setBottomMenuMode(i);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void setExpandState(int i) {
        super.setExpandState(i);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void setExpandState(int i, boolean z, boolean z2) {
        super.setExpandState(i, z, z2);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void setPendingInset(Rect rect) {
        super.setPendingInset(rect);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void setResizable(boolean z) {
        super.setResizable(z);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void setSplitView(ActionBarContainer actionBarContainer) {
        super.setSplitView(actionBarContainer);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void setSplitWhenNarrow(boolean z) {
        super.setSplitWhenNarrow(z);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void setSubTitleClickListener(View.OnClickListener onClickListener) {
        super.setSubTitleClickListener(onClickListener);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public /* bridge */ /* synthetic */ void setTitleClickable(boolean z) {
        super.setTitleClickable(z);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView, android.view.View
    public /* bridge */ /* synthetic */ void setVisibility(int i) {
        super.setVisibility(i);
    }

    public ActionBarContextView(Context context) {
        this(context, null);
    }

    public ActionBarContextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.actionModeStyle);
    }

    public ActionBarContextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mStartWithAnim = true;
        this.mBackgroundViewApplyBlur = false;
        this.mHandler = new Handler(Looper.myLooper());
        this.mPostAnimationRunnable = new Runnable() { // from class: miuix.appcompat.internal.app.widget.ActionBarContextView.1
            @Override // java.lang.Runnable
            public void run() {
                if (ActionBarContextView.this.mPostScroll != null) {
                    ActionBarContextView actionBarContextView = ActionBarContextView.this;
                    actionBarContextView.postOnAnimation(actionBarContextView.mPostScroll);
                }
            }
        };
        this.mOnMenuItemClickListener = new View.OnClickListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarContextView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EditActionModeImpl editActionModeImpl;
                ActionMenuItem actionMenuItem = view.getId() == 16908313 ? ActionBarContextView.this.mButton1MenuItem : ActionBarContextView.this.mButton2MenuItem;
                if (ActionBarContextView.this.mActionMode == null || (editActionModeImpl = (EditActionModeImpl) ActionBarContextView.this.mActionMode.get()) == null) {
                    return;
                }
                editActionModeImpl.onMenuItemSelected((MenuBuilder) editActionModeImpl.getMenu(), actionMenuItem);
            }
        };
        this.mCollapseController = new AbsActionBarView.CollapseView();
        this.mMovableController = new AbsActionBarView.CollapseView();
        this.mTouchScrolling = false;
        this.mNonTouchScrolling = false;
        this.mPostScroll = new Runnable() { // from class: miuix.appcompat.internal.app.widget.ActionBarContextView.4
            @Override // java.lang.Runnable
            public void run() {
                if (ActionBarContextView.this.mPostScroller.computeScrollOffset()) {
                    ActionBarContextView actionBarContextView = ActionBarContextView.this;
                    actionBarContextView.mPendingHeight = actionBarContextView.mPostScroller.getCurrY() - ActionBarContextView.this.mCollapseTotalHeight;
                    ActionBarContextView.this.requestLayout();
                    if (ActionBarContextView.this.mPostScroller.isFinished()) {
                        if (ActionBarContextView.this.mPostScroller.getCurrY() != ActionBarContextView.this.mCollapseTotalHeight) {
                            if (ActionBarContextView.this.mPostScroller.getCurrY() == ActionBarContextView.this.mCollapseTotalHeight + ActionBarContextView.this.mMovableMainContainer.getMeasuredHeight()) {
                                ActionBarContextView.this.setExpandState(1);
                                return;
                            }
                            return;
                        }
                        ActionBarContextView.this.setExpandState(0);
                        return;
                    }
                    ActionBarContextView.this.postOnAnimation(this);
                }
            }
        };
        this.mPostScroller = new Scroller(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.mMovableMainContainer = frameLayout;
        frameLayout.setId(miuix.appcompat.R.id.action_bar_movable_container);
        this.mMovableMainContainer.setPaddingRelative(context.getResources().getDimensionPixelOffset(miuix.appcompat.R.dimen.miuix_appcompat_action_bar_title_horizontal_padding), context.getResources().getDimensionPixelOffset(miuix.appcompat.R.dimen.miuix_appcompat_action_bar_title_top_padding), context.getResources().getDimensionPixelOffset(miuix.appcompat.R.dimen.miuix_appcompat_action_bar_title_horizontal_padding), context.getResources().getDimensionPixelOffset(miuix.appcompat.R.dimen.miuix_appcompat_action_bar_title_bottom_padding));
        this.mMovableMainContainer.setVisibility(0);
        this.mMovableController.attachViews(this.mMovableMainContainer);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, miuix.appcompat.R.styleable.ActionMode, i, 0);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(miuix.appcompat.R.styleable.ActionMode_android_background);
        this.mActionModeBackground = drawable;
        setBackground(drawable);
        this.mTitleStyleRes = typedArrayObtainStyledAttributes.getResourceId(miuix.appcompat.R.styleable.ActionMode_android_titleTextStyle, 0);
        this.mExpandTitleStyleRes = typedArrayObtainStyledAttributes.getResourceId(miuix.appcompat.R.styleable.ActionMode_expandTitleTextStyle, 0);
        this.mTitleMinHeight = typedArrayObtainStyledAttributes.getLayoutDimension(miuix.appcompat.R.styleable.ActionMode_android_minHeight, 0);
        this.mSplitBackground = typedArrayObtainStyledAttributes.getDrawable(miuix.appcompat.R.styleable.ActionMode_android_backgroundSplit);
        this.mButton1MenuItem = new ActionMenuItem(context, 0, 16908313, 0, 0, context.getString(R.string.cancel));
        this.mButton2MenuItem = new ActionMenuItem(context, 0, 16908314, 0, 0, context.getString(miuix.appcompat.R.string.miuix_appcompat_action_mode_select_all));
        this.mStartWithAnim = typedArrayObtainStyledAttributes.getBoolean(miuix.appcompat.R.styleable.ActionMode_actionModeAnim, true);
        typedArrayObtainStyledAttributes.recycle();
    }

    public void setActionBarView(ActionBarView actionBarView) {
        this.mActionBarView = actionBarView;
    }

    public void setActionModeAnim(boolean z) {
        this.mStartWithAnim = z;
    }

    public void setContentInset(int i) {
        this.mContentInset = i;
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView, android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        TextView textView;
        super.onConfigurationChanged(configuration);
        TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(null, miuix.appcompat.R.styleable.ActionMode, getActionBarStyle(), 0);
        this.mTitleMinHeight = typedArrayObtainStyledAttributes.getLayoutDimension(miuix.appcompat.R.styleable.ActionMode_android_minHeight, 0);
        typedArrayObtainStyledAttributes.recycle();
        int dimensionPixelOffset = getResources().getDimensionPixelOffset(miuix.appcompat.R.dimen.miuix_appcompat_action_bar_title_horizontal_padding);
        this.mMovableMainContainer.setPaddingRelative(dimensionPixelOffset, getResources().getDimensionPixelOffset(miuix.appcompat.R.dimen.miuix_appcompat_action_bar_title_top_padding), dimensionPixelOffset, getResources().getDimensionPixelOffset(miuix.appcompat.R.dimen.miuix_appcompat_action_bar_title_bottom_padding));
        setPaddingRelative(AttributeResolver.resolveDimensionPixelSize(getContext(), miuix.appcompat.R.attr.actionBarPaddingStart), getPaddingTop(), AttributeResolver.resolveDimensionPixelSize(getContext(), miuix.appcompat.R.attr.actionBarPaddingEnd), getPaddingBottom());
        if (this.mTitleStyleRes == 0 || (textView = this.mTitleView) == null) {
            return;
        }
        textView.setTextAppearance(getContext(), this.mTitleStyleRes);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mActionMenuPresenter != null) {
            this.mActionMenuPresenter.hideOverflowMenu(false);
            this.mActionMenuPresenter.hideSubMenus();
        }
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public View getTitleView(int i) {
        if (i == 0) {
            return this.mTitleView;
        }
        if (i != 1) {
            return null;
        }
        return this.mExpandTitleView;
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public void setSplitActionBar(boolean z) {
        if (this.mSplitActionBarEnable != z) {
            if (this.mActionMenuPresenter != null) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -1);
                if (!z) {
                    this.mMenuView = (ActionMenuView) this.mActionMenuPresenter.getMenuView(this);
                    this.mMenuView.setBackground(null);
                    ViewGroup viewGroup = (ViewGroup) this.mMenuView.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(this.mMenuView);
                    }
                    addView(this.mMenuView, layoutParams);
                } else {
                    this.mActionMenuPresenter.setWidthLimit(getContext().getResources().getDisplayMetrics().widthPixels, true);
                    layoutParams.width = -1;
                    layoutParams.height = -2;
                    layoutParams.gravity = this.mIsInWideMode ? 17 : 80;
                    this.mMenuView = (ActionMenuView) this.mActionMenuPresenter.getMenuView(this);
                    this.mMenuView.setBackground(this.mSplitBackground);
                    ViewGroup viewGroup2 = (ViewGroup) this.mMenuView.getParent();
                    if (viewGroup2 != null) {
                        viewGroup2.removeView(this.mMenuView);
                        this.mSplitView.onActionModeMenuViewRemoved(this.mMenuView);
                    }
                    this.mSplitView.addView(this.mMenuView, layoutParams);
                    this.mSplitView.onActionModeMenuViewAdded(this.mMenuView);
                }
            }
            super.setSplitActionBar(z);
        }
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.isOverflowOpen = isOverflowMenuShowing();
        savedState.title = getTitle();
        Button button = this.mButton2;
        if (button != null) {
            savedState.defaultButtonText = button.getText();
        }
        if (this.mInnerExpandState == 2) {
            savedState.expandState = 0;
        } else {
            savedState.expandState = this.mInnerExpandState;
        }
        return savedState;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        setTitle(savedState.title);
        setButton(16908314, savedState.defaultButtonText);
        if (savedState.isOverflowOpen) {
            postShowOverflowMenu();
        }
        setExpandState(savedState.expandState);
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        initTitle();
    }

    protected void initTitle() {
        if (this.mTitleLayout == null) {
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(miuix.appcompat.R.layout.miuix_appcompat_action_mode_title_item, (ViewGroup) this, false);
            this.mTitleLayout = linearLayout;
            this.mButton1 = (Button) linearLayout.findViewById(16908313);
            this.mButton2 = (Button) this.mTitleLayout.findViewById(16908314);
            Button button = this.mButton1;
            if (button != null) {
                button.setOnClickListener(this.mOnMenuItemClickListener);
                Folme.useAt(this.mButton1).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).setAlpha(0.6f, new ITouchStyle.TouchType[0]).handleTouchOf(this.mButton1, new AnimConfig[0]);
                Folme.useAt(this.mButton1).hover().setFeedbackRadius(60.0f);
                Folme.useAt(this.mButton1).hover().setEffect(IHoverStyle.HoverEffect.FLOATED_WRAPPED).handleHoverOf(this.mButton1, new AnimConfig[0]);
            }
            Button button2 = this.mButton2;
            if (button2 != null) {
                button2.setOnClickListener(this.mOnMenuItemClickListener);
                Folme.useAt(this.mButton2).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).setAlpha(0.6f, new ITouchStyle.TouchType[0]).handleTouchOf(this.mButton2, new AnimConfig[0]);
                Folme.useAt(this.mButton2).hover().setFeedbackRadius(60.0f);
                Folme.useAt(this.mButton2).hover().setEffect(IHoverStyle.HoverEffect.FLOATED_WRAPPED).handleHoverOf(this.mButton2, new AnimConfig[0]);
            }
            TextView textView = (TextView) this.mTitleLayout.findViewById(R.id.title);
            this.mTitleView = textView;
            if (this.mTitleStyleRes != 0) {
                textView.setTextAppearance(getContext(), this.mTitleStyleRes);
            }
            TextView textView2 = new TextView(getContext());
            this.mExpandTitleView = textView2;
            if (this.mExpandTitleStyleRes != 0) {
                textView2.setTextAppearance(getContext(), this.mExpandTitleStyleRes);
                if (RomUtils.getHyperOsVersion() <= 1) {
                    Typography.applyMiSansLight(this.mExpandTitleView);
                }
            }
        }
        this.mTitleView.setText(this.mTitle);
        this.mExpandTitleView.setText(this.mTitle);
        this.mMainContainer = this.mTitleLayout;
        this.mCollapseController.attachViews(this.mTitleView);
        boolean z = !TextUtils.isEmpty(this.mTitle);
        this.mTitleLayout.setVisibility(z ? 0 : 8);
        this.mExpandTitleView.setVisibility(z ? 0 : 8);
        if (this.mTitleLayout.getParent() == null) {
            addView(this.mTitleLayout);
        }
        if (this.mExpandTitleView.getParent() == null) {
            this.mExpandTitleView.setId(miuix.appcompat.R.id.action_context_bar_expand_title);
            this.mMovableMainContainer.addView(this.mExpandTitleView);
        }
        if (this.mMovableMainContainer.getParent() == null) {
            addView(this.mMovableMainContainer);
        }
        if (this.mInnerExpandState == 0) {
            this.mCollapseController.setAnimFrom(1.0f, 0, 0);
            this.mMovableController.setAnimFrom(0.0f, 0, 0);
        } else if (this.mInnerExpandState == 1) {
            this.mCollapseController.setAnimFrom(0.0f, 0, 20);
            this.mMovableController.setAnimFrom(1.0f, 0, 0);
        }
    }

    private boolean canTitleBeShown() {
        boolean z = (!isResizable() && getExpandState() == 0) || this.mTitleView.getPaint().measureText(this.mTitle.toString()) <= ((float) this.mTitleView.getMeasuredWidth());
        if (!ActionBarPolicy.get(getContext()).isTitleEnableEllipsis() || z) {
            return z;
        }
        return true;
    }

    public int getCollapsedHeight() {
        return this.mCollapseTotalHeight;
    }

    public int getExpandedHeight() {
        return this.mExpandTotalHeight;
    }

    @Override // miuix.appcompat.internal.app.widget.ActionModeView
    public void initForMode(ActionMode actionMode) {
        if (this.mActionMode != null) {
            cancelAnimation();
            killMode();
        }
        initTitle();
        if (this.mTitleView.getEllipsize() == TextUtils.TruncateAt.MARQUEE) {
            this.mTitleView.requestFocus();
        }
        this.mActionMode = new WeakReference<>(actionMode);
        MenuBuilder menuBuilder = (MenuBuilder) actionMode.getMenu();
        if (this.mActionMenuPresenter != null) {
            this.mActionMenuPresenter.dismissPopupMenus(false);
        }
        Object parent = getParent();
        while (true) {
            View view = (View) parent;
            if (!(view instanceof ActionBarOverlayLayout)) {
                if (view.getParent() instanceof View) {
                    parent = view.getParent();
                } else {
                    throw new IllegalStateException("ActionBarOverlayLayout not found");
                }
            } else {
                this.mActionMenuPresenter = new ActionMenuPresenter(getContext(), (ActionBarOverlayLayout) view, miuix.appcompat.R.layout.miuix_appcompat_responsive_action_menu_layout, miuix.appcompat.R.layout.miuix_appcompat_action_mode_menu_item_layout);
                this.mActionMenuPresenter.setReserveOverflow(true);
                this.mActionMenuPresenter.setActionEditMode(true);
                if (this.mMaxActionMenuItemCount != Integer.MIN_VALUE) {
                    this.mActionMenuPresenter.setItemLimit(this.mMaxActionMenuItemCount);
                }
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -1);
                menuBuilder.addMenuPresenter(this.mActionMenuPresenter);
                if (!this.mSplitActionBarEnable) {
                    this.mMenuView = (ActionMenuView) this.mActionMenuPresenter.getMenuView(this);
                    this.mMenuView.setBackground(null);
                    addView(this.mMenuView, layoutParams);
                    return;
                }
                addSplitMenuView();
                return;
            }
        }
    }

    private void addSplitMenuView() {
        this.mActionMenuPresenter.setWidthLimit(getContext().getResources().getDisplayMetrics().widthPixels, true);
        this.mMenuView = (ActionMenuView) this.mActionMenuPresenter.getMenuView(this);
        ViewGroup viewGroup = (ViewGroup) this.mMenuView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(this.mMenuView);
            this.mSplitView.onActionModeMenuViewRemoved(this.mMenuView);
        }
        if (this.mMenuView != null) {
            this.mMenuView.setSupportBlur(this.mSplitView.isSupportBlur());
            this.mMenuView.setEnableBlur(this.mSplitView.isEnableBlur());
            this.mMenuView.applyBlur(this.mSplitView.isEnableBlur() && this.mMenuView.getMeasuredWidth() > 0 && this.mMenuView.getMeasuredHeight() > 0);
            this.mMenuView.updateBackground(this.mBackgroundViewApplyBlur);
        }
        boolean z = this.mBottomMenuMode == 3;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
        layoutParams.gravity = 1;
        if (z) {
            layoutParams.bottomMargin = MiuixUIUtils.dp2px(getContext(), 16.0f);
        }
        if (this.mPendingInset != null) {
            if (z) {
                layoutParams.bottomMargin += this.mPendingInset.bottom;
                ViewUtils.resetPaddingBottom(this.mMenuView, 0);
            } else {
                ViewUtils.resetPaddingBottom(this.mMenuView, this.mPendingInset.bottom);
            }
        }
        if (this.mMenuView instanceof ResponsiveActionMenuView) {
            ((ResponsiveActionMenuView) this.mMenuView).setSuspendEnabled(z);
        }
        this.mSplitView.addView(this.mMenuView, layoutParams);
        this.mSplitView.onActionModeMenuViewAdded(this.mMenuView);
        requestLayout();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public void refreshBottomMenu() {
        if (!this.mSplitActionBarEnable || this.mActionMenuPresenter == null || this.mActionMode == null) {
            return;
        }
        addSplitMenuView();
    }

    @Override // miuix.appcompat.internal.app.widget.ActionModeView
    public void closeMode() {
        endAnimation();
        this.mAnimationMode = 2;
    }

    @Override // miuix.appcompat.internal.app.widget.ActionModeView
    public void killMode() {
        removeAllViews();
        List<ActionModeAnimationListener> list = this.mAnimationListeners;
        if (list != null) {
            list.clear();
            this.mAnimationListeners = null;
        }
        if (this.mSplitView != null) {
            if (this.mMenuView != null) {
                this.mMenuView.onWillRemoved();
            }
            this.mSplitView.removeView(this.mMenuView);
            this.mSplitView.onActionModeMenuViewRemoved(this.mMenuView);
        }
        this.mMenuView = null;
        this.mActionMode = null;
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public boolean showOverflowMenu() {
        return this.mActionMenuPresenter != null && this.mActionMenuPresenter.showOverflowMenu();
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public boolean hideOverflowMenu() {
        return this.mActionMenuPresenter != null && this.mActionMenuPresenter.hideOverflowMenu(false);
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public boolean isOverflowMenuShowing() {
        return this.mActionMenuPresenter != null && this.mActionMenuPresenter.isOverflowMenuShowing();
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.MarginLayoutParams(-1, -2);
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new ViewGroup.MarginLayoutParams(getContext(), attributeSet);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        return true;
    }

    public void updateBackground(boolean z) {
        this.mBackgroundViewApplyBlur = z;
        if (z) {
            clearBackground();
        } else {
            resetBackground();
        }
    }

    private void resetBackground() {
        setBackground(this.mActionModeBackground);
        if (!this.mSplitActionBarEnable || this.mSplitView == null) {
            return;
        }
        this.mSplitView.updateBackgroundInternal(false);
    }

    private void clearBackground() {
        setBackground(null);
        if (!this.mSplitActionBarEnable || this.mSplitView == null) {
            return;
        }
        this.mSplitView.updateBackgroundInternal(true);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int iMax;
        int size = View.MeasureSpec.getSize(i);
        int i3 = this.mTitleMaxHeight;
        int paddingTop = getPaddingTop() + getPaddingBottom();
        int paddingLeft = (size - getPaddingLeft()) - getPaddingRight();
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec((i3 > 0 ? i3 : View.MeasureSpec.getSize(i2)) - paddingTop, Integer.MIN_VALUE);
        if (this.mMenuView == null || this.mMenuView.getParent() != this) {
            iMax = 0;
        } else {
            paddingLeft = measureChildView(this.mMenuView, paddingLeft, iMakeMeasureSpec, 0);
            iMax = this.mMenuView.getMeasuredHeight();
        }
        if (this.mMainContainer.getVisibility() != 8) {
            this.mMainContainer.measure(View.MeasureSpec.makeMeasureSpec(paddingLeft, BasicMeasure.EXACTLY), iMakeMeasureSpec);
            iMax = Math.max(iMax, this.mMainContainer.getMeasuredHeight());
            TextView textView = this.mTitleView;
            if (textView != null) {
                textView.setVisibility(canTitleBeShown() ? 0 : 4);
            }
        }
        if (this.mMovableMainContainer.getVisibility() != 8) {
            this.mMovableMainContainer.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
        }
        if (i3 <= 0) {
            this.mCollapseTotalHeight = iMax > 0 ? Math.max(iMax, this.mTitleMinHeight) + this.mContentInset : 0;
        } else if (iMax >= i3) {
            this.mCollapseTotalHeight = i3 + this.mContentInset;
        }
        this.mExpandTotalHeight = this.mCollapseTotalHeight + this.mMovableMainContainer.getMeasuredHeight();
        if (this.mInnerExpandState == 2) {
            setMeasuredDimension(size, this.mCollapseTotalHeight + this.mPendingHeight);
        } else if (this.mInnerExpandState == 1) {
            setMeasuredDimension(size, this.mExpandTotalHeight);
        } else {
            setMeasuredDimension(size, this.mCollapseTotalHeight);
        }
    }

    /* JADX WARN: Code duplicated, block: B:12:0x005d  */
    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int measuredHeight;
        int i6 = (int) ((i3 - i) / getContext().getResources().getDisplayMetrics().density);
        if (this.mInnerExpandState == 2) {
            measuredHeight = this.mPendingHeight;
        } else {
            if (this.mInnerExpandState == 1) {
                measuredHeight = this.mMovableMainContainer.getMeasuredHeight();
            } else {
                i5 = 0;
            }
            int i7 = i4 - i2;
            onLayoutCollapseViews(i, i2, i3, i4 - i5);
            onLayoutExpandViews(z, i, i7 - i5, i3, i7);
            float fMin = Math.min(1.0f, (this.mMovableMainContainer.getMeasuredHeight() - i5) / this.mMovableMainContainer.getMeasuredHeight());
            animateLayoutWithProcess(fMin);
            this.mLastProcess = fMin;
            this.mIsInWideMode = i6 > 640;
        }
        i5 = measuredHeight;
        int i8 = i4 - i2;
        onLayoutCollapseViews(i, i2, i3, i4 - i5);
        onLayoutExpandViews(z, i, i8 - i5, i3, i8);
        float fMin2 = Math.min(1.0f, (this.mMovableMainContainer.getMeasuredHeight() - i5) / this.mMovableMainContainer.getMeasuredHeight());
        animateLayoutWithProcess(fMin2);
        this.mLastProcess = fMin2;
        this.mIsInWideMode = i6 > 640;
    }

    private void animateLayoutWithProcess(float f) {
        float fMin = 1.0f - Math.min(1.0f, f * 3.0f);
        if (this.mInnerExpandState == 2) {
            if (fMin > 0.0f) {
                this.mCollapseController.animTo(0.0f, 0, 20, this.mCollapseAnimHideConfig);
            } else {
                this.mCollapseController.animTo(1.0f, 0, 0, this.mCollapseAnimShowConfig);
            }
            this.mMovableController.animTo(fMin, 0, 0, this.mMovableAnimNormalConfig);
            return;
        }
        if (this.mInnerExpandState == 1) {
            this.mCollapseController.animTo(0.0f, 0, 20, this.mCollapseAnimHideConfig);
            this.mMovableController.animTo(1.0f, 0, 0, this.mMovableAnimNormalConfig);
        } else if (this.mInnerExpandState == 0) {
            this.mCollapseController.animTo(1.0f, 0, 0, this.mCollapseAnimShowConfig);
            this.mMovableController.animTo(0.0f, 0, 0, this.mMovableAnimNormalConfig);
        }
    }

    private void onLayoutCollapseViews(int i, int i2, int i3, int i4) {
        int paddingStart = getPaddingStart();
        int measuredHeight = this.mMainContainer.getMeasuredHeight();
        int i5 = ((i4 - i2) - measuredHeight) / 2;
        if (this.mMainContainer.getVisibility() != 8) {
            View view = this.mMainContainer;
            ViewUtils.layoutChildView(this, view, paddingStart, i5, paddingStart + view.getMeasuredWidth(), i5 + this.mMainContainer.getMeasuredHeight());
        }
        int paddingEnd = (i3 - i) - getPaddingEnd();
        if (this.mMenuView != null && this.mMenuView.getParent() == this) {
            positionChildInverse(this.mMenuView, paddingEnd, i5, measuredHeight);
        }
        if (this.mRequestAnimation) {
            this.mAnimationMode = 1;
            makeContextViewsShowHideWithAnimation(true);
            this.mRequestAnimation = false;
        } else if (this.mMenuView != null) {
            ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) getParent().getParent();
            if (actionBarOverlayLayout.isBottomAnimating()) {
                return;
            }
            actionBarOverlayLayout.onMenuStateChanged(this.mMenuView.getCollapsedHeight(), 1);
        }
    }

    protected void onLayoutExpandViews(boolean z, int i, int i2, int i3, int i4) {
        FrameLayout frameLayout = this.mMovableMainContainer;
        if (frameLayout == null || frameLayout.getVisibility() != 0 || this.mInnerExpandState == 0) {
            return;
        }
        FrameLayout frameLayout2 = this.mMovableMainContainer;
        frameLayout2.layout(i, i4 - frameLayout2.getMeasuredHeight(), i3, i4);
        if (ViewUtils.isLayoutRtl(this)) {
            i = i3 - this.mMovableMainContainer.getMeasuredWidth();
        }
        Rect rect = new Rect();
        rect.set(i, this.mMovableMainContainer.getMeasuredHeight() - (i4 - i2), this.mMovableMainContainer.getMeasuredWidth() + i, this.mMovableMainContainer.getMeasuredHeight());
        this.mMovableMainContainer.setClipBounds(rect);
    }

    public boolean isTitleOptional() {
        return this.mTitleOptional;
    }

    public void setTitleOptional(boolean z) {
        if (z != this.mTitleOptional) {
            requestLayout();
        }
        this.mTitleOptional = z;
    }

    protected void cancelAnimation() {
        SpringAnimation springAnimation = this.mVisibilityAnim;
        if (springAnimation != null) {
            springAnimation.cancel();
            this.mVisibilityAnim = null;
        }
        stopSplitMenuAnimation();
        setSplitAnimating(false);
    }

    protected void endAnimation() {
        SpringAnimation springAnimation = this.mVisibilityAnim;
        if (springAnimation != null) {
            springAnimation.skipToEnd();
            this.mVisibilityAnim = null;
        }
        stopSplitMenuAnimation();
        setSplitAnimating(false);
    }

    private void stopSplitMenuAnimation() {
        if (this.mMenuView != null) {
            Folme.useAt(this.mMenuView).state().setTo(new AnimState().add(ViewProperty.TRANSLATION_Y, this.mAnimateToVisible ? 0 : this.mMenuView.getCollapsedHeight()));
        }
    }

    private void setSplitAnimating(boolean z) {
        if (this.mSplitView != null) {
            ((ActionBarOverlayLayout) this.mSplitView.getParent()).setAnimating(z);
        }
    }

    public float getAnimationProgress() {
        return this.mAnimationProgress;
    }

    public void setAnimationProgress(float f) {
        this.mAnimationProgress = f;
        notifyAnimationUpdate(this.mAnimateToVisible, f);
    }

    protected void makeContextViewsShowHide(boolean z) {
        setAlpha(z ? 1.0f : 0.0f);
        if (!this.mSplitActionBarEnable) {
            onFinishStartActionMode(z);
            return;
        }
        ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) this.mSplitView.getParent();
        if (this.mMenuView != null) {
            int collapsedHeight = this.mMenuView.getCollapsedHeight();
            this.mMenuView.setTranslationY(z ? 0.0f : collapsedHeight);
            if (!z) {
                collapsedHeight = 0;
            }
            actionBarOverlayLayout.animateContentMarginBottomByBottomMenu(collapsedHeight);
            this.mMenuView.setAlpha(z ? 1.0f : 0.0f);
        }
        onFinishStartActionMode(z);
    }

    private void onFinishStartActionMode(boolean z) {
        notifyAnimationEnd(z);
        setVisibility(z ? 0 : 8);
        if (this.mSplitView == null || this.mMenuView == null) {
            return;
        }
        this.mMenuView.setVisibility(z ? 0 : 8);
    }

    protected void makeContextViewsShowHideWithAnimation(final boolean z) {
        int i;
        int i2;
        if (z != this.mAnimateToVisible || this.mVisibilityAnim == null) {
            this.mAnimateToVisible = z;
            this.mAnimateStart = false;
            float f = 0.0f;
            float f2 = 1.0f;
            if (!z) {
                f2 = 0.0f;
                f = 1.0f;
            }
            SpringAnimation viewSpringAnima = getViewSpringAnima(this, z ? STIFFNESS_LOW : STIFFNESS_HIGH, f, f2);
            viewSpringAnima.setStartDelay(z ? 50L : 0L);
            setAlpha(f);
            this.mVisibilityAnim = viewSpringAnima;
            if (!this.mSplitActionBarEnable) {
                final CountDown countDown = new CountDown(1, new CountDown.CountDownCompleteListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarContextView$$ExternalSyntheticLambda0
                    @Override // miuix.appcompat.internal.app.widget.ActionBarContextView.CountDown.CountDownCompleteListener
                    public final void onCountDownComplete() {
                        this.f$0.onAllAnimationsEnd();
                    }
                });
                viewSpringAnima.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarContextView$$ExternalSyntheticLambda1
                    @Override // miuix.animation.physics.DynamicAnimation.OnAnimationEndListener
                    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f3, float f4) {
                        countDown.countDown();
                    }
                });
                viewSpringAnima.start();
                return;
            }
            final CountDown countDown2 = new CountDown(2, new CountDown.CountDownCompleteListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarContextView$$ExternalSyntheticLambda0
                @Override // miuix.appcompat.internal.app.widget.ActionBarContextView.CountDown.CountDownCompleteListener
                public final void onCountDownComplete() {
                    this.f$0.onAllAnimationsEnd();
                }
            });
            viewSpringAnima.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarContextView$$ExternalSyntheticLambda2
                @Override // miuix.animation.physics.DynamicAnimation.OnAnimationEndListener
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f3, float f4) {
                    countDown2.countDown();
                }
            });
            viewSpringAnima.start();
            ActionMenuView actionMenuView = this.mMenuView;
            final ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) getParent().getParent();
            final int collapsedHeight = actionMenuView == null ? 0 : actionMenuView.getCollapsedHeight();
            if (z) {
                i2 = collapsedHeight;
                i = 0;
            } else {
                i = collapsedHeight;
                i2 = 0;
            }
            if (actionMenuView != null) {
                if (this.mMenuAnimConfig == null) {
                    this.mMenuAnimConfig = new AnimConfig().setEase(-2, 0.95f, 0.25f);
                }
                TransitionListener transitionListener = this.mTransitionListener;
                if (transitionListener != null) {
                    this.mMenuAnimConfig.removeListeners(transitionListener);
                }
                AnimConfig animConfig = this.mMenuAnimConfig;
                final int i3 = i;
                final int i4 = i2;
                TransitionListener transitionListener2 = new TransitionListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarContextView.3
                    private final int traceCookie = MiuixTraceUtils.generateUniqueCookie();

                    @Override // miuix.animation.listener.TransitionListener
                    public void onBegin(Object obj) {
                        MiuixTraceUtils.beginAsyncTrace(MiuixTraceUtils.ANIM_TRACE_TAG, this.traceCookie);
                        if (ActionBarContextView.this.mAnimateStart) {
                            return;
                        }
                        ActionBarContextView.this.notifyAnimationStart(z);
                        ActionBarContextView.this.mAnimateStart = true;
                        ActionBarContextView.this.mIsAnimating = true;
                    }

                    @Override // miuix.animation.listener.TransitionListener
                    public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                        UpdateInfo updateInfoFindByName = UpdateInfo.findByName(collection, View.TRANSLATION_Y.getName());
                        if (updateInfoFindByName == null) {
                            return;
                        }
                        float floatValue = updateInfoFindByName.getFloatValue();
                        actionBarOverlayLayout.onMenuStateChanged((int) (collapsedHeight - floatValue), 1);
                        int i5 = i3;
                        int i6 = i4;
                        ActionBarContextView.this.notifyAnimationUpdate(z, i5 == i6 ? 1.0f : (floatValue - i6) / (i5 - i6));
                    }

                    @Override // miuix.animation.listener.TransitionListener
                    public void onComplete(Object obj) {
                        ActionBarContextView.this.mIsAnimating = false;
                        countDown2.countDown();
                        MiuixTraceUtils.endAsyncTrace(MiuixTraceUtils.ANIM_TRACE_TAG, this.traceCookie);
                    }

                    @Override // miuix.animation.listener.TransitionListener
                    public void onCancel(Object obj) {
                        MiuixTraceUtils.endAsyncTrace(MiuixTraceUtils.ANIM_TRACE_TAG, this.traceCookie);
                    }
                };
                this.mTransitionListener = transitionListener2;
                animConfig.addListeners(transitionListener2);
                Folme.useAt(actionMenuView).state().setTo(ViewProperty.TRANSLATION_Y, Integer.valueOf(i2)).to(ViewProperty.TRANSLATION_Y, Integer.valueOf(i), this.mMenuAnimConfig);
                actionBarOverlayLayout.onMenuStateChanged(0, 1);
            }
        }
    }

    public boolean isAnimating() {
        return this.mIsAnimating;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAllAnimationsEnd() {
        setSplitAnimating(false);
        this.mAnimateStart = false;
        notifyAnimationEnd(this.mAnimateToVisible);
        if (this.mAnimationMode == 2) {
            killMode();
        }
        this.mAnimationMode = 0;
        this.mVisibilityAnim = null;
        setVisibility(this.mAnimateToVisible ? 0 : 8);
        if (this.mSplitView != null && this.mMenuView != null) {
            this.mMenuView.setVisibility(this.mAnimateToVisible ? 0 : 8);
        }
        if (this.mMenuView != null) {
            Folme.clean(this.mMenuView);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class CountDown {
        private int mCount;
        private CountDownCompleteListener mCountDownCompleteListener;

        interface CountDownCompleteListener {
            void onCountDownComplete();
        }

        public CountDown(int i, CountDownCompleteListener countDownCompleteListener) {
            this.mCount = i;
            this.mCountDownCompleteListener = countDownCompleteListener;
        }

        public void countDown() {
            int i = this.mCount - 1;
            this.mCount = i;
            if (i == 0) {
                this.mCountDownCompleteListener.onCountDownComplete();
            }
        }
    }

    private SpringAnimation getViewSpringAnima(View view, float f, float f2, float f3) {
        SpringAnimation springAnimation = new SpringAnimation(view, ViewProperty.ALPHA, f3);
        springAnimation.setStartValue(f2);
        springAnimation.getSpring().setStiffness(f);
        springAnimation.getSpring().setDampingRatio(DAMPING);
        springAnimation.setMinimumVisibleChange(0.00390625f);
        return springAnimation;
    }

    public void setButton(int i, CharSequence charSequence) {
        setButton(i, null, charSequence, 0);
    }

    public void setButton(int i, CharSequence charSequence, CharSequence charSequence2, int i2) {
        setButton(i, charSequence, 0, charSequence2, i2);
    }

    public void setButton(int i, CharSequence charSequence, int i2, CharSequence charSequence2, int i3) {
        initTitle();
        Button button = getButton(i);
        bindButtonInfo(button, charSequence2, i3, charSequence);
        bindActionMenuItemInfo(getButtonMenuItem(i), charSequence2);
        if (button != null) {
            button.setImportantForAccessibility(i2);
        }
    }

    public void setButton(int i, CharSequence charSequence, int i2) {
        initTitle();
        Button button = getButton(i);
        bindButtonInfo(button, charSequence, i2, null);
        bindActionMenuItemInfo(getButtonMenuItem(i), charSequence);
        if (!TextUtils.isEmpty(charSequence) || i2 == 0) {
            return;
        }
        setButtonContentDescription(button, i2);
    }

    private void setButtonContentDescription(Button button, int i) {
        if (button == null) {
            return;
        }
        if (miuix.appcompat.R.drawable.miuix_appcompat_action_mode_title_button_cancel == i || miuix.appcompat.R.drawable.miuix_action_icon_cancel_light == i || miuix.appcompat.R.drawable.miuix_action_icon_cancel_dark == i) {
            button.setContentDescription(getResources().getString(miuix.appcompat.R.string.miuix_appcompat_cancel_description));
            return;
        }
        if (miuix.appcompat.R.drawable.miuix_appcompat_action_mode_title_button_confirm == i || miuix.appcompat.R.drawable.miuix_action_icon_immersion_confirm_light == i || miuix.appcompat.R.drawable.miuix_action_icon_immersion_confirm_dark == i) {
            button.setContentDescription(getResources().getString(miuix.appcompat.R.string.miuix_appcompat_confirm_description));
            return;
        }
        if (miuix.appcompat.R.drawable.miuix_appcompat_action_mode_title_button_select_all == i || miuix.appcompat.R.drawable.miuix_action_icon_select_all_light == i || miuix.appcompat.R.drawable.miuix_action_icon_select_all_dark == i) {
            button.setContentDescription(getResources().getString(miuix.appcompat.R.string.miuix_appcompat_select_all_description));
            return;
        }
        if (miuix.appcompat.R.drawable.miuix_appcompat_action_mode_title_button_deselect_all == i || miuix.appcompat.R.drawable.miuix_action_icon_deselect_all_light == i || miuix.appcompat.R.drawable.miuix_action_icon_deselect_all_dark == i) {
            button.setContentDescription(getResources().getString(miuix.appcompat.R.string.miuix_appcompat_deselect_all_description));
        } else if (miuix.appcompat.R.drawable.miuix_appcompat_action_mode_title_button_delete == i || miuix.appcompat.R.drawable.miuix_action_icon_immersion_delete_light == i || miuix.appcompat.R.drawable.miuix_action_icon_immersion_delete_dark == i) {
            button.setContentDescription(getResources().getString(miuix.appcompat.R.string.miuix_appcompat_delete_description));
        }
    }

    private void bindButtonInfo(Button button, CharSequence charSequence, int i, CharSequence charSequence2) {
        if (button == null) {
            return;
        }
        button.setVisibility((TextUtils.isEmpty(charSequence) && i == 0) ? 8 : 0);
        button.setText(charSequence);
        button.setBackgroundResource(i);
        if (!TextUtils.isEmpty(charSequence2)) {
            button.setContentDescription(charSequence2);
        }
        if (TextUtils.isEmpty(charSequence) && i != 0) {
            button.setMaxHeight(getContext().getResources().getDimensionPixelSize(miuix.appcompat.R.dimen.miuix_appcompat_action_mode_title_button_height));
        } else {
            button.setMaxHeight(Integer.MAX_VALUE);
        }
    }

    private void bindActionMenuItemInfo(ActionMenuItem actionMenuItem, CharSequence charSequence) {
        if (actionMenuItem == null) {
            return;
        }
        actionMenuItem.setTitle(charSequence);
    }

    private Button getButton(int i) {
        if (i == 16908313) {
            return this.mButton1;
        }
        if (i == 16908314) {
            return this.mButton2;
        }
        return null;
    }

    private ActionMenuItem getButtonMenuItem(int i) {
        if (i == 16908313) {
            return this.mButton1MenuItem;
        }
        if (i == 16908314) {
            return this.mButton2MenuItem;
        }
        return null;
    }

    @Override // miuix.appcompat.internal.app.widget.ActionModeView
    public void animateToVisibility(boolean z) {
        cancelAnimation();
        setSplitAnimating(this.mStartWithAnim);
        if (z) {
            if (this.mStartWithAnim) {
                setVisibility(0);
                this.mRequestAnimation = true;
                return;
            } else {
                makeContextViewsShowHide(true);
                return;
            }
        }
        if (this.mStartWithAnim) {
            makeContextViewsShowHideWithAnimation(false);
        } else {
            makeContextViewsShowHide(false);
        }
    }

    @Override // miuix.appcompat.internal.app.widget.ActionModeView
    public void notifyAnimationStart(boolean z) {
        List<ActionModeAnimationListener> list = this.mAnimationListeners;
        if (list == null) {
            return;
        }
        Iterator<ActionModeAnimationListener> it = list.iterator();
        while (it.hasNext()) {
            it.next().onStart(z);
        }
    }

    @Override // miuix.appcompat.internal.app.widget.ActionModeView
    public void notifyAnimationUpdate(boolean z, float f) {
        List<ActionModeAnimationListener> list = this.mAnimationListeners;
        if (list == null) {
            return;
        }
        Iterator<ActionModeAnimationListener> it = list.iterator();
        while (it.hasNext()) {
            it.next().onUpdate(z, f);
        }
    }

    @Override // miuix.appcompat.internal.app.widget.ActionModeView
    public void notifyAnimationEnd(boolean z) {
        List<ActionModeAnimationListener> list = this.mAnimationListeners;
        if (list == null) {
            return;
        }
        Iterator<ActionModeAnimationListener> it = list.iterator();
        while (it.hasNext()) {
            it.next().onStop(z);
        }
    }

    @Override // miuix.appcompat.internal.app.widget.ActionModeView
    public void addAnimationListener(ActionModeAnimationListener actionModeAnimationListener) {
        if (actionModeAnimationListener == null) {
            return;
        }
        if (this.mAnimationListeners == null) {
            this.mAnimationListeners = new ArrayList();
        }
        this.mAnimationListeners.add(actionModeAnimationListener);
    }

    @Override // miuix.appcompat.internal.app.widget.ActionModeView
    public void removeAnimationListener(ActionModeAnimationListener actionModeAnimationListener) {
        List<ActionModeAnimationListener> list;
        if (actionModeAnimationListener == null || (list = this.mAnimationListeners) == null) {
            return;
        }
        list.remove(actionModeAnimationListener);
    }

    @Override // miuix.appcompat.internal.app.widget.ActionModeView
    public int getViewHeight() {
        return getCollapsedHeight();
    }

    private static class SavedState extends View.BaseSavedState {
        public static final Parcelable.ClassLoaderCreator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: miuix.appcompat.internal.app.widget.ActionBarContextView.SavedState.1
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }

            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }
        };
        public CharSequence defaultButtonText;
        public int expandState;
        public boolean isOverflowOpen;
        public CharSequence title;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.isOverflowOpen = parcel.readInt() != 0;
            this.title = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            this.defaultButtonText = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            this.expandState = parcel.readInt();
        }

        private SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.isOverflowOpen = parcel.readInt() != 0;
            this.title = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            this.defaultButtonText = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            this.expandState = parcel.readInt();
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.isOverflowOpen ? 1 : 0);
            TextUtils.writeToParcel(this.title, parcel, 0);
            TextUtils.writeToParcel(this.defaultButtonText, parcel, 0);
            parcel.writeInt(this.expandState);
        }
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    protected void onExpandStateChanged(int i, int i2) {
        AbsActionBarView.CollapseView collapseView;
        if (i == 2) {
            this.mPendingHeight = 0;
            if (!this.mPostScroller.isFinished()) {
                this.mPostScroller.forceFinished(true);
            }
        }
        if (i2 == 2 && (collapseView = this.mMovableController) != null) {
            collapseView.setVisibility(0);
        }
        if (i2 == 1) {
            if (this.mMovableMainContainer.getAlpha() > 0.0f) {
                AbsActionBarView.CollapseView collapseView2 = this.mCollapseController;
                if (collapseView2 != null) {
                    collapseView2.setAnimFrom(0.0f, 0, 20, true);
                }
                AbsActionBarView.CollapseView collapseView3 = this.mMovableController;
                if (collapseView3 != null) {
                    collapseView3.setAnimFrom(1.0f, 0, 0, true);
                }
            }
            AbsActionBarView.CollapseView collapseView4 = this.mMovableController;
            if (collapseView4 != null) {
                collapseView4.setVisibility(0);
            }
        }
        if (i2 == 0) {
            AbsActionBarView.CollapseView collapseView5 = this.mCollapseController;
            if (collapseView5 != null) {
                collapseView5.setAnimFrom(1.0f, 0, 0, true);
                this.mCollapseController.setVisibility(0);
                this.mCollapseController.onShow();
            }
            AbsActionBarView.CollapseView collapseView6 = this.mMovableController;
            if (collapseView6 != null) {
                collapseView6.setAnimFrom(0.0f, 0, 0, true);
                this.mMovableController.setVisibility(8);
                return;
            }
            return;
        }
        this.mPendingHeight = getHeight() - this.mCollapseTotalHeight;
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr, int[] iArr2) {
        if (isResizable()) {
            int measuredHeight = this.mMovableMainContainer.getMeasuredHeight();
            int i6 = this.mCollapseTotalHeight + measuredHeight;
            int height = getHeight();
            if (i4 >= 0 || height >= i6) {
                return;
            }
            int i7 = this.mPendingHeight;
            if (height - i4 <= i6) {
                this.mPendingHeight = i7 - i4;
                iArr[1] = iArr[1] + i4;
            } else {
                this.mPendingHeight = measuredHeight;
                iArr[1] = iArr[1] + (-(i6 - height));
            }
            if (this.mPendingHeight != i7) {
                if (this.mInnerExpandState != 2) {
                    if (!this.mPostScroller.isFinished()) {
                        this.mPostScroller.forceFinished(true);
                    }
                    setExpandState(2);
                }
                iArr2[1] = i4;
                requestLayout();
            }
        }
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public void onNestedScrollAccepted(View view, View view2, int i, int i2) {
        if (isResizable()) {
            if (i2 == 0) {
                this.mTouchScrolling = true;
            } else {
                this.mNonTouchScrolling = true;
            }
            if (!this.mPostScroller.isFinished()) {
                this.mPostScroller.forceFinished(true);
                Runnable runnable = this.mPostScroll;
                if (runnable != null) {
                    removeCallbacks(runnable);
                }
            }
            setExpandState(2);
        }
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public void onStopNestedScroll(View view, int i) {
        if (isResizable()) {
            int measuredHeight = this.mMovableMainContainer.getMeasuredHeight();
            int height = getHeight();
            if (this.mTouchScrolling) {
                this.mTouchScrolling = false;
                if (this.mNonTouchScrolling) {
                    return;
                }
            } else if (!this.mNonTouchScrolling) {
                return;
            } else {
                this.mNonTouchScrolling = false;
            }
            int i2 = this.mPendingHeight;
            if (i2 == 0) {
                setExpandState(0);
                return;
            }
            if (i2 == measuredHeight) {
                setExpandState(1);
                return;
            }
            int i3 = this.mCollapseTotalHeight;
            if (height > (measuredHeight / 2) + i3) {
                this.mPostScroller.startScroll(0, height, 0, (i3 + measuredHeight) - height);
            } else {
                this.mPostScroller.startScroll(0, height, 0, i3 - height);
            }
            this.mHandler.postDelayed(this.mPostAnimationRunnable, 17L);
        }
    }

    @Override // miuix.appcompat.internal.app.widget.AbsActionBarView
    public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3, int[] iArr2) {
        int i4;
        if (isResizable()) {
            int height = getHeight();
            if (i2 <= 0 || height <= (i4 = this.mCollapseTotalHeight)) {
                return;
            }
            int i5 = height - i2;
            int i6 = this.mPendingHeight;
            if (i5 >= i4) {
                this.mPendingHeight = i6 - i2;
            } else {
                this.mPendingHeight = 0;
            }
            iArr[1] = iArr[1] + i2;
            if (this.mPendingHeight != i6) {
                if (this.mInnerExpandState != 2) {
                    if (!this.mPostScroller.isFinished()) {
                        this.mPostScroller.forceFinished(true);
                    }
                    setExpandState(2);
                }
                iArr2[1] = i2;
                requestLayout();
            }
        }
    }
}
