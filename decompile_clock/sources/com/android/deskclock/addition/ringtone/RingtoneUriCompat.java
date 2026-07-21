package com.android.deskclock.addition.ringtone;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.ringtone.digital.DigitalTimerRingtoneHelper;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.permission.PermissionUtil;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
public class RingtoneUriCompat {
    private static String DIGITAL_TIMER_PATH = "storage/emulated/0/Ringtones/timer_ring.ogg";
    private static String SPECIAL_DIGITAL_TIMER_PATH = "storage/emulated/0/Ringtones/special_timer_ring.ogg";
    private static final String TAG = "DC:RingtoneUriCompat";

    public static Uri convertUri(Context context, Uri uri) {
        if (uri != null && atLeastU() && "file".equals(uri.getScheme())) {
            String path = uri.getPath();
            String strSubstring = path.substring(path.lastIndexOf(47) + 1);
            Log.i(TAG, "displayName： " + strSubstring);
            if (path.startsWith("/system/media/audio") || path.startsWith("/product/media/audio")) {
                Log.i(TAG, "has permission. " + (context.checkSelfPermission("android.permission.READ_MEDIA_AUDIO") == 0));
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri2 = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
                try {
                    Cursor cursorQuery = contentResolver.query(uri2, new String[]{"_id"}, "_display_name=?", new String[]{strSubstring}, null);
                    try {
                        if (cursorQuery.moveToFirst()) {
                            long j = cursorQuery.getLong(0);
                            Uri uriCanonicalize = contentResolver.canonicalize(ContentUris.withAppendedId(uri2, j));
                            Log.i(TAG, "query found audio id " + j + " , uri=" + uriCanonicalize);
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                            return uriCanonicalize;
                        }
                        Log.w(TAG, "not found for " + path);
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                    } catch (Throwable th) {
                        if (cursorQuery != null) {
                            try {
                                cursorQuery.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "fail query media. " + uri, e);
                }
            }
        }
        return uri;
    }

    public static void updateConvertAllUri() {
        Context appDEContext = DeskClockApp.getAppDEContext();
        convertWeatherUri(appDEContext);
        convertWeekrUri(appDEContext);
        convertDewrUri(appDEContext);
        convertFirefliesUri(appDEContext);
        convertDaydreamUri(appDEContext);
    }

    public static Uri convertWeatherUri(Context context) {
        Uri uriConvertUri = convertUri(context, RingtoneConstants.RINGTONE_URI_WEATHER);
        RingtoneConstants.RINGTONE_URI_WEATHER_NEW = uriConvertUri;
        return uriConvertUri;
    }

    public static Uri convertWeekrUri(Context context) {
        Uri uriConvertUri = convertUri(context, RingtoneConstants.RINGTONE_URI_WEEK);
        RingtoneConstants.RINGTONE_URI_WEEK_NEW = uriConvertUri;
        return uriConvertUri;
    }

    public static Uri convertDewrUri(Context context) {
        Uri uriConvertUri = convertUri(context, RingtoneConstants.RINGTONE_URI_DEW);
        RingtoneConstants.RINGTONE_URI_DEW_NEW = uriConvertUri;
        return uriConvertUri;
    }

    public static Uri convertFirefliesUri(Context context) {
        Uri uriConvertUri = convertUri(context, RingtoneConstants.RINGTONE_URI_FIREFLY);
        RingtoneConstants.RINGTONE_URI_FIREFLY_NEW = uriConvertUri;
        return uriConvertUri;
    }

    public static Uri convertDaydreamUri(Context context) {
        Uri uriConvertUri = convertUri(context, RingtoneConstants.RINGTONE_URI_DREAM);
        RingtoneConstants.RINGTONE_URI_DREAM_NEW = uriConvertUri;
        return uriConvertUri;
    }

    /* JADX WARN: Code duplicated, block: B:60:0x01a5  */
    /* JADX WARN: Code duplicated, block: B:61:0x01a8  */
    /* JADX WARN: Code duplicated, block: B:63:0x01b2  */
    /* JADX WARN: Code duplicated, block: B:67:0x01cf A[Catch: Exception -> 0x01db, LOOP:0: B:65:0x01c8->B:67:0x01cf, LOOP_END, TryCatch #0 {Exception -> 0x01db, blocks: (B:64:0x01b4, B:65:0x01c8, B:67:0x01cf, B:68:0x01d4), top: B:76:0x01b4 }] */
    /* JADX WARN: Code duplicated, block: B:85:0x01d4 A[EDGE_INSN: B:85:0x01d4->B:68:0x01d4 BREAK  A[LOOP:0: B:65:0x01c8->B:67:0x01cf], SYNTHETIC] */
    public static Uri saveMediaStore(final Context context, Uri uri) {
        String string;
        String str;
        int i;
        String str2;
        String str3;
        boolean z;
        Uri uriInsert;
        OutputStream outputStreamOpenOutputStream;
        InputStream inputStreamOpenRawResource;
        byte[] bArr;
        int i2;
        String string2 = null;
        if (!atLeastU()) {
            return null;
        }
        if (uri.equals(XiaoAiRingtoneHelper.getRingtoneUri())) {
            string2 = context.getString(R.string.xiaoai_ringtone_title);
            str = context.getString(R.string.xiaoai_ringtone_title) + ".mp3";
            string = context.getString(R.string.xiaoai_ringtone_title);
            i = R.raw.xiaoai_ringtone_preview;
        } else if (uri.equals(DigitalTimerRingtoneHelper.getRingtoneUri())) {
            string2 = context.getString(R.string.ringtone_digital_timer);
            if (Util.isSpecialDevice()) {
                str2 = "special_timer_ring.ogg";
                str3 = "special_timer_ring";
                i = R.raw.special_timer_ring;
            } else {
                str2 = "timer_ring.ogg";
                str3 = "timer_ring";
                i = R.raw.timer_ring;
            }
            String str4 = str3;
            str = str2;
            string = str4;
        } else {
            string = null;
            str = null;
            i = 0;
        }
        Log.d(TAG, "saveMediaStore start ringtoneString：" + string2 + " alert:" + uri);
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        if (context.checkSelfPermission("android.permission.READ_MEDIA_AUDIO") != 0) {
            new Handler().postDelayed(new Runnable() { // from class: com.android.deskclock.addition.ringtone.RingtoneUriCompat.1
                @Override // java.lang.Runnable
                public void run() {
                    Log.i(RingtoneUriCompat.TAG, "saveMediaStore not has permission");
                    PermissionUtil.shouldNotAskPermission((Activity) context);
                }
            }, 50L);
            return AlarmRingtoneUtil.getDefaultAlarmRingtone();
        }
        if (context.checkSelfPermission("android.permission.READ_MEDIA_AUDIO") == 0) {
            Log.i(TAG, "saveMediaStore has permission");
            try {
                z = true;
                try {
                    Cursor cursorQuery = contentResolver.query(uri2, new String[]{"_id", "_display_name"}, null, null, null);
                    try {
                        if (cursorQuery != null) {
                            while (cursorQuery.moveToNext()) {
                                try {
                                    if (cursorQuery.getString(1).contains(string)) {
                                        long j = cursorQuery.getLong(0);
                                        Uri uriCanonicalize = contentResolver.canonicalize(ContentUris.withAppendedId(uri2, j));
                                        Log.i(TAG, "saveMediaStore query found audio id " + j + " , uri=" + uriCanonicalize);
                                        if (uri.equals(XiaoAiRingtoneHelper.getRingtoneUri())) {
                                            XiaoAiRingtoneHelper.XIAO_AI_RINGTONE_URI_NEW = uriCanonicalize;
                                        } else if (uri.equals(DigitalTimerRingtoneHelper.getRingtoneUri())) {
                                            DigitalTimerRingtoneHelper.DIGITAL_TIMER_URI = uriCanonicalize;
                                        }
                                        AlarmRingtoneUtil.setDefaultAlarmRingtone(uriCanonicalize);
                                        if (cursorQuery != null) {
                                            cursorQuery.close();
                                        }
                                        return uriCanonicalize;
                                    }
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("title", string2);
                                    contentValues.put("is_alarm", Boolean.valueOf(z));
                                    contentValues.put("is_ringtone", Boolean.valueOf(z));
                                    contentValues.put("is_music", Boolean.valueOf(z));
                                    contentValues.put("_display_name", str);
                                    uriInsert = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
                                    if (uri.equals(XiaoAiRingtoneHelper.getRingtoneUri())) {
                                        XiaoAiRingtoneHelper.XIAO_AI_RINGTONE_URI_NEW = uriInsert;
                                    } else if (uri.equals(DigitalTimerRingtoneHelper.getRingtoneUri())) {
                                        DigitalTimerRingtoneHelper.DIGITAL_TIMER_URI = uriInsert;
                                    }
                                    outputStreamOpenOutputStream = context.getContentResolver().openOutputStream(uriInsert);
                                    inputStreamOpenRawResource = context.getResources().openRawResource(i);
                                    bArr = new byte[1024];
                                    while (true) {
                                        i2 = inputStreamOpenRawResource.read(bArr);
                                        if (i2 != -1) {
                                            break;
                                        }
                                        outputStreamOpenOutputStream.write(bArr, 0, i2);
                                    }
                                    outputStreamOpenOutputStream.close();
                                    inputStreamOpenRawResource.close();
                                    Log.d(TAG, "saveMediaStore new uri : " + uriInsert);
                                    AlarmRingtoneUtil.setDefaultAlarmRingtone(uriInsert);
                                    AlarmRingtoneUtil.getDefaultAlarmRingtone();
                                    return uriInsert;
                                } catch (Throwable th) {
                                    if (cursorQuery == null) {
                                        throw th;
                                    }
                                    try {
                                        cursorQuery.close();
                                        throw th;
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                        throw th;
                                    }
                                }
                                e = e;
                                Log.d(TAG, "Exception: " + e.getMessage());
                            }
                        }
                        outputStreamOpenOutputStream = context.getContentResolver().openOutputStream(uriInsert);
                        inputStreamOpenRawResource = context.getResources().openRawResource(i);
                        bArr = new byte[1024];
                        while (true) {
                            i2 = inputStreamOpenRawResource.read(bArr);
                            if (i2 != -1) {
                                break;
                                break;
                            }
                            outputStreamOpenOutputStream.write(bArr, 0, i2);
                        }
                        outputStreamOpenOutputStream.close();
                        inputStreamOpenRawResource.close();
                    } catch (Exception e) {
                        android.util.Log.d(TAG, "Exception: " + e.getMessage());
                    }
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                } catch (Exception e2) {
                    e = e2;
                }
            } catch (Exception e3) {
                e = e3;
                z = true;
            }
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("title", string2);
            contentValues2.put("is_alarm", Boolean.valueOf(z));
            contentValues2.put("is_ringtone", Boolean.valueOf(z));
            contentValues2.put("is_music", Boolean.valueOf(z));
            contentValues2.put("_display_name", str);
            uriInsert = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues2);
            if (uri.equals(XiaoAiRingtoneHelper.getRingtoneUri())) {
                XiaoAiRingtoneHelper.XIAO_AI_RINGTONE_URI_NEW = uriInsert;
            } else if (uri.equals(DigitalTimerRingtoneHelper.getRingtoneUri())) {
                DigitalTimerRingtoneHelper.DIGITAL_TIMER_URI = uriInsert;
            }
            Log.d(TAG, "saveMediaStore new uri : " + uriInsert);
            AlarmRingtoneUtil.setDefaultAlarmRingtone(uriInsert);
            AlarmRingtoneUtil.getDefaultAlarmRingtone();
            return uriInsert;
        }
        return AlarmRingtoneUtil.getDefaultAlarmRingtone();
    }

    public static boolean atLeastU() {
        return Build.VERSION.SDK_INT > 33 || "UpsideDownCake".equals(Build.VERSION.CODENAME);
    }
}
