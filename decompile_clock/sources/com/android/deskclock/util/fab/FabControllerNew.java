package com.android.deskclock.util.fab;

import android.animation.Animator;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.util.AnimationUtils;
import com.android.deskclock.util.ClickUtils;
import com.android.deskclock.util.Util;
import com.android.deskclock.view.FabView;
import com.android.deskclock.view.tab.TabViewModel;
import com.android.deskclock.widget.TimerButton;
import java.lang.ref.WeakReference;
import miuix.animation.listener.TransitionListener;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes.dex */
public class FabControllerNew implements FabDataHelper.DataChangeListener {
    private static final String TAG = "DC:FabControllerNew";
    public static boolean mEndBtnShow;
    private static volatile FabControllerNew mFabControllerNew;
    private WeakReference<FabView> mEndBtn2WeakRef;
    private boolean mIsInActionModeChangeAnim;
    private boolean mIsInPageChangeAnim;
    private boolean mIsInStateChangeAnim;
    FabDataHelper.TabInfo mLastTabInfo;
    private onAlarmFabClickListener mOnAlarmFabClickListener;
    private onStopWatchFabClickListener mOnStopWatchFabClickListener;
    private onTimerFabClickListener mOnTimerFabClickListener;
    private WeakReference<TimerButton> mStopWatchCenterBtnWeakRef;
    private WeakReference<TimerButton> mStopWatchEndBtnWeakRef;
    private WeakReference<TimerButton> mStopWatchStartBtnWeakRef;
    private WeakReference<TimerButton> mTimerCenterBtnWeakRef;
    private WeakReference<TimerButton> mTimerEndBtnWeakRef;
    private WeakReference<TimerButton> mTimerStartBtnWeakRef;
    private ValueAnimator resetAnimator1;
    private ValueAnimator resetAnimator2;
    private static FloatEvaluator evaluator = new FloatEvaluator();
    protected static boolean mSupportLinearMotorVibrate = Util.isSupportLinearMotorVibrate();
    private static int TYPE_SINGLE_BTN = 1;
    private static int TYPE_DOUBLE_BTN = 2;
    FabDataHelper.TabInfo mCurrTabInfo = FabDataHelper.getTabInfo(TabViewModel.TAB_ALARM);
    MiuiFolme.ClockTransitionListener mInnerStateChangeListener = new MiuiFolme.ClockTransitionListener() { // from class: com.android.deskclock.util.fab.FabControllerNew.1
        @Override // miuix.animation.listener.TransitionListener
        public void onBegin(Object obj) {
            super.onBegin(obj);
            FabControllerNew.this.mIsInStateChangeAnim = true;
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onCancel(Object obj) {
            super.onCancel(obj);
            FabControllerNew.this.mIsInStateChangeAnim = false;
            FabControllerNew fabControllerNew = FabControllerNew.this;
            TimerButton timerButton = (TimerButton) fabControllerNew.getValueFromWeakRef(fabControllerNew.mStopWatchStartBtnWeakRef);
            FabControllerNew fabControllerNew2 = FabControllerNew.this;
            TimerButton timerButton2 = (TimerButton) fabControllerNew2.getValueFromWeakRef(fabControllerNew2.mStopWatchEndBtnWeakRef);
            FabControllerNew fabControllerNew3 = FabControllerNew.this;
            TimerButton timerButton3 = (TimerButton) fabControllerNew3.getValueFromWeakRef(fabControllerNew3.mStopWatchCenterBtnWeakRef);
            FabControllerNew fabControllerNew4 = FabControllerNew.this;
            TimerButton timerButton4 = (TimerButton) fabControllerNew4.getValueFromWeakRef(fabControllerNew4.mTimerStartBtnWeakRef);
            FabControllerNew fabControllerNew5 = FabControllerNew.this;
            TimerButton timerButton5 = (TimerButton) fabControllerNew5.getValueFromWeakRef(fabControllerNew5.mTimerEndBtnWeakRef);
            FabControllerNew fabControllerNew6 = FabControllerNew.this;
            TimerButton timerButton6 = (TimerButton) fabControllerNew6.getValueFromWeakRef(fabControllerNew6.mTimerCenterBtnWeakRef);
            FabControllerNew fabControllerNew7 = FabControllerNew.this;
            FabView fabView = (FabView) fabControllerNew7.getValueFromWeakRef(fabControllerNew7.mEndBtn2WeakRef);
            MiuiFolme.cancelFolme(timerButton, timerButton2, timerButton3, timerButton4, timerButton5, timerButton6, fabView);
            MiuiFolme.resetFabTouch(timerButton, timerButton2, timerButton3, timerButton4, timerButton5, timerButton6, fabView);
            FabControllerNew.resetFabScaleAndAlpha(timerButton, timerButton2, timerButton3, timerButton4, timerButton5, timerButton6, fabView);
            FabControllerNew fabControllerNew8 = FabControllerNew.this;
            fabControllerNew8.startFabChangeDirectly(fabControllerNew8.mCurrTabInfo);
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj) {
            super.onComplete(obj);
            FabControllerNew.this.mIsInStateChangeAnim = false;
            FabControllerNew fabControllerNew = FabControllerNew.this;
            TimerButton timerButton = (TimerButton) fabControllerNew.getValueFromWeakRef(fabControllerNew.mStopWatchStartBtnWeakRef);
            FabControllerNew fabControllerNew2 = FabControllerNew.this;
            TimerButton timerButton2 = (TimerButton) fabControllerNew2.getValueFromWeakRef(fabControllerNew2.mStopWatchEndBtnWeakRef);
            FabControllerNew fabControllerNew3 = FabControllerNew.this;
            TimerButton timerButton3 = (TimerButton) fabControllerNew3.getValueFromWeakRef(fabControllerNew3.mStopWatchCenterBtnWeakRef);
            FabControllerNew fabControllerNew4 = FabControllerNew.this;
            TimerButton timerButton4 = (TimerButton) fabControllerNew4.getValueFromWeakRef(fabControllerNew4.mTimerStartBtnWeakRef);
            FabControllerNew fabControllerNew5 = FabControllerNew.this;
            TimerButton timerButton5 = (TimerButton) fabControllerNew5.getValueFromWeakRef(fabControllerNew5.mTimerEndBtnWeakRef);
            FabControllerNew fabControllerNew6 = FabControllerNew.this;
            TimerButton timerButton6 = (TimerButton) fabControllerNew6.getValueFromWeakRef(fabControllerNew6.mTimerCenterBtnWeakRef);
            FabControllerNew fabControllerNew7 = FabControllerNew.this;
            FabView fabView = (FabView) fabControllerNew7.getValueFromWeakRef(fabControllerNew7.mEndBtn2WeakRef);
            MiuiFolme.resetFabTouch(timerButton, timerButton2, timerButton3, timerButton4, timerButton5, timerButton6, fabView);
            FabControllerNew.resetFabScaleAndAlpha(timerButton, timerButton2, timerButton3, timerButton4, timerButton5, timerButton6, fabView);
            FabControllerNew fabControllerNew8 = FabControllerNew.this;
            fabControllerNew8.startFabChangeDirectly(fabControllerNew8.mCurrTabInfo);
        }
    };

    public interface onAlarmFabClickListener {
        void onEndFabClick2(View view);
    }

    public interface onStopWatchFabClickListener {
        void onCenterFabClick(View view);

        void onEndFabClick(View view);

        void onStartFabClick(View view);
    }

    public interface onTimerFabClickListener {
        void onCenterFabClick(View view);

        void onEndFabClick(View view);

        void onStartFabClick(View view);
    }

    public static FabControllerNew getInstance() {
        if (mFabControllerNew == null) {
            synchronized (FabControllerNew.class) {
                if (mFabControllerNew == null) {
                    mFabControllerNew = new FabControllerNew();
                }
            }
        }
        return mFabControllerNew;
    }

    private FabControllerNew() {
        FabDataHelper.getInstance().setDataChangeListener(this);
    }

    public void initAlarmFabViewBtn(FabView fabView) {
        FabDataHelper.getInstance().initAlarmFab();
        this.mEndBtn2WeakRef = new WeakReference<>(fabView);
        if (fabView != null) {
            fabView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.util.fab.FabControllerNew.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Log.d(FabControllerNew.TAG, "alarm btn onClick: mIsInPageChangeAnim" + FabControllerNew.this.mIsInPageChangeAnim + "mIsInStateChangeAnim" + FabControllerNew.this.mIsInStateChangeAnim + "mIsInActionModeChangeAnim" + FabControllerNew.this.mIsInActionModeChangeAnim);
                    if (FabControllerNew.this.mIsInPageChangeAnim || FabControllerNew.this.mIsInStateChangeAnim || FabControllerNew.this.mIsInActionModeChangeAnim) {
                        return;
                    }
                    FabControllerNew.vibrate(view);
                    if (FabControllerNew.this.mOnAlarmFabClickListener != null) {
                        FabControllerNew.this.mOnAlarmFabClickListener.onEndFabClick2(view);
                    }
                }
            });
        }
    }

    public void initStopWatchFabViewBtn(TimerButton timerButton, TimerButton timerButton2, TimerButton timerButton3) {
        FabDataHelper.getInstance().initStopWatchFab();
        this.mStopWatchStartBtnWeakRef = new WeakReference<>(timerButton);
        this.mStopWatchEndBtnWeakRef = new WeakReference<>(timerButton2);
        this.mStopWatchCenterBtnWeakRef = new WeakReference<>(timerButton3);
        if (timerButton != null) {
            timerButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.util.fab.FabControllerNew.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Log.d(FabControllerNew.TAG, "stopwatch start btn onClick: mIsInPageChangeAnim" + FabControllerNew.this.mIsInPageChangeAnim + "mIsInStateChangeAnim" + FabControllerNew.this.mIsInStateChangeAnim + "mIsInActionModeChangeAnim" + FabControllerNew.this.mIsInActionModeChangeAnim);
                    if (FabControllerNew.this.mIsInPageChangeAnim || FabControllerNew.this.mIsInStateChangeAnim || FabControllerNew.this.mIsInActionModeChangeAnim) {
                        return;
                    }
                    if (TabViewModel.TAB_STOPWATCH.equals(FabControllerNew.this.mCurrTabInfo.tabName) && FabControllerNew.this.mCurrTabInfo.state == 1) {
                        FabControllerNew.this.vibrate(view, HapticFeedbackConstants.MIUI_MESH_NORMAL);
                    } else {
                        FabControllerNew.vibrate(view);
                    }
                    if (FabControllerNew.this.mOnStopWatchFabClickListener != null) {
                        FabControllerNew.this.mOnStopWatchFabClickListener.onStartFabClick(view);
                    }
                }
            });
        }
        if (timerButton2 != null) {
            timerButton2.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.util.fab.FabControllerNew.4
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Log.d(FabControllerNew.TAG, "stopwatch end btn onClick: mIsInPageChangeAnim" + FabControllerNew.this.mIsInPageChangeAnim + "mIsInStateChangeAnim" + FabControllerNew.this.mIsInStateChangeAnim + "mIsInActionModeChangeAnim" + FabControllerNew.this.mIsInActionModeChangeAnim);
                    if (FabControllerNew.this.mIsInPageChangeAnim || FabControllerNew.this.mIsInStateChangeAnim || FabControllerNew.this.mIsInActionModeChangeAnim) {
                        return;
                    }
                    FabControllerNew.vibrate(view);
                    if (FabControllerNew.this.mOnStopWatchFabClickListener != null) {
                        FabControllerNew.this.mOnStopWatchFabClickListener.onEndFabClick(view);
                    }
                }
            });
        }
        if (timerButton3 != null) {
            timerButton3.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.util.fab.FabControllerNew.5
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Log.d(FabControllerNew.TAG, "stopwatch center btn onClick: mIsInPageChangeAnim" + FabControllerNew.this.mIsInPageChangeAnim + "mIsInStateChangeAnim" + FabControllerNew.this.mIsInStateChangeAnim + "mIsInActionModeChangeAnim" + FabControllerNew.this.mIsInActionModeChangeAnim);
                    if (FabControllerNew.this.mIsInPageChangeAnim || FabControllerNew.this.mIsInStateChangeAnim || FabControllerNew.this.mIsInActionModeChangeAnim) {
                        return;
                    }
                    FabControllerNew.vibrate(view);
                    if (FabControllerNew.this.mOnStopWatchFabClickListener != null) {
                        FabControllerNew.this.mOnStopWatchFabClickListener.onCenterFabClick(view);
                    }
                }
            });
        }
    }

    public void initTimerFabViewBtn(TimerButton timerButton, TimerButton timerButton2, TimerButton timerButton3) {
        FabDataHelper.getInstance().initTimerFab();
        this.mTimerStartBtnWeakRef = new WeakReference<>(timerButton);
        this.mTimerEndBtnWeakRef = new WeakReference<>(timerButton2);
        this.mTimerCenterBtnWeakRef = new WeakReference<>(timerButton3);
        if (timerButton != null) {
            timerButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.util.fab.FabControllerNew.6
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Log.d(FabControllerNew.TAG, "timer start fab onClick: mIsInPageChangeAnim" + FabControllerNew.this.mIsInPageChangeAnim + "mIsInStateChangeAnim" + FabControllerNew.this.mIsInStateChangeAnim + "mIsInActionModeChangeAnim" + FabControllerNew.this.mIsInActionModeChangeAnim);
                    if (FabControllerNew.this.mIsInPageChangeAnim || FabControllerNew.this.mIsInStateChangeAnim || FabControllerNew.this.mIsInActionModeChangeAnim) {
                        return;
                    }
                    if (TabViewModel.TAB_TIMER.equals(FabControllerNew.this.mCurrTabInfo.tabName) && FabControllerNew.this.mCurrTabInfo.state == 1) {
                        FabControllerNew.this.vibrate(view, HapticFeedbackConstants.MIUI_MESH_NORMAL);
                    } else {
                        FabControllerNew.vibrate(view);
                    }
                    if (FabControllerNew.this.mOnTimerFabClickListener != null) {
                        FabControllerNew.this.mOnTimerFabClickListener.onStartFabClick(view);
                    }
                }
            });
        }
        if (timerButton2 != null) {
            timerButton2.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.util.fab.FabControllerNew.7
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Log.d(FabControllerNew.TAG, "timer end fab onClick: mIsInPageChangeAnim" + FabControllerNew.this.mIsInPageChangeAnim + "mIsInStateChangeAnim" + FabControllerNew.this.mIsInStateChangeAnim + "mIsInActionModeChangeAnim" + FabControllerNew.this.mIsInActionModeChangeAnim);
                    if (FabControllerNew.this.mIsInPageChangeAnim || FabControllerNew.this.mIsInStateChangeAnim || FabControllerNew.this.mIsInActionModeChangeAnim || ClickUtils.isFastClick(400L)) {
                        return;
                    }
                    FabControllerNew.vibrate(view);
                    if (FabControllerNew.this.mOnTimerFabClickListener != null) {
                        FabControllerNew.this.mOnTimerFabClickListener.onEndFabClick(view);
                    }
                }
            });
        }
        if (timerButton3 != null) {
            timerButton3.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.util.fab.FabControllerNew.8
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Log.d(FabControllerNew.TAG, "timer center fab onClick: mIsInPageChangeAnim" + FabControllerNew.this.mIsInPageChangeAnim + "mIsInStateChangeAnim" + FabControllerNew.this.mIsInStateChangeAnim + "mIsInActionModeChangeAnim" + FabControllerNew.this.mIsInActionModeChangeAnim);
                    if (FabControllerNew.this.mIsInPageChangeAnim || FabControllerNew.this.mIsInStateChangeAnim || FabControllerNew.this.mIsInActionModeChangeAnim) {
                        return;
                    }
                    FabControllerNew.vibrate(view);
                    if (FabControllerNew.this.mOnTimerFabClickListener != null) {
                        FabControllerNew.this.mOnTimerFabClickListener.onCenterFabClick(view);
                    }
                }
            });
        }
    }

    public void setAlarmInitTab(String str) {
        FabDataHelper.TabInfo tabInfo = FabDataHelper.getTabInfo(str);
        this.mCurrTabInfo = tabInfo;
        showAlarmFab(tabInfo);
        setAlarmContentDescription(str);
    }

    public void setStopWatchInitTab(String str) {
        FabDataHelper.TabInfo tabInfo = FabDataHelper.getTabInfo(str);
        this.mCurrTabInfo = tabInfo;
        showStopWatchFab(tabInfo);
        setStopWatchContentDescription(str);
    }

    public void setTimerInitTab(String str) {
        FabDataHelper.TabInfo tabInfo = FabDataHelper.getTabInfo(str);
        this.mCurrTabInfo = tabInfo;
        showTimerFab(tabInfo);
        setTimerContentDescription(str);
    }

    public void setOnTimerFabClickListener(onTimerFabClickListener ontimerfabclicklistener) {
        this.mOnTimerFabClickListener = ontimerfabclicklistener;
    }

    public void setOnStopWatchFabClickListener(onStopWatchFabClickListener onstopwatchfabclicklistener) {
        this.mOnStopWatchFabClickListener = onstopwatchfabclicklistener;
    }

    public void setOnAlarmFabClickListener(onAlarmFabClickListener onalarmfabclicklistener) {
        this.mOnAlarmFabClickListener = onalarmfabclicklistener;
    }

    public void setCurrTab(String str) {
        this.mLastTabInfo = this.mCurrTabInfo;
        this.mCurrTabInfo = FabDataHelper.getTabInfo(str);
        setAlarmContentDescription(str);
        setStopWatchContentDescription(str);
        setTimerContentDescription(str);
    }

    private void setAlarmContentDescription(String str) {
        FabView fabView = (FabView) getValueFromWeakRef(this.mEndBtn2WeakRef);
        if (!TabViewModel.TAB_ALARM.equals(str) || fabView == null) {
            return;
        }
        fabView.setContentDescription(DeskClockApp.getAppDEContext().getResources().getString(R.string.add_alarm));
    }

    private void setStopWatchContentDescription(String str) {
        if (TabViewModel.TAB_STOPWATCH.equals(str)) {
            TimerButton timerButton = (TimerButton) getValueFromWeakRef(this.mStopWatchStartBtnWeakRef);
            TimerButton timerButton2 = (TimerButton) getValueFromWeakRef(this.mStopWatchEndBtnWeakRef);
            TimerButton timerButton3 = (TimerButton) getValueFromWeakRef(this.mStopWatchCenterBtnWeakRef);
            int i = this.mCurrTabInfo.state;
            if (i == 0) {
                if (timerButton3 != null) {
                    timerButton3.setContentDescription(DeskClockApp.getAppDEContext().getResources().getString(R.string.start));
                }
            } else {
                if (i == 1) {
                    if (timerButton == null || timerButton2 == null) {
                        return;
                    }
                    timerButton.setContentDescription(DeskClockApp.getAppDEContext().getResources().getString(R.string.lap));
                    timerButton2.setContentDescription(DeskClockApp.getAppDEContext().getResources().getString(R.string.pause));
                    return;
                }
                if (i != 2 || timerButton == null || timerButton2 == null) {
                    return;
                }
                timerButton.setContentDescription(DeskClockApp.getAppDEContext().getResources().getString(R.string.reset));
                timerButton2.setContentDescription(DeskClockApp.getAppDEContext().getResources().getString(R.string.timer_continue));
            }
        }
    }

    private void setTimerContentDescription(String str) {
        if (TabViewModel.TAB_TIMER.equals(str)) {
            TimerButton timerButton = (TimerButton) getValueFromWeakRef(this.mTimerStartBtnWeakRef);
            TimerButton timerButton2 = (TimerButton) getValueFromWeakRef(this.mTimerEndBtnWeakRef);
            TimerButton timerButton3 = (TimerButton) getValueFromWeakRef(this.mTimerCenterBtnWeakRef);
            int i = this.mCurrTabInfo.state;
            if (i == 0) {
                if (timerButton3 != null) {
                    timerButton3.setContentDescription(DeskClockApp.getAppDEContext().getResources().getString(R.string.timer_start_timer));
                }
            } else {
                if (i == 1) {
                    if (timerButton == null || timerButton2 == null) {
                        return;
                    }
                    timerButton.setContentDescription(DeskClockApp.getAppDEContext().getResources().getString(R.string.timer_cancel_timer));
                    timerButton2.setContentDescription(DeskClockApp.getAppDEContext().getResources().getString(R.string.timer_pause_timer));
                    return;
                }
                if (i != 2 || timerButton == null || timerButton2 == null) {
                    return;
                }
                timerButton.setContentDescription(DeskClockApp.getAppDEContext().getResources().getString(R.string.timer_cancel_timer));
                timerButton2.setContentDescription(DeskClockApp.getAppDEContext().getResources().getString(R.string.timer_continue_timer));
            }
        }
    }

    public void changeFabWithPageChanged(String str, float f) {
        this.mCurrTabInfo = FabDataHelper.getTabInfo(str);
        if (this.mIsInPageChangeAnim || this.mIsInStateChangeAnim || this.mIsInActionModeChangeAnim) {
            resetAll();
        } else if (!MiuiSdk.isLiteOrMiddleMode()) {
            startFabChangeWithAnim(this.mCurrTabInfo, this.mLastTabInfo, f);
        } else {
            startFabChangeDirectly(this.mCurrTabInfo);
        }
    }

    public void startFabChangeWithAnim(FabDataHelper.TabInfo tabInfo, FabDataHelper.TabInfo tabInfo2, float f) {
        if (tabInfo2 == null) {
            startFabChangeDirectly(tabInfo);
            return;
        }
        Log.d(TAG, "startFabChangeWithAnim currTabInfo: " + tabInfo);
        Log.d(TAG, "lastTabInfo: " + tabInfo2);
        int btnType = getBtnType(tabInfo.tabName);
        if (btnType == getBtnType(tabInfo2.tabName)) {
            if (btnType == TYPE_SINGLE_BTN) {
                changeFabIconWithAnim();
                return;
            } else if (tabInfo.num == tabInfo2.num) {
                changeFabIconWithAnim();
                return;
            } else {
                changeFabAllWithAnim(tabInfo);
                return;
            }
        }
        changeFabAllWithAnim(tabInfo);
    }

    private int getBtnType(String str) {
        if (TabViewModel.TAB_CLOCK.equals(str) || TabViewModel.TAB_ALARM.equals(str)) {
            return TYPE_SINGLE_BTN;
        }
        return TYPE_DOUBLE_BTN;
    }

    public void changeFabIconWithAnim() {
        if (this.mIsInStateChangeAnim || this.mIsInPageChangeAnim || this.mIsInActionModeChangeAnim) {
            resetAll();
            return;
        }
        ValueAnimator valueAnimator = this.resetAnimator1;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.resetAnimator1.removeAllUpdateListeners();
            this.resetAnimator1.removeAllListeners();
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.resetAnimator1 = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setDuration(350L);
        this.resetAnimator1.setStartDelay(0L);
        this.resetAnimator1.setInterpolator(MiuiFolme.getFabResetInterpolator());
        this.resetAnimator1.addListener(new AnimationListener1(this));
        this.resetAnimator1.start();
        startFabChangeDirectly(this.mCurrTabInfo);
    }

    private static class AnimationListener1 extends AnimationUtils.AnimatorListenerAdapter {
        WeakReference<FabControllerNew> fabControllerNewWeakReference;

        public AnimationListener1(FabControllerNew fabControllerNew) {
            this.fabControllerNewWeakReference = new WeakReference<>(fabControllerNew);
        }

        @Override // com.android.deskclock.util.AnimationUtils.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            WeakReference<FabControllerNew> weakReference = this.fabControllerNewWeakReference;
            FabControllerNew fabControllerNew = weakReference == null ? null : weakReference.get();
            if (fabControllerNew != null) {
                fabControllerNew.onAnimationStart1();
            }
        }

        @Override // com.android.deskclock.util.AnimationUtils.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
            super.onAnimationCancel(animator);
            WeakReference<FabControllerNew> weakReference = this.fabControllerNewWeakReference;
            FabControllerNew fabControllerNew = weakReference == null ? null : weakReference.get();
            if (fabControllerNew != null) {
                fabControllerNew.onAnimationCancel1();
            }
        }

        @Override // com.android.deskclock.util.AnimationUtils.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            super.onAnimationEnd(animator);
            WeakReference<FabControllerNew> weakReference = this.fabControllerNewWeakReference;
            FabControllerNew fabControllerNew = weakReference == null ? null : weakReference.get();
            if (fabControllerNew != null) {
                fabControllerNew.onAnimationEnd1();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAnimationStart1() {
        this.mIsInPageChangeAnim = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAnimationCancel1() {
        startFabChangeDirectly(this.mCurrTabInfo);
        this.mIsInPageChangeAnim = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAnimationEnd1() {
        resetFabScaleAndAlpha((TimerButton) getValueFromWeakRef(this.mStopWatchStartBtnWeakRef), (TimerButton) getValueFromWeakRef(this.mStopWatchEndBtnWeakRef), (TimerButton) getValueFromWeakRef(this.mStopWatchCenterBtnWeakRef), (TimerButton) getValueFromWeakRef(this.mTimerStartBtnWeakRef), (TimerButton) getValueFromWeakRef(this.mTimerEndBtnWeakRef), (TimerButton) getValueFromWeakRef(this.mTimerCenterBtnWeakRef), (FabView) getValueFromWeakRef(this.mEndBtn2WeakRef));
        this.mIsInPageChangeAnim = false;
    }

    private void changeFabAllWithAnim(FabDataHelper.TabInfo tabInfo) {
        if (this.mIsInStateChangeAnim || this.mIsInPageChangeAnim || this.mIsInActionModeChangeAnim) {
            resetAll();
            return;
        }
        ValueAnimator valueAnimator = this.resetAnimator2;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.resetAnimator2.removeAllUpdateListeners();
            this.resetAnimator2.removeAllListeners();
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.resetAnimator2 = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setDuration(350L);
        this.resetAnimator2.setStartDelay(0L);
        this.resetAnimator2.setInterpolator(MiuiFolme.getFabResetInterpolator());
        this.resetAnimator2.addListener(new AnimationListener2(this, tabInfo));
        this.resetAnimator2.start();
        startFabChangeDirectly(tabInfo);
    }

    private static class AnimationListener2 implements Animator.AnimatorListener {
        WeakReference<FabControllerNew> fabControllerNewWeakReference;
        FabDataHelper.TabInfo tabInfo;

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        public AnimationListener2(FabControllerNew fabControllerNew, FabDataHelper.TabInfo tabInfo) {
            this.tabInfo = tabInfo;
            this.fabControllerNewWeakReference = new WeakReference<>(fabControllerNew);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            WeakReference<FabControllerNew> weakReference = this.fabControllerNewWeakReference;
            FabControllerNew fabControllerNew = weakReference == null ? null : weakReference.get();
            if (fabControllerNew != null) {
                fabControllerNew.onAnimationStart2(this.tabInfo);
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            WeakReference<FabControllerNew> weakReference = this.fabControllerNewWeakReference;
            FabControllerNew fabControllerNew = weakReference == null ? null : weakReference.get();
            if (fabControllerNew != null) {
                fabControllerNew.onAnimationEnd2(animator);
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
            WeakReference<FabControllerNew> weakReference = this.fabControllerNewWeakReference;
            FabControllerNew fabControllerNew = weakReference == null ? null : weakReference.get();
            if (fabControllerNew != null) {
                fabControllerNew.onAnimationCancel2(animator);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAnimationStart2(FabDataHelper.TabInfo tabInfo) {
        this.mIsInPageChangeAnim = true;
        if (getBtnType(tabInfo.tabName) == TYPE_DOUBLE_BTN) {
            if (TabViewModel.TAB_STOPWATCH.equals(tabInfo.tabName)) {
                TimerButton timerButton = (TimerButton) getValueFromWeakRef(this.mStopWatchStartBtnWeakRef);
                TimerButton timerButton2 = (TimerButton) getValueFromWeakRef(this.mStopWatchEndBtnWeakRef);
                TimerButton timerButton3 = (TimerButton) getValueFromWeakRef(this.mStopWatchCenterBtnWeakRef);
                if (timerButton == null || timerButton2 == null || timerButton3 == null) {
                    return;
                }
                if (tabInfo.num == 1) {
                    timerButton3.setVisibility(0);
                    timerButton3.setImageResource(tabInfo.getCenterImageId());
                    timerButton.setVisibility(8);
                    timerButton2.setVisibility(8);
                    return;
                }
                timerButton3.setVisibility(8);
                timerButton.setImageResource(tabInfo.getStartImageId());
                timerButton.setVisibility(0);
                timerButton2.setImageResource(tabInfo.getEndImageId());
                timerButton2.setVisibility(0);
                return;
            }
            if (TabViewModel.TAB_TIMER.equals(tabInfo.tabName)) {
                TimerButton timerButton4 = (TimerButton) getValueFromWeakRef(this.mTimerStartBtnWeakRef);
                TimerButton timerButton5 = (TimerButton) getValueFromWeakRef(this.mTimerEndBtnWeakRef);
                TimerButton timerButton6 = (TimerButton) getValueFromWeakRef(this.mTimerCenterBtnWeakRef);
                if (timerButton4 == null || timerButton5 == null || timerButton6 == null) {
                    return;
                }
                if (tabInfo.num == 1) {
                    timerButton6.setVisibility(0);
                    timerButton6.setImageResource(tabInfo.getCenterImageId());
                    timerButton4.setVisibility(8);
                    timerButton5.setVisibility(8);
                    return;
                }
                timerButton6.setVisibility(8);
                timerButton4.setImageResource(tabInfo.getStartImageId());
                timerButton4.setVisibility(0);
                timerButton5.setImageResource(tabInfo.getEndImageId());
                timerButton5.setVisibility(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAnimationEnd2(Animator animator) {
        this.mIsInPageChangeAnim = false;
        TimerButton timerButton = (TimerButton) getValueFromWeakRef(this.mStopWatchStartBtnWeakRef);
        TimerButton timerButton2 = (TimerButton) getValueFromWeakRef(this.mStopWatchEndBtnWeakRef);
        TimerButton timerButton3 = (TimerButton) getValueFromWeakRef(this.mStopWatchCenterBtnWeakRef);
        TimerButton timerButton4 = (TimerButton) getValueFromWeakRef(this.mTimerStartBtnWeakRef);
        TimerButton timerButton5 = (TimerButton) getValueFromWeakRef(this.mTimerEndBtnWeakRef);
        TimerButton timerButton6 = (TimerButton) getValueFromWeakRef(this.mTimerCenterBtnWeakRef);
        resetFabScaleAndAlpha(timerButton, timerButton2, timerButton3, timerButton4, timerButton5, timerButton6, (FabView) getValueFromWeakRef(this.mEndBtn2WeakRef));
        if (getBtnType(this.mCurrTabInfo.tabName) == TYPE_DOUBLE_BTN) {
            if (TabViewModel.TAB_STOPWATCH.equals(this.mCurrTabInfo.tabName)) {
                if (timerButton == null || timerButton2 == null || timerButton3 == null) {
                    return;
                }
                if (this.mCurrTabInfo.num == 2) {
                    timerButton3.setVisibility(8);
                    timerButton.setVisibility(0);
                    timerButton2.setVisibility(0);
                    return;
                } else {
                    timerButton3.setVisibility(0);
                    timerButton.setVisibility(8);
                    timerButton2.setVisibility(8);
                    return;
                }
            }
            if (!TabViewModel.TAB_TIMER.equals(this.mCurrTabInfo.tabName) || timerButton4 == null || timerButton5 == null || timerButton6 == null) {
                return;
            }
            if (this.mCurrTabInfo.num == 2) {
                timerButton6.setVisibility(8);
                timerButton4.setVisibility(0);
                timerButton5.setVisibility(0);
            } else {
                timerButton6.setVisibility(0);
                timerButton4.setVisibility(8);
                timerButton5.setVisibility(8);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAnimationCancel2(Animator animator) {
        this.mIsInPageChangeAnim = false;
        startFabChangeDirectly(this.mCurrTabInfo);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startFabChangeDirectly(FabDataHelper.TabInfo tabInfo) {
        if (tabInfo == null) {
            return;
        }
        TimerButton timerButton = (TimerButton) getValueFromWeakRef(this.mStopWatchStartBtnWeakRef);
        TimerButton timerButton2 = (TimerButton) getValueFromWeakRef(this.mStopWatchEndBtnWeakRef);
        TimerButton timerButton3 = (TimerButton) getValueFromWeakRef(this.mStopWatchCenterBtnWeakRef);
        TimerButton timerButton4 = (TimerButton) getValueFromWeakRef(this.mTimerStartBtnWeakRef);
        TimerButton timerButton5 = (TimerButton) getValueFromWeakRef(this.mTimerEndBtnWeakRef);
        TimerButton timerButton6 = (TimerButton) getValueFromWeakRef(this.mTimerCenterBtnWeakRef);
        resetFabScaleAndAlpha(timerButton, timerButton2, timerButton3, timerButton4, timerButton5, timerButton6, (FabView) getValueFromWeakRef(this.mEndBtn2WeakRef));
        if (TabViewModel.TAB_STOPWATCH.equals(tabInfo.tabName)) {
            if (timerButton == null || timerButton2 == null || timerButton3 == null) {
                return;
            }
            if (tabInfo.num == 1) {
                timerButton3.setImageResource(tabInfo.imageIds[1]);
                timerButton3.setVisibility(0);
                timerButton.setVisibility(8);
                timerButton2.setVisibility(8);
                return;
            }
            timerButton3.setVisibility(8);
            timerButton.setImageResource(tabInfo.imageIds[0]);
            timerButton.setVisibility(0);
            timerButton2.setImageResource(tabInfo.imageIds[2]);
            timerButton2.setVisibility(0);
            return;
        }
        if (!TabViewModel.TAB_TIMER.equals(tabInfo.tabName) || timerButton4 == null || timerButton5 == null || timerButton6 == null) {
            return;
        }
        if (tabInfo.num == 1) {
            timerButton6.setImageResource(tabInfo.imageIds[1]);
            timerButton6.setVisibility(0);
            timerButton4.setVisibility(8);
            timerButton5.setVisibility(8);
            return;
        }
        timerButton6.setVisibility(8);
        timerButton4.setImageResource(tabInfo.imageIds[0]);
        timerButton4.setVisibility(0);
        timerButton5.setImageResource(tabInfo.imageIds[2]);
        timerButton5.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void resetFabScaleAndAlpha(ImageButton... imageButtonArr) {
        for (ImageButton imageButton : imageButtonArr) {
            if (imageButton == null) {
                return;
            }
            imageButton.setScaleX(1.0f);
            imageButton.setScaleY(1.0f);
            imageButton.setAlpha(1.0f);
            imageButton.setImageAlpha(255);
        }
    }

    private void showAlarmFab(FabDataHelper.TabInfo tabInfo) {
        FabView fabView = (FabView) getValueFromWeakRef(this.mEndBtn2WeakRef);
        if (fabView != null) {
            fabView.setVisibility(0);
        }
    }

    private void showStopWatchFab(FabDataHelper.TabInfo tabInfo) {
        TimerButton timerButton = (TimerButton) getValueFromWeakRef(this.mStopWatchStartBtnWeakRef);
        TimerButton timerButton2 = (TimerButton) getValueFromWeakRef(this.mStopWatchEndBtnWeakRef);
        TimerButton timerButton3 = (TimerButton) getValueFromWeakRef(this.mStopWatchCenterBtnWeakRef);
        if (timerButton == null || timerButton2 == null || timerButton3 == null || tabInfo == null) {
            return;
        }
        if (tabInfo.num == 1) {
            timerButton3.setImageResource(tabInfo.imageIds[1]);
            timerButton3.setVisibility(0);
            timerButton.setVisibility(8);
            timerButton2.setVisibility(8);
            return;
        }
        timerButton3.setVisibility(8);
        timerButton.setImageResource(tabInfo.imageIds[0]);
        timerButton.setVisibility(0);
        timerButton2.setImageResource(tabInfo.imageIds[2]);
        timerButton2.setVisibility(0);
    }

    private void showTimerFab(FabDataHelper.TabInfo tabInfo) {
        TimerButton timerButton = (TimerButton) getValueFromWeakRef(this.mTimerStartBtnWeakRef);
        TimerButton timerButton2 = (TimerButton) getValueFromWeakRef(this.mTimerEndBtnWeakRef);
        TimerButton timerButton3 = (TimerButton) getValueFromWeakRef(this.mTimerCenterBtnWeakRef);
        if (timerButton == null || timerButton2 == null || timerButton3 == null || tabInfo == null) {
            return;
        }
        if (tabInfo.num == 1) {
            timerButton3.setImageResource(tabInfo.imageIds[1]);
            timerButton3.setVisibility(0);
            timerButton.setVisibility(8);
            timerButton2.setVisibility(8);
            return;
        }
        timerButton3.setVisibility(8);
        timerButton.setImageResource(tabInfo.imageIds[0]);
        timerButton.setVisibility(0);
        timerButton2.setImageResource(tabInfo.imageIds[2]);
        timerButton2.setVisibility(0);
    }

    protected static void vibrate(View view) {
        if (mSupportLinearMotorVibrate) {
            try {
                HapticCompat.performHapticFeedback(view, HapticFeedbackConstants.MIUI_BUTTON_MIDDLE, HapticFeedbackConstants.MIUI_TAP_NORMAL);
            } catch (Exception e) {
                com.android.deskclock.util.Log.e("DC:ClockFragment", "doVibrate error: " + e.getMessage());
            }
        }
    }

    protected void vibrate(View view, int i) {
        if (mSupportLinearMotorVibrate) {
            try {
                HapticCompat.performHapticFeedback(view, HapticFeedbackConstants.MIUI_BUTTON_MIDDLE, i);
            } catch (Exception e) {
                com.android.deskclock.util.Log.e("DC:ClockFragment", "doVibrate error: " + e.getMessage());
            }
        }
    }

    @Override // com.android.deskclock.util.fab.FabDataHelper.DataChangeListener
    public void onFabDataChanged(FabDataHelper.TabInfo tabInfo) {
        FabDataHelper.TabInfo tabInfo2 = this.mCurrTabInfo;
        if (tabInfo2 == null || tabInfo == null || tabInfo2.tabName == null || tabInfo.tabName == null || !this.mCurrTabInfo.tabName.equals(tabInfo.tabName)) {
            return;
        }
        if (this.mIsInStateChangeAnim || this.mIsInPageChangeAnim || this.mIsInActionModeChangeAnim) {
            resetAll();
            return;
        }
        if (tabInfo.tabName.equals(TabViewModel.TAB_STOPWATCH)) {
            TimerButton timerButton = (TimerButton) getValueFromWeakRef(this.mStopWatchStartBtnWeakRef);
            TimerButton timerButton2 = (TimerButton) getValueFromWeakRef(this.mStopWatchEndBtnWeakRef);
            TimerButton timerButton3 = (TimerButton) getValueFromWeakRef(this.mStopWatchCenterBtnWeakRef);
            if (timerButton == null || timerButton2 == null || timerButton3 == null) {
                return;
            }
            if (tabInfo.num == this.mCurrTabInfo.num) {
                if (this.mCurrTabInfo.num == 1) {
                    timerButton3.setImageResource(this.mCurrTabInfo.getCenterImageId());
                } else {
                    timerButton.setImageResource(this.mCurrTabInfo.getStartImageId());
                    timerButton2.setImageResource(this.mCurrTabInfo.getEndImageId());
                }
            } else if (this.mCurrTabInfo.num == 1) {
                timerButton3.setImageResource(this.mCurrTabInfo.getCenterImageId());
                if (Util.isRtl()) {
                    timerButton3.setVisibility(0);
                    timerButton.setVisibility(8);
                    timerButton2.setVisibility(8);
                } else {
                    timerButton3.setAlpha(0.0f);
                    MiuiFolme.hideFab(timerButton, null);
                    MiuiFolme.hideFab(timerButton2, null);
                    MiuiFolme.showFab(timerButton3, this.mInnerStateChangeListener);
                }
            } else {
                timerButton.setImageResource(this.mCurrTabInfo.getStartImageId());
                timerButton2.setImageResource(this.mCurrTabInfo.getEndImageId());
                if (Util.isRtl()) {
                    timerButton.setVisibility(0);
                    timerButton2.setVisibility(0);
                    timerButton3.setVisibility(8);
                } else {
                    timerButton.setAlpha(0.0f);
                    timerButton2.setAlpha(0.0f);
                    MiuiFolme.hideFab(timerButton3, null);
                    MiuiFolme.showFab(timerButton, this.mInnerStateChangeListener);
                    MiuiFolme.showFab(timerButton2, this.mInnerStateChangeListener);
                }
            }
            setStopWatchContentDescription(tabInfo.tabName);
            return;
        }
        if (tabInfo.tabName.equals(TabViewModel.TAB_TIMER)) {
            TimerButton timerButton4 = (TimerButton) getValueFromWeakRef(this.mTimerStartBtnWeakRef);
            TimerButton timerButton5 = (TimerButton) getValueFromWeakRef(this.mTimerEndBtnWeakRef);
            TimerButton timerButton6 = (TimerButton) getValueFromWeakRef(this.mTimerCenterBtnWeakRef);
            if (timerButton4 == null || timerButton5 == null || timerButton6 == null) {
                return;
            }
            if (tabInfo.num == this.mCurrTabInfo.num) {
                if (this.mCurrTabInfo.num == 1) {
                    timerButton6.setImageResource(this.mCurrTabInfo.getCenterImageId());
                } else {
                    timerButton4.setImageResource(this.mCurrTabInfo.getStartImageId());
                    timerButton5.setImageResource(this.mCurrTabInfo.getEndImageId());
                }
            } else if (this.mCurrTabInfo.num == 1) {
                timerButton6.setImageResource(this.mCurrTabInfo.getCenterImageId());
                if (Util.isRtl()) {
                    timerButton6.setVisibility(0);
                    timerButton4.setVisibility(8);
                    timerButton5.setVisibility(8);
                } else {
                    timerButton6.setAlpha(0.0f);
                    MiuiFolme.hideFab(timerButton4, null);
                    MiuiFolme.hideFab(timerButton5, null);
                    MiuiFolme.showFab(timerButton6, this.mInnerStateChangeListener);
                }
            } else {
                timerButton4.setImageResource(this.mCurrTabInfo.getStartImageId());
                timerButton5.setImageResource(this.mCurrTabInfo.getEndImageId());
                if (Util.isRtl()) {
                    timerButton4.setVisibility(0);
                    timerButton5.setVisibility(0);
                    timerButton6.setVisibility(8);
                } else {
                    timerButton4.setAlpha(0.0f);
                    timerButton5.setAlpha(0.0f);
                    MiuiFolme.hideFab(timerButton6, null);
                    MiuiFolme.showFab(timerButton4, this.mInnerStateChangeListener);
                    MiuiFolme.showFab(timerButton5, this.mInnerStateChangeListener);
                }
            }
            setTimerContentDescription(tabInfo.tabName);
        }
    }

    public void changeBtnAlpha(boolean z) {
        final FabView fabView = (FabView) getValueFromWeakRef(this.mEndBtn2WeakRef);
        if (fabView == null) {
            return;
        }
        if (z) {
            MiuiFolme.animShow(fabView, 1.0f, new TransitionListener() { // from class: com.android.deskclock.util.fab.FabControllerNew.9
                @Override // miuix.animation.listener.TransitionListener
                public void onBegin(Object obj) {
                    FabControllerNew.this.mIsInActionModeChangeAnim = true;
                }

                @Override // miuix.animation.listener.TransitionListener
                public void onComplete(Object obj) {
                    super.onComplete(obj);
                    FabView fabView2 = fabView;
                    if (fabView2 != null) {
                        fabView2.setVisibility(0);
                    }
                    FabControllerNew.this.mIsInActionModeChangeAnim = false;
                }

                @Override // miuix.animation.listener.TransitionListener
                public void onCancel(Object obj) {
                    super.onCancel(obj);
                    FabControllerNew.this.mIsInActionModeChangeAnim = false;
                }
            });
        } else {
            MiuiFolme.animHide(fabView, 0.6f, new TransitionListener() { // from class: com.android.deskclock.util.fab.FabControllerNew.10
                @Override // miuix.animation.listener.TransitionListener
                public void onBegin(Object obj) {
                    FabControllerNew.this.mIsInActionModeChangeAnim = true;
                }

                @Override // miuix.animation.listener.TransitionListener
                public void onComplete(Object obj) {
                    super.onComplete(obj);
                    FabView fabView2 = fabView;
                    if (fabView2 != null) {
                        fabView2.setVisibility(8);
                    }
                    FabControllerNew.this.mIsInActionModeChangeAnim = false;
                }

                @Override // miuix.animation.listener.TransitionListener
                public void onCancel(Object obj) {
                    super.onCancel(obj);
                    FabControllerNew.this.mIsInActionModeChangeAnim = false;
                }
            });
        }
    }

    @Override // com.android.deskclock.util.fab.FabDataHelper.DataChangeListener
    public void onFabEnableChange(FabDataHelper.TabInfo tabInfo, boolean z) {
        TimerButton timerButton = (TimerButton) getValueFromWeakRef(this.mTimerCenterBtnWeakRef);
        if (z && timerButton != null) {
            timerButton.setEnabled(true);
        }
        FabDataHelper.TabInfo tabInfo2 = this.mCurrTabInfo;
        if (tabInfo2 == null || tabInfo == null) {
            return;
        }
        if ((tabInfo2.tabName == null || (this.mCurrTabInfo.tabName.equals(tabInfo.tabName) && TabViewModel.TAB_TIMER.equals(this.mCurrTabInfo.tabName))) && timerButton != null) {
            timerButton.setEnabled(z);
            timerButton.setImageResource(this.mCurrTabInfo.getCenterImageId());
        }
    }

    public void setMarginEnd(int i) {
        FabView fabView = (FabView) getValueFromWeakRef(this.mEndBtn2WeakRef);
        if (fabView == null) {
            return;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fabView.getLayoutParams();
        layoutParams.setMarginEnd(i);
        fabView.setLayoutParams(layoutParams);
    }

    public String getLastTab() {
        return this.mLastTabInfo.tabName;
    }

    public void resetAll() {
        this.mIsInStateChangeAnim = false;
        this.mIsInPageChangeAnim = false;
        this.mIsInActionModeChangeAnim = false;
        ValueAnimator valueAnimator = this.resetAnimator2;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.resetAnimator2.removeAllUpdateListeners();
            this.resetAnimator2.removeAllListeners();
            this.resetAnimator2 = null;
        }
        ValueAnimator valueAnimator2 = this.resetAnimator1;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
            this.resetAnimator1.removeAllUpdateListeners();
            this.resetAnimator1.removeAllListeners();
            this.resetAnimator1 = null;
        }
        startFabChangeDirectly(this.mLastTabInfo);
    }

    public void resetAnimChangeFlag() {
        this.mIsInStateChangeAnim = false;
        this.mIsInPageChangeAnim = false;
        this.mIsInActionModeChangeAnim = false;
    }

    public void destroy() {
        this.mIsInStateChangeAnim = false;
        this.mIsInPageChangeAnim = false;
        this.mIsInActionModeChangeAnim = false;
        ValueAnimator valueAnimator = this.resetAnimator2;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.resetAnimator2.removeAllUpdateListeners();
            this.resetAnimator2.removeAllListeners();
            this.resetAnimator2 = null;
        }
        ValueAnimator valueAnimator2 = this.resetAnimator1;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
            this.resetAnimator1.removeAllUpdateListeners();
            this.resetAnimator1.removeAllListeners();
            this.resetAnimator1 = null;
        }
        TimerButton timerButton = (TimerButton) getValueFromWeakRef(this.mStopWatchStartBtnWeakRef);
        TimerButton timerButton2 = (TimerButton) getValueFromWeakRef(this.mStopWatchEndBtnWeakRef);
        TimerButton timerButton3 = (TimerButton) getValueFromWeakRef(this.mStopWatchCenterBtnWeakRef);
        TimerButton timerButton4 = (TimerButton) getValueFromWeakRef(this.mTimerStartBtnWeakRef);
        TimerButton timerButton5 = (TimerButton) getValueFromWeakRef(this.mTimerEndBtnWeakRef);
        TimerButton timerButton6 = (TimerButton) getValueFromWeakRef(this.mTimerCenterBtnWeakRef);
        FabView fabView = (FabView) getValueFromWeakRef(this.mEndBtn2WeakRef);
        MiuiFolme.cleanFolme(timerButton, timerButton2, timerButton3, timerButton4, timerButton5, timerButton6, fabView);
        if (timerButton != null && timerButton2 != null && timerButton3 != null) {
            timerButton.clearAnimation();
            timerButton2.clearAnimation();
            timerButton3.clearAnimation();
        }
        if (timerButton4 != null && timerButton5 != null && timerButton6 != null) {
            timerButton4.clearAnimation();
            timerButton5.clearAnimation();
            timerButton6.clearAnimation();
        }
        if (fabView != null) {
            fabView.clearAnimation();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T> T getValueFromWeakRef(WeakReference<T> weakReference) {
        if (weakReference == null) {
            return null;
        }
        return weakReference.get();
    }
}
