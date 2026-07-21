package miuix.appcompat.app.floatingactivity.helper;

import android.R;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import androidx.core.view.ViewCompat;
import java.lang.ref.WeakReference;
import java.util.Collection;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ViewProperty;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.floatingactivity.FloatingAnimHelper;
import miuix.appcompat.app.floatingactivity.FloatingSwitcherAnimHelper;
import miuix.appcompat.app.floatingactivity.OnFloatingActivityCallback;
import miuix.appcompat.app.floatingactivity.OnFloatingCallback;
import miuix.appcompat.widget.dialoganim.DimAnimator;
import miuix.core.util.IntentUtils;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;
import miuix.internal.widget.RoundFrameLayout;
import miuix.theme.token.DimToken;
import miuix.view.CompatViewMethod;

/* JADX INFO: loaded from: classes2.dex */
public abstract class TabletFloatingActivityHelper extends BaseFloatingActivityHelper {
    private static final String ANIM_TAG_DISMISS = "dismiss";
    private static final String ANIM_TAG_INIT = "init";
    private static final int GESTURE_ENABLE_DELAY_TIME = 500;
    private static final float MOVE_DISTANCE_RATIO = 0.5f;
    private static final int PANEL_SHOW_DELAY_TIME = 90;
    protected AppCompatActivity mActivity;
    private View mBg;
    private final Drawable mDefaultPanelBg;
    private ViewGroup.LayoutParams mFloatingLayoutParam;
    private float mFloatingRadius;
    private View mFloatingRoot;
    private View mHandle;
    private float mLastMoveY;
    private float mMoveMaxY;
    private float mOffsetY;
    private OnFloatingActivityCallback mOnFloatingActivityCallback;
    private OnFloatingCallback mOnFloatingCallback;
    private View mPanel;
    private View mPanelParent;
    private GestureDetector mRootViewGestureDetector;
    private RoundFrameLayout mRoundFrameLayout;
    private float mTouchDownY;
    private float mBgAlpha = 1.0f;
    private boolean mEnableSwipeToDismiss = true;
    private final Handler mFloatingActivitySlidDownHandler = new Handler(Looper.getMainLooper());
    private boolean mAnimationDoing = false;
    private boolean mIsFloatingWindow = true;
    private boolean mIsBorderEnable = false;
    private int mFloatingActivityFinishingFlag = 0;

    public void execExitAnim() {
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public boolean shouldInterceptBack() {
        return true;
    }

    public TabletFloatingActivityHelper(AppCompatActivity appCompatActivity) {
        this.mActivity = appCompatActivity;
        this.mDefaultPanelBg = AttributeResolver.resolveDrawable(appCompatActivity, R.attr.windowBackground);
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void setFloatingWindowMode(boolean z) {
        this.mIsFloatingWindow = z;
        if (!IntentUtils.isIntentFromSettingsSplit(this.mActivity.getIntent())) {
            CompatViewMethod.setActivityTranslucent(this.mActivity, true);
        }
        if (this.mBg != null && this.mOnFloatingCallback.isFirstPage()) {
            this.mBg.setVisibility(z ? 0 : 8);
        }
        if (this.mRoundFrameLayout != null) {
            float dimensionPixelSize = this.mActivity.getResources().getDimensionPixelSize(miuix.appcompat.R.dimen.miuix_appcompat_floating_window_background_radius);
            this.mFloatingRadius = dimensionPixelSize;
            RoundFrameLayout roundFrameLayout = this.mRoundFrameLayout;
            if (!z) {
                dimensionPixelSize = 0.0f;
            }
            roundFrameLayout.setRadius(dimensionPixelSize);
            setRoundFrameLayoutBorder(this.mRoundFrameLayout);
        }
        if (this.mPanel != null) {
            if (!z && ViewUtils.isNightMode(this.mActivity)) {
                this.mPanel.setBackground(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK));
            } else {
                this.mPanel.setBackground(this.mDefaultPanelBg);
            }
        }
        View view = this.mHandle;
        if (view != null) {
            if (this.mEnableSwipeToDismiss && this.mIsFloatingWindow) {
                view.setVisibility(0);
            } else {
                view.setVisibility(8);
            }
        }
    }

    private void setRoundFrameLayoutBorder(RoundFrameLayout roundFrameLayout) {
        if (this.mIsFloatingWindow && this.mIsBorderEnable) {
            roundFrameLayout.setBorder(this.mActivity.getResources().getDimensionPixelSize(miuix.appcompat.R.dimen.miuix_appcompat_floating_window_background_border_width), AttributeResolver.resolveColor(this.mActivity, miuix.appcompat.R.attr.miuixAppcompatFloatingWindowBorderColor, 0));
        } else {
            roundFrameLayout.setBorder(0.0f, 0);
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void setFloatingWindowBorderEnable(boolean z) {
        this.mIsBorderEnable = z;
        RoundFrameLayout roundFrameLayout = this.mRoundFrameLayout;
        if (roundFrameLayout != null) {
            setRoundFrameLayoutBorder(roundFrameLayout);
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public ViewGroup replaceSubDecor(View view, boolean z) {
        ViewGroup viewGroup = (ViewGroup) View.inflate(this.mActivity, miuix.appcompat.R.layout.miuix_appcompat_screen_floating_window, null);
        View viewFindViewById = viewGroup.findViewById(miuix.appcompat.R.id.action_bar_overlay_layout);
        View viewFindViewById2 = viewGroup.findViewById(miuix.appcompat.R.id.sliding_drawer_handle);
        if (viewFindViewById2 != null && (viewFindViewById2.getParent() instanceof ViewGroup)) {
            ((ViewGroup) viewFindViewById2.getParent()).removeView(viewFindViewById2);
        }
        if (view instanceof ViewGroup) {
            ((ViewGroup) view).addView(viewFindViewById2);
        }
        ViewGroup.LayoutParams layoutParams = viewFindViewById.getLayoutParams();
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height, 17);
        this.mFloatingLayoutParam = layoutParams2;
        if (!z) {
            ((ViewGroup.LayoutParams) layoutParams2).width = -1;
            this.mFloatingLayoutParam.height = -1;
        } else {
            ((ViewGroup.LayoutParams) layoutParams2).height = -2;
            this.mFloatingLayoutParam.width = -2;
        }
        viewGroup.removeView(viewFindViewById);
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
        this.mFloatingRadius = this.mActivity.getResources().getDimensionPixelSize(miuix.appcompat.R.dimen.miuix_appcompat_floating_window_background_radius);
        RoundFrameLayout roundFrameLayout = new RoundFrameLayout(this.mActivity);
        this.mRoundFrameLayout = roundFrameLayout;
        roundFrameLayout.setLayoutParams(this.mFloatingLayoutParam);
        this.mRoundFrameLayout.addView(view);
        this.mRoundFrameLayout.setRadius(z ? this.mFloatingRadius : 0.0f);
        setRoundFrameLayoutBorder(this.mRoundFrameLayout);
        panelDelayShow();
        viewGroup.addView(this.mRoundFrameLayout);
        setPanelParent(this.mRoundFrameLayout);
        return viewGroup;
    }

    private void panelDelayShow() {
        if (this.mIsFloatingWindow) {
            final float alpha = this.mRoundFrameLayout.getAlpha();
            this.mRoundFrameLayout.setAlpha(0.0f);
            this.mRoundFrameLayout.postDelayed(new Runnable() { // from class: miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1823xa406446a(alpha);
                }
            }, 90L);
        }
    }

    /* JADX INFO: renamed from: lambda$panelDelayShow$0$miuix-appcompat-app-floatingactivity-helper-TabletFloatingActivityHelper, reason: not valid java name */
    /* synthetic */ void m1823xa406446a(float f) {
        this.mRoundFrameLayout.setAlpha(f);
    }

    private void setPanelParent(View view) {
        this.mPanelParent = view;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void init(View view, boolean z) {
        this.mHandle = view.findViewById(miuix.appcompat.R.id.sliding_drawer_handle);
        View viewFindViewById = view.findViewById(miuix.appcompat.R.id.action_bar_overlay_bg);
        this.mBg = viewFindViewById;
        viewFindViewById.setVisibility(z ? 0 : 8);
        boolean zResolveBoolean = AttributeResolver.resolveBoolean(view.getContext(), R.attr.isLightTheme, true);
        this.mPanel = view.findViewById(miuix.appcompat.R.id.action_bar_overlay_layout);
        this.mFloatingRoot = view.findViewById(miuix.appcompat.R.id.action_bar_overlay_floating_root);
        this.mIsFloatingWindow = z;
        this.mRootViewGestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() { // from class: miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper.1
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                if (TabletFloatingActivityHelper.this.mEnableSwipeToDismiss && !TabletFloatingActivityHelper.this.mAnimationDoing && !TabletFloatingActivityHelper.this.mActivity.isFinishing()) {
                    TabletFloatingActivityHelper.this.getSnapShotAndSetPanel();
                    TabletFloatingActivityHelper.this.makeDownMoveMaxY();
                    TabletFloatingActivityHelper.this.notifyPageHide();
                    TabletFloatingActivityHelper.this.triggerFinishCallback(true, 2);
                }
                return true;
            }
        });
        this.mFloatingRoot.postDelayed(new Runnable() { // from class: miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1821xfcdfa654();
            }
        }, 500L);
        this.mHandle.setOnTouchListener(new View.OnTouchListener() { // from class: miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper$$ExternalSyntheticLambda2
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                return this.f$0.m1822x17509f73(view2, motionEvent);
            }
        });
        firstFloatingTranslationTop(zResolveBoolean);
        this.mActivity.getWindow().setBackgroundDrawableResource(miuix.appcompat.R.color.miuix_appcompat_transparent);
        if (!this.mIsFloatingWindow && ViewUtils.isNightMode(this.mActivity)) {
            this.mPanel.setBackground(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK));
        } else {
            this.mPanel.setBackground(this.mDefaultPanelBg);
        }
        if (this.mEnableSwipeToDismiss && this.mIsFloatingWindow) {
            this.mHandle.setVisibility(0);
        } else {
            this.mHandle.setVisibility(8);
        }
    }

    /* JADX INFO: renamed from: lambda$init$2$miuix-appcompat-app-floatingactivity-helper-TabletFloatingActivityHelper, reason: not valid java name */
    /* synthetic */ void m1821xfcdfa654() {
        this.mFloatingRoot.setOnTouchListener(new View.OnTouchListener() { // from class: miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper$$ExternalSyntheticLambda4
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return this.f$0.m1820xe26ead35(view, motionEvent);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$init$1$miuix-appcompat-app-floatingactivity-helper-TabletFloatingActivityHelper, reason: not valid java name */
    /* synthetic */ boolean m1820xe26ead35(View view, MotionEvent motionEvent) {
        this.mRootViewGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    /* JADX INFO: renamed from: lambda$init$3$miuix-appcompat-app-floatingactivity-helper-TabletFloatingActivityHelper, reason: not valid java name */
    /* synthetic */ boolean m1822x17509f73(View view, MotionEvent motionEvent) {
        if (!this.mEnableSwipeToDismiss) {
            return true;
        }
        handleFingerMove(motionEvent);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyPageHide() {
        OnFloatingCallback onFloatingCallback = this.mOnFloatingCallback;
        if (onFloatingCallback != null) {
            onFloatingCallback.onHideBehindPage();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void triggerFinishCallback(boolean z, int i) {
        updateFloatingActivityFinishingFlag(i);
        if (z) {
            OnFloatingActivityCallback onFloatingActivityCallback = this.mOnFloatingActivityCallback;
            if (onFloatingActivityCallback != null && onFloatingActivityCallback.onFinish(i)) {
                m1818xb831e1bc(false, i);
                return;
            } else {
                OnFloatingCallback onFloatingCallback = this.mOnFloatingCallback;
                m1818xb831e1bc(onFloatingCallback == null || !onFloatingCallback.onFinish(i), i);
                return;
            }
        }
        m1818xb831e1bc(false, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: executeFolme, reason: merged with bridge method [inline-methods] */
    public void m1818xb831e1bc(final boolean z, final int i) {
        Object obj;
        float f;
        int i2;
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            this.mActivity.runOnUiThread(new Runnable() { // from class: miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1818xb831e1bc(z, i);
                }
            });
            return;
        }
        if (this.mAnimationDoing && z) {
            return;
        }
        this.mAnimationDoing = true;
        if (z) {
            i2 = (int) this.mMoveMaxY;
            obj = ANIM_TAG_DISMISS;
            f = 0.0f;
        } else {
            float f2 = this.mBgAlpha;
            obj = ANIM_TAG_INIT;
            f = f2;
            i2 = 0;
        }
        AnimConfig animConfig = FloatingSwitcherAnimHelper.getAnimConfig(z ? 2 : 1, null);
        animConfig.addListeners(new FloatingAnimTransitionListener(z, i2, i));
        AnimState animStateAdd = new AnimState(obj).add(ViewProperty.TRANSLATION_Y, i2);
        AnimState animStateAdd2 = new AnimState(obj).add(ViewProperty.ALPHA, f);
        Folme.useAt(getAnimPanel()).state().to(animStateAdd, animConfig);
        Folme.useAt(this.mBg).state().to(animStateAdd2, new AnimConfig[0]);
    }

    public void finishAllPage() {
        Runnable runnable = new Runnable() { // from class: miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper.2
            @Override // java.lang.Runnable
            public void run() {
                if (TabletFloatingActivityHelper.this.mOnFloatingCallback != null) {
                    TabletFloatingActivityHelper.this.mOnFloatingCallback.closeAllPage();
                }
            }
        };
        View view = this.mBg;
        if (view != null) {
            view.post(runnable);
        } else {
            runnable.run();
        }
    }

    private View getAnimPanel() {
        View view = this.mPanelParent;
        return view == null ? this.mPanel : view;
    }

    private void notifyDragEnd() {
        Runnable runnable = new Runnable() { // from class: miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper.3
            @Override // java.lang.Runnable
            public void run() {
                if (TabletFloatingActivityHelper.this.mOnFloatingCallback != null) {
                    TabletFloatingActivityHelper.this.mOnFloatingCallback.onDragEnd();
                }
            }
        };
        View view = this.mBg;
        if (view != null) {
            view.post(runnable);
        } else {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onEnd(Object obj) {
        if (TextUtils.equals(ANIM_TAG_DISMISS, obj.toString())) {
            View view = this.mBg;
            if (view != null) {
                view.post(new Runnable() { // from class: miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper.4
                    @Override // java.lang.Runnable
                    public void run() {
                        TabletFloatingActivityHelper.this.mActivity.realFinish();
                    }
                });
            } else {
                this.mActivity.realFinish();
            }
        } else if (TextUtils.equals(ANIM_TAG_INIT, obj.toString())) {
            notifyDragEnd();
        }
        this.mAnimationDoing = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFloatingActivityFinishingFlag(int i) {
        this.mFloatingActivityFinishingFlag = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getSnapShotAndSetPanel() {
        OnFloatingCallback onFloatingCallback;
        if (FloatingAnimHelper.isSupportTransWithClipAnim() || (onFloatingCallback = this.mOnFloatingCallback) == null || !this.mEnableSwipeToDismiss) {
            return;
        }
        onFloatingCallback.getSnapShotAndSetPanel(this.mActivity);
    }

    private void handleFingerMove(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            notifyDragStart();
            float rawY = motionEvent.getRawY();
            this.mTouchDownY = rawY;
            this.mLastMoveY = rawY;
            this.mOffsetY = 0.0f;
            makeDownMoveMaxY();
            return;
        }
        if (action == 1) {
            boolean z = motionEvent.getRawY() - this.mTouchDownY > ((float) this.mPanel.getHeight()) * 0.5f;
            updateFloatingActivityFinishingFlag(1);
            if (z) {
                getSnapShotAndSetPanel();
                OnFloatingCallback onFloatingCallback = this.mOnFloatingCallback;
                m1818xb831e1bc(onFloatingCallback == null || !onFloatingCallback.onFinish(1), 1);
                return;
            }
            m1818xb831e1bc(false, 1);
            return;
        }
        if (action != 2) {
            return;
        }
        float rawY2 = motionEvent.getRawY();
        float f = this.mOffsetY + (rawY2 - this.mLastMoveY);
        this.mOffsetY = f;
        if (f >= 0.0f) {
            movePanel(f);
            dimBg(this.mOffsetY / this.mMoveMaxY);
        }
        this.mLastMoveY = rawY2;
    }

    private void firstFloatingTranslationTop(final boolean z) {
        this.mPanel.post(new Runnable() { // from class: miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1819xdb79c181(z);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$firstFloatingTranslationTop$5$miuix-appcompat-app-floatingactivity-helper-TabletFloatingActivityHelper, reason: not valid java name */
    /* synthetic */ void m1819xdb79c181(boolean z) {
        float curThemeAlpha;
        if (isEnableFirstFloatingTranslationY()) {
            markActivityOpenEnterAnimExecuted();
            folmeShow();
            this.mBgAlpha = getCurThemeAlpha(z);
            curThemeAlpha = 0.0f;
        } else {
            curThemeAlpha = getCurThemeAlpha(z);
            this.mBgAlpha = curThemeAlpha;
        }
        this.mBg.setAlpha(curThemeAlpha);
    }

    private float getCurThemeAlpha(boolean z) {
        return z ? DimToken.DIM_LIGHT : DimToken.DIM_DARK;
    }

    private boolean isEnableFirstFloatingTranslationY() {
        return this.mIsFloatingWindow && isFirstPageEnterAnimExecuteEnable();
    }

    private void markActivityOpenEnterAnimExecuted() {
        OnFloatingCallback onFloatingCallback = this.mOnFloatingCallback;
        if (onFloatingCallback != null) {
            onFloatingCallback.markActivityOpenEnterAnimExecuted(this.mActivity);
        }
    }

    private boolean isFirstPageEnterAnimExecuteEnable() {
        OnFloatingCallback onFloatingCallback = this.mOnFloatingCallback;
        if (onFloatingCallback == null) {
            return true;
        }
        return onFloatingCallback.isFirstPageEnterAnimExecuteEnable();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isFirstPageExitAnimExecuteEnable() {
        OnFloatingCallback onFloatingCallback;
        return this.mIsFloatingWindow && ((onFloatingCallback = this.mOnFloatingCallback) == null || onFloatingCallback.isFirstPageExitAnimExecuteEnable());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void singleFloatingSlipExit(boolean z, int i) {
        if (!z || this.mAnimationDoing) {
            return;
        }
        makeDownMoveMaxY();
        notifyPageHide();
        m1818xb831e1bc(true, i);
    }

    private void notifyDragStart() {
        OnFloatingCallback onFloatingCallback = this.mOnFloatingCallback;
        if (onFloatingCallback != null) {
            onFloatingCallback.onDragStart();
        }
    }

    private void movePanel(float f) {
        getAnimPanel().setTranslationY(f);
    }

    private void dimBg(float f) {
        this.mBg.setAlpha(this.mBgAlpha * (1.0f - Math.max(0.0f, Math.min(f, 1.0f))));
    }

    private void folmeShow() {
        View animPanel = getAnimPanel();
        Folme.useAt(animPanel).state().setTo(ViewProperty.TRANSLATION_Y, Integer.valueOf(animPanel.getHeight() + ((this.mFloatingRoot.getHeight() - animPanel.getHeight()) / 2))).to(ViewProperty.TRANSLATION_Y, 0, FloatingSwitcherAnimHelper.getAnimConfig(1, null));
        DimAnimator.show(this.mBg);
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void hideFloatingDimBackground() {
        this.mBg.setVisibility(8);
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void hideFloatingBrightPanel() {
        this.mPanel.setVisibility(8);
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void showFloatingBrightPanel() {
        this.mPanel.setVisibility(0);
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void setEnableSwipToDismiss(boolean z) {
        this.mEnableSwipeToDismiss = z;
        if (z && this.mIsFloatingWindow) {
            this.mHandle.setVisibility(0);
        } else {
            this.mHandle.setVisibility(8);
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public View getFloatingBrightPanel() {
        return this.mPanel;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void onBackPressed() {
        if (this.mIsFloatingWindow && !FloatingAnimHelper.isSupportTransWithClipAnim()) {
            getSnapShotAndSetPanel();
        }
        backOneByOne(4);
    }

    private void backOneByOne(int i) {
        updateFloatingActivityFinishingFlag(i);
        if (!isFirstPageExitAnimExecuteEnable()) {
            this.mActivity.realFinish();
            FloatingAnimHelper.singleAppFloatingActivityExit(this.mActivity);
        } else if (!this.mAnimationDoing) {
            triggerBottomExit(i);
        }
        execExitAnim();
    }

    protected boolean isFloatingWindow() {
        return this.mIsFloatingWindow;
    }

    private void triggerBottomExit(int i) {
        makeDownMoveMaxY();
        notifyPageHide();
        m1818xb831e1bc(true, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void makeDownMoveMaxY() {
        View animPanel = getAnimPanel();
        this.mMoveMaxY = animPanel.getHeight() + ((this.mFloatingRoot.getHeight() - animPanel.getHeight()) / 2);
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public boolean delegateFinishFloatingActivityInternal() {
        if (FloatingAnimHelper.isSupportTransWithClipAnim()) {
            return delegateFinishTransWithClipAnimInternal();
        }
        if (this.mIsFloatingWindow) {
            getSnapShotAndSetPanel();
            this.mFloatingActivitySlidDownHandler.postDelayed(new FinishFloatingActivityDelegate(this, this.mActivity), 110L);
            return true;
        }
        this.mActivity.realFinish();
        execExitAnim();
        return true;
    }

    private boolean delegateFinishTransWithClipAnimInternal() {
        new FinishFloatingActivityDelegate(this, this.mActivity).delegatePadPhoneFinishFloatingActivity(true);
        return true;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public ViewGroup.LayoutParams getFloatingLayoutParam() {
        return this.mFloatingLayoutParam;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void setOnFloatingWindowCallback(OnFloatingActivityCallback onFloatingActivityCallback) {
        this.mOnFloatingActivityCallback = onFloatingActivityCallback;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void setOnFloatingCallback(OnFloatingCallback onFloatingCallback) {
        this.mOnFloatingCallback = onFloatingCallback;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void exitFloatingActivityAll() {
        getSnapShotAndSetPanel();
        makeDownMoveMaxY();
        notifyPageHide();
        triggerFinishCallback(true, 0);
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation
    public void executeOpenEnterAnimation() {
        if (this.mIsFloatingWindow) {
            FloatingSwitcherAnimHelper.executeOpenEnterAnimation(this.mPanel);
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation
    public void executeOpenExitAnimation() {
        if (this.mIsFloatingWindow) {
            FloatingSwitcherAnimHelper.executeOpenExitAnimation(this.mPanel);
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation
    public void executeCloseEnterAnimation() {
        if (this.mIsFloatingWindow) {
            FloatingSwitcherAnimHelper.executeCloseEnterAnimation(this.mPanel);
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation
    public void executeCloseExitAnimation() {
        if (this.mIsFloatingWindow) {
            FloatingSwitcherAnimHelper.executeCloseExitAnimation(this.mPanel);
        }
    }

    private static class FinishFloatingActivityDelegate implements Runnable {
        private WeakReference<AppCompatActivity> mActivity;
        private WeakReference<TabletFloatingActivityHelper> mRefs;

        public FinishFloatingActivityDelegate(TabletFloatingActivityHelper tabletFloatingActivityHelper, AppCompatActivity appCompatActivity) {
            this.mRefs = new WeakReference<>(tabletFloatingActivityHelper);
            this.mActivity = new WeakReference<>(appCompatActivity);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void delegatePadPhoneFinishFloatingActivity(boolean z) {
            TabletFloatingActivityHelper tabletFloatingActivityHelper = this.mRefs.get();
            if (tabletFloatingActivityHelper != null) {
                tabletFloatingActivityHelper.updateFloatingActivityFinishingFlag(3);
            }
            AppCompatActivity appCompatActivity = this.mActivity.get();
            if (tabletFloatingActivityHelper != null) {
                activityExitActuator(appCompatActivity, tabletFloatingActivityHelper, true, 3, z);
            }
        }

        private void activityExitActuator(AppCompatActivity appCompatActivity, TabletFloatingActivityHelper tabletFloatingActivityHelper, boolean z, int i, boolean z2) {
            if (tabletFloatingActivityHelper.isFirstPageExitAnimExecuteEnable()) {
                tabletFloatingActivityHelper.singleFloatingSlipExit(z, i);
            } else if (appCompatActivity != null) {
                appCompatActivity.realFinish();
                preformFloatingExitAnimWithClip(appCompatActivity, tabletFloatingActivityHelper, z2);
            }
        }

        private void preformFloatingExitAnimWithClip(AppCompatActivity appCompatActivity, TabletFloatingActivityHelper tabletFloatingActivityHelper, boolean z) {
            if (z) {
                FloatingAnimHelper.preformFloatingExitAnimWithClip(appCompatActivity, tabletFloatingActivityHelper.mIsFloatingWindow);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            delegatePadPhoneFinishFloatingActivity(false);
        }
    }

    private static class FloatingAnimTransitionListener extends TransitionListener {
        private boolean mAllActivityFinished;
        private boolean mDismiss;
        private WeakReference<TabletFloatingActivityHelper> mRefs;
        private int mTranslationY;
        private int mType;

        private FloatingAnimTransitionListener(TabletFloatingActivityHelper tabletFloatingActivityHelper, boolean z, int i, int i2) {
            this.mAllActivityFinished = false;
            this.mRefs = new WeakReference<>(tabletFloatingActivityHelper);
            this.mType = i2;
            this.mDismiss = z;
            this.mTranslationY = i;
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
            UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, ViewProperty.TRANSLATION_Y);
            if (!this.mDismiss || updateInfoFindBy == null) {
                return;
            }
            TabletFloatingActivityHelper tabletFloatingActivityHelper = this.mRefs.get();
            if (this.mAllActivityFinished || updateInfoFindBy.getFloatValue() <= this.mTranslationY * 0.6f || tabletFloatingActivityHelper == null) {
                return;
            }
            this.mAllActivityFinished = true;
            tabletFloatingActivityHelper.finishAllPage();
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj) {
            super.onComplete(obj);
            WeakReference<TabletFloatingActivityHelper> weakReference = this.mRefs;
            TabletFloatingActivityHelper tabletFloatingActivityHelper = weakReference == null ? null : weakReference.get();
            if (tabletFloatingActivityHelper != null) {
                tabletFloatingActivityHelper.onEnd(obj);
            }
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onCancel(Object obj) {
            super.onCancel(obj);
            WeakReference<TabletFloatingActivityHelper> weakReference = this.mRefs;
            TabletFloatingActivityHelper tabletFloatingActivityHelper = weakReference == null ? null : weakReference.get();
            if (tabletFloatingActivityHelper != null) {
                tabletFloatingActivityHelper.onEnd(obj);
            }
        }
    }
}
