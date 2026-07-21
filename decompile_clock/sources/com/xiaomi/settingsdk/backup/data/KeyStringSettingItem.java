package com.xiaomi.settingsdk.backup.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.xiaomi.onetrack.api.g;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class KeyStringSettingItem extends SettingItem<String> {
    public static final Parcelable.Creator<KeyStringSettingItem> CREATOR = new Parcelable.Creator<KeyStringSettingItem>() { // from class: com.xiaomi.settingsdk.backup.data.KeyStringSettingItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyStringSettingItem[] newArray(int i) {
            return new KeyStringSettingItem[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyStringSettingItem createFromParcel(Parcel parcel) {
            KeyStringSettingItem keyStringSettingItem = new KeyStringSettingItem();
            keyStringSettingItem.fillFromParcel(parcel);
            return keyStringSettingItem;
        }
    };
    public static final String TYPE = "string";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    public String stringToValue(String str) {
        return str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    public String valueToString(String str) {
        return str;
    }

    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    protected Object getJsonValue() {
        return getValue();
    }

    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    protected void setValueFromJson(JSONObject jSONObject) {
        setValue(jSONObject.optString(g.p));
    }

    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    protected String getType() {
        return "string";
    }
}
