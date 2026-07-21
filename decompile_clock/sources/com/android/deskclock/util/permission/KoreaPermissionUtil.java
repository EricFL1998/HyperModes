package com.android.deskclock.util.permission;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import androidx.activity.result.ActivityResultLauncher;
import com.android.deskclock.BuildConfig;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class KoreaPermissionUtil {
    private static final String FLAG_NECESSARY = "1";
    private static final String KOREA_AUTHORIZE_ACTION = "miui.intent.action.APP_PERMISSION_USE";
    public static final int KOREA_AUTH_REQUEST_CODE = 2307;
    private static final int KOREA_AUTH_RESULT_CODE = 2308;
    private static final String PERMISSION_CALENDAR = "android.permission-group.CALENDAR";
    private static final int PERMISSION_CALENDAR_DESC = 2131886424;
    private static final int PERMISSION_CALENDAR_TITLE = 2131886423;
    private static final String PERMISSION_STORAGE = "android.permission-group.STORAGE";
    private static final int PERMISSION_STORAGE_DESC = 2131886426;
    private static final int PERMISSION_STORAGE_TITLE = 2131886425;
    private static final String PREF_KEY_KOREA_AUTH_DISPLAYED = "pref_key_korea_auth_displayed";
    private static final String REGION_KOREA = "KR";
    private static final String SEPARATOR = "@";

    public static boolean isKoreaResultCode(int i) {
        return i == 2308;
    }

    private KoreaPermissionUtil() {
    }

    public static void showAuthorization(Activity activity, ActivityResultLauncher<Intent> activityResultLauncher) {
        if (isKoreaAuthDisplayed()) {
            return;
        }
        String region = Util.getRegion();
        if (!TextUtils.isEmpty(region) && REGION_KOREA.equals(region)) {
            try {
                Intent intent = new Intent(KOREA_AUTHORIZE_ACTION);
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("android.permission-group.STORAGE@1@" + activity.getString(R.string.korea_permission_storage) + SEPARATOR + activity.getString(R.string.korea_permission_storage_desc));
                intent.putStringArrayListExtra("extra_main_permission_groups", arrayList);
                intent.putExtra("extra_pkgname", BuildConfig.APPLICATION_ID);
                activityResultLauncher.launch(intent);
            } catch (Exception unused) {
                Log.e("korea authorize error");
            }
        }
    }

    public static void handleKoreaAuthCallback(int i) {
        if (i == 2308) {
            setKoreaAuthDisplayed(true);
        }
    }

    public static boolean isKoreaAuthDisplayed() {
        return FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).getBoolean(PREF_KEY_KOREA_AUTH_DISPLAYED, false);
    }

    private static void setKoreaAuthDisplayed(boolean z) {
        FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).edit().putBoolean(PREF_KEY_KOREA_AUTH_DISPLAYED, z).apply();
    }

    public static boolean isKoreaRegion() {
        return REGION_KOREA.equals(Util.getRegion());
    }
}
