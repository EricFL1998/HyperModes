package com.android.deskclock.alarm.shiftalarm;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.alarm.AlarmModel;
import com.android.deskclock.alarm.DurationPickerDialog;
import com.android.deskclock.alarm.ShiftAlarmTimePickerDialog;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.TimeUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.DatePickerDialog;
import miuix.pickerwidget.widget.DatePicker;
import miuix.pickerwidget.widget.NumberPicker;
import miuix.pickerwidget.widget.TimePicker;
import miuix.recyclerview.card.CardGroupAdapter;
import miuix.recyclerview.card.CardItemDecoration;
import miuix.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes.dex */
public class ShiftAlarmController {
    private static String TAG = "DC:ShiftAlarmController";
    private final ShiftAlarmAdapter mAdapter;
    private BackButtonClickListener mBackButtonClickListener;
    private final AppCompatActivity mContext;
    private ArrayList<DataBean> mDataList;
    private OnDateChangedListener mOnDateChangedListener;
    private OnMoreButtonClickListener mOnMoreButtonClickListener;
    private final RecyclerView mRecyclerView;
    private final View mRootView;
    private ShiftAlarmGroup mShiftAlarmGroup;
    private final boolean mShowSettings;

    public interface BackButtonClickListener {
        void onButtonClick(ShiftAlarmGroup shiftAlarmGroup);
    }

    public interface IGetDataInstance {
        boolean canClose();
    }

    public interface OnDateChangedListener {
        void onAlarmChanged(int i, int i2, boolean z, int i3, int i4);

        void onDurationChanged(int i, int i2);

        void onStartTimeChanged(long j, int i);
    }

    interface OnItemClickListener {
        void onItemClick(int i);
    }

    public interface OnMoreButtonClickListener {
        void onMoreButtonClick();
    }

    public ShiftAlarmController(AppCompatActivity appCompatActivity, View view, ShiftAlarmGroup shiftAlarmGroup, boolean z) {
        this.mContext = appCompatActivity;
        this.mRootView = view;
        this.mShowSettings = z;
        initData(shiftAlarmGroup, true);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        this.mRecyclerView = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(appCompatActivity));
        recyclerView.addItemDecoration(new CardItemDecoration(appCompatActivity, null));
        ShiftAlarmAdapter shiftAlarmAdapter = new ShiftAlarmAdapter(this.mDataList, new OnDateChangedListener() { // from class: com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.1
            @Override // com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.OnDateChangedListener
            public void onDurationChanged(int i, int i2) {
                Log.d(ShiftAlarmController.TAG, "onDurationChanged: " + i);
                ((ShiftTitleDataBean) ShiftAlarmController.this.mDataList.get(i2)).duration = i;
                ShiftAlarmController.this.mShiftAlarmGroup.resetDuration(i);
                ShiftAlarmController shiftAlarmController = ShiftAlarmController.this;
                shiftAlarmController.initData(shiftAlarmController.mShiftAlarmGroup, false);
                ShiftAlarmController.this.mAdapter.setCurrentIndex(ShiftAlarmAlertHelper.getCurrentIndex(ShiftAlarmController.this.mShiftAlarmGroup.startTime, ShiftAlarmController.this.mShiftAlarmGroup.duration));
                ShiftAlarmController.this.mAdapter.notifyDataSetChanged();
            }

            @Override // com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.OnDateChangedListener
            public void onStartTimeChanged(long j, int i) {
                Log.d(ShiftAlarmController.TAG, "onStartTimeChanged: " + j);
                ((ShiftTitleDataBean) ShiftAlarmController.this.mDataList.get(i)).startTime = j;
                ShiftAlarmController.this.mShiftAlarmGroup.startTime = j;
                ShiftAlarmController.this.mAdapter.setCurrentIndex(ShiftAlarmAlertHelper.getCurrentIndex(ShiftAlarmController.this.mShiftAlarmGroup.startTime, ShiftAlarmController.this.mShiftAlarmGroup.duration));
                ShiftAlarmController.this.mAdapter.notifyItemChanged(i);
                ShiftAlarmController.this.mAdapter.notifyDataSetChanged();
            }

            @Override // com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.OnDateChangedListener
            public void onAlarmChanged(int i, int i2, boolean z2, int i3, int i4) {
                Log.d(ShiftAlarmController.TAG, "onAlarmChanged, hour: " + i + " min:" + i2 + " enable:" + z2 + " index:" + i3);
                ShiftAlarmDataBean shiftAlarmDataBean = (ShiftAlarmDataBean) ShiftAlarmController.this.mDataList.get(i4);
                shiftAlarmDataBean.hour = i;
                shiftAlarmDataBean.minutes = i2;
                shiftAlarmDataBean.enable = z2;
                ShiftAlarmController.this.mAdapter.notifyItemChanged(i4);
                ShiftAlarmController.this.mShiftAlarmGroup.resetAlarm(i, i2, z2, i3);
                if (ShiftAlarmController.this.mOnDateChangedListener != null) {
                    ShiftAlarmController.this.mOnDateChangedListener.onAlarmChanged(i, i2, z2, i3, i4);
                }
            }
        }, new OnMoreButtonClickListener() { // from class: com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.2
            @Override // com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.OnMoreButtonClickListener
            public void onMoreButtonClick() {
                ShiftAlarmController.this.showAllData();
                if (ShiftAlarmController.this.mOnMoreButtonClickListener != null) {
                    ShiftAlarmController.this.mOnMoreButtonClickListener.onMoreButtonClick();
                }
            }
        }, appCompatActivity);
        this.mAdapter = shiftAlarmAdapter;
        shiftAlarmAdapter.setGetDataInstance(new IGetDataInstance() { // from class: com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.3
            @Override // com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.IGetDataInstance
            public boolean canClose() {
                Iterator<ShiftAlarm> it = ShiftAlarmController.this.mShiftAlarmGroup.shiftAlarms.iterator();
                int i = 0;
                while (it.hasNext() && (!it.next().enable || (i = i + 1) <= 1)) {
                }
                return i > 1;
            }
        });
        shiftAlarmAdapter.updateGroupInfo();
        shiftAlarmAdapter.setCurrentIndex(ShiftAlarmAlertHelper.getCurrentIndex(this.mShiftAlarmGroup.startTime, this.mShiftAlarmGroup.duration));
        recyclerView.setAdapter(shiftAlarmAdapter);
    }

    public void setOnMoreButtonClickListener(OnMoreButtonClickListener onMoreButtonClickListener) {
        this.mOnMoreButtonClickListener = onMoreButtonClickListener;
    }

    public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        this.mOnDateChangedListener = onDateChangedListener;
    }

    public ShiftAlarmGroup getShiftAlarmGroup() {
        return this.mShiftAlarmGroup;
    }

    public void setShiftAlarmsVisibility(int i) {
        this.mRecyclerView.setVisibility(i);
    }

    public boolean isRecyclerViewVisible() {
        RecyclerView recyclerView = this.mRecyclerView;
        return recyclerView != null && recyclerView.getVisibility() == 0;
    }

    public void showAllData() {
        initData(this.mShiftAlarmGroup, false);
        this.mAdapter.notifyDataSetChanged();
    }

    public void showLimitedData() {
        initData(this.mShiftAlarmGroup, true);
        this.mAdapter.notifyDataSetChanged();
    }

    public void initData(ShiftAlarmGroup shiftAlarmGroup, boolean z) {
        this.mShiftAlarmGroup = shiftAlarmGroup;
        if (shiftAlarmGroup == null) {
            this.mShiftAlarmGroup = ShiftAlarmGroup.getDefault();
        }
        ArrayList<DataBean> arrayList = this.mDataList;
        if (arrayList == null) {
            this.mDataList = new ArrayList<>();
        } else {
            arrayList.clear();
        }
        if (this.mShowSettings) {
            this.mDataList.add(new ShiftTitleDataBean(this.mContext.getString(R.string.shift_alarm_duration), 1, this.mShiftAlarmGroup.duration, this.mShiftAlarmGroup.startTime));
            this.mDataList.add(new ShiftTitleDataBean(this.mContext.getString(R.string.shift_alarm_start_time), 2, this.mShiftAlarmGroup.duration, this.mShiftAlarmGroup.startTime));
        }
        int size = (this.mShowSettings || !z) ? this.mShiftAlarmGroup.shiftAlarms.size() : Math.min(this.mShiftAlarmGroup.shiftAlarms.size(), 5);
        for (int i = 0; i < size; i++) {
            ShiftAlarm shiftAlarm = this.mShiftAlarmGroup.shiftAlarms.get(i);
            this.mDataList.add(new ShiftAlarmDataBean(shiftAlarm.index, shiftAlarm.hour, shiftAlarm.minutes, shiftAlarm.enable));
        }
        if (this.mShowSettings || !z || this.mShiftAlarmGroup.shiftAlarms.size() <= 5) {
            return;
        }
        this.mDataList.add(new MoreDataBean());
    }

    public void setData(ShiftAlarmGroup shiftAlarmGroup) {
        if (shiftAlarmGroup == null) {
            return;
        }
        initData(shiftAlarmGroup, true);
        ShiftAlarmAdapter shiftAlarmAdapter = this.mAdapter;
        if (shiftAlarmAdapter != null) {
            shiftAlarmAdapter.setData(this.mDataList);
            this.mAdapter.setCurrentIndex(ShiftAlarmAlertHelper.getCurrentIndex(this.mShiftAlarmGroup.startTime, this.mShiftAlarmGroup.duration));
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public void setBackButtonClickListener(BackButtonClickListener backButtonClickListener) {
        this.mBackButtonClickListener = backButtonClickListener;
    }

    public void setViewLayout(boolean z) {
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null || this.mContext == null) {
            return;
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
        if (z) {
            layoutParams.setMarginStart((int) this.mContext.getResources().getDimension(R.dimen.repeat_alarm_floating_margin_start));
            layoutParams.setMarginEnd((int) this.mContext.getResources().getDimension(R.dimen.repeat_alarm_floating_margin_start));
        } else {
            layoutParams.setMarginStart(0);
            layoutParams.setMarginEnd(0);
        }
        this.mRecyclerView.setLayoutParams(layoutParams);
    }

    static class ShiftAlarmAdapter extends CardGroupAdapter {
        private Context mContext;
        private int mCurrentIndex;
        private ArrayList<DataBean> mDataList;
        private IGetDataInstance mGetDataInstance;
        private OnItemClickListener mItemOnClickListener;
        private OnDateChangedListener mOnDateChangedListener;
        private OnMoreButtonClickListener mOnMoreButtonClickListener;

        @Override // miuix.recyclerview.card.CardGroupAdapter
        public void setHasStableIds() {
        }

        public void setCurrentIndex(int i) {
            this.mCurrentIndex = i;
        }

        public void setGetDataInstance(IGetDataInstance iGetDataInstance) {
            this.mGetDataInstance = iGetDataInstance;
        }

        public ShiftAlarmAdapter(final ArrayList<DataBean> arrayList, OnDateChangedListener onDateChangedListener, OnMoreButtonClickListener onMoreButtonClickListener, final Context context) {
            new ArrayList();
            this.mDataList = arrayList;
            this.mOnDateChangedListener = onDateChangedListener;
            this.mOnMoreButtonClickListener = onMoreButtonClickListener;
            this.mItemOnClickListener = new OnItemClickListener() { // from class: com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.ShiftAlarmAdapter.1
                @Override // com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.OnItemClickListener
                public void onItemClick(final int i) {
                    DataBean dataBean = (DataBean) arrayList.get(i);
                    if (dataBean.type == DataBean.TYPE_SETTINGS_TITLE && (dataBean instanceof ShiftTitleDataBean)) {
                        ShiftTitleDataBean shiftTitleDataBean = (ShiftTitleDataBean) dataBean;
                        if (shiftTitleDataBean.index == 1) {
                            DurationPickerDialog durationPickerDialog = new DurationPickerDialog(ShiftAlarmAdapter.this.mContext, new NumberPicker.OnValueChangeListener() { // from class: com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.ShiftAlarmAdapter.1.1
                                @Override // miuix.pickerwidget.widget.NumberPicker.OnValueChangeListener
                                public void onValueChange(NumberPicker numberPicker, int i2, int i3) {
                                    if (ShiftAlarmAdapter.this.mOnDateChangedListener != null) {
                                        ShiftAlarmAdapter.this.mOnDateChangedListener.onDurationChanged(i3, i);
                                    }
                                }
                            });
                            durationPickerDialog.setDurationValue(shiftTitleDataBean.duration);
                            durationPickerDialog.setHapticFeedbackEnabled(false);
                            durationPickerDialog.setTitle(R.string.shift_alarm_duration);
                            durationPickerDialog.show();
                            return;
                        }
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(shiftTitleDataBean.startTime);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(ShiftAlarmAdapter.this.mContext, new DatePickerDialog.OnDateSetListener() { // from class: com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.ShiftAlarmAdapter.1.2
                            @Override // miuix.appcompat.app.DatePickerDialog.OnDateSetListener
                            public void onDateSet(DatePicker datePicker, int i2, int i3, int i4) {
                                Calendar calendar2 = Calendar.getInstance();
                                calendar2.set(i2, i3, i4, 0, 0, 0);
                                if (ShiftAlarmAdapter.this.mOnDateChangedListener != null) {
                                    ShiftAlarmAdapter.this.mOnDateChangedListener.onStartTimeChanged(calendar2.getTimeInMillis(), i);
                                }
                            }
                        }, calendar.get(1), calendar.get(2), calendar.get(5));
                        datePickerDialog.setHapticFeedbackEnabled(false);
                        datePickerDialog.setTitle(R.string.shift_alarm_start_time);
                        datePickerDialog.setLunarMode(false);
                        datePickerDialog.show();
                        return;
                    }
                    final ShiftAlarmDataBean shiftAlarmDataBean = (ShiftAlarmDataBean) dataBean;
                    boolean zCanClose = (shiftAlarmDataBean.enable && ShiftAlarmAdapter.this.mGetDataInstance != null) ? ShiftAlarmAdapter.this.mGetDataInstance.canClose() : true;
                    ShiftAlarmTimePickerDialog shiftAlarmTimePickerDialog = new ShiftAlarmTimePickerDialog(ShiftAlarmAdapter.this.mContext, new ShiftAlarmTimePickerDialog.OnTimeChanged() { // from class: com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.ShiftAlarmAdapter.1.3
                        @Override // com.android.deskclock.alarm.ShiftAlarmTimePickerDialog.OnTimeChanged
                        public void onTimeChanged(TimePicker timePicker, int i2, int i3, boolean z) {
                            if (ShiftAlarmAdapter.this.mOnDateChangedListener != null) {
                                ShiftAlarmAdapter.this.mOnDateChangedListener.onAlarmChanged(i2, i3, z, shiftAlarmDataBean.index, i);
                            }
                        }
                    }, shiftAlarmDataBean.hour, shiftAlarmDataBean.minutes, DateFormat.is24HourFormat(ShiftAlarmAdapter.this.mContext));
                    shiftAlarmTimePickerDialog.setEnable(shiftAlarmDataBean.enable);
                    shiftAlarmTimePickerDialog.setCanClose(zCanClose);
                    shiftAlarmTimePickerDialog.setHapticFeedbackEnabled(true);
                    shiftAlarmTimePickerDialog.setTitle(context.getResources().getQuantityString(R.plurals.index_in_shift_alarms, shiftAlarmDataBean.index, Integer.valueOf(shiftAlarmDataBean.index)));
                    shiftAlarmTimePickerDialog.show();
                }
            };
            this.mContext = context;
        }

        public void setData(ArrayList<DataBean> arrayList) {
            this.mDataList = arrayList;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == DataBean.TYPE_MORE_DATA) {
                return new MoreItemHolder(LayoutInflater.from(this.mContext).inflate(R.layout.more_data_item, viewGroup, false), this.mOnMoreButtonClickListener);
            }
            return new AlarmHolder(LayoutInflater.from(this.mContext).inflate(R.layout.set_alarm_value_preference, viewGroup, false), this.mItemOnClickListener);
        }

        @Override // miuix.recyclerview.card.CardGroupAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, int i) {
            super.onBindViewHolder(viewHolder, i);
            DataBean dataBean = this.mDataList.get(i);
            if (dataBean.type == DataBean.TYPE_SETTINGS_TITLE && (dataBean instanceof ShiftTitleDataBean)) {
                AlarmHolder alarmHolder = (AlarmHolder) viewHolder;
                ShiftTitleDataBean shiftTitleDataBean = (ShiftTitleDataBean) dataBean;
                alarmHolder.title.setText(shiftTitleDataBean.title);
                if (shiftTitleDataBean.index == 1) {
                    alarmHolder.summary.setText(DeskClockApp.getAppContext().getResources().getQuantityString(R.plurals.shift_alarm_duration, shiftTitleDataBean.duration, Integer.valueOf(shiftTitleDataBean.duration)));
                    return;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(shiftTitleDataBean.startTime);
                String date = TimeUtil.formatDate(DeskClockApp.getAppContext().getString(R.string.worldcolock_time_date), calendar.getTime());
                alarmHolder.summary.setMaxWidth(this.mContext.getResources().getDimensionPixelSize(R.dimen.shift_alarm_preference_value_max_width));
                alarmHolder.summary.setText(date);
                return;
            }
            if (dataBean.type == DataBean.TYPE_ALARM) {
                AlarmHolder alarmHolder2 = (AlarmHolder) viewHolder;
                ShiftAlarmDataBean shiftAlarmDataBean = (ShiftAlarmDataBean) dataBean;
                Calendar calendar2 = Calendar.getInstance();
                String str = AlarmHelper.get24HourMode() ? AlarmModel.M24 : AlarmModel.M12_WITH_A;
                calendar2.set(11, shiftAlarmDataBean.hour);
                calendar2.set(12, shiftAlarmDataBean.minutes);
                if (shiftAlarmDataBean.index != this.mCurrentIndex) {
                    alarmHolder2.title.setText(DeskClockApp.getAppContext().getResources().getQuantityString(R.plurals.index_in_shift_alarms, shiftAlarmDataBean.index, Integer.valueOf(shiftAlarmDataBean.index)));
                } else {
                    alarmHolder2.title.setText(DeskClockApp.getAppContext().getString(R.string.index_in_shift_alarms_today, DeskClockApp.getAppContext().getResources().getQuantityString(R.plurals.index_in_shift_alarms, shiftAlarmDataBean.index, Integer.valueOf(shiftAlarmDataBean.index)), DeskClockApp.getAppContext().getResources().getString(R.string.today)));
                }
                alarmHolder2.summary.setText(shiftAlarmDataBean.enable ? (String) DateFormat.format(str, calendar2) : DeskClockApp.getAppContext().getString(R.string.rest));
                return;
            }
            if (dataBean.type == DataBean.TYPE_MORE_DATA) {
                ((MoreItemHolder) viewHolder).mTitle.setText(R.string.show_more_shift_alarms);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            ArrayList<DataBean> arrayList = this.mDataList;
            if (arrayList == null) {
                return 0;
            }
            return arrayList.get(i).type;
        }

        @Override // miuix.recyclerview.card.CardGroupAdapter
        public int getItemViewGroup(int i) {
            ArrayList<DataBean> arrayList = this.mDataList;
            if (arrayList == null) {
                return 0;
            }
            return arrayList.get(i).group;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            ArrayList<DataBean> arrayList = this.mDataList;
            if (arrayList == null) {
                return 0;
            }
            return arrayList.size();
        }
    }

    static class MoreItemHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        OnMoreButtonClickListener mOnMoreButtonClickListener;
        final TextView mTitle;

        public MoreItemHolder(View view, OnMoreButtonClickListener onMoreButtonClickListener) {
            super(view);
            this.mOnMoreButtonClickListener = onMoreButtonClickListener;
            this.mTitle = (TextView) view.findViewById(R.id.title);
            view.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.MoreItemHolder.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    if (MoreItemHolder.this.mOnMoreButtonClickListener != null) {
                        MoreItemHolder.this.mOnMoreButtonClickListener.onMoreButtonClick();
                    }
                }
            });
        }
    }

    static class AlarmHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private final OnItemClickListener mOnClickListener;
        public int position;
        private final TextView summary;
        private final TextView title;

        public AlarmHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.title);
            this.summary = (TextView) view.findViewById(R.id.summary);
            view.findViewById(R.id.arrow_right).setVisibility(0);
            this.mOnClickListener = onItemClickListener;
            view.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.AlarmHolder.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    int bindingAdapterPosition = AlarmHolder.this.getBindingAdapterPosition();
                    if (bindingAdapterPosition == -1 || AlarmHolder.this.mOnClickListener == null) {
                        return;
                    }
                    AlarmHolder.this.mOnClickListener.onItemClick(bindingAdapterPosition);
                }
            });
        }
    }

    private static class DataBean {
        public static int GROUP0 = 0;
        public static int GROUP1 = 1;
        public static int TYPE_ALARM = 1;
        public static int TYPE_MORE_DATA = 2;
        public static int TYPE_SETTINGS_TITLE;
        public int group;
        public int index;
        public int type;

        private DataBean() {
        }
    }

    static class MoreDataBean extends DataBean {
        MoreDataBean() {
            super();
            this.type = TYPE_MORE_DATA;
            this.group = GROUP1;
        }
    }

    static class ShiftAlarmDataBean extends DataBean {
        public boolean enable;
        public int hour;
        public int minutes;

        public ShiftAlarmDataBean(int i, int i2, int i3, boolean z) {
            super();
            this.index = i;
            this.hour = i2;
            this.minutes = i3;
            this.enable = z;
            this.type = TYPE_ALARM;
            this.group = GROUP1;
        }
    }

    static class ShiftTitleDataBean extends DataBean {
        int duration;
        long startTime;
        public String title;

        public ShiftTitleDataBean(String str, int i, int i2, long j) {
            super();
            this.title = str;
            this.startTime = j;
            this.duration = i2;
            this.index = i;
            this.type = TYPE_SETTINGS_TITLE;
            this.group = GROUP0;
        }
    }
}
