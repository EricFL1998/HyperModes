package com.xiaomi.settingsdk.backup.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class DataPackage implements Parcelable {
    public static final Parcelable.Creator<DataPackage> CREATOR = new Parcelable.Creator<DataPackage>() { // from class: com.xiaomi.settingsdk.backup.data.DataPackage.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DataPackage[] newArray(int i) {
            return new DataPackage[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DataPackage createFromParcel(Parcel parcel) {
            return DataPackage.parseDataPackageBundle(parcel.readBundle());
        }
    };
    public static final String KEY_DATA_PACKAGE = "data_package";
    public static final String KEY_VERSION = "version";
    private final Map<String, SettingItem<?>> mDataItems = new HashMap();
    private final Map<String, ParcelFileDescriptor> mFileItems = new HashMap();

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Map<String, SettingItem<?>> getDataItems() {
        return this.mDataItems;
    }

    public Map<String, ParcelFileDescriptor> getFileItems() {
        return this.mFileItems;
    }

    public static int getVersionFromBundle(Bundle bundle) {
        return bundle.getInt("version");
    }

    public static DataPackage fromWrappedBundle(Bundle bundle) {
        Bundle bundle2 = (Bundle) bundle.clone();
        bundle2.setClassLoader(SettingItem.class.getClassLoader());
        return parseDataPackageBundle(bundle2.getBundle(KEY_DATA_PACKAGE));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static DataPackage parseDataPackageBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        bundle.setClassLoader(SettingItem.class.getClassLoader());
        DataPackage dataPackage = new DataPackage();
        for (String str : bundle.keySet()) {
            Parcelable parcelable = bundle.getParcelable(str);
            if (parcelable instanceof SettingItem) {
                dataPackage.mDataItems.put(str, (SettingItem) parcelable);
            }
            if (parcelable instanceof ParcelFileDescriptor) {
                dataPackage.mFileItems.put(str, (ParcelFileDescriptor) parcelable);
            }
        }
        return dataPackage;
    }

    private Bundle getDataPackageBundle() {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, SettingItem<?>> entry : this.mDataItems.entrySet()) {
            bundle.putParcelable(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ParcelFileDescriptor> entry2 : this.mFileItems.entrySet()) {
            bundle.putParcelable(entry2.getKey(), entry2.getValue());
        }
        return bundle;
    }

    public void addAbstractDataItem(String str, SettingItem<?> settingItem) {
        this.mDataItems.put(str, settingItem);
    }

    public void addKeyJson(String str, JSONObject jSONObject) {
        KeyJsonSettingItem keyJsonSettingItem = new KeyJsonSettingItem();
        keyJsonSettingItem.key = str;
        keyJsonSettingItem.setValue(jSONObject);
        this.mDataItems.put(str, keyJsonSettingItem);
    }

    public void addKeyValue(String str, String str2) {
        KeyStringSettingItem keyStringSettingItem = new KeyStringSettingItem();
        keyStringSettingItem.key = str;
        keyStringSettingItem.setValue(str2);
        this.mDataItems.put(str, keyStringSettingItem);
    }

    public void addKeyFile(String str, File file) throws FileNotFoundException {
        this.mFileItems.put(str, ParcelFileDescriptor.open(file, 268435456));
    }

    public SettingItem<?> get(String str) {
        return this.mDataItems.get(str);
    }

    public ParcelFileDescriptor getFile(String str) {
        return this.mFileItems.get(str);
    }

    public void appendToWrappedBundle(Bundle bundle) {
        bundle.putBundle(KEY_DATA_PACKAGE, getDataPackageBundle());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBundle(getDataPackageBundle());
    }
}
