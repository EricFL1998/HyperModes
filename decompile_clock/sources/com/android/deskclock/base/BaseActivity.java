package com.android.deskclock.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.alarm.lifepost.LifePostSettingActivity;
import com.android.deskclock.util.DialogUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.permission.SystemPermissionUtil;
import com.android.deskclock.util.permission.UserNoticeUtil;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.view.SimpleDialogFragment;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.LayoutUiModeHelper;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseActivity extends AppCompatActivity {
    public static final int REQUEST_CTA_CODE = 101;
    public static final int REQUEST_CTA_CODE_NEW = 102;
    private long mCtaTriggerTime;
    private SimpleDialogFragment mUserNoticeDialog;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LayoutUiModeHelper.autoSetLayoutUiMode(this);
        checkCtaStatement();
    }

    protected void checkCtaStatement() {
        if (Util.isInternational()) {
            return;
        }
        showCtaDialog();
    }

    private void showCtaDialog() {
        if (System.currentTimeMillis() - this.mCtaTriggerTime > 500) {
            this.mCtaTriggerTime = System.currentTimeMillis();
            if (UserNoticeUtil.isNetPermissionAgreed() || !UserNoticeUtil.canRemindNetPermission()) {
                return;
            }
            if (Util.isKddiCustomized() && Util.isInternational()) {
                UserNoticeUtil.setAcceptNetPermission(false);
                UserNoticeUtil.setRemindNetPermission(false);
            } else if (Util.isInternational() || !SystemPermissionUtil.showPermissionDeclare(this, 101, 102)) {
                this.mUserNoticeDialog = UserNoticeUtil.showUserNoticeDialog(this, Util.isInternational() ? R.string.network_privacy_global : R.string.network_privacy, R.string.net_permission_cancel, R.string.net_permission_ok, new UserNoticeUtil.OnNetPermissionListener() { // from class: com.android.deskclock.base.BaseActivity.1
                    @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
                    public void onAccept() {
                        BaseActivity.this.onNetPermissionAccept();
                    }

                    @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
                    public void onReject() {
                        BaseActivity.this.onNetPermissionNotAccept();
                    }
                }, getString(R.string.dialog_message_not_remind), UserNoticeUtil.KEY_REMIND_INTERNET_PERMISSION, getSupportFragmentManager());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNetPermissionAccept() {
        LifePostSettingActivity.updateLifePostSwitchState(this, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNetPermissionNotAccept() {
        LifePostSettingActivity.updateLifePostSwitchState(this, false);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 101) {
            if (i2 == 1) {
                UserNoticeUtil.setAcceptNetPermission(true);
                StatHelper.init(DeskClockApp.getAppContext());
                OneTrackStatHelper.init(DeskClockApp.getAppContext());
                onNetPermissionAccept();
                return;
            }
            if (i2 == 0) {
                UserNoticeUtil.setAcceptNetPermission(false);
                onNetPermissionNotAccept();
                UserNoticeUtil.setRemindNetPermission(false);
                return;
            }
            Log.e(SystemPermissionUtil.TAG, "lack of important information");
            return;
        }
        if (i != 102) {
            return;
        }
        if (i2 == 1) {
            UserNoticeUtil.setAcceptNetPermission(true);
            StatHelper.init(DeskClockApp.getAppContext());
            OneTrackStatHelper.init(DeskClockApp.getAppContext());
            onNetPermissionAccept();
            return;
        }
        if (i2 == 666) {
            UserNoticeUtil.setAcceptNetPermission(false);
            onNetPermissionNotAccept();
            UserNoticeUtil.setRemindNetPermission(false);
            return;
        }
        Log.e(SystemPermissionUtil.TAG, "lack of important information");
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        StatHelper.recordPageStart(this);
        LayoutUiModeHelper.autoSetLayoutUiMode(this);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        StatHelper.recordPageEnd(this);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
    }

    public void setNavigationForActionMode(boolean z) {
        if (Util.isInFullWindowGestureMode(this)) {
            if (!z) {
                getWindow().addFlags(134217728);
            } else {
                getWindow().clearFlags(134217728);
            }
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        DialogUtil.dismissDialogFragment(this.mUserNoticeDialog);
        this.mUserNoticeDialog = null;
    }

    protected void resetOrientation() {
        if (getResources().getBoolean(R.bool.large_mode)) {
            setRequestedOrientation(-1);
        } else {
            setRequestedOrientation(1);
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        LayoutUiModeHelper.autoSetLayoutUiMode(this);
    }
}
