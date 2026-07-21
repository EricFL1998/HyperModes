package miuix.bottomsheet;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityViewCommand;
import androidx.customview.view.AbsSavedState;
import androidx.customview.widget.ViewDragHelper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import miuix.animation.Folme;
import miuix.animation.FolmeEase;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ViewProperty;
import miuix.container.ExtraPaddingPolicy;
import miuix.core.util.EnvStateManager;
import miuix.core.util.IntentUtils;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.ScreenModeHelper;
import miuix.core.util.WindowBaseInfo;
import miuix.core.util.WindowUtils;
import miuix.internal.util.ViewUtils;
import miuix.os.DeviceHelper;
import miuix.theme.token.ContainerToken;
import miuix.view.WindowInsetsController;

/* JADX INFO: loaded from: classes2.dex */
public class BottomSheetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    private static final int ANIM_END_THRESHOLD = 10;
    private static final int DEFAULT_SIGNIFICANT_DISTANCE_THRESHOLD = 60;
    static final int DEFAULT_SIGNIFICANT_VEL_THRESHOLD = 1000;
    public static final int EXPANDED_OFFSET_AUTO = -1;
    private static final String FOLME_KEY = "folme_key";
    private static final String FOLME_TARGET_RELEASE = "bottom_sheet_release";
    private static final float HIDE_FRICTION = 0.1f;
    private static final float HIDE_THRESHOLD = 0.5f;
    private static final int INSET_TOP_UNDEFINED = -1;
    private static final int INVALID_POSITION = -1;
    private static final int MAX_SPEED = 10000;
    private static final int NO_MAX_SIZE = -1;
    public static final int PEEK_HEIGHT_AUTO = -1;
    public static final int SAVE_ALL = -1;
    public static final int SAVE_FIT_TO_CONTENTS = 2;
    public static final int SAVE_HIDEABLE = 4;
    public static final int SAVE_NONE = 0;
    public static final int SAVE_PEEK_HEIGHT = 1;
    public static final int SAVE_SKIP_COLLAPSED = 8;
    public static final int STATE_COLLAPSED = 4;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_EXPANDED = 3;
    public static final int STATE_HALF_EXPANDED = 6;
    public static final int STATE_HIDDEN = 5;
    public static final int STATE_SETTLING = 2;
    private static final String TAG = "BottomSheetBehavior";
    private static final int THRESHOLD_FLOATING_WINDOW_HEIGHT_DP = 660;
    private static final int THRESHOLD_FLOATING_WINDOW_WIDTH_DP = 670;
    static final int VIEW_INDEX_ACCESSIBILITY_DELEGATE_VIEW = 1;
    private static final int VIEW_INDEX_BOTTOM_SHEET = 0;
    WeakReference<View> accessibilityDelegateViewRef;
    int activePointerId;
    private boolean animInterruptible;
    private boolean animRunning;
    private AttributeSet attrs;
    private ColorStateList backgroundTint;
    private AnimConfig bottomEnterAnimConfig;
    private IStateStyle bottomEnterAnimStateStyle;
    private TransitionListener bottomEnterAnimTransitionListener;
    private AnimConfig bottomExitAnimConfig;
    private IStateStyle bottomExitAnimStateStyle;
    private TransitionListener bottomExitAnimTransitionListener;
    private int bottomModeLandscapeWidth;
    private boolean bottomModeLandscapeWidthRuleEnabled;
    private int bottomModeMaxWidth;
    private final ArrayList<BottomSheetCallback> callbacks;
    private int childHeight;
    private int childYInWindow;
    int collapsedOffset;
    private int defaultExpandedOffset;
    private int defaultHighExpandedOffset;
    private float density;
    private boolean dismissed;
    private final ViewDragHelper.Callback dragCallback;
    private boolean draggable;
    float elevation;
    final SparseIntArray expandHalfwayActionIds;
    int expandedOffset;
    private int extraHeight;
    private boolean extraPaddingEnabled;
    private ExtraPaddingPolicy extraPaddingPolicy;
    int fitToContentsOffset;
    private float fixedHeightRatio;
    private boolean fixedHeightRatioEnabled;
    private AnimConfig floatingEnterAnimConfig;
    private IStateStyle floatingEnterAnimStateStyle;
    private TransitionListener floatingEnterAnimTransitionListener;
    private AnimConfig floatingExitAnimConfig;
    private IStateStyle floatingExitAnimStateStyle;
    private TransitionListener floatingExitAnimTransitionListener;
    private boolean floatingModeDependsOnWindow;
    private int floatingModeHeight;
    private float floatingModeHeightRatio;
    private int floatingModeWidth;
    private boolean forceFullHeight;
    private float fullHeightHighRatio;
    private float fullHeightLowRatio;
    private int fullHeightLowRatioThreshold;
    private float fullHeightMiddleRatio;
    private int fullHeightMiddleRatioThreshold;
    private boolean fullHeightMode;
    private int gestureInsetBottom;
    private boolean gestureInsetBottomIgnored;
    int halfExpandedOffset;
    float halfExpandedRatio;
    float halfExpandedRatioWhenFixHeightRatio;
    private float hideFriction;
    boolean hideable;
    private int highExpandedOffsetThreshold;
    private int horizontalExtraPadding;
    private boolean ignoreEvents;
    private Map<View, Integer> importantForAccessibilityMap;
    private boolean improveAnimPerformance;
    private int initialY;
    private int insetBottom;
    private int insetTop;
    private int insetTopInMeasureStep;
    private boolean internalDraggable;
    private boolean internalFixedHeightRatioEnabled;
    private int lastMode;
    private int lastNestedScrollDy;
    int lastStableState;
    private int mDeviceType;
    private RequestLayoutRunnable mRequestLayoutRunnable;
    private boolean marginLeftSystemWindowInsets;
    private boolean marginRightSystemWindowInsets;
    private int maxHeight;
    private int maxWidth;
    private float maximumVelocity;
    private int minHeight;
    private int mode;
    private int modeConfig;
    private boolean nestedScrolled;
    WeakReference<View> nestedScrollingChildRef;
    private OnExtraPaddingListener onExtraPaddingListener;
    private OnModeChangeListener onModeChangeListener;
    private boolean originalWindowInsetsEnabled;
    private boolean paddingBottomSystemWindowInsets;
    private boolean paddingLeftSystemWindowInsets;
    private boolean paddingRightSystemWindowInsets;
    int parentHeight;
    WeakReference<CoordinatorLayout> parentViewRef;
    int parentWidth;
    private int peekHeight;
    private boolean peekHeightAuto;
    private int peekHeightGestureInsetBuffer;
    private int peekHeightMin;
    private AnimConfig releaseAnimConfig;
    private ReleaseAnimListener releaseAnimListener;
    private IStateStyle releaseAnimStateStyle;
    private TransitionListener releaseAnimTransitionListener;
    private final AnimState releaseEndAnimState;
    private final AnimState releaseStartAnimState;
    private int saveFlags;
    private boolean shouldResetChildBeforeFirstShow;
    private int significantDistanceThreshold;
    private int significantVelocityThreshold;
    private boolean skipCollapsed;
    private boolean skipHalfExpanded;
    private int stableStateChildTop;
    int state;
    private final BottomSheetBehavior<V>.StateSettlingTracker stateSettlingTracker;
    boolean touchingScrollingChild;
    private boolean updateImportantForAccessibilityOnSiblings;
    private VelocityTracker velocityTracker;
    ViewDragHelper viewDragHelper;
    WeakReference<V> viewRef;

    interface AnimationListener {
        void onAnimationEnd();

        void onAnimationStart();
    }

    public static abstract class BottomSheetCallback {
        void onLayout(View view) {
        }

        public abstract void onSlide(View view, float f);

        public abstract void onStateChanged(View view, int i);
    }

    public interface OnExtraPaddingListener {
        void onExtraPaddingChanged(int i);
    }

    public interface OnModeChangeListener {
        void onModeChange(int i, View view);
    }

    interface ReleaseAnimListener {
        void onEnd(int i);

        void onStart(int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface SaveFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface StableState {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public boolean isHideableWhenDragging() {
        return true;
    }

    public boolean isNestedScrollingCheckEnabled() {
        return true;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, V v, View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
    }

    public boolean shouldExpandOnUpwardDrag(long j, float f) {
        return false;
    }

    public boolean shouldSkipHalfExpandedStateWhenDragging() {
        return false;
    }

    public boolean shouldSkipSmoothAnimation() {
        return true;
    }

    public BottomSheetBehavior() {
        this.saveFlags = 0;
        this.updateImportantForAccessibilityOnSiblings = false;
        this.maxWidth = -1;
        this.maxHeight = -1;
        this.insetTop = -1;
        this.stateSettlingTracker = new StateSettlingTracker();
        this.halfExpandedRatio = 0.5f;
        this.halfExpandedRatioWhenFixHeightRatio = 0.7f;
        this.elevation = -1.0f;
        this.draggable = true;
        this.state = 4;
        this.lastStableState = 4;
        this.hideFriction = 0.1f;
        this.callbacks = new ArrayList<>();
        this.initialY = -1;
        this.expandHalfwayActionIds = new SparseIntArray();
        this.modeConfig = 0;
        this.mode = 0;
        this.lastMode = -1;
        this.fullHeightMode = false;
        this.fullHeightHighRatio = 0.8f;
        this.fullHeightMiddleRatio = 0.7f;
        this.fullHeightLowRatio = 0.0f;
        this.skipHalfExpanded = false;
        this.forceFullHeight = false;
        this.fixedHeightRatioEnabled = false;
        this.internalFixedHeightRatioEnabled = false;
        this.fixedHeightRatio = 0.7f;
        this.bottomModeLandscapeWidthRuleEnabled = false;
        this.originalWindowInsetsEnabled = false;
        this.internalDraggable = true;
        this.animRunning = false;
        this.animInterruptible = false;
        this.horizontalExtraPadding = 0;
        this.extraPaddingEnabled = true;
        this.improveAnimPerformance = false;
        this.dismissed = false;
        this.shouldResetChildBeforeFirstShow = true;
        this.releaseStartAnimState = new AnimState();
        this.releaseEndAnimState = new AnimState();
        this.dragCallback = new ViewDragHelper.Callback() { // from class: miuix.bottomsheet.BottomSheetBehavior.6
            private long viewCapturedMillis;

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public boolean tryCaptureView(View view, int i) {
                if (BottomSheetBehavior.this.animRunning || BottomSheetBehavior.this.state == 1 || BottomSheetBehavior.this.touchingScrollingChild) {
                    return false;
                }
                if (BottomSheetBehavior.this.state == 3 && BottomSheetBehavior.this.activePointerId == i) {
                    View view2 = BottomSheetBehavior.this.nestedScrollingChildRef != null ? BottomSheetBehavior.this.nestedScrollingChildRef.get() : null;
                    if (view2 != null && view2.canScrollVertically(-1)) {
                        return false;
                    }
                }
                this.viewCapturedMillis = System.currentTimeMillis();
                return BottomSheetBehavior.this.viewRef != null && BottomSheetBehavior.this.viewRef.get() == view;
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
                BottomSheetBehavior.this.dispatchOnSlide(i2);
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public void onViewDragStateChanged(int i) {
                if (i == 1 && BottomSheetBehavior.this.draggable) {
                    BottomSheetBehavior.this.setStateInternal(1);
                }
            }

            private boolean releasedLow(View view) {
                return view.getTop() > (BottomSheetBehavior.this.parentHeight + BottomSheetBehavior.this.getExpandedOffset()) / 2;
            }

            /* JADX WARN: Code duplicated, block: B:39:0x00b1  */
            /* JADX WARN: Code duplicated, block: B:6:0x0010  */
            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public void onViewReleased(View view, float f, float f2) {
                int i = 6;
                if (f2 < 0.0f) {
                    if (BottomSheetBehavior.this.shouldSkipHalfExpanded()) {
                        i = 3;
                    } else {
                        int top = view.getTop();
                        long jCurrentTimeMillis = System.currentTimeMillis() - this.viewCapturedMillis;
                        if (BottomSheetBehavior.this.shouldSkipHalfExpandedStateWhenDragging()) {
                            if (!BottomSheetBehavior.this.shouldExpandOnUpwardDrag(jCurrentTimeMillis, (top * 100.0f) / BottomSheetBehavior.this.parentHeight)) {
                                i = 4;
                            }
                        } else if (top <= BottomSheetBehavior.this.halfExpandedOffset) {
                        }
                        i = 3;
                    }
                } else if (BottomSheetBehavior.this.hideable && BottomSheetBehavior.this.shouldHide(view, f2)) {
                    if ((Math.abs(f) < Math.abs(f2) && f2 > BottomSheetBehavior.this.significantVelocityThreshold) || releasedLow(view)) {
                        i = 5;
                    } else if (BottomSheetBehavior.this.shouldSkipHalfExpanded() || Math.abs(view.getTop() - BottomSheetBehavior.this.getExpandedOffset()) < Math.abs(view.getTop() - BottomSheetBehavior.this.halfExpandedOffset)) {
                        i = 3;
                    }
                } else if (f2 == 0.0f || Math.abs(f) > Math.abs(f2)) {
                    int top2 = view.getTop();
                    if (BottomSheetBehavior.this.shouldSkipHalfExpanded()) {
                        if (Math.abs(top2 - BottomSheetBehavior.this.fitToContentsOffset) < Math.abs(top2 - BottomSheetBehavior.this.collapsedOffset)) {
                            i = 3;
                        } else {
                            i = 4;
                        }
                    } else if (top2 < BottomSheetBehavior.this.halfExpandedOffset) {
                        if (top2 < Math.abs(top2 - BottomSheetBehavior.this.collapsedOffset)) {
                            i = 3;
                        } else if (BottomSheetBehavior.this.shouldSkipHalfExpandedStateWhenDragging()) {
                            i = 4;
                        }
                    } else if (Math.abs(top2 - BottomSheetBehavior.this.halfExpandedOffset) >= Math.abs(top2 - BottomSheetBehavior.this.collapsedOffset) || BottomSheetBehavior.this.shouldSkipHalfExpandedStateWhenDragging()) {
                        i = 4;
                    }
                } else if (BottomSheetBehavior.this.shouldSkipHalfExpanded()) {
                    i = 4;
                } else {
                    int top3 = view.getTop();
                    if (Math.abs(top3 - BottomSheetBehavior.this.halfExpandedOffset) >= Math.abs(top3 - BottomSheetBehavior.this.collapsedOffset) || BottomSheetBehavior.this.shouldSkipHalfExpandedStateWhenDragging()) {
                        i = 4;
                    }
                }
                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.this;
                bottomSheetBehavior.startSettling(view, i, bottomSheetBehavior.shouldSkipSmoothAnimation());
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int clampViewPositionVertical(View view, int i, int i2) {
                return MathUtils.clamp(i, BottomSheetBehavior.this.getExpandedOffset(), getViewVerticalDragRange(view));
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int clampViewPositionHorizontal(View view, int i, int i2) {
                return view.getLeft();
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int getViewVerticalDragRange(View view) {
                if (BottomSheetBehavior.this.canBeHiddenByDragging()) {
                    return BottomSheetBehavior.this.parentHeight;
                }
                return BottomSheetBehavior.this.collapsedOffset;
            }
        };
    }

    public BottomSheetBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.saveFlags = 0;
        this.updateImportantForAccessibilityOnSiblings = false;
        this.maxWidth = -1;
        this.maxHeight = -1;
        this.insetTop = -1;
        this.stateSettlingTracker = new StateSettlingTracker();
        this.halfExpandedRatio = 0.5f;
        this.halfExpandedRatioWhenFixHeightRatio = 0.7f;
        this.elevation = -1.0f;
        this.draggable = true;
        this.state = 4;
        this.lastStableState = 4;
        this.hideFriction = 0.1f;
        this.callbacks = new ArrayList<>();
        this.initialY = -1;
        this.expandHalfwayActionIds = new SparseIntArray();
        this.modeConfig = 0;
        this.mode = 0;
        this.lastMode = -1;
        this.fullHeightMode = false;
        this.fullHeightHighRatio = 0.8f;
        this.fullHeightMiddleRatio = 0.7f;
        this.fullHeightLowRatio = 0.0f;
        this.skipHalfExpanded = false;
        this.forceFullHeight = false;
        this.fixedHeightRatioEnabled = false;
        this.internalFixedHeightRatioEnabled = false;
        this.fixedHeightRatio = 0.7f;
        this.bottomModeLandscapeWidthRuleEnabled = false;
        this.originalWindowInsetsEnabled = false;
        this.internalDraggable = true;
        this.animRunning = false;
        this.animInterruptible = false;
        this.horizontalExtraPadding = 0;
        this.extraPaddingEnabled = true;
        this.improveAnimPerformance = false;
        this.dismissed = false;
        this.shouldResetChildBeforeFirstShow = true;
        this.releaseStartAnimState = new AnimState();
        this.releaseEndAnimState = new AnimState();
        this.dragCallback = new ViewDragHelper.Callback() { // from class: miuix.bottomsheet.BottomSheetBehavior.6
            private long viewCapturedMillis;

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public boolean tryCaptureView(View view, int i) {
                if (BottomSheetBehavior.this.animRunning || BottomSheetBehavior.this.state == 1 || BottomSheetBehavior.this.touchingScrollingChild) {
                    return false;
                }
                if (BottomSheetBehavior.this.state == 3 && BottomSheetBehavior.this.activePointerId == i) {
                    View view2 = BottomSheetBehavior.this.nestedScrollingChildRef != null ? BottomSheetBehavior.this.nestedScrollingChildRef.get() : null;
                    if (view2 != null && view2.canScrollVertically(-1)) {
                        return false;
                    }
                }
                this.viewCapturedMillis = System.currentTimeMillis();
                return BottomSheetBehavior.this.viewRef != null && BottomSheetBehavior.this.viewRef.get() == view;
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
                BottomSheetBehavior.this.dispatchOnSlide(i2);
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public void onViewDragStateChanged(int i) {
                if (i == 1 && BottomSheetBehavior.this.draggable) {
                    BottomSheetBehavior.this.setStateInternal(1);
                }
            }

            private boolean releasedLow(View view) {
                return view.getTop() > (BottomSheetBehavior.this.parentHeight + BottomSheetBehavior.this.getExpandedOffset()) / 2;
            }

            /* JADX WARN: Code duplicated, block: B:39:0x00b1  */
            /* JADX WARN: Code duplicated, block: B:6:0x0010  */
            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public void onViewReleased(View view, float f, float f2) {
                int i = 6;
                if (f2 < 0.0f) {
                    if (BottomSheetBehavior.this.shouldSkipHalfExpanded()) {
                        i = 3;
                    } else {
                        int top = view.getTop();
                        long jCurrentTimeMillis = System.currentTimeMillis() - this.viewCapturedMillis;
                        if (BottomSheetBehavior.this.shouldSkipHalfExpandedStateWhenDragging()) {
                            if (!BottomSheetBehavior.this.shouldExpandOnUpwardDrag(jCurrentTimeMillis, (top * 100.0f) / BottomSheetBehavior.this.parentHeight)) {
                                i = 4;
                            }
                        } else if (top <= BottomSheetBehavior.this.halfExpandedOffset) {
                        }
                        i = 3;
                    }
                } else if (BottomSheetBehavior.this.hideable && BottomSheetBehavior.this.shouldHide(view, f2)) {
                    if ((Math.abs(f) < Math.abs(f2) && f2 > BottomSheetBehavior.this.significantVelocityThreshold) || releasedLow(view)) {
                        i = 5;
                    } else if (BottomSheetBehavior.this.shouldSkipHalfExpanded() || Math.abs(view.getTop() - BottomSheetBehavior.this.getExpandedOffset()) < Math.abs(view.getTop() - BottomSheetBehavior.this.halfExpandedOffset)) {
                        i = 3;
                    }
                } else if (f2 == 0.0f || Math.abs(f) > Math.abs(f2)) {
                    int top2 = view.getTop();
                    if (BottomSheetBehavior.this.shouldSkipHalfExpanded()) {
                        if (Math.abs(top2 - BottomSheetBehavior.this.fitToContentsOffset) < Math.abs(top2 - BottomSheetBehavior.this.collapsedOffset)) {
                            i = 3;
                        } else {
                            i = 4;
                        }
                    } else if (top2 < BottomSheetBehavior.this.halfExpandedOffset) {
                        if (top2 < Math.abs(top2 - BottomSheetBehavior.this.collapsedOffset)) {
                            i = 3;
                        } else if (BottomSheetBehavior.this.shouldSkipHalfExpandedStateWhenDragging()) {
                            i = 4;
                        }
                    } else if (Math.abs(top2 - BottomSheetBehavior.this.halfExpandedOffset) >= Math.abs(top2 - BottomSheetBehavior.this.collapsedOffset) || BottomSheetBehavior.this.shouldSkipHalfExpandedStateWhenDragging()) {
                        i = 4;
                    }
                } else if (BottomSheetBehavior.this.shouldSkipHalfExpanded()) {
                    i = 4;
                } else {
                    int top3 = view.getTop();
                    if (Math.abs(top3 - BottomSheetBehavior.this.halfExpandedOffset) >= Math.abs(top3 - BottomSheetBehavior.this.collapsedOffset) || BottomSheetBehavior.this.shouldSkipHalfExpandedStateWhenDragging()) {
                        i = 4;
                    }
                }
                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.this;
                bottomSheetBehavior.startSettling(view, i, bottomSheetBehavior.shouldSkipSmoothAnimation());
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int clampViewPositionVertical(View view, int i, int i2) {
                return MathUtils.clamp(i, BottomSheetBehavior.this.getExpandedOffset(), getViewVerticalDragRange(view));
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int clampViewPositionHorizontal(View view, int i, int i2) {
                return view.getLeft();
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int getViewVerticalDragRange(View view) {
                if (BottomSheetBehavior.this.canBeHiddenByDragging()) {
                    return BottomSheetBehavior.this.parentHeight;
                }
                return BottomSheetBehavior.this.collapsedOffset;
            }
        };
        this.density = context.getResources().getDisplayMetrics().density;
        this.attrs = attributeSet;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.MiuixBottomSheetBehavior_Layout);
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_backgroundTint)) {
            this.backgroundTint = getColorStateList(context, typedArrayObtainStyledAttributes, R.styleable.MiuixBottomSheetBehavior_Layout_miuix_backgroundTint);
        }
        setHideable(typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_hideable, false));
        setGestureInsetBottomIgnored(typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixBottomSheetBehavior_Layout_miuixGestureInsetBottomIgnored, false));
        setForceFullHeight(typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_force_full_height, false));
        setSkipCollapsed(typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_skipCollapsed, false));
        setDraggable(typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_draggable, true));
        setSaveFlags(typedArrayObtainStyledAttributes.getInt(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_saveFlags, 0));
        setHalfExpandedRatio(typedArrayObtainStyledAttributes.getFloat(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_halfExpandedRatio, 0.5f));
        setSignificantVelocityThreshold(typedArrayObtainStyledAttributes.getInt(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_significantVelocityThreshold, 1000));
        setSignificantDistanceThreshold(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_significantDistanceThreshold, MiuixUIUtils.dp2px(context, 60.0f)));
        this.paddingBottomSystemWindowInsets = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixBottomSheetBehavior_Layout_miuixPaddingBottomSystemWindowInsets, true);
        this.paddingLeftSystemWindowInsets = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixBottomSheetBehavior_Layout_miuixPaddingLeftSystemWindowInsets, false);
        this.paddingRightSystemWindowInsets = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixBottomSheetBehavior_Layout_miuixPaddingRightSystemWindowInsets, false);
        this.marginLeftSystemWindowInsets = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixBottomSheetBehavior_Layout_miuixMarginLeftSystemWindowInsets, true);
        this.marginRightSystemWindowInsets = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixBottomSheetBehavior_Layout_miuixMarginRightSystemWindowInsets, true);
        this.modeConfig = typedArrayObtainStyledAttributes.getInt(R.styleable.MiuixBottomSheetBehavior_Layout_modeConfig, 0);
        this.floatingModeHeightRatio = typedArrayObtainStyledAttributes.getFloat(R.styleable.MiuixBottomSheetBehavior_Layout_floatingModeHeightRatio, 0.8f);
        this.fullHeightHighRatio = typedArrayObtainStyledAttributes.getFloat(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_full_height_high_ratio, 0.8f);
        this.fullHeightMiddleRatio = typedArrayObtainStyledAttributes.getFloat(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_full_height_middle_ratio, 0.7f);
        this.fullHeightLowRatio = typedArrayObtainStyledAttributes.getFloat(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_full_height_low_ratio, 0.0f);
        float f = typedArrayObtainStyledAttributes.getFloat(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_fixed_height_ratio, 0.7f);
        this.fixedHeightRatio = f;
        this.halfExpandedRatioWhenFixHeightRatio = f;
        setFloatingModeDependsOnWindow(typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixBottomSheetBehavior_Layout_floatingModeDependsOnWindow, true));
        typedArrayObtainStyledAttributes.recycle();
        updateSizeConfig(context, attributeSet);
        this.mDeviceType = DeviceHelper.detectType(context);
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
    }

    private void updateSizeConfig(Context context, AttributeSet attributeSet) {
        if (context == null) {
            return;
        }
        Resources resources = context.getResources();
        this.peekHeightGestureInsetBuffer = resources.getDimensionPixelSize(R.dimen.miuix_min_touch_target_size);
        this.minHeight = resources.getDimensionPixelSize(R.dimen.miuix_bottom_sheet_min_height);
        this.defaultExpandedOffset = resources.getDimensionPixelSize(R.dimen.miuix_bottom_sheet_default_expanded_offset);
        this.defaultHighExpandedOffset = resources.getDimensionPixelSize(R.dimen.miuix_bottom_sheet_default_high_expanded_offset);
        this.highExpandedOffsetThreshold = resources.getDimensionPixelSize(R.dimen.miuix_bottom_sheet_high_expanded_offset_threshold);
        this.fullHeightLowRatioThreshold = resources.getDimensionPixelSize(R.dimen.miuix_bottom_sheet_full_height_low_ratio_threshold);
        this.fullHeightMiddleRatioThreshold = resources.getDimensionPixelSize(R.dimen.miuix_bottom_sheet_full_height_middle_ratio_threshold);
        this.extraHeight = resources.getDimensionPixelSize(R.dimen.miuix_bottom_sheet_extra_height);
        if (attributeSet == null) {
            return;
        }
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.MiuixBottomSheetBehavior_Layout);
        this.elevation = typedArrayObtainStyledAttributes.getDimension(R.styleable.MiuixBottomSheetBehavior_Layout_android_elevation, -1.0f);
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixBottomSheetBehavior_Layout_android_maxWidth)) {
            setMaxWidth(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixBottomSheetBehavior_Layout_android_maxWidth, -1));
        }
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixBottomSheetBehavior_Layout_android_maxHeight)) {
            setMaxHeight(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixBottomSheetBehavior_Layout_android_maxHeight, -1));
        }
        TypedValue typedValuePeekValue = typedArrayObtainStyledAttributes.peekValue(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_peekHeight);
        if (typedValuePeekValue != null && typedValuePeekValue.data == -1) {
            setPeekHeight(typedValuePeekValue.data);
        } else {
            setPeekHeight(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_peekHeight, -1));
        }
        TypedValue typedValuePeekValue2 = typedArrayObtainStyledAttributes.peekValue(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_expandedOffset);
        if (typedValuePeekValue2 != null && typedValuePeekValue2.type == 16) {
            setExpandedOffset(typedValuePeekValue2.data);
        } else {
            setExpandedOffset(typedArrayObtainStyledAttributes.getDimensionPixelOffset(R.styleable.MiuixBottomSheetBehavior_Layout_miuix_behavior_expandedOffset, -1));
        }
        this.floatingModeWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixBottomSheetBehavior_Layout_floatingModeWidth, resources.getDimensionPixelSize(R.dimen.miuix_bottom_sheet_floating_width));
        this.floatingModeHeight = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixBottomSheetBehavior_Layout_floatingModeHeight, resources.getDimensionPixelSize(R.dimen.miuix_bottom_sheet_floating_height));
        setBottomModeMaxWidth(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixBottomSheetBehavior_Layout_bottomModeMaxWidth, resources.getDimensionPixelSize(R.dimen.miuix_bottom_sheet_default_max_width)));
        setBottomModeLandscapeMaxWidth(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixBottomSheetBehavior_Layout_bottomModeLandscapeMaxWidth, resources.getDimensionPixelSize(R.dimen.miuix_bottom_sheet_default_max_landscape_width)));
        setBottomModeLandscapeWidthRuleEnabled(typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixBottomSheetBehavior_Layout_bottomModeLandscapeMaxWidthRuleEnabled, false));
        typedArrayObtainStyledAttributes.recycle();
    }

    private static ColorStateList getColorStateList(Context context, TypedArray typedArray, int i) {
        int resourceId;
        ColorStateList colorStateList;
        return (!typedArray.hasValue(i) || (resourceId = typedArray.getResourceId(i, 0)) == 0 || (colorStateList = AppCompatResources.getColorStateList(context, resourceId)) == null) ? typedArray.getColorStateList(i) : colorStateList;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public Parcelable onSaveInstanceState(CoordinatorLayout coordinatorLayout, V v) {
        return new SavedState(super.onSaveInstanceState(coordinatorLayout, v), (BottomSheetBehavior<?>) this);
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public void onRestoreInstanceState(CoordinatorLayout coordinatorLayout, V v, Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(coordinatorLayout, v, savedState.getSuperState());
        restoreOptionalState(savedState);
        if (savedState.state == 1 || savedState.state == 2) {
            this.state = 4;
            this.lastStableState = 4;
        } else {
            int i = savedState.state;
            this.state = i;
            this.lastStableState = i;
        }
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public void onAttachedToLayoutParams(CoordinatorLayout.LayoutParams layoutParams) {
        super.onAttachedToLayoutParams(layoutParams);
        this.viewRef = null;
        this.viewDragHelper = null;
        this.parentViewRef = null;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public void onDetachedFromLayoutParams() {
        super.onDetachedFromLayoutParams();
        this.viewRef = null;
        this.viewDragHelper = null;
        this.parentViewRef = null;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onMeasureChild(CoordinatorLayout coordinatorLayout, V v, int i, int i2, int i3, int i4) {
        int i5;
        float f;
        int i6;
        boolean z = false;
        this.fullHeightMode = false;
        this.extraHeight = 0;
        int size = View.MeasureSpec.getSize(i3);
        int size2 = View.MeasureSpec.getSize(i);
        if (View.MeasureSpec.getMode(i3) != 1073741824) {
            Log.w(TAG, coordinatorLayout + " measure spec mode is not MeasureSpec.EXACTLY! Usually layout_height should be match_parent.");
        }
        Context context = coordinatorLayout.getContext();
        float f2 = context.getResources().getDisplayMetrics().density;
        if (f2 != this.density) {
            this.density = f2;
            updateSizeConfig(context, this.attrs);
        }
        int i7 = this.modeConfig;
        if (i7 == 0) {
            i5 = 1;
            if (supportFloatingMode(this.floatingModeDependsOnWindow, context, f2, size2, size)) {
                this.mode = 1;
            } else {
                this.mode = 0;
            }
        } else {
            i5 = 1;
            if (i7 == 1) {
                this.mode = 0;
            } else if (i7 == 2) {
                this.mode = 1;
            } else {
                throw new IllegalStateException("Unexpected mode config: " + this.modeConfig);
            }
        }
        boolean zIsPortrait = WindowUtils.isPortrait(v.getContext());
        if (this.mode == i5) {
            if (v instanceof BottomSheetView) {
                BottomSheetView bottomSheetView = (BottomSheetView) v;
                applyWindowInsets(bottomSheetView, true, false, true, true, true, true);
                bottomSheetView.setBottomSheetMode(this.mode);
                bottomSheetView.hideDragHandleView();
                bottomSheetView.setExtraHeightEnabled(false);
            }
            int i8 = this.floatingModeWidth;
            if (i8 >= size2) {
                WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(context);
                Log.w(TAG, "Width in the floating mode bigger than parent width, fix it to be same with parent width! finalFloatingModeWidth: " + i8 + ", parentSizeWidth: " + size2 + ", currentDensity: " + f + ", isPortrait: " + zIsPortrait + ", window width: " + windowInfo.windowSize.x + ", window height: " + windowInfo.windowSize.y);
            } else {
                size2 = i8;
            }
            if (zIsPortrait != 0) {
                f = f2;
                i6 = this.floatingModeHeight;
            } else {
                f = f2;
                if (this.floatingModeDependsOnWindow) {
                    i6 = (int) (this.floatingModeHeightRatio * EnvStateManager.getWindowInfo(context).windowSize.y);
                } else {
                    i6 = (int) (this.floatingModeHeightRatio * size);
                }
            }
            if (i6 >= size) {
                f = f2;
                f = f2;
                f = f2;
                WindowBaseInfo windowInfo2 = EnvStateManager.getWindowInfo(context);
                Log.w(TAG, "Height in the floating mode bigger than parent height, fix it to be " + this.floatingModeHeightRatio + " parent height! finalFloatingModeHeight: " + i6 + ", parentSizeHeight: " + size + ", currentDensity: " + f + ", isPortrait: " + zIsPortrait + ", window width: " + windowInfo2.windowSize.x + ", window height: " + windowInfo2.windowSize.y);
                i6 = (int) (this.floatingModeHeightRatio * size);
            }
            f = f2;
            f = f2;
            f = f2;
            v.measure(View.MeasureSpec.makeMeasureSpec(size2, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(i6, BasicMeasure.EXACTLY));
            return true;
        }
        boolean z2 = v instanceof BottomSheetView;
        if (z2) {
            BottomSheetView bottomSheetView2 = (BottomSheetView) v;
            applyWindowInsets(bottomSheetView2, false, true, true, true, true, this.paddingBottomSystemWindowInsets);
            bottomSheetView2.setBottomSheetMode(this.mode);
            if (bottomSheetView2.isDragHandleViewEnabled()) {
                bottomSheetView2.showDragHandleView();
            } else {
                bottomSheetView2.hideDragHandleView();
            }
            bottomSheetView2.setExtraHeightEnabled(true);
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        int childMeasureSpec = getChildMeasureSpec(i, coordinatorLayout.getPaddingLeft() + coordinatorLayout.getPaddingRight() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin + i2, this.maxWidth, marginLayoutParams.width);
        int childMeasureSpec2 = getChildMeasureSpec(i3, coordinatorLayout.getPaddingTop() + coordinatorLayout.getPaddingBottom() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin + i4, this.maxHeight, marginLayoutParams.height);
        v.measure(childMeasureSpec, childMeasureSpec2);
        if (z2) {
            this.extraHeight = ((BottomSheetView) v).getExtraHeight();
        }
        boolean z3 = this.fixedHeightRatioEnabled && size > this.fullHeightLowRatioThreshold;
        this.internalFixedHeightRatioEnabled = z3;
        if (z3) {
            childMeasureSpec2 = View.MeasureSpec.makeMeasureSpec((int) ((size * this.fixedHeightRatio) + this.extraHeight), BasicMeasure.EXACTLY);
            v.measure(childMeasureSpec, childMeasureSpec2);
        }
        int measuredHeight = v.getMeasuredHeight() - this.extraHeight;
        int measuredWidth = v.getMeasuredWidth();
        if (this.forceFullHeight || measuredHeight > size * getFullHeightRatio(size)) {
            this.fullHeightMode = true;
        }
        int i9 = this.bottomModeMaxWidth;
        if (this.bottomModeLandscapeWidthRuleEnabled && !zIsPortrait) {
            i9 = this.bottomModeLandscapeWidth;
        }
        if (measuredWidth > i9) {
            childMeasureSpec = View.MeasureSpec.makeMeasureSpec(i9, BasicMeasure.EXACTLY);
            z = true;
        }
        this.insetTopInMeasureStep = this.insetTop;
        if (this.mRequestLayoutRunnable == null) {
            this.mRequestLayoutRunnable = new RequestLayoutRunnable(v);
        }
        this.mRequestLayoutRunnable.mInsetTopInMeasuredStep = this.insetTopInMeasureStep;
        if (this.fullHeightMode) {
            v.measure(childMeasureSpec, View.MeasureSpec.makeMeasureSpec(getMaxHeight(size) + this.extraHeight, BasicMeasure.EXACTLY));
            return true;
        }
        int i10 = this.minHeight;
        if (measuredHeight <= i10) {
            v.measure(childMeasureSpec, View.MeasureSpec.makeMeasureSpec(i10 + this.extraHeight, BasicMeasure.EXACTLY));
            return true;
        }
        if (!z) {
            return true;
        }
        v.measure(childMeasureSpec, childMeasureSpec2);
        return true;
    }

    private boolean supportFloatingMode(boolean z, Context context, float f, int i, int i2) {
        if (z) {
            return supportFloatingMode(context);
        }
        return supportFloatingMode(f, i, i2);
    }

    private boolean supportFloatingMode(Context context) {
        boolean zIsIntentFromSettingsSplit = context instanceof Activity ? IntentUtils.isIntentFromSettingsSplit(((Activity) context).getIntent()) : false;
        if (!zIsIntentFromSettingsSplit && (context instanceof ContextWrapper)) {
            Context baseContext = ((ContextWrapper) context).getBaseContext();
            if (baseContext instanceof Activity) {
                zIsIntentFromSettingsSplit = IntentUtils.isIntentFromSettingsSplit(((Activity) baseContext).getIntent());
            }
        }
        if (zIsIntentFromSettingsSplit) {
            return false;
        }
        int i = this.mDeviceType;
        if (i == 3 || i == 5) {
            return FoldFloatingHelper.isFloatingModeSupport(context);
        }
        if (i == 2) {
            return PadFloatingHelper.isFloatingModeSupport(context);
        }
        return false;
    }

    private boolean supportFloatingMode(float f, int i, int i2) {
        return this.mDeviceType != 1 && MiuixUIUtils.px2dp(f, (float) i) > 670 && MiuixUIUtils.px2dp(f, (float) i2) > 660;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void applyWindowInsets(View view, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        if (view != 0) {
            if (view instanceof WindowInsetsController) {
                ((WindowInsetsController) view).applyWindowInsets(z, z2, z3, z4, z5, z6);
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    applyWindowInsets(viewGroup.getChildAt(i), z, z2, z3, z4, z5, z6);
                }
            }
        }
    }

    private float getFullHeightRatio(int i) {
        if (i <= this.fullHeightLowRatioThreshold) {
            return this.fullHeightLowRatio;
        }
        if (i <= this.fullHeightMiddleRatioThreshold) {
            return this.fullHeightMiddleRatio;
        }
        return this.fullHeightHighRatio;
    }

    private int getMaxHeight(int i) {
        int i2 = this.expandedOffset;
        return i2 == -1 ? i - calculateExpandedOffset(i, this.insetTop) : i - i2;
    }

    private int calculateExpandedOffset(int i, int i2) {
        int i3;
        if (i2 == -1) {
            return 0;
        }
        if (i >= this.highExpandedOffsetThreshold) {
            i3 = this.defaultHighExpandedOffset;
        } else {
            i3 = this.defaultExpandedOffset;
        }
        int i4 = this.insetTop;
        return i4 > i3 ? i4 : i3;
    }

    private int getChildMeasureSpec(int i, int i2, int i3, int i4) {
        int childMeasureSpec = ViewGroup.getChildMeasureSpec(i, i2, i4);
        if (i3 == -1) {
            return childMeasureSpec;
        }
        int mode = View.MeasureSpec.getMode(childMeasureSpec);
        int size = View.MeasureSpec.getSize(childMeasureSpec);
        if (mode == 1073741824) {
            return View.MeasureSpec.makeMeasureSpec(Math.min(size, i3), BasicMeasure.EXACTLY);
        }
        if (size != 0) {
            i3 = Math.min(size, i3);
        }
        return View.MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE);
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onLayoutChild(CoordinatorLayout coordinatorLayout, V v, int i) {
        int height;
        ExtraPaddingPolicy extraPaddingPolicy;
        int i2;
        if (ViewCompat.getFitsSystemWindows(coordinatorLayout) && !ViewCompat.getFitsSystemWindows(v)) {
            v.setFitsSystemWindows(true);
        }
        if (this.viewRef == null) {
            this.peekHeightMin = coordinatorLayout.getResources().getDimensionPixelSize(R.dimen.miuix_bottom_sheet_peek_height_min);
            setWindowInsetsListener(v);
            this.viewRef = new WeakReference<>(v);
            ColorStateList colorStateList = this.backgroundTint;
            if (colorStateList != null) {
                ViewCompat.setBackgroundTintList(v, colorStateList);
            }
            updateAccessibilityActions();
            if (ViewCompat.getImportantForAccessibility(v) == 0) {
                ViewCompat.setImportantForAccessibility(v, 1);
            }
        }
        if (this.parentViewRef == null) {
            this.parentViewRef = new WeakReference<>(coordinatorLayout);
        }
        if (this.viewDragHelper == null) {
            this.viewDragHelper = ViewDragHelper.create(coordinatorLayout, this.dragCallback);
        }
        int top = v.getTop();
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
        int extraPaddingDp = 0;
        if (coordinatorLayout.getMeasuredWidth() - (Math.max(layoutParams.leftMargin, layoutParams.rightMargin) * 2) > v.getMeasuredWidth()) {
            int measuredWidth = (coordinatorLayout.getMeasuredWidth() - v.getMeasuredWidth()) / 2;
            v.layout(measuredWidth, 0, v.getMeasuredWidth() + measuredWidth, v.getMeasuredHeight());
        } else {
            coordinatorLayout.onLayoutChild(v, i);
        }
        if ((this.parentWidth != coordinatorLayout.getWidth() || this.parentHeight != coordinatorLayout.getHeight()) && this.dismissed) {
            if (this.mode == 1) {
                height = (int) ((coordinatorLayout.getHeight() / 2.0f) + (v.getHeight() / 2.0f));
            } else {
                height = coordinatorLayout.getHeight();
            }
            v.setTranslationY(height);
        }
        this.parentWidth = coordinatorLayout.getWidth();
        this.parentHeight = coordinatorLayout.getHeight();
        if (this.mode == 1) {
            this.childHeight = v.getHeight();
        } else {
            this.childHeight = v.getHeight() - this.extraHeight;
        }
        int i3 = this.parentHeight;
        int i4 = i3 - this.childHeight;
        int i5 = this.insetTop;
        if (i4 < i5) {
            this.childHeight = i3 - i5;
        }
        if (this.mode == 1) {
            setInternalDraggable(false);
            this.fitToContentsOffset = Math.max(0, (this.parentHeight - this.childHeight) / 2);
            setStateInternal(fixStateInFloatingMode(this.state));
        } else {
            setInternalDraggable(true);
            this.fitToContentsOffset = Math.max(0, this.parentHeight - this.childHeight);
        }
        calculateHalfExpandedOffset();
        calculateCollapsedOffset();
        int i6 = this.state;
        if (i6 == 3) {
            ViewCompat.offsetTopAndBottom(v, getExpandedOffset());
            this.stableStateChildTop = v.getTop();
        } else if (i6 == 6) {
            ViewCompat.offsetTopAndBottom(v, this.halfExpandedOffset);
            this.stableStateChildTop = v.getTop();
        } else if (this.hideable && i6 == 5) {
            ViewCompat.offsetTopAndBottom(v, this.parentHeight);
        } else if (i6 == 4) {
            ViewCompat.offsetTopAndBottom(v, this.collapsedOffset);
            this.stableStateChildTop = v.getTop();
        } else if (i6 == 1 || i6 == 2) {
            ViewCompat.offsetTopAndBottom(v, top - v.getTop());
        }
        updateChildY(v);
        this.nestedScrollingChildRef = new WeakReference<>(findScrollingChild(v));
        for (int i7 = 0; i7 < this.callbacks.size(); i7++) {
            this.callbacks.get(i7).onLayout(v);
        }
        OnModeChangeListener onModeChangeListener = this.onModeChangeListener;
        if (onModeChangeListener != null && (i2 = this.mode) != this.lastMode) {
            onModeChangeListener.onModeChange(i2, v);
        }
        this.lastMode = this.mode;
        if (this.onExtraPaddingListener != null && this.extraPaddingPolicy == null) {
            ExtraPaddingPolicy extraPaddingPolicyCreateDefault = ExtraPaddingPolicy.Builder.createDefault(this.mDeviceType, ContainerToken.PADDING_BASE_DP, ContainerToken.PADDING_HORIZONTAL_COMMON);
            this.extraPaddingPolicy = extraPaddingPolicyCreateDefault;
            if (extraPaddingPolicyCreateDefault != null) {
                extraPaddingPolicyCreateDefault.setEnable(this.extraPaddingEnabled);
            }
        }
        if (this.onExtraPaddingListener != null && (extraPaddingPolicy = this.extraPaddingPolicy) != null) {
            extraPaddingPolicy.setEnable(this.extraPaddingEnabled);
            if (this.extraPaddingPolicy.isEnable()) {
                Context context = v.getContext();
                Point point = EnvStateManager.getWindowInfo(context).windowSizeDp;
                float f = context.getResources().getDisplayMetrics().density;
                this.extraPaddingPolicy.onContainerSizeChanged(point.x, point.y, v.getWidth(), v.getHeight(), f, this.mode == 1);
                extraPaddingDp = (int) (this.extraPaddingPolicy.getExtraPaddingDp() * f);
            }
            if (extraPaddingDp != this.horizontalExtraPadding) {
                this.horizontalExtraPadding = extraPaddingDp;
                this.onExtraPaddingListener.onExtraPaddingChanged(extraPaddingDp);
            }
        }
        return true;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onInterceptTouchEvent(CoordinatorLayout coordinatorLayout, V v, MotionEvent motionEvent) {
        int i;
        ViewDragHelper viewDragHelper;
        if (!v.isShown() || !isInternalDraggable()) {
            this.ignoreEvents = true;
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            reset();
        }
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        this.velocityTracker.addMovement(motionEvent);
        if (actionMasked == 0) {
            int x = (int) motionEvent.getX();
            this.initialY = (int) motionEvent.getY();
            if (this.state != 2) {
                WeakReference<View> weakReference = this.nestedScrollingChildRef;
                View view = weakReference != null ? weakReference.get() : null;
                if (view != null && coordinatorLayout.isPointInChildBounds(view, x, this.initialY)) {
                    this.activePointerId = motionEvent.getPointerId(motionEvent.getActionIndex());
                    this.touchingScrollingChild = true;
                }
            }
            this.ignoreEvents = this.activePointerId == -1 && !coordinatorLayout.isPointInChildBounds(v, x, this.initialY);
        } else if (actionMasked == 1 || actionMasked == 3) {
            this.touchingScrollingChild = false;
            this.activePointerId = -1;
            if (this.ignoreEvents) {
                this.ignoreEvents = false;
                return false;
            }
        }
        if (!this.ignoreEvents && (viewDragHelper = this.viewDragHelper) != null && viewDragHelper.shouldInterceptTouchEvent(motionEvent)) {
            return true;
        }
        WeakReference<View> weakReference2 = this.nestedScrollingChildRef;
        View view2 = weakReference2 != null ? weakReference2.get() : null;
        return (actionMasked != 2 || view2 == null || this.ignoreEvents || this.state == 1 || coordinatorLayout.isPointInChildBounds(view2, (int) motionEvent.getX(), (int) motionEvent.getY()) || this.viewDragHelper == null || (i = this.initialY) == -1 || Math.abs(((float) i) - motionEvent.getY()) <= ((float) this.viewDragHelper.getTouchSlop())) ? false : true;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onTouchEvent(CoordinatorLayout coordinatorLayout, V v, MotionEvent motionEvent) {
        if (!v.isShown()) {
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (this.state == 1 && actionMasked == 0) {
            return true;
        }
        if (shouldHandleDraggingWithHelper()) {
            this.viewDragHelper.processTouchEvent(motionEvent);
        }
        if (actionMasked == 0) {
            reset();
        }
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        this.velocityTracker.addMovement(motionEvent);
        if (shouldHandleDraggingWithHelper() && actionMasked == 2 && !this.ignoreEvents && Math.abs(this.initialY - motionEvent.getY()) > this.viewDragHelper.getTouchSlop()) {
            this.viewDragHelper.captureChildView(v, motionEvent.getPointerId(motionEvent.getActionIndex()));
        }
        return !this.ignoreEvents;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V v, View view, View view2, int i, int i2) {
        this.lastNestedScrollDy = 0;
        this.nestedScrolled = false;
        return (this.animRunning || (i & 2) == 0) ? false : true;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V v, View view, int i, int i2, int[] iArr, int i3) {
        if (i3 == 1) {
            return;
        }
        WeakReference<View> weakReference = this.nestedScrollingChildRef;
        View view2 = weakReference != null ? weakReference.get() : null;
        if (!isNestedScrollingCheckEnabled() || view == view2) {
            int top = v.getTop();
            int i4 = top - i2;
            if (i2 > 0) {
                if (i4 < getExpandedOffset()) {
                    int expandedOffset = top - getExpandedOffset();
                    iArr[1] = expandedOffset;
                    ViewCompat.offsetTopAndBottom(v, -expandedOffset);
                    setStateInternal(3);
                } else {
                    if (!isInternalDraggable()) {
                        return;
                    }
                    iArr[1] = i2;
                    ViewCompat.offsetTopAndBottom(v, -i2);
                    setStateInternal(1);
                }
            } else if (i2 < 0 && !view.canScrollVertically(-1)) {
                if (i4 <= this.collapsedOffset || canBeHiddenByDragging()) {
                    if (!isInternalDraggable()) {
                        return;
                    }
                    iArr[1] = i2;
                    ViewCompat.offsetTopAndBottom(v, -i2);
                    setStateInternal(1);
                } else {
                    int i5 = top - this.collapsedOffset;
                    iArr[1] = i5;
                    ViewCompat.offsetTopAndBottom(v, -i5);
                    setStateInternal(4);
                }
            }
            dispatchOnSlide(v.getTop());
            this.lastNestedScrollDy = i2;
            this.nestedScrolled = true;
        }
    }

    /* JADX WARN: Code duplicated, block: B:54:0x00b3  */
    /* JADX WARN: Code duplicated, block: B:57:0x00ca  */
    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V v, View view, int i) {
        WeakReference<View> weakReference;
        int i2 = 3;
        if (v.getTop() == getExpandedOffset()) {
            setStateInternal(3);
            return;
        }
        if (!isNestedScrollingCheckEnabled() || ((weakReference = this.nestedScrollingChildRef) != null && view == weakReference.get() && this.nestedScrolled)) {
            if (this.lastNestedScrollDy > 0) {
                if (!shouldSkipHalfExpanded() && v.getTop() > this.halfExpandedOffset) {
                    i2 = 6;
                }
            } else if (this.hideable && shouldHide(v, getYVelocity())) {
                i2 = (v.getTop() - this.stableStateChildTop > this.significantDistanceThreshold || getYVelocity() > ((float) this.significantVelocityThreshold)) ? 5 : this.lastStableState;
            } else if (this.lastNestedScrollDy == 0) {
                int top = v.getTop();
                if (shouldSkipHalfExpanded()) {
                    if (Math.abs(top - this.fitToContentsOffset) >= Math.abs(top - this.collapsedOffset)) {
                        i2 = 4;
                    }
                } else {
                    int i3 = this.halfExpandedOffset;
                    if (top < i3) {
                        if (top >= Math.abs(top - this.collapsedOffset)) {
                            if (shouldSkipHalfExpandedStateWhenDragging()) {
                                i2 = 4;
                            } else {
                                i2 = 6;
                            }
                        }
                    } else if (Math.abs(top - i3) < Math.abs(top - this.collapsedOffset)) {
                        i2 = 6;
                    } else {
                        i2 = 4;
                    }
                }
            } else {
                if (!shouldSkipHalfExpanded()) {
                    int top2 = v.getTop();
                    if (Math.abs(top2 - this.halfExpandedOffset) < Math.abs(top2 - this.collapsedOffset)) {
                        i2 = 6;
                    }
                }
                i2 = 4;
            }
            startSettling(v, i2, false);
            this.nestedScrolled = false;
        }
    }

    public void setSkipHalfExpanded(boolean z) {
        this.skipHalfExpanded = z;
    }

    public boolean shouldSkipHalfExpanded() {
        if (this.skipHalfExpanded) {
            return true;
        }
        return !this.fullHeightMode;
    }

    public void setImproveAnimPerformance(boolean z) {
        this.improveAnimPerformance = z;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V v, View view, float f, float f2) {
        WeakReference<View> weakReference;
        if (isNestedScrollingCheckEnabled() && (weakReference = this.nestedScrollingChildRef) != null && view == weakReference.get()) {
            return this.state != 3 || super.onNestedPreFling(coordinatorLayout, v, view, f, f2);
        }
        return false;
    }

    boolean startEnterAnimation(AnimationListener animationListener) {
        return startEnterAnimation(animationListener, false);
    }

    boolean startEnterAnimation(AnimationListener animationListener, boolean z) {
        WeakReference<CoordinatorLayout> weakReference;
        CoordinatorLayout coordinatorLayout;
        WeakReference<V> weakReference2;
        V v;
        if ((!this.animInterruptible && this.animRunning) || (weakReference = this.parentViewRef) == null || (coordinatorLayout = weakReference.get()) == null || (weakReference2 = this.viewRef) == null || (v = weakReference2.get()) == null) {
            return false;
        }
        if (this.mode == 0) {
            startBottomEnterAnim(animationListener, coordinatorLayout, v, z);
            return true;
        }
        startFloatingEnterAnim(animationListener, coordinatorLayout, v, z);
        return true;
    }

    private void startBottomEnterAnim(final AnimationListener animationListener, View view, final View view2, boolean z) {
        if (this.bottomEnterAnimConfig == null) {
            this.bottomEnterAnimConfig = new AnimConfig().setEase(FolmeEase.spring(0.9f, 0.38f));
        }
        TransitionListener transitionListener = this.bottomEnterAnimTransitionListener;
        if (transitionListener != null) {
            this.bottomEnterAnimConfig.removeListeners(transitionListener);
        }
        TransitionListener transitionListener2 = new TransitionListener() { // from class: miuix.bottomsheet.BottomSheetBehavior.1
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj) {
                BottomSheetBehavior.this.dismissed = false;
                BottomSheetBehavior.this.animRunning = true;
                View parentView = BottomSheetBehavior.this.getParentView();
                if (parentView != null) {
                    parentView.setVisibility(0);
                }
                AnimationListener animationListener2 = animationListener;
                if (animationListener2 != null) {
                    animationListener2.onAnimationStart();
                }
                View childView = BottomSheetBehavior.this.getChildView();
                if (BottomSheetBehavior.this.improveAnimPerformance && childView != null && childView.isHardwareAccelerated()) {
                    childView.setLayerType(2, null);
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onCancel(Object obj) {
                View view3;
                if (BottomSheetBehavior.this.improveAnimPerformance && (view3 = view2) != null && view3.isHardwareAccelerated()) {
                    view2.setLayerType(0, null);
                }
                BottomSheetBehavior.this.animRunning = false;
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                View childView = BottomSheetBehavior.this.getChildView();
                if (childView != null) {
                    Folme.clean(childView);
                }
                if (BottomSheetBehavior.this.improveAnimPerformance && childView != null && childView.isHardwareAccelerated()) {
                    childView.setLayerType(0, null);
                }
                AnimationListener animationListener2 = animationListener;
                if (animationListener2 != null) {
                    animationListener2.onAnimationEnd();
                }
                BottomSheetBehavior.this.animRunning = false;
            }
        };
        this.bottomEnterAnimTransitionListener = transitionListener2;
        this.bottomEnterAnimConfig.addListeners(transitionListener2);
        AnimState animStateAdd = new AnimState().add(ViewProperty.TRANSLATION_Y, 0.0d);
        if (z) {
            this.animRunning = true;
            if (animationListener != null) {
                animationListener.onAnimationStart();
            }
            view2.setTranslationY(0.0f);
            view.setTranslationY(0.0f);
            if (animationListener != null) {
                animationListener.onAnimationEnd();
            }
            this.animRunning = false;
            return;
        }
        if (!this.animInterruptible) {
            this.bottomEnterAnimStateStyle = Folme.useAt(view2).state().setTo(ViewProperty.TRANSLATION_Y, Integer.valueOf(view.getHeight())).to(animStateAdd, this.bottomEnterAnimConfig);
        } else {
            resetChildForFirstShow(view2, view.getHeight());
            this.bottomEnterAnimStateStyle = Folme.useAt(view2).state().to(animStateAdd, this.bottomEnterAnimConfig);
        }
    }

    private void resetChildForFirstShow(View view, int i) {
        if (this.shouldResetChildBeforeFirstShow && view.getTranslationY() == 0.0f) {
            view.setTranslationY(i);
            this.shouldResetChildBeforeFirstShow = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getParentView() {
        WeakReference<CoordinatorLayout> weakReference = this.parentViewRef;
        if (weakReference == null) {
            return null;
        }
        return weakReference.get();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getChildView() {
        WeakReference<V> weakReference = this.viewRef;
        if (weakReference == null) {
            return null;
        }
        return weakReference.get();
    }

    private void startFloatingEnterAnim(final AnimationListener animationListener, View view, View view2, boolean z) {
        updateChildY(view2);
        if (this.floatingEnterAnimConfig == null) {
            this.floatingEnterAnimConfig = new AnimConfig().setEase(FolmeEase.spring(0.88f, 0.38f));
        }
        TransitionListener transitionListener = this.floatingEnterAnimTransitionListener;
        if (transitionListener != null) {
            this.floatingEnterAnimConfig.removeListeners(transitionListener);
        }
        TransitionListener transitionListener2 = new TransitionListener() { // from class: miuix.bottomsheet.BottomSheetBehavior.2
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj) {
                BottomSheetBehavior.this.dismissed = false;
                BottomSheetBehavior.this.animRunning = true;
                View parentView = BottomSheetBehavior.this.getParentView();
                if (parentView != null) {
                    parentView.setVisibility(0);
                }
                AnimationListener animationListener2 = animationListener;
                if (animationListener2 != null) {
                    animationListener2.onAnimationStart();
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onCancel(Object obj) {
                BottomSheetBehavior.this.animRunning = false;
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                AnimationListener animationListener2 = animationListener;
                if (animationListener2 != null) {
                    animationListener2.onAnimationEnd();
                }
                View childView = BottomSheetBehavior.this.getChildView();
                if (childView != null) {
                    Folme.clean(childView);
                }
                BottomSheetBehavior.this.animRunning = false;
            }
        };
        this.floatingEnterAnimTransitionListener = transitionListener2;
        this.floatingEnterAnimConfig.addListeners(transitionListener2);
        int height = (int) ((view.getHeight() / 2.0f) + (view2.getHeight() / 2.0f));
        AnimState animStateAdd = new AnimState().add(ViewProperty.TRANSLATION_Y, 0.0d);
        if (z) {
            this.animRunning = true;
            if (animationListener != null) {
                animationListener.onAnimationStart();
            }
            view2.setTranslationY(0.0f);
            view.setTranslationY(0.0f);
            if (animationListener != null) {
                animationListener.onAnimationEnd();
            }
            this.animRunning = false;
            return;
        }
        if (this.animInterruptible) {
            resetChildForFirstShow(view2, height);
            this.floatingEnterAnimStateStyle = Folme.useAt(view2).state().to(animStateAdd, this.floatingEnterAnimConfig);
        } else {
            this.floatingEnterAnimStateStyle = Folme.useAt(view2).state().setTo(ViewProperty.TRANSLATION_Y, Integer.valueOf(height)).to(animStateAdd, this.floatingEnterAnimConfig);
        }
    }

    private void updateChildY(View view) {
        if (this.floatingModeDependsOnWindow) {
            this.childYInWindow = view.getTop();
        } else {
            int[] iArr = new int[2];
            view.getLocationInWindow(iArr);
            this.childYInWindow = iArr[1];
        }
        if (this.childYInWindow <= 0) {
            Log.w(TAG, view + "==》childYInWindow <= 0 ! It's probably a bad time to get the location.");
        }
    }

    boolean startExitAnimation(AnimationListener animationListener) {
        return startExitAnimation(animationListener, false);
    }

    boolean startExitAnimation(AnimationListener animationListener, boolean z) {
        WeakReference<CoordinatorLayout> weakReference;
        CoordinatorLayout coordinatorLayout;
        WeakReference<V> weakReference2;
        V v;
        if ((!this.animInterruptible && this.animRunning) || (weakReference = this.parentViewRef) == null || (coordinatorLayout = weakReference.get()) == null || (weakReference2 = this.viewRef) == null || (v = weakReference2.get()) == null) {
            return false;
        }
        if (this.mode == 0) {
            startBottomExitAnimation(animationListener, coordinatorLayout, v, z);
            return true;
        }
        startFloatingExitAnim(animationListener, coordinatorLayout, v, z);
        return true;
    }

    private void startBottomExitAnimation(final AnimationListener animationListener, View view, final View view2, boolean z) {
        if (this.bottomExitAnimConfig == null) {
            this.bottomExitAnimConfig = new AnimConfig().setEase(FolmeEase.spring(0.9f, 0.38f));
        }
        TransitionListener transitionListener = this.bottomExitAnimTransitionListener;
        if (transitionListener != null) {
            this.bottomExitAnimConfig.removeListeners(transitionListener);
        }
        TransitionListener transitionListener2 = new TransitionListener() { // from class: miuix.bottomsheet.BottomSheetBehavior.3
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj) {
                BottomSheetBehavior.this.animRunning = true;
                View parentView = BottomSheetBehavior.this.getParentView();
                if (parentView != null) {
                    parentView.setVisibility(0);
                }
                AnimationListener animationListener2 = animationListener;
                if (animationListener2 != null) {
                    animationListener2.onAnimationStart();
                }
                View childView = BottomSheetBehavior.this.getChildView();
                if (BottomSheetBehavior.this.improveAnimPerformance && childView != null && childView.isHardwareAccelerated()) {
                    childView.setLayerType(2, null);
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                if (BottomSheetBehavior.this.bottomExitAnimStateStyle == null || !BottomSheetBehavior.this.shouldBottomExitAnimEnd()) {
                    return;
                }
                BottomSheetBehavior.this.bottomExitAnimStateStyle.end();
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onCancel(Object obj) {
                View view3;
                if (BottomSheetBehavior.this.improveAnimPerformance && (view3 = view2) != null && view3.isHardwareAccelerated()) {
                    view2.setLayerType(0, null);
                }
                BottomSheetBehavior.this.animRunning = false;
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                View childView = BottomSheetBehavior.this.getChildView();
                if (childView != null) {
                    Folme.clean(childView);
                }
                if (BottomSheetBehavior.this.improveAnimPerformance && childView != null && childView.isHardwareAccelerated()) {
                    childView.setLayerType(0, null);
                }
                AnimationListener animationListener2 = animationListener;
                if (animationListener2 != null) {
                    animationListener2.onAnimationEnd();
                }
                BottomSheetBehavior.this.animRunning = false;
                BottomSheetBehavior.this.dismissed = true;
            }
        };
        this.bottomExitAnimTransitionListener = transitionListener2;
        this.bottomExitAnimConfig.addListeners(transitionListener2);
        AnimState animStateAdd = new AnimState().add(ViewProperty.TRANSLATION_Y, view.getHeight());
        if (z) {
            this.animRunning = true;
            if (animationListener != null) {
                animationListener.onAnimationStart();
            }
            if (view2 != null) {
                view2.setTranslationY(view.getHeight());
            }
            if (animationListener != null) {
                animationListener.onAnimationEnd();
            }
            this.animRunning = false;
            return;
        }
        if (this.animInterruptible) {
            IStateStyle iStateStyleState = Folme.useAt(view2).state();
            this.bottomExitAnimStateStyle = iStateStyleState;
            iStateStyleState.to(animStateAdd, this.bottomExitAnimConfig);
        } else {
            IStateStyle iStateStyleState2 = Folme.useAt(view2).state();
            this.bottomExitAnimStateStyle = iStateStyleState2;
            iStateStyleState2.setTo(ViewProperty.TRANSLATION_Y, 0).to(animStateAdd, this.bottomExitAnimConfig);
        }
    }

    private void startFloatingExitAnim(final AnimationListener animationListener, View view, View view2, boolean z) {
        if (this.floatingExitAnimConfig == null) {
            this.floatingExitAnimConfig = new AnimConfig().setEase(FolmeEase.spring(0.95f, 0.3f));
        }
        TransitionListener transitionListener = this.floatingExitAnimTransitionListener;
        if (transitionListener != null) {
            this.floatingExitAnimConfig.removeListeners(transitionListener);
        }
        TransitionListener transitionListener2 = new TransitionListener() { // from class: miuix.bottomsheet.BottomSheetBehavior.4
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj) {
                BottomSheetBehavior.this.animRunning = true;
                View parentView = BottomSheetBehavior.this.getParentView();
                if (parentView != null) {
                    parentView.setVisibility(0);
                }
                AnimationListener animationListener2 = animationListener;
                if (animationListener2 != null) {
                    animationListener2.onAnimationStart();
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                if (BottomSheetBehavior.this.floatingExitAnimStateStyle == null || !BottomSheetBehavior.this.shouldFloatingExitAnimEnd()) {
                    return;
                }
                BottomSheetBehavior.this.floatingExitAnimStateStyle.end();
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onCancel(Object obj) {
                BottomSheetBehavior.this.animRunning = false;
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                View childView = BottomSheetBehavior.this.getChildView();
                if (childView != null) {
                    Folme.clean(childView);
                }
                AnimationListener animationListener2 = animationListener;
                if (animationListener2 != null) {
                    animationListener2.onAnimationEnd();
                }
                BottomSheetBehavior.this.animRunning = false;
                BottomSheetBehavior.this.dismissed = true;
            }
        };
        this.floatingExitAnimTransitionListener = transitionListener2;
        this.floatingExitAnimConfig.addListeners(transitionListener2);
        int height = (int) ((view.getHeight() / 2.0f) + (view2.getHeight() / 2.0f));
        AnimState animStateAdd = new AnimState().add(ViewProperty.TRANSLATION_Y, height);
        if (z) {
            this.animRunning = true;
            if (animationListener != null) {
                animationListener.onAnimationStart();
            }
            view2.setTranslationY(height);
            if (animationListener != null) {
                animationListener.onAnimationEnd();
            }
            this.animRunning = false;
            return;
        }
        if (this.animInterruptible) {
            IStateStyle iStateStyleState = Folme.useAt(view2).state();
            this.floatingExitAnimStateStyle = iStateStyleState;
            iStateStyleState.to(animStateAdd, this.floatingExitAnimConfig);
        } else {
            IStateStyle iStateStyleState2 = Folme.useAt(view2).state();
            this.floatingExitAnimStateStyle = iStateStyleState2;
            iStateStyleState2.setTo(ViewProperty.TRANSLATION_Y, 0).to(animStateAdd, this.floatingExitAnimConfig);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldFloatingExitAnimEnd() {
        View childView = getChildView();
        return childView == null || ((float) childView.getTop()) + childView.getTranslationY() > ((float) (this.parentHeight + (-10)));
    }

    public void release() {
        releaseBottomAnimations();
        releaseFloatingAnimations();
        releaseReleaseAnimations();
        folmeClean();
    }

    private void releaseBottomAnimations() {
        IStateStyle iStateStyle = this.bottomEnterAnimStateStyle;
        if (iStateStyle != null) {
            iStateStyle.cancel();
            this.bottomEnterAnimStateStyle = null;
        }
        IStateStyle iStateStyle2 = this.bottomExitAnimStateStyle;
        if (iStateStyle2 != null) {
            iStateStyle2.cancel();
            this.bottomExitAnimStateStyle = null;
        }
        AnimConfig animConfig = this.bottomEnterAnimConfig;
        if (animConfig != null) {
            TransitionListener transitionListener = this.bottomEnterAnimTransitionListener;
            if (transitionListener != null) {
                animConfig.removeListeners(transitionListener);
            }
            this.bottomEnterAnimConfig = null;
            this.bottomEnterAnimTransitionListener = null;
        }
        AnimConfig animConfig2 = this.bottomExitAnimConfig;
        if (animConfig2 != null) {
            TransitionListener transitionListener2 = this.bottomExitAnimTransitionListener;
            if (transitionListener2 != null) {
                animConfig2.removeListeners(transitionListener2);
            }
            this.bottomExitAnimConfig = null;
            this.bottomExitAnimTransitionListener = null;
        }
    }

    private void releaseFloatingAnimations() {
        IStateStyle iStateStyle = this.floatingEnterAnimStateStyle;
        if (iStateStyle != null) {
            iStateStyle.cancel();
            this.floatingEnterAnimStateStyle = null;
        }
        IStateStyle iStateStyle2 = this.floatingExitAnimStateStyle;
        if (iStateStyle2 != null) {
            iStateStyle2.cancel();
            this.floatingExitAnimStateStyle = null;
        }
        AnimConfig animConfig = this.floatingEnterAnimConfig;
        if (animConfig != null) {
            TransitionListener transitionListener = this.floatingEnterAnimTransitionListener;
            if (transitionListener != null) {
                animConfig.removeListeners(transitionListener);
            }
            this.floatingEnterAnimConfig = null;
            this.floatingEnterAnimTransitionListener = null;
        }
        AnimConfig animConfig2 = this.floatingExitAnimConfig;
        if (animConfig2 != null) {
            TransitionListener transitionListener2 = this.floatingExitAnimTransitionListener;
            if (transitionListener2 != null) {
                animConfig2.removeListeners(transitionListener2);
            }
            this.floatingExitAnimConfig = null;
            this.floatingExitAnimTransitionListener = null;
        }
    }

    private void folmeClean() {
        View childView = getChildView();
        if (childView != null) {
            Folme.clean(childView);
        }
    }

    private void releaseReleaseAnimations() {
        IStateStyle iStateStyle = this.releaseAnimStateStyle;
        if (iStateStyle != null) {
            iStateStyle.cancel();
            this.releaseAnimStateStyle = null;
        }
        AnimConfig animConfig = this.releaseAnimConfig;
        if (animConfig != null) {
            TransitionListener transitionListener = this.releaseAnimTransitionListener;
            if (transitionListener != null) {
                animConfig.removeListeners(transitionListener);
            }
            this.releaseAnimConfig = null;
            this.releaseAnimTransitionListener = null;
        }
    }

    public boolean isForceFullHeight() {
        return this.forceFullHeight;
    }

    public void setForceFullHeight(boolean z) {
        this.forceFullHeight = z;
    }

    public void setFixedHeightRatioEnabled(boolean z) {
        this.fixedHeightRatioEnabled = z;
    }

    public boolean isFixedHeightRatioEnabled() {
        return this.fixedHeightRatioEnabled;
    }

    public void setMaxWidth(int i) {
        this.maxWidth = i;
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public void setMaxHeight(int i) {
        this.maxHeight = i;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public void setPeekHeight(int i) {
        setPeekHeight(i, false);
    }

    public final void setPeekHeight(int i, boolean z) {
        if (i == -1) {
            if (this.peekHeightAuto) {
                return;
            } else {
                this.peekHeightAuto = true;
            }
        } else {
            if (!this.peekHeightAuto && this.peekHeight == i) {
                return;
            }
            this.peekHeightAuto = false;
            this.peekHeight = Math.max(0, i);
        }
        updatePeekHeight(z);
    }

    private void updatePeekHeight(boolean z) {
        V v;
        if (this.viewRef != null) {
            calculateCollapsedOffset();
            if (this.state != 4 || (v = this.viewRef.get()) == null) {
                return;
            }
            if (z) {
                setState(4);
            } else {
                v.requestLayout();
            }
        }
    }

    public int getPeekHeight() {
        if (this.peekHeightAuto) {
            return -1;
        }
        return this.peekHeight;
    }

    public void setHalfExpandedRatio(float f) {
        if (f <= 0.0f || f >= 1.0f) {
            throw new IllegalArgumentException("ratio must be a float value between 0 and 1");
        }
        this.halfExpandedRatio = f;
        if (this.viewRef != null) {
            calculateHalfExpandedOffset();
        }
    }

    public float getHalfExpandedRatio() {
        return this.halfExpandedRatio;
    }

    public void setExpandedOffset(int i) {
        if (i < 0 && i != -1) {
            throw new IllegalArgumentException("offset must be greater than or equal to 0");
        }
        this.expandedOffset = i;
    }

    public int getExpandedOffset() {
        return this.fitToContentsOffset;
    }

    public float calculateSlideOffset() {
        WeakReference<V> weakReference = this.viewRef;
        if (weakReference == null || weakReference.get() == null) {
            return -1.0f;
        }
        return calculateSlideOffsetWithTop(this.viewRef.get().getTop());
    }

    public void setModeConfig(int i) {
        if (this.modeConfig != i) {
            this.modeConfig = i;
        }
    }

    public void setHideable(boolean z) {
        if (this.hideable != z) {
            this.hideable = z;
            if (!z && this.state == 5) {
                setState(4);
            }
            updateAccessibilityActions();
        }
    }

    public boolean isHideable() {
        return this.hideable;
    }

    public void setSkipCollapsed(boolean z) {
        this.skipCollapsed = z;
    }

    public boolean getSkipCollapsed() {
        return this.skipCollapsed;
    }

    public void setDraggable(boolean z) {
        this.draggable = z;
    }

    private void setInternalDraggable(boolean z) {
        this.internalDraggable = z;
    }

    private boolean isInternalDraggable() {
        return this.draggable && this.internalDraggable;
    }

    public boolean isDraggable() {
        return this.draggable;
    }

    public void setSignificantVelocityThreshold(int i) {
        this.significantVelocityThreshold = i;
    }

    public int getSignificantVelocityThreshold() {
        return this.significantVelocityThreshold;
    }

    public void setSignificantDistanceThreshold(int i) {
        this.significantDistanceThreshold = i;
    }

    public int getSignificantDistanceThreshold() {
        return this.significantDistanceThreshold;
    }

    public void setBottomModeMaxWidth(int i) {
        this.bottomModeMaxWidth = i;
    }

    public int getBottomModeMaxWidth() {
        return this.bottomModeMaxWidth;
    }

    public void setBottomModeLandscapeMaxWidth(int i) {
        this.bottomModeLandscapeWidth = i;
    }

    public int getBottomModeLandscapeMaxWidth() {
        return this.bottomModeLandscapeWidth;
    }

    public void setBottomModeLandscapeWidthRuleEnabled(boolean z) {
        this.bottomModeLandscapeWidthRuleEnabled = z;
    }

    public boolean isBottomModeLandscapeWidthRuleEnabled() {
        return this.bottomModeLandscapeWidthRuleEnabled;
    }

    public void setFloatingModeDependsOnWindow(boolean z) {
        this.floatingModeDependsOnWindow = z;
    }

    public boolean isFloatingModeDependsOnWindow() {
        return this.floatingModeDependsOnWindow;
    }

    public void setOnModeChangeListener(OnModeChangeListener onModeChangeListener) {
        this.onModeChangeListener = onModeChangeListener;
    }

    void setAnimationInterruptible(boolean z) {
        this.animInterruptible = z;
    }

    boolean isAnimationInterruptible() {
        return this.animInterruptible;
    }

    public void setExtraPaddingPolicy(ExtraPaddingPolicy extraPaddingPolicy) {
        this.extraPaddingPolicy = extraPaddingPolicy;
    }

    public void setOnExtraPaddingListener(OnExtraPaddingListener onExtraPaddingListener) {
        this.onExtraPaddingListener = onExtraPaddingListener;
    }

    public void setExtraPaddingEnabled(boolean z) {
        this.extraPaddingEnabled = z;
        ExtraPaddingPolicy extraPaddingPolicy = this.extraPaddingPolicy;
        if (extraPaddingPolicy != null) {
            extraPaddingPolicy.setEnable(z);
        }
    }

    public void setSaveFlags(int i) {
        this.saveFlags = i;
    }

    public int getSaveFlags() {
        return this.saveFlags;
    }

    public void setHideFriction(float f) {
        this.hideFriction = f;
    }

    public float getHideFriction() {
        return this.hideFriction;
    }

    @Deprecated
    public void setBottomSheetCallback(BottomSheetCallback bottomSheetCallback) {
        Log.w(TAG, "BottomSheetBehavior now supports multiple callbacks. `setBottomSheetCallback()` removes all existing callbacks, including ones set internally by library authors, which may result in unintended behavior. This may change in the future. Please use `addBottomSheetCallback()` and `removeBottomSheetCallback()` instead to set your own callbacks.");
        this.callbacks.clear();
        if (bottomSheetCallback != null) {
            this.callbacks.add(bottomSheetCallback);
        }
    }

    public void addBottomSheetCallback(BottomSheetCallback bottomSheetCallback) {
        if (this.callbacks.contains(bottomSheetCallback)) {
            return;
        }
        this.callbacks.add(bottomSheetCallback);
    }

    public void removeBottomSheetCallback(BottomSheetCallback bottomSheetCallback) {
        this.callbacks.remove(bottomSheetCallback);
    }

    public void setState(int i) {
        if (i == 1 || i == 2) {
            throw new IllegalArgumentException("STATE_" + (i == 1 ? "DRAGGING" : "SETTLING") + " should not be set externally.");
        }
        if (!this.hideable && i == 5) {
            Log.w(TAG, "Cannot set state: " + i);
            return;
        }
        int iFixStateInFloatingMode = fixStateInFloatingMode(i);
        final int i2 = (iFixStateInFloatingMode == 6 && shouldSkipHalfExpanded() && getTopOffsetForState(iFixStateInFloatingMode) <= this.fitToContentsOffset) ? 3 : iFixStateInFloatingMode;
        WeakReference<V> weakReference = this.viewRef;
        if (weakReference == null || weakReference.get() == null) {
            setStateInternal(iFixStateInFloatingMode);
            return;
        }
        final V v = this.viewRef.get();
        if (v.isLaidOut()) {
            runAfterLayout(v, new Runnable() { // from class: miuix.bottomsheet.BottomSheetBehavior$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1847lambda$setState$0$miuixbottomsheetBottomSheetBehavior(v, i2);
                }
            });
        } else {
            setStateInternal(iFixStateInFloatingMode);
        }
    }

    /* JADX INFO: renamed from: lambda$setState$0$miuix-bottomsheet-BottomSheetBehavior, reason: not valid java name */
    /* synthetic */ void m1847lambda$setState$0$miuixbottomsheetBottomSheetBehavior(View view, int i) {
        startSettling(view, i, false);
    }

    private int fixStateInFloatingMode(int i) {
        if (this.mode != 1) {
            return i;
        }
        if (i == 4 || i == 6) {
            return 3;
        }
        return i;
    }

    private void runAfterLayout(V v, Runnable runnable) {
        if (isLayouting(v)) {
            v.post(runnable);
        } else {
            runnable.run();
        }
    }

    private boolean isLayouting(V v) {
        ViewParent parent = v.getParent();
        return parent != null && parent.isLayoutRequested() && ViewCompat.isAttachedToWindow(v);
    }

    public void setGestureInsetBottomIgnored(boolean z) {
        this.gestureInsetBottomIgnored = z;
    }

    public boolean isGestureInsetBottomIgnored() {
        return this.gestureInsetBottomIgnored;
    }

    public int getState() {
        return this.state;
    }

    void setStateInternal(int i) {
        V v;
        if (this.state == i) {
            return;
        }
        this.state = i;
        if (i == 4 || i == 3 || i == 6 || (this.hideable && i == 5)) {
            this.lastStableState = i;
        }
        WeakReference<V> weakReference = this.viewRef;
        if (weakReference == null || (v = weakReference.get()) == null) {
            return;
        }
        if (i == 3) {
            updateImportantForAccessibility(true);
        } else if (i == 6 || i == 5 || i == 4) {
            updateImportantForAccessibility(false);
        }
        for (int i2 = 0; i2 < this.callbacks.size(); i2++) {
            this.callbacks.get(i2).onStateChanged(v, i);
        }
        updateAccessibilityActions();
    }

    private String getStateDescription(int i) {
        if (i == 1) {
            return "STATE_DRAGGING";
        }
        if (i == 2) {
            return "STATE_SETTLING";
        }
        if (i == 3) {
            return "STATE_EXPANDED";
        }
        if (i == 4) {
            return "STATE_COLLAPSED";
        }
        if (i == 5) {
            return "STATE_HIDDEN";
        }
        if (i == 6) {
            return "STATE_HALF_EXPANDED";
        }
        return "Unknown State";
    }

    private int calculatePeekHeight() {
        int i;
        if (this.peekHeightAuto) {
            return Math.min(Math.max(this.peekHeightMin, this.parentHeight - ((this.parentWidth * 9) / 16)), this.childHeight) + this.insetBottom;
        }
        if (!this.gestureInsetBottomIgnored && !this.paddingBottomSystemWindowInsets && (i = this.gestureInsetBottom) > 0) {
            return Math.max(this.peekHeight, i + this.peekHeightGestureInsetBuffer);
        }
        return this.peekHeight + this.insetBottom;
    }

    private void calculateCollapsedOffset() {
        this.collapsedOffset = Math.max(this.parentHeight - calculatePeekHeight(), this.fitToContentsOffset);
    }

    private void calculateHalfExpandedOffset() {
        float f;
        if (this.internalFixedHeightRatioEnabled) {
            f = this.halfExpandedRatioWhenFixHeightRatio;
        } else {
            f = this.halfExpandedRatio;
        }
        this.halfExpandedOffset = (int) (this.parentHeight * (1.0f - f));
    }

    private float calculateSlideOffsetWithTop(int i) {
        float f;
        float expandedOffset;
        int i2 = this.collapsedOffset;
        if (i > i2 || i2 == getExpandedOffset()) {
            int i3 = this.collapsedOffset;
            f = i3 - i;
            expandedOffset = this.parentHeight - i3;
        } else {
            int i4 = this.collapsedOffset;
            f = i4 - i;
            expandedOffset = i4 - getExpandedOffset();
        }
        return f / expandedOffset;
    }

    private void reset() {
        this.activePointerId = -1;
        this.initialY = -1;
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.velocityTracker = null;
        }
    }

    private void restoreOptionalState(SavedState savedState) {
        int i = this.saveFlags;
        if (i == 0) {
            return;
        }
        if (i == -1 || (i & 1) == 1) {
            this.peekHeight = savedState.peekHeight;
        }
        int i2 = this.saveFlags;
        if (i2 == -1 || (i2 & 4) == 4) {
            this.hideable = savedState.hideable;
        }
        int i3 = this.saveFlags;
        if (i3 == -1 || (i3 & 8) == 8) {
            this.skipCollapsed = savedState.skipCollapsed;
        }
    }

    boolean shouldHide(View view, float f) {
        if (this.skipCollapsed) {
            return true;
        }
        if (isHideableWhenDragging() && view.getTop() >= this.collapsedOffset) {
            return Math.abs((((float) view.getTop()) + (f * this.hideFriction)) - ((float) this.collapsedOffset)) / ((float) calculatePeekHeight()) > 0.5f;
        }
        return false;
    }

    View findScrollingChild(View view) {
        if (view.getVisibility() != 0) {
            return null;
        }
        if (ViewCompat.isNestedScrollingEnabled(view)) {
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View viewFindScrollingChild = findScrollingChild(viewGroup.getChildAt(i));
                if (viewFindScrollingChild != null) {
                    return viewFindScrollingChild;
                }
            }
        }
        return null;
    }

    private boolean shouldHandleDraggingWithHelper() {
        return this.viewDragHelper != null && (isInternalDraggable() || this.state == 1);
    }

    private void setWindowInsetsListener(final View view) {
        final boolean z = (Build.VERSION.SDK_INT < 29 || isGestureInsetBottomIgnored() || this.peekHeightAuto) ? false : true;
        if (this.paddingBottomSystemWindowInsets || this.paddingLeftSystemWindowInsets || this.paddingRightSystemWindowInsets || this.marginLeftSystemWindowInsets || this.marginRightSystemWindowInsets || z) {
            ViewUtils.doOnApplyWindowInsets(view, new ViewUtils.OnApplyWindowInsetsListener() { // from class: miuix.bottomsheet.BottomSheetBehavior$$ExternalSyntheticLambda0
                @Override // miuix.internal.util.ViewUtils.OnApplyWindowInsetsListener
                public final WindowInsetsCompat onApplyWindowInsets(View view2, WindowInsetsCompat windowInsetsCompat, ViewUtils.RelativePadding relativePadding) {
                    return this.f$0.m1848x83005830(view, z, view2, windowInsetsCompat, relativePadding);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$setWindowInsetsListener$1$miuix-bottomsheet-BottomSheetBehavior, reason: not valid java name */
    /* synthetic */ WindowInsetsCompat m1848x83005830(View view, boolean z, View view2, WindowInsetsCompat windowInsetsCompat, ViewUtils.RelativePadding relativePadding) {
        boolean z2;
        int i;
        WindowInsetsCompat rootWindowInsets;
        if (this.originalWindowInsetsEnabled && (rootWindowInsets = ViewCompat.getRootWindowInsets(view2)) != null) {
            windowInsetsCompat = rootWindowInsets;
        }
        boolean z3 = true;
        int iMax = 0;
        if (this.mode == 1) {
            if (this.paddingBottomSystemWindowInsets && (i = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.ime()).bottom) > 0) {
                iMax = Math.max(0, i - (EnvStateManager.getWindowInfo(view.getContext()).windowSize.y - (this.childYInWindow + view2.getHeight())));
            }
            view2.setPaddingRelative(relativePadding.start, relativePadding.top, relativePadding.end, relativePadding.bottom + iMax);
            return windowInsetsCompat;
        }
        Insets insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
        Insets insets2 = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.mandatorySystemGestures());
        int i2 = insets.top;
        this.insetTop = i2;
        if (i2 != this.insetTopInMeasureStep) {
            if (this.mRequestLayoutRunnable == null) {
                this.mRequestLayoutRunnable = new RequestLayoutRunnable(view);
            }
            view2.removeCallbacks(this.mRequestLayoutRunnable);
            this.mRequestLayoutRunnable.mInsetTop = this.insetTop;
            view2.post(this.mRequestLayoutRunnable);
        }
        boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(view2);
        int paddingBottom = view2.getPaddingBottom();
        int paddingLeft = view2.getPaddingLeft();
        int paddingRight = view2.getPaddingRight();
        if (this.paddingBottomSystemWindowInsets) {
            this.insetBottom = windowInsetsCompat.getSystemWindowInsetBottom();
            paddingBottom = relativePadding.bottom + this.insetBottom;
        }
        if (this.paddingLeftSystemWindowInsets) {
            paddingLeft = (zIsLayoutRtl ? relativePadding.end : relativePadding.start) + insets.left;
        }
        if (this.paddingRightSystemWindowInsets) {
            paddingRight = (zIsLayoutRtl ? relativePadding.start : relativePadding.end) + insets.right;
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view2.getLayoutParams();
        if (!this.marginLeftSystemWindowInsets || marginLayoutParams.leftMargin == insets.left) {
            z2 = false;
        } else {
            marginLayoutParams.leftMargin = insets.left;
            z2 = true;
        }
        if (!this.marginRightSystemWindowInsets || marginLayoutParams.rightMargin == insets.right) {
            z3 = z2;
        } else {
            marginLayoutParams.rightMargin = insets.right;
        }
        if (z3) {
            view2.setLayoutParams(marginLayoutParams);
        }
        view2.setPadding(paddingLeft, view2.getPaddingTop(), paddingRight, paddingBottom);
        if (z) {
            this.gestureInsetBottom = insets2.bottom;
        }
        if (this.paddingBottomSystemWindowInsets || z) {
            updatePeekHeight(false);
        }
        return windowInsetsCompat;
    }

    private static class RequestLayoutRunnable implements Runnable {
        private static final int UNDEFINED = Integer.MIN_VALUE;
        public int mInsetTop = Integer.MIN_VALUE;
        public int mInsetTopInMeasuredStep = Integer.MIN_VALUE;
        private final WeakReference<View> mViewRef;

        public RequestLayoutRunnable(View view) {
            this.mViewRef = new WeakReference<>(view);
        }

        @Override // java.lang.Runnable
        public void run() {
            int i = this.mInsetTop;
            if (i == Integer.MIN_VALUE || i == this.mInsetTopInMeasuredStep) {
                return;
            }
            this.mViewRef.get().requestLayout();
        }
    }

    private static class FoldFloatingHelper {
        private FoldFloatingHelper() {
        }

        public static boolean isFloatingModeSupport(Context context) {
            WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(context);
            if (EnvStateManager.getSmallestScreenWidthDp(context) < 600) {
                return false;
            }
            if (windowInfo.windowMode != 8195 && ScreenModeHelper.isInFreeFormMode(windowInfo.windowMode)) {
                return windowInfo.windowSizeDp.y >= 747 && windowInfo.windowSizeDp.x > 670;
            }
            return true;
        }
    }

    private static class PadFloatingHelper {
        private PadFloatingHelper() {
        }

        public static boolean isFloatingModeSupport(Context context) {
            WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(context);
            if (windowInfo.windowMode == 0 || windowInfo.windowMode == 8195 || windowInfo.windowMode == 4099) {
                return true;
            }
            return windowInfo.windowSizeDp.y >= 747 && windowInfo.windowSizeDp.x > 670;
        }
    }

    private float getYVelocity() {
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker == null) {
            return 0.0f;
        }
        velocityTracker.computeCurrentVelocity(1000, this.maximumVelocity);
        return this.velocityTracker.getYVelocity(this.activePointerId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSettling(View view, final int i, boolean z) {
        int topOffsetForState = getTopOffsetForState(i);
        this.releaseStartAnimState.add(FOLME_KEY, view.getTop());
        this.releaseEndAnimState.add(FOLME_KEY, topOffsetForState);
        if (this.viewDragHelper != null) {
            setStateInternal(2);
            if (this.releaseAnimConfig == null) {
                AnimConfig animConfig = new AnimConfig();
                this.releaseAnimConfig = animConfig;
                animConfig.setEase(FolmeEase.spring(0.85f, 0.4f));
            }
            TransitionListener transitionListener = this.releaseAnimTransitionListener;
            if (transitionListener != null) {
                this.releaseAnimConfig.removeListeners(transitionListener);
            }
            TransitionListener transitionListener2 = new TransitionListener() { // from class: miuix.bottomsheet.BottomSheetBehavior.5
                @Override // miuix.animation.listener.TransitionListener
                public void onBegin(Object obj) {
                    if (BottomSheetBehavior.this.releaseAnimListener != null) {
                        BottomSheetBehavior.this.releaseAnimListener.onStart(i);
                    }
                }

                @Override // miuix.animation.listener.TransitionListener
                public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                    UpdateInfo updateInfoFindByName = UpdateInfo.findByName(collection, BottomSheetBehavior.FOLME_KEY);
                    View childView = BottomSheetBehavior.this.getChildView();
                    if (updateInfoFindByName == null || childView == null) {
                        return;
                    }
                    childView.offsetTopAndBottom(updateInfoFindByName.getIntValue() - childView.getTop());
                    if (BottomSheetBehavior.this.releaseAnimStateStyle != null && i == 5 && BottomSheetBehavior.this.shouldBottomExitAnimEnd()) {
                        BottomSheetBehavior.this.releaseAnimStateStyle.end();
                    }
                }

                @Override // miuix.animation.listener.TransitionListener
                public void onComplete(Object obj) {
                    if (BottomSheetBehavior.this.state == 2) {
                        BottomSheetBehavior.this.setStateInternal(i);
                        if (BottomSheetBehavior.this.releaseAnimListener != null) {
                            BottomSheetBehavior.this.releaseAnimListener.onEnd(i);
                        }
                    }
                }
            };
            this.releaseAnimTransitionListener = transitionListener2;
            this.releaseAnimConfig.addListeners(transitionListener2);
            this.releaseAnimConfig.setFromSpeed(Math.min(10000.0f, Math.max(-10000.0f, getYVelocity())));
            IStateStyle iStateStyleUseValue = Folme.useValue(FOLME_TARGET_RELEASE);
            this.releaseAnimStateStyle = iStateStyleUseValue;
            if (iStateStyleUseValue != null) {
                iStateStyleUseValue.setFlags(1L).setTo(this.releaseStartAnimState).to(this.releaseEndAnimState, this.releaseAnimConfig);
                return;
            }
            return;
        }
        setStateInternal(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldBottomExitAnimEnd() {
        WeakReference<CoordinatorLayout> weakReference = this.parentViewRef;
        if (weakReference == null || this.viewRef == null) {
            return true;
        }
        CoordinatorLayout coordinatorLayout = weakReference.get();
        V v = this.viewRef.get();
        return coordinatorLayout == null || v == null || v.getTranslationY() + ((float) v.getTop()) > ((float) (coordinatorLayout.getHeight() + (-10)));
    }

    private int getTopOffsetForState(int i) {
        if (i == 3) {
            return getExpandedOffset();
        }
        if (i == 4) {
            return this.collapsedOffset;
        }
        if (i == 5) {
            return this.parentHeight;
        }
        if (i == 6) {
            return this.halfExpandedOffset;
        }
        throw new IllegalArgumentException("Invalid state to get top offset: " + i);
    }

    void dispatchOnSlide(int i) {
        V v = this.viewRef.get();
        if (v == null || this.callbacks.isEmpty()) {
            return;
        }
        float fCalculateSlideOffsetWithTop = calculateSlideOffsetWithTop(i);
        for (int i2 = 0; i2 < this.callbacks.size(); i2++) {
            this.callbacks.get(i2).onSlide(v, fCalculateSlideOffsetWithTop);
        }
    }

    int getPeekHeightMin() {
        return this.peekHeightMin;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean canBeHiddenByDragging() {
        return isHideable() && isHideableWhenDragging();
    }

    public void setHideableInternal(boolean z) {
        this.hideable = z;
    }

    void setOriginalWindowInsetsEnabled(boolean z) {
        this.originalWindowInsetsEnabled = z;
    }

    public int getLastStableState() {
        return this.lastStableState;
    }

    private class StateSettlingTracker {
        private final Runnable continueSettlingRunnable;
        private boolean isContinueSettlingRunnablePosted;
        private int targetState;

        private StateSettlingTracker() {
            this.continueSettlingRunnable = new Runnable() { // from class: miuix.bottomsheet.BottomSheetBehavior.StateSettlingTracker.1
                @Override // java.lang.Runnable
                public void run() {
                    StateSettlingTracker.this.isContinueSettlingRunnablePosted = false;
                    if (BottomSheetBehavior.this.viewDragHelper != null && BottomSheetBehavior.this.viewDragHelper.continueSettling(true)) {
                        StateSettlingTracker stateSettlingTracker = StateSettlingTracker.this;
                        stateSettlingTracker.continueSettlingToState(stateSettlingTracker.targetState);
                    } else if (BottomSheetBehavior.this.state == 2) {
                        BottomSheetBehavior.this.setStateInternal(StateSettlingTracker.this.targetState);
                    }
                }
            };
        }

        void continueSettlingToState(int i) {
            if (BottomSheetBehavior.this.viewRef == null || BottomSheetBehavior.this.viewRef.get() == null) {
                return;
            }
            this.targetState = i;
            if (this.isContinueSettlingRunnablePosted) {
                return;
            }
            ViewCompat.postOnAnimation(BottomSheetBehavior.this.viewRef.get(), this.continueSettlingRunnable);
            this.isContinueSettlingRunnablePosted = true;
        }
    }

    protected static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: miuix.bottomsheet.BottomSheetBehavior.SavedState.1
            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, (ClassLoader) null);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        boolean hideable;
        int peekHeight;
        boolean skipCollapsed;
        final int state;

        public SavedState(Parcel parcel) {
            this(parcel, (ClassLoader) null);
        }

        public SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.state = parcel.readInt();
            this.peekHeight = parcel.readInt();
            this.hideable = parcel.readInt() == 1;
            this.skipCollapsed = parcel.readInt() == 1;
        }

        public SavedState(Parcelable parcelable, BottomSheetBehavior<?> bottomSheetBehavior) {
            super(parcelable);
            this.state = bottomSheetBehavior.state;
            this.peekHeight = ((BottomSheetBehavior) bottomSheetBehavior).peekHeight;
            this.hideable = bottomSheetBehavior.hideable;
            this.skipCollapsed = ((BottomSheetBehavior) bottomSheetBehavior).skipCollapsed;
        }

        @Deprecated
        public SavedState(Parcelable parcelable, int i) {
            super(parcelable);
            this.state = i;
        }

        @Override // androidx.customview.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.state);
            parcel.writeInt(this.peekHeight);
            parcel.writeInt(this.hideable ? 1 : 0);
            parcel.writeInt(this.skipCollapsed ? 1 : 0);
        }
    }

    public static <V extends View> BottomSheetBehavior<V> from(V v) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        if (!(layoutParams instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) layoutParams).getBehavior();
        if (!(behavior instanceof BottomSheetBehavior)) {
            throw new IllegalArgumentException("The view is not associated with BottomSheetBehavior");
        }
        return (BottomSheetBehavior) behavior;
    }

    public void setUpdateImportantForAccessibilityOnSiblings(boolean z) {
        this.updateImportantForAccessibilityOnSiblings = z;
    }

    private void updateImportantForAccessibility(boolean z) {
        Map<View, Integer> map;
        WeakReference<V> weakReference = this.viewRef;
        if (weakReference == null) {
            return;
        }
        ViewParent parent = weakReference.get().getParent();
        if (parent instanceof CoordinatorLayout) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) parent;
            int childCount = coordinatorLayout.getChildCount();
            if (z) {
                if (this.importantForAccessibilityMap != null) {
                    return;
                } else {
                    this.importantForAccessibilityMap = new HashMap(childCount);
                }
            }
            for (int i = 0; i < childCount; i++) {
                View childAt = coordinatorLayout.getChildAt(i);
                if (childAt != this.viewRef.get()) {
                    if (z) {
                        this.importantForAccessibilityMap.put(childAt, Integer.valueOf(childAt.getImportantForAccessibility()));
                        if (this.updateImportantForAccessibilityOnSiblings) {
                            ViewCompat.setImportantForAccessibility(childAt, 4);
                        }
                    } else if (this.updateImportantForAccessibilityOnSiblings && (map = this.importantForAccessibilityMap) != null && map.containsKey(childAt)) {
                        ViewCompat.setImportantForAccessibility(childAt, this.importantForAccessibilityMap.get(childAt).intValue());
                    }
                }
            }
            if (!z) {
                this.importantForAccessibilityMap = null;
            } else if (this.updateImportantForAccessibilityOnSiblings) {
                this.viewRef.get().sendAccessibilityEvent(8);
            }
        }
    }

    void setAccessibilityDelegateView(View view) {
        WeakReference<View> weakReference;
        if (view == null && (weakReference = this.accessibilityDelegateViewRef) != null) {
            clearAccessibilityAction(weakReference.get(), 1);
            this.accessibilityDelegateViewRef = null;
        } else {
            this.accessibilityDelegateViewRef = new WeakReference<>(view);
            updateAccessibilityActions(view, 1);
        }
    }

    private void updateAccessibilityActions() {
        WeakReference<V> weakReference = this.viewRef;
        if (weakReference != null) {
            updateAccessibilityActions(weakReference.get(), 0);
        }
        WeakReference<View> weakReference2 = this.accessibilityDelegateViewRef;
        if (weakReference2 != null) {
            updateAccessibilityActions(weakReference2.get(), 1);
        }
    }

    private void updateAccessibilityActions(View view, int i) {
        if (view == null) {
            return;
        }
        clearAccessibilityAction(view, i);
        if (!shouldSkipHalfExpanded() && this.state != 6) {
            this.expandHalfwayActionIds.put(i, addAccessibilityActionForState(view, R.string.bottomsheet_action_expand_halfway, 6));
        }
        if (this.hideable && isHideableWhenDragging() && this.state != 5) {
            replaceAccessibilityActionForState(view, AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_DISMISS, 5);
        }
        int i2 = this.state;
        if (i2 == 3) {
            replaceAccessibilityActionForState(view, AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_COLLAPSE, shouldSkipHalfExpanded() ? 4 : 6);
            return;
        }
        if (i2 == 4) {
            replaceAccessibilityActionForState(view, AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_EXPAND, shouldSkipHalfExpanded() ? 3 : 6);
        } else {
            if (i2 != 6) {
                return;
            }
            replaceAccessibilityActionForState(view, AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_COLLAPSE, 4);
            replaceAccessibilityActionForState(view, AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_EXPAND, 3);
        }
    }

    private void clearAccessibilityAction(View view, int i) {
        if (view == null) {
            return;
        }
        ViewCompat.removeAccessibilityAction(view, 524288);
        ViewCompat.removeAccessibilityAction(view, 262144);
        ViewCompat.removeAccessibilityAction(view, 1048576);
        int i2 = this.expandHalfwayActionIds.get(i, -1);
        if (i2 != -1) {
            ViewCompat.removeAccessibilityAction(view, i2);
            this.expandHalfwayActionIds.delete(i);
        }
    }

    private void replaceAccessibilityActionForState(View view, AccessibilityNodeInfoCompat.AccessibilityActionCompat accessibilityActionCompat, int i) {
        ViewCompat.replaceAccessibilityAction(view, accessibilityActionCompat, null, createAccessibilityViewCommandForState(i));
    }

    private int addAccessibilityActionForState(View view, int i, int i2) {
        return ViewCompat.addAccessibilityAction(view, view.getResources().getString(i), createAccessibilityViewCommandForState(i2));
    }

    private AccessibilityViewCommand createAccessibilityViewCommandForState(final int i) {
        return new AccessibilityViewCommand() { // from class: miuix.bottomsheet.BottomSheetBehavior.7
            @Override // androidx.core.view.accessibility.AccessibilityViewCommand
            public boolean perform(View view, AccessibilityViewCommand.CommandArguments commandArguments) {
                BottomSheetBehavior.this.setState(i);
                return true;
            }
        };
    }

    void setReleaseAnimListener(ReleaseAnimListener releaseAnimListener) {
        this.releaseAnimListener = releaseAnimListener;
    }
}
