package com.android.deskclock.addition.holiday;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import com.android.deskclock.alarm.lifepost.okhttp.Net;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/* JADX INFO: loaded from: classes.dex */
public final class HolidayUtil {
    static final String PREFERENCE_HOLIDAY_DATA = "pref_holiday_data";
    static final String PREFERENCE_HOLIDAY_FILE = "holiday";
    static final String PREFERENCE_HOLIDAY_REMOTE_VERSION = "pref_holiday_remote_version";

    public static void setRemoteVersion(Context context, int i) {
        getHolidaySharedPreference(context).edit().putInt(PREFERENCE_HOLIDAY_REMOTE_VERSION, i).apply();
    }

    public static int getRemoteVersion(Context context) {
        return getHolidaySharedPreference(context).getInt(PREFERENCE_HOLIDAY_REMOTE_VERSION, 0);
    }

    public static boolean updateHolidayData(Context context) throws Throwable {
        BufferedReader bufferedReader = null;
        try {
            try {
                try {
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(((HttpURLConnection) new URL(new String(Base64.decode(Net.Api.HOLIDAY_ENCODE_ARRAY, 0))).openConnection()).getInputStream()));
                    try {
                        StringBuilder sb = new StringBuilder();
                        while (true) {
                            String line = bufferedReader2.readLine();
                            if (line == null) {
                                Log.i(HolidayHelper.TAG, sb.toString());
                                SharedPreferences.Editor editorEdit = getHolidaySharedPreference(context).edit();
                                editorEdit.putString(PREFERENCE_HOLIDAY_DATA, sb.toString());
                                editorEdit.apply();
                                try {
                                    bufferedReader2.close();
                                    return true;
                                } catch (IOException e) {
                                    Log.e("updateHolidayData() error", e);
                                    return true;
                                }
                            }
                            sb.append(line);
                        }
                    } catch (MalformedURLException e2) {
                        e = e2;
                        bufferedReader = bufferedReader2;
                        Log.e("updateHolidayData() url exception", e);
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                        return false;
                    } catch (IOException e3) {
                        e = e3;
                        bufferedReader = bufferedReader2;
                        Log.e("updateHolidayData() io exception", e);
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                        return false;
                    } catch (Throwable th) {
                        th = th;
                        bufferedReader = bufferedReader2;
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e4) {
                                Log.e("updateHolidayData() error", e4);
                            }
                        }
                        throw th;
                    }
                } catch (MalformedURLException e5) {
                    e = e5;
                } catch (IOException e6) {
                    e = e6;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e7) {
            Log.e("updateHolidayData() error", e7);
        }
    }

    public static SharedPreferences getHolidaySharedPreference(Context context) {
        return FBEUtil.getSharedPreferences(context, PREFERENCE_HOLIDAY_FILE, 0);
    }
}
