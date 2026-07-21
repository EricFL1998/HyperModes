package com.android.deskclock.util.permission;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import androidx.fragment.app.FragmentManager;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.alarm.lifepost.okhttp.Net;
import com.android.deskclock.util.DialogUtil;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.view.SimpleDialogFragment;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class UserNoticeUtil {
    public static final String KEY_REMIND_INTERNET_PERMISSION = "key_to_net_cta";
    public static final int RESULT_NOTIFICATION_PERMISSION_CODE_REFUSE = -1;
    public static final String TAG_DIALOG_USER_NOTICE = "user_notice";
    private static final String URL_MIUI_PRIVACY_POLICY = "https://privacy.mi.com/all/%1$s";
    private static final String URL_MIUI_USER_AGREEMENT = "https://www.miui.com/res/doc/eula.html?lang=%1$s";

    public interface OnNetPermissionListener {
        void onAccept();

        void onReject();
    }

    public static boolean isNetPermissionAgreed() {
        return PrefUtil.getNetPermission() == 1;
    }

    public static boolean isNetPermissionAgreed(Context context) {
        return PrefUtil.getNetPermission(context) == 1;
    }

    public static void setAcceptNetPermission(boolean z) {
        PrefUtil.setNetPermission(z);
    }

    public static String getPrivacyUrl() {
        return String.format(URL_MIUI_PRIVACY_POLICY, Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry());
    }

    public static String getAgreementUrl() {
        return String.format(URL_MIUI_USER_AGREEMENT, Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry());
    }

    public static void gotoPrivacyWebPage(Context context) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(getPrivacyUrl()));
            context.startActivity(intent);
        } catch (Exception e) {
            Log.d("gotoPrivacyWebPage Exception: " + e);
        }
    }

    public static void openFilingPolicy(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(new String(Base64.decode(Net.Api.FILING_CLOCK_INFORMATION_URI, 0))));
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.d("gotoPrivacyWebPage Exception: " + e);
        }
    }

    public static void gotoUserPolicyWebPage(Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(getAgreementUrl()));
        context.startActivity(intent);
    }

    public static Spanned getUserNoticeMessage(Context context, int i) {
        return Html.fromHtml(context.getString(i, 30, getAgreementUrl(), getPrivacyUrl()));
    }

    public static boolean canRemindNetPermission() {
        return FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).getBoolean(KEY_REMIND_INTERNET_PERMISSION, true);
    }

    public static void setRemindNetPermission(boolean z) {
        FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).edit().putBoolean(KEY_REMIND_INTERNET_PERMISSION, z).apply();
    }

    public static SimpleDialogFragment showUserNoticeDialog(Context context, int i, OnNetPermissionListener onNetPermissionListener, FragmentManager fragmentManager) {
        return showUserNoticeDialog(context, i, R.string.module_download_cancel, R.string.module_download_ok, onNetPermissionListener, null, null, fragmentManager);
    }

    public static SimpleDialogFragment showUserNoticeDialog(final Context context, int i, int i2, int i3, final OnNetPermissionListener onNetPermissionListener, String str, String str2, FragmentManager fragmentManager) {
        return DialogUtil.showAlertDialog("", getUserNoticeMessage(context, i), i2, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.util.permission.UserNoticeUtil.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i4) {
                UserNoticeUtil.setAcceptNetPermission(false);
                OnNetPermissionListener onNetPermissionListener2 = onNetPermissionListener;
                if (onNetPermissionListener2 != null) {
                    onNetPermissionListener2.onReject();
                }
            }
        }, i3, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.util.permission.UserNoticeUtil.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i4) {
                UserNoticeUtil.setAcceptNetPermission(true);
                StatHelper.init(context);
                OneTrackStatHelper.init(context);
                OnNetPermissionListener onNetPermissionListener2 = onNetPermissionListener;
                if (onNetPermissionListener2 != null) {
                    onNetPermissionListener2.onAccept();
                }
            }
        }, null, fragmentManager, false, str, str2, true, false);
    }

    public static SimpleDialogFragment showRecommendDialog(String str, int i, int i2, DialogInterface.OnClickListener onClickListener, FragmentManager fragmentManager) {
        return DialogUtil.showAlertDialog("", str, i, null, i2, onClickListener, null, fragmentManager, false, null, null, true, false);
    }

    public static void gotoNotificationSettingPage(Context context) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
            intent.putExtra("app_package", context.getPackageName());
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent2 = new Intent();
            intent2.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent2.setData(Uri.fromParts("package", context.getPackageName(), null));
            context.startActivity(intent2);
        }
    }

    public static boolean isCtaAgreed() {
        if (Util.isInternational()) {
            return true;
        }
        return isNetPermissionAgreed();
    }
}
