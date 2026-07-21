package com.android.deskclock.addition.resource;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import com.android.deskclock.util.FileUtil;
import java.io.File;
import miuix.os.Build;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class LoadManager {
    private static final String GLOBAL_RESOURCE_URL = "aHR0cHM6Ly9nbG9iYWwubWFya2V0LnhpYW9taS5jb20vYXBtL2NvbnRlbnQvbWl1aWFwcC9yZXNvdXJjZT8=";
    private static final String KEY_RESOURCE_ID = "resourceId";
    private static final String NEW_DATA_PATH = "product/prebuilts/com.android.deskclock/com.android.deskclocklib.res20190812.apk";
    private static final String NEW_DATA_PATH1 = "product/prebuilts/com.android.deskclocklib.res20190812/com.android.deskclocklib.res20190812.apk";
    private static final String OLD_DATA_PATH = "cust/prebuilts/com.android.deskclock/com.android.deskclocklib.res20190812.apk";
    private static final String RESOURCE_URL = "aHR0cHM6Ly9hcHAubWFya2V0LnhpYW9taS5jb20vYXBtL2NvbnRlbnQvbWl1aWFwcC9yZXNvdXJjZT8=";
    public static final String TAG = "DC:LoadManager";

    public interface ModuleLoadListener {
        void onLoadFail(String str, int i);

        void onLoadSuccess(String str, String str2);
    }

    public static void loadNetModule(ModuleLoadListener moduleLoadListener, String str, String str2) throws Throwable {
        String str3;
        Log.d(TAG, "loadNetModule");
        if (moduleLoadListener == null) {
            return;
        }
        File file = new File(str2);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (Build.IS_INTERNATIONAL_BUILD) {
            str3 = new String(Base64.decode(GLOBAL_RESOURCE_URL, 0));
        } else {
            str3 = new String(Base64.decode(RESOURCE_URL, 0));
        }
        Uri.Builder builderBuildUpon = Uri.parse(str3).buildUpon();
        builderBuildUpon.appendQueryParameter(KEY_RESOURCE_ID, str);
        String string = builderBuildUpon.build().toString();
        Log.d(TAG, "loadNetModule, metaDataUrl: " + string);
        JSONObject jSONObjectDownloadMetadata = NetworkUtils.downloadMetadata(string);
        if (jSONObjectDownloadMetadata != null) {
            Metadata metadata = Metadata.parse(jSONObjectDownloadMetadata);
            Log.d(TAG, "loadNetModule, metadata: " + metadata);
            if (metadata != null) {
                String str4 = metadata.mHost + metadata.mUrl;
                File file2 = new File(str2, metadata.mResourceId + ResourceRepository.MODULE_FILE_EXTENSION);
                if (NetworkUtils.downloadFile(str4, file2) && file2.exists() && MD5Utils.checkMD5(file2, metadata.mFileHash)) {
                    Log.d(TAG, "loadNetModule sucess");
                    moduleLoadListener.onLoadSuccess(str, file2.getAbsolutePath());
                    return;
                }
            }
        }
        Log.d(TAG, "loadNetModule failed");
        moduleLoadListener.onLoadFail(str, -1);
    }

    public static void loadRomModule(ModuleLoadListener moduleLoadListener, String str, String str2) throws Throwable {
        Log.d(TAG, "loadRomModule");
        File file = new File(OLD_DATA_PATH);
        if (!file.exists()) {
            file = new File(NEW_DATA_PATH);
        }
        if (!file.exists()) {
            file = new File(NEW_DATA_PATH1);
        }
        if (file.exists()) {
            Log.d(TAG, "loadRomModule from: " + file.getAbsolutePath());
            File file2 = new File(str2);
            if (!file2.exists()) {
                file2.mkdirs();
            }
            File file3 = new File(str2, str + ResourceRepository.MODULE_FILE_EXTENSION);
            boolean zCopyFile = FileUtil.copyFile(file, file3);
            if (moduleLoadListener != null) {
                if (zCopyFile) {
                    Log.d(TAG, "loadRomModule success");
                    moduleLoadListener.onLoadSuccess(str, file3.getAbsolutePath());
                    return;
                } else {
                    Log.d(TAG, "loadRomModule failed");
                    moduleLoadListener.onLoadFail(str, -1);
                    return;
                }
            }
            return;
        }
        Log.d(TAG, "data not exist");
        if (moduleLoadListener != null) {
            moduleLoadListener.onLoadFail(str, -1);
        }
    }
}
