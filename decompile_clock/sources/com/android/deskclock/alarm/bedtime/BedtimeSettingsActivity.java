package com.android.deskclock.alarm.bedtime;

import android.R;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.deskclock.base.BaseActivity;
import com.android.deskclock.settings.BedtimeSettingsFragment;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class BedtimeSettingsActivity extends BaseActivity {
    public static final int REQUEST_NETWORK_CODE = 4;
    public static final int REQUEST_NETWORK_CODE_NEW = 5;
    private static final String TAG = "DC:BedtimeSettingsActivity";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Util.isTinyScreen(this)) {
            finish();
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag(BedtimeSettingsFragment.TAG) == null) {
            FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
            fragmentTransactionBeginTransaction.add(R.id.content, new BedtimeSettingsFragment(), BedtimeSettingsFragment.TAG);
            fragmentTransactionBeginTransaction.commit();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        Fragment fragmentFindFragmentByTag = getSupportFragmentManager().findFragmentByTag(BedtimeSettingsFragment.TAG);
        if (fragmentFindFragmentByTag instanceof BedtimeSettingsFragment) {
            ((BedtimeSettingsFragment) fragmentFindFragmentByTag).handleRequestPermissionsResult(i, strArr, iArr);
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        Fragment fragmentFindFragmentByTag = getSupportFragmentManager().findFragmentByTag(BedtimeSettingsFragment.TAG);
        if (fragmentFindFragmentByTag instanceof BedtimeSettingsFragment) {
            BedtimeSettingsFragment bedtimeSettingsFragment = (BedtimeSettingsFragment) fragmentFindFragmentByTag;
            if (bedtimeSettingsFragment.isBedtimeRepeatAlarmDialogShow()) {
                bedtimeSettingsFragment.handleBedtimeRepeatResult();
            }
        }
        super.onBackPressed();
    }
}
