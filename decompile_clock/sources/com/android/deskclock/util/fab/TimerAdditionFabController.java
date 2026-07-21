package com.android.deskclock.util.fab;

import android.animation.FloatEvaluator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.R;
import com.android.deskclock.R2;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.timer.Timer;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.util.AnimationUtils;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.GestureLineUtil;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.ResponsiveUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.view.tab.TabViewModel;
import miuix.popupwidget.widget.GuidePopupWindow;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes.dex */
public class TimerAdditionFabController {
    private static final String KEY_SHOWED_GUIDE_WINDOW = "key_showed_guide_window";
    private DeskClockTabActivity mActivity;
    private ImageButton mCommonUsedBtn;
    private String mCurTab;
    private boolean mIsInAnim;
    private String mLastTab;
    onTimerAdditionFabClickListener mListener;
    private int mState;
    private GuidePopupWindow mWhiteNoiseGuideWindow;
    private ImageButton mWhiteNoiseTabBtn;
    private static TimerAdditionFabController mAdditionFabController = new TimerAdditionFabController();
    private static int[] WHITE_NOISE_BTN_ICON_NORMAL = {R.drawable.ic_white_noise_type_flag_n, R.drawable.ic_white_noise_type_forest_n, R.drawable.ic_white_noise_type_summer_night_n, R.drawable.ic_white_noise_type_beach_n, R.drawable.ic_white_noise_type_spring_rain_n, R.drawable.ic_white_noise_type_stove_fire_n};
    private static int[] WHITE_NOISE_BTN_ICON_CHECKED = {R.drawable.ic_white_noise_type_flag_checked, R.drawable.ic_white_noise_type_forest_checked, R.drawable.ic_white_noise_type_summer_night_checked, R.drawable.ic_white_noise_type_beach_checked, R.drawable.ic_white_noise_type_spring_rain_checked, R.drawable.ic_white_noise_type_stove_fire_checked};
    private FloatEvaluator evaluator = new FloatEvaluator();
    private boolean mHideBtnInHalfMode = false;
    private MiuiFolme.ClockTransitionListener clockTransitionListener = new MiuiFolme.ClockTransitionListener() { // from class: com.android.deskclock.util.fab.TimerAdditionFabController.3
        @Override // miuix.animation.listener.TransitionListener
        public void onBegin(Object obj) {
            super.onBegin(obj);
            TimerAdditionFabController.this.mIsInAnim = true;
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onCancel(Object obj) {
            super.onCancel(obj);
            MiuiFolme.cleanFolme(TimerAdditionFabController.this.mWhiteNoiseTabBtn, TimerAdditionFabController.this.mCommonUsedBtn);
            MiuiFolme.resetFabTouch(TimerAdditionFabController.this.mWhiteNoiseTabBtn, TimerAdditionFabController.this.mCommonUsedBtn);
            TimerAdditionFabController.resetFabScaleAndAlpha(TimerAdditionFabController.this.mWhiteNoiseTabBtn, TimerAdditionFabController.this.mCommonUsedBtn);
            TimerAdditionFabController.this.resetVisibleState();
            TimerAdditionFabController.this.mIsInAnim = false;
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj) {
            super.onComplete(obj);
            MiuiFolme.cleanFolme(TimerAdditionFabController.this.mWhiteNoiseTabBtn, TimerAdditionFabController.this.mCommonUsedBtn);
            MiuiFolme.resetFabTouch(TimerAdditionFabController.this.mWhiteNoiseTabBtn, TimerAdditionFabController.this.mCommonUsedBtn);
            TimerAdditionFabController.resetFabScaleAndAlpha(TimerAdditionFabController.this.mWhiteNoiseTabBtn, TimerAdditionFabController.this.mCommonUsedBtn);
            TimerAdditionFabController.this.mIsInAnim = false;
        }
    };
    private boolean mIsWhiteNoiseChecked = false;
    private int mWhiteNoiseCheckedPosition = 0;

    public interface onTimerAdditionFabClickListener {
        void onCommonTimerFabClick(View view);

        void onWhiteNoiseFabClick(View view);
    }

    private boolean canShow(int i) {
        return (i == 2 || i == 1) ? false : true;
    }

    private TimerAdditionFabController() {
    }

    public void init(ImageButton imageButton, ImageButton imageButton2, DeskClockTabActivity deskClockTabActivity) {
        this.mWhiteNoiseTabBtn = imageButton;
        this.mCommonUsedBtn = imageButton2;
        this.mActivity = deskClockTabActivity;
        Timer timer = TimerDao.getTimer(DeskClockApp.getAppDEContext());
        if (timer != null) {
            this.mState = timer.getState();
        } else {
            this.mState = 0;
        }
        this.mWhiteNoiseTabBtn.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.util.fab.TimerAdditionFabController.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (TimerAdditionFabController.this.mIsInAnim) {
                    return;
                }
                FabControllerNew.vibrate(view);
                if (TimerAdditionFabController.this.mListener != null) {
                    TimerAdditionFabController.this.mListener.onWhiteNoiseFabClick(view);
                }
            }
        });
        this.mCommonUsedBtn.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.util.fab.TimerAdditionFabController.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (TimerAdditionFabController.this.mIsInAnim) {
                    return;
                }
                FabControllerNew.vibrate(view);
                if (TimerAdditionFabController.this.mListener != null) {
                    TimerAdditionFabController.this.mListener.onCommonTimerFabClick(view);
                }
            }
        });
    }

    public void setBottomMargin(int i) {
        ImageButton imageButton = this.mWhiteNoiseTabBtn;
        if (imageButton == null || this.mCommonUsedBtn == null) {
            return;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageButton.getLayoutParams();
        layoutParams.bottomMargin = i;
        this.mWhiteNoiseTabBtn.setLayoutParams(layoutParams);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mCommonUsedBtn.getLayoutParams();
        layoutParams2.bottomMargin = i;
        this.mCommonUsedBtn.setLayoutParams(layoutParams2);
    }

    public void setStartMargin(Activity activity) {
        float dimension;
        float dimension2;
        if (Util.isDeviceCetus()) {
            dimension = DeskClockApp.getAppContext().getResources().getDimension(R.dimen.comm_timer_btn_cetus_margin_start);
        } else {
            dimension = PadAdapterUtil.IS_PAD ? activity.getResources().getDimension(R.dimen.comm_timer_btn_pad_margin_start) : activity.getResources().getDimension(R.dimen.comm_timer_btn_margin_start);
        }
        int i = (int) dimension;
        if (PadAdapterUtil.IS_PAD && !Util.isWideMode(activity)) {
            dimension2 = DeskClockApp.getAppContext().getResources().getDimension(R.dimen.comm_timer_btn_not_wide_margin_start);
        } else {
            if (PadAdapterUtil.IS_PAD && (Util.isFreeFormScreen(activity.getResources().getConfiguration()) || activity.isInMultiWindowMode())) {
                dimension2 = DeskClockApp.getAppContext().getResources().getDimension(R.dimen.comm_timer_btn_margin_start);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mCommonUsedBtn.getLayoutParams();
            layoutParams.setMarginEnd(i);
            this.mCommonUsedBtn.setLayoutParams(layoutParams);
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mWhiteNoiseTabBtn.getLayoutParams();
            layoutParams2.setMarginStart(i);
            this.mWhiteNoiseTabBtn.setLayoutParams(layoutParams2);
        }
        i = (int) dimension2;
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.mCommonUsedBtn.getLayoutParams();
        layoutParams3.setMarginEnd(i);
        this.mCommonUsedBtn.setLayoutParams(layoutParams3);
        FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.mWhiteNoiseTabBtn.getLayoutParams();
        layoutParams4.setMarginStart(i);
        this.mWhiteNoiseTabBtn.setLayoutParams(layoutParams4);
    }

    public void setCurrTab(String str) {
        if (this.mWhiteNoiseTabBtn == null || this.mCommonUsedBtn == null) {
            return;
        }
        String str2 = this.mCurTab;
        this.mLastTab = str2;
        this.mCurTab = str;
        if (str2 == null) {
            resetVisibleState();
        }
        if (TabViewModel.TAB_TIMER.equals(this.mCurTab) && !TabViewModel.TAB_TIMER.equals(this.mLastTab) && this.mState == 0) {
            showBtn();
            return;
        }
        if (!TabViewModel.TAB_TIMER.equals(this.mCurTab) && TabViewModel.TAB_TIMER.equals(this.mLastTab) && this.mState == 0) {
            hideBtn();
        } else if (this.mState == 2) {
            this.mWhiteNoiseTabBtn.setVisibility(8);
            this.mCommonUsedBtn.setVisibility(8);
        }
    }

    private void showBtn() {
        ImageButton imageButton;
        ImageButton imageButton2 = this.mWhiteNoiseTabBtn;
        if (imageButton2 == null || (imageButton = this.mCommonUsedBtn) == null) {
            return;
        }
        if (this.mIsInAnim) {
            MiuiFolme.cleanFolme(imageButton2, imageButton);
            MiuiFolme.resetFabTouch(this.mWhiteNoiseTabBtn, this.mCommonUsedBtn);
            resetFabScaleAndAlpha(this.mWhiteNoiseTabBtn, this.mCommonUsedBtn);
            resetVisibleState();
            this.mIsInAnim = false;
        }
        if (this.mHideBtnInHalfMode) {
            return;
        }
        this.mWhiteNoiseTabBtn.setVisibility(0);
        this.mCommonUsedBtn.setVisibility(0);
        if (MiuiSdk.isLiteOrMiddleMode()) {
            return;
        }
        MiuiFolme.showFab(this.mWhiteNoiseTabBtn, null);
        MiuiFolme.showFab(this.mCommonUsedBtn, this.clockTransitionListener);
    }

    public void handleTimerState(int i) {
        if (this.mWhiteNoiseTabBtn == null || this.mCommonUsedBtn == null || !TabViewModel.TAB_TIMER.equals(this.mCurTab)) {
            this.mState = i;
            return;
        }
        if (canShow(i) == canShow(this.mState)) {
            resetVisibleState();
            return;
        }
        this.mState = i;
        if (i == 1) {
            if (!MiuiSdk.isLiteOrMiddleMode()) {
                hideBtn();
                return;
            } else {
                AnimationUtils.hideView(this.mCommonUsedBtn, this.mWhiteNoiseTabBtn);
                return;
            }
        }
        if (i == 2) {
            MiuiFolme.cleanFolme(this.mWhiteNoiseTabBtn, this.mCommonUsedBtn);
            MiuiFolme.resetFabTouch(this.mWhiteNoiseTabBtn, this.mCommonUsedBtn);
            resetFabScaleAndAlpha(this.mWhiteNoiseTabBtn, this.mCommonUsedBtn);
            AnimationUtils.hideView(this.mCommonUsedBtn, this.mWhiteNoiseTabBtn);
            return;
        }
        if (i == 0) {
            if (!MiuiSdk.isLiteOrMiddleMode()) {
                showBtn();
            } else {
                AnimationUtils.showView(this.mWhiteNoiseTabBtn, this.mCommonUsedBtn);
            }
        }
    }

    private void hideBtn() {
        ImageButton imageButton;
        ImageButton imageButton2 = this.mWhiteNoiseTabBtn;
        if (imageButton2 == null || (imageButton = this.mCommonUsedBtn) == null) {
            return;
        }
        if (this.mIsInAnim) {
            MiuiFolme.cleanFolme(imageButton2, imageButton);
            MiuiFolme.resetFabTouch(this.mWhiteNoiseTabBtn, this.mCommonUsedBtn);
            resetFabScaleAndAlpha(this.mWhiteNoiseTabBtn, this.mCommonUsedBtn);
            resetVisibleState();
            this.mIsInAnim = false;
        }
        if (MiuiSdk.isLiteOrMiddleMode() || Util.inExternalSplitScreen(this.mActivity)) {
            this.mWhiteNoiseTabBtn.setVisibility(8);
            this.mCommonUsedBtn.setVisibility(8);
        } else {
            MiuiFolme.hideFab(this.mWhiteNoiseTabBtn, null);
            MiuiFolme.hideFab(this.mCommonUsedBtn, this.clockTransitionListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetVisibleState() {
        if (this.mWhiteNoiseTabBtn == null || this.mCommonUsedBtn == null) {
            return;
        }
        if (TabViewModel.TAB_TIMER.equals(this.mCurTab) && this.mState == 0 && !this.mHideBtnInHalfMode) {
            MiuiFolme.cleanFolme(this.mWhiteNoiseTabBtn, this.mCommonUsedBtn);
            MiuiFolme.resetFabTouch(this.mWhiteNoiseTabBtn, this.mCommonUsedBtn);
            resetFabScaleAndAlpha(this.mWhiteNoiseTabBtn, this.mCommonUsedBtn);
            this.mWhiteNoiseTabBtn.setVisibility(0);
            this.mCommonUsedBtn.setVisibility(0);
            return;
        }
        this.mWhiteNoiseTabBtn.setVisibility(8);
        this.mCommonUsedBtn.setVisibility(8);
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

    public void handleScroll(float f) {
        if (this.mWhiteNoiseTabBtn == null || this.mCommonUsedBtn == null) {
            return;
        }
        float fFloatValue = this.evaluator.evaluate(f, (Number) Float.valueOf(0.8f), (Number) Float.valueOf(1.0f)).floatValue();
        float fFloatValue2 = this.evaluator.evaluate(f, (Number) Float.valueOf(0.6f), (Number) Float.valueOf(1.0f)).floatValue();
        setScaleAndAlpha(this.mCommonUsedBtn, fFloatValue, fFloatValue2);
        setScaleAndAlpha(this.mWhiteNoiseTabBtn, fFloatValue, fFloatValue2);
    }

    private static void setScaleAndAlpha(ImageButton imageButton, float f, float f2) {
        if (imageButton == null || f < 0.0f || imageButton.getVisibility() != 0) {
            return;
        }
        imageButton.setScaleX(f);
        imageButton.setScaleY(f);
        imageButton.setImageAlpha((int) (f2 * 255.0f));
    }

    public void setListener(onTimerAdditionFabClickListener ontimeradditionfabclicklistener) {
        this.mListener = ontimeradditionfabclicklistener;
    }

    public static TimerAdditionFabController getInstance() {
        return mAdditionFabController;
    }

    public void setWhiteNoiseBtnChecked(boolean z) {
        ImageButton imageButton = this.mWhiteNoiseTabBtn;
        if (imageButton == null) {
            return;
        }
        this.mIsWhiteNoiseChecked = z;
        if (z) {
            imageButton.setBackgroundResource(R.drawable.sub_fab_bg_checked);
            this.mWhiteNoiseTabBtn.setImageResource(WHITE_NOISE_BTN_ICON_CHECKED[this.mWhiteNoiseCheckedPosition]);
        } else {
            imageButton.setBackgroundResource(R.drawable.sub_fab_bg);
            this.mWhiteNoiseTabBtn.setImageResource(WHITE_NOISE_BTN_ICON_NORMAL[this.mWhiteNoiseCheckedPosition]);
        }
    }

    public void setWhiteNoiseCheckedPosition(int i) {
        this.mWhiteNoiseCheckedPosition = i;
        handleWitheNoiseChecked(i);
    }

    public void handleWitheNoiseChecked(int i) {
        this.mWhiteNoiseCheckedPosition = i;
        ImageButton imageButton = this.mWhiteNoiseTabBtn;
        if (imageButton == null) {
            return;
        }
        if (this.mIsWhiteNoiseChecked) {
            imageButton.setImageResource(WHITE_NOISE_BTN_ICON_CHECKED[i]);
        } else {
            imageButton.setImageResource(WHITE_NOISE_BTN_ICON_NORMAL[i]);
        }
    }

    public void setCommonUsedBtnChecked(boolean z) {
        ImageButton imageButton = this.mCommonUsedBtn;
        if (imageButton == null) {
            return;
        }
        if (z) {
            imageButton.setBackgroundResource(R.drawable.sub_fab_bg_checked);
            this.mCommonUsedBtn.setImageResource(R.drawable.fab_common_timer_icon_checked);
        } else {
            imageButton.setBackgroundResource(R.drawable.sub_fab_bg);
            this.mCommonUsedBtn.setImageResource(R.drawable.fab_common_timer_icon);
        }
    }

    /* JADX WARN: Code duplicated, block: B:24:0x0074  */
    /* JADX WARN: Code duplicated, block: B:27:0x0086  */
    /* JADX WARN: Code duplicated, block: B:35:0x00b1  */
    /* JADX WARN: Code duplicated, block: B:43:0x00d9  */
    public void resetUi(ScreenSpec screenSpec, DeskClockTabActivity deskClockTabActivity) {
        float dimension;
        float dimension2;
        ImageButton imageButton;
        ImageButton imageButton2;
        int dimension3;
        boolean z;
        Context appContext = DeskClockApp.getAppContext();
        this.mActivity = deskClockTabActivity;
        if (Util.isDeviceCetus()) {
            dimension = DeskClockApp.getAppContext().getResources().getDimension(R.dimen.comm_timer_btn_cetus_margin_start);
        } else {
            dimension = PadAdapterUtil.IS_PAD ? appContext.getResources().getDimension(R.dimen.comm_timer_btn_pad_margin_start) : appContext.getResources().getDimension(R.dimen.comm_timer_btn_margin_start);
        }
        int i = (int) dimension;
        if (PadAdapterUtil.IS_PAD && !Util.isWideMode(appContext)) {
            dimension2 = DeskClockApp.getAppContext().getResources().getDimension(R.dimen.comm_timer_btn_not_wide_margin_start);
        } else {
            if (PadAdapterUtil.IS_PAD && (ResponsiveUtil.inFreeFormWindow(screenSpec) || ResponsiveUtil.inSplitScreen(screenSpec, appContext))) {
                dimension2 = DeskClockApp.getAppContext().getResources().getDimension(R.dimen.comm_timer_btn_margin_start);
            }
            imageButton = this.mCommonUsedBtn;
            if (imageButton != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageButton.getLayoutParams();
                layoutParams.setMarginEnd(i);
                this.mCommonUsedBtn.setLayoutParams(layoutParams);
            }
            imageButton2 = this.mWhiteNoiseTabBtn;
            if (imageButton2 != null) {
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) imageButton2.getLayoutParams();
                layoutParams2.setMarginStart(i);
                this.mWhiteNoiseTabBtn.setLayoutParams(layoutParams2);
            }
            if (PadAdapterUtil.IS_PAD && (ResponsiveUtil.inExternalSplitScreen(screenSpec, appContext) || ResponsiveUtil.inFreeFormWindow(screenSpec))) {
                dimension3 = (int) appContext.getResources().getDimension(R.dimen.comm_timer_btn_margin_start_small_mode);
            } else {
                dimension3 = ((int) appContext.getResources().getDimension(R.dimen.small_fab_margin_bottom)) + GestureLineUtil.getGestureLineHeight(appContext);
            }
            setBottomMargin(dimension3);
            if (PadAdapterUtil.IS_PAD && !ResponsiveUtil.inTwoThird(screenSpec) && Util.inExternalSplitScreen(this.mActivity)) {
                z = true;
            } else {
                z = false;
            }
            this.mHideBtnInHalfMode = z;
            resetVisibleState();
        }
        i = (int) dimension2;
        imageButton = this.mCommonUsedBtn;
        if (imageButton != null) {
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) imageButton.getLayoutParams();
            layoutParams3.setMarginEnd(i);
            this.mCommonUsedBtn.setLayoutParams(layoutParams3);
        }
        imageButton2 = this.mWhiteNoiseTabBtn;
        if (imageButton2 != null) {
            FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) imageButton2.getLayoutParams();
            layoutParams4.setMarginStart(i);
            this.mWhiteNoiseTabBtn.setLayoutParams(layoutParams4);
        }
        if (PadAdapterUtil.IS_PAD) {
            dimension3 = ((int) appContext.getResources().getDimension(R.dimen.small_fab_margin_bottom)) + GestureLineUtil.getGestureLineHeight(appContext);
        } else {
            dimension3 = ((int) appContext.getResources().getDimension(R.dimen.small_fab_margin_bottom)) + GestureLineUtil.getGestureLineHeight(appContext);
        }
        setBottomMargin(dimension3);
        if (PadAdapterUtil.IS_PAD) {
            z = false;
        } else {
            z = false;
        }
        this.mHideBtnInHalfMode = z;
        resetVisibleState();
    }

    public void showWhiteNoiseGuideWindow(Context context) {
        if (isGuideShowed() || context == null) {
            return;
        }
        GuidePopupWindow guidePopupWindow = this.mWhiteNoiseGuideWindow;
        if (guidePopupWindow != null) {
            guidePopupWindow.dismiss();
        }
        if (this.mWhiteNoiseTabBtn == null) {
            return;
        }
        GuidePopupWindow guidePopupWindow2 = new GuidePopupWindow(context);
        this.mWhiteNoiseGuideWindow = guidePopupWindow2;
        guidePopupWindow2.setArrowMode(18);
        this.mWhiteNoiseGuideWindow.setGuideText(R.string.white_noise_guide_desc);
        this.mWhiteNoiseGuideWindow.setShowDuration(R2.color.word_photo_color);
        this.mWhiteNoiseTabBtn.post(new Runnable() { // from class: com.android.deskclock.util.fab.TimerAdditionFabController.4
            @Override // java.lang.Runnable
            public void run() {
                if (TimerAdditionFabController.this.mWhiteNoiseGuideWindow == null) {
                    return;
                }
                TimerAdditionFabController.this.mWhiteNoiseGuideWindow.show(TimerAdditionFabController.this.mWhiteNoiseTabBtn, (int) (DeskClockApp.getAppContext().getResources().getDimension(R.dimen.white_noise_guide_view_width) / 2.0f), -((int) DeskClockApp.getAppContext().getResources().getDimension(R.dimen.white_noise_guide_view_offset_y)), true);
                TimerAdditionFabController.this.setGuideShowed(true);
            }
        });
    }

    public void onPause() {
        GuidePopupWindow guidePopupWindow = this.mWhiteNoiseGuideWindow;
        if (guidePopupWindow != null) {
            guidePopupWindow.dismiss();
            this.mWhiteNoiseGuideWindow = null;
        }
    }

    private boolean isGuideShowed() {
        return FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).getBoolean(KEY_SHOWED_GUIDE_WINDOW, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setGuideShowed(boolean z) {
        FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).edit().putBoolean(KEY_SHOWED_GUIDE_WINDOW, z).apply();
    }
}
