package com.android.deskclock.util.permission;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiSecurity;

/* JADX INFO: loaded from: classes.dex */
public class SystemPermissionUtil {
    private static final String ACTION_SYSTEM_PERMISSION_DECLARE = "miui.intent.action.SYSTEM_PERMISSION_DECLARE";
    private static final String ACTION_SYSTEM_PERMISSION_DECLARE_NEW = "miui.intent.action.SYSTEM_PERMISSION_DECLARE_NEW";
    private static final String KAY_OPTIONAL_PERM = "optional_perm";
    private static final String KAY_OPTIONAL_PERM_DESC = "optional_perm_desc";
    private static final String KEY_AGREE_DESC = "agree_desc";
    private static final String KEY_MAIN_PURPOSE = "main_purpose";
    private static final String KEY_MANDATORY_PERMISSION = "mandatory_permission";
    private static final String KEY_SHOW_LOCKED = "show_locked";
    private static final String KEY_USE_NETWORK = "use_network";
    private static final String SECURITY_CENTER_PACKAGE_NAME = "com.miui.securitycenter";
    public static final int SYSTEM_PERMISSION_PURE_REQUEST_CODE = 2;
    public static final int SYSTEM_PERMISSION_REQUEST_CODE = 1;
    public static final int SYSTEM_PERMISSION_REQUEST_NEW_CODE = 4;
    public static final int SYSTEM_PERMISSION_RESULT_AGREE = 1;
    public static final int SYSTEM_PERMISSION_RESULT_ERROR = -1;
    public static final int SYSTEM_PERMISSION_RESULT_ERROR_LANGUAGE_CHANGE = -3;
    public static final int SYSTEM_PERMISSION_RESULT_ERROR_NO_PERMISSION = -2;
    public static final int SYSTEM_PERMISSION_RESULT_NEW_REJECT = 666;
    public static final int SYSTEM_PERMISSION_RESULT_REJECT = 0;
    public static final String TAG = "DC:SystemPermissionUtil";

    private SystemPermissionUtil() {
    }

    public static boolean showPermissionDeclare(Activity activity, int i) {
        return showPermissionDeclare(activity, i, 4);
    }

    public static boolean showPermissionDeclare(Activity activity, int i, int i2) {
        if (activity == null) {
            return false;
        }
        try {
            if (!MiuiSecurity.supportSystemPermissionDeclare()) {
                return false;
            }
            Intent intent = new Intent();
            intent.setAction(ACTION_SYSTEM_PERMISSION_DECLARE_NEW);
            intent.setPackage(SECURITY_CENTER_PACKAGE_NAME);
            if (activity.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                Log.i(TAG, "showPermissionDeclare in new version");
                intent.putExtra(KEY_MANDATORY_PERMISSION, false);
                intent.putExtra(KEY_MAIN_PURPOSE, DeskClockApp.getAppDEContext().getString(R.string.system_permission_main_purpose));
                intent.putExtra(KEY_USE_NETWORK, true);
                intent.putExtra(KEY_AGREE_DESC, DeskClockApp.getAppDEContext().getString(R.string.system_permission_agree_desc));
                intent.putExtra(KEY_SHOW_LOCKED, false);
                intent.putExtra(KAY_OPTIONAL_PERM, new String[]{"android.permission-group.READ_MEDIA_AURAL"});
                intent.putExtra(KAY_OPTIONAL_PERM_DESC, new String[]{activity.getResources().getString(R.string.read_external_storage_permission_desc)});
                activity.startActivityForResult(intent, i2);
            } else {
                intent.setAction(ACTION_SYSTEM_PERMISSION_DECLARE);
                if (activity.getPackageManager().queryIntentActivities(intent, 0).size() <= 0) {
                    return false;
                }
                Log.i(TAG, "showPermissionDeclare in old version");
                intent.putExtra(KEY_MANDATORY_PERMISSION, false);
                intent.putExtra(KEY_MAIN_PURPOSE, DeskClockApp.getAppDEContext().getString(R.string.system_permission_main_purpose));
                intent.putExtra(KEY_USE_NETWORK, true);
                intent.putExtra(KEY_AGREE_DESC, DeskClockApp.getAppDEContext().getString(R.string.system_permission_agree_desc));
                intent.putExtra(KEY_SHOW_LOCKED, false);
                intent.putExtra(KAY_OPTIONAL_PERM, new String[]{"android.permission-group.READ_MEDIA_AURAL"});
                intent.putExtra(KAY_OPTIONAL_PERM_DESC, new String[]{activity.getResources().getString(R.string.read_external_storage_permission_desc)});
                activity.startActivityForResult(intent, i);
            }
            return true;
        } catch (Exception e) {
            com.android.deskclock.util.Log.e(TAG, "error: " + e.getMessage());
            return false;
        }
    }

    public static boolean showPermissionDeclare(Activity activity, ActivityResultLauncher<Intent> activityResultLauncher) {
        if (activity == null) {
            return false;
        }
        try {
            if (!MiuiSecurity.supportSystemPermissionDeclare()) {
                return false;
            }
            Intent intent = new Intent();
            intent.setAction(ACTION_SYSTEM_PERMISSION_DECLARE_NEW);
            intent.setPackage(SECURITY_CENTER_PACKAGE_NAME);
            if (DeskClockApp.getAppContext().getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                intent.putExtra(KEY_MANDATORY_PERMISSION, false);
                intent.putExtra(KEY_MAIN_PURPOSE, DeskClockApp.getAppDEContext().getString(R.string.system_permission_main_purpose));
                intent.putExtra(KEY_USE_NETWORK, true);
                intent.putExtra(KEY_AGREE_DESC, DeskClockApp.getAppDEContext().getString(R.string.system_permission_agree_desc));
                intent.putExtra(KEY_SHOW_LOCKED, false);
                intent.putExtra(KAY_OPTIONAL_PERM, new String[]{"android.permission-group.READ_MEDIA_AURAL"});
                intent.putExtra(KAY_OPTIONAL_PERM_DESC, new String[]{activity.getResources().getString(R.string.read_external_storage_permission_desc)});
                activityResultLauncher.launch(intent);
            } else {
                intent.setAction(ACTION_SYSTEM_PERMISSION_DECLARE);
                if (activity.getPackageManager().queryIntentActivities(intent, 0).size() <= 0) {
                    return false;
                }
                intent.putExtra(KEY_MANDATORY_PERMISSION, false);
                intent.putExtra(KEY_MAIN_PURPOSE, DeskClockApp.getAppDEContext().getString(R.string.system_permission_main_purpose));
                intent.putExtra(KEY_USE_NETWORK, true);
                intent.putExtra(KEY_AGREE_DESC, DeskClockApp.getAppDEContext().getString(R.string.system_permission_agree_desc));
                intent.putExtra(KEY_SHOW_LOCKED, false);
                intent.putExtra(KAY_OPTIONAL_PERM, new String[]{"android.permission-group.READ_MEDIA_AURAL"});
                intent.putExtra(KAY_OPTIONAL_PERM_DESC, new String[]{activity.getResources().getString(R.string.read_external_storage_permission_desc)});
                activityResultLauncher.launch(intent);
            }
            return true;
        } catch (Exception e) {
            com.android.deskclock.util.Log.e(TAG, "error: " + e.getMessage());
            return false;
        }
    }
}
