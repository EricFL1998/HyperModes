package com.android.deskclock.alarm.bedtime;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.OriginalViewPager;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.base.BaseActivity;
import com.android.deskclock.settings.AlarmSettingsFragment;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.DialogUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.ResponsiveUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.permission.SystemPermissionUtil;
import com.android.deskclock.util.permission.UserNoticeUtil;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.view.SimpleDialogFragment;
import com.android.deskclock.view.pref.AlarmCheckboxLayout;
import com.android.deskclock.widget.NumberPicker;
import com.android.deskclock.widget.SleepTimePicker;
import com.android.deskclock.widget.TextNumberPicker;
import com.miui.support.cardview.CardView;
import java.util.ArrayList;
import java.util.Locale;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.floatingactivity.OnFloatingActivityCallback;
import miuix.core.util.MiuixUIUtils;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes.dex */
public class BedtimeGuideActivity extends BaseActivity implements View.OnClickListener, OriginalViewPager.OnPageChangeListener {
    public static final int INVAID_PAGE = -1;
    private static int MIN_HEIGHT_PIXELS = 1920;
    private static final int REQUEST_NETWORK_CODE = 1;
    private static final int REQUEST_NETWORK_CODE_NEW = 2;
    private static final String TAG = "DC:BedtimeGuideActivity";
    private static final int TIME_PICKER_COUNT = 3;
    private Button backButton;
    private ImageView[] indicatorPoint;
    private View mBackIcon;
    private TextView mCompleteTitle;
    private TextView mDndTitle;
    private SettingGuidePagerAdapter mPageAdapter;
    private TextView mRepeatTitle;
    private SleepTimePicker mSleepTimePicker;
    private TextView mSleepTimeTitle;
    private SleepTimePicker mWakeTimePicker;
    private TextView mWakeTimeTitle;
    private TextView mWelcomeTitle;
    private MiHomeHelper miHomeHelper;
    private Button nextButton;
    private Button primaryButton;
    private SettingGuideViewPager mViewPager = null;
    private LinearLayout indicatorView = null;
    private SimpleDialogFragment mBackConfirmDialog = null;
    private SimpleDialogFragment mNetPermissionDialog = null;
    private TextNumberPicker numberPicker = null;
    private Alarm sleepAlarm = null;
    private Alarm wakeUpAlarm = null;
    private TextView mSleepDuration = null;
    private TextView mSleepRecommendation = null;
    private AlarmCheckboxLayout mPref = null;
    private CardView mNoDisturbanceView = null;
    private ImageView mCompleteImage = null;
    private boolean showIndicator = true;
    private boolean saveData = true;
    private final int REPEAT_MONDAY_TO_FRIDAY_INDEX = 0;
    private final int REPEAT_EVERY_DAY_INDEX = 1;
    private final int REPEAT_LEGAL_WORK_DAYINDEX = 2;
    private final int WELCOME_PAGE = 0;
    private final int SLEEP_ALARM_PAGE = 1;
    private final int WAKE_ALARM_PAGE = 2;
    private final int REPEAT_TYPE_PAGE = 3;
    private final int NO_DISTURB_PAGE = 4;
    private final int COMPLETED_PAGE = 5;
    private final int PRE_LOAD_PAGES = 2;
    private int pageIndex = -1;

    @Override // miuix.appcompat.app.AppCompatActivity
    protected boolean isResponsiveEnabled() {
        return true;
    }

    @Override // androidx.viewpager.widget.OriginalViewPager.OnPageChangeListener
    public void onPageScrollStateChanged(int i) {
    }

    @Override // androidx.viewpager.widget.OriginalViewPager.OnPageChangeListener
    public void onPageScrolled(int i, float f, int i2) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        Object obj;
        super.onCreate(bundle);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.action_bar_overlay_layout);
        if (!isInFloatingWindowMode()) {
            viewGroup.setBackground(new ColorDrawable(getResources().getColor(R.color.main_bg)));
        }
        if (Util.isTinyScreen(this)) {
            finish();
        }
        setOnFloatingWindowCallback(new OnFloatingActivityCallback() { // from class: com.android.deskclock.alarm.bedtime.BedtimeGuideActivity.1
            @Override // miuix.appcompat.app.floatingactivity.OnFloatingActivityCallback
            public boolean onFinish(int i) {
                BedtimeGuideActivity.this.showBackDialog();
                return true;
            }
        });
        if (getIntent().hasExtra(BedtimeUtil.PAGE_INDEX)) {
            this.pageIndex = getIntent().getIntExtra(BedtimeUtil.PAGE_INDEX, 0);
        } else if (bundle != null && (obj = bundle.get("index")) != null) {
            this.pageIndex = ((Integer) obj).intValue();
        }
        setContentView(R.layout.bedtime_guide_setting);
        addMiuiActionBar();
        initSleepAlarm();
        initWakeAlarm();
        this.mViewPager = (SettingGuideViewPager) findViewById(R.id.guide_view_pager);
        this.indicatorView = (LinearLayout) findViewById(R.id.page_indicator);
        this.primaryButton = (Button) findViewById(R.id.primary_button);
        this.backButton = (Button) findViewById(R.id.back_button);
        this.nextButton = (Button) findViewById(R.id.next_button);
        if (Locale.getDefault().getLanguage().contains("bo") && MiuixUIUtils.getFontLevel(DeskClockApp.getAppDEContext()) == 2) {
            this.primaryButton.setTextSize(DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.bedtime_guide_button_textsize_large));
        }
        this.primaryButton.setOnClickListener(this);
        this.primaryButton.setVisibility(0);
        this.backButton.setOnClickListener(this);
        this.nextButton.setOnClickListener(this);
        this.showIndicator = canShowIndicator();
        initData();
        this.mViewPager.setAdapter(this.mPageAdapter);
        this.mViewPager.setOffscreenPageLimit(2);
        this.mViewPager.setOnPageChangeListener(this);
        int i = this.pageIndex;
        int i2 = i != -1 ? i : 0;
        this.pageIndex = i2;
        this.mViewPager.setCurrentItem(i2);
        if (this.pageIndex == 0) {
            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.GUIDE_WELCOME);
        }
        BedtimeUtil.setGuideSettingsIndex(this, -1);
        OneTrackStatHelper.trackViewEvent(OneTrackStatHelper.BEDTIME_ENTER_GUIDE_PAGE);
        initCetusView();
        final View viewFindViewById = findViewById(R.id.bedtime_guide_setting_root);
        ViewCompat.setOnApplyWindowInsetsListener(viewFindViewById, new OnApplyWindowInsetsListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeGuideActivity.2
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                WindowInsetsCompat rootWindowInsets;
                if (!MiuixUIUtils.isLayoutHideNavigation(view) || (rootWindowInsets = ViewCompat.getRootWindowInsets(view)) == null) {
                    return windowInsetsCompat;
                }
                Insets insets = rootWindowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
                View view2 = viewFindViewById;
                if (view2 != null) {
                    view2.setPadding(0, 0, 0, insets.bottom);
                }
                return windowInsetsCompat;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("index", this.mViewPager.getCurrentItem());
    }

    @Override // com.android.deskclock.base.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        if (this.mViewPager.getCurrentItem() == 0 || this.mViewPager.getCurrentItem() == 5) {
            if (this.mViewPager.getCurrentItem() == 0) {
                this.mBackIcon.setAccessibilityTraversalBefore(R.id.welcome_title);
                this.primaryButton.setAccessibilityTraversalAfter(R.id.welcome_text);
            }
            this.primaryButton.setVisibility(0);
            this.backButton.setVisibility(8);
            this.nextButton.setVisibility(8);
            return;
        }
        this.primaryButton.setVisibility(8);
        this.backButton.setVisibility(0);
        this.nextButton.setVisibility(0);
    }

    private void initData() {
        ArrayList arrayList = new ArrayList();
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(this);
        View viewInflate = layoutInflaterFrom.inflate(R.layout.bedtime_guide_setting_welcome, (ViewGroup) null);
        this.mWelcomeTitle = (TextView) viewInflate.findViewById(R.id.welcome_title);
        ((TextView) viewInflate.findViewById(R.id.welcome_text)).setMovementMethod(ScrollingMovementMethod.getInstance());
        arrayList.add(viewInflate);
        View viewInflate2 = layoutInflaterFrom.inflate(R.layout.bedtime_guide_setting_sleeptime, (ViewGroup) null);
        ((TextView) viewInflate2.findViewById(R.id.sleep_time_remind)).setText(String.format(getResources().getString(R.string.bedtime_sleep_time_remind_new), 15));
        this.mSleepTimeTitle = (TextView) viewInflate2.findViewById(R.id.sleep_time_title);
        SleepTimePicker sleepTimePicker = (SleepTimePicker) viewInflate2.findViewById(R.id.time_picker);
        this.mSleepTimePicker = sleepTimePicker;
        sleepTimePicker.setSelectorIndicesCount(3);
        sleepTimePicker.setIs24HourView(true);
        sleepTimePicker.setCurrentHour(Integer.valueOf(this.sleepAlarm.hour));
        sleepTimePicker.setCurrentMinute(Integer.valueOf(this.sleepAlarm.minutes / 5));
        sleepTimePicker.setOnTimeChangedListener(new SleepTimePicker.OnTimeChangedListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeGuideActivity.3
            @Override // com.android.deskclock.widget.SleepTimePicker.OnTimeChangedListener
            public void onTimeChanged(SleepTimePicker sleepTimePicker2, int i, int i2) {
                BedtimeGuideActivity.this.sleepAlarm.hour = i;
                BedtimeGuideActivity.this.sleepAlarm.minutes = i2 * 5;
                BedtimeGuideActivity.this.setSleepDuration();
            }
        });
        arrayList.add(viewInflate2);
        View viewInflate3 = layoutInflaterFrom.inflate(R.layout.bedtime_guide_setting_wake_up_time, (ViewGroup) null);
        this.mWakeTimeTitle = (TextView) viewInflate3.findViewById(R.id.sleep_time_title);
        ((TextView) viewInflate3.findViewById(R.id.sleep_duration_desc)).setText(String.format(getResources().getString(R.string.sleep_duration_recommendation_new), 7, 9));
        SleepTimePicker sleepTimePicker2 = (SleepTimePicker) viewInflate3.findViewById(R.id.time_picker);
        this.mWakeTimePicker = sleepTimePicker2;
        sleepTimePicker2.setSelectorIndicesCount(3);
        this.mSleepDuration = (TextView) viewInflate3.findViewById(R.id.sleep_duration_length);
        sleepTimePicker2.setIs24HourView(true);
        setSleepDuration();
        sleepTimePicker2.setCurrentHour(Integer.valueOf(this.wakeUpAlarm.hour));
        sleepTimePicker2.setCurrentMinute(Integer.valueOf(this.wakeUpAlarm.minutes / 5));
        sleepTimePicker2.setOnTimeChangedListener(new SleepTimePicker.OnTimeChangedListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeGuideActivity.4
            @Override // com.android.deskclock.widget.SleepTimePicker.OnTimeChangedListener
            public void onTimeChanged(SleepTimePicker sleepTimePicker3, int i, int i2) {
                if (i != BedtimeGuideActivity.this.sleepAlarm.hour || i2 != BedtimeGuideActivity.this.sleepAlarm.minutes / 5) {
                    BedtimeGuideActivity.this.nextButton.setEnabled(true);
                    BedtimeGuideActivity.this.nextButton.setAlpha(1.0f);
                } else {
                    BedtimeGuideActivity.this.nextButton.setEnabled(false);
                    BedtimeGuideActivity.this.nextButton.setAlpha(0.5f);
                }
                BedtimeGuideActivity.this.wakeUpAlarm.hour = i;
                BedtimeGuideActivity.this.wakeUpAlarm.minutes = i2 * 5;
                BedtimeGuideActivity.this.setSleepDuration();
            }
        });
        TextView textView = (TextView) viewInflate3.findViewById(R.id.sleep_duration);
        TextView textView2 = (TextView) viewInflate3.findViewById(R.id.sleep_duration_desc);
        this.mSleepRecommendation = textView2;
        if (!this.showIndicator) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) textView2.getLayoutParams();
            marginLayoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.sleep_duration_desc_margin_bottom_without_indicator);
            this.mSleepRecommendation.setLayoutParams(marginLayoutParams);
        }
        arrayList.add(viewInflate3);
        View viewInflate4 = layoutInflaterFrom.inflate(R.layout.bedtime_guide_setting_repeat_type, (ViewGroup) null);
        this.mRepeatTitle = (TextView) viewInflate4.findViewById(R.id.repeat_title);
        TextNumberPicker textNumberPicker = (TextNumberPicker) viewInflate4.findViewById(R.id.repeat_type);
        this.numberPicker = textNumberPicker;
        textNumberPicker.setSelectorIndicesCount(3);
        this.numberPicker.setMinValue(0);
        if (!Util.isInternational()) {
            this.numberPicker.setDisplayedValues(getResources().getStringArray(R.array.bedtime_guide_repeat_type));
            this.numberPicker.setMaxValue(2);
        } else {
            this.numberPicker.setDisplayedValues(getResources().getStringArray(R.array.bedtime_guide_repeat_type_international));
            this.numberPicker.setMaxValue(1);
        }
        if (MiuiSdk.isSupportFontAnim()) {
            this.numberPicker.setTypeface(MiuiFont.MI_PRO_REGULAR, MiuiFont.MI_PRO_MEDIUM);
        }
        this.numberPicker.setValue(transRepeatTypeToIndex(this.wakeUpAlarm.daysOfWeek));
        this.numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeGuideActivity.5
            @Override // com.android.deskclock.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                if (i2 == 0) {
                    BedtimeGuideActivity.this.wakeUpAlarm.daysOfWeek.setDay(31);
                    BedtimeGuideActivity.this.numberPicker.sendAccessibilityEvent(4);
                } else if (i2 == 1) {
                    BedtimeGuideActivity.this.wakeUpAlarm.daysOfWeek.setDay(127);
                    BedtimeGuideActivity.this.numberPicker.sendAccessibilityEvent(4);
                } else {
                    if (i2 != 2) {
                        return;
                    }
                    BedtimeGuideActivity.this.wakeUpAlarm.daysOfWeek.setDay(128);
                    BedtimeGuideActivity.this.numberPicker.sendAccessibilityEvent(4);
                }
            }
        });
        arrayList.add(viewInflate4);
        View viewInflate5 = layoutInflaterFrom.inflate(R.layout.bedtime_guide_setting_no_disturbance, (ViewGroup) null);
        this.mDndTitle = (TextView) viewInflate5.findViewById(R.id.dnd_title);
        this.mPref = (AlarmCheckboxLayout) viewInflate5.findViewById(R.id.sleep_no_disturbance);
        this.mNoDisturbanceView = (CardView) viewInflate5.findViewById(R.id.sleep_no_disturbance_card_view);
        this.mPref.setOnPreferenceChangeListener(new AlarmCheckboxLayout.OnPreferenceChangeListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeGuideActivity.6
            @Override // com.android.deskclock.view.pref.AlarmCheckboxLayout.OnPreferenceChangeListener
            public void onPreferenceChange(String str, boolean z) {
                BedtimeUtil.setDisturbanceState(BedtimeGuideActivity.this, z);
            }
        });
        arrayList.add(viewInflate5);
        View viewInflate6 = layoutInflaterFrom.inflate(R.layout.bedtime_guide_setting_complete, (ViewGroup) null);
        this.mCompleteImage = (ImageView) viewInflate6.findViewById(R.id.complete_ic);
        this.mCompleteTitle = (TextView) viewInflate6.findViewById(R.id.complete_text);
        arrayList.add(viewInflate6);
        this.mPageAdapter = new SettingGuidePagerAdapter(arrayList);
        this.indicatorPoint = new ImageView[4];
        int[] iArr = {R.id.indicator_point1, R.id.indicator_point2, R.id.indicator_point3, R.id.indicator_point4};
        for (int i = 0; i < 4; i++) {
            this.indicatorPoint[i] = (ImageView) findViewById(iArr[i]);
        }
        if (MiuiSdk.isSupportFontAnim()) {
            MiuiFont.setFont(this.mWelcomeTitle, MiuiFont.MI_PRO_MEDIUM);
            MiuiFont.setFont(this.primaryButton, MiuiFont.MI_PRO_MEDIUM);
            MiuiFont.setFont(this.backButton, MiuiFont.MI_PRO_MEDIUM);
            MiuiFont.setFont(this.nextButton, MiuiFont.MI_PRO_MEDIUM);
            MiuiFont.setFont(textView, MiuiFont.MI_PRO_MEDIUM);
            MiuiFont.setFont(this.mSleepDuration, MiuiFont.MI_PRO_MEDIUM);
            MiuiFont.setFont(this.mSleepTimeTitle, MiuiFont.MI_PRO_MEDIUM);
            MiuiFont.setFont(this.mWakeTimeTitle, MiuiFont.MI_PRO_MEDIUM);
            MiuiFont.setFont(this.mRepeatTitle, MiuiFont.MI_PRO_MEDIUM);
            MiuiFont.setFont(this.mDndTitle, MiuiFont.MI_PRO_MEDIUM);
            MiuiFont.setFont(this.mCompleteTitle, MiuiFont.MI_PRO_MEDIUM);
            MiuiFont.setFont(this.mSleepRecommendation, MiuiFont.MI_PRO_NORMAL);
        }
        if (Util.isFreeFormScreen(getResources().getConfiguration()) || Util.inExternalSplitScreen(this)) {
            initFreeFormScreenView();
        } else {
            updateFullView();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSleepDuration() {
        if (this.mSleepDuration == null) {
            return;
        }
        int sleepDuration = BedtimeUtil.getSleepDuration(this.sleepAlarm.hour, this.sleepAlarm.minutes, this.wakeUpAlarm.hour, this.wakeUpAlarm.minutes);
        int i = sleepDuration / 60;
        int i2 = sleepDuration % 60;
        String quantityString = getResources().getQuantityString(R.plurals.sleep_duration_hour, i, Integer.valueOf(i));
        if (i2 == 0) {
            this.mSleepDuration.setText(quantityString);
        } else {
            this.mSleepDuration.setText(getString(R.string.sleep_duration_text, new Object[]{quantityString, getResources().getQuantityString(R.plurals.sleep_duration_min, i2, Integer.valueOf(i2))}));
        }
    }

    private void addMiuiActionBar() {
        ActionBar appCompatActionBar = getAppCompatActionBar();
        appCompatActionBar.setCustomView(R.layout.bedtime_guide_setting_bar);
        appCompatActionBar.setDisplayShowCustomEnabled(true);
        appCompatActionBar.setDisplayShowTitleEnabled(false);
        View viewFindViewById = appCompatActionBar.getCustomView().findViewById(R.id.cancle);
        this.mBackIcon = viewFindViewById;
        viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeGuideActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BedtimeGuideActivity.this.showBackDialog();
            }
        });
    }

    @Override // com.android.deskclock.base.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        DialogUtil.dismissDialogFragment(this.mBackConfirmDialog);
        this.mBackConfirmDialog = null;
        DialogUtil.dismissDialogFragment(this.mNetPermissionDialog);
        this.mNetPermissionDialog = null;
    }

    @Override // miuix.appcompat.app.AppCompatActivity, android.app.Activity
    public void finish() {
        showBackDialog();
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showBackDialog() {
        SettingGuideViewPager settingGuideViewPager = this.mViewPager;
        if (settingGuideViewPager == null || settingGuideViewPager.getCurrentItem() != 5) {
            SimpleDialogFragment simpleDialogFragment = this.mBackConfirmDialog;
            if (simpleDialogFragment != null) {
                simpleDialogFragment.dismiss();
            }
            SimpleDialogFragment simpleDialogFragmentShowAlertDialog = DialogUtil.showAlertDialog(getResources().getString(R.string.bedtime_manage), getResources().getString(R.string.bedtime_guide_back_desc), R.string.back, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeGuideActivity.8
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    StatHelper.trackEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.GUIDE_X, "CANCEL");
                    OneTrackStatHelper.trackBoolEvent(false, OneTrackStatHelper.BEDTIME_GUIDE_CANCEL);
                    BedtimeGuideActivity.this.setResult(-1);
                    BedtimeGuideActivity.this.saveData = false;
                    BedtimeUtil.setGuideSettingsIndex(BedtimeGuideActivity.this, -1);
                    BedtimeGuideActivity.super.finish();
                }
            }, R.string.continue_operation, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeGuideActivity.9
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    StatHelper.trackEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.GUIDE_X, "SUCCESS");
                    OneTrackStatHelper.trackBoolEvent(true, OneTrackStatHelper.BEDTIME_GUIDE_CANCEL);
                }
            }, getSupportFragmentManager());
            this.mBackConfirmDialog = simpleDialogFragmentShowAlertDialog;
            simpleDialogFragmentShowAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeGuideActivity.10
                @Override // android.content.DialogInterface.OnCancelListener
                public void onCancel(DialogInterface dialogInterface) {
                    StatHelper.trackEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.GUIDE_X, "CANCEL");
                    OneTrackStatHelper.trackBoolEvent(false, OneTrackStatHelper.BEDTIME_GUIDE_CANCEL);
                }
            });
        }
    }

    @Override // androidx.viewpager.widget.OriginalViewPager.OnPageChangeListener
    public void onPageSelected(int i) {
        if (i == 0) {
            this.primaryButton.setVisibility(0);
            this.primaryButton.setText(getResources().getText(R.string.usage_start));
            this.backButton.setVisibility(8);
            this.nextButton.setVisibility(8);
            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.GUIDE_WELCOME);
        } else if (i == 1) {
            this.primaryButton.setVisibility(8);
            this.backButton.setVisibility(0);
            this.nextButton.setVisibility(0);
            this.nextButton.setEnabled(true);
            this.nextButton.setAlpha(1.0f);
            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.GUIDE_SLEEP_TIME);
        } else if (i == 2) {
            if (this.wakeUpAlarm.hour == this.sleepAlarm.hour && this.wakeUpAlarm.minutes == this.sleepAlarm.minutes) {
                this.nextButton.setEnabled(false);
                this.nextButton.setAlpha(0.5f);
            } else {
                this.nextButton.setEnabled(true);
                this.nextButton.setAlpha(1.0f);
            }
            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.GUIDE_WAKE_TIME);
        } else if (i == 3) {
            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.GUIDE_REPEAT_TYPE);
        } else if (i == 4) {
            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.GUIDE_NO_DISTURB);
        } else if (i == 5) {
            getAppCompatActionBar().getCustomView().findViewById(R.id.cancle).setVisibility(8);
            this.primaryButton.setVisibility(0);
            this.primaryButton.setText(getResources().getText(R.string.setting_complete));
            this.backButton.setVisibility(8);
            this.nextButton.setVisibility(8);
            this.saveData = false;
            BedtimeUtil.setGuideSettingsIndex(this, -1);
            BedtimeUtil.saveSleepAlarm(this, this.sleepAlarm);
            Log.i(TAG, "saveSleepAlarm, hour: " + this.sleepAlarm.hour + ", minutes: " + this.sleepAlarm.minutes);
            BedtimeUtil.setBedTimeCompleted(this, true);
            BedtimeUtil.setBannerVisiable(this, false);
            BedtimeUtil.setBedtimeOpenState(this, true);
            AlarmHelper.addWakeAlarm(this, this.wakeUpAlarm);
            Log.i(TAG, "saveWakeAlarm, " + this.wakeUpAlarm.toString());
            AlarmHelper.setSleepNotification(this);
            AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.BedtimeGuideActivity.11
                @Override // java.lang.Runnable
                public void run() {
                    if (BedtimeUtil.getDisturbanceState(BedtimeGuideActivity.this)) {
                        AlarmHelper.setZenMode(BedtimeGuideActivity.this);
                    }
                    if (HealthDataUtil.isHealthAppValuable(BedtimeGuideActivity.this)) {
                        BedtimeGuideActivity bedtimeGuideActivity = BedtimeGuideActivity.this;
                        HealthDataUtil.updateSchedule(bedtimeGuideActivity, bedtimeGuideActivity.sleepAlarm.hour, BedtimeGuideActivity.this.sleepAlarm.minutes, BedtimeGuideActivity.this.wakeUpAlarm.hour, BedtimeGuideActivity.this.wakeUpAlarm.minutes, BedtimeGuideActivity.this.wakeUpAlarm.daysOfWeek);
                    }
                }
            });
            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.GUIDE_FINISH);
            OneTrackStatHelper.trackViewEvent(OneTrackStatHelper.BEDTIME_GUIDE_FINISH);
            notifyBedtimeChanged(this);
        }
        if (i > 0 && i < 5 && this.showIndicator) {
            this.indicatorView.setVisibility(0);
            for (int i2 = 1; i2 < 5; i2++) {
                if (i == i2) {
                    this.indicatorPoint[i2 - 1].setImageResource(R.drawable.bedtime_page_indicator_focused);
                } else {
                    this.indicatorPoint[i2 - 1].setImageResource(R.drawable.bedtime_page_indicator_unfocused);
                }
            }
        } else {
            this.indicatorView.setVisibility(8);
        }
        SleepTimePicker sleepTimePicker = this.mWakeTimePicker;
        if (sleepTimePicker != null) {
            sleepTimePicker.stopScroll();
        }
        SleepTimePicker sleepTimePicker2 = this.mSleepTimePicker;
        if (sleepTimePicker2 != null) {
            sleepTimePicker2.stopScroll();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        if (this.saveData) {
            AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.BedtimeGuideActivity.12
                @Override // java.lang.Runnable
                public void run() {
                    BedtimeGuideActivity bedtimeGuideActivity = BedtimeGuideActivity.this;
                    BedtimeUtil.setGuideSettingsIndex(bedtimeGuideActivity, bedtimeGuideActivity.mViewPager.getCurrentItem());
                    BedtimeGuideActivity bedtimeGuideActivity2 = BedtimeGuideActivity.this;
                    BedtimeUtil.saveSleepAlarm(bedtimeGuideActivity2, bedtimeGuideActivity2.sleepAlarm);
                    BedtimeGuideActivity bedtimeGuideActivity3 = BedtimeGuideActivity.this;
                    BedtimeUtil.saveTempWakeAlarm(bedtimeGuideActivity3, bedtimeGuideActivity3.wakeUpAlarm);
                }
            });
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int currentItem = this.mViewPager.getCurrentItem();
        int id = view.getId();
        if (id == R.id.back_button) {
            this.mViewPager.setCurrentItem(currentItem - 1);
            return;
        }
        if (id == R.id.next_button) {
            if (currentItem == 3 && !UserNoticeUtil.isNetPermissionAgreed() && this.wakeUpAlarm.daysOfWeek.getCoded() == 128) {
                showNetPermissionDialog();
                return;
            } else {
                this.mViewPager.setCurrentItem(currentItem + 1);
                return;
            }
        }
        if (id != R.id.primary_button) {
            return;
        }
        if (currentItem == 0) {
            this.mViewPager.setCurrentItem(currentItem + 1);
        } else if (currentItem == 5) {
            super.finish();
        }
    }

    private void initWakeAlarm() {
        if (this.wakeUpAlarm != null) {
            return;
        }
        Alarm alarm = new Alarm();
        this.wakeUpAlarm = alarm;
        alarm.skipTime = 0L;
        this.wakeUpAlarm.vibrate = AlarmSettingsFragment.getVibrateState();
        this.wakeUpAlarm.time = 0L;
        if (this.pageIndex > -1) {
            this.wakeUpAlarm.daysOfWeek.setDay(BedtimeUtil.getTempWakeRepeat(this));
            this.wakeUpAlarm.hour = BedtimeUtil.getTempWakeHour(this);
            this.wakeUpAlarm.minutes = BedtimeUtil.getTempWakeMin(this);
            return;
        }
        this.wakeUpAlarm.daysOfWeek.setDay(127);
        this.wakeUpAlarm.hour = 6;
        this.wakeUpAlarm.minutes = 0;
    }

    private void initSleepAlarm() {
        if (this.sleepAlarm != null) {
            return;
        }
        Alarm alarm = new Alarm();
        this.sleepAlarm = alarm;
        if (this.pageIndex > -1) {
            alarm.hour = BedtimeUtil.getSleepAlarmHour(this);
            this.sleepAlarm.minutes = BedtimeUtil.getSleepAlarmMin(this);
        } else {
            alarm.hour = 22;
            this.sleepAlarm.minutes = 0;
        }
    }

    private void initFreeFormScreenView() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mSleepTimePicker.getLayoutParams();
        layoutParams.height = (int) getResources().getDimension(R.dimen.set_alarm_numberpicker_height_small_mode);
        layoutParams.topMargin = (int) getResources().getDimension(R.dimen.bedtime_sleeptime_remind_small_margin_bottom);
        this.mSleepTimePicker.setLayoutParams(layoutParams);
        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.mWakeTimePicker.getLayoutParams();
        layoutParams2.height = (int) getResources().getDimension(R.dimen.set_alarm_numberpicker_height_small_mode);
        layoutParams2.topMargin = (int) getResources().getDimension(R.dimen.bedtime_sleeptime_remind_small_margin_bottom);
        this.mWakeTimePicker.setLayoutParams(layoutParams2);
        RelativeLayout.LayoutParams layoutParams3 = (RelativeLayout.LayoutParams) this.indicatorView.getLayoutParams();
        layoutParams3.bottomMargin = (int) getResources().getDimension(R.dimen.page_indicator_small_margin_bottom);
        this.indicatorView.setLayoutParams(layoutParams3);
        RelativeLayout.LayoutParams layoutParams4 = (RelativeLayout.LayoutParams) this.mSleepRecommendation.getLayoutParams();
        layoutParams4.bottomMargin = (int) getResources().getDimension(R.dimen.sleep_duration_desc_small_margin_bottom);
        this.mSleepRecommendation.setLayoutParams(layoutParams4);
        LinearLayout.LayoutParams layoutParams5 = (LinearLayout.LayoutParams) this.numberPicker.getLayoutParams();
        layoutParams5.height = (int) getResources().getDimension(R.dimen.set_alarm_numberpicker_height_small_mode);
        layoutParams5.topMargin = (int) getResources().getDimension(R.dimen.bedtime_guide_number_picker_small_margin_top);
        this.numberPicker.setLayoutParams(layoutParams5);
        int dimension = (int) getResources().getDimension(R.dimen.sleep_no_disturbance_freeform_margin_start);
        LinearLayout.LayoutParams layoutParams6 = (LinearLayout.LayoutParams) this.mNoDisturbanceView.getLayoutParams();
        layoutParams6.setMarginStart(dimension);
        layoutParams6.setMarginEnd(dimension);
        layoutParams6.topMargin = (int) getResources().getDimension(R.dimen.sleep_no_disturbance_freeform_margin_top);
        this.mNoDisturbanceView.setLayoutParams(layoutParams6);
        RelativeLayout.LayoutParams layoutParams7 = (RelativeLayout.LayoutParams) this.mCompleteImage.getLayoutParams();
        layoutParams7.topMargin = (int) getResources().getDimension(R.dimen.bedtime_setting_complete_ic_small_margin_top);
        this.mCompleteImage.setLayoutParams(layoutParams7);
        LinearLayout.LayoutParams layoutParams8 = (LinearLayout.LayoutParams) this.mWelcomeTitle.getLayoutParams();
        layoutParams8.topMargin = (int) getResources().getDimension(R.dimen.bedtime_guide_title_split_margin_top);
        this.mWelcomeTitle.setLayoutParams(layoutParams8);
        RelativeLayout.LayoutParams layoutParams9 = (RelativeLayout.LayoutParams) this.mWakeTimeTitle.getLayoutParams();
        layoutParams9.topMargin = (int) getResources().getDimension(R.dimen.bedtime_guide_title_split_margin_top);
        this.mWakeTimeTitle.setLayoutParams(layoutParams9);
        LinearLayout.LayoutParams layoutParams10 = (LinearLayout.LayoutParams) this.mSleepTimeTitle.getLayoutParams();
        layoutParams10.topMargin = (int) getResources().getDimension(R.dimen.bedtime_guide_title_split_margin_top);
        this.mSleepTimeTitle.setLayoutParams(layoutParams10);
        LinearLayout.LayoutParams layoutParams11 = (LinearLayout.LayoutParams) this.mRepeatTitle.getLayoutParams();
        layoutParams11.topMargin = (int) getResources().getDimension(R.dimen.bedtime_guide_title_split_margin_top);
        this.mRepeatTitle.setLayoutParams(layoutParams11);
        LinearLayout.LayoutParams layoutParams12 = (LinearLayout.LayoutParams) this.mDndTitle.getLayoutParams();
        layoutParams12.topMargin = (int) getResources().getDimension(R.dimen.bedtime_guide_title_split_margin_top);
        this.mDndTitle.setLayoutParams(layoutParams12);
    }

    private void updateFullView() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mSleepTimePicker.getLayoutParams();
        layoutParams.height = (int) getResources().getDimension(R.dimen.set_alarm_numberpicker_height);
        if (Locale.getDefault().getLanguage().contains("bo") && MiuixUIUtils.getFontLevel(DeskClockApp.getAppDEContext()) == 2) {
            layoutParams.topMargin = (int) getResources().getDimension(R.dimen.bedtime_sleeptime_remind_margin_top_large);
        } else {
            layoutParams.topMargin = (int) getResources().getDimension(R.dimen.bedtime_sleeptime_remind_margin_top);
        }
        this.mSleepTimePicker.setLayoutParams(layoutParams);
        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.mWakeTimePicker.getLayoutParams();
        layoutParams2.height = (int) getResources().getDimension(R.dimen.set_alarm_numberpicker_height);
        if (Locale.getDefault().getLanguage().contains("bo") && MiuixUIUtils.getFontLevel(DeskClockApp.getAppDEContext()) == 2) {
            layoutParams2.topMargin = (int) getResources().getDimension(R.dimen.bedtime_sleeptime_remind_margin_top_large);
        } else {
            layoutParams2.topMargin = (int) getResources().getDimension(R.dimen.bedtime_sleeptime_remind_margin_top);
        }
        this.mWakeTimePicker.setLayoutParams(layoutParams2);
        RelativeLayout.LayoutParams layoutParams3 = (RelativeLayout.LayoutParams) this.indicatorView.getLayoutParams();
        layoutParams3.bottomMargin = (int) getResources().getDimension(R.dimen.page_indicator_margin_bottom);
        this.indicatorView.setLayoutParams(layoutParams3);
        RelativeLayout.LayoutParams layoutParams4 = (RelativeLayout.LayoutParams) this.mSleepRecommendation.getLayoutParams();
        layoutParams4.bottomMargin = (int) getResources().getDimension(R.dimen.sleep_duration_desc_margin_bottom);
        this.mSleepRecommendation.setLayoutParams(layoutParams4);
        LinearLayout.LayoutParams layoutParams5 = (LinearLayout.LayoutParams) this.numberPicker.getLayoutParams();
        layoutParams5.height = (int) getResources().getDimension(R.dimen.set_alarm_numberpicker_height);
        layoutParams5.topMargin = (int) getResources().getDimension(R.dimen.bedtime_guide_number_picker_top);
        this.numberPicker.setLayoutParams(layoutParams5);
        int dimension = (int) getResources().getDimension(R.dimen.sleep_no_disturbance_margin_start);
        if (Util.isInInternalScreen(this) && Util.isFoldDevice(this) && !isInMultiWindowMode()) {
            dimension = (int) getResources().getDimension(R.dimen.sleep_no_disturbance_fold_margin_start);
        }
        LinearLayout.LayoutParams layoutParams6 = (LinearLayout.LayoutParams) this.mNoDisturbanceView.getLayoutParams();
        layoutParams6.setMarginStart(dimension);
        layoutParams6.setMarginEnd(dimension);
        layoutParams6.topMargin = (int) getResources().getDimension(R.dimen.sleep_no_disturbance_margin_top);
        this.mNoDisturbanceView.setLayoutParams(layoutParams6);
        RelativeLayout.LayoutParams layoutParams7 = (RelativeLayout.LayoutParams) this.mCompleteImage.getLayoutParams();
        layoutParams7.topMargin = (int) getResources().getDimension(R.dimen.bedtime_setting_complete_ic_margin_top);
        this.mCompleteImage.setLayoutParams(layoutParams7);
        LinearLayout.LayoutParams layoutParams8 = (LinearLayout.LayoutParams) this.mWelcomeTitle.getLayoutParams();
        layoutParams8.topMargin = (int) getResources().getDimension(R.dimen.bedtime_guide_title_margin_top);
        this.mWelcomeTitle.setLayoutParams(layoutParams8);
        RelativeLayout.LayoutParams layoutParams9 = (RelativeLayout.LayoutParams) this.mWakeTimeTitle.getLayoutParams();
        layoutParams9.topMargin = (int) getResources().getDimension(R.dimen.bedtime_guide_title_margin_top);
        this.mWakeTimeTitle.setLayoutParams(layoutParams9);
        LinearLayout.LayoutParams layoutParams10 = (LinearLayout.LayoutParams) this.mSleepTimeTitle.getLayoutParams();
        layoutParams10.topMargin = (int) getResources().getDimension(R.dimen.bedtime_guide_title_margin_top);
        this.mSleepTimeTitle.setLayoutParams(layoutParams10);
        LinearLayout.LayoutParams layoutParams11 = (LinearLayout.LayoutParams) this.mRepeatTitle.getLayoutParams();
        layoutParams11.topMargin = (int) getResources().getDimension(R.dimen.bedtime_guide_title_margin_top);
        this.mRepeatTitle.setLayoutParams(layoutParams11);
        LinearLayout.LayoutParams layoutParams12 = (LinearLayout.LayoutParams) this.mDndTitle.getLayoutParams();
        layoutParams12.topMargin = (int) getResources().getDimension(R.dimen.bedtime_guide_title_margin_top);
        this.mDndTitle.setLayoutParams(layoutParams12);
    }

    private void initCetusView() {
        if (!Util.isDeviceCetus() || Util.isInInternalScreen(this)) {
            return;
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.bedtime_guide_secondary_cetus_button_width), -2);
        layoutParams.setMarginStart((int) getResources().getDimension(R.dimen.bedtime_guide_cetus_button_margin_start));
        this.backButton.setLayoutParams(layoutParams);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.bedtime_guide_secondary_cetus_button_width), -2);
        layoutParams2.addRule(21);
        layoutParams2.setMarginEnd((int) getResources().getDimension(R.dimen.bedtime_guide_cetus_button_margin_start));
        this.nextButton.setLayoutParams(layoutParams2);
    }

    @Override // com.android.deskclock.base.BaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1 || i == 2) {
            if (i2 == -3) {
                showNetPermissionDialog();
                return;
            }
            if (i2 == 1) {
                UserNoticeUtil.setAcceptNetPermission(true);
                StatHelper.init(DeskClockApp.getAppContext());
                OneTrackStatHelper.init(DeskClockApp.getAppContext());
                this.mViewPager.setCurrentItem(4);
                return;
            }
            if (i2 == 666 || i2 == 0) {
                UserNoticeUtil.setAcceptNetPermission(false);
                UserNoticeUtil.setRemindNetPermission(false);
            } else {
                Log.e(SystemPermissionUtil.TAG, "lack of important information");
            }
        }
    }

    private void showNetPermissionDialog() {
        if (SystemPermissionUtil.showPermissionDeclare(this, 1, 2)) {
            return;
        }
        this.mNetPermissionDialog = UserNoticeUtil.showUserNoticeDialog(this, R.string.dialog_net_permission_in_repeat_type, new UserNoticeUtil.OnNetPermissionListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeGuideActivity.13
            @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
            public void onReject() {
            }

            @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
            public void onAccept() {
                BedtimeGuideActivity.this.mViewPager.setCurrentItem(4);
            }
        }, getSupportFragmentManager());
    }

    private boolean canShowIndicator() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics.heightPixels > MIN_HEIGHT_PIXELS;
    }

    private int transRepeatTypeToIndex(Alarm.DaysOfWeek daysOfWeek) {
        int coded = daysOfWeek.getCoded();
        if (coded != 127) {
            return coded != 128 ? 0 : 2;
        }
        return 1;
    }

    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        MiHomeHelper miHomeHelper = this.miHomeHelper;
        if (miHomeHelper != null) {
            miHomeHelper.release();
            this.miHomeHelper = null;
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, miuix.responsive.interfaces.IResponsive
    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        super.onResponsiveLayout(configuration, screenSpec, z);
        if (ResponsiveUtil.inFreeFormWindow(screenSpec) || ResponsiveUtil.inExternalSplitScreen(screenSpec, this)) {
            initFreeFormScreenView();
        } else {
            updateFullView();
        }
    }

    public void notifyBedtimeChanged(Context context) {
        Log.d(TAG, "notifyBedtimeChanged");
        if (this.miHomeHelper == null) {
            this.miHomeHelper = new MiHomeHelper(context);
        }
        this.miHomeHelper.notifyBedtimeChanged();
    }
}
