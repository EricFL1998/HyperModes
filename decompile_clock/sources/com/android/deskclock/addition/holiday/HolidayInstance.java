package com.android.deskclock.addition.holiday;

import android.content.Context;
import com.android.deskclock.R;
import com.android.deskclock.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class HolidayInstance {
    private static final String JSON_TAG_FREEDAY = "freeday";
    private static final String JSON_TAG_HOLIDAY = "holiday";
    private static final String JSON_TAG_VERSION = "versioncode";
    private static final String JSON_TAG_WORKDAY = "workday";
    private static final String JSON_TAG_YEAR = "year";
    private static HolidayInstance sInstance;
    private ArrayList<HolidayData> mHolidayArray = new ArrayList<>();
    private int mVersionCode;

    class HolidayData {
        public HashSet<Integer> mFreedaySet;
        public HashSet<Integer> mWorkdaySet;
        public int mYear;

        public HolidayData() {
        }

        public HolidayData(int i, HashSet<Integer> hashSet, HashSet<Integer> hashSet2) {
            this.mYear = i;
            this.mWorkdaySet = hashSet;
            this.mFreedaySet = hashSet2;
        }
    }

    public static synchronized HolidayInstance getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new HolidayInstance(context);
        }
        return sInstance;
    }

    public ArrayList<HolidayData> getHolidayData() {
        return this.mHolidayArray;
    }

    private HolidayInstance(Context context) throws Throwable {
        initHolidayData(context);
    }

    /* JADX WARN: Code duplicated, block: B:30:0x0070 A[Catch: Exception -> 0x006c, TRY_LEAVE, TryCatch #3 {Exception -> 0x006c, blocks: (B:26:0x0068, B:30:0x0070), top: B:50:0x0068 }] */
    /* JADX WARN: Code duplicated, block: B:41:0x0089 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:42:0x008b A[Catch: Exception -> 0x0087, TRY_LEAVE, TryCatch #5 {Exception -> 0x0087, blocks: (B:38:0x0083, B:42:0x008b), top: B:52:0x0083 }] */
    /* JADX WARN: Code duplicated, block: B:52:0x0083 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private String loadHolidayData(Context context) throws Throwable {
        BufferedReader bufferedReader;
        InputStreamReader inputStreamReader;
        Exception e;
        StringBuilder sb;
        StringBuilder sb2 = new StringBuilder();
        InputStreamReader inputStreamReader2 = null;
        try {
            inputStreamReader = new InputStreamReader(context.getResources().openRawResource(R.raw.holiday));
            try {
                bufferedReader = new BufferedReader(inputStreamReader);
                while (true) {
                    try {
                        try {
                            String line = bufferedReader.readLine();
                            if (line != null) {
                                sb2.append(line);
                            } else {
                                try {
                                    break;
                                } catch (Exception e2) {
                                    e = e2;
                                    sb = new StringBuilder("loadHolidayData() finally error is ");
                                    Log.e(sb.append(e).toString());
                                }
                            }
                        } catch (Throwable th) {
                            th = th;
                            inputStreamReader2 = inputStreamReader;
                            if (inputStreamReader2 != null) {
                                try {
                                    inputStreamReader2.close();
                                    if (bufferedReader != null) {
                                        bufferedReader.close();
                                    }
                                } catch (Exception e3) {
                                    Log.e("loadHolidayData() finally error is " + e3);
                                    throw th;
                                }
                            } else if (bufferedReader != null) {
                                bufferedReader.close();
                            }
                            throw th;
                        }
                    } catch (Exception e4) {
                        e = e4;
                        Log.e("loadHolidayData() error is" + e);
                        if (inputStreamReader != null) {
                            try {
                                inputStreamReader.close();
                                if (bufferedReader != null) {
                                    bufferedReader.close();
                                }
                            } catch (Exception e5) {
                                e = e5;
                                sb = new StringBuilder("loadHolidayData() finally error is ");
                                Log.e(sb.append(e).toString());
                            }
                        } else if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                    }
                }
                inputStreamReader.close();
                bufferedReader.close();
            } catch (Exception e6) {
                e = e6;
                bufferedReader = null;
            } catch (Throwable th2) {
                th = th2;
                bufferedReader = null;
                inputStreamReader2 = inputStreamReader;
                if (inputStreamReader2 != null) {
                    inputStreamReader2.close();
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } else if (bufferedReader != null) {
                    bufferedReader.close();
                }
                throw th;
            }
        } catch (Exception e7) {
            inputStreamReader = null;
            e = e7;
            bufferedReader = null;
        } catch (Throwable th3) {
            th = th3;
            bufferedReader = null;
            if (inputStreamReader2 != null) {
                inputStreamReader2.close();
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } else if (bufferedReader != null) {
                bufferedReader.close();
            }
            throw th;
        }
        return sb2.toString();
    }

    public boolean initHolidayData(Context context) throws Throwable {
        this.mVersionCode = HolidayUtil.getRemoteVersion(context);
        String strLoadHolidayData = loadHolidayData(context);
        if (strLoadHolidayData != null) {
            parseHoliday(context, strLoadHolidayData);
        }
        String string = HolidayUtil.getHolidaySharedPreference(context).getString("pref_holiday_data", null);
        boolean z = false;
        if (string != null) {
            try {
                JSONObject jSONObject = new JSONObject(string);
                if (jSONObject.getInt(JSON_TAG_VERSION) >= this.mVersionCode) {
                    if (jSONObject.getInt(JSON_TAG_VERSION) > this.mVersionCode) {
                        parseHoliday(context, string);
                    }
                    z = true;
                }
            } catch (JSONException e) {
                Log.e("initHolidayData() json exception", e);
            }
        }
        Log.i("current holiday data version: " + this.mVersionCode + z);
        return z;
    }

    private void parseHoliday(Context context, String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            this.mHolidayArray.clear();
            JSONArray jSONArray = jSONObject.getJSONArray(JSON_TAG_HOLIDAY);
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                int i2 = jSONObject2.getInt(JSON_TAG_YEAR);
                JSONArray jSONArray2 = jSONObject2.getJSONArray(JSON_TAG_WORKDAY);
                HashSet hashSet = new HashSet();
                for (int i3 = 0; i3 < jSONArray2.length(); i3++) {
                    hashSet.add(Integer.valueOf(jSONArray2.getInt(i3)));
                }
                JSONArray jSONArray3 = jSONObject2.getJSONArray(JSON_TAG_FREEDAY);
                HashSet hashSet2 = new HashSet();
                for (int i4 = 0; i4 < jSONArray3.length(); i4++) {
                    hashSet2.add(Integer.valueOf(jSONArray3.getInt(i4)));
                }
                this.mHolidayArray.add(new HolidayData(i2, hashSet, hashSet2));
            }
            int i5 = jSONObject.getInt(JSON_TAG_VERSION);
            this.mVersionCode = i5;
            HolidayUtil.setRemoteVersion(context, i5);
        } catch (JSONException e) {
            Log.e("parseHoliday() json exception", e);
        }
    }

    public boolean isFreeday(int i, int i2) {
        int theYearHolidayData = getTheYearHolidayData(i);
        return theYearHolidayData >= 0 && theYearHolidayData < this.mHolidayArray.size() && this.mHolidayArray.get(theYearHolidayData).mFreedaySet != null && this.mHolidayArray.get(theYearHolidayData).mFreedaySet.contains(Integer.valueOf(i2));
    }

    public boolean isWorkday(int i, int i2) {
        int theYearHolidayData = getTheYearHolidayData(i);
        return theYearHolidayData >= 0 && theYearHolidayData < this.mHolidayArray.size() && this.mHolidayArray.get(theYearHolidayData).mWorkdaySet != null && this.mHolidayArray.get(theYearHolidayData).mWorkdaySet.contains(Integer.valueOf(i2));
    }

    public int getTheYearHolidayData(int i) {
        for (int i2 = 0; i2 < this.mHolidayArray.size(); i2++) {
            if (this.mHolidayArray.get(i2) != null && this.mHolidayArray.get(i2).mYear == i) {
                return i2;
            }
        }
        return -1;
    }
}
