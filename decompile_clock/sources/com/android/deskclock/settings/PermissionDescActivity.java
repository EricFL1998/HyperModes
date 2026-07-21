package com.android.deskclock.settings;

import android.R;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.deskclock.base.BaseActivity;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class PermissionDescActivity extends BaseActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Util.isTinyScreen(this)) {
            finish();
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag(PermissionDescFragment.TAG) == null) {
            FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
            fragmentTransactionBeginTransaction.add(R.id.content, new PermissionDescFragment(), PermissionDescFragment.TAG);
            fragmentTransactionBeginTransaction.commit();
        }
    }
}
