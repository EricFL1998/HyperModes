package com.android.deskclock.alarm;

import android.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.android.deskclock.Alarm;
import com.android.deskclock.util.Util;
import com.android.deskclock.view.list.AlarmRecyclerView;
import java.util.ArrayList;
import miuix.recyclerview.card.CardItemDecoration;

/* JADX INFO: loaded from: classes.dex */
public class SelfDefineController extends AlarmController {
    private static final int[] SELF_DEFINE_GROUPS = {0, 0, 0, 0, 0, 0, 0};
    private Activity mActivity;
    private boolean[] mCheckedItems;
    private View mRootView;
    private SelfDefineAdapter mSelfDefineAdapter;
    private AlarmRecyclerView mSelfDefineLv;

    @Override // com.android.deskclock.alarm.AlarmController
    public void initBundleData(Bundle bundle) {
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void initOtherData() {
    }

    public SelfDefineController(Activity activity, View view) {
        super(activity, view);
        this.mActivity = activity;
        this.mRootView = view;
        this.mSelfDefineLv = (AlarmRecyclerView) view.findViewById(R.id.list);
        this.mSelfDefineAdapter = new SelfDefineAdapter(this.mActivity, this.mCheckedItems);
        this.mSelfDefineLv.setLayoutManager(new LinearLayoutManager(this.mActivity));
        this.mSelfDefineLv.setAdapter(this.mSelfDefineAdapter);
        this.mSelfDefineLv.addItemDecoration(new CardItemDecoration(this.mActivity, null));
        initSelfDefineAdapterData();
    }

    private void initSelfDefineAdapterData() {
        ArrayList<DataBean> arrayList = new ArrayList<>();
        for (int i : SELF_DEFINE_GROUPS) {
            DataBean dataBean = new DataBean();
            dataBean.groupId = i;
            arrayList.add(dataBean);
        }
        this.mSelfDefineAdapter.setData(arrayList);
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void initData(Alarm alarm, Alarm alarm2, boolean z, boolean z2) {
        this.mSelfDefineAdapter.setLastCheckedSelfDefineItem(this.mCheckedItems, true);
        initActionBar();
        this.mSelfDefineAdapter.setOnWeekDayItemClickListener(new SelfDefineAdapter.OnWeekDayItemClickListener() { // from class: com.android.deskclock.alarm.SelfDefineController.1
            @Override // com.android.deskclock.alarm.SelfDefineAdapter.OnWeekDayItemClickListener
            public void onWeekDayItemClick(int i, boolean z3) {
                boolean z4;
                if (z3) {
                    z4 = false;
                    SelfDefineController.this.mSelfDefineAdapter.setItemChecked(i, false);
                } else {
                    z4 = true;
                    SelfDefineController.this.mSelfDefineAdapter.setItemChecked(i, true);
                }
                if (RepeatAlarmController.isPagerReconstruction) {
                    RepeatAlarmController.mNewDaysOfWeekSwitchScreen.set(i, z4);
                    RepeatAlarmController.mNewDaysOfWeek.set(RepeatAlarmController.mNewDaysOfWeekSwitchScreen);
                } else {
                    RepeatAlarmController.mNewDaysOfWeek.set(i, z4);
                }
            }
        });
    }

    private void initActionBar() {
        View viewFindViewById = this.mRootView.findViewById(com.android.deskclock.R.id.self_define_actionbar);
        TextView textView = (TextView) this.mRootView.findViewById(com.android.deskclock.R.id.repeat_title);
        textView.setText(com.android.deskclock.R.string.alarm_repeat_self_define);
        if (Util.isTinyScreen(this.mActivity)) {
            textView.setWidth((int) this.mActivity.getResources().getDimension(com.android.deskclock.R.dimen.repeat_alarm_actionbar_text_width));
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setMaxLines(1);
            textView.setGravity(1);
        }
        ImageView imageView = (ImageView) viewFindViewById.findViewById(16908313);
        if (!Util.isNightMode(this.mActivity)) {
            imageView.setBackgroundResource(com.android.deskclock.R.drawable.miuix_action_icon_back_light);
        } else {
            imageView.setBackgroundResource(com.android.deskclock.R.drawable.miuix_action_icon_back_dark);
        }
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.SelfDefineController.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SelfDefineController.this.dismissDialog();
            }
        });
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void onSaveInstance(Bundle bundle) {
        super.onSaveInstance(bundle);
    }

    public void setCheckedItems(boolean[] zArr) {
        this.mCheckedItems = zArr;
    }

    public void setViewLayout(boolean z) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mSelfDefineLv.getLayoutParams();
        if (z) {
            layoutParams.setMarginStart((int) this.mActivity.getResources().getDimension(com.android.deskclock.R.dimen.repeat_alarm_floating_margin_start));
            layoutParams.setMarginEnd((int) this.mActivity.getResources().getDimension(com.android.deskclock.R.dimen.repeat_alarm_floating_margin_start));
        } else {
            layoutParams.setMarginStart(0);
            layoutParams.setMarginEnd(0);
        }
        this.mSelfDefineLv.setLayoutParams(layoutParams);
    }

    public int[] getCheckedSelfDefineItems() {
        return this.mSelfDefineAdapter.getCheckedItemIds();
    }
}
