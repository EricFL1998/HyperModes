package com.android.deskclock.settings;

import android.R;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.deskclock.base.BaseActivity;
import com.android.deskclock.util.UiUtil;
import com.android.deskclock.util.Util;
import miuix.appcompat.app.ActionBar;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes.dex */
public class SettingsActivity extends BaseActivity {
    public static final String KEY_AUTO_SILENCE = "auto_silence";
    public static final String KEY_IS_SET_BACKGROUND_EVER = "is_set_bg_ever";

    @Override // miuix.appcompat.app.AppCompatActivity
    protected boolean isResponsiveEnabled() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Util.isTinyScreen(this)) {
            finish();
        }
        initActionBar();
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag(SettingsFragment.TAG) == null) {
            FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
            fragmentTransactionBeginTransaction.add(R.id.content, new SettingsFragment(), SettingsFragment.TAG);
            fragmentTransactionBeginTransaction.commit();
        }
        Util.cutOut(this);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override // com.android.deskclock.base.BaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        Fragment fragmentFindFragmentByTag = getSupportFragmentManager().findFragmentByTag(SettingsFragment.TAG);
        if (fragmentFindFragmentByTag instanceof SettingsFragment) {
            ((SettingsFragment) fragmentFindFragmentByTag).handleActivityResult(i, i2, intent);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        Fragment fragmentFindFragmentByTag = getSupportFragmentManager().findFragmentByTag(SettingsFragment.TAG);
        if (fragmentFindFragmentByTag instanceof SettingsFragment) {
            ((SettingsFragment) fragmentFindFragmentByTag).handlePermissionsResult(i, strArr, iArr);
        }
    }

    private void initActionBar() {
        ActionBar appCompatActionBar = getAppCompatActionBar();
        if (appCompatActionBar != null) {
            appCompatActionBar.setTitle(com.android.deskclock.R.string.more_settings);
            appCompatActionBar.setDisplayHomeAsUpEnabled(false);
            ImageView floatPageBackIcon = UiUtil.getFloatPageBackIcon(this);
            floatPageBackIcon.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.settings.SettingsActivity$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m92x449ca012(view);
                }
            });
            floatPageBackIcon.setContentDescription(getResources().getString(com.android.deskclock.R.string.go_back));
            appCompatActionBar.setStartView(floatPageBackIcon);
        }
    }

    /* JADX INFO: renamed from: lambda$initActionBar$0$com-android-deskclock-settings-SettingsActivity, reason: not valid java name */
    /* synthetic */ void m92x449ca012(View view) {
        onBackPressed();
    }

    @Override // miuix.appcompat.app.AppCompatActivity, miuix.responsive.interfaces.IResponsive
    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        super.onResponsiveLayout(configuration, screenSpec, z);
        initActionBar();
    }
}
