package com.android.deskclock.worldclock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.BackgroundUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.view.CustomFlexboxLayout;
import com.android.deskclock.view.FormatClockForLocalTime;
import com.android.deskclock.view.list.EditableAdapter;
import com.android.deskclock.view.list.EditableViewHolder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.property.ViewProperty;
import miuix.core.util.MiuixUIUtils;

/* JADX INFO: loaded from: classes.dex */
public class TimezoneAdapter extends EditableAdapter {
    private static final String M12 = "hh:mm";
    private static final String M24 = "kk:mm";
    private Context mContext;
    private boolean mContrastMode;
    private List<CityObj> mDataList;
    private long mDisplayTimeMillis;
    private boolean mIsInternalScreen;
    private OnItemClickListener mItemClickListener;
    private float mLargeFontTranslationX;
    private AnimState mMiddleEndState;
    private AnimState mMiddleStartState;
    private float mMiddleTranslationX;
    private OnIvTouchListener mOnIvTouchListener;
    private OnLongClickListener mOnLongClickListener;
    private int mSelectedIndex;
    private List<TimezoneModel.TimezoneBean> mShowDataList;
    private AnimState mTempEndState;
    private AnimState mTempLargeFontEndState;
    private AnimState mTempLargeFontStartState;
    private AnimState mTempStartState;

    public interface OnItemClickListener {
        void onTimezoneClick(int i, CityObj cityObj, Calendar calendar);
    }

    public interface OnIvTouchListener {
        void onCancelTouch(RecyclerView.ViewHolder viewHolder);

        void onIvTouch(RecyclerView.ViewHolder viewHolder);
    }

    public interface OnLongClickListener {
        boolean onLongClick(int i, RecyclerView.ViewHolder viewHolder);
    }

    @Override // com.android.deskclock.view.list.EditableAdapter
    public boolean isItemEditable(int i) {
        return true;
    }

    public TimezoneAdapter(Context context, RecyclerView recyclerView) {
        super(recyclerView);
        this.mSelectedIndex = -1;
        this.mIsInternalScreen = true;
        this.mContext = context;
        this.mMiddleTranslationX = context.getResources().getDimensionPixelSize(R.dimen.worldclock_digit_clock_margin_end);
        this.mLargeFontTranslationX = this.mContext.getResources().getDimensionPixelSize(R.dimen.worldclock_digit_clock_large_font_translation_x);
        initAnimConfig();
        this.mDisplayTimeMillis = System.currentTimeMillis();
        this.mDataList = new ArrayList();
    }

    private void initAnimConfig() {
        this.mMiddleStartState = new AnimState("middleStart").add(ViewProperty.TRANSLATION_X, 0.0d);
        this.mMiddleEndState = new AnimState("middleEnd").add(ViewProperty.TRANSLATION_X, this.mMiddleTranslationX);
        this.mTempStartState = new AnimState("tempStart").add(ViewProperty.TRANSLATION_X, 0.0d);
        this.mTempLargeFontStartState = new AnimState("tempLargeFontStart").add(ViewProperty.TRANSLATION_X, this.mMiddleTranslationX);
        this.mTempLargeFontEndState = new AnimState("tempLargeFontEnd").add(ViewProperty.TRANSLATION_X, this.mLargeFontTranslationX);
        this.mTempEndState = new AnimState("tempEnd").add(ViewProperty.TRANSLATION_X, -this.mMiddleTranslationX);
        if (Util.isRtl()) {
            this.mMiddleEndState = new AnimState("middleEnd").add(ViewProperty.TRANSLATION_X, -this.mMiddleTranslationX);
            this.mTempEndState = new AnimState("tempEnd").add(ViewProperty.TRANSLATION_X, this.mMiddleTranslationX);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<TimezoneModel.TimezoneBean> list = this.mShowDataList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        List<TimezoneModel.TimezoneBean> list = this.mShowDataList;
        if (list == null || i >= list.size()) {
            return -1L;
        }
        return this.mShowDataList.get(i).cityObj.id;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return this.mIsInternalScreen ? 1 : -1;
    }

    public void setInternalScreen(boolean z) {
        this.mIsInternalScreen = z;
    }

    public String getItemTime(int i) {
        List<TimezoneModel.TimezoneBean> list = this.mShowDataList;
        if (list == null || list.isEmpty() || i >= this.mShowDataList.size()) {
            return null;
        }
        return this.mShowDataList.get(i).timeDisplay;
    }

    @Override // com.android.deskclock.view.list.EditableAdapter
    public int getEditableItemCount() {
        return getItemCount();
    }

    public void initData(List<CityObj> list, List<TimezoneModel.TimezoneBean> list2) {
        this.mShowDataList = list2;
    }

    public boolean setTime(long j) {
        if (this.mDisplayTimeMillis / 60000 == j / 60000) {
            return false;
        }
        this.mDisplayTimeMillis = j;
        return true;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.mOnLongClickListener = onLongClickListener;
    }

    public void setOnIvTouchListener(OnIvTouchListener onIvTouchListener) {
        this.mOnIvTouchListener = onIvTouchListener;
    }

    public void setSelected(int i) {
        if (this.mSelectedIndex == i) {
            this.mSelectedIndex = -1;
        } else {
            this.mSelectedIndex = i;
        }
    }

    public void cancelContrastMode() {
        if (this.mSelectedIndex != -1) {
            setSelected(-1);
            this.mContrastMode = false;
            notifyDataSetChanged();
        }
    }

    public void setContrastMode(boolean z) {
        this.mContrastMode = z;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new TimezoneViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.timezone_item, viewGroup, false));
    }

    @Override // com.android.deskclock.view.list.EditableAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        if (viewHolder instanceof TimezoneViewHolder) {
            bindTimezoneViewHolder((TimezoneViewHolder) viewHolder, i);
        }
    }

    public void bindTimezoneViewHolder(final TimezoneViewHolder timezoneViewHolder, final int i) {
        timezoneViewHolder.position = i;
        CityObj cityObj = this.mShowDataList.get(i).cityObj;
        TimezoneModel.TimezoneBean timezoneBean = this.mShowDataList.get(i);
        timezoneViewHolder.mDateDisplay.setText(timezoneBean.dateDisplay);
        timezoneViewHolder.mTimeDisplay.setText(timezoneBean.timeDisplay);
        if (AlarmHelper.get24HourMode()) {
            timezoneViewHolder.mAmPmDisplay.setText("");
            timezoneViewHolder.mAmPmDisplay.setVisibility(4);
        } else {
            timezoneViewHolder.mAmPmDisplay.setVisibility(0);
            timezoneViewHolder.mAmPmDisplay.setText(timezoneBean.amPmDisplay);
        }
        timezoneViewHolder.timezoneDateGap.setText(timezoneBean.timezoneDateGap);
        timezoneViewHolder.timezoneName.setText(timezoneBean.timezoneName);
        timezoneViewHolder.commonView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.worldclock.TimezoneAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (TimezoneAdapter.this.mShowDataList == null || i >= TimezoneAdapter.this.mShowDataList.size()) {
                    return;
                }
                CityObj cityObj2 = ((TimezoneModel.TimezoneBean) TimezoneAdapter.this.mShowDataList.get(i)).cityObj;
                if (TimezoneAdapter.this.mItemClickListener == null || i >= TimezoneAdapter.this.mShowDataList.size()) {
                    return;
                }
                TimezoneAdapter.this.mItemClickListener.onTimezoneClick(i, cityObj2, timezoneViewHolder.digitClock.getDisplayCalendar());
            }
        });
        timezoneViewHolder.commonView.setLongClickable(true);
        if (timezoneViewHolder.commonView.isLongClickable()) {
            timezoneViewHolder.commonView.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.deskclock.worldclock.TimezoneAdapter.2
                @Override // android.view.View.OnLongClickListener
                public boolean onLongClick(View view) {
                    if (TimezoneAdapter.this.mOnLongClickListener == null) {
                        return true;
                    }
                    MiuiFolme.setTouchUp(timezoneViewHolder.commonView);
                    TimezoneAdapter.this.mOnLongClickListener.onLongClick(i, timezoneViewHolder);
                    if (!timezoneViewHolder.mCheckBox.isChecked()) {
                        return true;
                    }
                    timezoneViewHolder.commonView.setForeground(TimezoneAdapter.this.mContext.getResources().getDrawable(R.drawable.alarm_item_background_checked));
                    timezoneViewHolder.commonView.setBackgroundResource(R.drawable.timezone_item_background);
                    return true;
                }
            });
        }
        timezoneViewHolder.mDragImageView.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.deskclock.worldclock.TimezoneAdapter.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == 0) {
                    TimezoneAdapter.this.mOnIvTouchListener.onIvTouch(timezoneViewHolder);
                    return true;
                }
                TimezoneAdapter.this.mOnIvTouchListener.onCancelTouch(timezoneViewHolder);
                return true;
            }
        });
        updateTimeZone(cityObj, timezoneViewHolder);
        setClockItemMargin(timezoneViewHolder);
        timezoneViewHolder.mTimezoneDate.post(new Runnable() { // from class: com.android.deskclock.worldclock.TimezoneAdapter.4
            @Override // java.lang.Runnable
            public void run() {
                if (timezoneViewHolder.mTimezoneDate.isLineBreak()) {
                    timezoneViewHolder.mLineAfterDate.setVisibility(4);
                } else {
                    timezoneViewHolder.mLineAfterDate.setVisibility(0);
                }
            }
        });
    }

    private void setClockItemMargin(TimezoneViewHolder timezoneViewHolder) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) timezoneViewHolder.mAmPmDisplay.getLayoutParams();
        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) timezoneViewHolder.mTimeDisplay.getLayoutParams();
        String language = Locale.getDefault().getLanguage();
        if (language.equals("zh") || language.equals("ja") || language.equals("ko") || ((language.equals("fa") || language.equals("ar")) && Util.isRtl())) {
            layoutParams2.addRule(17, R.id.am_pm);
        } else {
            layoutParams.addRule(17, R.id.time_display);
        }
        ((ViewGroup.MarginLayoutParams) timezoneViewHolder.itemView.getLayoutParams()).height = -2;
    }

    public void updateTimeZone(CityObj cityObj, TimezoneViewHolder timezoneViewHolder) {
        Calendar.getInstance(TimeZone.getTimeZone(cityObj.mTimeZone)).setTimeInMillis(this.mDisplayTimeMillis);
        if (this.mContrastMode) {
            return;
        }
        if (this.mSelectedIndex == timezoneViewHolder.getAdapterPosition()) {
            timezoneViewHolder.digitClock.setTextColor(this.mContext.getColor(R.color.worldclock_item_checked_color));
            timezoneViewHolder.timezoneName.setTextColor(this.mContext.getColor(R.color.worldclock_item_checked_color));
            timezoneViewHolder.mAmPmDisplay.setTextColor(this.mContext.getColor(R.color.worldclock_item_checked_color));
            timezoneViewHolder.mDateDisplay.setTextColor(this.mContext.getColor(R.color.worldclock_item_checked_color));
            timezoneViewHolder.timezoneDateGap.setTextColor(this.mContext.getColor(R.color.worldclock_item_checked_color));
            timezoneViewHolder.mLineAfterDate.setTextColor(this.mContext.getColor(R.color.worldclock_item_checked_color));
            return;
        }
        timezoneViewHolder.digitClock.setTextColor(this.mContext.getColor(R.color.black_worldclock_primary_text_color));
        timezoneViewHolder.timezoneName.setTextColor(this.mContext.getColor(R.color.black_worldclock_primary_text_color));
        timezoneViewHolder.mAmPmDisplay.setTextColor(this.mContext.getColor(R.color.black_worldclock_primary_text_color));
        timezoneViewHolder.mDateDisplay.setTextColor(this.mContext.getColor(R.color.worldclock_local_date_color));
        timezoneViewHolder.timezoneDateGap.setTextColor(this.mContext.getColor(R.color.worldclock_local_date_color));
        timezoneViewHolder.mLineAfterDate.setTextColor(this.mContext.getColor(R.color.worldclock_local_date_color));
    }

    private class TimezoneViewHolder extends EditableViewHolder {
        public View commonView;
        public FormatClockForLocalTime digitClock;
        public TextView mAmPmDisplay;
        public CheckBox mCheckBox;
        public TextView mDateDisplay;
        public ImageView mDragImageView;
        public TextView mLineAfterDate;
        public TextView mTimeDisplay;
        public CustomFlexboxLayout mTimezoneDate;
        public RelativeLayout mTimezoneTimeDisplay;
        public int position;
        public TextView timezoneDateGap;
        public TextView timezoneName;

        public TimezoneViewHolder(View view) {
            super(view);
            this.digitClock = (FormatClockForLocalTime) view.findViewById(R.id.timezone_time);
            this.timezoneDateGap = (TextView) view.findViewById(R.id.timezone_gap);
            this.timezoneName = (TextView) view.findViewById(R.id.timezone_name);
            this.commonView = view.findViewById(R.id.timezone_common_view);
            this.mTimeDisplay = (TextView) view.findViewById(R.id.time_display);
            this.mDateDisplay = (TextView) view.findViewById(R.id.date_display);
            this.mAmPmDisplay = (TextView) view.findViewById(R.id.am_pm);
            this.mDragImageView = (ImageView) view.findViewById(R.id.image_mv);
            this.mTimezoneDate = (CustomFlexboxLayout) view.findViewById(R.id.timezone_date);
            this.mTimezoneTimeDisplay = (RelativeLayout) view.findViewById(R.id.timezone_time_display);
            this.mCheckBox = (CheckBox) view.findViewById(android.R.id.checkbox);
            this.mLineAfterDate = (TextView) view.findViewById(R.id.line_after_date);
            if (Util.isRtl()) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mTimeDisplay.getLayoutParams();
                layoutParams.setMarginEnd((int) TimezoneAdapter.this.mContext.getResources().getDimension(R.dimen.worldclock_local_item_marginTop));
                this.mTimeDisplay.setLayoutParams(layoutParams);
            }
            if (MiuiSdk.isSupportFontAnim()) {
                MiuiFont.setFont(this.timezoneName, MiuiFont.MI_TYPE_MONO_DEMIBOLD);
                MiuiFont.setFont(this.timezoneDateGap, MiuiFont.MI_PRO_REGULAR);
                MiuiFont.setFont(this.mTimeDisplay, MiuiFont.MI_TYPE_MONO_DEMIBOLD);
                MiuiFont.setFont(this.mDateDisplay, MiuiFont.MI_PRO_REGULAR);
                MiuiFont.setFont(this.mAmPmDisplay, MiuiFont.MI_PRO_REGULAR);
            }
            if (MiuiSdk.isSupportFolmeAnim()) {
                MiuiFolme.registerItemAnim(this.commonView, BackgroundUtil.isAppNightMode());
            }
            this.mTimezoneDate.post(new Runnable() { // from class: com.android.deskclock.worldclock.TimezoneAdapter.TimezoneViewHolder.1
                @Override // java.lang.Runnable
                public void run() {
                    if (TimezoneViewHolder.this.mTimezoneDate.isLineBreak()) {
                        TimezoneViewHolder.this.mLineAfterDate.setVisibility(4);
                    } else {
                        TimezoneViewHolder.this.mLineAfterDate.setVisibility(0);
                    }
                }
            });
        }

        @Override // com.android.deskclock.view.list.EditableViewHolder, com.android.deskclock.view.list.ViewHolderEditableCallback
        public void onAnimationStart(boolean z) {
            super.onAnimationStart(z);
            MiuixUIUtils.getFontLevel(TimezoneAdapter.this.mContext);
            if (this.mDragImageView != null) {
                if (MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode()) {
                    if (z) {
                        setDigitClockMarginEnd((int) TimezoneAdapter.this.mMiddleTranslationX);
                        this.mDragImageView.setVisibility(0);
                        this.mTimezoneTimeDisplay.setVisibility(8);
                        return;
                    } else {
                        setDigitClockMarginEnd(0);
                        this.mDragImageView.setVisibility(8);
                        this.mTimezoneTimeDisplay.setVisibility(0);
                        return;
                    }
                }
                if (z) {
                    this.mDragImageView.setVisibility(0);
                    Folme.useAt(this.mTimezoneDate).state().to(TimezoneAdapter.this.mMiddleEndState, new AnimConfig[0]);
                    Folme.useAt(this.timezoneName).state().to(TimezoneAdapter.this.mMiddleEndState, new AnimConfig[0]);
                    this.mTimezoneTimeDisplay.setVisibility(8);
                    return;
                }
                this.mDragImageView.setVisibility(8);
                Folme.useAt(this.mTimezoneDate).state().to(TimezoneAdapter.this.mMiddleStartState, new AnimConfig[0]);
                Folme.useAt(this.timezoneName).state().to(TimezoneAdapter.this.mMiddleStartState, new AnimConfig[0]);
                this.mTimezoneTimeDisplay.setVisibility(0);
            }
        }

        @Override // com.android.deskclock.view.list.EditableViewHolder, com.android.deskclock.view.list.ViewHolderEditableCallback
        public void onAnimationUpdate(boolean z, float f) {
            super.onAnimationUpdate(z, f);
            if (MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode() || z) {
                return;
            }
            this.commonView.setForeground(null);
        }

        @Override // com.android.deskclock.view.list.EditableViewHolder, com.android.deskclock.view.list.ViewHolderEditableCallback
        public void onAnimationStop(boolean z) {
            super.onAnimationStop(z);
            ImageView imageView = this.mDragImageView;
            if (imageView == null || this.commonView == null) {
                return;
            }
            if (z) {
                imageView.setVisibility(0);
                if (MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode()) {
                    setDigitClockMarginEnd((int) TimezoneAdapter.this.mMiddleTranslationX);
                    return;
                }
                return;
            }
            imageView.setVisibility(8);
            this.commonView.setForeground(null);
            if (MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode()) {
                setDigitClockMarginEnd(0);
            }
        }

        @Override // com.android.deskclock.view.list.EditableViewHolder, com.android.deskclock.view.list.ViewHolderEditableCallback
        public void onUpdateEditable(boolean z, boolean z2) {
            ImageView imageView;
            super.onUpdateEditable(z, z2);
            if (this.mDragImageView != null && !MiuiSdk.isSuperLiteMode() && !MiuiSdk.isLiteV1StockMode() && this.commonView != null) {
                MiuixUIUtils.getFontLevel(TimezoneAdapter.this.mContext);
                if (z) {
                    this.mDragImageView.setVisibility(0);
                    Folme.useAt(this.mTimezoneDate).state().setTo(TimezoneAdapter.this.mMiddleEndState);
                    Folme.useAt(this.timezoneName).state().setTo(TimezoneAdapter.this.mMiddleEndState);
                    this.mTimezoneTimeDisplay.setVisibility(8);
                    if (z2) {
                        MiuiFolme.setTouchUp(this.commonView);
                        this.commonView.setForeground(TimezoneAdapter.this.mContext.getResources().getDrawable(R.drawable.alarm_item_background_checked));
                        return;
                    } else {
                        MiuiFolme.setTouchUp(this.commonView);
                        this.commonView.setForeground(null);
                        this.commonView.setBackgroundResource(R.drawable.timezone_item_background);
                        return;
                    }
                }
                this.mDragImageView.setVisibility(8);
                Folme.useAt(this.mTimezoneDate).state().setTo(TimezoneAdapter.this.mMiddleStartState);
                Folme.useAt(this.timezoneName).state().setTo(TimezoneAdapter.this.mMiddleStartState);
                this.mTimezoneTimeDisplay.setVisibility(0);
                this.commonView.setForeground(null);
                this.commonView.setBackgroundResource(R.drawable.alarm_item_background);
                return;
            }
            if (this.commonView == null || (imageView = this.mDragImageView) == null) {
                return;
            }
            if (z) {
                imageView.setVisibility(0);
                this.mTimezoneTimeDisplay.setVisibility(8);
                if (z2) {
                    this.commonView.setForeground(TimezoneAdapter.this.mContext.getResources().getDrawable(R.drawable.alarm_item_background_checked));
                    return;
                } else {
                    this.commonView.setForeground(null);
                    this.commonView.setBackgroundResource(R.drawable.alarm_item_background);
                    return;
                }
            }
            imageView.setVisibility(8);
            this.mTimezoneTimeDisplay.setVisibility(0);
            this.commonView.setForeground(null);
            this.commonView.setBackgroundResource(R.drawable.alarm_item_background);
        }

        private void setDigitClockMarginEnd(int i) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.digitClock.getLayoutParams();
            layoutParams.setMarginEnd(0);
            layoutParams.setMarginStart(i);
            this.digitClock.setLayoutParams(layoutParams);
        }
    }

    public List<CityObj> getData() {
        this.mDataList.clear();
        for (int i = 0; i < this.mShowDataList.size(); i++) {
            this.mDataList.add(this.mShowDataList.get(i).cityObj);
        }
        return this.mDataList;
    }

    public List<TimezoneModel.TimezoneBean> getShowDataList() {
        return this.mShowDataList;
    }

    public boolean onItemMove(int i, int i2) {
        swapCheckedStates(i, i2);
        Collections.swap(this.mShowDataList, i, i2);
        notifyDataSetChanged();
        return true;
    }
}
