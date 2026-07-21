package com.android.deskclock.alarm.lifepost;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiMusic;
import com.android.deskclock.base.BaseActivity;
import com.android.deskclock.settings.LifePostSettingsFragment;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.UiUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.StatHelper;
import java.util.List;
import miuix.appcompat.app.ActionBar;
import miuix.recyclerview.widget.RecyclerView;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes.dex */
public class LifePostSettingActivity extends BaseActivity {
    public static final String KEY_OPEN_ALARM_LIFE_POST = "key_open_alarm_life_post";
    public static final String KEY_OPEN_ALARM_LIFE_POST_BACKGROUND = "key_open_life_post_background";
    public static final String KEY_OPEN_ALARM_LIFE_POST_NEWS = "key_open_alarm_life_post_news";
    private LifePostAdapter mAdapter;
    private DataModel mDataModel;
    private View mDividerLine;
    private RecyclerView mListView;
    private TextView mWakeUpListTitle;

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
        setContentView(R.layout.life_post_setting);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag(LifePostSettingsFragment.TAG) == null) {
            FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
            fragmentTransactionBeginTransaction.add(R.id.settings_life_post, new LifePostSettingsFragment(), LifePostSettingsFragment.TAG);
            fragmentTransactionBeginTransaction.commit();
        }
        initActionBar();
        initView();
        StatHelper.recordCountEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_LIFE_POST_ACTIVE_COUNT);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void initView() {
        this.mListView = (RecyclerView) findViewById(R.id.wake_up_list);
        this.mWakeUpListTitle = (TextView) findViewById(R.id.wake_up_list_title);
        this.mDividerLine = findViewById(R.id.divider_line);
        this.mAdapter = new LifePostAdapter(this);
        this.mListView.setLayoutManager(new LinearLayoutManager(this));
        this.mListView.setAdapter(this.mAdapter);
        DataModel dataModel = new DataModel(new DataModel.Observer() { // from class: com.android.deskclock.alarm.lifepost.LifePostSettingActivity.1
            @Override // com.android.deskclock.alarm.lifepost.DataModel.Observer
            public void onDataLoaded(List<DataModel.DataBean> list) {
                LifePostSettingActivity.this.mAdapter.setData(list);
                LifePostSettingActivity.this.mAdapter.notifyDataSetChanged();
                LifePostSettingActivity.this.mWakeUpListTitle.setVisibility(list.size() > 0 ? 0 : 8);
                LifePostSettingActivity.this.mDividerLine.setVisibility(list.size() <= 0 ? 8 : 0);
            }
        });
        this.mDataModel = dataModel;
        dataModel.startLoad();
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        finish();
    }

    public static void updateLifePostSwitchState(Context context, boolean z) {
        if (LifePostUtils.isLifePostEnabled() && Util.isChinese(context) && MiuiMusic.supportNews()) {
            FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).edit().putBoolean("key_open_alarm_life_post_news", z).apply();
            FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).edit().putBoolean("key_open_alarm_life_post", z).apply();
        }
    }

    @Override // com.android.deskclock.base.BaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        Fragment fragmentFindFragmentByTag = getSupportFragmentManager().findFragmentByTag(LifePostSettingsFragment.TAG);
        if (fragmentFindFragmentByTag instanceof LifePostSettingsFragment) {
            ((LifePostSettingsFragment) fragmentFindFragmentByTag).handleActivityResult(i, i2, intent);
        }
    }

    private void initActionBar() {
        ActionBar appCompatActionBar = getAppCompatActionBar();
        if (appCompatActionBar != null) {
            appCompatActionBar.setTitle(R.string.life_post_setting);
            appCompatActionBar.setDisplayHomeAsUpEnabled(false);
            appCompatActionBar.setExpandState(0);
            ImageView floatPageBackIcon = UiUtil.getFloatPageBackIcon(this);
            floatPageBackIcon.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.lifepost.LifePostSettingActivity$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m84x4fb4eb58(view);
                }
            });
            floatPageBackIcon.setContentDescription(getResources().getString(R.string.go_back));
            appCompatActionBar.setStartView(floatPageBackIcon);
        }
    }

    /* JADX INFO: renamed from: lambda$initActionBar$0$com-android-deskclock-alarm-lifepost-LifePostSettingActivity, reason: not valid java name */
    /* synthetic */ void m84x4fb4eb58(View view) {
        onBackPressed();
    }

    @Override // miuix.appcompat.app.AppCompatActivity, miuix.responsive.interfaces.IResponsive
    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        super.onResponsiveLayout(configuration, screenSpec, z);
        initActionBar();
    }
}
