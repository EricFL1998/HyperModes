package miuix.springback.trigger;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import miuix.animation.utils.VelocityMonitor;
import miuix.core.view.ViewCompatOnScrollChangeListener;
import miuix.springback.R;
import miuix.springback.view.SpringBackLayout;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes3.dex */
public abstract class CustomTrigger extends BaseTrigger {
    private static final int ACTION_COMPLETE_HAPTIC_THRESHOLD = 5000;
    private static final float OFFSET_RESET_STATE = 0.25f;
    private static final float OFFSET_SIMPLE_VELOCITY_Y = 1000.0f;
    private static final String TAG = "CustomTrigger";
    protected final ActionComplete mActionComplete;
    private int mActionIndex;
    protected final ActionStart mActionStart;
    protected final ActionTriggered mActionTriggered;
    private BaseTrigger.IndeterminateAction.OnActionCompleteListener mCompleteListener;
    private RelativeLayout mContainer;
    protected Context mContext;
    private BaseTrigger.Action mCurrentAction;
    private TriggerState mCurrentState;
    private long mEnterTime;
    protected final Idle mIdle;
    private FrameLayout mIndicatorContainer;
    private boolean mIsExitISimpleAction;
    private boolean mIsExitIndeterminateAction;
    private boolean mIsExitIndeterminateUpAction;
    private boolean mIsShowContainer;
    private boolean mIsStartIndeterminate;
    private boolean mIsStartIndeterminateUp;
    protected int mLastScrollDistance;
    public SpringBackLayout mLayout;
    private View.OnLayoutChangeListener mLayoutChangeListener;
    protected LayoutInflater mLayoutInflater;
    private View mLoadingContainer;
    private OnIndeterminateActionDataListener mOnActionDataListener;
    private BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener mOnIndeterminateActionViewListener;
    private BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener mOnIndeterminateUpActionViewListener;
    private ViewCompatOnScrollChangeListener mOnScrollListener;
    private BaseTrigger.SimpleAction.OnSimpleActionViewListener mOnSimpleActionViewListener;
    private SpringBackLayout.OnSpringListener mOnSpringListener;
    private OnIndeterminateUpActionDataListener mOnUpActionDataListener;
    protected int mScrollDistance;
    private int mScrollState;
    private boolean mScrollerFinished;
    private View mSimpleActionView;
    protected final Tracking mTracking;
    private boolean mUpActionBegin;
    private BaseTrigger.IndeterminateUpAction.OnUpActionDataListener mUpActionDataListener;
    private View mUpContainer;
    private VelocityMonitor mVelocityMonitor;
    private float mVelocityY;
    protected final WaitForIndeterminate mWaitForIndeterminate;

    public interface OnIndeterminateActionDataListener {
        void onActionComplete(BaseTrigger.IndeterminateAction indeterminateAction);

        void onActionLoadCancel(BaseTrigger.IndeterminateAction indeterminateAction);

        void onActionLoadFail(BaseTrigger.IndeterminateAction indeterminateAction);

        void onActionNoData(BaseTrigger.IndeterminateAction indeterminateAction, int i);

        void onActionStart(BaseTrigger.IndeterminateAction indeterminateAction);
    }

    public interface OnIndeterminateUpActionDataListener {
        void onActionComplete(BaseTrigger.IndeterminateUpAction indeterminateUpAction);

        void onActionLoadCancel(BaseTrigger.IndeterminateUpAction indeterminateUpAction);

        void onActionLoadFail(BaseTrigger.IndeterminateUpAction indeterminateUpAction);

        void onActionNoData(BaseTrigger.IndeterminateUpAction indeterminateUpAction, int i);

        void onActionStart(BaseTrigger.IndeterminateUpAction indeterminateUpAction);
    }

    public abstract void onSpringBackLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    public abstract void onSpringBackScrolled(SpringBackLayout springBackLayout, int i, int i2, int i3);

    public CustomTrigger(Context context) {
        super(context);
        this.mVelocityY = 0.0f;
        this.mScrollerFinished = true;
        this.mUpActionBegin = false;
        this.mEnterTime = -1L;
        this.mActionIndex = -1;
        this.mIsShowContainer = true;
        this.mIsExitIndeterminateAction = false;
        this.mIsExitIndeterminateUpAction = false;
        this.mIsExitISimpleAction = false;
        this.mIsStartIndeterminate = false;
        this.mIsStartIndeterminateUp = false;
        this.mLayoutChangeListener = new View.OnLayoutChangeListener() { // from class: miuix.springback.trigger.CustomTrigger.1
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                int paddingTop;
                int paddingBottom;
                SpringBackLayout springBackLayout = (SpringBackLayout) view;
                int springScrollY = springBackLayout.getSpringScrollY();
                int i9 = -springScrollY;
                int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(), BasicMeasure.EXACTLY);
                int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(i9, 0);
                CustomTrigger.this.mContainer.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
                View indeterminateView = CustomTrigger.this.getIndeterminateView();
                View indeterminateUpView = CustomTrigger.this.getIndeterminateUpView();
                if (indeterminateView != null) {
                    indeterminateView.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
                }
                if (springBackLayout.springBackEnable()) {
                    CustomTrigger.this.mContainer.layout(0, -i9, view.getWidth(), 0);
                } else {
                    CustomTrigger.this.mContainer.layout(0, 0, view.getWidth(), i9);
                }
                if (CustomTrigger.this.mLayout.getTarget() != null) {
                    paddingTop = CustomTrigger.this.mLayout.getTarget().getPaddingTop();
                    paddingBottom = CustomTrigger.this.mLayout.getTarget().getPaddingBottom();
                } else {
                    paddingTop = 0;
                    paddingBottom = 0;
                }
                if (indeterminateUpView != null) {
                    indeterminateUpView.measure(iMakeMeasureSpec, View.MeasureSpec.makeMeasureSpec(springScrollY, 0));
                    indeterminateUpView.layout(0, CustomTrigger.this.mLayout.getHeight() - paddingBottom, view.getWidth(), (CustomTrigger.this.mLayout.getHeight() - paddingBottom) + springScrollY);
                }
                if (indeterminateView != null) {
                    if (springBackLayout.springBackEnable()) {
                        indeterminateView.layout(0, (-i9) + paddingTop, view.getWidth(), paddingTop);
                    } else {
                        indeterminateView.layout(0, paddingTop, view.getWidth(), i9 + paddingTop);
                    }
                }
                CustomTrigger.this.onSpringBackLayoutChange(view, i, i2, i3, i4, i5, i6, i7, i8);
            }
        };
        this.mOnSpringListener = new SpringBackLayout.OnSpringListener() { // from class: miuix.springback.trigger.CustomTrigger.2
            @Override // miuix.springback.view.SpringBackLayout.OnSpringListener
            public boolean onSpringBack() {
                return CustomTrigger.this.mCurrentState.handleSpringBack();
            }
        };
        this.mOnScrollListener = new ViewCompatOnScrollChangeListener() { // from class: miuix.springback.trigger.CustomTrigger.3
            @Override // miuix.core.view.ViewCompatOnScrollChangeListener
            public void onStateChanged(int i, int i2, boolean z) {
                CustomTrigger.this.mScrollState = i2;
                CustomTrigger.this.mScrollerFinished = z;
                CustomTrigger.this.mCurrentState.handleScrollStateChange(i, i2);
                if (CustomTrigger.this.mCurrentState != CustomTrigger.this.mIdle) {
                    if (CustomTrigger.this.isShowContainer()) {
                        CustomTrigger.this.mContainer.setVisibility(0);
                    } else {
                        CustomTrigger.this.mContainer.setVisibility(4);
                    }
                    View indeterminateUpView = CustomTrigger.this.getIndeterminateUpView();
                    if (!CustomTrigger.this.mIsStartIndeterminateUp || indeterminateUpView == null || indeterminateUpView.getVisibility() == 0) {
                        return;
                    }
                    indeterminateUpView.setVisibility(0);
                    return;
                }
                View indeterminateUpView2 = CustomTrigger.this.getIndeterminateUpView();
                if (CustomTrigger.this.mIsStartIndeterminateUp || indeterminateUpView2 == null || indeterminateUpView2.getVisibility() != 0) {
                    return;
                }
                indeterminateUpView2.setVisibility(8);
            }

            @Override // miuix.core.view.ViewCompatOnScrollChangeListener
            public void onScrollChange(View view, int i, int i2, int i3, int i4) {
                SpringBackLayout springBackLayout = (SpringBackLayout) view;
                int i5 = i2 - i4;
                int i6 = i - i3;
                int springScrollY = springBackLayout.getSpringScrollY();
                CustomTrigger customTrigger = CustomTrigger.this;
                customTrigger.mLastScrollDistance = customTrigger.mScrollDistance;
                CustomTrigger.this.mScrollDistance = -springScrollY;
                CustomTrigger.this.mVelocityMonitor.update(CustomTrigger.this.mScrollDistance);
                CustomTrigger customTrigger2 = CustomTrigger.this;
                customTrigger2.mVelocityY = customTrigger2.mVelocityMonitor.getVelocity(0);
                if (springBackLayout.springBackEnable()) {
                    CustomTrigger.this.mContainer.setTop(springScrollY);
                } else {
                    CustomTrigger.this.mContainer.setTop(0);
                }
                int paddingBottom = CustomTrigger.this.mLayout.getTarget() != null ? CustomTrigger.this.mLayout.getTarget().getPaddingBottom() : 0;
                if (CustomTrigger.this.mUpContainer != null && springScrollY >= 0) {
                    CustomTrigger.this.mUpContainer.layout(0, CustomTrigger.this.mLayout.getHeight() - paddingBottom, view.getWidth(), (CustomTrigger.this.mLayout.getHeight() - paddingBottom) + springScrollY);
                }
                if (CustomTrigger.this.mScrollDistance < 0 && CustomTrigger.this.mCurrentAction == CustomTrigger.this.getIndeterminateUpAction() && CustomTrigger.this.getIndeterminateUpAction() != null) {
                    CustomTrigger customTrigger3 = CustomTrigger.this;
                    float fActionRestartOffsetPoint = customTrigger3.actionRestartOffsetPoint(customTrigger3.mCurrentAction);
                    if (CustomTrigger.this.mScrollState == 1 && ((Math.abs(CustomTrigger.this.mLastScrollDistance) < fActionRestartOffsetPoint || Math.abs(CustomTrigger.this.mScrollDistance) < fActionRestartOffsetPoint) && CustomTrigger.this.mCurrentState == CustomTrigger.this.mActionComplete)) {
                        CustomTrigger customTrigger4 = CustomTrigger.this;
                        customTrigger4.transition(customTrigger4.mTracking);
                    }
                }
                if (CustomTrigger.this.mCurrentAction != null && (CustomTrigger.this.mCurrentAction instanceof BaseTrigger.IndeterminateAction)) {
                    CustomTrigger customTrigger5 = CustomTrigger.this;
                    float fActionRestartOffsetPoint2 = customTrigger5.actionRestartOffsetPoint(customTrigger5.mCurrentAction);
                    if (CustomTrigger.this.mScrollState == 1 && ((Math.abs(CustomTrigger.this.mLastScrollDistance) < fActionRestartOffsetPoint2 || Math.abs(CustomTrigger.this.mScrollDistance) < fActionRestartOffsetPoint2) && CustomTrigger.this.mCurrentState == CustomTrigger.this.mActionComplete)) {
                        CustomTrigger customTrigger6 = CustomTrigger.this;
                        customTrigger6.transition(customTrigger6.mTracking);
                    }
                    if (CustomTrigger.this.mScrollState == 1 && CustomTrigger.this.mCurrentState == CustomTrigger.this.mWaitForIndeterminate && Math.abs(CustomTrigger.this.mLastScrollDistance) > CustomTrigger.this.mCurrentAction.mEnterPoint) {
                        CustomTrigger customTrigger7 = CustomTrigger.this;
                        customTrigger7.transition(customTrigger7.mTracking);
                    }
                }
                CustomTrigger.this.mCurrentState.handleScrolled(i5, springScrollY);
                CustomTrigger customTrigger8 = CustomTrigger.this;
                customTrigger8.onSpringBackScrolled(springBackLayout, i6, i5, customTrigger8.mScrollDistance);
            }
        };
        this.mUpActionDataListener = new BaseTrigger.IndeterminateUpAction.OnUpActionDataListener() { // from class: miuix.springback.trigger.CustomTrigger.4
            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnUpActionDataListener
            public void onActionNoData(BaseTrigger.IndeterminateUpAction indeterminateUpAction, int i) {
                CustomTrigger.this.mIsStartIndeterminateUp = false;
                if (CustomTrigger.this.mCurrentState == CustomTrigger.this.mActionStart && CustomTrigger.this.mCurrentAction == indeterminateUpAction) {
                    if (CustomTrigger.this.mOnUpActionDataListener != null) {
                        CustomTrigger.this.mOnUpActionDataListener.onActionNoData(indeterminateUpAction, i);
                    }
                    if (CustomTrigger.this.mLayout.getSpringScrollY() != 0) {
                        CustomTrigger customTrigger = CustomTrigger.this;
                        customTrigger.transition(customTrigger.mActionComplete);
                        if (CustomTrigger.this.mScrollState == 0) {
                            CustomTrigger.this.mLayout.smoothScrollTo(0, 0);
                            return;
                        }
                        return;
                    }
                    CustomTrigger customTrigger2 = CustomTrigger.this;
                    customTrigger2.transition(customTrigger2.mIdle);
                }
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnUpActionDataListener
            public void onActionStart(BaseTrigger.IndeterminateUpAction indeterminateUpAction) {
                CustomTrigger.this.mIsStartIndeterminateUp = true;
                if (CustomTrigger.this.getIndeterminateUpAction() == null || CustomTrigger.this.getIndeterminateUpAction() != indeterminateUpAction) {
                    return;
                }
                CustomTrigger customTrigger = CustomTrigger.this;
                customTrigger.transition(customTrigger.mTracking);
                CustomTrigger customTrigger2 = CustomTrigger.this;
                customTrigger2.mCurrentAction = customTrigger2.getIndeterminateUpAction();
                View indeterminateUpView = CustomTrigger.this.getIndeterminateUpView();
                if (indeterminateUpView != null) {
                    indeterminateUpView.setVisibility(0);
                }
                if (CustomTrigger.this.mOnUpActionDataListener != null) {
                    CustomTrigger.this.mOnUpActionDataListener.onActionStart(indeterminateUpAction);
                }
                CustomTrigger.this.mLayout.smoothScrollTo(0, CustomTrigger.this.mCurrentAction.mTriggerPoint);
                if (indeterminateUpView != null) {
                    if (CustomTrigger.this.mLayout.springBackEnable()) {
                        indeterminateUpView.layout(0, CustomTrigger.this.mLayout.getHeight(), CustomTrigger.this.mLayout.getWidth(), CustomTrigger.this.mLayout.getHeight() + indeterminateUpView.getMeasuredHeight());
                    } else {
                        indeterminateUpView.layout(0, CustomTrigger.this.mLayout.getHeight() - indeterminateUpView.getMeasuredHeight(), CustomTrigger.this.mLayout.getWidth(), CustomTrigger.this.mLayout.getHeight());
                    }
                }
                CustomTrigger customTrigger3 = CustomTrigger.this;
                customTrigger3.transition(customTrigger3.mWaitForIndeterminate);
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnUpActionDataListener
            public void onActionComplete(BaseTrigger.IndeterminateUpAction indeterminateUpAction) {
                if (CustomTrigger.this.mCurrentState == CustomTrigger.this.mActionStart && CustomTrigger.this.mCurrentAction == indeterminateUpAction) {
                    if (CustomTrigger.this.mLayout.getSpringScrollY() != 0) {
                        CustomTrigger customTrigger = CustomTrigger.this;
                        customTrigger.transition(customTrigger.mActionComplete);
                        if (CustomTrigger.this.mOnUpActionDataListener != null) {
                            CustomTrigger.this.mOnUpActionDataListener.onActionComplete(indeterminateUpAction);
                        }
                        if (CustomTrigger.this.mScrollState == 0) {
                            CustomTrigger.this.mLayout.smoothScrollTo(0, 0);
                        }
                    } else {
                        CustomTrigger customTrigger2 = CustomTrigger.this;
                        customTrigger2.transition(customTrigger2.mIdle);
                    }
                    View indeterminateUpView = CustomTrigger.this.getIndeterminateUpView();
                    if (CustomTrigger.this.mScrollState == 0 && indeterminateUpView != null && indeterminateUpView.getVisibility() == 0) {
                        indeterminateUpView.setVisibility(8);
                    }
                }
                CustomTrigger.this.mIsStartIndeterminateUp = false;
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnUpActionDataListener
            public void onActionLoadFail(BaseTrigger.IndeterminateUpAction indeterminateUpAction) {
                CustomTrigger.this.mIsStartIndeterminateUp = false;
                if (CustomTrigger.this.mCurrentState == CustomTrigger.this.mActionStart && CustomTrigger.this.mCurrentAction == indeterminateUpAction) {
                    if (CustomTrigger.this.mOnUpActionDataListener != null) {
                        CustomTrigger.this.mOnUpActionDataListener.onActionLoadFail(indeterminateUpAction);
                    }
                    if (CustomTrigger.this.mLayout.getSpringScrollY() != 0) {
                        CustomTrigger customTrigger = CustomTrigger.this;
                        customTrigger.transition(customTrigger.mActionComplete);
                        if (CustomTrigger.this.mScrollState == 0) {
                            CustomTrigger.this.mLayout.smoothScrollTo(0, 0);
                            return;
                        }
                        return;
                    }
                    CustomTrigger customTrigger2 = CustomTrigger.this;
                    customTrigger2.transition(customTrigger2.mIdle);
                }
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnUpActionDataListener
            public void onUpdateTriggerTextIndex(BaseTrigger.IndeterminateUpAction indeterminateUpAction, int i, String str) {
                indeterminateUpAction.mTriggerTexts[i] = str;
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnUpActionDataListener
            public void onActionLoadCancel(BaseTrigger.IndeterminateUpAction indeterminateUpAction) {
                if (CustomTrigger.this.mCurrentState == CustomTrigger.this.mActionStart && CustomTrigger.this.mCurrentAction == indeterminateUpAction) {
                    View indeterminateUpView = CustomTrigger.this.getIndeterminateUpView();
                    if (CustomTrigger.this.mScrollState == 0 && indeterminateUpView != null && indeterminateUpView.getVisibility() == 0) {
                        indeterminateUpView.setVisibility(8);
                    }
                }
                CustomTrigger.this.mIsStartIndeterminateUp = false;
            }
        };
        this.mCompleteListener = new BaseTrigger.IndeterminateAction.OnActionCompleteListener() { // from class: miuix.springback.trigger.CustomTrigger.5
            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnActionCompleteListener
            public void onActionNoData(BaseTrigger.IndeterminateAction indeterminateAction, int i) {
                CustomTrigger.this.mIsStartIndeterminate = false;
                if (CustomTrigger.this.mCurrentState == CustomTrigger.this.mActionStart && CustomTrigger.this.mCurrentAction == indeterminateAction) {
                    if (CustomTrigger.this.mOnActionDataListener != null) {
                        CustomTrigger.this.mOnActionDataListener.onActionNoData(indeterminateAction, i);
                    }
                    if (CustomTrigger.this.mLayout.getSpringScrollY() != 0) {
                        CustomTrigger customTrigger = CustomTrigger.this;
                        customTrigger.transition(customTrigger.mActionComplete);
                        if (CustomTrigger.this.mScrollState == 0) {
                            CustomTrigger.this.mLayout.smoothScrollTo(0, 0);
                            return;
                        }
                        return;
                    }
                    CustomTrigger customTrigger2 = CustomTrigger.this;
                    customTrigger2.transition(customTrigger2.mIdle);
                }
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnActionCompleteListener
            public void onActionComplete(BaseTrigger.IndeterminateAction indeterminateAction) {
                if (CustomTrigger.this.mCurrentState == CustomTrigger.this.mActionStart && CustomTrigger.this.mCurrentAction == indeterminateAction) {
                    if (CustomTrigger.this.mLayout.getSpringScrollY() != 0) {
                        CustomTrigger customTrigger = CustomTrigger.this;
                        customTrigger.transition(customTrigger.mActionComplete);
                        if (CustomTrigger.this.mScrollState == 0 || CustomTrigger.this.mScrollState == 2) {
                            CustomTrigger.this.mLayout.smoothScrollTo(0, 0);
                        }
                    } else {
                        CustomTrigger customTrigger2 = CustomTrigger.this;
                        customTrigger2.transition(customTrigger2.mIdle);
                    }
                    if (CustomTrigger.this.mOnActionDataListener != null) {
                        CustomTrigger.this.mOnActionDataListener.onActionComplete(indeterminateAction);
                    }
                }
                if (!CustomTrigger.this.mIsStartIndeterminate && CustomTrigger.this.getActionIntervalTime() > 5000) {
                    HapticCompat.performHapticFeedback(CustomTrigger.this.mLayout, HapticFeedbackConstants.MIUI_BOUNDARY_SPATIAL, HapticFeedbackConstants.MIUI_MESH_NORMAL);
                    CustomTrigger.this.resetEnterTime();
                }
                CustomTrigger.this.mIsStartIndeterminate = false;
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnActionCompleteListener
            public void onActionLoadFail(BaseTrigger.IndeterminateAction indeterminateAction) {
                CustomTrigger.this.mIsStartIndeterminate = false;
                if (CustomTrigger.this.mCurrentState == CustomTrigger.this.mActionStart && CustomTrigger.this.mCurrentAction == indeterminateAction) {
                    if (CustomTrigger.this.mOnActionDataListener != null) {
                        CustomTrigger.this.mOnActionDataListener.onActionLoadFail(indeterminateAction);
                    }
                    if (CustomTrigger.this.mLayout.getSpringScrollY() != 0) {
                        CustomTrigger customTrigger = CustomTrigger.this;
                        customTrigger.transition(customTrigger.mActionComplete);
                        if (CustomTrigger.this.mScrollState == 0) {
                            CustomTrigger.this.mLayout.smoothScrollTo(0, 0);
                            return;
                        }
                        return;
                    }
                    CustomTrigger customTrigger2 = CustomTrigger.this;
                    customTrigger2.transition(customTrigger2.mIdle);
                }
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnActionCompleteListener
            public void onActionStart(BaseTrigger.IndeterminateAction indeterminateAction) {
                BaseTrigger.Action action;
                CustomTrigger.this.mIsStartIndeterminate = true;
                if (CustomTrigger.this.getActions().size() > 0 && (action = CustomTrigger.this.getActions().get(0)) == indeterminateAction && CustomTrigger.this.mCurrentAction == null && CustomTrigger.this.mCurrentState == CustomTrigger.this.mIdle) {
                    CustomTrigger customTrigger = CustomTrigger.this;
                    customTrigger.transition(customTrigger.mTracking);
                    BaseTrigger.Action action2 = CustomTrigger.this.mCurrentAction;
                    CustomTrigger.this.mCurrentAction = action;
                    CustomTrigger customTrigger2 = CustomTrigger.this;
                    customTrigger2.notifyViewsStart(customTrigger2.mCurrentAction, action2, CustomTrigger.this.mLastScrollDistance);
                    if (CustomTrigger.this.mOnActionDataListener != null) {
                        CustomTrigger.this.mOnActionDataListener.onActionStart(indeterminateAction);
                    }
                    CustomTrigger.this.mLayout.smoothScrollTo(0, -CustomTrigger.this.mCurrentAction.mTriggerPoint);
                    if (CustomTrigger.this.mLayout.springBackEnable()) {
                        CustomTrigger.this.mContainer.layout(0, -CustomTrigger.this.mCurrentAction.mTriggerPoint, CustomTrigger.this.mContainer.getWidth(), 0);
                    } else {
                        CustomTrigger.this.mContainer.layout(0, 0, CustomTrigger.this.mContainer.getWidth(), CustomTrigger.this.mCurrentAction.mTriggerPoint);
                    }
                    CustomTrigger customTrigger3 = CustomTrigger.this;
                    customTrigger3.transition(customTrigger3.mWaitForIndeterminate);
                }
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnActionCompleteListener
            public void onUpdateTriggerTextIndex(BaseTrigger.IndeterminateAction indeterminateAction, int i, String str) {
                indeterminateAction.mTriggerTexts[i] = str;
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnActionCompleteListener
            public void onActionLoadCancel(BaseTrigger.IndeterminateAction indeterminateAction) {
                CustomTrigger.this.mIsStartIndeterminate = false;
                if (CustomTrigger.this.mCurrentState == CustomTrigger.this.mActionStart && CustomTrigger.this.mCurrentAction == indeterminateAction) {
                    if (CustomTrigger.this.mLayout.getSpringScrollY() != 0) {
                        CustomTrigger customTrigger = CustomTrigger.this;
                        customTrigger.transition(customTrigger.mActionComplete);
                        if (CustomTrigger.this.mScrollState == 0) {
                            CustomTrigger.this.mLayout.smoothScrollTo(0, 0);
                        }
                    } else {
                        CustomTrigger customTrigger2 = CustomTrigger.this;
                        customTrigger2.transition(customTrigger2.mIdle);
                    }
                    if (CustomTrigger.this.mOnActionDataListener != null) {
                        CustomTrigger.this.mOnActionDataListener.onActionComplete(indeterminateAction);
                    }
                }
            }
        };
        Idle idle = new Idle();
        this.mIdle = idle;
        this.mTracking = new Tracking();
        this.mActionStart = new ActionStart();
        this.mActionComplete = new ActionComplete();
        this.mWaitForIndeterminate = new WaitForIndeterminate();
        this.mActionTriggered = new ActionTriggered();
        this.mCurrentState = idle;
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mVelocityMonitor = new VelocityMonitor();
        RelativeLayout relativeLayout = (RelativeLayout) this.mLayoutInflater.inflate(R.layout.miuix_sbl_trigger_layout, (ViewGroup) null);
        this.mContainer = relativeLayout;
        this.mIndicatorContainer = (FrameLayout) relativeLayout.findViewById(R.id.indicator_container);
    }

    public void setOnIndeterminateActionViewListener(BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener onIndeterminateActionViewListener) {
        this.mOnIndeterminateActionViewListener = onIndeterminateActionViewListener;
    }

    public void setOnSimpleActionViewListener(BaseTrigger.SimpleAction.OnSimpleActionViewListener onSimpleActionViewListener) {
        this.mOnSimpleActionViewListener = onSimpleActionViewListener;
    }

    public void setOnIndeterminateUpActionViewListener(BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener onIndeterminateUpActionViewListener) {
        this.mOnIndeterminateUpActionViewListener = onIndeterminateUpActionViewListener;
    }

    public void setOnActionDataListener(OnIndeterminateActionDataListener onIndeterminateActionDataListener) {
        this.mOnActionDataListener = onIndeterminateActionDataListener;
    }

    public void setOnUpActionDataListener(OnIndeterminateUpActionDataListener onIndeterminateUpActionDataListener) {
        this.mOnUpActionDataListener = onIndeterminateUpActionDataListener;
    }

    public void setShowContainer(boolean z) {
        this.mIsShowContainer = z;
    }

    public TriggerState getCurrentState() {
        return this.mCurrentState;
    }

    public BaseTrigger.Action getCurrentAction() {
        return this.mCurrentAction;
    }

    @Override // miuix.springback.trigger.BaseTrigger
    public void addAction(BaseTrigger.Action action) {
        View view;
        View view2;
        View view3;
        super.addAction(action);
        if (action instanceof BaseTrigger.IndeterminateUpAction) {
            this.mIsExitIndeterminateUpAction = true;
            BaseTrigger.IndeterminateUpAction indeterminateUpAction = (BaseTrigger.IndeterminateUpAction) action;
            indeterminateUpAction.mUpDataListener = this.mUpActionDataListener;
            if (this.mUpContainer == null) {
                View viewOnCreateIndicator = indeterminateUpAction.onCreateIndicator(this.mLayoutInflater, this.mLayout);
                this.mUpContainer = viewOnCreateIndicator;
                if (viewOnCreateIndicator == null) {
                    this.mUpContainer = this.mLayoutInflater.inflate(R.layout.miuix_sbl_trigger_up_layout, (ViewGroup) null);
                }
                SpringBackLayout springBackLayout = this.mLayout;
                if (springBackLayout == null || (view3 = this.mUpContainer) == null) {
                    return;
                }
                springBackLayout.addView(view3);
                return;
            }
            return;
        }
        if (action instanceof BaseTrigger.IndeterminateAction) {
            this.mIsExitIndeterminateAction = true;
            BaseTrigger.IndeterminateAction indeterminateAction = (BaseTrigger.IndeterminateAction) action;
            indeterminateAction.mCompleteListener = this.mCompleteListener;
            if (this.mLoadingContainer == null) {
                View viewOnCreateIndicator2 = indeterminateAction.onCreateIndicator(this.mLayoutInflater, this.mContainer);
                this.mLoadingContainer = viewOnCreateIndicator2;
                if (viewOnCreateIndicator2 == null) {
                    View viewInflate = this.mLayoutInflater.inflate(R.layout.miuix_sbl_trigger_loading_progress, (ViewGroup) null);
                    View viewInflate2 = this.mLayoutInflater.inflate(R.layout.miuix_sbl_trigger_tracking_progress, (ViewGroup) null);
                    View viewInflate3 = this.mLayoutInflater.inflate(R.layout.miuix_sbl_trigger_tracking_progress_label, (ViewGroup) null);
                    this.mContainer.addView(viewInflate);
                    this.mContainer.addView(viewInflate2);
                    this.mContainer.addView(viewInflate3);
                }
                RelativeLayout relativeLayout = this.mContainer;
                if (relativeLayout == null || (view2 = this.mLoadingContainer) == null) {
                    return;
                }
                relativeLayout.addView(view2);
                return;
            }
            return;
        }
        if (action instanceof BaseTrigger.SimpleAction) {
            this.mIsExitISimpleAction = true;
            BaseTrigger.SimpleAction simpleAction = (BaseTrigger.SimpleAction) action;
            if (this.mSimpleActionView == null) {
                View viewOnCreateIndicator3 = simpleAction.onCreateIndicator(this.mLayoutInflater, this.mIndicatorContainer);
                this.mSimpleActionView = viewOnCreateIndicator3;
                if (viewOnCreateIndicator3 == null) {
                    this.mSimpleActionView = this.mLayoutInflater.inflate(R.layout.miuix_sbl_simple_indicator, (ViewGroup) this.mIndicatorContainer, false);
                }
                FrameLayout frameLayout = this.mIndicatorContainer;
                if (frameLayout == null || (view = this.mSimpleActionView) == null) {
                    return;
                }
                frameLayout.addView(view);
            }
        }
    }

    public ViewGroup getRootContainer() {
        return this.mContainer;
    }

    public ViewGroup getIndicatorContainer() {
        return this.mIndicatorContainer;
    }

    public View getSimpleActionView() {
        return this.mSimpleActionView;
    }

    public View getIndeterminateView() {
        return this.mLoadingContainer;
    }

    public View getIndeterminateUpView() {
        return this.mUpContainer;
    }

    public boolean isExitIndeterminateAction() {
        return this.mIsExitIndeterminateAction;
    }

    public boolean isExitIndeterminateUpAction() {
        return this.mIsExitIndeterminateUpAction;
    }

    public boolean isExitSimpleAction() {
        return this.mIsExitISimpleAction;
    }

    public boolean isShowContainer() {
        return this.mIsShowContainer;
    }

    @Override // miuix.springback.trigger.BaseTrigger
    public boolean removeAction(BaseTrigger.Action action) {
        boolean zRemoveAction = super.removeAction(action);
        if (zRemoveAction && (action instanceof BaseTrigger.IndeterminateUpAction)) {
            this.mIsExitIndeterminateUpAction = false;
            View view = this.mUpContainer;
            if (view != null) {
                this.mLayout.removeView(view);
                this.mUpContainer = null;
            }
        } else if (zRemoveAction && (action instanceof BaseTrigger.IndeterminateAction)) {
            this.mIsExitIndeterminateAction = false;
            View view2 = this.mLoadingContainer;
            if (view2 != null) {
                this.mContainer.removeView(view2);
                this.mLoadingContainer = null;
            }
        } else if (zRemoveAction && (action instanceof BaseTrigger.SimpleAction)) {
            this.mIsExitISimpleAction = false;
            View view3 = this.mSimpleActionView;
            if (view3 != null) {
                this.mIndicatorContainer.removeView(view3);
                this.mSimpleActionView = null;
            }
        }
        return zRemoveAction;
    }

    @Override // miuix.springback.trigger.BaseTrigger
    public boolean isActionRunning() {
        TriggerState triggerState = this.mCurrentState;
        return (triggerState == null || triggerState == this.mIdle) ? false : true;
    }

    @Override // miuix.springback.trigger.BaseTrigger
    public boolean isActionRunning(BaseTrigger.Action action) {
        TriggerState triggerState = this.mCurrentState;
        return (triggerState == null || triggerState == this.mIdle || this.mCurrentAction != action) ? false : true;
    }

    public void attach(SpringBackLayout springBackLayout) {
        if (!springBackLayout.springBackEnable()) {
            springBackLayout.setSpringBackEnableOnTriggerAttached(true);
        }
        this.mLayout = springBackLayout;
        springBackLayout.addView(this.mContainer);
        if (this.mUpContainer != null) {
            boolean z = false;
            for (int i = 0; i < this.mLayout.getChildCount(); i++) {
                if (this.mLayout.getChildAt(i) == this.mUpContainer) {
                    z = true;
                }
            }
            if (!z) {
                this.mLayout.addView(this.mUpContainer);
            }
        }
        if (this.mSimpleActionView != null) {
            boolean z2 = false;
            for (int i2 = 0; i2 < this.mIndicatorContainer.getChildCount(); i2++) {
                if (this.mIndicatorContainer.getChildAt(i2) == this.mSimpleActionView) {
                    z2 = true;
                }
            }
            if (!z2) {
                this.mIndicatorContainer.addView(this.mSimpleActionView);
            }
        }
        springBackLayout.addOnLayoutChangeListener(this.mLayoutChangeListener);
        springBackLayout.setOnSpringListener(this.mOnSpringListener);
        springBackLayout.addOnScrollChangeListener(this.mOnScrollListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float actionRestartOffsetPoint(BaseTrigger.Action action) {
        float simpleViewRestartOffsetPoint;
        float f;
        int i;
        if (action != null && (action instanceof BaseTrigger.IndeterminateAction)) {
            simpleViewRestartOffsetPoint = getIndeterminateViewRestartOffsetPoint();
        } else if (action != null && (action instanceof BaseTrigger.IndeterminateUpAction)) {
            simpleViewRestartOffsetPoint = getIndeterminateUpViewRestartOffsetPoint();
        } else {
            simpleViewRestartOffsetPoint = (action == null || !(action instanceof BaseTrigger.SimpleAction)) ? -1.0f : getSimpleViewRestartOffsetPoint();
        }
        if (simpleViewRestartOffsetPoint < 0.0f) {
            if (this.mScrollDistance < 0 && action == getIndeterminateUpAction() && getIndeterminateUpAction() != null) {
                f = (getIndeterminateUpAction().mTriggerPoint - getIndeterminateUpAction().mEnterPoint) * OFFSET_RESET_STATE;
                i = getIndeterminateUpAction().mEnterPoint;
            } else {
                BaseTrigger.Action action2 = this.mCurrentAction;
                if (action2 != null && (action instanceof BaseTrigger.IndeterminateAction)) {
                    f = (action2.mTriggerPoint - this.mCurrentAction.mEnterPoint) * OFFSET_RESET_STATE;
                    i = this.mCurrentAction.mEnterPoint;
                }
            }
            return f + i;
        }
        return 0.0f;
    }

    private class Idle extends TriggerState {
        private Idle() {
        }

        @Override // miuix.springback.trigger.TriggerState
        public void handleScrollStateChange(int i, int i2) {
            if (i == 0) {
                if (i2 == 1 || i2 == 2) {
                    CustomTrigger customTrigger = CustomTrigger.this;
                    customTrigger.transition(customTrigger.mTracking);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class Tracking extends TriggerState {
        private boolean mLockActivated;
        private boolean mTriggerable;
        private boolean mUpTriggerable;

        private Tracking() {
            this.mTriggerable = false;
            this.mUpTriggerable = false;
            this.mLockActivated = false;
        }

        @Override // miuix.springback.trigger.TriggerState
        void handleScrollStateChange(int i, int i2) {
            if (i2 == 0) {
                CustomTrigger customTrigger = CustomTrigger.this;
                customTrigger.transition(customTrigger.mIdle);
                CustomTrigger.this.mLayout.smoothScrollTo(0, 0);
                this.mUpTriggerable = false;
                this.mLockActivated = false;
            }
        }

        @Override // miuix.springback.trigger.TriggerState
        boolean handleSpringBack() {
            if ((!this.mTriggerable || CustomTrigger.this.mCurrentAction == null) && CustomTrigger.this.mCurrentAction != null && (CustomTrigger.this.mCurrentAction instanceof BaseTrigger.SimpleAction) && CustomTrigger.this.getSimpleActionView() != null) {
                CustomTrigger.this.getSimpleActionView().setVisibility(8);
            }
            if (CustomTrigger.this.mCurrentAction == null) {
                return false;
            }
            if (!(CustomTrigger.this.mCurrentAction instanceof BaseTrigger.IndeterminateAction) || CustomTrigger.this.mScrollDistance <= CustomTrigger.this.mCurrentAction.mEnterPoint) {
                if (CustomTrigger.this.mCurrentAction instanceof BaseTrigger.IndeterminateUpAction) {
                    CustomTrigger.this.mLayout.smoothScrollTo(0, CustomTrigger.this.mCurrentAction.mTriggerPoint);
                    CustomTrigger customTrigger = CustomTrigger.this;
                    customTrigger.transition(customTrigger.mWaitForIndeterminate);
                    return true;
                }
                CustomTrigger customTrigger2 = CustomTrigger.this;
                customTrigger2.transition(customTrigger2.mActionTriggered);
                if (this.mLockActivated) {
                    CustomTrigger.this.mCurrentAction.notifyTriggered();
                    CustomTrigger customTrigger3 = CustomTrigger.this;
                    customTrigger3.notifyViewsTriggered(customTrigger3.mCurrentAction, CustomTrigger.this.mScrollDistance);
                } else {
                    CustomTrigger.this.mCurrentAction.notifyExit();
                    CustomTrigger customTrigger4 = CustomTrigger.this;
                    customTrigger4.notifyViewsExit(customTrigger4.mCurrentAction, CustomTrigger.this.mScrollDistance);
                }
                if (CustomTrigger.this.getSimpleActionView() != null) {
                    CustomTrigger.this.getSimpleActionView().setVisibility(8);
                }
                return false;
            }
            if (this.mTriggerable) {
                CustomTrigger.this.mLayout.smoothScrollTo(0, -CustomTrigger.this.mCurrentAction.mTriggerPoint);
                CustomTrigger customTrigger5 = CustomTrigger.this;
                customTrigger5.transition(customTrigger5.mWaitForIndeterminate);
            } else {
                if (Math.abs(CustomTrigger.this.mLayout.getScaleY()) < Math.abs(CustomTrigger.this.mCurrentAction.mTriggerPoint)) {
                    CustomTrigger.this.mCurrentAction.notifyExit();
                    CustomTrigger customTrigger6 = CustomTrigger.this;
                    customTrigger6.notifyViewsExit(customTrigger6.mCurrentAction, CustomTrigger.this.mScrollDistance);
                }
                CustomTrigger.this.mLayout.smoothScrollTo(0, 0);
            }
            return true;
        }

        @Override // miuix.springback.trigger.TriggerState
        void handleScrolled(int i, int i2) {
            if (CustomTrigger.this.mScrollState == 1 || CustomTrigger.this.mScrollState == 2) {
                BaseTrigger.Action action = CustomTrigger.this.mCurrentAction;
                if (CustomTrigger.this.mScrollDistance < 0) {
                    if (!CustomTrigger.this.mUpActionBegin) {
                        this.mUpTriggerable = false;
                    }
                    boolean z = this.mUpTriggerable;
                    BaseTrigger.IndeterminateUpAction indeterminateUpAction = CustomTrigger.this.getIndeterminateUpAction();
                    if (indeterminateUpAction != null) {
                        CustomTrigger.this.mIsStartIndeterminateUp = true;
                        View indeterminateUpView = CustomTrigger.this.getIndeterminateUpView();
                        if (indeterminateUpView != null && indeterminateUpView.getVisibility() != 0) {
                            indeterminateUpView.setVisibility(0);
                        }
                        CustomTrigger.this.mCurrentAction = indeterminateUpAction;
                        CustomTrigger customTrigger = CustomTrigger.this;
                        customTrigger.notifyViewsStart(customTrigger.mCurrentAction, action, CustomTrigger.this.mLastScrollDistance);
                        if (Math.abs(CustomTrigger.this.mScrollDistance) > CustomTrigger.this.getIndeterminateUpAction().mEnterPoint && !CustomTrigger.this.mUpActionBegin) {
                            CustomTrigger.this.mUpActionBegin = true;
                            this.mUpTriggerable = true;
                            CustomTrigger.this.mEnterTime = SystemClock.elapsedRealtime();
                            indeterminateUpAction.notifyEntered();
                            CustomTrigger customTrigger2 = CustomTrigger.this;
                            customTrigger2.notifyViewsEntered(customTrigger2.mCurrentAction, CustomTrigger.this.mScrollDistance);
                        }
                        boolean z2 = this.mUpTriggerable;
                        if (z != z2 && z2) {
                            indeterminateUpAction.notifyActivated();
                            CustomTrigger customTrigger3 = CustomTrigger.this;
                            customTrigger3.notifyViewsActivated(customTrigger3.mCurrentAction, CustomTrigger.this.mScrollDistance);
                            if (CustomTrigger.this.mScrollState == 2) {
                                CustomTrigger.this.mLayout.smoothScrollTo(0, indeterminateUpAction.mTriggerPoint);
                                CustomTrigger customTrigger4 = CustomTrigger.this;
                                customTrigger4.transition(customTrigger4.mWaitForIndeterminate);
                            }
                        }
                    }
                    CustomTrigger customTrigger5 = CustomTrigger.this;
                    customTrigger5.notifyViewsAnimator(customTrigger5.mCurrentAction, action, CustomTrigger.this.mScrollDistance);
                    return;
                }
                this.mUpTriggerable = false;
                int i3 = CustomTrigger.this.mActionIndex;
                boolean z3 = this.mTriggerable;
                BaseTrigger.Action action2 = CustomTrigger.this.mCurrentAction;
                for (int i4 = 0; i4 < CustomTrigger.this.getActions().size() && CustomTrigger.this.mScrollDistance > CustomTrigger.this.getActions().get(i4).mEnterPoint; i4++) {
                    CustomTrigger.this.mActionIndex = i4;
                }
                if (CustomTrigger.this.mActionIndex >= 0) {
                    BaseTrigger.Action action3 = CustomTrigger.this.getActions().get(CustomTrigger.this.mActionIndex);
                    boolean z4 = action3 != null && (action3 instanceof BaseTrigger.SimpleAction);
                    if ((!z4 || CustomTrigger.this.mVelocityY >= CustomTrigger.OFFSET_SIMPLE_VELOCITY_Y || CustomTrigger.this.mScrollState != 1) && z4) {
                        CustomTrigger.this.mActionIndex = i3;
                    } else {
                        CustomTrigger.this.mCurrentAction = action3;
                        CustomTrigger customTrigger6 = CustomTrigger.this;
                        customTrigger6.notifyViewsStart(customTrigger6.mCurrentAction, action, CustomTrigger.this.mLastScrollDistance);
                        this.mTriggerable = CustomTrigger.this.mScrollDistance >= CustomTrigger.this.mCurrentAction.mTriggerPoint;
                    }
                } else {
                    CustomTrigger.this.mCurrentAction = null;
                    this.mTriggerable = false;
                }
                if (i3 != CustomTrigger.this.mActionIndex) {
                    if (action2 != null) {
                        action2.onExit();
                        if (CustomTrigger.this.getSimpleActionView() != null) {
                            CustomTrigger.this.getSimpleActionView().setVisibility(8);
                        }
                    }
                    if (CustomTrigger.this.mCurrentAction != null) {
                        if (!(CustomTrigger.this.mCurrentAction instanceof BaseTrigger.IndeterminateAction)) {
                            if ((CustomTrigger.this.mCurrentAction instanceof BaseTrigger.SimpleAction) && CustomTrigger.this.getSimpleActionView() != null) {
                                CustomTrigger.this.getSimpleActionView().setVisibility(0);
                            }
                        } else if (CustomTrigger.this.getSimpleActionView() != null) {
                            CustomTrigger.this.getSimpleActionView().setVisibility(8);
                        }
                        CustomTrigger.this.mEnterTime = SystemClock.elapsedRealtime();
                        CustomTrigger.this.mCurrentAction.notifyEntered();
                        CustomTrigger customTrigger7 = CustomTrigger.this;
                        customTrigger7.notifyViewsEntered(customTrigger7.mCurrentAction, CustomTrigger.this.mScrollDistance);
                        this.mLockActivated = false;
                        if (this.mTriggerable) {
                            if (CustomTrigger.this.mCurrentAction instanceof BaseTrigger.SimpleAction) {
                                this.mLockActivated = true;
                                HapticCompat.performHapticFeedback(CustomTrigger.this.mLayout, HapticFeedbackConstants.MIUI_BOUNDARY_SPATIAL, HapticFeedbackConstants.MIUI_SWITCH);
                            }
                            CustomTrigger.this.mCurrentAction.notifyActivated();
                            CustomTrigger customTrigger8 = CustomTrigger.this;
                            customTrigger8.notifyViewsActivated(customTrigger8.mCurrentAction, CustomTrigger.this.mScrollDistance);
                        }
                    } else if (CustomTrigger.this.getSimpleActionView() != null) {
                        CustomTrigger.this.getSimpleActionView().setVisibility(8);
                    }
                } else if (action2 != null && z3 != this.mTriggerable) {
                    if (z3) {
                        CustomTrigger.this.mEnterTime = SystemClock.elapsedRealtime();
                        action2.notifyEntered();
                        CustomTrigger customTrigger9 = CustomTrigger.this;
                        customTrigger9.notifyViewsEntered(customTrigger9.mCurrentAction, CustomTrigger.this.mScrollDistance);
                        this.mLockActivated = false;
                    } else {
                        if (CustomTrigger.this.mCurrentAction instanceof BaseTrigger.SimpleAction) {
                            this.mLockActivated = true;
                        }
                        HapticCompat.performHapticFeedback(CustomTrigger.this.mLayout, HapticFeedbackConstants.MIUI_BOUNDARY_SPATIAL, HapticFeedbackConstants.MIUI_MESH_NORMAL);
                        action2.notifyActivated();
                        CustomTrigger customTrigger10 = CustomTrigger.this;
                        customTrigger10.notifyViewsActivated(customTrigger10.mCurrentAction, CustomTrigger.this.mScrollDistance);
                    }
                }
                CustomTrigger customTrigger11 = CustomTrigger.this;
                customTrigger11.notifyViewsAnimator(customTrigger11.mCurrentAction, action, CustomTrigger.this.mScrollDistance);
            }
        }
    }

    private class WaitForIndeterminate extends TriggerState {
        private WaitForIndeterminate() {
        }

        @Override // miuix.springback.trigger.TriggerState
        void handleScrollStateChange(int i, int i2) {
            if (i2 == 0) {
                CustomTrigger customTrigger = CustomTrigger.this;
                customTrigger.transition(customTrigger.mActionStart);
                if (CustomTrigger.this.mCurrentAction != null && (CustomTrigger.this.mCurrentAction instanceof BaseTrigger.IndeterminateAction)) {
                    CustomTrigger.this.mCurrentAction.notifyTriggered();
                    CustomTrigger customTrigger2 = CustomTrigger.this;
                    customTrigger2.notifyViewsTriggered(customTrigger2.mCurrentAction, CustomTrigger.this.mScrollDistance);
                } else {
                    if (CustomTrigger.this.getIndeterminateUpAction() == null || !(CustomTrigger.this.mCurrentAction instanceof BaseTrigger.IndeterminateUpAction)) {
                        return;
                    }
                    CustomTrigger.this.getIndeterminateUpAction().notifyTriggered();
                    CustomTrigger customTrigger3 = CustomTrigger.this;
                    customTrigger3.notifyViewsTriggered(customTrigger3.mCurrentAction, CustomTrigger.this.mScrollDistance);
                }
            }
        }
    }

    private class ActionTriggered extends TriggerState {
        private ActionTriggered() {
        }

        @Override // miuix.springback.trigger.TriggerState
        void handleScrollStateChange(int i, int i2) {
            if (i2 == 0) {
                CustomTrigger customTrigger = CustomTrigger.this;
                customTrigger.transition(customTrigger.mIdle);
            }
        }

        @Override // miuix.springback.trigger.TriggerState
        void handleScrolled(int i, int i2) {
            if (CustomTrigger.this.mCurrentAction == null || !(CustomTrigger.this.mCurrentAction instanceof BaseTrigger.SimpleAction) || CustomTrigger.this.mScrollDistance >= CustomTrigger.this.mCurrentAction.mEnterPoint || CustomTrigger.this.mScrollState != 1) {
                return;
            }
            CustomTrigger.this.mActionIndex = -1;
            CustomTrigger customTrigger = CustomTrigger.this;
            customTrigger.transition(customTrigger.mTracking);
        }
    }

    private class ActionStart extends TriggerState {
        private ActionStart() {
        }

        @Override // miuix.springback.trigger.TriggerState
        void handleScrollStateChange(int i, int i2) {
            super.handleScrollStateChange(i, i2);
        }

        @Override // miuix.springback.trigger.TriggerState
        void handleScrolled(int i, int i2) {
            super.handleScrolled(i, i2);
        }

        @Override // miuix.springback.trigger.TriggerState
        boolean handleSpringBack() {
            if (CustomTrigger.this.mCurrentAction != null && (CustomTrigger.this.mCurrentAction instanceof BaseTrigger.IndeterminateAction) && CustomTrigger.this.mScrollDistance > CustomTrigger.this.mCurrentAction.mTriggerPoint) {
                CustomTrigger.this.mLayout.smoothScrollTo(0, -CustomTrigger.this.mCurrentAction.mTriggerPoint);
                return true;
            }
            return super.handleSpringBack();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class ActionComplete extends TriggerState {
        private ActionComplete() {
        }

        @Override // miuix.springback.trigger.TriggerState
        void handleScrollStateChange(int i, int i2) {
            super.handleScrollStateChange(i, i2);
            if (i2 == 0) {
                CustomTrigger customTrigger = CustomTrigger.this;
                customTrigger.transition(customTrigger.mIdle);
            }
        }
    }

    protected void transition(TriggerState triggerState) {
        BaseTrigger.Action action;
        this.mCurrentState = triggerState;
        if (triggerState == this.mIdle) {
            if (this.mScrollerFinished && (action = this.mCurrentAction) != null) {
                action.notifyFinished();
                BaseTrigger.Action action2 = this.mCurrentAction;
                if (action2 instanceof BaseTrigger.IndeterminateAction) {
                    notifyIndeterminateViewFinished(this.mScrollDistance);
                } else if (action2 instanceof BaseTrigger.IndeterminateUpAction) {
                    notifyIndeterminateUpViewFinished(this.mScrollDistance);
                } else if (action2 instanceof BaseTrigger.SimpleAction) {
                    notifySimpleViewFinished(this.mScrollDistance);
                }
            }
            this.mCurrentAction = null;
            this.mActionIndex = -1;
            this.mVelocityMonitor.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetEnterTime() {
        this.mEnterTime = -1L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getActionIntervalTime() {
        if (this.mEnterTime == -1) {
            return 0L;
        }
        return SystemClock.elapsedRealtime() - this.mEnterTime;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyViewsStart(BaseTrigger.Action action, BaseTrigger.Action action2, int i) {
        if (action != null && (action instanceof BaseTrigger.IndeterminateAction) && action2 != action) {
            notifyIndeterminateViewStart(i);
            return;
        }
        if (action != null && (action instanceof BaseTrigger.SimpleAction) && action2 != action) {
            notifySimpleViewStart(i);
        } else {
            if (action == null || !(action instanceof BaseTrigger.IndeterminateUpAction) || action2 == action) {
                return;
            }
            notifyIndeterminateUpViewStart(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyViewsEntered(BaseTrigger.Action action, int i) {
        if (action != null && (action instanceof BaseTrigger.IndeterminateAction)) {
            notifyIndeterminateViewEntered(i);
            return;
        }
        if (action != null && (action instanceof BaseTrigger.SimpleAction)) {
            notifySimpleViewEntered(i);
        } else {
            if (action == null || !(action instanceof BaseTrigger.IndeterminateUpAction)) {
                return;
            }
            notifyIndeterminateUpViewEntered(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyViewsActivated(BaseTrigger.Action action, int i) {
        if (action != null && (action instanceof BaseTrigger.IndeterminateAction)) {
            notifyIndeterminateViewActivated(i);
            return;
        }
        if (action != null && (action instanceof BaseTrigger.SimpleAction)) {
            notifySimpleViewActivated(i);
        } else {
            if (action == null || !(action instanceof BaseTrigger.IndeterminateUpAction)) {
                return;
            }
            notifyIndeterminateUpViewActivated(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyViewsTriggered(BaseTrigger.Action action, int i) {
        if (action != null && (action instanceof BaseTrigger.IndeterminateAction)) {
            notifyIndeterminateViewTriggered(i);
            return;
        }
        if (action != null && (action instanceof BaseTrigger.SimpleAction)) {
            notifySimpleViewTriggered(i);
        } else {
            if (action == null || !(action instanceof BaseTrigger.IndeterminateUpAction)) {
                return;
            }
            notifyIndeterminateUpViewTriggered(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyViewsExit(BaseTrigger.Action action, int i) {
        if (action != null && (action instanceof BaseTrigger.IndeterminateAction)) {
            notifyIndeterminateViewExit(i);
            return;
        }
        if (action != null && (action instanceof BaseTrigger.SimpleAction)) {
            notifySimpleViewExit(i);
        } else {
            if (action == null || !(action instanceof BaseTrigger.IndeterminateUpAction)) {
                return;
            }
            notifyIndeterminateUpViewExit(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyViewsAnimator(BaseTrigger.Action action, BaseTrigger.Action action2, int i) {
        if (action != null && (action instanceof BaseTrigger.IndeterminateAction)) {
            if (Math.abs(i) < action.mEnterPoint) {
                notifyIndeterminateViewStarting(i);
            }
            if (Math.abs(i) >= action.mEnterPoint && Math.abs(i) < action.mTriggerPoint) {
                notifyIndeterminateViewEntering(i);
            }
            if (Math.abs(i) >= action.mTriggerPoint) {
                notifyIndeterminateViewActivating(i);
                return;
            }
            return;
        }
        if (action != null && (action instanceof BaseTrigger.SimpleAction)) {
            if (Math.abs(i) < action.mEnterPoint) {
                notifySimpleViewStarting(i);
            }
            if (Math.abs(i) >= action.mEnterPoint && Math.abs(i) < action.mTriggerPoint) {
                notifySimpleViewEntering(i);
            }
            if (Math.abs(i) >= action.mTriggerPoint) {
                notifySimpleViewActivating(i);
                return;
            }
            return;
        }
        if (action == null || !(action instanceof BaseTrigger.IndeterminateUpAction)) {
            return;
        }
        if (Math.abs(i) < action.mEnterPoint) {
            notifyIndeterminateUpViewStarting(i);
        }
        if (Math.abs(i) >= action.mEnterPoint && Math.abs(i) < action.mTriggerPoint) {
            notifyIndeterminateUpViewEntering(i);
        }
        if (Math.abs(i) >= action.mTriggerPoint) {
            notifyIndeterminateUpViewActivating(i);
        }
    }

    private float getIndeterminateViewRestartOffsetPoint() {
        BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener onIndeterminateActionViewListener = this.mOnIndeterminateActionViewListener;
        if (onIndeterminateActionViewListener != null) {
            return onIndeterminateActionViewListener.getViewRestartOffsetPoint();
        }
        return 0.0f;
    }

    private void notifyIndeterminateViewStart(int i) {
        BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener onIndeterminateActionViewListener = this.mOnIndeterminateActionViewListener;
        if (onIndeterminateActionViewListener != null) {
            onIndeterminateActionViewListener.onViewStart(i);
        }
    }

    private void notifyIndeterminateViewStarting(int i) {
        BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener onIndeterminateActionViewListener = this.mOnIndeterminateActionViewListener;
        if (onIndeterminateActionViewListener != null) {
            onIndeterminateActionViewListener.onViewStarting(i);
        }
    }

    private void notifyIndeterminateViewEntered(int i) {
        BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener onIndeterminateActionViewListener = this.mOnIndeterminateActionViewListener;
        if (onIndeterminateActionViewListener != null) {
            onIndeterminateActionViewListener.onViewEntered(i);
        }
    }

    private void notifyIndeterminateViewEntering(int i) {
        BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener onIndeterminateActionViewListener = this.mOnIndeterminateActionViewListener;
        if (onIndeterminateActionViewListener != null) {
            onIndeterminateActionViewListener.onViewEntering(i);
        }
    }

    private void notifyIndeterminateViewActivated(int i) {
        BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener onIndeterminateActionViewListener = this.mOnIndeterminateActionViewListener;
        if (onIndeterminateActionViewListener != null) {
            onIndeterminateActionViewListener.onViewActivated(i);
        }
    }

    private void notifyIndeterminateViewActivating(int i) {
        BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener onIndeterminateActionViewListener = this.mOnIndeterminateActionViewListener;
        if (onIndeterminateActionViewListener != null) {
            onIndeterminateActionViewListener.onViewActivating(i);
        }
    }

    private void notifyIndeterminateViewTriggered(int i) {
        BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener onIndeterminateActionViewListener = this.mOnIndeterminateActionViewListener;
        if (onIndeterminateActionViewListener != null) {
            onIndeterminateActionViewListener.onViewTriggered(i);
        }
    }

    private void notifyIndeterminateViewFinished(int i) {
        BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener onIndeterminateActionViewListener = this.mOnIndeterminateActionViewListener;
        if (onIndeterminateActionViewListener != null) {
            onIndeterminateActionViewListener.onViewFinished(i);
        }
    }

    private void notifyIndeterminateViewExit(int i) {
        BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener onIndeterminateActionViewListener = this.mOnIndeterminateActionViewListener;
        if (onIndeterminateActionViewListener != null) {
            onIndeterminateActionViewListener.onViewExit(i);
        }
    }

    private float getSimpleViewRestartOffsetPoint() {
        BaseTrigger.SimpleAction.OnSimpleActionViewListener onSimpleActionViewListener = this.mOnSimpleActionViewListener;
        if (onSimpleActionViewListener != null) {
            return onSimpleActionViewListener.getViewRestartOffsetPoint();
        }
        return 0.0f;
    }

    private void notifySimpleViewStart(int i) {
        BaseTrigger.SimpleAction.OnSimpleActionViewListener onSimpleActionViewListener = this.mOnSimpleActionViewListener;
        if (onSimpleActionViewListener != null) {
            onSimpleActionViewListener.onViewStart(i);
        }
    }

    private void notifySimpleViewStarting(int i) {
        BaseTrigger.SimpleAction.OnSimpleActionViewListener onSimpleActionViewListener = this.mOnSimpleActionViewListener;
        if (onSimpleActionViewListener != null) {
            onSimpleActionViewListener.onViewStarting(i);
        }
    }

    private void notifySimpleViewEntered(int i) {
        BaseTrigger.SimpleAction.OnSimpleActionViewListener onSimpleActionViewListener = this.mOnSimpleActionViewListener;
        if (onSimpleActionViewListener != null) {
            onSimpleActionViewListener.onViewEntered(i);
        }
    }

    private void notifySimpleViewEntering(int i) {
        BaseTrigger.SimpleAction.OnSimpleActionViewListener onSimpleActionViewListener = this.mOnSimpleActionViewListener;
        if (onSimpleActionViewListener != null) {
            onSimpleActionViewListener.onViewEntering(i);
        }
    }

    private void notifySimpleViewActivated(int i) {
        BaseTrigger.SimpleAction.OnSimpleActionViewListener onSimpleActionViewListener = this.mOnSimpleActionViewListener;
        if (onSimpleActionViewListener != null) {
            onSimpleActionViewListener.onViewActivated(i);
        }
    }

    private void notifySimpleViewActivating(int i) {
        BaseTrigger.SimpleAction.OnSimpleActionViewListener onSimpleActionViewListener = this.mOnSimpleActionViewListener;
        if (onSimpleActionViewListener != null) {
            onSimpleActionViewListener.onViewActivating(i);
        }
    }

    private void notifySimpleViewTriggered(int i) {
        BaseTrigger.SimpleAction.OnSimpleActionViewListener onSimpleActionViewListener = this.mOnSimpleActionViewListener;
        if (onSimpleActionViewListener != null) {
            onSimpleActionViewListener.onViewTriggered(i);
        }
    }

    private void notifySimpleViewFinished(int i) {
        BaseTrigger.SimpleAction.OnSimpleActionViewListener onSimpleActionViewListener = this.mOnSimpleActionViewListener;
        if (onSimpleActionViewListener != null) {
            onSimpleActionViewListener.onViewFinished(i);
        }
    }

    private void notifySimpleViewExit(int i) {
        BaseTrigger.SimpleAction.OnSimpleActionViewListener onSimpleActionViewListener = this.mOnSimpleActionViewListener;
        if (onSimpleActionViewListener != null) {
            onSimpleActionViewListener.onViewExit(i);
        }
    }

    private float getIndeterminateUpViewRestartOffsetPoint() {
        BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener onIndeterminateUpActionViewListener = this.mOnIndeterminateUpActionViewListener;
        if (onIndeterminateUpActionViewListener != null) {
            return onIndeterminateUpActionViewListener.getViewRestartOffsetPoint();
        }
        return 0.0f;
    }

    private void notifyIndeterminateUpViewStart(int i) {
        BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener onIndeterminateUpActionViewListener = this.mOnIndeterminateUpActionViewListener;
        if (onIndeterminateUpActionViewListener != null) {
            onIndeterminateUpActionViewListener.onViewStart(i);
        }
    }

    private void notifyIndeterminateUpViewStarting(int i) {
        BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener onIndeterminateUpActionViewListener = this.mOnIndeterminateUpActionViewListener;
        if (onIndeterminateUpActionViewListener != null) {
            onIndeterminateUpActionViewListener.onViewStarting(i);
        }
    }

    private void notifyIndeterminateUpViewEntered(int i) {
        BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener onIndeterminateUpActionViewListener = this.mOnIndeterminateUpActionViewListener;
        if (onIndeterminateUpActionViewListener != null) {
            onIndeterminateUpActionViewListener.onViewEntered(i);
        }
    }

    private void notifyIndeterminateUpViewEntering(int i) {
        BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener onIndeterminateUpActionViewListener = this.mOnIndeterminateUpActionViewListener;
        if (onIndeterminateUpActionViewListener != null) {
            onIndeterminateUpActionViewListener.onViewEntering(i);
        }
    }

    private void notifyIndeterminateUpViewActivated(int i) {
        BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener onIndeterminateUpActionViewListener = this.mOnIndeterminateUpActionViewListener;
        if (onIndeterminateUpActionViewListener != null) {
            onIndeterminateUpActionViewListener.onViewActivated(i);
        }
    }

    private void notifyIndeterminateUpViewActivating(int i) {
        BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener onIndeterminateUpActionViewListener = this.mOnIndeterminateUpActionViewListener;
        if (onIndeterminateUpActionViewListener != null) {
            onIndeterminateUpActionViewListener.onViewActivating(i);
        }
    }

    private void notifyIndeterminateUpViewTriggered(int i) {
        BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener onIndeterminateUpActionViewListener = this.mOnIndeterminateUpActionViewListener;
        if (onIndeterminateUpActionViewListener != null) {
            onIndeterminateUpActionViewListener.onViewTriggered(i);
        }
    }

    private void notifyIndeterminateUpViewFinished(int i) {
        BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener onIndeterminateUpActionViewListener = this.mOnIndeterminateUpActionViewListener;
        if (onIndeterminateUpActionViewListener != null) {
            onIndeterminateUpActionViewListener.onViewFinished(i);
        }
    }

    private void notifyIndeterminateUpViewExit(int i) {
        BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener onIndeterminateUpActionViewListener = this.mOnIndeterminateUpActionViewListener;
        if (onIndeterminateUpActionViewListener != null) {
            onIndeterminateUpActionViewListener.onViewExit(i);
        }
    }
}
