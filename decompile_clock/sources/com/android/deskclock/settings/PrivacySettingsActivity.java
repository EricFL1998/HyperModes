package com.android.deskclock.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.deskclock.R;
import com.android.deskclock.base.BaseActivity;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.permission.UserNoticeUtil;

/* JADX INFO: loaded from: classes.dex */
public class PrivacySettingsActivity extends BaseActivity implements View.OnClickListener {
    private ImageView imageView;
    private TextView mTextView;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_privacy_settings);
        if (Util.isTinyScreen(this)) {
            finish();
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag(PrivacySettingsFragment.TAG) == null) {
            FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
            fragmentTransactionBeginTransaction.add(R.id.privacy_preference_container, new PrivacySettingsFragment(), PrivacySettingsFragment.TAG);
            fragmentTransactionBeginTransaction.commit();
        }
        if (Util.isInternational()) {
            return;
        }
        this.mTextView = (TextView) findViewById(R.id.clock_information);
        this.imageView = (ImageView) findViewById(R.id.clock_image);
        this.mTextView.setOnClickListener(this);
        this.imageView.setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R.id.clock_information || view.getId() == R.id.clock_image) {
            UserNoticeUtil.openFilingPolicy(this);
        }
    }
}
