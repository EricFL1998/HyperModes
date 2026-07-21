package com.android.deskclock.worldclock;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.DateFormatUtil;
import com.android.deskclock.util.TimeUtil;
import com.android.deskclock.view.FormatClockForLocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public class TimeZoneShowAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private OnStateChangeListener mListener;
    private long mDisplayTimeMillis = System.currentTimeMillis();
    private List<CityObj> mDataList = new ArrayList();

    public interface OnStateChangeListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    public TimeZoneShowAdapter(Context context, RecyclerView recyclerView) {
        this.mContext = context;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new TimeZoneShowVH(LayoutInflater.from(this.mContext).inflate(R.layout.timezone_show_item, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof TimeZoneShowVH) {
            bindTimezoneShowVH((TimeZoneShowVH) viewHolder, i);
        }
    }

    public void bindTimezoneShowVH(final TimeZoneShowVH timeZoneShowVH, int i) {
        timeZoneShowVH.tzMoveIv.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.deskclock.worldclock.TimeZoneShowAdapter.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() != 0) {
                    return true;
                }
                TimeZoneShowAdapter.this.mListener.onStartDrag(timeZoneShowVH);
                return true;
            }
        });
        timeZoneShowVH.digitClock.resetTimeFormat();
        updateTimeZone(this.mDataList.get(i), timeZoneShowVH);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<CityObj> list = this.mDataList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        List<CityObj> list = this.mDataList;
        if (list == null || i >= list.size()) {
            return -1L;
        }
        return this.mDataList.get(i).id;
    }

    public List<CityObj> getDataList() {
        return this.mDataList;
    }

    public void initData(List<CityObj> list) {
        this.mDataList.clear();
        this.mDataList.addAll(list);
    }

    public void setTime(long j) {
        this.mDisplayTimeMillis = j;
    }

    public void addCityData(CityObj cityObj) {
        this.mDataList.add(cityObj);
        notifyDataSetChanged();
    }

    public void addFirstCity(CityObj cityObj) {
        this.mDataList.add(0, cityObj);
        this.mDataList.removeIf(new Predicate() { // from class: com.android.deskclock.worldclock.TimeZoneShowAdapter$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((CityObj) obj).mCityId.equals(WorldClockEditActivity.LOCAL_CITY_ID);
            }
        });
        notifyDataSetChanged();
    }

    public void updateTimeZone(CityObj cityObj, TimeZoneShowVH timeZoneShowVH) {
        int i;
        String quantityString;
        TimeZone timeZone = TimeZone.getTimeZone(cityObj.mTimeZone);
        Calendar calendar = Calendar.getInstance(timeZone);
        boolean z = AlarmHelper.get24HourMode();
        calendar.setTimeInMillis(this.mDisplayTimeMillis);
        timeZoneShowVH.digitClock.updateTime(timeZone, this.mDisplayTimeMillis);
        timeZoneShowVH.timezoneName.setText(cityObj.mCityName);
        String date = TimeUtil.formatDate(DeskClockApp.getAppContext().getString(R.string.worldcolock_timezone_date), Calendar.getInstance().getTime(), timeZone);
        if (!date.equals(timeZoneShowVH.timezoneDateDisplay.getText())) {
            timeZoneShowVH.timezoneDateDisplay.setText(date);
        }
        if (!z) {
            timeZoneShowVH.timeAmPm.setVisibility(0);
            DateFormatUtil.reset();
            String[] amPmStrings = DateFormatUtil.getAmPmStrings();
            timeZoneShowVH.timeAmPm.setPadding(6, 0, 0, 0);
            timeZoneShowVH.timeAmPm.setText(Calendar.getInstance().get(9) == 0 ? amPmStrings[0] : amPmStrings[1]);
        } else {
            timeZoneShowVH.timeAmPm.setPadding(0, 0, 0, 0);
            timeZoneShowVH.timeAmPm.setText("");
            timeZoneShowVH.timeAmPm.setVisibility(4);
        }
        int offset = timeZone.getOffset(Calendar.getInstance().getTimeInMillis()) - TimeZone.getDefault().getOffset(Calendar.getInstance().getTimeInMillis());
        if (offset < 0) {
            offset = -offset;
            i = R.plurals.worldcolock_timezone_gap_slow;
        } else {
            i = R.plurals.worldcolock_timezone_gap_fast;
        }
        long j = offset;
        if (j % AlarmHelper.ARRIVING_ALARM_DURATION == 0) {
            Resources resources = this.mContext.getResources();
            int i2 = (int) (j / AlarmHelper.ARRIVING_ALARM_DURATION);
            quantityString = resources.getQuantityString(i, i2, Integer.valueOf(i2));
        } else {
            float f = offset / 3600000.0f;
            quantityString = this.mContext.getResources().getQuantityString(i, ((int) f) + 1, Float.valueOf(f));
        }
        if (offset == 0) {
            quantityString = this.mContext.getString(R.string.worldcolock_timezone_local);
        }
        timeZoneShowVH.timezoneGap.setText(quantityString);
    }

    public void onItemMove(int i, int i2) {
        Collections.swap(this.mDataList, i, i2);
        notifyItemMoved(i, i2);
    }

    private class TimeZoneShowVH extends RecyclerView.ViewHolder {
        private FormatClockForLocalTime digitClock;
        private TextView timeAmPm;
        private TextView timezoneDateDisplay;
        private TextView timezoneGap;
        private TextView timezoneName;
        private ImageView tzMoveIv;

        public TimeZoneShowVH(View view) {
            super(view);
            this.timezoneName = (TextView) view.findViewById(R.id.timezone_name);
            this.timezoneDateDisplay = (TextView) view.findViewById(R.id.date_display);
            this.timeAmPm = (TextView) view.findViewById(R.id.am_pm);
            this.timezoneGap = (TextView) view.findViewById(R.id.timezone_gap);
            this.digitClock = (FormatClockForLocalTime) view.findViewById(R.id.timezone_time);
            this.tzMoveIv = (ImageView) view.findViewById(R.id.iv_move);
            if (MiuiSdk.isSupportMiUiFont()) {
                MiuiFont.setFont(this.timezoneName, MiuiFont.MI_PRO_MEDIUM);
                MiuiFont.setFont(this.timezoneGap, MiuiFont.MI_PRO_NORMAL);
                MiuiFont.setFont((TextView) this.digitClock.findViewById(R.id.time_display), MiuiFont.MI_TYPE_MONO_MEDIUM);
                MiuiFont.setFont(this.timezoneDateDisplay, MiuiFont.MI_PRO_NORMAL);
                MiuiFont.setFont(this.timeAmPm, MiuiFont.MI_PRO_NORMAL);
            }
        }
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.mListener = onStateChangeListener;
    }
}
