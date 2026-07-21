package com.xiaomi.settingsdk.backup.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public abstract class SettingItem<T> implements Parcelable, Comparable<SettingItem<?>> {
    private static final String KEY_KEY = "key";
    private static final String KEY_TYPE = "type";
    protected static final String KEY_VALUE = "value";
    protected static final String TAG = "SettingsBackup";
    public String key;
    private T value;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected abstract Object getJsonValue();

    protected abstract String getType();

    protected abstract void setValueFromJson(JSONObject jSONObject);

    protected abstract T stringToValue(String str);

    protected abstract String valueToString(T t);

    public T getValue() {
        return this.value;
    }

    public void setValue(T t) {
        this.value = t;
    }

    public static SettingItem<?> fromJson(JSONObject jSONObject) {
        if (jSONObject == null) {
            throw new IllegalArgumentException("json cannot be null");
        }
        SettingItem<?> settingItemCreateByType = createByType(jSONObject.optString("type"));
        if (settingItemCreateByType == null) {
            return null;
        }
        settingItemCreateByType.key = jSONObject.optString(KEY_KEY);
        settingItemCreateByType.setValueFromJson(jSONObject);
        return settingItemCreateByType;
    }

    private static SettingItem<?> createByType(String str) {
        if ("string".equals(str)) {
            return new KeyStringSettingItem();
        }
        if (KeyBinarySettingItem.TYPE.equals(str)) {
            return new KeyBinarySettingItem();
        }
        if (KeyJsonSettingItem.TYPE.equals(str)) {
            return new KeyJsonSettingItem();
        }
        Log.w("SettingsBackup", "type: " + str + " are not handled!");
        return null;
    }

    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(KEY_KEY, this.key);
            jSONObject.put("type", getType());
            jSONObject.put("value", getJsonValue());
        } catch (JSONException e) {
            Log.e("SettingsBackup", "JSONException occorred when toJson()", e);
        }
        return jSONObject;
    }

    protected void fillFromParcel(Parcel parcel) {
        String string = parcel.readString();
        String string2 = parcel.readString();
        this.key = string;
        setValue(stringToValue(string2));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        String strValueToString = valueToString(getValue());
        parcel.writeString(this.key);
        parcel.writeString(strValueToString);
    }

    @Override // java.lang.Comparable
    public int compareTo(SettingItem<?> settingItem) {
        if (settingItem == null) {
            return 1;
        }
        String str = this.key;
        if (str != null || settingItem.key == null) {
            return str.compareTo(settingItem.key);
        }
        return -1;
    }
}
