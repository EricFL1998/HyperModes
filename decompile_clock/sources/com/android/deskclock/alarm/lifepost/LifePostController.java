package com.android.deskclock.alarm.lifepost;

import android.animation.Animator;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.weather.WeatherAQIData;
import com.android.deskclock.addition.weather.WeatherInfo;
import com.android.deskclock.addition.weather.WeatherType;
import com.android.deskclock.addition.weather.WeatherUtils;
import com.android.deskclock.alarm.AlarmModel;
import com.android.deskclock.alarm.alert.AlarmAlertFullScreenActivity;
import com.android.deskclock.alarm.alert.AlertAlarmFragment;
import com.android.deskclock.alarm.bedtime.BedtimeManageActivity;
import com.android.deskclock.alarm.bedtime.SleepReport;
import com.android.deskclock.alarm.lifepost.model.News;
import com.android.deskclock.alarm.lifepost.model.NewsHelper;
import com.android.deskclock.alarm.lifepost.model.RecommendViewModel;
import com.android.deskclock.alarm.lifepost.okhttp.Net;
import com.android.deskclock.util.AnimationUtils;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.NetworkUtil;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.widget.CircleImageView;
import com.xiaomi.settingsdk.backup.SettingsBackupConsts;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class LifePostController implements ILifePostView, View.OnClickListener {
    private static final long ANIMATE_IN_DURATION = 500;
    public static final String MI_PLAYER_PKG_NAME = "com.miui.player";
    private static final int MI_PLAYER_VERSION = 120042;
    private static final String OPEN_KEY_GUARD = "xiaomi.intent.action.SHOW_SECURE_KEYGUARD";
    private static final String TAG = "DC:LifePostController";
    private static final long TIME_ROUND_PLAY = 4000;
    private static final String URL_TYPE_APP = "app";
    private static final String URL_TYPE_H5 = "h5";
    private AlertAlarmFragment mAlertAlarmFragment;
    private AnimateListener mAnimateListener;
    private Context mContext;
    private ImageButton mDismissPostLifeView;
    private TextView mFBENoNewsView;
    private TextView mGetUpTimeView;
    private boolean mIsRoundPlay;
    private View mLifePostInfo;
    private long mLifePostStartTime;
    private List<News> mNews;
    private TextView mNewsDetailsView;
    private ImageView mNewsStartIv;
    private TextView mNewsTitleView;
    private boolean mOpenNews;
    private LifePostDragHelperLayout mRoot;
    private View mRootView;
    private Runnable mRoundPlayRunnable;
    private boolean mShowSleep;
    private TextView mSleepDurationView;
    private LinearLayout mSleepReportView;
    private TextView mSleepTimeView;
    private TextView mTemparatureDegree;
    private TextView mTemparatureHigh;
    private TextView mTemparatureLow;
    private TextView mWakeUpPercentageView;
    private TextView mWakeUpTimeView;
    private LinearLayout mWakeUpView;
    private TextView mWakeUpmAcumulateView;
    private TextView mWeatherApiLevelView;
    private TextView mWeatherCityNameView;
    private WeatherInfo mWeatherInfo;
    private View mWeatherInfoContainer;
    private TextView mWeatherInfoGetView;
    private ImageView mWeatherIv;
    private View mWeatherNewsContainer;
    private CircleImageView mWeatherNewsContentIv;
    private int mWeatherType;
    private TextView mWeatherTypeView;
    private Map<String, Bitmap> mNewsPicMap = new HashMap();
    private int mListenNewsCandidatesIndex = 0;
    private int mListenNewsRoundPlayIndex = 0;
    private int mListenNewsCandidatesLength = -1;
    private View.OnClickListener mWeatherListener = new View.OnClickListener() { // from class: com.android.deskclock.alarm.lifepost.LifePostController.3
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            StatHelper.recordCountEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_CLICK_WEATHER);
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.LIFE_POST_WEATHER_CLICK);
            WeatherUtils.startWeatherOrInstall(LifePostController.this.mContext);
            LifePostController.this.sendKeyGuardBroadcast();
            LifePostController.this.finishActivity();
        }
    };
    private LifePostPresenter mLifePostPresenter = new LifePostPresenter(this);
    private Handler mHandler = new Handler();

    static /* synthetic */ int access$408(LifePostController lifePostController) {
        int i = lifePostController.mListenNewsRoundPlayIndex;
        lifePostController.mListenNewsRoundPlayIndex = i + 1;
        return i;
    }

    public LifePostController(LifePostDragHelperLayout lifePostDragHelperLayout, Context context, View view, AlertAlarmFragment alertAlarmFragment, boolean z) {
        this.mRoot = lifePostDragHelperLayout;
        this.mShowSleep = z;
        this.mContext = context;
        this.mAlertAlarmFragment = alertAlarmFragment;
        this.mRootView = view;
        initChildViews();
        initViewData();
        this.mAnimateListener = new AnimateListener();
        this.mRoundPlayRunnable = new Runnable() { // from class: com.android.deskclock.alarm.lifepost.LifePostController.1
            @Override // java.lang.Runnable
            public void run() {
                if (!LifePostController.this.mIsRoundPlay || LifePostController.this.mListenNewsCandidatesLength <= 0) {
                    Log.v("round play stop");
                    return;
                }
                Log.d("mRoundPlayRunnable is running");
                LifePostController lifePostController = LifePostController.this;
                lifePostController.mListenNewsCandidatesIndex = lifePostController.mListenNewsRoundPlayIndex;
                LifePostController.this.updateListenNewsDesc();
                AnimationUtils.animateAlpha(LifePostController.this.mWeatherNewsContentIv, 1.0f, 0.2f, LifePostController.ANIMATE_IN_DURATION, 0L, LifePostController.this.mAnimateListener);
                if (LifePostController.this.mListenNewsRoundPlayIndex == LifePostController.this.mListenNewsCandidatesLength - 1) {
                    LifePostController.this.mListenNewsRoundPlayIndex = 0;
                } else {
                    LifePostController.access$408(LifePostController.this);
                }
                LifePostController.this.mHandler.postAtTime(LifePostController.this.mRoundPlayRunnable, SystemClock.uptimeMillis() + LifePostController.TIME_ROUND_PLAY);
            }
        };
    }

    private void initChildViews() {
        float dimension;
        this.mLifePostInfo = this.mRootView.findViewById(R.id.life_post_info);
        this.mRootView.findViewById(R.id.wake_up_time_container).setOnClickListener(this);
        this.mRootView.findViewById(R.id.beat_container).setOnClickListener(this);
        this.mRootView.findViewById(R.id.acumulate_container).setOnClickListener(this);
        ImageButton imageButton = (ImageButton) this.mRootView.findViewById(R.id.dismiss_life_post);
        this.mDismissPostLifeView = imageButton;
        imageButton.setImageResource(R.drawable.life_post_close);
        this.mDismissPostLifeView.setBackground(this.mContext.getResources().getDrawable(R.drawable.dismiss_life_post_bg));
        this.mDismissPostLifeView.setOnClickListener(this);
        this.mWeatherTypeView = (TextView) this.mRootView.findViewById(R.id.weather_info_weather_tv);
        this.mWeatherApiLevelView = (TextView) this.mRootView.findViewById(R.id.weather_api_level);
        this.mWeatherCityNameView = (TextView) this.mRootView.findViewById(R.id.weather_city_name);
        this.mWeatherInfoGetView = (TextView) this.mRootView.findViewById(R.id.weather_info_get);
        this.mWeatherIv = (ImageView) this.mRootView.findViewById(R.id.weather_info_weather_iv);
        this.mTemparatureLow = (TextView) this.mRootView.findViewById(R.id.weather_temp_low);
        this.mTemparatureHigh = (TextView) this.mRootView.findViewById(R.id.weather_temp_high);
        this.mWeatherNewsContentIv = (CircleImageView) this.mRootView.findViewById(R.id.weather_info_news_content_iv);
        this.mNewsTitleView = (TextView) this.mRootView.findViewById(R.id.news_title);
        this.mNewsDetailsView = (TextView) this.mRootView.findViewById(R.id.news_details);
        ImageView imageView = (ImageView) this.mRootView.findViewById(R.id.weather_info_news_start_iv);
        this.mNewsStartIv = imageView;
        imageView.setOnClickListener(this);
        this.mWakeUpView = (LinearLayout) this.mRootView.findViewById(R.id.life_post_wake_up);
        this.mWakeUpTimeView = (TextView) this.mRootView.findViewById(R.id.wake_up_time);
        this.mWakeUpPercentageView = (TextView) this.mRootView.findViewById(R.id.beat_percentage);
        this.mWakeUpmAcumulateView = (TextView) this.mRootView.findViewById(R.id.acumulate_day);
        this.mTemparatureDegree = (TextView) this.mRootView.findViewById(R.id.temperature_degree);
        this.mFBENoNewsView = (TextView) this.mRootView.findViewById(R.id.life_post_fbe_no_news);
        this.mWeatherInfoContainer = this.mRootView.findViewById(R.id.weather_info_container);
        View viewFindViewById = this.mRootView.findViewById(R.id.listen_news_container);
        this.mWeatherNewsContainer = viewFindViewById;
        viewFindViewById.setOnClickListener(this);
        this.mSleepReportView = (LinearLayout) this.mRootView.findViewById(R.id.life_post_bedtime_wake_up);
        this.mSleepTimeView = (TextView) this.mRootView.findViewById(R.id.sleep_time);
        this.mGetUpTimeView = (TextView) this.mRootView.findViewById(R.id.get_up_time);
        this.mSleepDurationView = (TextView) this.mRootView.findViewById(R.id.sleep_duration);
        View view = this.mLifePostInfo;
        if (view != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            if (PadAdapterUtil.IS_PAD) {
                dimension = DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.life_post_weather_info_container_pad_margin_left);
            } else {
                dimension = DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.life_post_weather_info_container_margin_left);
            }
            int i = (int) dimension;
            layoutParams.setMarginStart(i);
            layoutParams.setMarginEnd(i);
            this.mLifePostInfo.setLayoutParams(layoutParams);
        }
    }

    public void getSleepReport() {
        if (this.mShowSleep) {
            this.mLifePostPresenter.loadSleepReport();
        }
    }

    private void initViewData() {
        this.mLifePostPresenter.loadAcumulateDay();
        boolean z = FBEUtil.getDefaultSharedPreferences(this.mContext).getBoolean("key_open_alarm_life_post_news", false);
        this.mOpenNews = z;
        if (z && Util.isChinese(this.mContext)) {
            getListenNews(Net.Param.NEWS);
        } else {
            StatHelper.recordCountEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_LIFE_POST_NEWS_GONE_COUNT);
            OneTrackStatHelper.trackTriggerEvent(OneTrackStatHelper.LIFE_POST_NEWS_GONE);
        }
    }

    public void showLifePostNewsBackground(int i) {
        showLifePostNewsBackground(null, i);
    }

    public void showLifePostNewsBackground(RecommendViewModel recommendViewModel, int i) {
        Log.d("showLifePostNewsBackground(), weatherType:" + this.mWeatherType);
        int shownWeatherType = WeatherType.getShownWeatherType(i);
        this.mWeatherType = shownWeatherType;
        switch (shownWeatherType) {
            case 0:
                this.mWeatherIv.setImageResource(R.drawable.weather_sunny_iv);
                break;
            case 1:
                this.mWeatherIv.setImageResource(R.drawable.weather_cloudy_iv);
                break;
            case 2:
                this.mWeatherIv.setImageResource(R.drawable.weather_overcast_iv);
                break;
            case 3:
                this.mWeatherIv.setImageResource(R.drawable.weather_rain_iv);
                break;
            case 4:
                this.mWeatherIv.setImageResource(R.drawable.weather_snow_iv);
                break;
            case 5:
                this.mWeatherIv.setImageResource(R.drawable.weather_fog_iv);
                break;
            case 6:
                this.mWeatherIv.setImageResource(R.drawable.weather_haze_iv);
                break;
            case 7:
                this.mWeatherIv.setImageResource(R.drawable.weather_small_sand_iv);
                break;
            default:
                this.mDismissPostLifeView.setColorFilter(this.mContext.getResources().getColor(R.color.life_post_weather_sunny_text_color));
                break;
        }
    }

    private void getListenNews(String str) {
        new ListenNewsAsyncTask(this, str).execute(new Void[0]);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.acumulate_container /* 2131361890 */:
                StatHelper.recordCountEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_CLCIK_ACUMULATE_DAY);
                break;
            case R.id.beat_container /* 2131361974 */:
                StatHelper.recordCountEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_CLICK_BEAT_PERCENTAGE);
                break;
            case R.id.dismiss_life_post /* 2131362155 */:
                Log.f(TAG, "click dismiss life post button");
                recordDismissLifePostTime();
                PrefUtil.setLifePostCloseTime(System.currentTimeMillis());
                PrefUtil.setNewsUpdateTime(-1L);
                PrefUtil.setGalleryUpdateTime(-1L);
                ((AlarmAlertFullScreenActivity) this.mContext).finish();
                break;
            case R.id.listen_news_container /* 2131362342 */:
                Log.f(TAG, "click news item");
                listenNewsToMusicPlayer();
                StatHelper.trackEvent(StatHelper.EVENT_LIFE_POST_NEWS_START, "ITEM");
                OneTrackStatHelper.trackStringEvent("ITEM", OneTrackStatHelper.LIFE_POST_NEWS_ITEM_CLICK_VALUE);
                break;
            case R.id.wake_up_time_container /* 2131362981 */:
                StatHelper.recordCountEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_CLICK_WAKE_UP_TIME);
                break;
            case R.id.weather_info_news_start_iv /* 2131362987 */:
                Log.f(TAG, "click news button");
                listenNewsToMusicPlayer();
                StatHelper.trackEvent(StatHelper.EVENT_LIFE_POST_NEWS_START, "BUTTON");
                OneTrackStatHelper.trackStringEvent("BUTTON", OneTrackStatHelper.LIFE_POST_NEWS_ITEM_CLICK_VALUE);
                break;
        }
    }

    private void listenNewsToMusicPlayer() {
        sendKeyGuardBroadcast();
        if (Util.getPackageCode(MI_PLAYER_PKG_NAME) < MI_PLAYER_VERSION) {
            try {
                Intent intent = new Intent("android.intent.action.VIEW", new Uri.Builder().scheme("market").encodedAuthority("details").appendQueryParameter(SettingsBackupConsts.EXTRA_PACKAGE_NAME, MI_PLAYER_PKG_NAME).appendQueryParameter("back", "true").build());
                intent.setPackage("com.xiaomi.market");
                intent.addFlags(268435456);
                DeskClockApp.getAppDEContext().startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "downloadHealthApp error, " + e.getMessage());
            }
            finishActivity();
            return;
        }
        String urlType = this.mNews.get(this.mListenNewsCandidatesIndex).getUrlType();
        String urlApp = this.mNews.get(this.mListenNewsCandidatesIndex).getUrlApp();
        String url = this.mNews.get(this.mListenNewsCandidatesIndex).getUrl();
        Log.f(TAG, "listenNewsToMusicPlayer, urlType: " + urlType + ", urlApp: " + urlApp + ", url: " + url);
        if (URL_TYPE_APP.equals(urlType)) {
            try {
                openApp(urlApp);
                finishActivity();
                return;
            } catch (Exception unused) {
                Log.e("listenNewsToMusicPlayer(), app in uninstall");
                Toast.makeText(DeskClockApp.getAppDEContext(), R.string.install_news_app_error, 0).show();
                return;
            }
        }
        if (URL_TYPE_H5.equals(urlType)) {
            openApp(url);
            finishActivity();
        } else {
            try {
                openApp(urlApp);
            } catch (Exception unused2) {
                Log.e("listenNewsToMusicPlayer(), app in uninstall and open h5");
                openApp(url);
            }
            finishActivity();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishActivity() {
        PrefUtil.setLifePostCloseTime(System.currentTimeMillis());
        PrefUtil.setNewsUpdateTime(-1L);
        PrefUtil.setGalleryUpdateTime(-1L);
        ((AlarmAlertFullScreenActivity) this.mContext).finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendKeyGuardBroadcast() {
        if (((KeyguardManager) this.mContext.getSystemService("keyguard")).isKeyguardLocked()) {
            this.mContext.sendBroadcast(new Intent(OPEN_KEY_GUARD));
        }
    }

    private void openApp(String str) {
        this.mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
    }

    public void recordShowLifePostTime() {
        this.mLifePostStartTime = System.currentTimeMillis();
    }

    private void recordDismissLifePostTime() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(jCurrentTimeMillis);
        StatHelper.recordCountEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_CLICK_CLOSE_LIFE_POST, StatHelper.buildLifePostCloseTimeMap(String.valueOf(calendar.get(11))));
        OneTrackStatHelper.trackNumEvent(calendar.get(11), OneTrackStatHelper.LIFE_POST_PAGE_CLOSE);
        StatHelper.recordNumericPropertyEvent(StatHelper.CATEGORY_ALARM_COMMON, StatHelper.KEY_LIFE_POST_VISIBLE_DURATION, ((long) ((int) (jCurrentTimeMillis - this.mLifePostStartTime))) / 1000);
        OneTrackStatHelper.trackNumEvent(((long) ((int) (jCurrentTimeMillis - this.mLifePostStartTime))) / 1000, OneTrackStatHelper.LIFE_POST_PAGE_VISIBLE_DURATION);
    }

    public void onDestroy() {
        this.mLifePostPresenter.onDestroy();
        this.mHandler.removeCallbacksAndMessages(null);
    }

    public void updateWakeUpTime(long j) {
        this.mLifePostPresenter.updateWakeUpTime(j);
    }

    public void saveWakeUp() {
        this.mLifePostPresenter.saveWakeUpDay();
    }

    @Override // com.android.deskclock.alarm.lifepost.ILifePostView
    public void setAcumulateDay(int i) {
        this.mWakeUpmAcumulateView.setText(String.valueOf(i));
    }

    @Override // com.android.deskclock.alarm.lifepost.ILifePostView
    public void setSleepReport(SleepReport sleepReport) {
        if (sleepReport == null || sleepReport.duration == 0) {
            this.mSleepReportView.setVisibility(8);
            this.mWakeUpView.setVisibility(0);
            return;
        }
        this.mWakeUpView.setVisibility(8);
        this.mSleepReportView.setVisibility(0);
        this.mSleepReportView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.lifepost.LifePostController.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent(LifePostController.this.mContext, (Class<?>) BedtimeManageActivity.class);
                intent.setPackage(LifePostController.this.mContext.getPackageName());
                LifePostController.this.mContext.startActivity(intent);
                StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.LIFE_SLEEP_CLICK);
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.BEDTIME_LIFE_POST_ITEM_CLICK);
                LifePostController.this.sendKeyGuardBroadcast();
                LifePostController.this.finishActivity();
            }
        });
        this.mSleepTimeView.setText((String) DateFormat.format(AlarmModel.M24, sleepReport.beginTime));
        this.mGetUpTimeView.setText((String) DateFormat.format(AlarmModel.M24, sleepReport.endTime));
        this.mSleepDurationView.setText(String.valueOf(((double) ((int) ((((double) sleepReport.duration) / 60.0d) * 100.0d))) / 100.0d));
        StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.LIFE_SLEEP_SHOW);
        OneTrackStatHelper.trackViewEvent(OneTrackStatHelper.BEDTIME_LIFE_POST_ITEM_SHOW);
    }

    @Override // com.android.deskclock.alarm.lifepost.ILifePostView
    public void setWakeUpTime(CharSequence charSequence) {
        this.mWakeUpTimeView.setText(charSequence);
    }

    @Override // com.android.deskclock.alarm.lifepost.ILifePostView
    public void setWakeUpPercentage(String str) {
        this.mWakeUpPercentageView.setText(str);
    }

    @Override // com.android.deskclock.alarm.lifepost.ILifePostView
    public void setWeatherClickListener(View view, View.OnClickListener onClickListener) {
        view.setOnClickListener(onClickListener);
    }

    public void setWeatherInfo(WeatherInfo weatherInfo) {
        this.mWeatherInfo = weatherInfo;
        updateWeatherInfo();
    }

    private void updateListenNewsTitle() {
        this.mNewsTitleView.setText(this.mNews.get(this.mListenNewsCandidatesIndex).getTitle());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateListenNewsDesc() {
        this.mNewsDetailsView.setText(this.mNews.get(this.mListenNewsCandidatesIndex).getDesc());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNewsImage() {
        String pic = this.mNews.get(this.mListenNewsCandidatesIndex).getPic();
        Bitmap bitmap = this.mNewsPicMap.get(pic);
        if (bitmap != null && !bitmap.isRecycled()) {
            this.mWeatherNewsContentIv.setImageBitmap(bitmap);
        } else {
            new DecodeBitmapAsyncTask(this.mAlertAlarmFragment, this.mWeatherNewsContentIv, pic, (int) this.mContext.getResources().getDimension(R.dimen.life_post_weather_news_content_iv_width), (int) this.mContext.getResources().getDimension(R.dimen.life_post_weather_news_content_iv_width), R.drawable.news_content_default_iv).execute(new Void[0]);
        }
    }

    private void updateWeatherInfo() {
        if (this.mWeatherInfo == null) {
            if (!NetworkUtil.isNetworkConnected()) {
                this.mWeatherInfoContainer.setVisibility(8);
                this.mWeatherInfoGetView.setVisibility(0);
                this.mWeatherInfoGetView.setText(R.string.life_post_weather_no_network);
                this.mWeatherInfoGetView.setContentDescription(this.mContext.getResources().getString(R.string.life_post_weather_no_network));
            } else {
                this.mWeatherInfoContainer.setVisibility(8);
                this.mWeatherInfoGetView.setVisibility(0);
                this.mWeatherInfoGetView.setText(R.string.life_post_weather_invalid);
                this.mWeatherInfoGetView.setContentDescription(this.mContext.getResources().getString(R.string.life_post_weather_invalid));
            }
            setWeatherClickListener(this.mWeatherInfoGetView, this.mWeatherListener);
        } else {
            this.mWeatherInfoContainer.setVisibility(0);
            this.mWeatherInfoGetView.setVisibility(8);
            setWeatherClickListener(this.mWeatherInfoContainer, this.mWeatherListener);
            String str = this.mWeatherInfo.temperature;
            this.mTemparatureDegree.setText(appendTemp(str.substring(0, str.length() - 1)));
            this.mWeatherTypeView.setText(this.mWeatherInfo.weather);
            this.mWeatherCityNameView.setText(this.mWeatherInfo.cityName);
            this.mWeatherApiLevelView.setText(WeatherAQIData.getAqiValue(this.mContext, this.mWeatherInfo.aqilevel));
            if (WeatherType.isFogOrPmdirt(this.mWeatherInfo.weatherType)) {
                this.mWeatherApiLevelView.setVisibility(4);
            }
            this.mWeatherCityNameView.setText(this.mWeatherInfo.cityName);
            this.mTemparatureLow.setText(String.valueOf(this.mWeatherInfo.tempLow));
            this.mTemparatureHigh.setText(String.valueOf(this.mWeatherInfo.tempHigh));
        }
        StatHelper.trackEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_WEATHER_PROPERTY, this.mWeatherInfo == null ? "INVALID" : "VALID");
        OneTrackStatHelper.trackBoolEvent(this.mWeatherInfo != null, OneTrackStatHelper.LIFE_POST_WEATHER_INVALID);
    }

    public int getWeatherType() {
        WeatherInfo weatherInfo = this.mWeatherInfo;
        if (weatherInfo != null) {
            return weatherInfo.weatherType;
        }
        return -1;
    }

    private String appendTemp(String str) {
        return str + this.mContext.getResources().getString(R.string.temperature_unit);
    }

    public void onTrimMemory(int i) {
        if (i >= 20) {
            this.mIsRoundPlay = false;
            this.mHandler.removeCallbacksAndMessages(null);
        }
    }

    public void startNewsRoundPlay() {
        if (this.mRoot.getMovable() || this.mListenNewsCandidatesLength <= 0 || this.mIsRoundPlay) {
            return;
        }
        StatHelper.recordCountEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_LIFE_POST_NEWS_VISIBLE);
        OneTrackStatHelper.trackViewEvent(OneTrackStatHelper.LIFE_POST_NEWS_VISIBLE);
        this.mIsRoundPlay = true;
        this.mRoundPlayRunnable.run();
    }

    private class AnimateListener implements Animator.AnimatorListener {
        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
        }

        private AnimateListener() {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            if (animator != null) {
                animator.removeAllListeners();
            }
            LifePostController.this.setNewsImage();
            AnimationUtils.animateAlpha(LifePostController.this.mWeatherNewsContentIv, 0.2f, 1.0f, LifePostController.ANIMATE_IN_DURATION, 0L, null);
        }
    }

    private static class ListenNewsAsyncTask extends AsyncTask<Void, Void, List<News>> {
        private String mItemId;
        private WeakReference<LifePostController> mLifePostControllerRef;

        public ListenNewsAsyncTask(LifePostController lifePostController, String str) {
            this.mItemId = str;
            this.mLifePostControllerRef = new WeakReference<>(lifePostController);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public List<News> doInBackground(Void... voidArr) {
            List<News> newsByType = NewsHelper.getNewsByType(DeskClockApp.getAppContext(), this.mItemId);
            if (newsByType == null || newsByType.size() == 0) {
                Log.i(LifePostController.TAG, "news(" + this.mItemId + ") is empty, skip");
                return null;
            }
            long expire = newsByType.get(0).getExpire();
            boolean zIsShow = newsByType.get(0).isShow();
            if (zIsShow && expire >= System.currentTimeMillis()) {
                return newsByType;
            }
            Log.i(LifePostController.TAG, "news(" + this.mItemId + ") show=" + zIsShow + ", expire=" + expire + ", skip");
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(List<News> list) {
            WeakReference<LifePostController> weakReference = this.mLifePostControllerRef;
            LifePostController lifePostController = weakReference == null ? null : weakReference.get();
            if (lifePostController != null) {
                lifePostController.handleNews(list, this.mItemId);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNews(List<News> list, String str) {
        if (list == null) {
            if (!Net.Param.NEWS_BACKUP.equals(str)) {
                getListenNews(Net.Param.NEWS_BACKUP);
                return;
            } else {
                this.mWeatherNewsContainer.setVisibility(8);
                return;
            }
        }
        if (FBEUtil.isLockedUnderFBE(this.mContext)) {
            Log.d("ListenNewsAsyncTask is fbe mode");
            this.mWeatherNewsContainer.setVisibility(8);
            this.mFBENoNewsView.setVisibility(0);
            StatHelper.recordCountEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_LIFE_POST_NEWS_FBE_COUNT);
            OneTrackStatHelper.trackTriggerEvent(OneTrackStatHelper.LIFE_POST_NEWS_FBE_GONE);
            return;
        }
        this.mNews = list;
        Log.i("DC:", "show news count: " + this.mNews.size());
        this.mListenNewsCandidatesLength = this.mNews.size();
        this.mWeatherNewsContainer.setVisibility(0);
        Log.d("ListenNewsAsyncTask() onPostExecute() and to update the NewsBean UI!");
        updateListenNewsTitle();
        updateListenNewsDesc();
        setNewsImage();
        startNewsRoundPlay();
    }

    private static class DecodeBitmapAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        private WeakReference<AlertAlarmFragment> mAlertAlarmFragment;
        private int mDefaultResId;
        private ImageView mImageView;
        private String mPath;
        private int mRepHeight;
        private int mRepWidth;

        public DecodeBitmapAsyncTask(AlertAlarmFragment alertAlarmFragment, ImageView imageView, String str, int i, int i2, int i3) {
            this.mAlertAlarmFragment = new WeakReference<>(alertAlarmFragment);
            this.mImageView = imageView;
            this.mPath = str;
            this.mRepHeight = i2;
            this.mRepWidth = i;
            this.mDefaultResId = i3;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Bitmap doInBackground(Void... voidArr) {
            return Util.decodeBitmap(this.mPath, this.mRepWidth, this.mRepHeight);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                this.mImageView.setImageBitmap(bitmap);
            } else {
                this.mImageView.setImageResource(this.mDefaultResId);
            }
        }
    }

    public void resetLifePostLayout() {
        float dimension;
        View view = this.mLifePostInfo;
        if (view != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            if (PadAdapterUtil.IS_PAD) {
                dimension = DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.life_post_weather_info_container_pad_margin_left);
            } else {
                dimension = DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.life_post_weather_info_container_margin_left);
            }
            int i = (int) dimension;
            layoutParams.setMarginStart(i);
            layoutParams.setMarginEnd(i);
            this.mLifePostInfo.setLayoutParams(layoutParams);
        }
    }
}
