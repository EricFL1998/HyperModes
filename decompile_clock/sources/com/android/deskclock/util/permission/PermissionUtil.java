package com.android.deskclock.util.permission;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.android.deskclock.R;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class PermissionUtil {
    public static final int NOTIFICATION_REQUEST_CODE = 1000;
    private static final int PERMISSION_FLAG_ASK = 1;
    private static final int PERMISSION_FLAG_DENIED = -1;
    private static final int PERMISSION_FLAG_FOREGROUND = 2;
    private static final int PERMISSION_FLAG_GRANTED = 0;
    public static final int PERMISSION_REQUEST = 1;
    public static final int READ_MEDIA_AUDIO_REQUEST_CODE = 2000;
    private static String TAG = "DC:PermissionUtil";

    public static boolean requestPermissionIfNeeded(Activity activity, String str) {
        if (!canPermissionAsk(activity, str)) {
            return false;
        }
        requestPermissions(activity, str);
        return true;
    }

    public static boolean requestPermissionIfNeeded(Activity activity, String str, int i) {
        if (!canPermissionAsk(activity, str)) {
            return false;
        }
        requestPermissions(activity, str, i);
        return true;
    }

    public static boolean requestPermissionIfNeeded(Fragment fragment, String str) {
        if (!canPermissionAsk(fragment.getContext(), str)) {
            return false;
        }
        requestPermissions(fragment, str);
        return true;
    }

    public static boolean isPermissionGranted(Context context, String str) {
        if (context == null) {
            return false;
        }
        return checkPermission(context, str);
    }

    public static boolean canPermissionAsk(Context context, String str) {
        if (isSupportPermissionStatus(context)) {
            return getPermissionStatus(context, str) == 1;
        }
        return !checkPermission(context, str);
    }

    public static boolean checkPermission(Context context, String str) {
        return context.checkSelfPermission(str) == 0;
    }

    public static void requestPermissions(Activity activity, String str, String str2, int i) {
        activity.requestPermissions(new String[]{str, str2}, i);
    }

    public static void requestPermissions(Activity activity, String str, int i) {
        activity.requestPermissions(new String[]{str, activity.getResources().getString(R.string.read_external_storage_permission_desc)}, i);
    }

    private static void requestPermissions(Activity activity, String str) {
        activity.requestPermissions(new String[]{str, activity.getResources().getString(R.string.read_external_storage_permission_desc)}, 1);
    }

    private static void requestPermissions(Fragment fragment, String str) {
        fragment.requestPermissions(new String[]{str, fragment.getResources().getString(R.string.read_external_storage_permission_desc)}, 1);
    }

    private static int getPermissionStatus(Context context, String str) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("permissionName", str);
            return context.getContentResolver().call(Uri.parse("content://com.lbe.security.miui.autostartmgr"), "getPermissionState", (String) null, bundle).getInt("flag");
        } catch (Throwable unused) {
            return context.checkSelfPermission(str);
        }
    }

    private static boolean isSupportPermissionStatus(Context context) {
        return getMeta(context);
    }

    private static boolean getMeta(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo("com.lbe.security.miui", 128).metaData.getBoolean("miui.supportGetPermissionState", false);
        } catch (Exception e) {
            Log.e(TAG, "getMeta error=" + e);
            return false;
        }
    }

    public static boolean isNewPrivacyPolicySupport() {
        return Util.isMiUi11() && !Util.isInternational();
    }

    public static boolean shouldAskReadPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 33) {
            if (UserNoticeUtil.isCtaAgreed() && isPermissionGranted(activity, "android.permission.READ_MEDIA_AUDIO")) {
                return true;
            }
            if (UserNoticeUtil.isCtaAgreed() && requestPermissionIfNeeded(activity, "android.permission.READ_MEDIA_AUDIO")) {
                return true;
            }
        } else {
            if (UserNoticeUtil.isCtaAgreed() && isPermissionGranted(activity, "android.permission.READ_EXTERNAL_STORAGE")) {
                return true;
            }
            if (UserNoticeUtil.isCtaAgreed() && requestPermissionIfNeeded(activity, "android.permission.READ_EXTERNAL_STORAGE")) {
                return true;
            }
        }
        return false;
    }

    public static boolean askReadPermissionByRingtoneChange(Activity activity) {
        String str = Build.VERSION.SDK_INT >= 33 ? "android.permission.READ_MEDIA_AUDIO" : "android.permission.READ_EXTERNAL_STORAGE";
        if (!UserNoticeUtil.isCtaAgreed() || isPermissionGranted(activity, str)) {
            return false;
        }
        return requestPermissionIfNeeded(activity, str, 2000);
    }

    public static boolean isCTAAndReadPermissionGranted(Activity activity) {
        return UserNoticeUtil.isCtaAgreed() && isPermissionGranted(activity, Build.VERSION.SDK_INT >= 33 ? "android.permission.READ_MEDIA_AUDIO" : "android.permission.READ_EXTERNAL_STORAGE");
    }

    public static boolean shouldShowCtaPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 33) {
            if (!isPermissionGranted(activity, "android.permission.READ_MEDIA_AUDIO") && !canPermissionAsk(activity, "android.permission.READ_MEDIA_AUDIO")) {
                return false;
            }
        } else if (!isPermissionGranted(activity, "android.permission.READ_EXTERNAL_STORAGE") && !canPermissionAsk(activity, "android.permission.READ_EXTERNAL_STORAGE")) {
            return false;
        }
        return true;
    }

    public static boolean shouldNotAskPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 33) {
            if (!isPermissionGranted(activity, "android.permission.READ_MEDIA_AUDIO") && !requestPermissionIfNeeded(activity, "android.permission.READ_MEDIA_AUDIO")) {
                return true;
            }
        } else if (!isPermissionGranted(activity, "android.permission.READ_EXTERNAL_STORAGE") && !requestPermissionIfNeeded(activity, "android.permission.READ_EXTERNAL_STORAGE")) {
            return true;
        }
        return false;
    }
}
