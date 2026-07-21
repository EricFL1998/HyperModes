package com.xiaomi.settingsdk.backup.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import com.xiaomi.onetrack.api.g;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class KeyBinarySettingItem extends SettingItem<byte[]> {
    public static final Parcelable.Creator<KeyBinarySettingItem> CREATOR = new Parcelable.Creator<KeyBinarySettingItem>() { // from class: com.xiaomi.settingsdk.backup.data.KeyBinarySettingItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyBinarySettingItem[] newArray(int i) {
            return new KeyBinarySettingItem[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyBinarySettingItem createFromParcel(Parcel parcel) {
            KeyBinarySettingItem keyBinarySettingItem = new KeyBinarySettingItem();
            keyBinarySettingItem.fillFromParcel(parcel);
            return keyBinarySettingItem;
        }
    };
    public static final String TYPE = "binary";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    public byte[] stringToValue(String str) {
        return Base64.decode(str, 2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    public String valueToString(byte[] bArr) {
        return Base64.encodeToString(bArr, 2);
    }

    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    protected Object getJsonValue() {
        return valueToString(getValue());
    }

    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    protected void setValueFromJson(JSONObject jSONObject) {
        setValue(stringToValue(jSONObject.optString(g.p)));
    }

    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    protected String getType() {
        return TYPE;
    }
}
