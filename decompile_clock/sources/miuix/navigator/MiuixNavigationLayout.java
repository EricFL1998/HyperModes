package miuix.navigator;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.util.Consumer;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.utils.EaseManager;
import miuix.appcompat.internal.app.NavigatorSwitchPresenter;
import miuix.appcompat.internal.app.widget.ActionBarOverlayLayout;
import miuix.core.util.EnvStateManager;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.WindowBaseInfo;
import miuix.internal.util.AttributeResolver;
import miuix.navigator.draganddrop.DragOnTrigger;
import miuix.os.DeviceHelper;
import miuix.recyclerview.widget.RecyclerView;
import miuix.theme.token.DimToken;

/* JADX INFO: loaded from: classes3.dex */
public class MiuixNavigationLayout extends ViewGroup {
    private static final TimeInterpolator INTERPOLATOR = new EaseManager.SpringInterpolator().setDamping(0.95f).setResponse(0.4f);
    private static final String TAG = "MiuixNavigationLayout";
    static final String TAG_C_MAX_WIDTH = "widthConfig.contentMaxWidth";
    static final String TAG_C_WIDTH_MODE = "widthConfig.contentWidthMode";
    static final String TAG_SC_MAX_WIDTH = "widthConfig.secondaryMaxWidth";
    static final String TAG_SECONDARY_ON_TOP = "secondaryOnTop";
    static final String TAG_USER_NAVIGATION_OPEN = "userNavigationOpen";
    private boolean hasAnimate;
    private final AnimConfig mAlphaHideAnimConfig;
    private final AnimConfig mAlphaShowAnimConfig;
    private final AnimHelper mAnimHelper;
    private FrameLayout mBackground;
    private View mBackgroundView;
    private int mBlurRadius;
    private BottomNavigation mBottomNavigation;
    private int mBottomNavigationHeight;
    private boolean mContentAnimFinished;
    private final String mContentCloseTag;
    private FrameLayout mContentDecor;
    private int mContentExpandedMaxWidthDp;
    private View mContentMask;
    private int mContentMaxWidth;
    private int mContentMaxWidthInListMode;
    private TypedValue mContentMaxWidthTypedValue;
    private int mContentMinWidthInRegular;
    private TypedValue mContentMinWidthInRegularTypedValue;
    private boolean mContentOpen;
    private final String mContentOpenTag;
    private FrameLayout mContentParent;
    private final List<SwitchInfo> mContentSwitch;
    private final AnimConfig mContentUpMoveAnimConfig;
    private int mContentViewAfterSwitchOffset;
    private int mContentWidth;
    private int mContentWidthMode;
    private int mDeviceType;
    private View mDivider;
    private boolean mEditingMode;
    private boolean mFirstToModeWithNavigation;
    private boolean mFlush;
    private boolean mIsDragging;
    private Navigator.Mode mMode;
    private final AnimConfig mMoveAnimConfig;
    private boolean mNavigationAnimFinished;
    private boolean mNavigationAnimRunning;
    private final String mNavigationCloseTag;
    private FrameLayout mNavigationDecor;
    private View mNavigationDivider;
    private View mNavigationEditMask;
    private boolean mNavigationInitOpen;
    private View mNavigationMask;
    private boolean mNavigationOpen;
    private final String mNavigationOpenTag;
    private View mNavigationSwitch;
    private int mNavigationWidth;
    private NavigatorFragmentListener mNavigatorFragmentListener;
    private Navigator.NavigatorStateListener mNavigatorStateListener;
    private int mNotifiedContentVisibility;
    private int mNotifiedNavigationVisibility;
    private int mNotifiedSecondaryContentVisibility;
    private final View.OnLayoutChangeListener mOnBottomNavigationHeightChange;
    private int mOriginalAfterId;
    private final String mOthersTag;
    private boolean mOverlay;
    private final DragOnTrigger mOverlayMaskDragOnTrigger;
    private final View.OnClickListener mOverlayMaskOnClick;
    private boolean mOverlaySwitchEnabled;
    private int mPadding;
    private boolean mPendingAddBottomNavigation;
    private FrameLayout mSecondaryContentDecor;
    private View mSecondaryContentMask;
    private int mSecondaryContentMaxWidth;
    private boolean mSecondaryContentReady;
    private final List<SwitchInfo> mSecondaryContentSwitch;
    private boolean mSecondaryOnTop;
    private boolean mSecondaryOnTopNow;
    private TypedValue mSecondayContentMaxWidthTypedValue;
    private boolean mShowBothContent;
    private int mSplitAnimationStyle;
    private final IStateStyle mStateStyle;
    private Boolean mUserNavigationOpenNoOverlay;
    private int mViewHeight;
    private int mViewWidth;
    private WidthConfig mWidthConfig;
    private int mWindowHeight;
    private int mWindowWidth;
    private int mWindowWidthDp;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ContentWidthMode {
        public static final int FIT = 1;
        public static final int FIXED = 0;
    }

    public static class WidthConfig {
        int contentMaxWidth;
        int contentWidthMode;
        int secondaryContentMaxWidth;
    }

    private static int getFragmentVisibility(int i, boolean z) {
        return i | (z ? 4 : 0);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchConfigurationChanged(Configuration configuration) {
    }

    /* JADX INFO: renamed from: lambda$new$0$miuix-navigator-MiuixNavigationLayout, reason: not valid java name */
    /* synthetic */ void m1886lambda$new$0$miuixnavigatorMiuixNavigationLayout(View view) {
        closeNavigation();
    }

    public MiuixNavigationLayout(Context context) {
        this(context, null);
    }

    public MiuixNavigationLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.navigatorLayoutStyle);
    }

    public MiuixNavigationLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMode = Navigator.Mode.C;
        this.mPendingAddBottomNavigation = false;
        this.mContentMinWidthInRegularTypedValue = null;
        this.mContentMaxWidthTypedValue = null;
        this.mSecondayContentMaxWidthTypedValue = null;
        this.mFirstToModeWithNavigation = true;
        this.mUserNavigationOpenNoOverlay = null;
        this.mBottomNavigationHeight = 0;
        this.mIsDragging = false;
        this.mOverlayMaskOnClick = new View.OnClickListener() { // from class: miuix.navigator.MiuixNavigationLayout$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1886lambda$new$0$miuixnavigatorMiuixNavigationLayout(view);
            }
        };
        this.mOverlayMaskDragOnTrigger = new DragOnTrigger(new Runnable() { // from class: miuix.navigator.MiuixNavigationLayout$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.closeNavigation();
            }
        });
        this.mOnBottomNavigationHeightChange = new View.OnLayoutChangeListener() { // from class: miuix.navigator.MiuixNavigationLayout.1
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
                int i10 = view.getVisibility() == 8 ? 0 : i5 - i3;
                if (MiuixNavigationLayout.this.mBottomNavigationHeight != i10) {
                    MiuixNavigationLayout.this.mBottomNavigationHeight = i10;
                    if (MiuixNavigationLayout.this.mNavigatorFragmentListener != null) {
                        MiuixNavigationLayout.this.mNavigatorFragmentListener.onBottomNavigationVisibilityChanged(MiuixNavigationLayout.this.mBottomNavigationHeight > 0, MiuixNavigationLayout.this.mBottomNavigationHeight);
                    }
                }
            }
        };
        this.mContentSwitch = new ArrayList();
        this.mSecondaryContentSwitch = new ArrayList();
        this.mOverlaySwitchEnabled = true;
        this.mNavigationOpenTag = "NAVIGATION_OPEN";
        this.mNavigationCloseTag = "NAVIGATION_CLOSE";
        this.mContentOpenTag = "CONTENT_OPEN";
        this.mContentCloseTag = "CONTENT_CLOSE";
        this.mOthersTag = "OTHERS";
        this.mFlush = true;
        this.mNavigationAnimRunning = false;
        this.mSplitAnimationStyle = 0;
        this.mBlurRadius = 50;
        Resources resources = getResources();
        this.mDeviceType = DeviceHelper.detectType(context);
        WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(context);
        this.mWindowWidthDp = windowInfo.windowSizeDp.x;
        this.mWindowWidth = windowInfo.windowSize.x;
        this.mWindowHeight = windowInfo.windowSize.y;
        this.mNavigationWidth = resources.getDimensionPixelSize(R.dimen.miuix_navigator_navigation_width);
        this.mContentMaxWidthInListMode = resources.getDimensionPixelSize(R.dimen.miuix_navigator_content_max_width_in_list_mode);
        this.mContentViewAfterSwitchOffset = getResources().getDimensionPixelSize(R.dimen.miuix_navigator_content_view_after_switch_offset);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.MiuixNavigationLayout, i, R.style.Miuix_Navigator_Layout);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.MiuixNavigationLayout_navigatorCrossBackground, 0);
        this.mContentMinWidthInRegularTypedValue = new TypedValue();
        typedArrayObtainStyledAttributes.getValue(R.styleable.MiuixNavigationLayout_navigatorContentMinWidthInRegular, this.mContentMinWidthInRegularTypedValue);
        if (this.mContentMinWidthInRegularTypedValue.resourceId != 0) {
            resources.getValue(this.mContentMinWidthInRegularTypedValue.resourceId, this.mContentMinWidthInRegularTypedValue, true);
        }
        this.mContentWidthMode = typedArrayObtainStyledAttributes.getInt(R.styleable.MiuixNavigationLayout_navigatorContentWidthMode, 0);
        this.mContentMaxWidthTypedValue = new TypedValue();
        typedArrayObtainStyledAttributes.getValue(R.styleable.MiuixNavigationLayout_navigatorContentWidth, this.mContentMaxWidthTypedValue);
        if (this.mContentMaxWidthTypedValue.resourceId != 0) {
            resources.getValue(this.mContentMaxWidthTypedValue.resourceId, this.mContentMaxWidthTypedValue, true);
        }
        this.mSecondayContentMaxWidthTypedValue = new TypedValue();
        typedArrayObtainStyledAttributes.getValue(R.styleable.MiuixNavigationLayout_navigatorSecondaryContentWidth, this.mSecondayContentMaxWidthTypedValue);
        if (this.mSecondayContentMaxWidthTypedValue.resourceId != 0) {
            resources.getValue(this.mSecondayContentMaxWidthTypedValue.resourceId, this.mSecondayContentMaxWidthTypedValue, true);
        }
        updateContentWidthConfig();
        this.mContentWidth = this.mContentMaxWidth;
        this.mNavigationInitOpen = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixNavigationLayout_navigatorNavigationOpen, true);
        typedArrayObtainStyledAttributes.recycle();
        if (resourceId != 0) {
            setCrossBackground(resourceId);
        }
        AnimHelper animHelper = new AnimHelper();
        this.mAnimHelper = animHelper;
        this.mStateStyle = Folme.useValue(animHelper);
        this.mMoveAnimConfig = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.95f, 0.4f)).addListeners(new TransitionListener() { // from class: miuix.navigator.MiuixNavigationLayout.2
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj) {
                ActionBarOverlayLayout actionBarOverlayLayout;
                ActionBarOverlayLayout actionBarOverlayLayout2;
                if (2 == MiuixNavigationLayout.this.mSplitAnimationStyle) {
                    ActionBarOverlayLayout actionBarOverlayLayout3 = (ActionBarOverlayLayout) MiuixNavigationLayout.this.mContentParent.findViewById(R.id.secondary_content_decor_wrapper).findViewById(R.id.secondary_content_decor).findViewById(R.id.action_bar_overlay_layout);
                    if (actionBarOverlayLayout3 != null) {
                        if ((MiuixNavigationLayout.this.isNavigationOpenCloseAnimation(obj) && !MiuixNavigationLayout.this.mOverlay) || MiuixNavigationLayout.this.isContentOpenCloseAnimation(obj)) {
                            actionBarOverlayLayout3.showContentMask(MiuixNavigationLayout.this.mBlurRadius);
                            FrameLayout frameLayout = (FrameLayout) actionBarOverlayLayout3.getContentView();
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
                            layoutParams.width = actionBarOverlayLayout3.getMeasuredWidth();
                            layoutParams.gravity = 17;
                            frameLayout.setLayoutParams(layoutParams);
                        }
                    } else if (MiuixNavigationLayout.this.mMode == Navigator.Mode.NC) {
                        ActionBarOverlayLayout actionBarOverlayLayout4 = (ActionBarOverlayLayout) MiuixNavigationLayout.this.mContentDecor.findViewById(R.id.content_decor).findViewById(R.id.action_bar_overlay_layout);
                        if (!MiuixNavigationLayout.this.mOverlay && actionBarOverlayLayout4 != null && ("NAVIGATION_CLOSE".equals(obj) || "NAVIGATION_OPEN".equals(obj))) {
                            actionBarOverlayLayout4.showContentMask(MiuixNavigationLayout.this.mBlurRadius);
                            FrameLayout frameLayout2 = (FrameLayout) actionBarOverlayLayout4.getContentView();
                            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) frameLayout2.getLayoutParams();
                            layoutParams2.width = actionBarOverlayLayout4.getMeasuredWidth();
                            frameLayout2.setLayoutParams(layoutParams2);
                        }
                    }
                }
                if (1 == MiuixNavigationLayout.this.mSplitAnimationStyle) {
                    ActionBarOverlayLayout actionBarOverlayLayout5 = (ActionBarOverlayLayout) MiuixNavigationLayout.this.mContentParent.findViewById(R.id.secondary_content_decor_wrapper).findViewById(R.id.secondary_content_decor).findViewById(R.id.action_bar_overlay_layout);
                    if (actionBarOverlayLayout5 != null) {
                        if (MiuixNavigationLayout.this.mMode == Navigator.Mode.NC && (actionBarOverlayLayout2 = (ActionBarOverlayLayout) MiuixNavigationLayout.this.mContentDecor.findViewById(R.id.content_decor).findViewById(R.id.action_bar_overlay_layout)) != null) {
                            ((FrameLayout.LayoutParams) ((FrameLayout) actionBarOverlayLayout2.getContentView()).getLayoutParams()).width = -1;
                        }
                        FrameLayout frameLayout3 = (FrameLayout) actionBarOverlayLayout5.getContentView();
                        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) frameLayout3.getLayoutParams();
                        if ("NAVIGATION_CLOSE".equals(obj) && !MiuixNavigationLayout.this.mOverlay) {
                            MiuixNavigationLayout.this.mFlush = true;
                            MiuixNavigationLayout.this.mNavigationAnimFinished = false;
                            MiuixNavigationLayout.this.hasAnimate = false;
                            layoutParams3.width = frameLayout3.getMeasuredWidth();
                        }
                        if ("CONTENT_CLOSE".equals(obj)) {
                            MiuixNavigationLayout.this.mFlush = true;
                            MiuixNavigationLayout.this.mContentAnimFinished = false;
                            MiuixNavigationLayout.this.hasAnimate = false;
                            layoutParams3.width = frameLayout3.getMeasuredWidth();
                        }
                        if ("NAVIGATION_OPEN".equals(obj) && !MiuixNavigationLayout.this.mOverlay) {
                            MiuixNavigationLayout.this.mFlush = false;
                            if (MiuixNavigationLayout.this.mNavigationAnimFinished) {
                                layoutParams3.width = frameLayout3.getMeasuredWidth() - MiuixNavigationLayout.this.mNavigationWidth;
                            } else {
                                layoutParams3.width = -1;
                            }
                            frameLayout3.setPadding(0, 0, 0, 0);
                        }
                        if ("CONTENT_OPEN".equals(obj)) {
                            MiuixNavigationLayout.this.mFlush = false;
                            if (MiuixNavigationLayout.this.mContentAnimFinished) {
                                if (MiuixNavigationLayout.this.isNavigationOpen()) {
                                    layoutParams3.width = (frameLayout3.getMeasuredWidth() - MiuixNavigationLayout.this.mContentWidth) - MiuixNavigationLayout.this.mNavigationWidth;
                                } else {
                                    layoutParams3.width = frameLayout3.getMeasuredWidth() - MiuixNavigationLayout.this.mContentWidth;
                                }
                                frameLayout3.setPadding(0, 0, 0, 0);
                            } else {
                                layoutParams3.width = -1;
                            }
                        }
                        layoutParams3.gravity = 17;
                        frameLayout3.setLayoutParams(layoutParams3);
                    } else if (MiuixNavigationLayout.this.mMode == Navigator.Mode.NC && (actionBarOverlayLayout = (ActionBarOverlayLayout) MiuixNavigationLayout.this.mContentDecor.findViewById(R.id.content_decor).findViewById(R.id.action_bar_overlay_layout)) != null) {
                        FrameLayout frameLayout4 = (FrameLayout) actionBarOverlayLayout.getContentView();
                        FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) frameLayout4.getLayoutParams();
                        if ("NAVIGATION_CLOSE".equals(obj)) {
                            MiuixNavigationLayout.this.mFlush = true;
                            MiuixNavigationLayout.this.mNavigationAnimFinished = false;
                            MiuixNavigationLayout.this.hasAnimate = false;
                            layoutParams4.width = frameLayout4.getMeasuredWidth();
                        } else if ("NAVIGATION_OPEN".equals(obj)) {
                            MiuixNavigationLayout.this.mFlush = false;
                            if (MiuixNavigationLayout.this.mNavigationAnimFinished) {
                                layoutParams4.width = frameLayout4.getMeasuredWidth() - MiuixNavigationLayout.this.mNavigationWidth;
                                frameLayout4.setPadding(0, 0, 0, 0);
                            } else {
                                layoutParams4.width = -1;
                            }
                        }
                        layoutParams4.gravity = 17;
                        frameLayout4.setLayoutParams(layoutParams4);
                    }
                }
                if ("NAVIGATION_OPEN".equals(obj) || "NAVIGATION_CLOSE".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigationAnimRunning = true;
                    MiuixNavigationLayout.this.postDelayed(new Runnable() { // from class: miuix.navigator.MiuixNavigationLayout.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (MiuixNavigationLayout.this.mNavigationAnimRunning) {
                                Log.w(MiuixNavigationLayout.TAG, "mNavigationAnimRunning is still true after workaround delay!!");
                                MiuixNavigationLayout.this.mNavigationAnimRunning = false;
                            }
                        }
                    }, 550L);
                }
                if (MiuixNavigationLayout.this.mNavigatorStateListener == null || "OTHERS".equals(obj)) {
                    return;
                }
                if ("NAVIGATION_OPEN".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigatorStateListener.onNavigationOpenBegin();
                    return;
                }
                if ("NAVIGATION_CLOSE".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigatorStateListener.onNavigationCloseBegin();
                } else if ("CONTENT_OPEN".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigatorStateListener.onContentOpenBegin();
                } else if ("CONTENT_CLOSE".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigatorStateListener.onContentCloseBegin();
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                UpdateInfo next = collection.iterator().next();
                if (1 == MiuixNavigationLayout.this.mSplitAnimationStyle && !MiuixNavigationLayout.this.hasAnimate) {
                    if (MiuixNavigationLayout.this.mMode == Navigator.Mode.NLC) {
                        if (MiuixNavigationLayout.this.mContentOpen) {
                            if ("NAVIGATION_CLOSE".equals(obj) && next.getFloatValue() < 0.2f) {
                                MiuixNavigationLayout.this.sectionAnimateDetailContent();
                                MiuixNavigationLayout.this.hasAnimate = true;
                            }
                        } else if ("CONTENT_CLOSE".equals(obj) && next.getFloatValue() < 0.2f) {
                            MiuixNavigationLayout.this.sectionAnimateDetailContent();
                            MiuixNavigationLayout.this.hasAnimate = true;
                        }
                    } else if (MiuixNavigationLayout.this.mMode != Navigator.Mode.NC) {
                        if (MiuixNavigationLayout.this.mMode == Navigator.Mode.LC && "CONTENT_CLOSE".equals(obj) && next.getFloatValue() < 0.2f) {
                            MiuixNavigationLayout.this.sectionAnimateDetailContent();
                            MiuixNavigationLayout.this.hasAnimate = true;
                        }
                    } else if ("NAVIGATION_CLOSE".equals(obj) && next.getFloatValue() < 0.2f) {
                        MiuixNavigationLayout.this.sectionAnimateDetailContent();
                        MiuixNavigationLayout.this.hasAnimate = true;
                    }
                }
                if (MiuixNavigationLayout.this.mNavigatorStateListener == null || "OTHERS".equals(obj)) {
                    return;
                }
                if ("NAVIGATION_OPEN".equals(obj) || "NAVIGATION_CLOSE".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigatorStateListener.onNavigationRatioChanged(next.getFloatValue());
                } else if ("CONTENT_OPEN".equals(obj) || "CONTENT_CLOSE".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigatorStateListener.onContentRatioChanged(next.getFloatValue());
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                ActionBarOverlayLayout actionBarOverlayLayout;
                if (2 == MiuixNavigationLayout.this.mSplitAnimationStyle && ("OTHERS".equals(obj) || (MiuixNavigationLayout.this.mMode == Navigator.Mode.LC && MiuixNavigationLayout.this.isContentOpenCloseAnimation(obj)))) {
                    ActionBarOverlayLayout actionBarOverlayLayout2 = (ActionBarOverlayLayout) MiuixNavigationLayout.this.mContentParent.findViewById(R.id.secondary_content_decor_wrapper).findViewById(R.id.secondary_content_decor).findViewById(R.id.action_bar_overlay_layout);
                    if (actionBarOverlayLayout2 == null || MiuixNavigationLayout.this.mOverlay) {
                        if (MiuixNavigationLayout.this.mMode == Navigator.Mode.NC && (actionBarOverlayLayout = (ActionBarOverlayLayout) MiuixNavigationLayout.this.mContentDecor.findViewById(R.id.content_decor).findViewById(R.id.action_bar_overlay_layout)) != null && !MiuixNavigationLayout.this.mOverlay) {
                            actionBarOverlayLayout.hideContentMask();
                            FrameLayout frameLayout = (FrameLayout) actionBarOverlayLayout.getContentView();
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
                            layoutParams.width = -1;
                            frameLayout.setLayoutParams(layoutParams);
                        }
                    } else {
                        actionBarOverlayLayout2.hideContentMask();
                        FrameLayout frameLayout2 = (FrameLayout) actionBarOverlayLayout2.getContentView();
                        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) frameLayout2.getLayoutParams();
                        layoutParams2.width = -1;
                        frameLayout2.setLayoutParams(layoutParams2);
                    }
                }
                if (1 == MiuixNavigationLayout.this.mSplitAnimationStyle) {
                    if ("NAVIGATION_CLOSE".equals(obj)) {
                        MiuixNavigationLayout.this.mNavigationAnimFinished = true;
                    }
                    if ("CONTENT_CLOSE".equals(obj)) {
                        MiuixNavigationLayout.this.mContentAnimFinished = true;
                    }
                }
                if ("NAVIGATION_OPEN".equals(obj) || "NAVIGATION_CLOSE".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigationAnimRunning = false;
                }
                if (MiuixNavigationLayout.this.mNavigatorStateListener == null || "OTHERS".equals(obj)) {
                    return;
                }
                if ("NAVIGATION_OPEN".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigatorStateListener.onNavigationOpenFinish();
                    return;
                }
                if ("NAVIGATION_CLOSE".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigatorStateListener.onNavigationCloseFinish();
                } else if ("CONTENT_OPEN".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigatorStateListener.onContentOpenFinish();
                } else if ("CONTENT_CLOSE".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigatorStateListener.onContentCloseFinish();
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onCancel(Object obj) {
                if ("NAVIGATION_OPEN".equals(obj) || "NAVIGATION_CLOSE".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigationAnimRunning = false;
                }
                if (MiuixNavigationLayout.this.mNavigatorStateListener == null || "OTHERS".equals(obj)) {
                    return;
                }
                if ("NAVIGATION_OPEN".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigatorStateListener.onNavigationOpenCancel();
                    return;
                }
                if ("NAVIGATION_CLOSE".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigatorStateListener.onNavigationCloseCancel();
                } else if ("CONTENT_OPEN".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigatorStateListener.onContentOpenCancel();
                } else if ("CONTENT_CLOSE".equals(obj)) {
                    MiuixNavigationLayout.this.mNavigatorStateListener.onContentCloseCancel();
                }
            }
        });
        this.mContentUpMoveAnimConfig = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.95f, 0.15f));
        this.mAlphaShowAnimConfig = new AnimConfig().setEase(EaseManager.getStyle(10, 350.0f));
        this.mAlphaHideAnimConfig = new AnimConfig().setEase(EaseManager.getStyle(4, 60.0f));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isNavigationOpenCloseAnimation(Object obj) {
        return "NAVIGATION_CLOSE".equals(obj) || "NAVIGATION_OPEN".equals(obj);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isContentOpenCloseAnimation(Object obj) {
        return "CONTENT_CLOSE".equals(obj) || "CONTENT_OPEN".equals(obj);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        FrameLayout frameLayout = (FrameLayout) findViewById(android.R.id.background);
        this.mBackground = frameLayout;
        View view = this.mBackgroundView;
        if (view != null) {
            frameLayout.addView(view);
        }
        this.mNavigationDecor = (FrameLayout) findViewById(R.id.navigation_decor_wrapper);
        this.mContentParent = (FrameLayout) findViewById(R.id.content_parent);
        this.mContentDecor = (FrameLayout) findViewById(R.id.content_decor_wrapper);
        this.mNavigationDivider = findViewById(R.id.navigation_divider);
        this.mDivider = findViewById(R.id.divider);
        this.mSecondaryContentDecor = (FrameLayout) findViewById(R.id.secondary_content_decor_wrapper);
        onNavigatorChanged(true);
        updateContentWidth(this.mNavigationWidth * this.mAnimHelper.getNavigationRatio());
    }

    void onSaveState(Bundle bundle) {
        bundle.putBoolean(TAG_SECONDARY_ON_TOP, this.mSecondaryOnTop);
        Boolean bool = this.mUserNavigationOpenNoOverlay;
        if (bool != null) {
            bundle.putBoolean(TAG_USER_NAVIGATION_OPEN, bool.booleanValue());
        }
        WidthConfig widthConfig = this.mWidthConfig;
        if (widthConfig != null) {
            bundle.putInt(TAG_C_WIDTH_MODE, widthConfig.contentWidthMode);
            bundle.putInt(TAG_C_MAX_WIDTH, this.mWidthConfig.contentMaxWidth);
            bundle.putInt(TAG_SC_MAX_WIDTH, this.mWidthConfig.secondaryContentMaxWidth);
        }
    }

    void onRestoreState(Bundle bundle) {
        this.mSecondaryOnTop = bundle.getBoolean(TAG_SECONDARY_ON_TOP);
        if (bundle.containsKey(TAG_USER_NAVIGATION_OPEN)) {
            this.mUserNavigationOpenNoOverlay = Boolean.valueOf(bundle.getBoolean(TAG_USER_NAVIGATION_OPEN));
        }
        if (bundle.containsKey(TAG_C_WIDTH_MODE)) {
            WidthConfig widthConfig = new WidthConfig();
            this.mWidthConfig = widthConfig;
            widthConfig.contentWidthMode = bundle.getInt(TAG_C_WIDTH_MODE);
            this.mWidthConfig.contentMaxWidth = bundle.getInt(TAG_C_MAX_WIDTH);
            this.mWidthConfig.secondaryContentMaxWidth = bundle.getInt(TAG_SC_MAX_WIDTH);
            updateContentWidthConfig();
        }
        onNavigatorChanged(false);
    }

    void setNavigationInitOpen(boolean z) {
        this.mNavigationInitOpen = z;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.mNavigationAnimRunning) {
            return true;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    void dispatchConfigurationChanged() {
        super.dispatchConfigurationChanged(getResources().getConfiguration());
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mFirstToModeWithNavigation = true;
        Resources resources = getResources();
        this.mDeviceType = DeviceHelper.detectType(getContext());
        WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(getContext(), resources.getConfiguration());
        this.mWindowWidthDp = windowInfo.windowSizeDp.x;
        this.mWindowWidth = windowInfo.windowSize.x;
        this.mWindowHeight = windowInfo.windowSize.y;
        this.mContentViewAfterSwitchOffset = resources.getDimensionPixelSize(R.dimen.miuix_navigator_content_view_after_switch_offset);
        this.mNavigationWidth = resources.getDimensionPixelSize(R.dimen.miuix_navigator_navigation_width);
        this.mContentMaxWidthInListMode = resources.getDimensionPixelSize(R.dimen.miuix_navigator_content_max_width_in_list_mode);
        updateContentWidthConfig();
        onNavigatorChanged(false);
        ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) this.mContentParent.findViewById(R.id.secondary_content_decor_wrapper).findViewById(R.id.secondary_content_decor).findViewById(R.id.action_bar_overlay_layout);
        if (actionBarOverlayLayout != null) {
            ((FrameLayout.LayoutParams) ((FrameLayout) actionBarOverlayLayout.getContentView()).getLayoutParams()).width = -1;
        }
        ActionBarOverlayLayout actionBarOverlayLayout2 = (ActionBarOverlayLayout) this.mContentDecor.findViewById(R.id.content_decor).findViewById(R.id.action_bar_overlay_layout);
        if (actionBarOverlayLayout2 != null) {
            ((FrameLayout.LayoutParams) ((FrameLayout) actionBarOverlayLayout2.getContentView()).getLayoutParams()).width = -1;
        }
        int i = this.mContentExpandedMaxWidthDp;
        if (i > 0) {
            setContentExpandedMaxWidthWithDp(i);
        }
        this.mAnimHelper.invalidate();
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        setMeasuredDimension(size, size2);
        measureChild(this.mBackground, i, i2);
        measureChild(this.mNavigationDivider, i, i2);
        measureChild(this.mNavigationDecor, View.MeasureSpec.makeMeasureSpec(this.mNavigationWidth, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(size2, BasicMeasure.EXACTLY));
        measureChild(this.mContentParent, View.MeasureSpec.makeMeasureSpec(size - (this.mOverlay ? 0 : (int) Math.abs(this.mContentParent.getTranslationX())), BasicMeasure.EXACTLY), i2);
        View view = this.mNavigationEditMask;
        if (view != null) {
            measureChild(view, size, size2);
        }
        BottomNavigation bottomNavigation = this.mBottomNavigation;
        if (bottomNavigation == null || bottomNavigation.getView().getParent() == null) {
            return;
        }
        measureChild(this.mBottomNavigation.getView(), View.MeasureSpec.makeMeasureSpec(this.mContentDecor.getMeasuredWidth(), BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(size2, Integer.MIN_VALUE));
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int i) {
        AnimHelper animHelper = this.mAnimHelper;
        if (animHelper != null) {
            animHelper.invalidate();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.mBackground.layout(i, i2, i3, i4);
        boolean z2 = getLayoutDirection() == 1;
        int measuredWidth = this.mNavigationDivider.getVisibility() == 8 ? 0 : this.mNavigationDivider.getMeasuredWidth();
        if (z2) {
            this.mNavigationDivider.layout(i3, i2, measuredWidth + i3, i4);
            FrameLayout frameLayout = this.mNavigationDecor;
            frameLayout.layout(i3, i2, this.mNavigationWidth + i3, frameLayout.getMeasuredHeight() + i2);
        } else {
            this.mNavigationDivider.layout(i - measuredWidth, i2, i, i4);
            FrameLayout frameLayout2 = this.mNavigationDecor;
            frameLayout2.layout(i - this.mNavigationWidth, i2, i, frameLayout2.getMeasuredHeight() + i2);
        }
        this.mContentParent.layout(i, i2, i3, i4);
        View view = this.mNavigationEditMask;
        if (view != null) {
            view.layout(i, i2, i3, i4);
        }
        BottomNavigation bottomNavigation = this.mBottomNavigation;
        if (bottomNavigation != null && bottomNavigation.getView().getParent() != null && this.mBottomNavigationHeight > 0 && this.mBottomNavigation.getView().getVisibility() == 8) {
            this.mBottomNavigationHeight = 0;
            NavigatorFragmentListener navigatorFragmentListener = this.mNavigatorFragmentListener;
            if (navigatorFragmentListener != null) {
                navigatorFragmentListener.onBottomNavigationVisibilityChanged(false, 0);
            }
        }
        int i5 = i3 - i;
        if (this.mViewWidth != i5) {
            this.mViewWidth = i5;
            onBottomNavigationChanged(false);
        }
        int i6 = i4 - i2;
        if (this.mViewHeight != i6) {
            this.mViewHeight = i6;
            this.mAnimHelper.mNavigationHadLayout = false;
        }
    }

    public void onContentShow() {
        if (this.mPendingAddBottomNavigation) {
            this.mPendingAddBottomNavigation = false;
            if (isBottomNavigationEnable()) {
                addBottomNavigation();
            }
        }
    }

    public void setNavigatorStateListener(Navigator.NavigatorStateListener navigatorStateListener) {
        this.mNavigatorStateListener = navigatorStateListener;
    }

    private boolean isBottomNavigationEnable() {
        return !canAccessNavigation();
    }

    public void setBottomNavigation(BottomNavigation bottomNavigation) {
        BottomNavigation bottomNavigation2 = this.mBottomNavigation;
        if (bottomNavigation2 != null) {
            removeView(bottomNavigation2.getView());
        }
        this.mBottomNavigation = bottomNavigation;
        if (isBottomNavigationEnable()) {
            addBottomNavigation();
        }
    }

    private void addBottomNavigation() {
        this.mBottomNavigationHeight = 0;
        BottomNavigation bottomNavigation = this.mBottomNavigation;
        if (bottomNavigation == null) {
            return;
        }
        ViewGroup viewGroup = (ViewGroup) bottomNavigation.getView().getParent();
        View viewFindViewById = this.mContentDecor.findViewById(android.R.id.content);
        if (viewFindViewById == null) {
            this.mPendingAddBottomNavigation = true;
            return;
        }
        ViewGroup viewGroup2 = (ViewGroup) viewFindViewById.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(this.mBottomNavigation.getView());
        }
        int iIndexOfChild = viewGroup2.indexOfChild(viewFindViewById) + 1;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
        layoutParams.gravity = 80;
        viewGroup2.addView(this.mBottomNavigation.getView(), iIndexOfChild, layoutParams);
        this.mBottomNavigation.getView().addOnLayoutChangeListener(this.mOnBottomNavigationHeightChange);
        NavigatorFragmentListener navigatorFragmentListener = this.mNavigatorFragmentListener;
        if (navigatorFragmentListener != null) {
            navigatorFragmentListener.onBottomNavigationPrepared();
        }
    }

    private void removeBottomNavigation() {
        BottomNavigation bottomNavigation = this.mBottomNavigation;
        if (bottomNavigation == null) {
            return;
        }
        ViewGroup viewGroup = (ViewGroup) bottomNavigation.getView().getParent();
        if (viewGroup != null) {
            viewGroup.removeView(this.mBottomNavigation.getView());
            NavigatorFragmentListener navigatorFragmentListener = this.mNavigatorFragmentListener;
            if (navigatorFragmentListener != null) {
                navigatorFragmentListener.onBottomNavigationVisibilityChanged(false, 0);
            }
            this.mBottomNavigationHeight = 0;
            NavigatorFragmentListener navigatorFragmentListener2 = this.mNavigatorFragmentListener;
            if (navigatorFragmentListener2 != null) {
                navigatorFragmentListener2.onBottomNavigationDestroyed();
            }
        }
        this.mBottomNavigation.getView().removeOnLayoutChangeListener(this.mOnBottomNavigationHeightChange);
    }

    public BottomNavigation getBottomNavigation() {
        return this.mBottomNavigation;
    }

    void initWithMode(Navigator.Mode mode) {
        if (mode == Navigator.Mode.NLC || mode == Navigator.Mode.NC) {
            boolean z = this.mNavigationInitOpen;
            this.mNavigationOpen = z;
            if (z) {
                this.mNavigationDecor.setVisibility(0);
            } else {
                this.mNavigationDecor.setVisibility(4);
            }
            updateDividers();
        }
        if (mode == Navigator.Mode.NLC || mode == Navigator.Mode.LC) {
            this.mContentOpen = true;
            this.mSecondaryContentDecor.setVisibility(0);
            ViewGroup.LayoutParams layoutParams = this.mContentDecor.getLayoutParams();
            updateDividers();
            layoutParams.width = this.mContentWidth;
        }
        notifyModeChanged(mode);
    }

    public void notifyModeChanged(Navigator.Mode mode) {
        if (this.mMode != mode) {
            this.mMode = mode;
            onBottomNavigationChanged(true);
            onNavigatorChanged(true);
            if (this.mFirstToModeWithNavigation) {
                if (this.mMode == Navigator.Mode.NC || this.mMode == Navigator.Mode.NLC) {
                    this.mFirstToModeWithNavigation = false;
                }
            }
        }
    }

    public void openNavigation() {
        openNavigation(true);
    }

    public void openNavigation(boolean z) {
        if (!canAccessNavigation() || this.mNavigationOpen) {
            return;
        }
        if (!this.mOverlay) {
            this.mUserNavigationOpenNoOverlay = true;
        }
        transformInternal(true, this.mContentOpen, z);
    }

    public void closeNavigation() {
        closeNavigation(true);
    }

    public void closeNavigation(boolean z) {
        if (canAccessNavigation() && this.mNavigationOpen) {
            if (!this.mOverlay) {
                this.mUserNavigationOpenNoOverlay = false;
            }
            transformInternal(false, this.mContentOpen, z);
        }
    }

    public void toggleNavigation() {
        toggleNavigation(true);
    }

    public void toggleNavigation(boolean z) {
        if (canAccessNavigation()) {
            if (!this.mOverlay) {
                this.mUserNavigationOpenNoOverlay = Boolean.valueOf(!this.mNavigationOpen);
            }
            transformInternal(!this.mNavigationOpen, this.mContentOpen, z);
        }
    }

    public void maskContent(boolean z) {
        boolean z2 = this.mContentDecor.getVisibility() == 0;
        if (z) {
            if (this.mContentMask == null) {
                View view = new View(getContext());
                this.mContentMask = view;
                view.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
                this.mContentMask.setAlpha(0.0f);
                this.mContentMask.setVisibility(4);
                this.mContentMask.setClickable(true);
                this.mContentMask.setImportantForAccessibility(2);
                this.mContentDecor.addView(this.mContentMask, new FrameLayout.LayoutParams(-1, -1));
            }
            doMask(this.mContentMask, true, z2);
            maybeClearInputFocus(this.mContentDecor);
            notifyContentMasked(true);
            return;
        }
        View view2 = this.mContentMask;
        if (view2 != null) {
            doMask(view2, false, z2);
            notifyContentMasked(false);
            this.mContentMask = null;
        }
    }

    public boolean isContentMasked() {
        return this.mContentMask != null;
    }

    public void maskSecondaryContent(boolean z) {
        boolean z2 = this.mSecondaryContentDecor.getVisibility() == 0;
        if (z) {
            if (this.mSecondaryContentMask == null) {
                View view = new View(getContext());
                this.mSecondaryContentMask = view;
                view.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
                this.mSecondaryContentMask.setAlpha(0.0f);
                this.mSecondaryContentMask.setVisibility(4);
                this.mSecondaryContentMask.setClickable(true);
                this.mSecondaryContentMask.setImportantForAccessibility(2);
                this.mSecondaryContentDecor.addView(this.mSecondaryContentMask, new FrameLayout.LayoutParams(-1, -1));
            }
            doMask(this.mSecondaryContentMask, true, z2);
            maybeClearInputFocus(this.mSecondaryContentDecor);
            notifySecondaryContentMasked(true);
            return;
        }
        View view2 = this.mSecondaryContentMask;
        if (view2 != null) {
            doMask(view2, false, z2);
            notifySecondaryContentMasked(false);
            this.mSecondaryContentMask = null;
        }
    }

    public void maskNavigation(boolean z) {
        boolean zIsNavigationOpen = isNavigationOpen();
        if (z) {
            if (this.mNavigationMask == null) {
                View view = new View(getContext());
                this.mNavigationMask = view;
                view.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
                this.mNavigationMask.setAlpha(0.0f);
                this.mNavigationMask.setVisibility(4);
                this.mNavigationMask.setClickable(true);
                this.mNavigationMask.setImportantForAccessibility(2);
                this.mNavigationDecor.addView(this.mNavigationMask, new FrameLayout.LayoutParams(-1, -1));
            }
            doMask(this.mNavigationMask, true, zIsNavigationOpen);
            notifyNavigationMasked(true);
        } else {
            View view2 = this.mNavigationMask;
            if (view2 != null) {
                doMask(view2, false, zIsNavigationOpen);
                notifyNavigationMasked(false);
                this.mNavigationMask = null;
            }
        }
        this.mAnimHelper.mNavigationHadLayout = false;
    }

    public void setOverlaySwitchEnabled(boolean z) {
        this.mOverlaySwitchEnabled = z;
        updateContentSwitch();
    }

    private void updateContentWidthConfig() {
        Resources resources = getResources();
        Point screenSize = EnvStateManager.getScreenSize(getContext());
        if (this.mContentMinWidthInRegularTypedValue.type != 0) {
            if (this.mContentMinWidthInRegularTypedValue.resourceId != 0) {
                this.mContentMinWidthInRegular = resources.getDimensionPixelSize(this.mContentMinWidthInRegularTypedValue.resourceId);
            } else {
                this.mContentMinWidthInRegular = (int) this.mContentMinWidthInRegularTypedValue.getDimension(resources.getDisplayMetrics());
            }
        }
        WidthConfig widthConfig = this.mWidthConfig;
        if (widthConfig != null) {
            this.mContentWidthMode = widthConfig.contentWidthMode;
        }
        int i = this.mContentWidthMode;
        if (i == 0) {
            WidthConfig widthConfig2 = this.mWidthConfig;
            if (widthConfig2 != null && widthConfig2.contentMaxWidth > 0) {
                this.mContentMaxWidth = this.mWidthConfig.contentMaxWidth;
                return;
            } else {
                this.mContentMaxWidth = resolveZoneWidthDimension(this.mContentMaxWidthTypedValue);
                return;
            }
        }
        if (i != 1) {
            return;
        }
        WidthConfig widthConfig3 = this.mWidthConfig;
        if (widthConfig3 != null) {
            this.mSecondaryContentMaxWidth = widthConfig3.secondaryContentMaxWidth;
        } else {
            this.mSecondaryContentMaxWidth = resolveZoneWidthDimension(this.mSecondayContentMaxWidthTypedValue);
        }
        if (this.mSecondaryContentMaxWidth == 0) {
            int iMin = Math.min(screenSize.x, screenSize.y);
            int iMax = Math.max(screenSize.x, screenSize.y);
            if (this.mWindowWidth <= ((double) screenSize.x) * 0.9d) {
                iMin = Math.min(this.mWindowWidth, this.mWindowHeight);
                iMax = Math.max(this.mWindowWidth, this.mWindowHeight);
            }
            if ((iMin * 1.0f) / iMax < 0.85d) {
                this.mSecondaryContentMaxWidth = iMin;
            } else {
                this.mSecondaryContentMaxWidth = (int) (((this.mWindowWidth * iMin) * 1.0f) / (iMin + iMax));
            }
        }
        this.mContentMaxWidth = Math.max(this.mContentMaxWidthInListMode, this.mWindowWidth - this.mSecondaryContentMaxWidth);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateContentWidth(float f) {
        boolean z = this.mOverlay || !canAccessNavigation();
        float f2 = this.mWindowWidth;
        if (z) {
            f = 0.0f;
        }
        float f3 = (f2 - f) * 0.45f;
        if (this.mMode == Navigator.Mode.NC) {
            this.mContentWidth = (int) f3;
        } else {
            this.mContentWidth = Math.min((int) f3, this.mContentMaxWidth);
        }
    }

    private int resolveZoneWidthDimension(TypedValue typedValue) {
        float fraction;
        if (typedValue.type == 5) {
            fraction = typedValue.getDimension(getResources().getDisplayMetrics());
        } else if (typedValue.type == 6) {
            int i = this.mWindowWidth;
            fraction = typedValue.getFraction(i, i);
        } else {
            if (typedValue.type != 4) {
                return 0;
            }
            return (int) (this.mWindowWidth * Math.max(0.0f, Math.min(typedValue.getFloat(), 1.0f)));
        }
        return (int) fraction;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forAllSwitch(Iterable<SwitchInfo> iterable, Consumer<SwitchInfo> consumer) {
        Iterator<SwitchInfo> it = iterable.iterator();
        while (it.hasNext()) {
            consumer.accept(it.next());
        }
    }

    private void updateContentSwitch() {
        forAllSwitch(this.mContentSwitch, new Consumer() { // from class: miuix.navigator.MiuixNavigationLayout$$ExternalSyntheticLambda0
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                this.f$0.m1891xa15a34bb((SwitchInfo) obj);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$updateContentSwitch$1$miuix-navigator-MiuixNavigationLayout, reason: not valid java name */
    /* synthetic */ void m1891xa15a34bb(SwitchInfo switchInfo) {
        switchInfo.view.setEnabled(!this.mOverlay || this.mOverlaySwitchEnabled);
    }

    public boolean isSecondaryContentMasked() {
        return this.mSecondaryContentMask != null;
    }

    public boolean isOverlay() {
        return this.mOverlay;
    }

    public boolean isNavigationOpen() {
        return this.mNavigationOpen;
    }

    public boolean isNavigationInitOpen() {
        Boolean bool = this.mUserNavigationOpenNoOverlay;
        if (bool == null) {
            return this.mNavigationInitOpen;
        }
        return bool.booleanValue();
    }

    public void openContent() {
        openContent(true);
    }

    public void openContent(boolean z) {
        if (!canAccessContent() || this.mContentOpen) {
            return;
        }
        transformInternal(this.mNavigationOpen, true, z);
    }

    public void closeContent() {
        closeContent(true);
    }

    public void closeContent(boolean z) {
        if (canAccessContent() && this.mContentOpen) {
            transformInternal(this.mNavigationOpen, false, z);
        }
    }

    public void toggleContent() {
        toggleContent(true);
    }

    public void toggleContent(boolean z) {
        if (canAccessContent()) {
            transformInternal(this.mNavigationOpen, !this.mContentOpen, z);
        }
    }

    public boolean isContentOpen() {
        return this.mContentOpen;
    }

    private boolean canAccessNavigation() {
        return this.mMode == Navigator.Mode.NLC || this.mMode == Navigator.Mode.NC;
    }

    private boolean canAccessContent() {
        return this.mMode == Navigator.Mode.NLC || this.mMode == Navigator.Mode.LC;
    }

    private void createAndAddNavigationEditMask() {
        if (this.mNavigationEditMask == null) {
            View view = new View(getContext());
            this.mNavigationEditMask = view;
            view.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
            this.mNavigationEditMask.setAlpha(0.0f);
            this.mNavigationEditMask.setImportantForAccessibility(2);
        }
        if (this.mNavigationEditMask.getParent() == null) {
            addView(this.mNavigationEditMask, indexOfChild(this.mNavigationDecor), new ViewGroup.LayoutParams(-1, -1));
        }
    }

    private void updateNavigationEditMaskListener() {
        this.mNavigationEditMask.setOnClickListener(this.mEditingMode ? null : this.mOverlayMaskOnClick);
        this.mNavigationEditMask.setOnDragListener(this.mEditingMode ? null : this.mOverlayMaskDragOnTrigger);
    }

    private void folmeTo(Object obj, float f, AnimConfig animConfig, boolean z) {
        if (z) {
            AnimState animState = new AnimState((Object) "OTHERS", true);
            animState.add(obj, f);
            this.mStateStyle.to(animState, animConfig);
            return;
        }
        this.mStateStyle.setTo(obj, Float.valueOf(f));
    }

    private void folmeTo(Object obj, Object obj2, float f, AnimConfig animConfig, boolean z) {
        if (z) {
            this.mStateStyle.to(obj, obj2, Float.valueOf(f), animConfig);
        } else {
            this.mStateStyle.setTo(obj2, Float.valueOf(f));
        }
    }

    private void transformInternal(boolean z, boolean z2, boolean z3) {
        transformNavigation(z, z3);
        transformContent(z2, z3);
        transformSwitch(z, z2, z3);
        setupBottomNavigation(z2);
        if (this.mNavigationOpen != z) {
            this.mNavigationOpen = z;
            announceExpansionAccessibilityEvent(z);
        }
        if (this.mContentOpen != z2) {
            this.mContentOpen = z2;
            announceExpansionAccessibilityEvent(!z2);
        }
    }

    private void announceExpansionAccessibilityEvent(boolean z) {
        String string;
        if (z) {
            string = getResources().getString(R.string.miuix_appcompat_accessibility_expand_state);
        } else {
            string = getResources().getString(R.string.miuix_appcompat_accessibility_collapse_state);
        }
        announceForAccessibility(string);
    }

    private void transformNavigation(boolean z, boolean z2) {
        View view;
        folmeTo(z ? "NAVIGATION_OPEN" : "NAVIGATION_CLOSE", "navigationRatio", z ? 1.0f : 0.0f, this.mMoveAnimConfig, z2);
        if (this.mOverlay && z) {
            createAndAddNavigationEditMask();
            updateNavigationEditMaskListener();
            doMask(this.mNavigationEditMask, true, z2);
            maybeClearInputFocus(this.mContentParent);
            notifyContentMasked(true);
            notifySecondaryContentMasked(true);
            return;
        }
        if (this.mEditingMode || (view = this.mNavigationEditMask) == null) {
            return;
        }
        doMask(view, false, z2);
        notifyContentMasked(false);
        notifySecondaryContentMasked(false);
    }

    private void maybeClearInputFocus(View view) {
        view.clearFocus();
        ((InputMethodManager) getContext().getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void transformContent(boolean z, boolean z2) {
        folmeTo(z ? "CONTENT_OPEN" : "CONTENT_CLOSE", "contentRatio", z ? 1.0f : 0.0f, this.mMoveAnimConfig, z2);
    }

    private void transformSwitch(boolean z, boolean z2, boolean z3) {
        AnimConfig animConfig;
        float f;
        AnimConfig animConfig2;
        float f2;
        AnimConfig animConfig3;
        float f3 = 0.0f;
        if (canAccessNavigation() && z && z2) {
            animConfig = this.mAlphaShowAnimConfig;
            f = 1.0f;
        } else {
            animConfig = this.mAlphaHideAnimConfig;
            f = 0.0f;
        }
        folmeTo("navigationSwitchRatio", f, this.mMoveAnimConfig, z3);
        folmeTo("navigationSwitchAlpha", f, animConfig, z3);
        if (canAccessNavigation() && !z && z2) {
            animConfig2 = this.mAlphaShowAnimConfig;
            f2 = 1.0f;
        } else {
            animConfig2 = this.mAlphaHideAnimConfig;
            f2 = 0.0f;
        }
        folmeTo("contentSwitchRatio", f2, this.mMoveAnimConfig, z3);
        folmeTo("contentSwitchAlpha", f2, animConfig2, z3);
        if (this.mOverlay || (canAccessNavigation() && z && z2)) {
            float f4 = this.mOverlay ? 0.0f : 1.0f;
            animConfig3 = this.mContentUpMoveAnimConfig;
            f3 = f4;
        } else {
            animConfig3 = this.mMoveAnimConfig;
        }
        folmeTo("contentViewAfterSwitchRatio", f3, animConfig3, z3);
        setSwitchOffset(z, z2);
    }

    private void setSwitchOffset(boolean z, boolean z2) {
        this.mAnimHelper.mNavigationOffset = 0;
        this.mAnimHelper.mContentOffset = 0;
        if (this.mOverlay) {
            return;
        }
        if (!this.mContentOpen && z2) {
            if (z) {
                this.mAnimHelper.mNavigationOffset = (-(this.mNavigationWidth + this.mContentWidth)) / 3;
                return;
            } else {
                this.mAnimHelper.mContentOffset = (-this.mContentWidth) / 3;
                return;
            }
        }
        boolean z3 = this.mNavigationOpen;
        if (!z3 && z) {
            this.mAnimHelper.mNavigationOffset = (-this.mNavigationWidth) / 3;
        } else {
            if (!z3 || z) {
                return;
            }
            this.mAnimHelper.mContentOffset = this.mContentViewAfterSwitchOffset;
        }
    }

    private void setupBottomNavigation(boolean z) {
        if (!z || this.mContentOpen || this.mBottomNavigation == null || !isBottomNavigationEnable() || this.mBottomNavigation.getView() == null) {
            return;
        }
        ViewGroup viewGroup = (ViewGroup) this.mBottomNavigation.getView().getParent();
        if (viewGroup != null) {
            viewGroup.removeView(this.mBottomNavigation.getView());
        }
        addBottomNavigation();
    }

    private void doMask(final View view, final boolean z, boolean z2) {
        if (z) {
            view.setVisibility(0);
        }
        float f = AttributeResolver.resolveBoolean(getContext(), android.R.attr.isLightTheme, true) ? DimToken.DIM_LIGHT : DimToken.DIM_DARK;
        if (!z2) {
            if (!z) {
                f = 0.0f;
            }
            view.setAlpha(f);
            onAnimateMaskEnd(view, z);
            return;
        }
        ViewPropertyAnimator viewPropertyAnimatorAnimate = view.animate();
        if (!z) {
            f = 0.0f;
        }
        viewPropertyAnimatorAnimate.alpha(f).setDuration(z ? 300L : 250L).setInterpolator(new DecelerateInterpolator(1.5f)).setListener(new Animator.AnimatorListener() { // from class: miuix.navigator.MiuixNavigationLayout.3
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                MiuixNavigationLayout.this.onAnimateMaskEnd(view, z);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAnimateMaskEnd(View view, boolean z) {
        if (z || view.getParent() == null) {
            return;
        }
        ((ViewGroup) view.getParent()).removeView(view);
    }

    /* JADX WARN: Code duplicated, block: B:43:0x0087  */
    /* JADX WARN: Code duplicated, block: B:57:0x00a4  */
    private void onNavigatorChanged(boolean z) {
        boolean zCanAccessNavigation;
        boolean zIsNavigationInitOpen;
        this.mShowBothContent = false;
        boolean z2 = this.mOverlay;
        this.mOverlay = false;
        int i = this.mWindowWidthDp;
        if (i <= 640) {
            this.mOverlay = true;
            zCanAccessNavigation = false;
        } else if (i >= 960) {
            zCanAccessNavigation = canAccessNavigation();
            this.mShowBothContent = canAccessContent();
        } else {
            if (!canAccessNavigation()) {
                zCanAccessNavigation = false;
            } else if (this.mMode == Navigator.Mode.NLC) {
                zCanAccessNavigation = this.mDeviceType != 3;
                this.mOverlay = true;
            } else {
                if ((this.mWindowWidth - this.mNavigationWidth) * 0.45f < this.mContentMinWidthInRegular) {
                    this.mOverlay = true;
                }
                zCanAccessNavigation = true;
            }
            this.mShowBothContent = canAccessContent();
        }
        boolean z3 = this.mShowBothContent;
        boolean z4 = !z3 && this.mSecondaryOnTop;
        boolean z5 = z || this.mContentOpen || !z3;
        if (!zCanAccessNavigation) {
            this.mEditingMode = false;
            transformInternal(false, z5, false);
            this.mAnimHelper.postInvalidate();
        } else if (z || z2 != this.mOverlay) {
            this.mAnimHelper.mNavigationHadLayout = false;
            if (this.mEditingMode) {
                zIsNavigationInitOpen = true;
            } else if (this.mFirstToModeWithNavigation) {
                zIsNavigationInitOpen = !this.mOverlay ? isNavigationInitOpen() : false;
                boolean z6 = this.mOverlay;
                if (z2 != z6 && !z6) {
                    if (this.mNavigationOpen || zIsNavigationInitOpen) {
                        zIsNavigationInitOpen = true;
                    } else {
                        zIsNavigationInitOpen = false;
                    }
                }
            } else {
                zIsNavigationInitOpen = false;
            }
            transformInternal(zIsNavigationInitOpen, z5, false);
            this.mAnimHelper.postInvalidate();
        }
        if (this.mSecondaryOnTopNow != z4) {
            this.mSecondaryOnTopNow = z4;
            setContentVisible(this.mAnimHelper.mLastRealContentRatio > 0.0f, this.mAnimHelper.mLastRealContentRatio == 1.0f);
        }
        setSecondaryContentVisible();
        updateDividers();
        updateContentSwitch();
        this.mAnimHelper.invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyNavigationVisible(boolean z, boolean z2) {
        int fragmentVisibility = getFragmentVisibility(z, z2, (this.mNotifiedNavigationVisibility & 4) != 0);
        if (this.mNotifiedNavigationVisibility != fragmentVisibility) {
            this.mNotifiedNavigationVisibility = fragmentVisibility;
            onNavigationVisibilityChanged(fragmentVisibility);
        }
    }

    private void notifyNavigationMasked(boolean z) {
        this.mNavigationDecor.setImportantForAccessibility(z ? 4 : 0);
        int fragmentVisibility = getFragmentVisibility(this.mNotifiedNavigationVisibility & 3, z);
        if (this.mNotifiedNavigationVisibility != fragmentVisibility) {
            this.mNotifiedNavigationVisibility = fragmentVisibility;
            onNavigationVisibilityChanged(fragmentVisibility);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setContentVisible(boolean z, boolean z2) {
        if (z && !this.mSecondaryOnTopNow) {
            if (this.mContentDecor.getVisibility() != 0) {
                this.mContentDecor.setVisibility(0);
            }
            notifyContentVisible(true, z2);
        } else if (z && !this.mSecondaryContentReady) {
            if (this.mContentDecor.getVisibility() != 0) {
                this.mContentDecor.setVisibility(0);
            }
            notifyContentVisible(true, false);
        } else {
            if (this.mContentDecor.getVisibility() != 8) {
                this.mContentDecor.setVisibility(8);
            }
            notifyContentVisible(false, false);
        }
    }

    private void notifyContentVisible(boolean z, boolean z2) {
        int fragmentVisibility = getFragmentVisibility(z, z2, (this.mNotifiedContentVisibility & 4) != 0);
        if (this.mNotifiedContentVisibility != fragmentVisibility) {
            this.mNotifiedContentVisibility = fragmentVisibility;
            onContentVisibilityChanged(fragmentVisibility);
        }
    }

    private void notifyContentMasked(boolean z) {
        this.mContentDecor.setImportantForAccessibility(z ? 4 : 0);
        int fragmentVisibility = getFragmentVisibility(this.mNotifiedContentVisibility & 3, z);
        if (this.mNotifiedContentVisibility != fragmentVisibility) {
            this.mNotifiedContentVisibility = fragmentVisibility;
            onContentVisibilityChanged(fragmentVisibility);
        }
    }

    private void setSecondaryContentVisible() {
        if (this.mShowBothContent || this.mSecondaryOnTopNow) {
            if (this.mSecondaryContentDecor.getVisibility() != 0) {
                this.mSecondaryContentDecor.setVisibility(0);
            }
            notifySecondaryContentVisible(true, true);
        } else {
            if (this.mSecondaryContentDecor.getVisibility() != 8) {
                this.mSecondaryContentDecor.setVisibility(8);
            }
            notifySecondaryContentVisible(false, false);
        }
    }

    private void notifySecondaryContentVisible(boolean z, boolean z2) {
        int fragmentVisibility = getFragmentVisibility(z, z2, (this.mNotifiedSecondaryContentVisibility & 4) != 0);
        if (this.mNotifiedSecondaryContentVisibility != fragmentVisibility) {
            this.mNotifiedSecondaryContentVisibility = fragmentVisibility;
            onSecondaryContentVisibilityChanged(fragmentVisibility);
        }
    }

    private void notifySecondaryContentMasked(boolean z) {
        this.mSecondaryContentDecor.setImportantForAccessibility(z ? 4 : 0);
        int fragmentVisibility = getFragmentVisibility(this.mNotifiedSecondaryContentVisibility & 3, z);
        if (this.mNotifiedSecondaryContentVisibility != fragmentVisibility) {
            this.mNotifiedSecondaryContentVisibility = fragmentVisibility;
            onSecondaryContentVisibilityChanged(fragmentVisibility);
        }
    }

    private void onBottomNavigationChanged(boolean z) {
        BottomNavigation bottomNavigation;
        if (this.mBottomNavigation == null) {
            return;
        }
        if (!isBottomNavigationEnable() && this.mBottomNavigation.getView().getParent() != null) {
            removeBottomNavigation();
            return;
        }
        if (!isBottomNavigationEnable() || (bottomNavigation = this.mBottomNavigation) == null) {
            return;
        }
        if (bottomNavigation.getView().getParent() == null) {
            addBottomNavigation();
        }
        int iPx2dp = MiuixUIUtils.px2dp(getContext().getResources().getDisplayMetrics().density, getMeasuredWidth());
        int i = AnonymousClass6.$SwitchMap$miuix$navigator$Navigator$Mode[this.mMode.ordinal()];
        if (i != 1) {
            if (i != 2) {
                return;
            }
            this.mBottomNavigation.setLayoutStyle(0);
        } else if (iPx2dp >= 960) {
            this.mBottomNavigation.setLayoutStyle(3);
        } else if (iPx2dp > 640) {
            this.mBottomNavigation.setLayoutStyle(1);
        } else {
            this.mBottomNavigation.setLayoutStyle(0);
        }
    }

    /* JADX INFO: renamed from: miuix.navigator.MiuixNavigationLayout$6, reason: invalid class name */
    static /* synthetic */ class AnonymousClass6 {
        static final /* synthetic */ int[] $SwitchMap$miuix$navigator$Navigator$Mode;

        static {
            int[] iArr = new int[Navigator.Mode.values().length];
            $SwitchMap$miuix$navigator$Navigator$Mode = iArr;
            try {
                iArr[Navigator.Mode.C.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$miuix$navigator$Navigator$Mode[Navigator.Mode.LC.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDividers() {
        this.mNavigationDivider.setVisibility(0);
        if (this.mContentDecor.getVisibility() == 0 && this.mSecondaryContentDecor.getVisibility() == 0 && (!this.mSecondaryOnTopNow || this.mShowBothContent)) {
            this.mDivider.setVisibility(0);
        } else {
            this.mDivider.setVisibility(8);
        }
    }

    public void setEditingMode(boolean z) {
        this.mEditingMode = z;
        if (!this.mOverlay) {
            createAndAddNavigationEditMask();
            updateNavigationEditMaskListener();
            doMask(this.mNavigationEditMask, this.mEditingMode, true);
            maybeClearInputFocus(this.mContentParent);
            notifyContentMasked(this.mEditingMode);
            notifySecondaryContentMasked(this.mEditingMode);
            return;
        }
        updateNavigationEditMaskListener();
    }

    private static int getFragmentVisibility(boolean z, boolean z2, boolean z3) {
        int i;
        if (z2) {
            i = 2;
        } else {
            i = z ? 1 : 0;
        }
        return getFragmentVisibility(i, z3);
    }

    private void onNavigationVisibilityChanged(int i) {
        NavigatorFragmentListener navigatorFragmentListener = this.mNavigatorFragmentListener;
        if (navigatorFragmentListener != null) {
            navigatorFragmentListener.onNavigationVisibilityChanged(i);
        }
    }

    private void onContentVisibilityChanged(int i) {
        NavigatorFragmentListener navigatorFragmentListener = this.mNavigatorFragmentListener;
        if (navigatorFragmentListener != null) {
            navigatorFragmentListener.onContentVisibilityChanged(i);
        }
    }

    private void onSecondaryContentVisibilityChanged(int i) {
        NavigatorFragmentListener navigatorFragmentListener = this.mNavigatorFragmentListener;
        if (navigatorFragmentListener != null) {
            navigatorFragmentListener.onSecondaryContentVisibilityChanged(i);
        }
    }

    public void setNavigatorFragmentListener(NavigatorFragmentListener navigatorFragmentListener) {
        this.mNavigatorFragmentListener = navigatorFragmentListener;
        onNavigationVisibilityChanged(this.mNotifiedNavigationVisibility);
        onContentVisibilityChanged(this.mNotifiedContentVisibility);
        onSecondaryContentVisibilityChanged(this.mNotifiedSecondaryContentVisibility);
    }

    public void setSecondaryOnTop(boolean z, boolean z2) {
        this.mSecondaryOnTop = z;
        if (z2) {
            onNavigatorChanged(false);
        }
    }

    public boolean isSecondaryOnTop() {
        return this.mSecondaryOnTop;
    }

    public boolean isSecondaryOnTopNow() {
        return this.mSecondaryOnTopNow;
    }

    public void setSecondaryContentReady(boolean z) {
        if (this.mSecondaryContentReady != z) {
            this.mSecondaryContentReady = z;
            setContentVisible(this.mAnimHelper.mLastRealContentRatio > 0.0f, this.mAnimHelper.mLastRealContentRatio == 1.0f);
        }
    }

    private void setClassNameToButton(View view) {
        if (view == null) {
            return;
        }
        ViewCompat.setAccessibilityDelegate(view, new AccessibilityDelegateCompat() { // from class: miuix.navigator.MiuixNavigationLayout.4
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setClassName(Button.class.getName());
            }
        });
    }

    public void setNavigationSwitch(View view) {
        this.mNavigationSwitch = view;
        view.setOnClickListener(new View.OnClickListener() { // from class: miuix.navigator.MiuixNavigationLayout$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.m1889xb87e3a22(view2);
            }
        });
        this.mNavigationSwitch.setOnDragListener(new View.OnDragListener() { // from class: miuix.navigator.MiuixNavigationLayout$$ExternalSyntheticLambda4
            @Override // android.view.View.OnDragListener
            public final boolean onDrag(View view2, DragEvent dragEvent) {
                return this.f$0.m1890x817f3163(view2, dragEvent);
            }
        });
        this.mNavigationSwitch.setContentDescription(getResources().getString(R.string.miuix_navigator_navigator_switch_description));
        setClassNameToButton(this.mNavigationSwitch);
        this.mAnimHelper.invalidate();
    }

    /* JADX INFO: renamed from: lambda$setNavigationSwitch$2$miuix-navigator-MiuixNavigationLayout, reason: not valid java name */
    /* synthetic */ void m1889xb87e3a22(View view) {
        View viewFindViewById = findViewById(R.id.up);
        this.mOriginalAfterId = this.mNavigationSwitch.getAccessibilityTraversalAfter();
        viewFindViewById.setAccessibilityTraversalAfter(this.mNavigationSwitch.getId());
        if (this.mIsDragging) {
            return;
        }
        closeNavigation();
    }

    /* JADX INFO: renamed from: lambda$setNavigationSwitch$3$miuix-navigator-MiuixNavigationLayout, reason: not valid java name */
    /* synthetic */ boolean m1890x817f3163(View view, DragEvent dragEvent) {
        int action = dragEvent.getAction();
        if (action == 1) {
            this.mIsDragging = true;
            return true;
        }
        if (action != 4) {
            return false;
        }
        this.mIsDragging = false;
        return true;
    }

    public void addContentSwitch(View view, ViewAfterNavigatorSwitchPresenter viewAfterNavigatorSwitchPresenter) {
        SwitchInfo switchInfo = new SwitchInfo();
        switchInfo.view = view;
        switchInfo.presenter = viewAfterNavigatorSwitchPresenter;
        switchInfo.offset = this.mContentViewAfterSwitchOffset;
        this.mContentSwitch.add(switchInfo);
        view.setOnClickListener(new View.OnClickListener() { // from class: miuix.navigator.MiuixNavigationLayout$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.m1884lambda$addContentSwitch$4$miuixnavigatorMiuixNavigationLayout(view2);
            }
        });
        view.setOnDragListener(new DragOnTrigger(new MiuixNavigationLayout$$ExternalSyntheticLambda6(this)) { // from class: miuix.navigator.MiuixNavigationLayout.5
            @Override // miuix.navigator.draganddrop.DragOnTrigger, android.view.View.OnDragListener
            public boolean onDrag(View view2, DragEvent dragEvent) {
                int action = dragEvent.getAction();
                if (action == 1) {
                    MiuixNavigationLayout.this.mIsDragging = true;
                } else if (action == 4) {
                    MiuixNavigationLayout.this.mIsDragging = false;
                }
                return super.onDrag(view2, dragEvent);
            }
        });
        view.setContentDescription(getResources().getString(R.string.miuix_navigator_content_switch_description));
        setClassNameToButton(view);
        this.mAnimHelper.invalidate();
    }

    /* JADX INFO: renamed from: lambda$addContentSwitch$4$miuix-navigator-MiuixNavigationLayout, reason: not valid java name */
    /* synthetic */ void m1884lambda$addContentSwitch$4$miuixnavigatorMiuixNavigationLayout(View view) {
        if (this.mNavigationSwitch != null) {
            this.mNavigationSwitch.setAccessibilityTraversalBefore(this.mOriginalAfterId);
        }
        openNavigation();
    }

    public void removeContentSwitch(View view) {
        Iterator<SwitchInfo> it = this.mContentSwitch.iterator();
        while (it.hasNext()) {
            if (it.next().view == view) {
                it.remove();
                break;
            }
        }
        this.mAnimHelper.invalidate();
    }

    public void addSecondaryContentSwitch(View view, ViewAfterNavigatorSwitchPresenter viewAfterNavigatorSwitchPresenter) {
        SwitchInfo switchInfo = new SwitchInfo();
        switchInfo.view = view;
        switchInfo.presenter = viewAfterNavigatorSwitchPresenter;
        switchInfo.offset = this.mContentViewAfterSwitchOffset;
        this.mSecondaryContentSwitch.add(switchInfo);
        view.setOnClickListener(new View.OnClickListener() { // from class: miuix.navigator.MiuixNavigationLayout$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.m1885x26d1d8b5(view2);
            }
        });
        view.setOnDragListener(new DragOnTrigger(new MiuixNavigationLayout$$ExternalSyntheticLambda6(this)));
        view.setContentDescription(getResources().getString(R.string.miuix_navigator_content_switch_description));
        setClassNameToButton(view);
        this.mAnimHelper.invalidate();
    }

    /* JADX INFO: renamed from: lambda$addSecondaryContentSwitch$5$miuix-navigator-MiuixNavigationLayout, reason: not valid java name */
    /* synthetic */ void m1885x26d1d8b5(View view) {
        openNavigation();
    }

    public void removeSecondaryContentSwitch(View view) {
        Iterator<SwitchInfo> it = this.mSecondaryContentSwitch.iterator();
        while (it.hasNext()) {
            if (it.next().view == view) {
                it.remove();
                break;
            }
        }
        this.mAnimHelper.invalidate();
    }

    public void setCrossBackground(int i) {
        setCrossBackground(LayoutInflater.from(getContext()).inflate(i, (ViewGroup) this.mBackground, false));
    }

    public void setCrossBackground(View view) {
        View view2 = this.mBackgroundView;
        if (view2 == view) {
            return;
        }
        FrameLayout frameLayout = this.mBackground;
        if (frameLayout == null) {
            this.mBackgroundView = view;
            return;
        }
        if (view2 != null) {
            frameLayout.removeAllViews();
        }
        this.mBackgroundView = view;
        this.mBackground.addView(view);
    }

    public void setContentExpandedPaddingWithDp(int i) {
        if (i > 0) {
            this.mPadding = MiuixUIUtils.dp2px(getContext(), i);
        } else {
            this.mPadding = 0;
        }
    }

    public void setContentExpandedMaxWidthWithDp(int i) {
        this.mContentExpandedMaxWidthDp = i;
        int iDp2px = MiuixUIUtils.dp2px(getContext(), i);
        int measuredWidth = getMeasuredWidth();
        if (measuredWidth > iDp2px) {
            this.mPadding = measuredWidth - iDp2px;
        }
    }

    public void setSplitAnimationStyle(int i) {
        this.mSplitAnimationStyle = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sectionAnimateDetailContent() {
        ActionBarOverlayLayout actionBarOverlayLayout;
        if (this.mFlush) {
            ActionBarOverlayLayout actionBarOverlayLayout2 = (ActionBarOverlayLayout) this.mContentParent.findViewById(R.id.secondary_content_decor_wrapper).findViewById(R.id.secondary_content_decor).findViewById(R.id.action_bar_overlay_layout);
            if (actionBarOverlayLayout2 != null) {
                final FrameLayout frameLayout = (FrameLayout) actionBarOverlayLayout2.getContentView();
                final int measuredWidth = actionBarOverlayLayout2.getMeasuredWidth() - frameLayout.getMeasuredWidth();
                final ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                valueAnimatorOfFloat.setDuration(500L);
                valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.navigator.MiuixNavigationLayout$$ExternalSyntheticLambda7
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        this.f$0.m1887x74da5748(valueAnimatorOfFloat, measuredWidth, frameLayout, valueAnimator);
                    }
                });
                valueAnimatorOfFloat.setInterpolator(INTERPOLATOR);
                valueAnimatorOfFloat.start();
                return;
            }
            if (this.mMode != Navigator.Mode.NC || (actionBarOverlayLayout = (ActionBarOverlayLayout) this.mContentDecor.findViewById(R.id.content_decor).findViewById(R.id.action_bar_overlay_layout)) == null) {
                return;
            }
            final FrameLayout frameLayout2 = (FrameLayout) actionBarOverlayLayout.getContentView();
            final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout2.getLayoutParams();
            final int measuredWidth2 = actionBarOverlayLayout.getMeasuredWidth() - frameLayout2.getMeasuredWidth();
            final ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
            valueAnimatorOfFloat2.setDuration(500L);
            valueAnimatorOfFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.navigator.MiuixNavigationLayout$$ExternalSyntheticLambda8
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.m1888x3ddb4e89(valueAnimatorOfFloat2, measuredWidth2, frameLayout2, layoutParams, valueAnimator);
                }
            });
            valueAnimatorOfFloat2.setInterpolator(INTERPOLATOR);
            valueAnimatorOfFloat2.start();
        }
    }

    /* JADX INFO: renamed from: lambda$sectionAnimateDetailContent$6$miuix-navigator-MiuixNavigationLayout, reason: not valid java name */
    /* synthetic */ void m1887x74da5748(ValueAnimator valueAnimator, int i, FrameLayout frameLayout, ValueAnimator valueAnimator2) {
        int i2;
        int iFloatValue = (int) (i * (1.0f - ((Float) valueAnimator.getAnimatedValue()).floatValue()) * 0.5f);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
        if (((this.mMode == Navigator.Mode.NLC && !isContentOpen()) || (this.mMode == Navigator.Mode.NC && !isNavigationOpen())) && iFloatValue < (i2 = this.mPadding)) {
            iFloatValue = i2;
        }
        frameLayout.setPadding(iFloatValue, 0, iFloatValue, 0);
        layoutParams.width = -1;
        frameLayout.setLayoutParams(layoutParams);
    }

    /* JADX INFO: renamed from: lambda$sectionAnimateDetailContent$7$miuix-navigator-MiuixNavigationLayout, reason: not valid java name */
    /* synthetic */ void m1888x3ddb4e89(ValueAnimator valueAnimator, int i, FrameLayout frameLayout, FrameLayout.LayoutParams layoutParams, ValueAnimator valueAnimator2) {
        int i2;
        int iFloatValue = (int) (i * (1.0f - ((Float) valueAnimator.getAnimatedValue()).floatValue()) * 0.5f);
        if (!isNavigationOpen() && iFloatValue < (i2 = this.mPadding)) {
            iFloatValue = i2;
        }
        frameLayout.setPadding(iFloatValue, 0, iFloatValue, 0);
        layoutParams.width = -1;
        frameLayout.setLayoutParams(layoutParams);
    }

    public void setSplitAnimationMaskBlurRadius(int i) {
        if (i <= 0) {
            return;
        }
        this.mBlurRadius = i;
    }

    void setWidthConfig(WidthConfig widthConfig) {
        if (this.mWidthConfig != widthConfig) {
            this.mWidthConfig = widthConfig;
            updateContentWidthConfig();
        }
    }

    WidthConfig getWidthConfig() {
        return this.mWidthConfig;
    }

    public static class AnimHelper {
        private static final String CONTENT_RATIO = "contentRatio";
        private static final String CONTENT_SWITCH_ALPHA = "contentSwitchAlpha";
        private static final String CONTENT_SWITCH_RATIO = "contentSwitchRatio";
        private static final String CONTENT_VIEW_AFTER_SWITCH_RATIO = "contentViewAfterSwitchRatio";
        private static final String NAVIGATION_RATIO = "navigationRatio";
        private static final String NAVIGATION_SWITCH_ALPHA = "navigationSwitchAlpha";
        private static final String NAVIGATION_SWITCH_RATIO = "navigationSwitchRatio";
        private int mContentOffset;
        private float mContentRatio;
        private float mContentSwitchAlpha;
        private float mContentSwitchRatio;
        private int mContentViewAfterSwitchOffset;
        private float mContentViewAfterSwitchRatio;
        private int mCurrentContentViewAfterSwitchOffset;
        private boolean mInvalidated;
        private float mLastRealContentRatio;
        private float mLastRealNavigationRatio;
        private boolean mNavigationHadLayout;
        private int mNavigationOffset;
        private float mNavigationRatio;
        private float mNavigationSwitchAlpha;
        private float mNavigationSwitchRatio;
        private final Runnable mUpdateAnimation;
        private WeakReference<MiuixNavigationLayout> mView;

        /* JADX INFO: renamed from: lambda$new$1$miuix-navigator-MiuixNavigationLayout$AnimHelper, reason: not valid java name */
        /* synthetic */ void m1893lambda$new$1$miuixnavigatorMiuixNavigationLayout$AnimHelper() {
            float f;
            float f2;
            this.mInvalidated = false;
            MiuixNavigationLayout miuixNavigationLayout = this.mView.get();
            if (miuixNavigationLayout == null) {
                return;
            }
            final boolean z = miuixNavigationLayout.getLayoutDirection() == 1;
            float f3 = miuixNavigationLayout.mNavigationWidth * this.mNavigationRatio;
            miuixNavigationLayout.updateContentWidth(f3);
            float f4 = this.mContentRatio * (f3 + miuixNavigationLayout.mContentWidth);
            if (f4 >= miuixNavigationLayout.mContentWidth) {
                f2 = (f4 - miuixNavigationLayout.mContentWidth) / miuixNavigationLayout.mNavigationWidth;
                f = 1.0f;
            } else {
                f = f4 / miuixNavigationLayout.mContentWidth;
                f2 = 0.0f;
            }
            if (Build.VERSION.SDK_INT >= 29) {
                RecyclerView recyclerView = (RecyclerView) miuixNavigationLayout.mNavigationDecor.findViewById(R.id.navigation_rv);
                miuixNavigationLayout.mNavigationDecor.suppressLayout(this.mNavigationHadLayout && f2 < 1.0f && !(recyclerView != null && recyclerView.hasPendingAdapterUpdates()));
            }
            float f5 = this.mLastRealNavigationRatio;
            if (f5 == 0.0f && f2 > 0.0f) {
                miuixNavigationLayout.mNavigationDecor.setVisibility(0);
                this.mNavigationHadLayout = true;
            } else if (f5 > 0.0f && f2 == 0.0f) {
                miuixNavigationLayout.mNavigationDecor.setVisibility(4);
            }
            if (f2 == 0.0f) {
                miuixNavigationLayout.notifyNavigationVisible(false, false);
            } else if (f2 == 1.0f) {
                miuixNavigationLayout.notifyNavigationVisible(true, miuixNavigationLayout.mNavigationMask == null || miuixNavigationLayout.mNavigationMask.getVisibility() != 0);
            } else {
                miuixNavigationLayout.notifyNavigationVisible(true, false);
            }
            float f6 = miuixNavigationLayout.mNavigationWidth * f2;
            if (z) {
                f6 = -f6;
            }
            miuixNavigationLayout.mNavigationDecor.setTranslationX(f6);
            miuixNavigationLayout.mContentParent.setTranslationX(miuixNavigationLayout.mOverlay ? 0.0f : f6);
            miuixNavigationLayout.mNavigationDivider.setTranslationX(f6);
            this.mLastRealNavigationRatio = f2;
            this.mLastRealContentRatio = f;
            miuixNavigationLayout.setContentVisible(f > 0.0f, f == 1.0f);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) miuixNavigationLayout.mContentDecor.getLayoutParams();
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) miuixNavigationLayout.mSecondaryContentDecor.getLayoutParams();
            if (miuixNavigationLayout.mShowBothContent) {
                float f7 = miuixNavigationLayout.mContentWidth * (f - 1.0f);
                if (z) {
                    f7 = -f7;
                }
                miuixNavigationLayout.mContentDecor.setTranslationX(f7);
                marginLayoutParams.width = miuixNavigationLayout.mContentWidth;
                ViewGroup.MarginLayoutParams marginLayoutParams3 = (ViewGroup.MarginLayoutParams) miuixNavigationLayout.mDivider.getLayoutParams();
                marginLayoutParams3.setMarginStart((int) ((miuixNavigationLayout.mContentWidth * f) - (miuixNavigationLayout.mDivider.getVisibility() != 8 ? marginLayoutParams3.width : 0)));
                miuixNavigationLayout.mDivider.setLayoutParams(marginLayoutParams3);
                ViewGroup.MarginLayoutParams marginLayoutParams4 = (ViewGroup.MarginLayoutParams) miuixNavigationLayout.mSecondaryContentDecor.getLayoutParams();
                marginLayoutParams4.setMarginStart((int) (miuixNavigationLayout.mContentWidth * f));
                miuixNavigationLayout.mSecondaryContentDecor.setLayoutParams(marginLayoutParams4);
            } else {
                miuixNavigationLayout.mContentDecor.setTranslationX(0.0f);
                marginLayoutParams.width = -1;
                marginLayoutParams2.setMarginStart(0);
                miuixNavigationLayout.mSecondaryContentDecor.setLayoutParams(marginLayoutParams2);
            }
            miuixNavigationLayout.updateDividers();
            if (miuixNavigationLayout.mNavigationSwitch != null) {
                NavigatorSwitchPresenter navigatorSwitchPresenter = (NavigatorSwitchPresenter) miuixNavigationLayout.mNavigationSwitch.getTag(R.id.miuix_appcompat_navigator_switch_presenter);
                navigatorSwitchPresenter.setVisibility(this.mNavigationSwitchRatio == 0.0f ? 8 : 0);
                navigatorSwitchPresenter.setAlpha(this.mNavigationSwitchAlpha);
                miuixNavigationLayout.mNavigationSwitch.setTranslationX(this.mNavigationOffset * (1.0f - this.mNavigationSwitchRatio));
            }
            Consumer consumer = new Consumer() { // from class: miuix.navigator.MiuixNavigationLayout$AnimHelper$$ExternalSyntheticLambda0
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    this.f$0.m1892lambda$new$0$miuixnavigatorMiuixNavigationLayout$AnimHelper(z, (SwitchInfo) obj);
                }
            };
            miuixNavigationLayout.forAllSwitch(miuixNavigationLayout.mContentSwitch, consumer);
            miuixNavigationLayout.forAllSwitch(miuixNavigationLayout.mSecondaryContentSwitch, consumer);
            miuixNavigationLayout.requestLayout();
        }

        /* JADX INFO: renamed from: lambda$new$0$miuix-navigator-MiuixNavigationLayout$AnimHelper, reason: not valid java name */
        /* synthetic */ void m1892lambda$new$0$miuixnavigatorMiuixNavigationLayout$AnimHelper(boolean z, SwitchInfo switchInfo) {
            float f;
            float f2;
            NavigatorSwitchPresenter navigatorSwitchPresenter = (NavigatorSwitchPresenter) switchInfo.view.getTag(R.id.miuix_appcompat_navigator_switch_presenter);
            navigatorSwitchPresenter.setVisibility(this.mContentSwitchRatio != 0.0f ? 0 : 8);
            navigatorSwitchPresenter.setAlpha(this.mContentSwitchAlpha);
            switchInfo.view.setTranslationX(this.mContentOffset * (1.0f - this.mContentSwitchRatio));
            this.mCurrentContentViewAfterSwitchOffset = switchInfo.view.getVisibility() == 0 ? switchInfo.offset : 0;
            if (switchInfo.presenter != null) {
                if (z) {
                    f = this.mCurrentContentViewAfterSwitchOffset;
                    f2 = this.mContentViewAfterSwitchRatio - 1.0f;
                } else {
                    f = this.mCurrentContentViewAfterSwitchOffset;
                    f2 = 1.0f - this.mContentViewAfterSwitchRatio;
                }
                float f3 = f * f2;
                switchInfo.presenter.setTranslationX(f3);
                float f4 = this.mContentViewAfterSwitchRatio;
                if (f4 == 0.0f || f4 == 1.0f) {
                    switchInfo.presenter.setTranslationXTagForHover(f3);
                } else {
                    switchInfo.presenter.ignoreHover();
                }
            }
        }

        private AnimHelper(MiuixNavigationLayout miuixNavigationLayout) {
            this.mNavigationRatio = 1.0f;
            this.mContentRatio = 1.0f;
            this.mUpdateAnimation = new Runnable() { // from class: miuix.navigator.MiuixNavigationLayout$AnimHelper$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1893lambda$new$1$miuixnavigatorMiuixNavigationLayout$AnimHelper();
                }
            };
            this.mView = new WeakReference<>(miuixNavigationLayout);
        }

        public void setNavigationRatio(float f) {
            if (this.mNavigationRatio == f) {
                return;
            }
            this.mNavigationRatio = f;
            postInvalidate();
        }

        public float getNavigationRatio() {
            return this.mNavigationRatio;
        }

        public void setContentRatio(float f) {
            if (this.mContentRatio == f) {
                return;
            }
            this.mContentRatio = f;
            postInvalidate();
        }

        public float getContentRatio() {
            return this.mContentRatio;
        }

        public void setNavigationSwitchRatio(float f) {
            if (this.mNavigationSwitchRatio == f) {
                return;
            }
            this.mNavigationSwitchRatio = f;
            postInvalidate();
        }

        public float getNavigationSwitchRatio() {
            return this.mNavigationSwitchRatio;
        }

        public void setContentSwitchRatio(float f) {
            if (this.mContentSwitchRatio == f) {
                return;
            }
            this.mContentSwitchRatio = f;
            postInvalidate();
        }

        public float getContentSwitchRatio() {
            return this.mContentSwitchRatio;
        }

        public void setContentViewAfterSwitchRatio(float f) {
            if (this.mContentViewAfterSwitchRatio == f) {
                return;
            }
            this.mContentViewAfterSwitchRatio = f;
            postInvalidate();
        }

        public float getContentViewAfterSwitchRatio() {
            return this.mContentViewAfterSwitchRatio;
        }

        public void setNavigationSwitchAlpha(float f) {
            if (this.mNavigationSwitchAlpha == f) {
                return;
            }
            this.mNavigationSwitchAlpha = f;
            postInvalidate();
        }

        public float getNavigationSwitchAlpha() {
            return this.mNavigationSwitchAlpha;
        }

        public void setContentSwitchAlpha(float f) {
            if (this.mContentSwitchAlpha == f) {
                return;
            }
            this.mContentSwitchAlpha = f;
            postInvalidate();
        }

        public float getContentSwitchAlpha() {
            return this.mContentSwitchAlpha;
        }

        public void invalidate() {
            MiuixNavigationLayout miuixNavigationLayout;
            if (this.mInvalidated && (miuixNavigationLayout = this.mView.get()) != null) {
                miuixNavigationLayout.removeCallbacks(this.mUpdateAnimation);
            }
            this.mUpdateAnimation.run();
        }

        public void postInvalidate() {
            MiuixNavigationLayout miuixNavigationLayout;
            if (this.mInvalidated || (miuixNavigationLayout = this.mView.get()) == null) {
                return;
            }
            this.mInvalidated = true;
            miuixNavigationLayout.removeCallbacks(this.mUpdateAnimation);
            miuixNavigationLayout.postOnAnimation(this.mUpdateAnimation);
        }
    }
}
