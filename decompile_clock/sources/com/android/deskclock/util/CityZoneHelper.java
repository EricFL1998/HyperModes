package com.android.deskclock.util;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.SparseArray;
import com.android.deskclock.AsyncHandler;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.worldclock.CityNameComparator;
import com.android.deskclock.worldclock.CityObj;
import com.android.deskclock.worldclock.WorldClockEditActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import miuix.pinyin.utilities.ChinesePinyinConverter;

/* JADX INFO: loaded from: classes.dex */
public class CityZoneHelper {
    private static final String CITY_ZONE_FILE = "city_timezone";
    private static HashMap<String, String> mChinese2PinyinCityName;
    private static HashMap<String, String> mChinese2PinyinCountryName;
    public static List<CityObj> mCityTimezoneItems;
    private static String mLanguage = Locale.getDefault().toLanguageTag();

    public static void init() {
        if (!mLanguage.equals(Locale.getDefault().toLanguageTag())) {
            mCityTimezoneItems = Arrays.asList(loadCitiesFromXml(DeskClockApp.getAppDEContext()));
            if (isChineseLocale()) {
                buildChinese2PinYin();
                buildChinese2CountryPinYin();
            }
            mLanguage = Locale.getDefault().toLanguageTag();
            return;
        }
        if (mCityTimezoneItems == null) {
            mCityTimezoneItems = Arrays.asList(loadCitiesFromXml(DeskClockApp.getAppDEContext()));
        }
        if (isChineseLocale()) {
            if (mChinese2PinyinCityName == null) {
                buildChinese2PinYin();
            }
            if (mChinese2PinyinCountryName == null) {
                buildChinese2CountryPinYin();
            }
        }
    }

    public static void sort() {
        Collections.sort(mCityTimezoneItems, new CityNameComparator());
    }

    public static CityObj getCityTimezoneItemById(String str) {
        for (CityObj cityObj : mCityTimezoneItems) {
            if (cityObj.mCityId.equals(str)) {
                return cityObj;
            }
            if (str.equals(WorldClockEditActivity.LOCAL_CITY_ID)) {
                return LocalCityZoneUtil.getLocalTimeZone();
            }
        }
        return null;
    }

    public ArrayList<CityObj> getTimezonesItemByCursor(Cursor cursor) {
        ArrayList<CityObj> arrayList = new ArrayList<>();
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        arrayList.add(getCityTimezoneItemById(cursor.getString(1)));
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                Log.e("getTimezonesItemByCursor error", e);
            }
        }
        return arrayList;
    }

    public static CityObj getCityByTimeZone(String str) {
        for (CityObj cityObj : mCityTimezoneItems) {
            if (cityObj.mTimeZone.equals(str)) {
                return cityObj;
            }
        }
        return null;
    }

    public static ArrayList<CityObj> queryCityTimezoneItems(String str) {
        if (TextUtils.isEmpty(str)) {
            return new ArrayList<>(mCityTimezoneItems);
        }
        ArrayList<CityObj> arrayList = new ArrayList<>();
        String lowerCase = str.toLowerCase();
        for (CityObj cityObj : mCityTimezoneItems) {
            boolean z = cityObj.mCityName.toLowerCase().contains(lowerCase) || cityObj.mCountryName.toLowerCase().contains(lowerCase) || cityObj.getTimezoneDisplay().toLowerCase().contains(lowerCase);
            if (!z && isChineseLocale()) {
                z = chinese2PinyinCityNameContainQuery(cityObj, lowerCase) || chinese2PinyinCountryNameContainQuery(cityObj, lowerCase);
            }
            if (z) {
                arrayList.add(cityObj);
            }
        }
        return arrayList;
    }

    private static boolean chinese2PinyinCityNameContainQuery(CityObj cityObj, String str) {
        HashMap<String, String> map = mChinese2PinyinCityName;
        if (map != null) {
            String str2 = map.get(cityObj.mCityName);
            if (str2 != null) {
                return str2.toLowerCase().contains(str);
            }
            return false;
        }
        buildChinese2PinYin();
        return false;
    }

    private static boolean chinese2PinyinCountryNameContainQuery(CityObj cityObj, String str) {
        HashMap<String, String> map = mChinese2PinyinCountryName;
        if (map != null) {
            String str2 = map.get(cityObj.mCountryName);
            if (str2 != null) {
                return str2.toLowerCase().contains(str);
            }
            return false;
        }
        buildChinese2CountryPinYin();
        return false;
    }

    private static void buildChinese2PinYin() {
        AsyncHandler.post(new Runnable() { // from class: com.android.deskclock.util.CityZoneHelper.1
            @Override // java.lang.Runnable
            public void run() {
                if (CityZoneHelper.mCityTimezoneItems != null) {
                    if (CityZoneHelper.mChinese2PinyinCityName != null) {
                        return;
                    }
                    HashMap unused = CityZoneHelper.mChinese2PinyinCityName = new HashMap();
                    for (CityObj cityObj : CityZoneHelper.mCityTimezoneItems) {
                        ArrayList<ChinesePinyinConverter.Token> arrayList = ChinesePinyinConverter.getInstance(DeskClockApp.getAppDEContext()).get(cityObj.mCityName);
                        StringBuilder sb = new StringBuilder();
                        Iterator<ChinesePinyinConverter.Token> it = arrayList.iterator();
                        while (it.hasNext()) {
                            sb.append(it.next().target);
                        }
                        CityZoneHelper.mChinese2PinyinCityName.put(cityObj.mCityName, sb.toString());
                    }
                    return;
                }
                Log.i("No timezone items, error");
            }
        });
    }

    private static void buildChinese2CountryPinYin() {
        AsyncHandler.post(new Runnable() { // from class: com.android.deskclock.util.CityZoneHelper.2
            @Override // java.lang.Runnable
            public void run() {
                if (CityZoneHelper.mCityTimezoneItems != null && CityZoneHelper.mChinese2PinyinCountryName == null) {
                    HashMap unused = CityZoneHelper.mChinese2PinyinCountryName = new HashMap();
                    for (CityObj cityObj : CityZoneHelper.mCityTimezoneItems) {
                        ArrayList<ChinesePinyinConverter.Token> arrayList = ChinesePinyinConverter.getInstance(DeskClockApp.getAppDEContext()).get(cityObj.mCountryName);
                        StringBuilder sb = new StringBuilder();
                        Iterator<ChinesePinyinConverter.Token> it = arrayList.iterator();
                        while (it.hasNext()) {
                            sb.append(it.next().target);
                        }
                        CityZoneHelper.mChinese2PinyinCountryName.put(cityObj.mCountryName, sb.toString());
                    }
                }
            }
        });
    }

    public static SparseArray<String> getOldIdConverter(Context context) {
        SparseArray<String> sparseArray = new SparseArray<>(256);
        BufferedReader bufferedReader = null;
        try {
            try {
                try {
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(context.getAssets().open(CITY_ZONE_FILE)));
                    while (true) {
                        try {
                            String line = bufferedReader2.readLine();
                            if (line == null) {
                                break;
                            }
                            String[] strArrSplit = line.split("\t");
                            if (strArrSplit != null && strArrSplit.length == 7) {
                                int i = Integer.parseInt(strArrSplit[0]);
                                String str = strArrSplit[6];
                                if (!TextUtils.isEmpty(str) && str.startsWith("C")) {
                                    sparseArray.put(i, str);
                                }
                            }
                        } catch (IOException e) {
                            e = e;
                            bufferedReader = bufferedReader2;
                            Log.e("parse city timezone error", e);
                            if (bufferedReader != null) {
                                bufferedReader.close();
                            }
                            return sparseArray;
                        } catch (Throwable th) {
                            th = th;
                            bufferedReader = bufferedReader2;
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e2) {
                                    Log.e("close parse city timezone error", e2);
                                }
                            }
                            throw th;
                        }
                    }
                    bufferedReader2.close();
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (IOException e3) {
                e = e3;
            }
        } catch (IOException e4) {
            Log.e("close parse city timezone error", e4);
        }
        return sparseArray;
    }

    private static CityObj[] loadCitiesFromXml(Context context) {
        Resources resources = context.getResources();
        String[] stringArray = resources.getStringArray(R.array.cities_names_miui);
        String[] stringArray2 = resources.getStringArray(R.array.cities_tz_miui);
        String[] stringArray3 = resources.getStringArray(R.array.cities_id_miui);
        String[] stringArray4 = resources.getStringArray(R.array.countries_names_miui);
        int length = stringArray.length;
        if (length != stringArray2.length || stringArray3.length != length || length != stringArray4.length) {
            length = Math.min(Math.min(stringArray.length, Math.min(stringArray2.length, stringArray3.length)), stringArray4.length);
            Log.e("City lists sizes are not the same, trancating");
        }
        CityObj[] cityObjArr = new CityObj[length];
        for (int i = 0; i < length; i++) {
            try {
                cityObjArr[i] = new CityObj(stringArray[i], stringArray2[i], stringArray3[i], stringArray4[i]);
            } catch (Exception e) {
                Log.e("parse city error city=" + stringArray[i], e);
            }
        }
        return cityObjArr;
    }

    public static boolean isChineseLocale() {
        return Locale.getDefault().equals(Locale.CHINESE) || Locale.getDefault().equals(Locale.SIMPLIFIED_CHINESE) || Locale.getDefault().equals(Locale.TRADITIONAL_CHINESE);
    }
}
