package com.xiaomi.settingsdk.backup.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.xiaomi.onetrack.api.g;
import com.xiaomi.settingsdk.backup.SettingsBackupConsts;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class KeyJsonSettingItem extends SettingItem<JSONObject> {
    public static final Parcelable.Creator<KeyJsonSettingItem> CREATOR = new Parcelable.Creator<KeyJsonSettingItem>() { // from class: com.xiaomi.settingsdk.backup.data.KeyJsonSettingItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyJsonSettingItem[] newArray(int i) {
            return new KeyJsonSettingItem[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyJsonSettingItem createFromParcel(Parcel parcel) {
            KeyJsonSettingItem keyJsonSettingItem = new KeyJsonSettingItem();
            keyJsonSettingItem.fillFromParcel(parcel);
            return keyJsonSettingItem;
        }
    };
    public static final String TYPE = "json";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    public JSONObject stringToValue(String str) {
        try {
            return new JSONObject(str);
        } catch (JSONException e) {
            Log.e(SettingsBackupConsts.TAG, "JSONException occorred when stringToValue()", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    public String valueToString(JSONObject jSONObject) {
        return jSONObject.toString();
    }

    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    protected void setValueFromJson(JSONObject jSONObject) {
        setValue(jSONObject.optJSONObject(g.p));
    }

    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    protected Object getJsonValue() {
        return getValue();
    }

    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    protected String getType() {
        return TYPE;
    }
}
