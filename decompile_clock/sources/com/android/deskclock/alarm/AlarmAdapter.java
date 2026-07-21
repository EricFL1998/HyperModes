package com.android.deskclock.alarm;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.BackgroundUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.view.list.EditableAdapter;
import com.android.deskclock.view.list.EditableViewHolder;
import java.util.List;
import java.util.Locale;
import miuix.core.util.MiuixUIUtils;
import miuix.slidingwidget.widget.SlidingButton;

/* JADX INFO: loaded from: classes.dex */
public class AlarmAdapter extends EditableAdapter {
    private static final String TAG = "DC:AlarmAdapter";
    private OnAlarmCheckedChangedListener mCheckedChangeListener;
    private final Context mContext;
    private List<AlarmModel.AlarmBean> mDataList;
    private int mFontLevel;
    private boolean mHasWakeAlarm;
    private boolean mIsAppNightMode;
    private boolean mIsInternalScreen;
    private OnItemClickListener mItemClickListener;
    private OnLongClickListener mOnLongClickListener;
    private int mSelectedId;

    public interface OnAlarmCheckedChangedListener {
        void onCheckedChanged(CompoundButton compoundButton, boolean z, Alarm alarm, int i);
    }

    public interface OnItemClickListener {
        void onAlarmClick(View view, int i, Alarm alarm);

        void onWakeAlarmClick(View view);
    }

    public interface OnLongClickListener {
        boolean onLongClick(int i);
    }

    public AlarmAdapter(Context context, RecyclerView recyclerView) {
        super(recyclerView);
        this.mIsInternalScreen = true;
        this.mSelectedId = -1;
        this.mContext = context;
        this.mIsAppNightMode = BackgroundUtil.isAppNightMode();
        this.mFontLevel = MiuixUIUtils.getFontLevel(context);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<AlarmModel.AlarmBean> list = this.mDataList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        List<AlarmModel.AlarmBean> list = this.mDataList;
        if (list == null || i >= list.size()) {
            return -1L;
        }
        return this.mDataList.get(i).id;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        List<AlarmModel.AlarmBean> list = this.mDataList;
        if (list == null || i >= list.size()) {
            return super.getItemViewType(i);
        }
        if (this.mIsInternalScreen) {
            return this.mDataList.get(i).type;
        }
        return -this.mDataList.get(i).type;
    }

    public AlarmModel.AlarmBean getBean(int i) {
        List<AlarmModel.AlarmBean> list = this.mDataList;
        if (list == null || i >= list.size()) {
            return null;
        }
        return this.mDataList.get(i);
    }

    public int getPositionById(int i) {
        if (this.mDataList == null) {
            return -1;
        }
        for (int i2 = 0; i2 < this.mDataList.size(); i2++) {
            if (this.mDataList.get(i2).id == i) {
                return i2;
            }
        }
        return -1;
    }

    public void setInternalScreen(boolean z) {
        this.mIsInternalScreen = z;
    }

    @Override // com.android.deskclock.view.list.EditableAdapter
    public boolean isItemEditable(int i) {
        return getItemViewType(i) == 3 || getItemViewType(i) == -3;
    }

    @Override // com.android.deskclock.view.list.EditableAdapter
    public int getEditableItemCount() {
        if (this.mDataList == null) {
            return 0;
        }
        int i = 0;
        for (int i2 = 0; i2 < this.mDataList.size(); i2++) {
            if (isItemEditable(i2)) {
                i++;
            }
        }
        return i;
    }

    public void setSelectedId(int i) {
        this.mSelectedId = i;
    }

    public int getSelectedId() {
        return this.mSelectedId;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.mOnLongClickListener = onLongClickListener;
    }

    public void setOnAlarmCheckedChangeListener(OnAlarmCheckedChangedListener onAlarmCheckedChangedListener) {
        this.mCheckedChangeListener = onAlarmCheckedChangedListener;
    }

    public void initData(List<AlarmModel.AlarmBean> list) {
        this.mDataList = list;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i != -2) {
            if (i == -1 || i == 1) {
                return new TextViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.item_alarm_title, viewGroup, false));
            }
            if (i != 2) {
                return new AlarmViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.alarm_time, viewGroup, false), this.mIsAppNightMode);
            }
        }
        return new WakeAlarmViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.bedtime_alarm_display, viewGroup, false));
    }

    @Override // com.android.deskclock.view.list.EditableAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        if (viewHolder instanceof WakeAlarmViewHolder) {
            bindWakeAlarmViewHolder((WakeAlarmViewHolder) viewHolder, i);
        } else if (viewHolder instanceof AlarmViewHolder) {
            bindAlarmViewHolder((AlarmViewHolder) viewHolder, i);
        } else if (viewHolder instanceof TextViewHolder) {
            bindTitleViewHolder((TextViewHolder) viewHolder, i);
        }
    }

    private void bindTitleViewHolder(TextViewHolder textViewHolder, int i) {
        textViewHolder.mTitleTv.setText(i == 0 ? R.string.bedtime : R.string.alarm_list_title);
    }

    private void bindWakeAlarmViewHolder(final WakeAlarmViewHolder wakeAlarmViewHolder, int i) {
        AlarmModel.AlarmBean alarmBean = this.mDataList.get(i);
        MiuiFolme.registerItemAnim(wakeAlarmViewHolder.itemView, this.mIsAppNightMode);
        wakeAlarmViewHolder.itemView.setFocusable(false);
        wakeAlarmViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (AlarmAdapter.this.mItemClickListener != null) {
                    wakeAlarmViewHolder.itemView.setForeground(null);
                    AlarmAdapter.this.mItemClickListener.onWakeAlarmClick(wakeAlarmViewHolder.itemView);
                }
            }
        });
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) wakeAlarmViewHolder.amPmDisplay.getLayoutParams();
        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) wakeAlarmViewHolder.timeDisplay.getLayoutParams();
        String language = Locale.getDefault().getLanguage();
        if (language.equals("zh") || language.equals("ja") || language.equals("ko") || ((language.equals("fa") || language.equals("ar")) && Util.isRtl())) {
            layoutParams2.addRule(17, R.id.am_pm);
        } else {
            layoutParams.addRule(17, R.id.get_up_time);
        }
        RelativeLayout.LayoutParams layoutParams3 = (RelativeLayout.LayoutParams) wakeAlarmViewHolder.alarmInFutureView.getLayoutParams();
        if (this.mFontLevel == 2) {
            wakeAlarmViewHolder.daysOfWeekViewRightLine.setVisibility(8);
            layoutParams3.addRule(3, R.id.days_of_week);
        } else {
            wakeAlarmViewHolder.daysOfWeekViewRightLine.setVisibility(0);
            layoutParams3.addRule(17, R.id.days_of_week_right_line);
        }
        wakeAlarmViewHolder.timeDisplay.setText(alarmBean.mTimeDisplay);
        wakeAlarmViewHolder.daysOfWeekView.setText(alarmBean.daysOfWeekStr);
        if (AlarmHelper.get24HourMode()) {
            wakeAlarmViewHolder.amPmDisplay.setVisibility(8);
        } else {
            wakeAlarmViewHolder.amPmDisplay.setText(alarmBean.mAmPmDisplay);
            wakeAlarmViewHolder.amPmDisplay.setVisibility(0);
        }
        if (!TextUtils.isEmpty(alarmBean.alarmSkipOnceText)) {
            if (this.mFontLevel == 2) {
                wakeAlarmViewHolder.daysOfWeekViewRightLine.setVisibility(8);
            } else {
                wakeAlarmViewHolder.daysOfWeekViewRightLine.setVisibility(0);
            }
            wakeAlarmViewHolder.alarmInFutureView.setVisibility(0);
            wakeAlarmViewHolder.alarmInFutureView.setText(alarmBean.alarmSkipOnceText);
        } else {
            wakeAlarmViewHolder.daysOfWeekViewRightLine.setVisibility(8);
            wakeAlarmViewHolder.alarmInFutureView.setVisibility(8);
        }
        setBedtimeColor(alarmBean.alarm.enabled, wakeAlarmViewHolder);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) wakeAlarmViewHolder.bedtimeView.getLayoutParams();
        marginLayoutParams.setMarginEnd((int) this.mContext.getResources().getDimension(R.dimen.alarm_item_layout_margin_start));
        marginLayoutParams.setMarginStart((int) this.mContext.getResources().getDimension(R.dimen.alarm_item_layout_margin_start));
        marginLayoutParams.height = -2;
    }

    private void setBedtimeColor(boolean z, WakeAlarmViewHolder wakeAlarmViewHolder) {
        if (isInActionMode()) {
            wakeAlarmViewHolder.bedtimeAlarmButton.setForeground(null);
            wakeAlarmViewHolder.bedtimeAlarmButton.setBackgroundResource(R.drawable.ic_manage_settings_enter_cheked);
            wakeAlarmViewHolder.bedtimeAlarmButton.setAlpha(0.3f);
            z = false;
        } else {
            wakeAlarmViewHolder.bedtimeAlarmButton.setBackgroundResource(R.drawable.ic_manage_settings_enter_n);
            wakeAlarmViewHolder.bedtimeAlarmButton.setAlpha(0.4f);
        }
        int color = this.mContext.getResources().getColor(z ? R.color.alarm_item_time_display_color_enable : R.color.alarm_item_time_display_color_disable);
        wakeAlarmViewHolder.timeDisplay.setTextColor(color);
        wakeAlarmViewHolder.amPmDisplay.setTextColor(color);
        int color2 = this.mContext.getResources().getColor(z ? R.color.alarm_item_info_enable : R.color.alarm_item_info_disable);
        wakeAlarmViewHolder.daysOfWeekView.setTextColor(color2);
        wakeAlarmViewHolder.daysOfWeekViewRightLine.setTextColor(color2);
        wakeAlarmViewHolder.alarmInFutureView.setTextColor(color2);
    }

    private void bindAlarmViewHolder(AlarmViewHolder alarmViewHolder, int i) {
        alarmViewHolder.position = i;
        if (!isInActionMode()) {
            alarmViewHolder.itemView.setForeground(null);
        }
        AlarmModel.AlarmBean alarmBean = this.mDataList.get(i);
        Alarm alarm = alarmBean.alarm;
        alarmViewHolder.itemView.setVisibility(0);
        if (!alarm.enabled && 0 < alarm.skipTime && alarm.skipTime < System.currentTimeMillis()) {
            AlarmHelper.enableAlarm(this.mContext, alarm.id, true);
            alarm.enabled = true;
            alarm.skipTime = 0L;
        }
        changeToLinearLayout(alarmViewHolder);
        setAlarmItemForLinear(alarmViewHolder, alarmBean);
        if (Util.isTinyScreen(DeskClockApp.getAppContext())) {
            alarmViewHolder.daysOfWeekView.setMaxWidth((int) this.mContext.getResources().getDimension(R.dimen.alarm_item_days_of_week_width_tiny));
            alarmViewHolder.daysOfWeekViewRightLine.setVisibility(8);
            alarmViewHolder.labelView.setVisibility(8);
        }
    }

    public void hasWakeAlarm(boolean z) {
        this.mHasWakeAlarm = z;
    }

    private boolean shouldHasEndMargin(int i) {
        boolean z = this.mHasWakeAlarm;
        if (z && i % 2 == 1) {
            return true;
        }
        return !z && i % 2 == 0;
    }

    private void changeToLinearLayout(AlarmViewHolder alarmViewHolder) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) alarmViewHolder.amPmDisplay.getLayoutParams();
        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) alarmViewHolder.timeDisplay.getLayoutParams();
        String language = Locale.getDefault().getLanguage();
        if (language.equals("zh") || language.equals("ja") || language.equals("ko") || language.equals("fa") || language.equals("ar")) {
            layoutParams2.addRule(17, R.id.am_pm);
        } else {
            layoutParams.addRule(17, R.id.time_display);
        }
        RelativeLayout.LayoutParams layoutParams3 = (RelativeLayout.LayoutParams) alarmViewHolder.labelView.getLayoutParams();
        RelativeLayout.LayoutParams layoutParams4 = (RelativeLayout.LayoutParams) alarmViewHolder.alarmInFutureView.getLayoutParams();
        if (this.mFontLevel == 2) {
            alarmViewHolder.daysOfWeekViewRightLine.setVisibility(8);
            layoutParams3.addRule(3, R.id.days_of_week);
            layoutParams4.addRule(3, R.id.days_of_week);
            layoutParams3.topMargin = (int) this.mContext.getResources().getDimension(R.dimen.alarm_time_info_margin_top);
        } else {
            alarmViewHolder.daysOfWeekViewRightLine.setVisibility(0);
            layoutParams3.addRule(17, R.id.days_of_week_right_line);
            layoutParams4.addRule(17, R.id.days_of_week_right_line);
        }
        RelativeLayout.LayoutParams layoutParams5 = (RelativeLayout.LayoutParams) alarmViewHolder.clockOnOff.getLayoutParams();
        layoutParams5.addRule(15, -1);
        layoutParams5.addRule(10, 0);
        layoutParams5.topMargin = 0;
        RelativeLayout.LayoutParams layoutParams6 = (RelativeLayout.LayoutParams) alarmViewHolder.itemView.findViewById(android.R.id.checkbox).getLayoutParams();
        layoutParams6.addRule(15, -1);
        layoutParams6.addRule(10, 0);
        layoutParams6.topMargin = 0;
    }

    private void setAlarmItemForLinear(AlarmViewHolder alarmViewHolder, AlarmModel.AlarmBean alarmBean) {
        alarmViewHolder.timeDisplay.setText(alarmBean.mTimeDisplay);
        if (!TextUtils.isEmpty(alarmBean.daysOfWeekStr)) {
            alarmViewHolder.daysOfWeekView.setText(alarmBean.daysOfWeekStr);
            alarmViewHolder.daysOfWeekView.setVisibility(0);
        } else {
            alarmViewHolder.daysOfWeekView.setText("");
            alarmViewHolder.daysOfWeekView.setVisibility(8);
        }
        if (AlarmHelper.get24HourMode()) {
            alarmViewHolder.amPmDisplay.setVisibility(8);
        } else {
            alarmViewHolder.amPmDisplay.setText(alarmBean.mAmPmDisplay);
            alarmViewHolder.amPmDisplay.setVisibility(0);
            if (Util.isTinyScreen(DeskClockApp.getAppContext())) {
                alarmViewHolder.amPmDisplay.setMaxWidth((int) this.mContext.getResources().getDimension(R.dimen.alarm_clock_am_pm_max_width));
            }
        }
        if (alarmBean.alarm.label != null && alarmBean.alarm.label.trim().length() != 0) {
            alarmViewHolder.labelView.setText(alarmBean.alarm.label.trim());
            alarmViewHolder.labelView.setVisibility(0);
        } else {
            alarmViewHolder.labelView.setText("");
            alarmViewHolder.labelView.setVisibility(8);
        }
        if (alarmViewHolder.labelView.getVisibility() == 8 || alarmViewHolder.daysOfWeekView.getVisibility() == 8) {
            alarmViewHolder.daysOfWeekViewRightLine.setVisibility(8);
            alarmViewHolder.daysOfWeekView.setMaxWidth((int) this.mContext.getResources().getDimension(R.dimen.alarm_time_days_of_week_max_width_large));
        } else if (this.mFontLevel == 2) {
            alarmViewHolder.daysOfWeekViewRightLine.setVisibility(8);
        } else {
            alarmViewHolder.daysOfWeekViewRightLine.setVisibility(0);
            alarmViewHolder.daysOfWeekView.setMaxWidth((int) this.mContext.getResources().getDimension(R.dimen.alarm_time_days_of_week_max_width));
        }
        if (!TextUtils.isEmpty(alarmBean.alarmSkipOnceText)) {
            if (alarmViewHolder.labelView.getVisibility() == 0) {
                alarmViewHolder.labelView.setVisibility(8);
            } else if (this.mFontLevel == 2) {
                alarmViewHolder.daysOfWeekViewRightLine.setVisibility(8);
            } else {
                alarmViewHolder.daysOfWeekViewRightLine.setVisibility(0);
            }
            alarmViewHolder.alarmInFutureView.setVisibility(0);
            alarmViewHolder.alarmInFutureView.setText(alarmBean.alarmSkipOnceText);
        } else {
            alarmViewHolder.alarmInFutureView.setVisibility(8);
        }
        if (alarmBean.alarm.id != this.mSelectedId) {
            alarmViewHolder.clockOnOff.setChecked(alarmBean.alarm.enabled);
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) alarmViewHolder.itemView.getLayoutParams();
        marginLayoutParams.setMarginEnd((int) this.mContext.getResources().getDimension(R.dimen.alarm_item_layout_margin_start));
        marginLayoutParams.setMarginStart((int) this.mContext.getResources().getDimension(R.dimen.alarm_item_layout_margin_start));
        marginLayoutParams.height = -2;
        alarmViewHolder.itemView.setLayoutParams(marginLayoutParams);
        alarmViewHolder.itemView.requestLayout();
        setClockItemTextColor(alarmViewHolder, alarmBean.alarm.enabled);
    }

    private void setClockItemTextColor(AlarmViewHolder alarmViewHolder, boolean z) {
        int color = this.mContext.getResources().getColor(z ? R.color.alarm_item_time_display_color_enable : R.color.alarm_item_time_display_color_disable);
        alarmViewHolder.timeDisplay.setTextColor(color);
        alarmViewHolder.amPmDisplay.setTextColor(color);
        alarmViewHolder.daysOfWeekViewRightLine.setTextColor(this.mContext.getResources().getColor(z ? R.color.alarm_item_line_enable : R.color.alarm_item_line_disable));
        int color2 = this.mContext.getResources().getColor(z ? R.color.alarm_item_info_enable : R.color.alarm_item_info_disable);
        alarmViewHolder.labelView.setTextColor(color2);
        alarmViewHolder.daysOfWeekView.setTextColor(color2);
        alarmViewHolder.alarmInFutureView.setTextColor(color2);
    }

    private class TextViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTv;

        public TextViewHolder(View view) {
            super(view);
            this.mTitleTv = (TextView) view.findViewById(R.id.title_text_view);
        }
    }

    private class WakeAlarmViewHolder extends RecyclerView.ViewHolder {
        private TextView alarmInFutureView;
        private TextView amPmDisplay;
        private ImageView bedtimeAlarmButton;
        public View bedtimeView;
        private TextView daysOfWeekView;
        private TextView daysOfWeekViewRightLine;
        private TextView timeDisplay;

        public WakeAlarmViewHolder(View view) {
            super(view);
            this.timeDisplay = (TextView) view.findViewById(R.id.get_up_time);
            ViewGroup viewGroup = (ViewGroup) view;
            this.daysOfWeekView = (TextView) viewGroup.findViewById(R.id.days_of_week);
            this.amPmDisplay = (TextView) viewGroup.findViewById(R.id.am_pm);
            this.bedtimeView = viewGroup.findViewById(R.id.bedtime_alarm);
            this.bedtimeAlarmButton = (ImageView) viewGroup.findViewById(R.id.bedtime_alarm_btn);
            TextView textView = (TextView) viewGroup.findViewById(R.id.days_of_week_right_line);
            this.daysOfWeekViewRightLine = textView;
            textView.setVisibility(0);
            this.alarmInFutureView = (TextView) viewGroup.findViewById(R.id.alarm_in_future);
        }
    }

    private class AlarmViewHolder extends EditableViewHolder {
        public TextView alarmInFutureView;
        private TextView amPmDisplay;
        public SlidingButton clockOnOff;
        public TextView daysOfWeekView;
        public TextView daysOfWeekViewRightLine;
        public TextView labelView;
        public int position;
        private TextView timeDisplay;

        public AlarmViewHolder(View view, boolean z) {
            super(view);
            this.clockOnOff = (SlidingButton) view.findViewById(R.id.clock_onoff);
            this.daysOfWeekViewRightLine = (TextView) view.findViewById(R.id.days_of_week_right_line);
            this.daysOfWeekView = (TextView) view.findViewById(R.id.days_of_week);
            this.labelView = (TextView) view.findViewById(R.id.label);
            this.alarmInFutureView = (TextView) view.findViewById(R.id.alarm_in_future);
            this.timeDisplay = (TextView) view.findViewById(R.id.time_display);
            this.amPmDisplay = (TextView) view.findViewById(R.id.am_pm);
            MiuiFolme.registerItemAnim(view, z);
            MiuiFolme.addPressAnim(this.clockOnOff);
            this.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmAdapter.AlarmViewHolder.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    if (AlarmAdapter.this.mItemClickListener == null || AlarmViewHolder.this.position >= AlarmAdapter.this.mDataList.size()) {
                        return;
                    }
                    AlarmAdapter.this.mItemClickListener.onAlarmClick(AlarmViewHolder.this.itemView, AlarmViewHolder.this.position, ((AlarmModel.AlarmBean) AlarmAdapter.this.mDataList.get(AlarmViewHolder.this.position)).alarm);
                }
            });
            this.itemView.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.deskclock.alarm.AlarmAdapter.AlarmViewHolder.2
                @Override // android.view.View.OnLongClickListener
                public boolean onLongClick(View view2) {
                    if (AlarmAdapter.this.mOnLongClickListener == null || !AlarmAdapter.this.mOnLongClickListener.onLongClick(AlarmViewHolder.this.position)) {
                        return true;
                    }
                    MiuiFolme.setTouchUp(AlarmViewHolder.this.itemView);
                    AlarmViewHolder.this.itemView.setForeground(AlarmAdapter.this.mContext.getResources().getDrawable(R.drawable.alarm_item_background_checked));
                    return true;
                }
            });
            this.clockOnOff.setOnCheckedChangeListener(null);
            this.clockOnOff.setOnPerformCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.deskclock.alarm.AlarmAdapter.AlarmViewHolder.3
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    if (AlarmViewHolder.this.position >= AlarmAdapter.this.mDataList.size() || ((AlarmModel.AlarmBean) AlarmAdapter.this.mDataList.get(AlarmViewHolder.this.position)).alarm.enabled == z2 || AlarmAdapter.this.mCheckedChangeListener == null) {
                        return;
                    }
                    AlarmAdapter.this.mCheckedChangeListener.onCheckedChanged(compoundButton, z2, ((AlarmModel.AlarmBean) AlarmAdapter.this.mDataList.get(AlarmViewHolder.this.position)).alarm, AlarmViewHolder.this.position);
                }
            });
            if (Util.isDeviceCetus()) {
                this.daysOfWeekView.setMaxWidth((int) AlarmAdapter.this.mContext.getResources().getDimension(R.dimen.alarm_time_item_label_textview_max_width));
            }
        }

        @Override // com.android.deskclock.view.list.EditableViewHolder, com.android.deskclock.view.list.ViewHolderEditableCallback
        public void onAnimationStart(boolean z) {
            super.onAnimationStart(z);
            this.clockOnOff.setVisibility(0);
        }

        @Override // com.android.deskclock.view.list.EditableViewHolder, com.android.deskclock.view.list.ViewHolderEditableCallback
        public void onAnimationUpdate(boolean z, float f) {
            super.onAnimationUpdate(z, f);
            if (z) {
                this.clockOnOff.setAlpha(1.0f - f);
                this.clockOnOff.setVisibility(8);
            } else {
                this.itemView.setForeground(null);
                this.clockOnOff.setAlpha(f);
            }
        }

        @Override // com.android.deskclock.view.list.EditableViewHolder, com.android.deskclock.view.list.ViewHolderEditableCallback
        public void onAnimationStop(boolean z) {
            super.onAnimationStop(z);
            if (z) {
                this.clockOnOff.setVisibility(8);
            } else {
                this.clockOnOff.setVisibility(0);
            }
        }

        @Override // com.android.deskclock.view.list.EditableViewHolder, com.android.deskclock.view.list.ViewHolderEditableCallback
        public void onUpdateEditable(boolean z, boolean z2) {
            super.onUpdateEditable(z, z2);
            if (z) {
                this.clockOnOff.setVisibility(8);
                if (z2) {
                    MiuiFolme.setTouchUp(this.itemView);
                    this.itemView.setForeground(AlarmAdapter.this.mContext.getResources().getDrawable(R.drawable.alarm_item_background_checked));
                    return;
                } else {
                    this.itemView.setForeground(null);
                    this.itemView.setBackgroundResource(R.drawable.alarm_item_background);
                    MiuiFolme.setTouchUp(this.itemView);
                    return;
                }
            }
            this.itemView.setBackgroundResource(R.drawable.alarm_item_background);
            this.clockOnOff.setAlpha(1.0f);
            this.clockOnOff.setVisibility(0);
        }
    }
}
