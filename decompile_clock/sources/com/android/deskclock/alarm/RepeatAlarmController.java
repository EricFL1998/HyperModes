package com.android.deskclock.alarm;

import android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmController;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmGroup;
import com.android.deskclock.util.ClickUtils;
import com.android.deskclock.util.DialogUtil;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.permission.SystemPermissionUtil;
import com.android.deskclock.util.permission.UserNoticeUtil;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.view.SimpleDialogFragment;
import com.android.deskclock.view.list.AlarmRecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import miuix.animation.Folme;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;
import miuix.bottomsheet.BottomSheetBehavior;
import miuix.bottomsheet.BottomSheetModal;
import miuix.recyclerview.card.CardItemDecoration;

/* JADX INFO: loaded from: classes.dex */
public class RepeatAlarmController extends AlarmController {
    private static final int DEFINE_GROUP_ID = 1;
    public static final int EVERY_DAY_TYPE = 1;
    public static final String IS_DIALOG_SHOW = "is_dialog_show";
    public static final String IS_RESET_DAYS = "is_reset_days";
    public static final String IS_SHOW_SELF_DEFINE_DIALOG = "is_show_self_define_dialog";
    public static final int LEGAL_OFF_DAY_TYPE = 3;
    public static final int LEGAL_WORKDAY_TYPE = 2;
    public static final int MON_TO_FRI_TYPE = 4;
    private static final int NORMAL_GROUP_ID = 0;
    public static final int ONLY_ONCE_TYPE = 0;
    public static final int SELF_DEFINE_TYPE = 7;
    public static final int SHIFT_WORK_TYPE = 6;
    private static final String TAG = "DC:RepeatAlarmController";
    public static boolean isPagerReconstruction = false;
    public static boolean isRepeatPagerReconstruction = false;
    public static Alarm.DaysOfWeek mDaysOfWeek = new Alarm.DaysOfWeek(0);
    public static Alarm.DaysOfWeek mNewDaysOfWeek = new Alarm.DaysOfWeek(0);
    public static Alarm.DaysOfWeek mNewDaysOfWeekSwitchScreen = new Alarm.DaysOfWeek(0);
    private boolean isWakeAlarmRepeat;
    private AppCompatActivity mActivity;
    private AlarmRepeatAdapter mAlarmRepeatAdapter;
    private AlarmRecyclerView mAlarmRepeatLv;
    private RepeatGroup mGroups;
    private RepeatGroup mInternationalGroups;
    private SimpleDialogFragment mNetPermissionDialog;
    private onShiftAlarmSelectedListener mOnShiftAlarmSelectedListener;
    private boolean mResetDays;
    private View mRootView;
    public BottomSheetModal mSelfDefBottomSheetModal;
    private View mSelfDefView;
    public SelfDefineController mSelfDefineController;
    private BottomSheetModal mShifAlarmBottomSheetModal;
    private ShiftAlarmController mShiftAlarmController;
    private ShiftAlarmGroup mShiftAlarmGroup;
    private int mTempType;
    private RepeatGroup mWakeAlarmGroups;
    private RepeatGroup mWakeAlarmInternationGroups;
    private AlertDialog mWeekdayDialog;

    public interface onShiftAlarmSelectedListener {
        void onBack(ShiftAlarmGroup shiftAlarmGroup);
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void initOtherData() {
    }

    public RepeatAlarmController(AppCompatActivity appCompatActivity, View view, boolean z) {
        super(appCompatActivity, view);
        this.mResetDays = false;
        this.isWakeAlarmRepeat = false;
        this.mNetPermissionDialog = null;
        if (!z) {
            this.mGroups = new RepeatGroup(new int[]{0, 0, 0, 0, 0, 1}, new int[]{0, 1, 2, 3, 4, 7});
            this.mInternationalGroups = new RepeatGroup(new int[]{0, 0, 0, 1}, new int[]{0, 1, 4, 7});
            this.mWakeAlarmGroups = new RepeatGroup(new int[]{0, 0, 0, 0, 1}, new int[]{1, 2, 3, 4, 7});
            this.mWakeAlarmInternationGroups = new RepeatGroup(new int[]{0, 0, 1}, new int[]{1, 4, 7});
        } else {
            this.mGroups = new RepeatGroup(new int[]{0, 0, 0, 0, 0, 1, 1}, new int[]{0, 1, 2, 3, 4, 6, 7});
            this.mInternationalGroups = new RepeatGroup(new int[]{0, 0, 0, 1, 1}, new int[]{0, 1, 4, 6, 7});
            this.mWakeAlarmGroups = new RepeatGroup(new int[]{0, 0, 0, 0, 1, 1}, new int[]{1, 2, 3, 4, 6, 7});
            this.mWakeAlarmInternationGroups = new RepeatGroup(new int[]{0, 0, 1, 1}, new int[]{1, 4, 6, 7});
        }
        this.mActivity = appCompatActivity;
        this.mRootView = view;
        this.mAlarmRepeatLv = (AlarmRecyclerView) view.findViewById(R.id.list);
        this.mAlarmRepeatAdapter = new AlarmRepeatAdapter(this.mActivity);
        this.mAlarmRepeatLv.setLayoutManager(new LinearLayoutManager(this.mActivity));
        this.mAlarmRepeatLv.setAdapter(this.mAlarmRepeatAdapter);
        this.mAlarmRepeatLv.addItemDecoration(new CardItemDecoration(this.mActivity, null));
        this.mAlarmRepeatAdapter.setRepeatItemChecked(mDaysOfWeek.getAlarmType());
    }

    public ShiftAlarmGroup getShiftAlarmGroup() {
        return this.mShiftAlarmGroup;
    }

    private void initRepeatAlarmAdapterData() {
        ArrayList<DataBean> arrayList = new ArrayList<>();
        if (this.isWakeAlarmRepeat) {
            if (Util.isInternational()) {
                addDataBeanToList(arrayList, this.mWakeAlarmInternationGroups);
            } else {
                addDataBeanToList(arrayList, this.mWakeAlarmGroups);
            }
        } else if (Util.isInternational()) {
            addDataBeanToList(arrayList, this.mInternationalGroups);
        } else {
            addDataBeanToList(arrayList, this.mGroups);
        }
        this.mAlarmRepeatAdapter.setData(arrayList, this.isWakeAlarmRepeat);
    }

    private void addDataBeanToList(ArrayList<DataBean> arrayList, RepeatGroup repeatGroup) {
        if (repeatGroup == null) {
            return;
        }
        int groupItemSize = repeatGroup.getGroupItemSize();
        int[] groupItemIds = repeatGroup.getGroupItemIds();
        int[] groupItemTypes = repeatGroup.getGroupItemTypes();
        if (groupItemSize <= 0 || groupItemIds == null || groupItemTypes == null || groupItemSize != groupItemTypes.length || groupItemSize != groupItemIds.length) {
            return;
        }
        for (int i = 0; i < groupItemSize; i++) {
            DataBean dataBean = new DataBean();
            dataBean.groupId = groupItemIds[i];
            dataBean.repeatType = groupItemTypes[i];
            arrayList.add(dataBean);
        }
    }

    public void initShiftAlarm(ShiftAlarmGroup shiftAlarmGroup) {
        this.mShiftAlarmGroup = shiftAlarmGroup;
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void initBundleData(Bundle bundle) {
        if (bundle != null) {
            boolean z = bundle.getBoolean(IS_SHOW_SELF_DEFINE_DIALOG);
            boolean z2 = bundle.getBoolean(IS_RESET_DAYS);
            if (z) {
                isPagerReconstruction = true;
                if (z2) {
                    mNewDaysOfWeek.setDay(0);
                }
            }
        }
        if (isRepeatPagerReconstruction && this.mSelfDefineController == null && this.mSelfDefBottomSheetModal == null) {
            isRepeatPagerReconstruction = false;
            this.mAlarmRepeatAdapter.setRepeatItemChecked(mDaysOfWeek.getAlarmType());
        }
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void initData(Alarm alarm, Alarm alarm2, boolean z, boolean z2) {
        this.isWakeAlarmRepeat = z2;
        initRepeatAlarmAdapterData();
        initView();
        initActionBar();
    }

    private void initActionBar() {
        View viewFindViewById = this.mRootView.findViewById(com.android.deskclock.R.id.repeat_alarm_actionbar);
        ((TextView) this.mRootView.findViewById(com.android.deskclock.R.id.repeat_title)).setText(com.android.deskclock.R.string.alarm_repeat);
        ImageView imageView = (ImageView) viewFindViewById.findViewById(16908313);
        if (!Util.isNightMode(this.mActivity)) {
            imageView.setImageDrawable(ContextCompat.getDrawable(this.mActivity, com.android.deskclock.R.drawable.miuix_action_icon_back_light));
        } else {
            imageView.setImageDrawable(ContextCompat.getDrawable(this.mActivity, com.android.deskclock.R.drawable.miuix_action_icon_back_dark));
        }
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.RepeatAlarmController.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (RepeatAlarmController.this.mOnShiftAlarmSelectedListener != null) {
                    RepeatAlarmController.this.mOnShiftAlarmSelectedListener.onBack(RepeatAlarmController.this.mShiftAlarmGroup);
                }
                RepeatAlarmController.this.dismissDialog();
                DialogUtil.dismissDialogFragment(RepeatAlarmController.this.mNetPermissionDialog);
                RepeatAlarmController.this.mNetPermissionDialog = null;
            }
        });
    }

    public void handleRepeatAlarmNewWork(ActivityResult activityResult) {
        int resultCode = activityResult.getResultCode();
        Log.i(TAG, "toNetWorkLauncher resultCode: " + resultCode);
        if (activityResult.getResultCode() == -3) {
            showNetPermissionDialog(this.mTempType);
        }
        if (resultCode != 1) {
            if (resultCode == 0) {
                UserNoticeUtil.setAcceptNetPermission(false);
                UserNoticeUtil.setRemindNetPermission(false);
                this.mAlarmRepeatAdapter.setRepeatItemChecked(mDaysOfWeek.getAlarmType());
                return;
            } else {
                if (resultCode == 666) {
                    UserNoticeUtil.setAcceptNetPermission(false);
                    UserNoticeUtil.setRemindNetPermission(false);
                    this.mAlarmRepeatAdapter.setRepeatItemChecked(mDaysOfWeek.getAlarmType());
                    return;
                }
                Log.e(SystemPermissionUtil.TAG, "lack of important information");
                return;
            }
        }
        UserNoticeUtil.setAcceptNetPermission(true);
        StatHelper.init(DeskClockApp.getAppContext());
        OneTrackStatHelper.init(DeskClockApp.getAppContext());
        int i = this.mTempType;
        if (i == 2) {
            mNewDaysOfWeek.setWorkDay(true);
            onDialogClosed(true);
        } else {
            if (i != 3) {
                return;
            }
            mNewDaysOfWeek.setOffDay(true);
            onDialogClosed(true);
        }
    }

    private void showNetPermissionDialog(final int i) {
        this.mTempType = i;
        if (SystemPermissionUtil.showPermissionDeclare(this.mActivity, AlarmClockFragment.toNetWorkLauncher)) {
            return;
        }
        this.mNetPermissionDialog = UserNoticeUtil.showUserNoticeDialog(this.mActivity, com.android.deskclock.R.string.dialog_net_permission_in_repeat_type, new UserNoticeUtil.OnNetPermissionListener() { // from class: com.android.deskclock.alarm.RepeatAlarmController.2
            @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
            public void onReject() {
            }

            @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
            public void onAccept() {
                int i2 = i;
                if (i2 == 2) {
                    RepeatAlarmController.mNewDaysOfWeek.setWorkDay(true);
                    RepeatAlarmController.this.onDialogClosed(true);
                } else {
                    if (i2 != 3) {
                        return;
                    }
                    RepeatAlarmController.mNewDaysOfWeek.setOffDay(true);
                    RepeatAlarmController.this.onDialogClosed(true);
                }
            }
        }, this.mActivity.getSupportFragmentManager());
    }

    private void initView() {
        this.mAlarmRepeatAdapter.setOnOtherViewItemClickListener(new AlarmRepeatAdapter.OnOtherViewItemClickListener() { // from class: com.android.deskclock.alarm.RepeatAlarmController.3
            @Override // com.android.deskclock.alarm.AlarmRepeatAdapter.OnOtherViewItemClickListener
            public void onOtherViewItemClick(ArrayList<DataBean> arrayList, int i) {
                if (arrayList == null || i >= arrayList.size()) {
                    return;
                }
                int i2 = arrayList.get(i).repeatType;
                if (i2 == 0) {
                    RepeatAlarmController.this.setOnlyOnceDayOfWeek();
                } else if (i2 == 1) {
                    RepeatAlarmController.this.setEveryDayOfWeek();
                } else if (i2 == 2) {
                    RepeatAlarmController.this.setLegalWorkDayOfWeek();
                } else if (i2 == 3) {
                    RepeatAlarmController.this.setLegalOffDayOfWeek();
                } else if (i2 == 4) {
                    RepeatAlarmController.this.setMonToFriDayOfWeek();
                }
                RepeatAlarmController.this.mAlarmRepeatAdapter.setRepeatItemChecked(RepeatAlarmController.mDaysOfWeek.getAlarmType());
            }
        });
        this.mAlarmRepeatAdapter.setOnSelfDefViewItemClickListener(new AlarmRepeatAdapter.OnSelfDefViewItemClickListener() { // from class: com.android.deskclock.alarm.RepeatAlarmController.4
            @Override // com.android.deskclock.alarm.AlarmRepeatAdapter.OnSelfDefViewItemClickListener
            public void onSelfDefViewItemClick() {
                if (ClickUtils.isFastClick()) {
                    return;
                }
                RepeatAlarmController.this.showSelfDefDialog(RepeatAlarmController.mDaysOfWeek.getBooleanArray(), null);
                if (RepeatAlarmController.this.mResetDays) {
                    RepeatAlarmController.mNewDaysOfWeek.setDay(0);
                }
            }
        });
        this.mAlarmRepeatAdapter.setOnSelfDefButtonViewClickListener(new AlarmRepeatAdapter.OnSelfDefButtonViewClickListener() { // from class: com.android.deskclock.alarm.RepeatAlarmController.5
            @Override // com.android.deskclock.alarm.AlarmRepeatAdapter.OnSelfDefButtonViewClickListener
            public void onSelfDefButtonViewClick() {
                if (ClickUtils.isFastClick()) {
                    return;
                }
                RepeatAlarmController.this.showSelfDefDialog(RepeatAlarmController.mDaysOfWeek.getBooleanArray(), null);
                if (RepeatAlarmController.this.mResetDays) {
                    RepeatAlarmController.mNewDaysOfWeek.setDay(0);
                }
            }
        });
        this.mAlarmRepeatAdapter.setOnShiftAlarmItemClickListener(new AlarmRepeatAdapter.OnShiftAlarmItemClickListener() { // from class: com.android.deskclock.alarm.RepeatAlarmController.6
            @Override // com.android.deskclock.alarm.AlarmRepeatAdapter.OnShiftAlarmItemClickListener
            public void onShiftAlarmItemClick() {
                if (ClickUtils.isFastClick()) {
                    return;
                }
                RepeatAlarmController.this.showShiftAlarmDialog();
                RepeatAlarmController.this.setShiftDayOfWeek();
            }
        });
    }

    public void showShiftAlarmDialog() {
        this.mShifAlarmBottomSheetModal = new BottomSheetModal(this.mActivity, false);
        final View viewInflate = LayoutInflater.from(this.mActivity).inflate(com.android.deskclock.R.layout.shift_alarm_setting, (ViewGroup) null);
        this.mShifAlarmBottomSheetModal.setContentView(viewInflate);
        this.mShifAlarmBottomSheetModal.setDragHandleViewEnabled(true);
        this.mShifAlarmBottomSheetModal.setOnDismissListener(new BottomSheetModal.OnDismissListener() { // from class: com.android.deskclock.alarm.RepeatAlarmController.7
            @Override // miuix.bottomsheet.BottomSheetModal.OnDismissListener
            public void onDismiss() {
                if (RepeatAlarmController.this.mShiftAlarmGroup == null && RepeatAlarmController.this.mShiftAlarmController != null) {
                    RepeatAlarmController repeatAlarmController = RepeatAlarmController.this;
                    repeatAlarmController.mShiftAlarmGroup = repeatAlarmController.mShiftAlarmController.getShiftAlarmGroup();
                }
                View view = viewInflate;
                if (view != null) {
                    Folme.getTarget(view).clean();
                    Folme.clean(viewInflate);
                }
                if (RepeatAlarmController.this.mShifAlarmBottomSheetModal != null) {
                    Folme.getTarget((View) RepeatAlarmController.this.mShifAlarmBottomSheetModal.getBottomSheetView()).clean();
                    Folme.clean(RepeatAlarmController.this.mShifAlarmBottomSheetModal.getBottomSheetView());
                    RepeatAlarmController.this.mShifAlarmBottomSheetModal.release();
                    RepeatAlarmController.this.mShifAlarmBottomSheetModal = null;
                }
            }
        });
        ShiftAlarmController shiftAlarmController = new ShiftAlarmController(this.mActivity, viewInflate, this.mShiftAlarmGroup, true);
        this.mShiftAlarmController = shiftAlarmController;
        shiftAlarmController.setBackButtonClickListener(new ShiftAlarmController.BackButtonClickListener() { // from class: com.android.deskclock.alarm.RepeatAlarmController.8
            @Override // com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.BackButtonClickListener
            public void onButtonClick(ShiftAlarmGroup shiftAlarmGroup) {
                if (RepeatAlarmController.this.mShiftAlarmController == null || RepeatAlarmController.this.mShifAlarmBottomSheetModal == null) {
                    return;
                }
                RepeatAlarmController.this.mShiftAlarmGroup = shiftAlarmGroup;
                RepeatAlarmController.this.mShifAlarmBottomSheetModal.dismiss();
            }
        });
        BottomSheetBehavior<FrameLayout> behavior = this.mShifAlarmBottomSheetModal.getBehavior();
        behavior.setOnModeChangeListener(new BottomSheetBehavior.OnModeChangeListener() { // from class: com.android.deskclock.alarm.RepeatAlarmController.9
            @Override // miuix.bottomsheet.BottomSheetBehavior.OnModeChangeListener
            public void onModeChange(int i, View view) {
                android.util.Log.d(RepeatAlarmController.TAG, " shiftDefBottomSheetBehavior onModeChange: " + i);
                if (RepeatAlarmController.this.mShiftAlarmController != null) {
                    RepeatAlarmController.this.mShiftAlarmController.setViewLayout(i == 1);
                }
            }
        });
        behavior.setDraggable(true);
        behavior.setSkipHalfExpanded(true);
        behavior.setSkipCollapsed(true);
        behavior.setForceFullHeight(true);
        behavior.setState(3);
        this.mShifAlarmBottomSheetModal.show();
    }

    public void showSelfDefDialog(boolean[] zArr, Bundle bundle) {
        this.mSelfDefBottomSheetModal = new BottomSheetModal(this.mActivity);
        View viewInflate = LayoutInflater.from(this.mActivity).inflate(com.android.deskclock.R.layout.self_define_dialog, (ViewGroup) null);
        this.mSelfDefView = viewInflate;
        this.mSelfDefBottomSheetModal.setContentView(viewInflate);
        this.mSelfDefBottomSheetModal.setDragHandleViewEnabled(true);
        SelfDefineController selfDefineController = new SelfDefineController(this.mActivity, this.mSelfDefView);
        this.mSelfDefineController = selfDefineController;
        selfDefineController.setCheckedItems(zArr);
        this.mSelfDefineController.initData(null, null, false, false);
        this.mSelfDefineController.initBundleData(bundle);
        this.mSelfDefineController.setBackButtonClickListener(new AlarmController.BackButtonClickListener() { // from class: com.android.deskclock.alarm.RepeatAlarmController.10
            @Override // com.android.deskclock.alarm.AlarmController.BackButtonClickListener
            public void onButtonClick() {
                RepeatAlarmController.this.dismissSelfDefineDialog();
            }
        });
        this.mSelfDefBottomSheetModal.setOnDismissListener(new BottomSheetModal.OnDismissListener() { // from class: com.android.deskclock.alarm.RepeatAlarmController.11
            @Override // miuix.bottomsheet.BottomSheetModal.OnDismissListener
            public void onDismiss() {
                if (RepeatAlarmController.this.mSelfDefBottomSheetModal != null) {
                    Folme.getTarget((View) RepeatAlarmController.this.mSelfDefBottomSheetModal.getBottomSheetView()).clean();
                    Folme.clean(RepeatAlarmController.this.mSelfDefBottomSheetModal.getBottomSheetView());
                    if (RepeatAlarmController.this.mSelfDefView != null) {
                        Folme.getTarget(RepeatAlarmController.this.mSelfDefView).clean();
                        Folme.clean(RepeatAlarmController.this.mSelfDefView);
                    }
                    RepeatAlarmController.this.mSelfDefBottomSheetModal.release();
                    RepeatAlarmController.this.mSelfDefBottomSheetModal = null;
                }
            }
        });
        BottomSheetBehavior<FrameLayout> behavior = this.mSelfDefBottomSheetModal.getBehavior();
        behavior.setOnModeChangeListener(new BottomSheetBehavior.OnModeChangeListener() { // from class: com.android.deskclock.alarm.RepeatAlarmController.12
            @Override // miuix.bottomsheet.BottomSheetBehavior.OnModeChangeListener
            public void onModeChange(int i, View view) {
                if (i == 1) {
                    RepeatAlarmController.this.mSelfDefineController.setViewLayout(true);
                } else {
                    RepeatAlarmController.this.mSelfDefineController.setViewLayout(false);
                }
            }
        });
        behavior.setDraggable(true);
        behavior.setSkipHalfExpanded(true);
        behavior.setSkipCollapsed(true);
        behavior.setForceFullHeight(true);
        behavior.setState(3);
        this.mSelfDefBottomSheetModal.show();
        int alarmType = mDaysOfWeek.getAlarmType();
        if (alarmType == 2 || alarmType == 3 || alarmType == 6) {
            this.mResetDays = true;
        } else {
            this.mResetDays = false;
        }
    }

    public void resetSelfDefineDialog() {
        BottomSheetModal bottomSheetModal;
        if (this.mSelfDefineController == null || (bottomSheetModal = this.mSelfDefBottomSheetModal) == null) {
            return;
        }
        bottomSheetModal.dismiss();
        this.mSelfDefineController = null;
    }

    public void dismissSelfDefineDialog() {
        Log.d(TAG, "mSelfDefineController: " + this.mSelfDefineController);
        Log.d(TAG, "mSelfDefBottomSheetModal: " + this.mSelfDefBottomSheetModal);
        if (this.mSelfDefineController == null || this.mSelfDefBottomSheetModal == null) {
            return;
        }
        handleSelfDefineResult();
        this.mSelfDefBottomSheetModal.dismiss();
    }

    public void handleSelfDefineResult() {
        int alarmType = mDaysOfWeek.getAlarmType();
        if (alarmType == 2 || alarmType == 3 || alarmType == 6) {
            this.mResetDays = true;
        } else {
            this.mResetDays = false;
        }
        onDialogClosed(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLegalOffDayOfWeek() {
        if (UserNoticeUtil.isNetPermissionAgreed()) {
            mNewDaysOfWeek.setOffDay(true);
            mDaysOfWeek.set(mNewDaysOfWeek);
        } else {
            showNetPermissionDialog(3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLegalWorkDayOfWeek() {
        if (UserNoticeUtil.isNetPermissionAgreed()) {
            mNewDaysOfWeek.setWorkDay(true);
            mDaysOfWeek.set(mNewDaysOfWeek);
        } else {
            showNetPermissionDialog(2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setOnlyOnceDayOfWeek() {
        mNewDaysOfWeek.set(new Alarm.DaysOfWeek(0));
        mDaysOfWeek.set(mNewDaysOfWeek);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setShiftDayOfWeek() {
        mNewDaysOfWeek.set(new Alarm.DaysOfWeek(512));
        mDaysOfWeek.set(mNewDaysOfWeek);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMonToFriDayOfWeek() {
        mNewDaysOfWeek.set(new Alarm.DaysOfWeek(31));
        mDaysOfWeek.set(mNewDaysOfWeek);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setEveryDayOfWeek() {
        mNewDaysOfWeek.set(new Alarm.DaysOfWeek(127));
        mDaysOfWeek.set(mNewDaysOfWeek);
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void onSaveInstance(Bundle bundle) {
        super.onSaveInstance(bundle);
        isRepeatPagerReconstruction = true;
        if (this.mSelfDefBottomSheetModal != null) {
            bundle.putBoolean(IS_SHOW_SELF_DEFINE_DIALOG, true);
            bundle.putBoolean(IS_RESET_DAYS, this.mResetDays);
            mNewDaysOfWeekSwitchScreen.set(mNewDaysOfWeek);
            SelfDefineController selfDefineController = this.mSelfDefineController;
            if (selfDefineController != null) {
                selfDefineController.onSaveInstance(bundle);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDialogClosed(boolean z) {
        if (z) {
            if (this.mResetDays && this.mSelfDefineController.getCheckedSelfDefineItems().length == 0 && !isPagerReconstruction) {
                mNewDaysOfWeek.set(mDaysOfWeek);
            } else {
                if (isPagerReconstruction) {
                    if (this.isWakeAlarmRepeat && !mNewDaysOfWeekSwitchScreen.isRepeatSet()) {
                        mNewDaysOfWeekSwitchScreen.set(new Alarm.DaysOfWeek(127));
                    }
                    mNewDaysOfWeek.set(mNewDaysOfWeekSwitchScreen);
                } else if (this.isWakeAlarmRepeat && !mNewDaysOfWeek.isRepeatSet()) {
                    mNewDaysOfWeek.set(new Alarm.DaysOfWeek(127));
                }
                mDaysOfWeek.set(mNewDaysOfWeek);
            }
        }
        isPagerReconstruction = false;
        this.mAlarmRepeatAdapter.setRepeatItemChecked(mDaysOfWeek.getAlarmType());
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void onPause() {
        super.onPause();
    }

    public void setResultOk() {
        FBEUtil.getDefaultSharedPreferences(this.mActivity).edit().apply();
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void onDestroy() {
        super.onDestroy();
        DialogUtil.dismissDialogFragment(this.mNetPermissionDialog);
        this.mNetPermissionDialog = null;
    }

    public static void setRepeatDaysOfWeek(Alarm.DaysOfWeek daysOfWeek) {
        mDaysOfWeek.set(daysOfWeek);
        mNewDaysOfWeek.set(daysOfWeek);
    }

    public static int getDays() {
        return mDaysOfWeek.get();
    }

    public boolean isSelfDefineDialogShow() {
        return (this.mSelfDefineController == null || this.mSelfDefBottomSheetModal == null) ? false : true;
    }

    public void setLastCheckedItem(String str) {
        Log.d(TAG, "setLastCheckedItem: " + str);
        if (str == null) {
            if (this.isWakeAlarmRepeat) {
                str = this.mActivity.getResources().getString(com.android.deskclock.R.string.every_day);
            } else {
                str = this.mActivity.getResources().getString(com.android.deskclock.R.string.never);
            }
        }
        if (str.equals(this.mActivity.getResources().getString(com.android.deskclock.R.string.never))) {
            this.mAlarmRepeatAdapter.setRepeatItemChecked(0);
            setOnlyOnceDayOfWeek();
            return;
        }
        if (str.equals(this.mActivity.getResources().getString(com.android.deskclock.R.string.every_day))) {
            this.mAlarmRepeatAdapter.setRepeatItemChecked(1);
            setEveryDayOfWeek();
            return;
        }
        if (str.equals(this.mActivity.getResources().getString(com.android.deskclock.R.string.legal_workday)) || str.equals(this.mActivity.getResources().getString(com.android.deskclock.R.string.legal_workday_invalidate))) {
            this.mAlarmRepeatAdapter.setRepeatItemChecked(2);
            setLegalWorkDayOfWeek();
            return;
        }
        if (str.equals(this.mActivity.getResources().getString(com.android.deskclock.R.string.legal_off_day))) {
            this.mAlarmRepeatAdapter.setRepeatItemChecked(3);
            setLegalOffDayOfWeek();
        } else if (str.equals(this.mActivity.getResources().getString(com.android.deskclock.R.string.monday_to_friday))) {
            this.mAlarmRepeatAdapter.setRepeatItemChecked(4);
            setMonToFriDayOfWeek();
        } else if (str.equals(this.mActivity.getResources().getString(com.android.deskclock.R.string.shift_alarm))) {
            this.mAlarmRepeatAdapter.setRepeatItemChecked(6);
            setShiftDayOfWeek();
        } else {
            this.mAlarmRepeatAdapter.setRepeatItemChecked(5);
        }
    }

    public void setViewLayout(boolean z) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mAlarmRepeatLv.getLayoutParams();
        if (z) {
            layoutParams.setMarginStart((int) this.mActivity.getResources().getDimension(com.android.deskclock.R.dimen.repeat_alarm_floating_margin_start));
            layoutParams.setMarginEnd((int) this.mActivity.getResources().getDimension(com.android.deskclock.R.dimen.repeat_alarm_floating_margin_start));
        } else {
            layoutParams.setMarginStart(0);
            layoutParams.setMarginEnd(0);
        }
        this.mAlarmRepeatLv.setLayoutParams(layoutParams);
        ShiftAlarmController shiftAlarmController = this.mShiftAlarmController;
        if (shiftAlarmController != null) {
            shiftAlarmController.setViewLayout(z);
        }
    }

    public boolean isShiftAlarmRvVisible() {
        ShiftAlarmController shiftAlarmController = this.mShiftAlarmController;
        if (shiftAlarmController != null) {
            return shiftAlarmController.isRecyclerViewVisible();
        }
        return false;
    }

    public void setOnShiftAlarmSelectedListener(onShiftAlarmSelectedListener onshiftalarmselectedlistener) {
        this.mOnShiftAlarmSelectedListener = onshiftalarmselectedlistener;
    }

    private static class RepeatGroup {
        private final int[] mGroupItemIds;
        private final int[] mGroupItemTypes;

        public RepeatGroup(int[] iArr, int[] iArr2) {
            this.mGroupItemIds = iArr;
            this.mGroupItemTypes = iArr2;
        }

        public int[] getGroupItemIds() {
            if (!checkCorrectness()) {
                return null;
            }
            int[] iArr = this.mGroupItemIds;
            return Arrays.copyOf(iArr, iArr.length);
        }

        public int[] getGroupItemTypes() {
            if (!checkCorrectness()) {
                return null;
            }
            int[] iArr = this.mGroupItemTypes;
            return Arrays.copyOf(iArr, iArr.length);
        }

        public int getGroupItemSize() {
            if (checkCorrectness()) {
                return this.mGroupItemTypes.length;
            }
            return -1;
        }

        private boolean checkCorrectness() {
            int[] iArr;
            int[] iArr2 = this.mGroupItemIds;
            if (iArr2 == null || (iArr = this.mGroupItemTypes) == null) {
                Log.d(RepeatAlarmController.TAG, "mGroupItemIds is " + this.mGroupItemIds + " mGroupItemTypes is " + this.mGroupItemTypes);
                return false;
            }
            if (iArr2.length == iArr.length) {
                return true;
            }
            Log.d(RepeatAlarmController.TAG, "arrays length is  not same !");
            return false;
        }
    }
}
