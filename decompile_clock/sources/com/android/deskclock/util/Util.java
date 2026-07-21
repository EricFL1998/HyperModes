package com.android.deskclock.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.Settings;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.android.deskclock.BuildConfig;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.base.BaseActivity;
import com.android.deskclock.compat.ClockCompat;
import com.android.deskclock.settings.AlarmSettingsActivity;
import com.android.deskclock.view.TypefaceTextView;
import com.android.deskclock.worldclock.WorldClockEditActivity;
import com.miui.miwallpaper.MiuiWallpaperManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import miui.content.res.ThemeResources;
import miui.os.Build;
import miui.settings.commonlib.SoundDefaultValueUtil;
import miuix.animation.FolmeEase;
import miuix.appcompat.app.AppCompatActivity;
import miuix.core.util.SystemProperties;
import miuix.core.util.WindowUtils;
import miuix.os.DeviceHelper;
import miuix.pickerwidget.date.DateUtils;

/* JADX INFO: loaded from: classes.dex */
public class Util {
    private static final String ACTION_SET_FPS = "com.miui.powerkeeper.SET_ACTIVITY_FPS";
    public static final int ALARM_TABINDEX = 0;
    public static final String ANIM_BACK_KEY = "disableBackAnimation";
    public static final int CIRCLE_DEGREES = 360;
    public static final String CURRENT_TIME_MILLS_OFFSET = "current_time_mills_offset";
    private static final String DATA_KEY = "data_trans_history";
    private static final String DEFAULT_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DELETE_SOUND_SETTING = "delete_sound_effect";
    private static String ENGLISH_LANGUAGE = null;
    private static final String HAS_RESTORED = "has_restored";
    public static final String INTENT_FROM = "intent_from";
    public static final String IS_ANIM_BACK_KEY = "isAnimBack";
    public static final String IS_FROM_CTS_SET_ALARM = "is_from_cts_set_alarm";
    public static final String IS_NIGHT_MODE_KEY = "isNightMode";
    public static final String IS_START_TIMER = "is_start_timer";
    public static final String IS_SWITCH_NIGHT_MODE = "isSwitchNightMode";
    public static final int MAIN_SPACE_ID = 0;
    private static final String MISHOW_RECOGNIZE_KEY = "disable_security_by_mishow";
    public static final String NAVIGATION_TAB = "navigation_tab";
    public static final String PATH_DELETE_OGG = "/system/media/audio/ui/Delete.ogg";
    public static final String POWER_PACKAGE_NAME = "com.miui.powerkeeper";
    private static final String PREF_MISHOW_ON_FOREGROUND = "mishow_on_foreground";
    public static final int SCROLL_STATE_HAND_UP = 2;
    public static final int SCROLL_STATE_RUNNING = 1;
    private static final int SKIP_DATE_FORMAT = 4480;
    public static final String STOPWATCH_BASE_TIME_PREF = "stopwatch_base_time_pref";
    public static final String STOPWATCH_ELAPSED_TIME_PREF = "stopwatch_elapsed_time_pref";
    public static final String STOPWATCH_STATE_RUNNING_PREF = "stopwatch_state_running_pref";
    public static final int STOPWATCH_TABINDEX = 2;
    private static final String TAG = "DC:Util";
    public static final int TIMER_TABINDEX = 3;
    public static final int TIMER_TINY_SCREEN_TABINDEX = 1;
    private static final String TIME_FORMAT = "%02d:%02d.%02d";
    private static final String TIME_FORMAT_SPAN = "%02d:%02d%02d";
    private static final String TIME_FORMAT_WITH_HOUR = "%02d:%02d:%02d.%02d";
    private static final String TIME_FORMAT_WITH_HOUR_SPAN = "%02d:%02d:%02d%02d";
    private static final String TIME_FORMAT_WITH_SPAN = "%02d:%02d:%02d";
    public static final int UI_MODE_PC_MASK = 12288;
    public static final int UI_MODE_PC_YES = 8192;
    public static final int WORLDCLOCK_TABINDEX = 1;
    private static boolean isInSecureSpace;
    private static boolean mIsRtl;
    private static MiuiWallpaperManager mMiuiWallpaperManager;
    private static Formatter sFormatter;
    public static final boolean IS_NEXUS_5 = "hammerhead".equals(Build.DEVICE);
    private static boolean IS_KDDI_CUSTOMIZED = false;
    private static boolean HAS_EXTRA = false;
    private static final StringBuilder sStringBuilder = new StringBuilder(57);

    static {
        mIsRtl = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
        ENGLISH_LANGUAGE = new Locale("en").getLanguage();
    }

    public static boolean isInSecureSpace() {
        return isInSecureSpace;
    }

    public static void init(Context context) {
        isInSecureSpace = !isApplicationInMainSpace();
        try {
            IS_KDDI_CUSTOMIZED = "jp_kd".equals(SystemProperties.get("ro.miui.customized.region"));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static String formatDigits(long j) {
        return j < 10 ? WorldClockEditActivity.LOCAL_CITY_ID + j : String.valueOf(j);
    }

    public static synchronized String formatTime(String str, Object... objArr) {
        String string;
        initFormatter();
        string = sFormatter.format(Locale.getDefault(), str, objArr).toString();
        StringBuilder sb = sStringBuilder;
        sb.delete(0, sb.length());
        return string;
    }

    public static void initFormatter() {
        if (sFormatter == null) {
            sFormatter = new Formatter(sStringBuilder, Locale.getDefault());
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmapCreateBitmap;
    }

    public static boolean isDeviceLandScape(Context context) {
        int rotation = ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRotation();
        return rotation == 1 || rotation == 3;
    }

    public static void saveClockTimeOffset(SharedPreferences sharedPreferences) {
        sharedPreferences.edit().putLong(CURRENT_TIME_MILLS_OFFSET, System.currentTimeMillis() - SystemClock.elapsedRealtime()).commit();
    }

    public static void adjustStopwatchBaseTime(SharedPreferences sharedPreferences) {
        long jCurrentTimeMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime();
        long j = sharedPreferences.getLong(CURRENT_TIME_MILLS_OFFSET, jCurrentTimeMillis);
        if (j != jCurrentTimeMillis && sharedPreferences.contains(STOPWATCH_BASE_TIME_PREF)) {
            long j2 = sharedPreferences.getLong(STOPWATCH_BASE_TIME_PREF, 0L);
            long j3 = (jCurrentTimeMillis - j) + j2;
            sharedPreferences.edit().putLong(STOPWATCH_BASE_TIME_PREF, j3).commit();
            Log.i("Stopwatch Base adjusted, old base : " + j2 + " , new base : " + j3);
        }
        sharedPreferences.edit().putLong(CURRENT_TIME_MILLS_OFFSET, jCurrentTimeMillis).commit();
    }

    public static void setBaseTime(SharedPreferences.Editor editor, long j) {
        editor.putLong(STOPWATCH_BASE_TIME_PREF, j).apply();
    }

    public static long getBaseTime(SharedPreferences sharedPreferences) {
        return sharedPreferences.getLong(STOPWATCH_BASE_TIME_PREF, System.currentTimeMillis());
    }

    public static void setElapsedTime(SharedPreferences.Editor editor, long j) {
        editor.putLong(STOPWATCH_ELAPSED_TIME_PREF, j).apply();
    }

    public static long getElapsedTime(SharedPreferences sharedPreferences) {
        return sharedPreferences.getLong(STOPWATCH_ELAPSED_TIME_PREF, 0L);
    }

    public static void setRunningState(SharedPreferences.Editor editor, boolean z) {
        editor.putBoolean(STOPWATCH_STATE_RUNNING_PREF, z).apply();
    }

    public static boolean getRunningState(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(STOPWATCH_STATE_RUNNING_PREF, false);
    }

    public static String getSkipDateString(Context context, long j) {
        String string;
        Calendar calendar = Calendar.getInstance();
        int i = calendar.get(6);
        calendar.setTimeInMillis(j);
        int i2 = calendar.get(6) - i;
        if (i2 == 0) {
            string = context.getString(R.string.today);
        } else if (i2 == 1) {
            string = context.getString(R.string.tomorrow);
        } else {
            string = calendar.getDisplayName(7, 1, Locale.getDefault());
        }
        return context.getString(R.string.skip_alarm_once_hint, context.getString(R.string.skip_alarm_date_format, DateUtils.formatDateTime(context, j, 4480), string));
    }

    public static boolean supportShutdownAlarm() {
        return (IS_NEXUS_5 || isInSecureSpace()) ? false : true;
    }

    public static void hideSoftInput(Context context, View view) {
        if (context == null) {
            return;
        }
        ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showSoftInput(View view) {
        view.requestFocus();
        ((InputMethodManager) view.getContext().getSystemService("input_method")).showSoftInput(view, 0);
    }

    public static boolean isShowKeyboard(Context context, View view) {
        return view != null && ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isInternational() {
        return Build.IS_INTERNATIONAL_BUILD;
    }

    public static boolean isKddiCustomized() {
        return IS_KDDI_CUSTOMIZED;
    }

    public static String formatToast(Context context, long j, int i) {
        Object quantityString;
        String quantityString2;
        String quantityString3;
        long jCurrentTimeMillis = j - System.currentTimeMillis();
        long j2 = jCurrentTimeMillis / AlarmHelper.ARRIVING_ALARM_DURATION;
        long j3 = (jCurrentTimeMillis / 60000) % 60;
        long j4 = j2 / 24;
        long j5 = j2 % 24;
        if (isKddiCustomized() && isInternational()) {
            if (j3 == 59) {
                j5++;
                j3 = 0;
            } else {
                j3++;
            }
        }
        if (j4 == 0) {
            quantityString = "";
        } else {
            int i2 = (int) j4;
            quantityString = context.getResources().getQuantityString(R.plurals.day, i2, Integer.valueOf(i2));
        }
        if (j3 == 0) {
            quantityString2 = "";
        } else {
            int i3 = (int) j3;
            quantityString2 = context.getResources().getQuantityString(R.plurals.minute, i3, Integer.valueOf(i3));
        }
        if (j5 == 0) {
            quantityString3 = "";
        } else {
            int i4 = (int) j5;
            quantityString3 = context.getResources().getQuantityString(R.plurals.hour, i4, Integer.valueOf(i4));
        }
        int i5 = ((j5 > 0L ? 1 : (j5 == 0L ? 0 : -1)) > 0 ? 2 : 0) | (j4 > 0 ? 1 : 0) | ((j3 > 0L ? 1 : (j3 == 0L ? 0 : -1)) > 0 ? 4 : 0);
        String[] stringArray = context.getResources().getStringArray(i);
        if (i5 >= stringArray.length) {
            return "";
        }
        String str = stringArray[i5];
        if (i5 == 0) {
            quantityString = 1;
        }
        return String.format(str, quantityString, quantityString3, quantityString2);
    }

    public static boolean isBuildVersionAboveOrEqualP() {
        return android.os.Build.VERSION.SDK_INT >= 28;
    }

    public static String formatToastNew(Context context) {
        return String.format(context.getResources().getString(R.string.alarm_set_new), 1);
    }

    public static String formatToastInFutureNew(Context context) {
        return String.format(context.getResources().getString(R.string.alarm_in_furture_new), 1);
    }

    public static int getSnoozeMinutes(Context context) {
        return Integer.parseInt(FBEUtil.getDefaultSharedPreferences(context).getString("snooze_duration", "10"));
    }

    public static int getAlarmSpaceSnoozeMinutes(Context context) {
        return FBEUtil.getDefaultSharedPreferences(context).getInt(AlarmSettingsActivity.KEY_ALARM_APACE_SNOOZE, Integer.MIN_VALUE);
    }

    public static String formatElapsedTime(long j) {
        return formatElapsedTime(j, false).toString();
    }

    public static CharSequence formatElapsedTime(long j, boolean z) {
        long j2 = j / AlarmHelper.ARRIVING_ALARM_DURATION;
        long j3 = (j % AlarmHelper.ARRIVING_ALARM_DURATION) / 60000;
        long j4 = (j % 60000) / 1000;
        long j5 = (j % 1000) / 10;
        if (j % 10 >= 5) {
            j5++;
            if (j5 >= 100) {
                j4++;
                if (j4 >= 60) {
                    j3++;
                    j4 = 0;
                }
                if (j3 >= 60) {
                    j2++;
                    j3 = 0;
                    j5 = 0;
                } else {
                    j5 = 0;
                }
            }
        }
        if (j2 > 0) {
            return formatTime(String.format(DeskClockApp.getAppDEContext().getResources().getString(z ? R.string.time_view_with_hour_span : R.string.time_view_with_hour), Long.valueOf(j2), Long.valueOf(j3), Long.valueOf(j4), Long.valueOf(j5)), new Object[0]);
        }
        return formatTime(String.format(DeskClockApp.getAppDEContext().getResources().getString(z ? R.string.time_view_with_span : R.string.time_view), Long.valueOf(j3), Long.valueOf(j4), Long.valueOf(j5)), new Object[0]);
    }

    public static CharSequence formatTimeWithSPAN(long j) {
        long j2 = j / AlarmHelper.ARRIVING_ALARM_DURATION;
        long j3 = (j % AlarmHelper.ARRIVING_ALARM_DURATION) / 60000;
        long j4 = (j % 60000) / 1000;
        long j5 = (j % 1000) / 10;
        if (j % 10 >= 5 && j5 + 1 >= 100) {
            j4++;
            if (j4 >= 60) {
                j3++;
                j4 = 0;
            }
            if (j3 >= 60) {
                j2++;
                j3 = 0;
            }
        }
        return formatTime(TIME_FORMAT_WITH_SPAN, Long.valueOf(j2), Long.valueOf(j3), Long.valueOf(j4));
    }

    public static int getWeekDay(Calendar calendar) {
        return calendar.get(7);
    }

    /* JADX WARN: Code duplicated, block: B:32:0x0055 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0 */
    /* JADX WARN: Type inference failed for: r2v1, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r2v2 */
    /* JADX WARN: Type inference failed for: r5v0, types: [int] */
    public static Bitmap decodeBitmap(int i, int i2, int i3) throws Throwable {
        InputStream inputStreamOpenRawResource;
        ?? r2 = 0;
        try {
            try {
                inputStreamOpenRawResource = DeskClockApp.getAppDEContext().getResources().openRawResource(i);
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(inputStreamOpenRawResource, null, options);
                    inputStreamOpenRawResource.reset();
                    options.inSampleSize = calculateInSampleSize(options, i2, i3);
                    options.inJustDecodeBounds = false;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(inputStreamOpenRawResource, null, options);
                    if (inputStreamOpenRawResource != null) {
                        try {
                            inputStreamOpenRawResource.close();
                        } catch (IOException e) {
                            Log.e(TAG, "decodeBitmap is close error", e);
                        }
                    }
                    return bitmapDecodeStream;
                } catch (Exception e2) {
                    e = e2;
                    Log.e("decodeBitmap error", e);
                    if (inputStreamOpenRawResource != null) {
                        try {
                            inputStreamOpenRawResource.close();
                        } catch (IOException e3) {
                            Log.e(TAG, "decodeBitmap is close error", e3);
                        }
                    }
                    return null;
                }
            } catch (Exception e4) {
                e = e4;
                inputStreamOpenRawResource = null;
            } catch (Throwable th) {
                th = th;
                if (r2 != 0) {
                    try {
                        r2.close();
                    } catch (IOException e5) {
                        Log.e(TAG, "decodeBitmap is close error", e5);
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            r2 = i;
            if (r2 != 0) {
                r2.close();
            }
            throw th;
        }
    }

    public static Bitmap decodeBitmap(String str, int i, int i2) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(str, options);
            options.inSampleSize = calculateInSampleSize(options, i, i2);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(str, options);
        } catch (Exception e) {
            Log.e(TAG, "decodeBitmap error", e);
            return null;
        }
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        bitmap.recycle();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int i, int i2) {
        int iRound;
        int i3 = options.outHeight;
        int i4 = options.outWidth;
        if (i3 > i2 || i4 > i) {
            if (i4 > i3) {
                iRound = Math.round(i3 / i2);
            } else {
                iRound = Math.round(i4 / i);
            }
            while ((i4 * i3) / (iRound * iRound) > i * i2 * 2) {
                iRound++;
            }
        } else {
            iRound = 1;
        }
        Log.i("calculateInSampleSize reqWidth=" + i + ", reqHeight=" + i2 + ", options.outHeight=" + i3 + ", options.outWidth=" + i4 + ", inSampleSize=" + iRound);
        return iRound;
    }

    public static boolean checkApkExist(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            DeskClockApp.getAppContext().getPackageManager().getApplicationInfo(str, 8192);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static String formatTimerDuration(Context context, long j, int i) {
        String quantityString;
        String quantityString2;
        String quantityString3;
        long j2 = j / AlarmHelper.ARRIVING_ALARM_DURATION;
        long j3 = (j / 60000) % 60;
        long j4 = (j / 1000) % 60;
        long j5 = j2 % 24;
        if (j4 == 0) {
            quantityString = "";
        } else {
            int i2 = (int) j4;
            quantityString = context.getResources().getQuantityString(R.plurals.second, i2, Integer.valueOf(i2));
        }
        if (j3 == 0) {
            quantityString2 = "";
        } else {
            int i3 = (int) j3;
            quantityString2 = context.getResources().getQuantityString(R.plurals.minute, i3, Integer.valueOf(i3));
        }
        if (j5 == 0) {
            quantityString3 = "";
        } else {
            int i4 = (int) j5;
            quantityString3 = context.getResources().getQuantityString(R.plurals.hour, i4, Integer.valueOf(i4));
        }
        boolean z = j4 > 0;
        int i5 = ((j3 > 0L ? 1 : (j3 == 0L ? 0 : -1)) > 0 ? 2 : 0) | (j5 > 0 ? 1 : 0) | (z ? 4 : 0);
        String[] stringArray = context.getResources().getStringArray(i);
        if (i5 == 0) {
            return "";
        }
        return String.format(stringArray[i5 - 1], quantityString3, quantityString2, quantityString);
    }

    public static String formatLapItem(Context context, long j, int i) {
        String quantityString;
        String quantityString2;
        long j2 = (j / 60000) % 60;
        long j3 = (j / AlarmHelper.ARRIVING_ALARM_DURATION) % 24;
        double d = ((j / 1000) % 60) + 0.001d;
        int i2 = (int) d;
        String quantityString3 = context.getResources().getQuantityString(R.plurals.second, i2, Integer.valueOf(i2));
        if (j2 == 0) {
            quantityString = "";
        } else {
            int i3 = (int) j2;
            quantityString = context.getResources().getQuantityString(R.plurals.minute, i3, Integer.valueOf(i3));
        }
        if (j3 == 0) {
            quantityString2 = "";
        } else {
            int i4 = (int) j3;
            quantityString2 = context.getResources().getQuantityString(R.plurals.hour, i4, Integer.valueOf(i4));
        }
        int i5 = (j3 > 0 ? 1 : 0) | ((j2 > 0L ? 1 : (j2 == 0L ? 0 : -1)) > 0 ? 2 : 0) | ((d > 0.0d ? 1 : (d == 0.0d ? 0 : -1)) > 0 ? 4 : 0);
        String[] stringArray = context.getResources().getStringArray(i);
        if (i5 == 0) {
            return "";
        }
        return String.format(stringArray[i5 - 1], quantityString2, quantityString, quantityString3);
    }

    public static String formatTimer(Context context, long j, long j2, long j3, int i) {
        String quantityString;
        String quantityString2;
        String quantityString3;
        long j4 = j % 24;
        if (j3 == 0) {
            quantityString = "";
        } else {
            int i2 = (int) j3;
            quantityString = context.getResources().getQuantityString(R.plurals.second, i2, Integer.valueOf(i2));
        }
        if (j2 == 0) {
            quantityString2 = "";
        } else {
            int i3 = (int) j2;
            quantityString2 = context.getResources().getQuantityString(R.plurals.minute, i3, Integer.valueOf(i3));
        }
        if (j4 == 0) {
            quantityString3 = "";
        } else {
            int i4 = (int) j4;
            quantityString3 = context.getResources().getQuantityString(R.plurals.hour, i4, Integer.valueOf(i4));
        }
        int i5 = (j4 > 0 ? 1 : 0) | ((j2 > 0L ? 1 : (j2 == 0L ? 0 : -1)) > 0 ? 2 : 0) | ((j3 > 0L ? 1 : (j3 == 0L ? 0 : -1)) > 0 ? 4 : 0);
        String[] stringArray = context.getResources().getStringArray(i);
        if (i5 == 0) {
            return "";
        }
        return String.format(stringArray[i5 - 1], quantityString3, quantityString2, quantityString);
    }

    public static void playDeleteRingtone() {
        Context appDEContext = DeskClockApp.getAppDEContext();
        if (shouldPlaySoundForOperation(appDEContext)) {
            playRingtoneAsync(appDEContext, PATH_DELETE_OGG);
        }
    }

    private static boolean shouldPlaySoundForOperation(Context context) {
        try {
            int i = Settings.System.getInt(context.getContentResolver(), DELETE_SOUND_SETTING, SoundDefaultValueUtil.getDeleteSoundEffectDefaultValue() ? 1 : 0);
            Log.d("Got sound setting value %d" + i);
            return i > 0;
        } catch (Exception e) {
            Log.e("Error to get sound setting value", e);
            return true;
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [com.android.deskclock.util.Util$1] */
    private static void playRingtoneAsync(final Context context, final String str) {
        if (TextUtils.isEmpty(str)) {
            Log.d("playRingtoneAsync file path is null");
        } else {
            new AsyncTask<Void, Void, Void>() { // from class: com.android.deskclock.util.Util.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Void doInBackground(Void... voidArr) {
                    File file = new File(str);
                    if (file.exists()) {
                        Ringtone ringtone = RingtoneManager.getRingtone(context, Uri.fromFile(file));
                        ringtone.setAudioAttributes(new AudioAttributes.Builder().setLegacyStreamType(1).build());
                        if (ringtone == null) {
                            return null;
                        }
                        ringtone.play();
                        return null;
                    }
                    Log.d("ringtone file delete.ogg is not exist");
                    return null;
                }
            }.execute(new Void[0]);
        }
    }

    public static int getPackageCode(String str) {
        try {
            return DeskClockApp.getAppDEContext().getPackageManager().getPackageInfo(str, 0).versionCode;
        } catch (Throwable th) {
            th.printStackTrace();
            return 0;
        }
    }

    public static CharSequence formatTimerDuration(Context context) {
        long j = FBEUtil.getDefaultSharedPreferences(context).getLong("duration", 0L);
        return AlarmHelper.formatTimerDuration(context, (int) (j / AlarmHelper.ARRIVING_ALARM_DURATION), (int) ((j % AlarmHelper.ARRIVING_ALARM_DURATION) / 60000), (int) ((j % 60000) / 1000));
    }

    public static int getTextViewWidth(Context context, CharSequence charSequence, int i) {
        TextView textView = new TextView(context);
        textView.setText(charSequence, TextView.BufferType.SPANNABLE);
        textView.setTextSize(0, i);
        textView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        return textView.getMeasuredWidth();
    }

    public static int getTypefaceTextViewWidth(Context context, CharSequence charSequence, int i, Typeface typeface) {
        TextView textView = new TextView(context);
        textView.setText(charSequence, TextView.BufferType.SPANNABLE);
        textView.setTextSize(0, i);
        MiuiFont.setFont(textView, typeface);
        textView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        return textView.getMeasuredWidth();
    }

    public static int getTextViewHeight(Context context, CharSequence charSequence, int i, int i2) {
        TextView textView = new TextView(context);
        textView.setText(charSequence, TextView.BufferType.SPANNABLE);
        textView.setTextSize(0, i);
        textView.measure(View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
        return textView.getMeasuredHeight();
    }

    public static int getTypefaceTextViewHeight(Context context, CharSequence charSequence, int i, int i2, Typeface typeface) {
        TypefaceTextView typefaceTextView = new TypefaceTextView(context);
        typefaceTextView.setText(charSequence, TextView.BufferType.SPANNABLE);
        typefaceTextView.setTextSize(0, i);
        typefaceTextView.setTypeface(typeface);
        typefaceTextView.measure(View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
        return typefaceTextView.getMeasuredHeight();
    }

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        if (windowManager == null) {
            return 0;
        }
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        return point.x;
    }

    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        if (windowManager == null) {
            return 0;
        }
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        return point.y;
    }

    public static int getStatusBarHeight(Context context) {
        return getDimensionPixelOffset(context.getResources(), "status_bar_height", "dimen", "android");
    }

    public static int getStatusBarHeights(Context context) {
        int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public static int getNavigationBarHeight(Context context) {
        return getDimensionPixelOffset(context.getResources(), "navigation_bar_height", "dimen", "android");
    }

    public static int getVirtualNavigationBarHeight(Context context) {
        int identifier = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public static int getWindowHeightByWm() {
        WindowManager windowManager = (WindowManager) DeskClockApp.getAppContext().getSystemService("window");
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point.y;
    }

    public static int getWindowWidthByWM() {
        WindowManager windowManager = (WindowManager) DeskClockApp.getAppContext().getSystemService("window");
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            return windowManager.getCurrentWindowMetrics().getBounds().width();
        }
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point.x;
    }

    public static int getActionBarHeight(Context context) {
        float dimension;
        if (!isInternational()) {
            boolean zIsInMultiWindowMode = context instanceof Activity ? ((Activity) context).isInMultiWindowMode() : false;
            if (!isWideMode(context) || zIsInMultiWindowMode) {
                dimension = context.getResources().getDimension(R.dimen.action_bar_height_miui12);
            } else {
                dimension = context.getResources().getDimension(R.dimen.action_bar_height_min);
            }
        } else {
            dimension = context.getResources().getDimension(R.dimen.action_bar_height);
        }
        return (int) dimension;
    }

    private static int getDimensionPixelOffset(Resources resources, String str, String str2, String str3) {
        int identifier = resources.getIdentifier(str, str2, str3);
        if (identifier > 0) {
            return resources.getDimensionPixelSize(identifier);
        }
        Log.i("getDimensionPixelOffset(), resourceId is invalid.");
        return 0;
    }

    public static boolean isToday(long j) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(new Date(j));
        return calendar2.get(1) == calendar.get(1) && calendar2.get(6) == calendar.get(6);
    }

    public static String getRegion() {
        String str = SystemProperties.get("ro.miui.region");
        if (!TextUtils.isEmpty(str)) {
            return str;
        }
        String str2 = SystemProperties.get("ro.product.locale.region");
        if (!TextUtils.isEmpty(str2)) {
            return str2;
        }
        String str3 = SystemProperties.get("persist.sys.country");
        return TextUtils.isEmpty(str3) ? Locale.getDefault().getCountry() : str3;
    }

    public static String formatTimeForLog(long j) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(j));
        } catch (Exception unused) {
            return String.valueOf(j);
        }
    }

    public static String getRingtoneTitle(Context context, Uri uri) {
        String alarmRingtoneTitle;
        try {
            if (uri == null) {
                alarmRingtoneTitle = context.getResources().getString(R.string.silent_alarm_summary);
            } else {
                alarmRingtoneTitle = AlarmRingtoneUtil.getAlarmRingtoneTitle(context, uri);
            }
            return alarmRingtoneTitle;
        } catch (Exception unused) {
            return "";
        }
    }

    public static long startTimeToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    public static long startTimeTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, 1);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    public static String getFilesDir() {
        return getFilesDir(DeskClockApp.getAppDEContext());
    }

    public static String getFilesDir(Context context) {
        File filesDir = context.getFilesDir();
        try {
            if (filesDir != null) {
                if (!filesDir.exists()) {
                    filesDir.mkdirs();
                }
                return filesDir.getAbsolutePath();
            }
            Log.e("getFilesDir error, null file");
            return null;
        } catch (Exception e) {
            Log.e("getFilesDir error: " + e.getMessage());
            return null;
        }
    }

    public static UserHandle getUserHandle() {
        if (android.os.Build.VERSION.SDK_INT > 28) {
            return ClockCompat.UserHandle_CURRENT;
        }
        return ClockCompat.UserHandle_ALL;
    }

    public static void resetRtl() {
        mIsRtl = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
    }

    public static boolean isRtl() {
        return mIsRtl;
    }

    public static boolean isSupportLinearMotorVibrate() {
        return FolmeEase.LINEAR.equals(SystemProperties.get("sys.haptic.motor"));
    }

    public static int getNotchHeight(Context context, int i) {
        int identifier = context.getResources().getIdentifier("edge_suppression_size", "dimen", "android");
        return (identifier <= 0 || context.getResources().getDimensionPixelSize(identifier) <= 0) ? i : context.getResources().getDimensionPixelSize(identifier);
    }

    public static int getCurrentUser() {
        try {
            Method method = ActivityManager.class.getMethod("getCurrentUser", new Class[0]);
            method.setAccessible(true);
            return ((Integer) method.invoke(ActivityManager.class, new Object[0])).intValue();
        } catch (Exception unused) {
            return Integer.MIN_VALUE;
        }
    }

    public static UserHandle getCurrentUserHandle(int i) {
        UserHandle userHandle = ClockCompat.UserHandle_CURRENT;
        try {
            Method method = UserHandle.class.getMethod("of", Integer.TYPE);
            method.setAccessible(true);
            return (UserHandle) method.invoke(UserHandle.class, Integer.valueOf(i));
        } catch (Exception e) {
            Log.d("getCurrentUserHandle Exception:" + e);
            return userHandle;
        }
    }

    public static int getUserId(Context context) {
        try {
            Method method = Context.class.getMethod("getUserId", new Class[0]);
            method.setAccessible(true);
            return ((Integer) method.invoke(context, new Object[0])).intValue();
        } catch (Exception unused) {
            return 0;
        }
    }

    public static UserHandle getUser(Context context) {
        try {
            Method method = Context.class.getMethod("getUser", new Class[0]);
            method.setAccessible(true);
            return (UserHandle) method.invoke(context, new Object[0]);
        } catch (Exception e) {
            Log.d("getUser Exception :" + e);
            return ClockCompat.UserHandle_CURRENT;
        }
    }

    public static int getUserIdFromUri(Uri uri) {
        try {
            Method method = ContentProvider.class.getMethod("getUserIdFromUri", Uri.class);
            method.setAccessible(true);
            return ((Integer) method.invoke(ContentProvider.class, uri)).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean uriHasUserId(Uri uri) {
        try {
            Method method = ContentProvider.class.getMethod("uriHasUserId", Uri.class);
            method.setAccessible(true);
            return ((Boolean) method.invoke(ContentProvider.class, uri)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isUserInMainSpace() {
        return getCurrentUser() == 0;
    }

    public static boolean isApplicationInMainSpace() {
        return getUserId(DeskClockApp.getAppContext()) == 0;
    }

    public static boolean isChinese(Context context) {
        return context.getResources().getConfiguration().locale.equals(Locale.SIMPLIFIED_CHINESE);
    }

    public static boolean isEnglish(Context context) {
        return ENGLISH_LANGUAGE.equals(context.getResources().getConfiguration().locale.getLanguage());
    }

    public static MiuiWallpaperManager getMiuiWallpaperManager() {
        MiuiWallpaperManager.init(DeskClockApp.getAppContext(), new MiuiWallpaperManager.MiuiWallpaperManagerCallback() { // from class: com.android.deskclock.util.Util.2
            @Override // com.miui.miwallpaper.MiuiWallpaperManager.MiuiWallpaperManagerCallback
            public void onMiuiWallpaperInitialized(MiuiWallpaperManager miuiWallpaperManager) {
                MiuiWallpaperManager unused = Util.mMiuiWallpaperManager = miuiWallpaperManager;
            }
        });
        return mMiuiWallpaperManager;
    }

    public static Bitmap getLockWallpaper(Context context) {
        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) ThemeResources.getLockWallpaperCache(context);
            if (bitmapDrawable == null) {
                return null;
            }
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ThemeResources.clearLockWallpaperCache();
            return bitmap;
        } catch (Exception unused) {
            return null;
        }
    }

    public static Bitmap setGaussianBitmap(Bitmap bitmap, Context context) {
        if (bitmap == null) {
            return null;
        }
        try {
            Bitmap bitmapCreateScaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 20, bitmap.getHeight() / 20, false);
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmapCreateScaledBitmap);
            RenderScript renderScriptCreate = RenderScript.create(context);
            ScriptIntrinsicBlur scriptIntrinsicBlurCreate = ScriptIntrinsicBlur.create(renderScriptCreate, Element.U8_4(renderScriptCreate));
            Allocation allocationCreateFromBitmap = Allocation.createFromBitmap(renderScriptCreate, bitmapCreateScaledBitmap);
            Allocation allocationCreateFromBitmap2 = Allocation.createFromBitmap(renderScriptCreate, bitmapCreateBitmap);
            scriptIntrinsicBlurCreate.setRadius(15.0f);
            scriptIntrinsicBlurCreate.setInput(allocationCreateFromBitmap);
            scriptIntrinsicBlurCreate.forEach(allocationCreateFromBitmap2);
            allocationCreateFromBitmap2.copyTo(bitmapCreateBitmap);
            bitmap.recycle();
            bitmapCreateScaledBitmap.recycle();
            renderScriptCreate.destroy();
            return bitmapCreateBitmap;
        } catch (Exception unused) {
            return null;
        }
    }

    public static boolean isSpecialDevice() {
        return Build.DEVICE.equals("zeus");
    }

    public static void notifyPowerState(Context context, boolean z) {
        Log.f(TAG, "notifyPowerState, isEnter: " + z);
        Intent intent = new Intent();
        intent.setPackage("com.miui.powerkeeper");
        intent.setAction(ACTION_SET_FPS);
        intent.putExtra("package_name", BuildConfig.APPLICATION_ID);
        intent.putExtra("isEnter", z);
        context.sendBroadcast(intent);
    }

    public static boolean isInFullWindowGestureMode(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0) != 0;
    }

    public static int getPackageCode() {
        try {
            return DeskClockApp.getAppDEContext().getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean isVersionS() {
        return android.os.Build.VERSION.SDK_INT >= 31;
    }

    public static boolean isVersionBelowS() {
        return android.os.Build.VERSION.SDK_INT <= 30;
    }

    public static boolean isRingtoneInternal(Uri uri) {
        if (uri == null) {
            return true;
        }
        return isRingtoneInternal(uri.toString());
    }

    public static boolean isRingtoneInternal(String str) {
        return str == null || str.startsWith("android.resource:") || str.startsWith("file:///system/media/audio");
    }

    public static boolean isMiUi11() {
        return Integer.valueOf(SystemProperties.get("ro.miui.ui.version.code")).intValue() >= 9;
    }

    public static void hasRestored(Context context) {
        FBEUtil.getDefaultSharedPreferences(context).edit().putBoolean(HAS_RESTORED, true).apply();
    }

    public static boolean isRestored(Context context) {
        return FBEUtil.getDefaultSharedPreferences(context).getBoolean(HAS_RESTORED, false);
    }

    public static boolean isDataRestored(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), DATA_KEY);
        Log.d(TAG, " isDataRestored data:" + string);
        if (string != null) {
            return !string.isEmpty();
        }
        return false;
    }

    public static boolean isInInternalScreen(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        return point.x > 1500 && point.y > 1500;
    }

    public static boolean isWideMode(Context context) {
        return context.getResources().getConfiguration().screenWidthDp >= 650;
    }

    public static boolean isGridSplitScreen(Activity activity) {
        return isInInternalScreen(activity) && activity.isInMultiWindowMode() && !isFreeFormScreen(activity.getResources().getConfiguration());
    }

    public static boolean inExternalSplitScreen(Activity activity) {
        return (isInInternalScreen(activity) || !activity.isInMultiWindowMode() || isFreeFormScreen(activity.getResources().getConfiguration())) ? false : true;
    }

    public static boolean inExternalSplitScreen(DeskClockTabActivity deskClockTabActivity) {
        return (isInInternalScreen(deskClockTabActivity) || !deskClockTabActivity.isInMultiWindowMode() || isFreeFormScreen(deskClockTabActivity.getResources().getConfiguration())) ? false : true;
    }

    public static boolean isTopBottomSplitScreen(DeskClockTabActivity deskClockTabActivity) {
        Rect bounds = android.os.Build.VERSION.SDK_INT >= 30 ? WindowUtils.getWindowManager(deskClockTabActivity).getCurrentWindowMetrics().getBounds() : null;
        if (bounds == null) {
            return false;
        }
        int iHeight = bounds.height();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowUtils.getWindowManager(deskClockTabActivity).getDefaultDisplay().getRealMetrics(displayMetrics);
        return iHeight < displayMetrics.heightPixels;
    }

    public static boolean isPcMode(Context context) {
        return context != null && (context.getResources().getConfiguration().uiMode & 12288) == 8192;
    }

    public static boolean isFreeFormScreen(Configuration configuration) {
        return configuration != null && configuration.toString().contains("mWindowingMode=freeform");
    }

    public static boolean isInMultiWindowMode(AppCompatActivity appCompatActivity) {
        return appCompatActivity.isInMultiWindowMode();
    }

    public static boolean isDeviceCetus() {
        return Build.DEVICE.equals("cetus");
    }

    public static boolean isDeviceZizhan() {
        return Build.DEVICE.equals("zizhan");
    }

    public static boolean isDeviceYunluo() {
        return Build.DEVICE.equals("yunluo");
    }

    public static boolean isDeviceEarth() {
        return Build.DEVICE.equals("earth");
    }

    public static boolean isDeviceMerlin() {
        return Build.DEVICE.equals("merlin");
    }

    public static boolean isDeviceDandelion() {
        return Build.DEVICE.equals("dandelion");
    }

    public static boolean isDeviceMondrian() {
        return Build.DEVICE.equals("mondrian");
    }

    public static boolean isDeviceRuyiOrBixi() {
        return Build.DEVICE.equals("ruyi") || Build.DEVICE.equals("bixi");
    }

    public static boolean isDeviceN85OrN85X() {
        return Build.DEVICE.equals("flare") || Build.DEVICE.equals("spark");
    }

    public static boolean isSupportColorfulLight() {
        return Build.DEVICE.equals("onyx") || Build.DEVICE.equals("rodin") || Build.DEVICE.equals("klee") || Build.DEVICE.equals("dash");
    }

    public static boolean isPadOrientationLand(Context context) {
        if (context == null) {
            return false;
        }
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        return point.x > point.y && PadAdapterUtil.IS_PAD;
    }

    public static boolean isOrientationPortrait(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        return point.x < point.y && PadAdapterUtil.IS_PAD;
    }

    public static boolean isFoldOrientationLand(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        return point.x > point.y && isFoldDevice(context);
    }

    public static boolean isFoldOrientationPortrait(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        return point.x < point.y && isFoldDevice(context);
    }

    public static boolean isPhoneDevice(Context context) {
        return DeviceHelper.detectType(context) == 1;
    }

    public static boolean isFoldDevice(Context context) {
        return DeviceHelper.detectType(context) == 3;
    }

    public static boolean isInTinyScreen(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        return point.y < 2000;
    }

    public static boolean isFoldDeviceByType(Context context) {
        return DeviceHelper.isFoldable();
    }

    public static boolean isFlipType(Context context) {
        return DeviceHelper.detectType(context) == 4;
    }

    public static boolean isTinyScreen(Context context) {
        return DeviceHelper.isFoldable() && DeviceHelper.isTinyScreen(context);
    }

    public static boolean isInnerScreen(Context context) {
        return isDeviceRuyiOrBixi() && !isTinyScreen(context);
    }

    public static boolean isLargeScreenPad() {
        String str = Build.DEVICE;
        return str.equals("yudi") || str.equals("sheng");
    }

    public static boolean isNightMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & 48) == 32;
    }

    public static boolean getBooleanMetaData(String str, String str2, String str3) {
        try {
            boolean z = DeskClockApp.getAppContext().getPackageManager().getActivityInfo(new ComponentName(str, str2), 128).metaData.getBoolean(str3);
            Log.d(TAG, "getBooleanMetaData, " + str3 + ": " + z);
            return z;
        } catch (Exception e) {
            Log.e(TAG, "getBooleanMetaData: " + str3 + ", error: " + e);
            return false;
        }
    }

    public static boolean isSupportVibrator(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
        Log.d(TAG, "isSupportVibrator: " + vibrator.hasVibrator());
        return vibrator.hasVibrator();
    }

    public static boolean isPadOrFoldDeviceFullScreen(BaseActivity baseActivity) {
        return (PadAdapterUtil.IS_PAD || (isFoldDevice(baseActivity) && isInInternalScreen(baseActivity))) && !baseActivity.isInMultiWindowMode();
    }

    public static boolean atLeastU() {
        return android.os.Build.VERSION.SDK_INT > 33 || "UpsideDownCake".equals(android.os.Build.VERSION.CODENAME);
    }

    public static int getRotationMode(Context context) {
        int rotation = ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRotation();
        Log.d(TAG, "getRotationMode : " + rotation);
        return rotation;
    }

    public static int getCurrentFoldState() {
        int iIntValue = -1;
        try {
            Class<?> cls = Class.forName("android.hardware.devicestate.DeviceStateManager");
            Object objNewInstance = cls.getConstructor(new Class[0]).newInstance(new Object[0]);
            Method method = cls.getMethod("getCurrentState", new Class[0]);
            method.setAccessible(true);
            iIntValue = ((Integer) method.invoke(objNewInstance, new Object[0])).intValue();
            Log.d(TAG, "getCurrentFoldState state : " + iIntValue);
            return iIntValue;
        } catch (Exception e) {
            Log.d(TAG, "getCurrentFoldState Exception:" + e);
            return iIntValue;
        }
    }

    public static int getWindowWidthByWm() {
        WindowManager windowManager = (WindowManager) DeskClockApp.getAppContext().getSystemService("window");
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point.y;
    }

    public static boolean isSmallPad() {
        return PadAdapterUtil.IS_PAD && (Build.DEVICE.equals("flare") || Build.DEVICE.equals("spark") || Build.DEVICE.equals("turner") || Build.DEVICE.equals("yili"));
    }

    public static void cutOut(Activity activity) {
        WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            attributes.layoutInDisplayCutoutMode = 3;
            activity.getWindow().setAttributes(attributes);
        } else {
            Log.d(TAG, "current android version not support cutout display");
        }
    }

    public static boolean isScreenOn() {
        boolean zIsInteractive = ((PowerManager) DeskClockApp.getAppDEContext().getSystemService("power")).isInteractive();
        Log.d(TAG, "isScreen = " + zIsInteractive);
        return zIsInteractive;
    }

    public static boolean isIndependentRearDevice() {
        try {
            Class<?> cls = Class.forName("miui.util.MiuiMultiDisplayTypeInfo");
            Object objInvoke = cls.getMethod("isIndependentRearDevice", new Class[0]).invoke(cls, new Object[0]);
            if (objInvoke instanceof Boolean) {
                return ((Boolean) objInvoke).booleanValue();
            }
            return false;
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isSupportRearSmartAssistant() {
        try {
            Class<?> cls = Class.forName("miui.os.DeviceFeature");
            Object objInvoke = cls.getMethod("isSupportRearSmartAssistant", new Class[0]).invoke(cls, new Object[0]);
            if (objInvoke instanceof Boolean) {
                return ((Boolean) objInvoke).booleanValue();
            }
            return true;
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean isDemoRom(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.xiaomi.mihomemanager", 0);
            if (packageInfo != null) {
                return TextUtils.equals(packageInfo.sharedUserId, "android.uid.system");
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "isDemoRom Exception:" + e);
            return false;
        }
    }

    public static boolean stopAlertForBackScreenDemo(Context context) {
        try {
            if (!isDemoRom(context) || !isIndependentRearDevice() || !isSupportRearSmartAssistant()) {
                return false;
            }
            int i = Settings.Secure.getInt(context.getContentResolver(), MISHOW_RECOGNIZE_KEY);
            int i2 = Settings.Global.getInt(context.getContentResolver(), PREF_MISHOW_ON_FOREGROUND, 0);
            Log.d(TAG, "stopAlertForBackScreenDemo miShowRecognize = " + i + ", miShowOnForeground = " + i2);
            return i == 1 && i2 == 1;
        } catch (Exception e) {
            Log.e(TAG, "isDemoBackScreen Exception:" + e);
            return false;
        }
    }
}
